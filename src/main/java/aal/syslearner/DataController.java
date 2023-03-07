package aal.syslearner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataController {

    public List<Trace> getTraces() throws IOException {

        JSONParser parser = new JSONParser();

        try {
            Process p = Runtime.getRuntime().exec("python __main__.py");


            JSONArray a = (JSONArray) parser.parse(new FileReader("out.json"));
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }

        return new ArrayList<>();
    }
}
