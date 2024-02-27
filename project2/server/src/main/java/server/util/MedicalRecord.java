package server.util;
import server.users.*;

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
    private Division division;


    public MedicalRecord(int id, Patient patient, Doctor doctor, Nurse nurse, Division division, Data data){
        this.id = id;
        this.patient = patient;
        this.doc = doctor;
        this.nurse = nurse;
        this.division = division;
        this.data = new HashMap<>();
        this.data.put("First Entry:", data);
        log = new ArrayList<>();
    }



    public MedicalRecord updateData(Data data, String dataTitle){
        this.data.put(dataTitle, data);
        return this;
    }

    public void newEvent(Event event){
        log.add(event);
    }

    public Patient getPatient(){
        return patient;
    }

    public Nurse getNurse(){return nurse;}
    public Doctor getDoc(){return doc;}
    public int getID(){return id;}
}
