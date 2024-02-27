package server.users;

import server.util.MedicalRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Patient extends User{

    public Patient(String name, int id){
        super(name, id);
        perms.add((Permissions.READ));
    }

    public String getRole(){
        return "Patient";
    }
    public String display(){ return (name + ", id: " + id);}
    
}
