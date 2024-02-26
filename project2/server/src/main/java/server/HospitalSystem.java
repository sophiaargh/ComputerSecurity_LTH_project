package server;


import server.users.*;
import server.util.Data;
import server.util.MedicalRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class HospitalSystem {
    /**
     * Displays the possible actions according to the user's role
     * @param user
     * @param out
     */
    public void displayAction(User user, PrintWriter out){
        out.print("What do you want to do? ");
        String role = user.getRole();
        out.print("You are a " + role + " you can:");
        switch (role) {
            case "Patient" -> out.println("read: press 1, quit: 'q'");
            case "Nurse" -> out.println("read: press 1, write: press 2, quit: 'q'");
            case "Doctor" -> out.println("read: press 1, write: press 2, create: press 3, quit: 'q'");
            case "Government Agency" -> out.println("read: press 1, delete: press 4, quit: 'q'");
        }
        out.flush();
    }
    public void action(User user, BufferedReader in, PrintWriter out) throws IOException {
        int id = 0;
        String action = in.readLine();
        System.out.println("Client provided action: " + action);
        String quit = "q";
        Permissions pAction = Permissions.NONE;
        switch (action) {
            case "q" -> out.println("quitting");

            //TODO: quit
            case "1" -> pAction = Permissions.READ;
            case "2" -> pAction = Permissions.WRITE;
            case "3" -> pAction = Permissions.CREATE;
            case "4" -> pAction = Permissions.DELETE;
            default -> out.println(action + " is not a valid action");
        }
        out.flush();
        if (user.getPerms().contains(pAction)){
            out.println("Excecuting "+ pAction);
            out.flush();
            System.out.println("Excecuting "+ pAction);
            switch (pAction) {
                case READ -> {
                    //TODO: display the records associated with the patient or division or everything (if govagency)
                    out.println("ID of the record you want to read: ");
                    out.flush();
                    id = Integer.parseInt(in.readLine());
                    System.out.println("Client provided id of record to read: " + id);
                    MedicalRecord MR = null; //TODO: find medical record using its id
                    //MR.display()
                    //eventLogger.log()
                }

                case WRITE -> {
                    writeInRecord(in, out, user);
                    //eventLogger.log()
                }

                case CREATE -> {
                    createRecord(in, out, (Doctor) user);
                    //eventLogger.log()
                }
                case DELETE -> {
                    out.println("ID of the record you want to delete: ");
                    //TODO: display all the files
                    out.flush();
                    id = Integer.parseInt(in.readLine());
                    System.out.println("Client provided id of record to delete: " + id);
                    //medicalRecordChosen.delete()
                    //eventLogger.log()
                }

            }
        }else{
            out.println("You are not authorized to do that");
            return;
        }


        out.flush();

    }
    public void run(User user, BufferedReader in, PrintWriter out) throws IOException {
        out.print("Successfully logged in!  -- ");
        out.print("id: " + user.getId());
        out.print("; Name:" + user.getName());
        out.print("; Division: " + user.getDiv());
        out.println("; Role: " + user.getRole());
        out.flush();

        displayAction(user, out);
        action(user, in, out);

        String clientMsg = null;
        while ((clientMsg = in.readLine()) != null) {
            String rev = new StringBuilder(clientMsg).reverse().toString();
            System.out.println("received '" + clientMsg + "' from client");
            System.out.print("sending '" + rev + "' to client...");

            out.println(rev);
            out.flush();
            System.out.println("done\n");
        }
    }
    public void createRecord(BufferedReader in, PrintWriter out, Doctor user) throws IOException {
        out.println("ID of the record you want to create: ");
        out.flush();
        int id = Integer.parseInt(in.readLine());
        System.out.println("Client provided id of record to create: " + id);
        out.println("ID of the patient for whom to create the record: ");
        out.flush();
        int pID = Integer.parseInt(in.readLine());
        System.out.println("Client provided id of patient associated with the file: " + pID);
        Patient patient = null; //TODO: find patient using its id
        //if the doctor is not in the same division as the patient, abort (we assume that the doctor is treating that patient)
        if (patient.getDiv() != user.getDiv()){
            out.println("You are not authorized to create a record for this patient");
            return;
        }
        out.println("ID of the nurse you want to associate the record with");
        out.flush();
        int nID = Integer.parseInt(in.readLine());
        System.out.println("Client provided id of nurse associated with the file " + nID);
        Nurse nurse = null; //TODO find nurse using its id
        out.println("Data you want to input: ");
        out.flush();
        Data data = new Data(in.readLine());
        System.out.println("Client provided data to write in the file " + data);
        MedicalRecord MR = new MedicalRecord(id, patient, (Doctor) user, nurse, data);
        patient.addMedicalRec(MR);
    }

    public void writeInRecord(BufferedReader in, PrintWriter out, User user) throws IOException {
        //TODO: display records associated with the nurse or doctor
        out.println("ID of the record you want to write in: ");
        out.flush();
        int id = Integer.parseInt(in.readLine());
        System.out.println("Client provided id of record to write in: " + id);
        MedicalRecord MR = null; //TODO: find the medical record associated with the id
        out.println("Title: ");
        out.flush();
        String dataTitle = in.readLine();
        System.out.println("Client provided data title: " + dataTitle);
        out.println("Data: ");
        out.flush();
        Data data = new Data(in.readLine());
        System.out.println("Client provided data: " + data);
        MR.updateData(data, dataTitle);
    }


}
