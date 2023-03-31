package aal.syslearner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
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

        Process p = Runtime.getRuntime().exec("python __main__.py -i");
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
                List<IEvent> events = new ArrayList<>();
                for (Object e : trace) {
                    JSONObject event = (JSONObject) e;
                    String eventMessage = (String) event.get("MESSAGE");
                    JSONArray eventMonotonicTimestamp = (JSONArray) event.get("__MONOTONIC_TIMESTAMP");
                    double eventTimestamp = parseMonotonicTimestamp((String) eventMonotonicTimestamp.get(0));
                    events.add(new Event(eventMessage, eventTimestamp));
                }
                allBoardTraces.get(currentBoard).add(new Trace(events));
                i++;
            }
            currentBoard++;
            break;
        }
        return allBoardTraces;
    }

    private double parseMonotonicTimestamp(String monotonicTimestamp){
        String[] splitTimestamp = monotonicTimestamp.split(":");
        String[] splitDaysAndHours = splitTimestamp[0].split("day(s)?,");
        double hoursToSeconds, daysToSeconds;
        if(splitDaysAndHours.length == 2){
            daysToSeconds = Double.parseDouble(splitDaysAndHours[0]) * 24 * 3600;
            hoursToSeconds = Double.parseDouble(splitDaysAndHours[1]) * 3600;
        } else {
            daysToSeconds = 0;
            hoursToSeconds = Double.parseDouble(splitTimestamp[0]) * 3600;
        }

        double minutesToSeconds = Double.parseDouble(splitTimestamp[1]) * 60;
        double seconds = Double.parseDouble(splitTimestamp[2]);
        return daysToSeconds + hoursToSeconds + minutesToSeconds + seconds;
    }
}
