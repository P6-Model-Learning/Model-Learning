package KTail;

import aal.syslearner.Event;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.automata.fsa.impl.compact.CompactNFA;

import java.util.HashMap;
import java.util.Map;


public class KTailsMerge {

    public final KTailsComputation computation;
    public CompactDFA<Event> model;
    public KTailsMerge(CompactDFA<Event> model) {
        this.model = model;
        this.computation = new KTailsComputation(model, model.getInputAlphabet());
    }

    public CompactNFA<Event> mergeLocations(int k) {
        return null;
    }

    private Map<Event, Event> computeMerges(int k) {
        var mergesInto = new HashMap<Event, Event>();

        for (var location : model.getStates()) {
            var locationTails = computation.getKFuturesOf(k, location);
        }
        return null;
    }
}
