package server.users;

import java.util.List;
import java.util.Set;

public class Nurse extends MedicalEmployee {
    public Nurse(String name, int id, Division division){
        super(name, id, division);
        perms.add(Permissions.READ);
        perms.add(Permissions.WRITE);
    }

    public String getRole(){
        return "Nurse";
    }
    public String display(){return (name + ", id: " + id + ", division: " + division.display());}
}
