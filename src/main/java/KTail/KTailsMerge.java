package KTail;

import aal.syslearner.IEvent;
import aal.syslearner.Event;
import aal.syslearner.Symbolic.SymbolicTimedEvent;
import com.google.common.collect.MoreCollectors;
import net.automatalib.automata.fsa.NFA;
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

        System.out.println("Merged model before CTS: " + merged.getStates().size());

        var changedFinalStates = computeFinalStateChange(mergesInto);

        //Changes the NFA to contain accept states after the merge
        var temp = new ArrayList<Integer>();
        for (var state : changedFinalStates){
            merged.setAccepting(mergedLocations.get(state), true);
            temp.add(mergedLocations.get(state));
            System.out.println(mergedLocations.get(state) + " is accepting");
        }
        changedFinalStates = temp;
        return merged;
        //return collapseTrivialSequences(merged, changedFinalStates);
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

    private List<Integer> computeFinalStateChange(Map<Integer, Integer> mergesInto){
        var result = new ArrayList<Integer>();

        for (var state : mergesInto.keySet()){
            if (model.isAccepting(state)){
                result.add(mergesInto.get(state));
                System.out.println(state + " maps to " + mergesInto.get(state));
            }
        }

        return result;
    }


    private CompactNFA<IEvent> collapseTrivialSequences(CompactNFA<IEvent> model, List<Integer> oldAcceptStates){
        CompactNFA<IEvent> collapsed = null;
        HashMap<Integer, Integer> correspondingStates = new HashMap<>();
        IEvent input;
        HashMap<Integer, Integer> parents = calculateParents(model);
        ArrayList<ArrayList<Integer>> trivialSequences = determineTrivialSequences(new ArrayList<>(), model, 0, new HashSet<>(), parents, oldAcceptStates);

        // If no trivial Sequences exist return
        if (trivialSequences.size() == 0) {
            return model;
        }

        // Remove trivial sequences
        for (List<Integer> sequence : trivialSequences) {
            collapsed = new CompactNFA<>(new GrowingMapAlphabet<>());
            input = model.getLocalInputs(sequence.get(sequence.size() - 2)).iterator().next();

            // Remove transitions in trivial sequence from model
            for (int i = 0; i < sequence.size() - 1; i++) {
                model.removeAllTransitions(sequence.get(i));
            }

            // Create input for collapsed sequence
            if (input.getClass() == SymbolicTimedEvent.class) {
                input = new SymbolicTimedEvent("Collapsed trivial sequence ending with: " + input.getMessage(),
                        (((SymbolicTimedEvent) input).getSymbolicTime()));
            } else {
                input = new Event("Collapsed trivial sequence ending with: " + input.getMessage());
            }

            model.addAlphabetSymbol(input);
            model.addTransition(sequence.get(0), input, sequence.get(sequence.size() - 1));

            // Build new states and create reference to state in original model
            for (Integer state : model.getStates()){
                if(!sequence.get(0).equals(state) && !sequence.get(sequence.size() - 1).equals(state)
                        && sequence.contains(state))
                    continue;

                if(collapsed.getStates().isEmpty()){
                    correspondingStates.put(state, collapsed.addInitialState());
                } else {
                    correspondingStates.put(state, collapsed.addState());
                }
            }

            // Create alphabet and transitions for collapsed model
            for (Integer state : model.getStates()) {
                if (!correspondingStates.containsKey(state)) continue;
                Integer newSource = correspondingStates.get(state);
                for (IEvent event : model.getLocalInputs(state)) {
                    for (Integer transition : model.getTransitions(state, event)) {
                        Integer target = correspondingStates.get(model.getSuccessor(transition));
                        collapsed.addAlphabetSymbol(event);
                        collapsed.addTransition(newSource, event, target);
                    }
                }
            }

            model = collapsed;

            // Update trivial sequences to match change of model
            for (int i = trivialSequences.indexOf(sequence) + 1; i < trivialSequences.size(); i++){
                ArrayList<Integer> modifiedSequence = trivialSequences.get(i);
                modifiedSequence.replaceAll(correspondingStates::get);
                trivialSequences.set(i, modifiedSequence);
            }

            //Updates the list of accept states iteratively
            var tempList = new ArrayList<Integer>();
            for (var accept : oldAcceptStates) {
                tempList.add(correspondingStates.get(accept));
                System.out.println(accept + " maps to " + correspondingStates.get(accept));
            }
            System.out.println();
            oldAcceptStates = tempList;
        }

        //Sets the corresponding states as accepting
        for (var accept : oldAcceptStates) {
            collapsed.setAccepting(accept, true);
            System.out.println(accept + " is accepting");
        }

        return collapsed;
    }

    private ArrayList<ArrayList<Integer>> determineTrivialSequences(ArrayList<ArrayList<Integer>> trivialSequences, CompactNFA<IEvent> model, Integer source,
                                                                    HashSet<Integer> visited, HashMap<Integer, Integer> parents, List<Integer> acceptStates){
        int sequenceLength;
        ArrayList<Integer> statesInSequence = new ArrayList<>();
        Collection<IEvent> inputs = model.getLocalInputs(source);
        IEvent input;

        // Iterate till either reaching the end of sequence or multiple inputs are available from source
        while (inputs != null && inputs.size() == 1) {
            input = model.getLocalInputs(source).iterator().next();
            // Break if sequence branches nondeterministically
            if(model.getSuccessors(source, input).size() != 1){
                break;
            }

            // Break if multiple parents for all other states than first
            if(parents.get(source) != 1 && statesInSequence.size() > 0){
                break;
            }

            //Break if an accept state is part of the sequence
            if (acceptStates.contains(source)){
                break;
            }

            statesInSequence.add(source);
            visited.add(model.getTransitions(source, input).iterator().next());
            source = model.getSuccessors(source, input).iterator().next();
            inputs = model.getLocalInputs(source);
        }

        statesInSequence.add(source);
        sequenceLength = statesInSequence.size();

        // Enter if sequence meets length requirement of trivial sequence
        if (sequenceLength >= Math.ceil(0.05 * model.getStates().size())) {
            trivialSequences.add(statesInSequence);
            System.out.println("States in trivial sequence: " + statesInSequence);
        }

        // Enter if not the last state in a sequence/branching
        if (inputs != null) {
            //Recursively call method to search the successor of each transition going from source
            for (IEvent event : inputs) {
                for (Integer transition : model.getTransitions(source, event)){
                    if(visited.contains(transition)){
                        continue;
                    }
                    visited.add(transition);
                    trivialSequences = determineTrivialSequences(trivialSequences, model, model.getSuccessor(transition),
                            visited, parents, acceptStates);
                }
            }
        }
        return trivialSequences;
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
