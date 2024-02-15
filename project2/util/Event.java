package util;

import java.security.Permission;

import users.User;

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
