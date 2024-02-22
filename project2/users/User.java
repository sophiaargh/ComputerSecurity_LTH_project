package users;

import java.util.HashSet;
import java.util.Set;

public class User{

    private String name; 
    private int id;
    private Division division;
    private Object type;

    protected Set<Permissions> perms;


    public User(String name, int id, Division divison){
        this.perms = new HashSet<>();

        this.name = name;
        this.id = id;
        this.division = divison;

    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public Division getDiv(){
        return division;
    }

}