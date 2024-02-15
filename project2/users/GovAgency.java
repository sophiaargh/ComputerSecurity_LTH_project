package users;

import util.MedicalRecord;

public class GovAgency extends User {

    public GovAgency (String name, int id, Division division){
        super(name, id, division);

        perms.add(Permissions.READ);
        perms.add(Permissions.DELETE);
    }

    public void deleteRecord(MedicalRecord medRec){

        medRec = null;
    }

}