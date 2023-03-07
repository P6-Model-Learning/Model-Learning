package aal.syslearner;

import org.json.simple.parser.ParseException;

import java.io.IOException;

public class AalSysLearner
{
    public static void main(String args[]) throws IOException {
        DataController dc = new DataController();
        try {
            dc.getTraces();
        }
        catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
