package server;

import server.users.*;
import server.util.Data;
import server.util.MedicalRecord;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private Map<Integer, Patient> patients;
    private Map<Integer, Doctor> doctors;
    private Map<Integer, Nurse> nurses;
    private Map<Integer, MedicalRecord> medicalRecords;

    public Database(){
        patients = new HashMap<>();
        doctors = new HashMap<>();
        nurses = new HashMap<>();
        medicalRecords = new HashMap<>();
        init();
    }
    private void init(){
        Division oncology = new Division("Oncology");
        Division orthopedic = new Division("Orthopedic");
        Patient gunilla = new Patient("Gunilla Persson", 3);
        Patient alice = new Patient("Alice Svensson", 5);
        Patient markus = new Patient("Markus Smith", 7);
        Doctor lars = new Doctor("Lars Eriksson", 0, oncology);
        Doctor erika = new Doctor("Erika Persson", 1, orthopedic);
        Nurse jonas = new Nurse("Jonas Marcusson",2,oncology);
        Nurse bob = new Nurse("Bob Rikardson", 6,orthopedic);
        Nurse max = new Nurse("Max Pers", 8, oncology);
        patients.put(3, gunilla);
        patients.put(5, alice);
        patients.put(7, markus);
        doctors.put(0, lars);
        doctors.put(1, erika);
        nurses.put(2, jonas);
        nurses.put(6, bob);
        nurses.put(8, max);

        medicalRecords.put(0,new MedicalRecord(0, gunilla,lars,jonas,oncology, new Data("She is getting better")));
        medicalRecords.put(1, new MedicalRecord(1, alice, erika, bob, orthopedic, new Data("She has flat feet")));
        medicalRecords.put(2, new MedicalRecord(2, markus, lars, max, oncology, new Data("He is not sick anymore")));

        lars.addPatient(gunilla);
        lars.addPatient(markus);
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
        medicalRecords.put(MR.getID(), MR);
    }

    public int getRecordListSize() {
        return medicalRecords.size();
    }

    public void removeRecord(int id){
        medicalRecords.remove(id);
    }
    public void updateRecord(MedicalRecord MR){
        medicalRecords.replace(MR.getID(), MR);
    }
    public User transform(int id){
        User user = null;
        if(doctors.containsKey(id)) return doctors.get(id);
        if(nurses.containsKey(id)) return nurses.get(id);
        if(patients.containsKey(id)) return patients.get(id);
        return user;
    }
}
