package KTail;

import aal.syslearner.Event;
import net.automatalib.automata.fsa.NFA;
import net.automatalib.commons.util.Pair;
import net.automatalib.words.Alphabet;

import java.util.*;
import java.util.stream.Collectors;

public class KTailsComputation {
    private final NFA<Integer, Event> model;
    private final Map<Integer, Map<Integer, Set<List<Event>>>> kFutureCache;
    private final Alphabet<Event> inputs;

    public KTailsComputation(NFA<Integer, Event> model, Alphabet<Event> inputs) {
        this.model = model;
        this.inputs = inputs;

        model.getStates();
        kFutureCache = new HashMap<>();

        //Base case for k future is just the full set of locations
        kFutureCache.put(0, model.getStates().stream().map(
                location -> Pair.of(location, Set.of(List.<Event>of()))
        ).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
    }

    public Set<List<Event>> getKFuturesOf(int k, Integer location) {
        computeKFuturesUpTo(k);
        return kFutureCache.get(k).get(location);
    }

    private void computeKFuturesUpTo(int k) {
        if (k < 0) {
            throw new IllegalArgumentException();
        }
        if (kFutureCache.containsKey(k)) {
            return;
        }
        computeKFuturesUpTo(k - 1);
        cacheKFutures(k);
    }

    private void cacheKFutures(int k) {
        kFutureCache.put(k, new HashMap<>());
        for (Integer location : model.getStates()) {
            cacheKFutures(k, location);
        }
    }

    private void cacheKFutures(int k , Integer location) {
        var shorterFutures = kFutureCache.get(k - 1);
        var futures = inputs.stream().flatMap(input -> model.getTransitions(location, input).stream().flatMap(
                transition -> shorterFutures.get(model.getSuccessor(transition)).stream().map(future -> {
                    List<Event> newFuture = new LinkedList<>();
                    newFuture.add(input);
                    newFuture.addAll(future);
                    return newFuture;
                })
        )).collect(Collectors.toSet());
        kFutureCache.get(k).put(location, futures);
    }
}
