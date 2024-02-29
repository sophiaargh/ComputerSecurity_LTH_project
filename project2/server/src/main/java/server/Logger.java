package server;

import server.util.Event;
import server.util.MedicalRecord;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static PrintStream output;

    public Logger() {
        try {
            FileOutputStream fileOutput = new FileOutputStream("server/src/main/resources/logs.txt", true);
            output = new PrintStream(fileOutput);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getTimeDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void updateLog(Event event) {
        output.println(getTimeDate() + " : " + event.toString());
        output.flush();
    }
}
