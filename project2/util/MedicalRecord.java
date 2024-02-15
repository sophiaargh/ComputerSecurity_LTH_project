package util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import users.Patient;
import users.User;

public class MedicalRecord {
    private Patient patient;
    private Set<User> authorizedUsers;
    private List<Data> data;
    private List<Event> log;


    public MedicalRecord(Patient patient, Set<User> initalAuthUsers, Data data){
        this.patient = patient;
        authorizedUsers = new HashSet<>();
        for(User user: initalAuthUsers){
            authorizedUsers.add(user);
        }
        this.data = new ArrayList<>();
        this.data.add(data);
        log = new ArrayList<>();
        
    }

    public boolean addUser(User user){
        authorizedUsers.add(user);
        return true;
    }

    public void removeUser(User user){
        authorizedUsers.remove(user);
    }

    public void updateData(Data data){
        this.data.add(data);
    }

    public void newEvent(Event event){
        log.add(event);
    }
}
