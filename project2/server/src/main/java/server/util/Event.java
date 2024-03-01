package server.util;

import server.users.User;

import java.security.Permission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Event {
    private User user;
    private String action;
    private User patient;
    private boolean allowed;

    public Event (User user, String action, boolean allowed){//}, User patient){
        this.user = user;
        this.action = action;
        this.patient = null;
        this.allowed = allowed;
    }

    public Event (User user, String action, boolean allowed, User patient){
        this.user = user;
        this.action = action;
        this.patient = patient;
        this.allowed = allowed;
    }

    public String toString(){
        String accessed = allowed ? "ALLOWED" : "DENIED";
        if (patient != null){
            return ("'" + user.getName() +  "' performed '" + action + "' to record of '" + patient.getName() + "'. ACCESS: " + accessed);
        } else {
            return ("'" + user.getName() +  "' performed '" + action + "'. ACCESS: " + accessed);
        }
    }

}
