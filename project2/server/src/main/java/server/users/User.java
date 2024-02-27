package server.users;

import java.util.HashSet;
import java.util.Set;

public abstract class User{

    private String name; 
    private int id;
    private Object type;

    protected Set<Permissions> perms;


    public User(String name, int id){
        this.perms = new HashSet<>();

        this.name = name;
        this.id = id;

    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public Set<Permissions> getPerms(){return perms;}

    public abstract String getRole();
}