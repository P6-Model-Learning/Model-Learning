package KTail;

import aal.syslearner.Event;
import aal.syslearner.Trace;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.words.impl.MapAlphabet;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    public static CompactDFA<Event> makePrefixTreeAcceptor(List<List<Trace>> boards) {
        var traceSet = boards.stream().flatMap(Collection::stream).collect(Collectors.toSet());
        var uniqueEvents = new HashSet<Event>();
        for (Trace trace : traceSet) {
            for (Event event : trace) {
                uniqueEvents.add(event);
            }
        }

        var dfa = new CompactDFA<>(new MapAlphabet<>(uniqueEvents));
        var initial = dfa.addInitialState();

        for (Trace trace : boards.get(0)) {
            var source = initial;
            for (Event event : trace) {
                var maybeTarget = dfa.getSuccessor(source, event);
                if (maybeTarget == null) {
                    var target = dfa.addState();
                    dfa.addTransition(source, event, target);
                    source = target;
                } else {
                    source = maybeTarget;
                }
            }
        }
        return dfa;
    }
}
