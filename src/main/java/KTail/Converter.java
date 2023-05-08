package KTail;

import aal.syslearner.Symbolic.SymbolicTimedEvent;
import aal.syslearner.Symbolic.TimedEventInterval;
import aal.syslearner.IEvent;
import aal.syslearner.Event;
import aal.syslearner.Trace;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.automata.fsa.impl.compact.CompactNFA;
import net.automatalib.words.impl.GrowingMapAlphabet;
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
            int finalEvent = trace.getEvents().size() - 1;
            for (int i = 0; i < finalEvent; i++) {
                IEvent event = trace.getEvents().get(i);
                var maybeTarget = dfa.getSuccessor(source, event);
                if (maybeTarget == null) {
                    var target = dfa.addState();
                    dfa.addTransition(source, event, target);
                    source = target;
                } else {
                    source = maybeTarget;
                }
            }

            var maybeTarget = dfa.getSuccessor(source, trace.getEvents().get(finalEvent));
            if (maybeTarget == null) {
                var target = dfa.addState(true);
                dfa.addTransition(source, trace.getEvents().get(finalEvent), target);
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
                symbolicTrace.add(new SymbolicTimedEvent(event.getMessage(), timedEventInterval.getMinTimestamp(), timedEventInterval.getMaxTimestamp()));
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

    public static CompactNFA<IEvent> makeMinValuesStricter(CompactNFA<IEvent> model){
        HashMap<Integer, Double> minValParents = calculateMinValParents(model);
        CompactNFA<IEvent> updatedModel = new CompactNFA<>(new GrowingMapAlphabet<>());

        // Create states for new model;
        updatedModel.addInitialState();
        for (int i = 1; i < model.getStates().size(); i++) {
            updatedModel.addState();
        }

        // Create alphabet and transitions with stricter min values
        for (Integer state : model.getStates()) {
            for (IEvent event : model.getLocalInputs(state)) {
                SymbolicTimedEvent newEvent;
                double minValEvent = ((SymbolicTimedEvent) event).getMin();
                double minValParent = minValParents.get(state);
                if(minValEvent < minValParent){
                    double maxValEvent = ((SymbolicTimedEvent) event).getMax();
                    newEvent = new SymbolicTimedEvent(event.getMessage(), minValParent, maxValEvent);
                } else{
                    newEvent = (SymbolicTimedEvent) event;
                }
                for (Integer transition : model.getTransitions(state, event)) {
                    Integer target = model.getSuccessor(transition);
                    updatedModel.addAlphabetSymbol(newEvent);
                    updatedModel.addTransition(state, newEvent, target);
                }
            }
        }

        return updatedModel;
    }

    private static HashMap<Integer, Double> calculateMinValParents(CompactNFA<IEvent> model){
        HashMap<Integer, Double> minValParents = new HashMap<>();
        Integer successor;
        double parentMin;
        minValParents.put(0, 0.0);

        for (Integer state : model.getStates()){
            for(IEvent input : model.getLocalInputs(state)){
                for(Integer transition : model.getTransitions(state, input)){
                    successor = model.getSuccessor(transition);
                    parentMin = ((SymbolicTimedEvent) input).getMin();
                    if(minValParents.get(successor) == null){
                        minValParents.put(successor, parentMin);
                    } else {
                        minValParents.put(successor, Math.min(minValParents.get(successor), parentMin));
                    }
                }
            }
        }
        return minValParents;
    }
}
