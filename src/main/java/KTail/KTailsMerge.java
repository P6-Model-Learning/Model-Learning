package KTail;

import aal.syslearner.IEvent;
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
        return merged;
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
}
