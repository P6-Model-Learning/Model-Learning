package aal.syslearner;

import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public class AalSysLearner
{
    public static void main(String args[]) throws IOException {
        DataController dc = new DataController();
        Event startEvent = new Event("start");
        PTA tree = new PTA(startEvent);
        final Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        List<Trace> traces = null;
        try {
            traces = dc.getTraces();
            tree = tree.BuildPTA(traces);
            System.out.println(tree.toString());
        }
        catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Visualization.visualize(tree);
    }
}
