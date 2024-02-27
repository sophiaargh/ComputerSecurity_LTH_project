package server;

import server.users.Division;
import server.users.Doctor;
import server.users.Nurse;
import server.users.Patient;
import server.util.Data;
import server.util.MedicalRecord;

import java.util.Map;

public class Database {
    private Map<Integer, Patient> patients;
    private Map<Integer, Doctor> doctors;
    private Map<Integer, Nurse> nurses;
    private Map<Integer, MedicalRecord> medicalRecords;

    public Database(){
        init();
    }
    private void init(){
        Division oncology = new Division("Oncology");
        Division orthopedic = new Division("Orthopedic");
        Patient gunilla = new Patient("Gunilla Persson", 3);
        Patient alice = new Patient("Alice Svensson", 5);
        Doctor lars = new Doctor("Lars Eriksson", 0, oncology);
        Doctor erika = new Doctor("Erika Persson", 1, orthopedic);
        Nurse jonas = new Nurse("Jonas Marcusson",2,oncology);
        Nurse bob = new Nurse("Bob Rikardson", 6,orthopedic);
        patients.put(3, gunilla);
        patients.put(5, alice);
        doctors.put(0, lars);
        doctors.put(1, erika);
        nurses.put(2, jonas);
        nurses.put(6, bob);

        medicalRecords.put(0,new MedicalRecord(0, gunilla,lars,jonas,oncology, new Data("He is not sick anymore")));
        medicalRecords.put(1, new MedicalRecord(1, alice, erika, bob, orthopedic, new Data("She has flat feet")));

        lars.addPatient(gunilla);
        erika.addPatient(alice);
    }
    public Map<Integer,MedicalRecord> getMedicalRecords(){
        return medicalRecords;
    }
    public Map<Integer,Patient> getPatients(){
        return patients;
    }
    public Map<Integer,Nurse> getNurses(){
        return nurses;
    }
    public Map<Integer,Doctor> getDoctors(){
        return doctors;
    }
    public void addRecord(MedicalRecord MR){
        medicalRecords.put(MR.getID(),MR);
    }
}
