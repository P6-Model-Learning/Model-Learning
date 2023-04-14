package KTail;

import aal.syslearner.Event;
import aal.syslearner.IEvent;
import aal.syslearner.Symbolic.SymbolicTimedEvent;
import com.sun.jdi.InterfaceType;
import net.automatalib.automata.ShrinkableAutomaton;
import net.automatalib.automata.fsa.NFA;
import net.automatalib.automata.fsa.impl.FastNFA;
import net.automatalib.automata.fsa.impl.compact.CompactNFA;
import net.automatalib.commons.util.Pair;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.GrowingMapAlphabet;

import java.util.*;
import java.util.stream.Collectors;


public class KTailsMerge {

    public final KTailsComputation computation;
    public final Alphabet<IEvent> alphabet;
    public NFA<Integer, IEvent> model;
    public KTailsMerge(NFA<Integer, IEvent> model, Alphabet<IEvent> alphabet) {
        this.model = model;
        this.alphabet = alphabet;
        this.computation = new KTailsComputation(model, alphabet);
    }

    public CompactNFA<IEvent> mergeLocations(int k) {
        var mergesInto = computeMerges(k);

        var targetLocations = new HashSet<>(mergesInto.values());
        var initialTargetLocations = model.getInitialStates().stream().map(mergesInto::get).collect(Collectors.toSet());

        var merged = new CompactNFA<IEvent>(new GrowingMapAlphabet<>());
        var mergedLocations = targetLocations.stream().filter(Objects::nonNull).map(
                targetLocation -> Pair.of(targetLocation,
                        (initialTargetLocations.contains(targetLocation) ? merged.addInitialState() : //Add initial state
                                merged.addState()))                                                   //Add a state
        ).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        //Collect target transitions

        var newTransitions = new HashMap<>();
        for (var location : model.getStates()) {
            var source = mergedLocations.get(mergesInto.get(location));
            if (source == null ) continue;
            //For each input in the alphabet
            for (var input : alphabet) {
                // For each transition triggered by the input on the location
                for (var transition : model.getTransitions(location, input)) {
                    var target = mergedLocations.get(mergesInto.get(model.getSuccessor(transition)));
                    if (target == null) continue;

                    //TODO Sigurd: Project inputs in a way that makes sense
                    var newInput = input;
                    merged.addAlphabetSymbol(newInput);
                    merged.addTransition(source, newInput, target);
                }
            }
        }
        return collapseTrivialSequences(merged, 0, new HashSet<>());
    }


    private Map<Integer, Integer> computeMerges(int k) {
        var mergesInto = new HashMap<Integer, Integer>();
        var maybeFutures = new HashMap<Integer, Set<List<IEvent>>>();
        maybeFutures.put(null, Set.of(List.of()));

        again:
        for (var location : model.getStates()) {
            var locationFutures = computation.getKFuturesOf(k, location);
            for (var maybeFuture : maybeFutures.entrySet()) {
                var maybeFutureValues = maybeFuture.getValue();
                if (compareLocationFutures(locationFutures, maybeFutureValues))
                {
                    mergesInto.put(location, maybeFuture.getKey());
                    continue again;
                }
            }
            maybeFutures.put(location, locationFutures);
            mergesInto.put(location, location);
        }
        return mergesInto;
    }
    private boolean compareLocationFutures(Set<List<IEvent>> locationFutures, Set<List<IEvent>> maybeFutureValues) {
        return locationFutures.stream().allMatch(List::isEmpty) && maybeFutureValues.stream().allMatch(List::isEmpty) ||
                (locationFutures.stream().anyMatch(list -> !list.isEmpty()) && locationFutures.stream().allMatch(list ->
                        maybeFutureValues.stream().anyMatch(maybeList -> maybeList.equals(list))));
    }

