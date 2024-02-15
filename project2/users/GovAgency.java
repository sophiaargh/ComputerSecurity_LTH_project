package users;

import java.util.List;

public class GovAgency extends User {

    private List<Permissions.perm> perms;

    public GovAgency (){
        perms.add(Permissions.perm.READ);
        perms.add(Permissions.perm.WRITE);
    }

}