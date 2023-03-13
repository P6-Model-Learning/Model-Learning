package aal.syslearner;

import org.json.simple.parser.ParseException;
import aal.syslearner.*;
import java.io.IOException;
import java.util.List;

public class AalSysLearner
{
    public static void main(String args[]) throws IOException {
        DataController dc = new DataController();
        Event startEvent = new Event("start");
        PTA tree = new PTA(startEvent);

        try {
            List<Trace> traces = dc.getTraces();
            tree = tree.BuildPTA(traces);
            System.out.println(tree.toString());
        }
        catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
