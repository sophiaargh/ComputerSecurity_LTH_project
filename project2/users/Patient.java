package users;
import util.MedicalRecord;

import java.util.ArrayList;
import java.util.List;

public class Patient extends User{

    private List<Permissions.perm> perms; 

    private List<MedicalRecord> medicalRecords;
    public Patient(String name, int id, Division division){
        super(name, id, division);
        this.medicalRecords = new ArrayList<>();
        this.perms.add((Permissions.perm.READ));
    }

    public void addMedicalRec(MedicalRecord medRec){
        medicalRecords.add(medRec);
    }


    
}
