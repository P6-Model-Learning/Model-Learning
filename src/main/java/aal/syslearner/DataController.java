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
    private boolean  allBoards = false;
    public List<List<Trace>> getTraces() throws IOException, ParseException, InterruptedException {
        return getTraces(null);
    }
    public List<List<Trace>> getTraces(String[] args) throws IOException, ParseException, InterruptedException {
        ParseArgs(args);
        JSONParser parser = new JSONParser();
        JSONArray a;
        Process p = Runtime.getRuntime().exec("python __main__.py");
        p.waitFor();

        a = (JSONArray) parser.parse(new FileReader("out.json"));

        return ExtractTracesForAllBoards(a);
    }

    private void ParseArgs(String[] args) {
        if(args != null){
            for (String arg : args) {
                if (arg.equals("--ab")) {
                    allBoards = true;
                }
            }
        }
    }

    private List<List<Trace>> ExtractTracesForAllBoards(JSONArray a) {
        ArrayList<List<Trace>> allBoardTraces = new ArrayList<>();
        int currentBoard = 0;
        int i = 0;
        for (Object b : a) {
            JSONArray board = (JSONArray) b;
            allBoardTraces.add(new ArrayList<>());
            for (Object t : board) {
                if (i > 4) break;
                JSONArray trace = (JSONArray) t;
                List<Event> events = new ArrayList<>();
                for (Object e : trace) {
                    JSONObject event = (JSONObject) e;
                    String eventMessage = (String) event.get("MESSAGE");
                    events.add(new Event(eventMessage));
                }
                allBoardTraces.get(currentBoard).add(new Trace(events));
                i++;
            }
            currentBoard++;
        }
        return allBoardTraces;
    }
}
