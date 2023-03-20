package aal.syslearner;

import KTail.Converter;
import KTail.KTailsComputation;
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
        final Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        try {
            List<List<Trace>> traces = dc.getTraces();
            //tree = tree.BuildPTA(traces);
            //System.out.println(tree.toString());
            var pta = Converter.makePrefixTreeAcceptor(traces);
            var computation = new KTailsComputation(pta, pta.getInputAlphabet());
            computation.getKFuturesOf(5, pta.getInitialState());
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

