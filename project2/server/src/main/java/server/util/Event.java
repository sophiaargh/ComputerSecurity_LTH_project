package server.util;

import server.users.User;

import java.security.Permission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Event {
    private User user;
    private String action;
    private MedicalRecord record;
    private boolean allowed;

    public Event (User user, String action, boolean allowed){//}, User patient){
        this.user = user;
        this.action = action;
        this.record = null;
        this.allowed = allowed;
    }

    public Event (User user, String action, boolean allowed, MedicalRecord record){
        this.user = user;
        this.action = action;
        this.record = record;
        this.allowed = allowed;
    }

    public String toString(){
        String accessed = allowed ? "ALLOWED" : "DENIED";
        if (record != null){
            return ("'" + user.getName() +  "' performed '" + action + "' to record (" + record + ") of '" + record.getPatient().getName() + "'. ACCESS: " + accessed);
        } else {
            return ("'" + user.getName() +  "' performed '" + action + "'. ACCESS: " + accessed);
        }
    }

}
