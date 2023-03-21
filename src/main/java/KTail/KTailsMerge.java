package KTail;

import aal.syslearner.Event;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.automata.fsa.impl.compact.CompactNFA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
        var maybeFutures = new HashMap<Event, Set<List<Event>>>();
        maybeFutures.put(null, Set.of(List.of()));

        for (var location : model.getStates()) {
            var locationFutures = computation.getKFuturesOf(k, location);
            for (var maybeFuture : maybeFutures.entrySet()) {
//                if(locationFutures.stream().anyMatch(futurelist -> futurelist.stream().anyMatch(future -> future.equals(maybeFuture.getValue())))) {
//
//                }
            }
        }
        return mergesInto;
    }
}
