package server.util;

import server.users.User;

import java.security.Permission;


public class Event {
    private Permission action;
    private User user;

    public Event (Permission action, User user){
        this.action = action;
        this.user = user;
    }

    public String getEvent(){
        return user.getName() +  "did action: " + action.toString() ;
    }

}
