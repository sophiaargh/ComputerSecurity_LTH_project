package server.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Doctor extends MedicalEmployee {
    List<Patient> patients;
    public Doctor (String name, int id, Division division){
        super(name, id, division);
        this.division = division;
        perms.add(Permissions.READ);
        perms.add(Permissions.WRITE);
        perms.add(Permissions.CREATE);
        patients = new ArrayList<>();
    }
    public String getRole(){
        return "Doctor";
    }
    public void addPatient(Patient p){
        patients.add(p);
    }
    public List<Patient> getPatients(){
        return patients;
    }
}
