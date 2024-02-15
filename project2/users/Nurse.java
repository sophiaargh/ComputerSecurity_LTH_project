package users;

import java.util.List;

public class Nurse extends User {
    private List<Permissions.perm> perms;

    public Nurse(String name, int id, Division division){
        super(name, id, division);
        perms.add(Permissions.perm.READ);
        perms.add(Permissions.perm.WRITE);
    }

}
