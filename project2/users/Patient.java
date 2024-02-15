package users;
import util.MedicalRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Patient extends User{

    private List<MedicalRecord> medicalRecords;
    public Patient(String name, int id, Division division){
        super(name, id, division);
        this.medicalRecords = new ArrayList<>();
        perms.add((Permissions.READ));
    }

    public void addMedicalRec(MedicalRecord medRec){
        medicalRecords.add(medRec);
    }


    
}
