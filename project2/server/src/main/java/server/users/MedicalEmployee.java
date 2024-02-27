package server.users;

public abstract class MedicalEmployee extends User {
    Division division;
    public MedicalEmployee(String name, int id, Division division){
        super(name, id);
        this.division = division;
    }
    public Division getDiv(){return division;}
}
