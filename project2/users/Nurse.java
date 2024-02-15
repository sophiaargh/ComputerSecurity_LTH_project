package users;

import java.util.List;
import java.util.Set;

public class Nurse extends User {

    public Nurse(String name, int id, Division division){
        super(name, id, division);
        perms.add(Permissions.perm.READ);
        perms.add(Permissions.perm.WRITE);
    }

}
