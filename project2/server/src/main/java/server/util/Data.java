package server.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Data {
    private String data;
    private Date date;
    

    public Data (String data){
        this.data = data;
        date = new Date();
    }

    public String getData(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date) + ": " + data;
    }
    
}
