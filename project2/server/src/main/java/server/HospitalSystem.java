package server;


import server.users.*;
import server.util.Data;
import server.util.Event;
import server.util.MedicalRecord;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HospitalSystem {
    Database database;
    private Logger eventLogger;
    public HospitalSystem(Database database, Logger eventLogger){
        this.database = database;
        this.eventLogger = eventLogger;
    }
    /**
     * Displays the possible actions according to the user's role
     * @param user
     * @param comms
     */
    public void displayAction(User user, CommunicationsBroadcaster comms){
        comms.sendLine("-------------------------------");
        comms.sendLine("What do you want to do? ");
        String role = user.getRole();
        comms.sendLine("You are a " + role + " you can:");
        switch (role) {
            case "Patient" -> comms.sendLine("read: press 1, quit: 'q'");
            case "Nurse" -> comms.sendLine("read: press 1, write: press 2, quit: 'q'");
            case "Doctor" -> comms.sendLine("read: press 1, write: press 2, create: press 3, quit: 'q'");
            case "Government Agency" -> comms.sendLine("read: press 1, delete: press 4, quit: 'q'");
        }

    }
    public int action(User user, CommunicationsBroadcaster comms) throws IOException {
        int id = 0;
        String action = comms.awaitClient();
        System.out.println("Client provided action: " + action);
        Permissions pAction;

        switch (action) {
            case "q" -> {
                comms.sendLine("quitting");
                System.out.println("Client is quitting");
                return -1;
            }
            case "1" -> pAction = Permissions.READ;
            case "2" -> pAction = Permissions.WRITE;
            case "3" -> pAction = Permissions.CREATE;
            case "4" -> pAction = Permissions.DELETE;
            default -> pAction = Permissions.NONE;
        }

        if (user.getPerms().contains(pAction)){
            System.out.println("Excecuting "+ pAction);
            List<MedicalRecord> allMedRecs = new ArrayList<>(database.getMedicalRecords().values());
            List<MedicalRecord> availabMedicalRecords = availabMR(user, allMedRecs);
            switch (pAction) {
                case READ -> {
                    comms.sendLine("Available Medical records: ");
                    for(MedicalRecord medrec: availabMedicalRecords){
                        comms.sendLine("ID: " + medrec.getID() + ": Patient: " + medrec.getPatient().getName() );
                    }
                    comms.sendLine("ID of the record you want to read");
                    id = Integer.parseInt(comms.awaitClient());
                    System.out.println("Client provided id of record to read: " + id);
                    MedicalRecord MR = database.getMedicalRecords().get(id);
                    if (MR == null){
                        comms.sendLine("Not a valid ID number");
                        return 0;
                    }
                    if (canReadIt(user, MR)){
                        MR.display(comms);
                        eventLogger.updateLog(new Event(user, "READ", true, MR.getPatient()));
                    }else{
                        comms.sendLine("You are not authorized to read this record");
                        eventLogger.updateLog(new Event(user, "READ", false, MR.getPatient()));
                    }
                    return 0;

                }

                case WRITE -> {
                    writeInRecord(comms, user, availabMedicalRecords);
                    eventLogger.updateLog(new Event(user, "WRITE", true, database.getMedicalRecords().get(id).getPatient()));
                    return 0;
                }

                case CREATE -> {
                    createRecord(comms, (Doctor) user);
                    eventLogger.updateLog(new Event(user, "CREATE", true, database.getMedicalRecords().get(id).getPatient()));
                    return 0;
                }
                case DELETE -> {
                    comms.sendLine("ID of the record you want to delete: ");
                    for(MedicalRecord MR: allMedRecs){
                        comms.sendLine("ID: " + MR.getID() + ": Patient: " + MR.getPatient().getName() );
                    }

                    id = Integer.parseInt(comms.awaitClient());
                    System.out.println("Client provided id of record to delete: " + id);
                    eventLogger.updateLog(new Event(user, "DELETE", true, database.getMedicalRecords().get(id).getPatient()));
                    database.removeRecord(id);
                    comms.sendLine("Record successfully deleted");

                    return 0;
                }
                case NONE -> {
                    comms.sendLine("Invalid action");
                    return 0;
                }
            }
        }else{
            eventLogger.updateLog(new Event(user, pAction.name(), false));
            comms.sendLine("You are not authorized to do that");
        }
        return 0;
    }
    public void run(User user, CommunicationsBroadcaster comms) throws IOException {
        if(!user.getRole().equals("Government Agency")) user = database.transform(user.getId());

        comms.sendLine("Successfully logged in!");
        comms.sendLine("id: " + user.getId());
        comms.sendLine("Name: " + user.getName());
        comms.sendLine("Role: " + user.getRole());
        if (user instanceof MedicalEmployee){
            comms.sendLine("Division: " + ((MedicalEmployee) user).getDiv().toString());
        }

        do {
            displayAction(user, comms);
        } while (action(user, comms) != -1);
    }

    private List<MedicalRecord> availabMR(User user, List<MedicalRecord> allMedRecs){
        List<MedicalRecord> availabMedicalRecords = null;
        switch (user.getRole()){
            case "Patient": {
                availabMedicalRecords = allMedRecs.stream()
                        .filter(x -> x.getPatient().getId() == user.getId()).toList();
                break;
            }
            case "Doctor" :
            case "Nurse": {
                MedicalEmployee medEmp = (MedicalEmployee) user;
                availabMedicalRecords = allMedRecs.stream()
                        .filter(x -> x.getDivision().equals(medEmp.getDiv())).toList();
                break;
            }
            case "Government Agency":{
                availabMedicalRecords = allMedRecs;
            }

        }

        return availabMedicalRecords;
    }
    private boolean canReadIt(User user, MedicalRecord MR){
        switch (user.getRole()){
            case "Government Agency":
                return true;
            case "Doctor":
            case "Nurse":
                MedicalEmployee ME = (MedicalEmployee) user;
                return MR.getDivision().equals(ME.getDiv());
            case "Patient":
                return MR.getPatient().equals((Patient) user);
        }
        return false;
    }
    private void createRecord(CommunicationsBroadcaster comms, Doctor user) throws IOException {
        boolean valid = false;
        int id = database.getRecordListSize();

        while(!valid){
            comms.sendLine("ID of the record you want to create: ");
            id = Integer.parseInt(comms.awaitClient());
            valid = (database.getMedicalRecords().get(id) == null);
            if (!valid) comms.sendLine("ID already taken");
        }


        System.out.println("Client provided id of record to create: " + id);

        List<Patient> dPatients = user.getPatients();
        comms.sendLine("Patient(s) you are treating: ");
        for (Patient patient : dPatients) comms.sendLine(patient.display());
        comms.sendLine("ID of the patient for whom to create the record: ");
        int pID = Integer.parseInt(comms.awaitClient());
        System.out.println("Client provided id of patient associated with the file: " + pID);

        Patient patient = database.getPatients().get(pID);
        if (patient == null){
            comms.sendLine("Not a valid ID number");
            return;
        }

        if (!dPatients.contains(patient)){
            comms.sendLine("You are not authorized to create a record for this patient");
            eventLogger.updateLog(new Event(user, "CREATE", false, patient));
            return;
        }

        comms.sendLine("Nurse(s) in your division: ");
        Map<Integer, Nurse> nurses = database.getNurses();

        for (Nurse nurse: nurses.values())
            if (nurse.getDiv().equals(user.getDiv()))
                comms.sendLine(nurse.display());


        comms.sendLine("ID of the nurse you want to associate the record with");
        int nID = Integer.parseInt(comms.awaitClient());
        System.out.println("Client provided id of nurse associated with the file " + nID);
        Nurse nurse = database.getNurses().get(nID);
        if (nurse == null){
            comms.sendLine("Not a valid ID number");
            return;
        }
        comms.sendLine("Data you want to input: ");

        Data data = new Data(comms.awaitClient());
        System.out.println("Client provided data to write in the file " + data);
        MedicalRecord MR = new MedicalRecord(id, patient, user, nurse, user.getDiv(), data);
        database.addRecord(MR);
        comms.sendLine("Medical Record created successfully: ");
        MR.display(comms);
    }

    private void writeInRecord(CommunicationsBroadcaster comms, User user, List<MedicalRecord> allMedRecs) throws IOException {
        List<MedicalRecord> availabMedicalRecords = availabMR(user, allMedRecs);
        comms.sendLine("Available Medical records: ");
        for(MedicalRecord medrec: availabMedicalRecords){
            comms.sendLine("ID: " + medrec.getID() + ": Patient: " + medrec.getPatient().getName() );
        }
        comms.sendLine("ID of the record you want to write in: ");
        int id = Integer.parseInt(comms.awaitClient());
        System.out.println("Client provided id of record to write in: " + id);
        MedicalRecord MR = database.getMedicalRecords().get(id);
        if (MR == null){
            comms.sendLine("Not a valid ID number");
            return;
        }
        if (MR.getDoc().getId() != user.getId() && user.getRole().equals("Doctor")
                    || user.getRole().equals("Nurse") && MR.getNurse().getId() != user.getId()){
            comms.sendLine("You are not authorized to write in this record");
            eventLogger.updateLog(new Event(user, "WRITE", false));
            return;
        }

        comms.sendLine("Data: ");

        Data data = new Data(comms.awaitClient());
        System.out.println("Client provided data: " + data);
        MR = MR.updateData(data);
        database.updateRecord(MR);
        comms.sendLine("Record updated successfully");

    }


}
