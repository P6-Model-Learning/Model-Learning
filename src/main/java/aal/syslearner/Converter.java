package aal.syslearner;

import aal.syslearner.Symbolic.SymbolicTimedEvent;
import aal.syslearner.Symbolic.TimedEventInterval;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.words.impl.MapAlphabet;

import java.util.*;
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

    //Replace events in board with symbolic timed events
    public static List<Trace> makeBoardSymbolic(List<Trace> board){
        HashMap<String, TimedEventInterval> timedEventIntervals = makeTimedEventIntervals(board);
        for (Trace trace : board) {
            List<IEvent> symbolicTrace = new ArrayList<>();
            for (IEvent event : trace) {
                TimedEventInterval timedEventInterval = timedEventIntervals.get(event.getMessage());
                symbolicTrace.add(new SymbolicTimedEvent(event.getMessage(), timedEventInterval.getSymbolicTime()));
            }
            trace.setEvents(symbolicTrace);
        }

        return board;
    }


    // Used to determine the time intervals for each event
    private static HashMap<String, TimedEventInterval> makeTimedEventIntervals(List<Trace> board){
        HashMap<String, TimedEventInterval> timedEventIntervals = new HashMap<>();
        for (Trace trace : board) {
            for (IEvent iEvent : trace) {
                Event event = (Event)iEvent;
                var timedEventInterval = timedEventIntervals.get(event.getMessage());
                if (timedEventInterval == null){
                    TimedEventInterval newTimedInterval = new TimedEventInterval(event.getTimestamp(), event.getTimestamp(), event.getMessage());
                    timedEventIntervals.put(event.getMessage(), newTimedInterval);
                } else {
                    if (event.getTimestamp() < timedEventInterval.getMinTimestamp()){
                        timedEventInterval.setMinTimestamp(event.getTimestamp());
                        timedEventIntervals.put(event.getMessage(), timedEventInterval);
                    } else if (event.getTimestamp() > timedEventInterval.getMaxTimestamp()) {
                        timedEventInterval.setMaxTimestamp(event.getTimestamp());
                        timedEventIntervals.put(event.getMessage(), timedEventInterval);
                    }
                }
            }
        }
        return timedEventIntervals;
    }
}
