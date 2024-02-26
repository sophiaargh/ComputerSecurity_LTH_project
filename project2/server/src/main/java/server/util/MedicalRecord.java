package server.util;
import server.users.Doctor;
import server.users.Nurse;
import server.users.Patient;
import server.users.User;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MedicalRecord {
    private Patient patient;
    private Map<String,Data> data;
    private List<Event> log;
    private int id;
    private Doctor doc;
    private Nurse nurse;


    public MedicalRecord(int id, Patient patient, Doctor doctor, Nurse nurse, Data data){
        this.id = id;
        this.patient = patient;
        this.doc = doctor;
        this.nurse = nurse;
        this.data = new HashMap<>();
        this.data.put("First Entry:", data);
        log = new ArrayList<>();
    }



    public void updateData(Data data, String dataTitle){
        this.data.put(dataTitle, data);
    }

    public void newEvent(Event event){
        log.add(event);
    }

    public Patient getPatient(){
        return patient;
    }

    public void deleteRecord(){
        System.out.println("TODO delete the record (gov agency)");
    }
}
