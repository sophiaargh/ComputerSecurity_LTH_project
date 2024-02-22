package server.users;

import java.util.List;
import java.util.Set;

public class Doctor extends User {

    public Doctor (String name, int id, Division division){
        super(name, id, division);
        perms.add(Permissions.READ);
        perms.add(Permissions.WRITE);
        perms.add(Permissions.CREATE);

    }
    
}
