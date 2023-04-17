package KTail;

import aal.syslearner.IEvent;
import net.automatalib.automata.fsa.NFA;
import net.automatalib.commons.util.Pair;
import net.automatalib.words.Alphabet;

import java.util.*;
import java.util.stream.Collectors;

public class KTailsComputation {
    private final NFA<Integer, IEvent> model;
    private final Map<Integer, Map<Integer, Set<List<IEvent>>>> kFutureCache;
    private final Alphabet<IEvent> inputs;

    public KTailsComputation(NFA<Integer, IEvent> model, Alphabet<IEvent> inputs) {
        this.model = model;
        this.inputs = inputs;

        model.getStates();
        kFutureCache = new HashMap<>();

        //Base case for k future is just the full set of locations
        kFutureCache.put(0, model.getStates().stream().map(
                location -> Pair.of(location, Set.of(List.<IEvent>of()))
        ).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
    }

    public Set<List<IEvent>> getKFuturesOf(int k, Integer location) {
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
                    List<IEvent> newFuture = new LinkedList<>();
                    newFuture.add(input);
                    newFuture.addAll(future);
                    return newFuture;
                })
        )).collect(Collectors.toSet());

        // This might be a hack, but it fixes the tail of the ktails algorithm
        if (futures.size() == 0) {
            futures = shorterFutures.get(location);
        }

        kFutureCache.get(k).put(location, futures);
    }
}
