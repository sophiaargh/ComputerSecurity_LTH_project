package server.util;
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
    private Set<User> authorizedUsers;
    private Map<String,Data> data;
    private List<Event> log;
    private int id;


    public MedicalRecord(int id, Patient patient, Set<User> initalAuthUsers, Data data){
        this.id = id;
        this.patient = patient;
        authorizedUsers = new HashSet<>();
        for(User user: initalAuthUsers){
            authorizedUsers.add(user);
        }
        this.data = new HashMap<>();
        this.data.put("First Entry:", data);
        log = new ArrayList<>();
    }

    public boolean addUser(User user){
        authorizedUsers.add(user);
        return true;
    }

    public void removeUser(User user){
        authorizedUsers.remove(user);
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

    public void createRecord(){
        System.out.println("TODO create the record (action for a doctor)");
    }
    public void deleteRecord(){
        System.out.println("TODO delete the record (gov agency)");
    }
}
