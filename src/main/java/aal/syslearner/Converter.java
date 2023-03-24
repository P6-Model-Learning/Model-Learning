package aal.syslearner;

import aal.syslearner.Symbolic.TimedEventInterval;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.words.impl.MapAlphabet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    public static CompactDFA<IEvent> makePrefixTreeAcceptor(List<List<Trace>> boards) {
        var traceSet = boards.stream().flatMap(Collection::stream).collect(Collectors.toSet());
        var uniqueEvents = new HashSet<IEvent>();
        for (Trace trace : traceSet) {
            for (IEvent event : trace) {
                uniqueEvents.add(event);
            }
        }

        var dfa = new CompactDFA<>(new MapAlphabet<>(uniqueEvents));
        var initial = dfa.addInitialState();

        for (Trace trace : boards.get(0)) {
            var source = initial;
            for (IEvent event : trace) {
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

    private  static List<Trace> makeTimedEventsSymbolic(List<Trace> board){
        HashMap<String, TimedEventInterval> timedEventIntervals = makeTimedEventIntervals(board);
        // Do some funky shit here >_<
        return null;
    }


    // Used to determine the time intervals for each event
    private static HashMap<String, TimedEventInterval> makeTimedEventIntervals(List<Trace> board){
        HashMap<String, TimedEventInterval> timedEventIntervals = new HashMap<>();
        for (Trace trace : board) {
            for (IEvent iEvent : trace) {
                Event event = (Event)iEvent;
                var timedEventInterval = timedEventIntervals.get(event.getMessage());
                if (timedEventInterval == null){
                    TimedEventInterval newTimedInterval = new TimedEventInterval(event.getTimeStamp(), event.getTimeStamp(), event.getMessage());
                    timedEventIntervals.put(event.getMessage(), newTimedInterval);
                } else {
                    if (event.getTimeStamp() == timedEventInterval.getMinTimestamp()){
                        timedEventInterval.setMinTimeStamp(event.getTimeStamp());
                        timedEventIntervals.put(event.getMessage(), timedEventInterval);
                    } else if (event.getTimeStamp() == timedEventInterval.getMaxTimestamp()) {
                        timedEventInterval.setMaxTimestamp(event.getTimeStamp());
                        timedEventIntervals.put(event.getMessage(), timedEventInterval);
                    }
                }
            }
        }
        return timedEventIntervals;
    }
}
