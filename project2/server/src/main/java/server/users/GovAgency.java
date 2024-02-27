package server.users;

import server.util.MedicalRecord;

public class GovAgency extends User {

    public GovAgency (String name, int id){
        super(name, id);

        perms.add(Permissions.READ);
        perms.add(Permissions.DELETE);
    }

    public void deleteRecord(MedicalRecord medRec){

        medRec = null;
    }
    public String getRole(){
        return "Government Agency";
    }
}