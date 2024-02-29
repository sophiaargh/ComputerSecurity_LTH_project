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
    Logger eventLogger;
    public HospitalSystem(){
        database = new Database();
        eventLogger = new Logger();
    }
    /**
     * Displays the possible actions according to the user's role
     * @param user
     * @param comms
     */
    public void displayAction(User user, CommunicationsBroadcaster comms){
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
        Permissions pAction = Permissions.NONE;
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
            default -> {
                comms.sendLine(action + " is not a valid action");
                displayAction(user,comms);
                action(user,comms);
            }
        }

        if (user.getPerms().contains(pAction)){
            System.out.println("Excecuting "+ pAction);
            switch (pAction) {
                case READ -> {
                    Set<Integer> medRecIds = database.getMedicalRecords().keySet();
                    List<MedicalRecord> allMedRecs = new ArrayList<>();
                    for(Integer i: medRecIds){
                        allMedRecs.add(database.getMedicalRecords().get(i));
                    }
                    List<MedicalRecord> availabMedicalRecords = allMedRecs.stream()
                                                                          .filter(x -> x.getDoc().getId() == user.getId() || x.getNurse().getId() == user.getId() || x.getPatient().getId() == user.getId())
                                                                          .collect(Collectors.toList());
                    comms.sendLine("Available Medical records: ");
                    for(MedicalRecord medrec: availabMedicalRecords){
                        comms.sendLine("ID: " + medrec.getID() + ": owner: " + medrec.getPatient().getName() );
                    }
                    //TODO: display all the records according to permission
                    comms.sendLine("ID of the record you want to read");
                    id = Integer.parseInt(comms.awaitClient());
                    System.out.println("Client provided id of record to read: " + id);
                    MedicalRecord MR = database.getMedicalRecords().get(id);
                    if (MR == null){
                        comms.sendLine("Not a valid ID number");
                        displayAction(user,comms);
                        action(user,comms);
                        return 0;
                    }
                    MR.display(comms);
                    eventLogger.updateLog(new Event(user, "READ", MR.getPatient()));
                }

                case WRITE -> {
                    writeInRecord(comms, user);
                    eventLogger.updateLog(new Event(user, "WRITE", database.getMedicalRecords().get(id).getPatient()));
                }

                case CREATE -> {
                    createRecord(comms, (Doctor) user);
                    eventLogger.updateLog(new Event(user, "CREATE", database.getMedicalRecords().get(id).getPatient()));
                }
                case DELETE -> {
                    comms.sendLine("ID of the record you want to delete: ");
                    Set<Integer> medRecIds = database.getMedicalRecords().keySet();
                    List<MedicalRecord> allMedRecs = new ArrayList<>();
                    for(Integer i: medRecIds){
                        System.out.println("ID: " + allMedRecs.get(i).getID() + ": owner: " + allMedRecs.get(i).getPatient().getName() );
                    }

                    id = Integer.parseInt(comms.awaitClient());
                    System.out.println("Client provided id of record to delete: " + id);
                    database.removeRecord(id);
                    eventLogger.updateLog(new Event(user, "DELETE", database.getMedicalRecords().get(id).getPatient()));
                }

            }
        }else{
            comms.sendLine("You are not authorized to do that");
            displayAction(user,comms);
            action(user,comms);
            return 0;
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
            comms.sendLine("Division: " + ((MedicalEmployee) user).getDiv().display());
        }
        comms.sendLine("To continue, press Enter");

        String clientMsg = null;
        while ((clientMsg = comms.awaitClient()) != null) {
            displayAction(user, comms);
            if (action(user, comms) == -1){
                return;
            }
            String rev = new StringBuilder(clientMsg).reverse().toString();
            System.out.println("received '" + clientMsg + "' from client");
            System.out.print("sending '" + rev + "' to client...");

            comms.sendLine(rev);

            System.out.println("done\n");
            comms.sendLine("To continue, press Enter");
        }
    }
    public void createRecord(CommunicationsBroadcaster comms, Doctor user) throws IOException {
        boolean valid = false;
        int id = 0;

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
            return;
        }

        comms.sendLine("Nurse(s) in your division: ");
        Map<Integer, Nurse> nurses = database.getNurses();

        for (Nurse nurse: nurses.values())
            if (nurse.getDiv().display().equals(user.getDiv().display()))
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
        MedicalRecord MR = new MedicalRecord(id, patient, (Doctor) user, nurse, user.getDiv(), data);
        database.addRecord(MR);
        comms.sendLine("Medical Record created successfully");
        //TODO display medical record
    }

    public void writeInRecord(CommunicationsBroadcaster comms, User user) throws IOException {
        //TODO: display records associated with the nurse or doctor
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
            //TODO: log?
            writeInRecord(comms, user);
            return;
        }

        comms.sendLine("Title: ");
        String dataTitle = comms.awaitClient();
        System.out.println("Client provided data title: " + dataTitle);
        comms.sendLine("Data: ");

        Data data = new Data(comms.awaitClient());
        System.out.println("Client provided data: " + data);
        MR = MR.updateData(data);
        database.updateRecord(MR);
        comms.sendLine("Record updated successfully");

    }


}
