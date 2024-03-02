package server.users;

public class Division {
    private String division;
    public Division(String division){
        this.division = division;
    }

    @Override
    public String toString() {
        return division;
    }
    public boolean equals(Division div){
        return this.toString().equals(div.toString());
    }
}
