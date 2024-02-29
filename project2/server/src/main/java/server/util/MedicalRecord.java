package server.util;
import server.CommunicationsBroadcaster;
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
    public String getStringDivision(){return division.display();}

    public void display(CommunicationsBroadcaster comms){
        comms.sendLine("-------------------------------");
        comms.sendLine("Medical Record: " + id + ": Patient: " + patient.getName());
        comms.sendLine("Division: " + division.toString() + ": Doctor - " + doc.getName() + ": Nurse - " + nurse.getName());
        comms.sendLine("Data Log: ");
        for(Data d: data){
            comms.sendLine(d.getData());
        }
        comms.sendLine("End of Medical Record");
        comms.sendLine("-------------------------------");
    }
}
