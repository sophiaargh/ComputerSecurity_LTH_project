package server.users;

public class Division {
    private String division;
    public Division(String division){
        this.division = division;
    }
    public String display(){
        return division;
    }

    @Override
    public String toString() {
        return division;
    }
}