    private CompactNFA<IEvent> collapseTrivialSequences(CompactNFA<IEvent> model, Integer source, HashSet<Integer> visitedTransitions){
        int sequenceLength;
        ArrayList<Integer> statesInSequence = new ArrayList<>();
        CompactNFA<IEvent> collapsedModel = new CompactNFA<>(new GrowingMapAlphabet<>());
        Collection<IEvent> inputs = model.getLocalInputs(source);
        HashMap<Integer, Integer> correspondingLocations = new HashMap<>();
        HashMap<Integer, Integer> parents = calculateParents(model);
        IEvent input = null;

        while (inputs != null && inputs.size() == 1) {
            input = model.getLocalInputs(source).iterator().next();
            // Break if sequence branches nondeterministically
            if(model.getSuccessors(source, input).size() != 1){
                break;
            }

            // Break if multiple parents
            if(parents.get(source) != 1){
                break;
            }

            statesInSequence.add(source);
            visitedTransitions.add(model.getTransitions(source, input).iterator().next());
            source = model.getSuccessors(source, input).iterator().next();
            inputs = model.getLocalInputs(source);
        }

        statesInSequence.add(source);
        sequenceLength = statesInSequence.size();
        //TODO: Determine length of trivial sequence
        if (sequenceLength >= 5) {
            for (int i = 0; i < sequenceLength - 1; i++) {
                model.removeAllTransitions(statesInSequence.get(i));
                visitedTransitions.remove(statesInSequence.get(i));
            }

            if (input.getClass() == SymbolicTimedEvent.class) {
                input = new SymbolicTimedEvent("Collapsed trivial sequence ending with: " + input.getMessage(),
                        (((SymbolicTimedEvent) input).getSymbolicTime()));
            }   else {
                input = new Event("Collapsed trivial sequence ending with: " + input.getMessage());
            }

            model.addAlphabetSymbol(input);
            model.addTransition(statesInSequence.get(0), input, statesInSequence.get(sequenceLength - 1));
            System.out.println("Collapsed the following states in trivial sequence: " + statesInSequence);

            for (Integer location : model.getStates()){
                if(!statesInSequence.get(0).equals(location) && !statesInSequence.get(sequenceLength-1).equals(location) && statesInSequence.contains(location)) continue;

                if(collapsedModel.getStates().isEmpty()){
                    correspondingLocations.put(location, collapsedModel.addInitialState());
                } else {
                    correspondingLocations.put(location, collapsedModel.addState());
                }
            }

            for (Integer location : model.getStates()) {
                if(!correspondingLocations.containsKey(location)) continue;
                Integer newLocation = correspondingLocations.get(location);
                for (IEvent event : model.getLocalInputs(location)) {
                    for (Integer transition : model.getTransitions(location, event)) {
                        Integer target = correspondingLocations.get(model.getSuccessor(transition));

                        collapsedModel.addAlphabetSymbol(event);
                        collapsedModel.addTransition(newLocation, event, target);
                    }
                }
            }
        } else {
            collapsedModel = model;
        }

        //TODO: Handle collapsedModel
        if (inputs != null && inputs.size() != 0) {
            if(!correspondingLocations.isEmpty()){
                source = correspondingLocations.get(source);
            }
            for (IEvent event : inputs) {
                for (Integer transition : collapsedModel.getTransitions(source, event)){
                    if(visitedTransitions.contains(transition)){
                        continue;
                    }
                    visitedTransitions.add(transition);
                    collapsedModel = collapseTrivialSequences(collapsedModel, collapsedModel.getSuccessor(transition), visitedTransitions);
                }
            }
        }
        return collapsedModel;
    }

    private HashMap<Integer, Integer> calculateParents(CompactNFA<IEvent> model){
        HashMap<Integer, Integer> parents = new HashMap<>();
        Integer successor;
        parents.put(0,1);
        for (Integer state : model.getStates()){
            for(IEvent input : model.getLocalInputs(state)){
                for(Integer transition : model.getTransitions(state, input)){
                    successor = model.getSuccessor(transition);

                    if(parents.get(successor) == null){
                        parents.put(successor, 1);
                    } else {
                        parents.put(successor, parents.get(successor) + 1);
                    }
                }
            }
        }
        return parents;
    }
}
