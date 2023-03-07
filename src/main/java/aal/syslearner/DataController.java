package aal.syslearner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataController {

    public List<Trace> getTraces() throws IOException, ParseException {
        return getTraces(null);
    }
    public List<Trace> getTraces(String[] args) throws IOException, ParseException {

        JSONParser parser = new JSONParser();
        JSONArray a;
        Process p = Runtime.getRuntime().exec("python __main__.py");

        a = (JSONArray) parser.parse(new FileReader("out.json"));

        return ExtractTraces(a);
    }

    private static List<Trace> ExtractTraces(JSONArray a) {
        ArrayList<Trace> traces = new ArrayList<>();
        for (Object b : a) {
            JSONArray board = (JSONArray) b;
            for (Object t : board) {
                JSONArray trace = (JSONArray) t;
                List<Event> events = new ArrayList<>();
                for (Object e : trace) {
                    JSONObject event = (JSONObject) e;
                    String eventMessage = (String) event.get("MESSAGE");
                    events.add(new Event(eventMessage));
                }
                traces.add(new Trace(events));
            }
        }
        return traces;
    }

}
