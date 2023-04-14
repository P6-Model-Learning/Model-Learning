package aal.syslearner;

import KTail.Converter;
import KTail.KTailsMerge;
import net.automatalib.visualization.Visualization;
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
            var mergedGraph = new KTailsMerge(pta, pta.getInputAlphabet()).mergeLocations(2);
            System.out.println("showing the goods");
            System.out.println(pta.getStates().size() + "  :  " + mergedGraph.getStates().size());
            Visualization.visualize(mergedGraph);
        }
        catch (ParseException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

