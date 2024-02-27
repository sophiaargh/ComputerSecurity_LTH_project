package server;


import server.users.*;
import server.util.Data;
import server.util.MedicalRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalSystem {
    Database database;
    public HospitalSystem(){
        database = new Database();
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
                    //MR.display()
                    //eventLogger.log()
                }

                case WRITE -> {
                    writeInRecord(comms, user);
                    //eventLogger.log()
                }

                case CREATE -> {
                    createRecord(comms, (Doctor) user);
                    //eventLogger.log()
                }
                case DELETE -> {
                    comms.sendLine("ID of the record you want to delete: ");
                    //TODO: display all the files

                    id = Integer.parseInt(comms.awaitClient());
                    System.out.println("Client provided id of record to delete: " + id);
                    //medicalRecordChosen.delete()
                    //eventLogger.log()
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

        comms.sendLine("Successfully logged in!");
        comms.sendLine("id: " + user.getId());
        comms.sendLine("Name: " + user.getName());
        comms.sendLine("Role: " + user.getRole());
        if (user instanceof MedicalEmployee){
            comms.sendLine("Division: " + ((MedicalEmployee) user).getDiv());
        }



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
        comms.sendLine("Patients you are treating: ");
        for (Patient patient : dPatients) comms.sendLine(patient.display());
        comms.sendLine("ID of the patient for whom to create the record: ");
        int pID = Integer.parseInt(comms.awaitClient());
        System.out.println("Client provided id of patient associated with the file: " + pID);

        Patient patient = database.getPatients().get(pID);
        if (patient == null){
            comms.sendLine("Not a valid ID number");
            /*displayAction(user,comms);
            action(user,comms);*/
            return;
        }

        if (!dPatients.contains(patient)){
            comms.sendLine("You are not authorized to create a record for this patient");
            return;
        }

        comms.sendLine("Nurses in your division: ");
        Map<Integer, Nurse> nurses = database.getNurses();
        for (Nurse nurse: nurses.values()) if (nurse.getDiv() == user.getDiv()) nurse.display();

        comms.sendLine("ID of the nurse you want to associate the record with");
        int nID = Integer.parseInt(comms.awaitClient());
        System.out.println("Client provided id of nurse associated with the file " + nID);
        Nurse nurse = database.getNurses().get(nID);
        if (nurse == null){
            comms.sendLine("Not a valid ID number");
            /*displayAction(user,comms);
            action(user,comms);*/
            return;
        }
        comms.sendLine("Data you want to input: ");

        Data data = new Data(comms.awaitClient());
        System.out.println("Client provided data to write in the file " + data);
        MedicalRecord MR = new MedicalRecord(id, patient, (Doctor) user, nurse, user.getDiv(), data);

    }

    public void writeInRecord(CommunicationsBroadcaster comms, User user) throws IOException {
        //TODO: display records associated with the nurse or doctor
        comms.sendLine("ID of the record you want to write in: ");
        int id = Integer.parseInt(comms.awaitClient());
        System.out.println("Client provided id of record to write in: " + id);
        MedicalRecord MR = database.getMedicalRecords().get(id);
        if (MR == null){
            comms.sendLine("Not a valid ID number");
            /*displayAction(user,comms);
            action(user,comms);*/
            return;
        }
        if (MR.getDoc().getId() != user.getId() && MR.getNurse().getId() != user.getId()){
            comms.sendLine("You are not authorized to write in this record");
            //LOG?
            writeInRecord(comms, user);
            return;
        }
        comms.sendLine("Title: ");

        String dataTitle = comms.awaitClient();
        System.out.println("Client provided data title: " + dataTitle);
        comms.sendLine("Data: ");

        Data data = new Data(comms.awaitClient());
        System.out.println("Client provided data: " + data);
        MR.updateData(data, dataTitle);
    }


}
