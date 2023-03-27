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
        try {
            List<List<Trace>> traces = dc.getTraces();
            //tree = tree.BuildPTA(traces);
            //System.out.println(tree.toString());
            List<Trace> symbolicBoard = Converter.makeBoardSymbolic(traces.get(0));
            traces.set(0, symbolicBoard);
            
            var pta = Converter.makePrefixTreeAcceptor(traces);
            System.out.println("showing the goods");
            Visualization.visualize(pta);
        }
        catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

