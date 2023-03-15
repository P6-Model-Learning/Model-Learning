package aal.syslearner;

import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.words.impl.MapAlphabet;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    public static CompactDFA<String> makePrefixTreeAcceptor(List<List<Trace>> boards) {
        var bla = boards.stream().flatMap(Collection::stream).collect(Collectors.toSet());
        var uniqueEventsmessages = new HashSet<String>();
        var uniqueEvents = new HashSet<Event>();
        for (Trace trace : bla) {
            for (Event event : trace) {
                uniqueEventsmessages.add(event.getMessage());
                uniqueEvents.add(event);
            }
        }


        var dfa = new CompactDFA<>(new MapAlphabet<>(uniqueEventsmessages));
        var initial = dfa.addInitialState();

        for (Trace trace : boards.get(0)) {
            var source = initial;
            for (Event event : trace) {
                var maybeTarget = dfa.getSuccessor(source, event.getMessage());
                if (maybeTarget == null) {
                    var target = dfa.addState();
                    dfa.addTransition(source, event.getMessage(), target);
                    source = target;
                } else {
                    source = maybeTarget;
                }
            }
        }
        return dfa;
    }
}
