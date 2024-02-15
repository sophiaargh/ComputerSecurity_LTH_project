package users;

import java.util.List;

public class GovAgency extends User {

   

    public GovAgency (String name, int id, Division division){
        super(name, id, division);

        perms.add(Permissions.perm.READ);
        perms.add(Permissions.perm.DELETE);
    }

}