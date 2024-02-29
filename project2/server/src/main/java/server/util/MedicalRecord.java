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
    private List<Data> data;
    private List<Event> log;
    private int id;
    private Doctor doc;
    private Nurse nurse;
    private Division division;


    public MedicalRecord(int id, Patient patient, Doctor doctor, Nurse nurse, Division division, Data d){
        this.id = id;
        this.patient = patient;
        this.doc = doctor;
        this.nurse = nurse;
        this.division = division;
        this.data = new ArrayList<>();
        this.data.add(d);
        log = new ArrayList<>();
    }

    public MedicalRecord updateData(Data data){
        this.data.add( data);
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

    public void display(){
        System.out.println("Medical Record: " + Integer.toString(id) + ": owner: " + patient.getName());
        System.out.println("Division: " + division.toString() + ": Doctor - " + doc.getName() + ": Nurse - " + nurse.getName());
        System.out.println("Data Log: ");
        for(Data d: data){
            System.out.println(d.toString());
        }
    }
}
