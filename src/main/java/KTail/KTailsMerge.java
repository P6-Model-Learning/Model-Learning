package KTail;

import aal.syslearner.IEvent;
import aal.syslearner.Event;
import aal.syslearner.Symbolic.SymbolicTimedEvent;
import com.google.common.collect.MoreCollectors;
import net.automatalib.automata.fsa.NFA;
import net.automatalib.automata.fsa.impl.compact.CompactNFA;
import net.automatalib.commons.util.Pair;
import net.automatalib.visualization.Visualization;
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
        var targetTransitions = collectTargetTransitions(mergesInto, mergedLocations);

        for (var transitionSet : targetTransitions.entrySet()) {
            var from = transitionSet.getKey().getFirst();
            var to = transitionSet.getKey().getSecond();
            for (var transition : transitionSet.getValue().values()) {
                var mergedInput = transition.stream().collect(MoreCollectors.onlyElement());
                merged.addAlphabetSymbol(mergedInput);
                merged.addTransition(from, mergedInput, to);
            }
        }
        return collapseTrivialSequences(merged, 0, new HashSet<>());
    }

    private Map<Pair<Integer, Integer>, Map<IEvent, Set<IEvent>>> collectTargetTransitions(Map<Integer, Integer> mergesInto, Map<Integer, Integer> mergedLocations) {
        var targetTransitions = new HashMap<Pair<Integer, Integer>, Map<IEvent, Set<IEvent>>>();

        for (var location : model.getStates()) {
            var source = mergedLocations.get(mergesInto.get(location));
            if (source == null ) continue;
            for (var input : alphabet) {
                for (var transition : model.getTransitions(location, input)) {
                    var target = mergedLocations.get(mergesInto.get(model.getSuccessor(transition)));
                    if (target == null) {continue;}

                    //TODO Sigurd: Project inputs in a way that makes sense
                    var newInput = input;
                    targetTransitions.computeIfAbsent(Pair.of(source, target), ignored -> new HashMap<>())
                            .computeIfAbsent(newInput, ignored -> new HashSet<>()).add(input);
                }
            }
        }
        return targetTransitions;
    }


    private Map<Integer, Integer> computeMerges(int k) {
        var mergesInto = new HashMap<Integer, Integer>();
        var maybeFutures = new HashMap<Integer, Set<List<IEvent>>>();

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
        Collection<IEvent> inputs = null;
        try {
            inputs = model.getLocalInputs(source);
        } catch (Exception e) {
            System.out.println("Fucky wucky happened with source = " + source + " : " + e.getMessage() + " : #States = " + model.getStates().size());
            Visualization.visualize(model);
        }
        HashMap<Integer, Integer> correspondingStates = new HashMap<>();
        HashMap<Integer, Integer> parents = calculateParents(model);
        IEvent input = null;

        // Iterate till either reaching the end of sequence or multiple inputs are available from source
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

        // Enter if sequence meets length requirement of trivial sequence
        if (sequenceLength >= 5) {
            // Remove transitions in trivial sequence from model
            for (int i = 0; i < sequenceLength - 1; i++) {
                model.removeAllTransitions(statesInSequence.get(i));
                visitedTransitions.remove(statesInSequence.get(i));
            }

            // Create input for collapsed sequence
            if (input.getClass() == SymbolicTimedEvent.class) {
                input = new SymbolicTimedEvent("Collapsed trivial sequence ending with: " + input.getMessage(),
                        (((SymbolicTimedEvent) input).getSymbolicTime()));
            }   else {
                input = new Event("Collapsed trivial sequence ending with: " + input.getMessage());
            }

            model.addAlphabetSymbol(input);
            model.addTransition(statesInSequence.get(0), input, statesInSequence.get(sequenceLength - 1));
            System.out.println("Collapsed states in trivial sequence: " + statesInSequence);

            // Build new states and create reference to state in original model
            for (Integer state : model.getStates()){
                if(!statesInSequence.get(0).equals(state) && !statesInSequence.get(sequenceLength-1).equals(state)
                        && statesInSequence.contains(state))
                    continue;

                if(collapsedModel.getStates().isEmpty()){
                    correspondingStates.put(state, collapsedModel.addInitialState());
                } else {
                    correspondingStates.put(state, collapsedModel.addState());
                }
            }

            // Create alphabet and transitions for collapsed model
            for (Integer state : model.getStates()) {
                if(!correspondingStates.containsKey(state)) continue;
                Integer newSource = correspondingStates.get(state);
                for (IEvent event : model.getLocalInputs(state)) {
                    for (Integer transition : model.getTransitions(state, event)) {
                        Integer target = correspondingStates.get(model.getSuccessor(transition));

                        collapsedModel.addAlphabetSymbol(event);
                        collapsedModel.addTransition(newSource, event, target);
                    }
                }
            }
        } else {
            collapsedModel = model;
        }

        // Enter if not the last state in a sequence/branching
        if (inputs != null) {
            // If model has been altered
            if(!correspondingStates.isEmpty()){
                source = correspondingStates.get(source);
            }

            //Recursively call method to search the successor of each transition going from source
            for (IEvent event : inputs) {
                for (Integer transition : collapsedModel.getTransitions(source, event)){
                    if(visitedTransitions.contains(transition)){
                        continue;
                    }
                    visitedTransitions.add(transition);
                    int successor = collapsedModel.getSuccessor(transition);
                    if(successor > collapsedModel.getStates().size()){
                        continue;
                    }
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
