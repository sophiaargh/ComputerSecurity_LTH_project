package users;

import java.util.List;
import java.util.Set;

public class Doctor extends User {

    public Doctor (String name, int id, Division division){
        super(name, id, division);
        perms.add(Permissions.perm.READ);
        perms.add(Permissions.perm.WRITE);
        perms.add(Permissions.perm.EXECUTE);

    }
    
}
