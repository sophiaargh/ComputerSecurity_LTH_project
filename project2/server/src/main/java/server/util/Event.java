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

    public Event (User user, String action){//}, User patient){
        this.user = user;
        this.action = action;
        this.patient = null;
    }

    public Event (User user, String action, User patient){
        this.user = user;
        this.action = action;
        this.patient = patient;
    }

    public String toString(){
        if (patient != null){
            return ("'" + user.getName() +  "' performed '" + action + "' to record of '" + patient.getName() + "'");
        } else {
            return ("'" + user.getName() +  "' performed '" + action);
        }
    }

}
