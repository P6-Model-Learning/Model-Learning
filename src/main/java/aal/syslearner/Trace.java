package aal.syslearner;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Trace implements Iterable<IEvent> {
    public Trace(List<IEvent> events){
        id = idCount++;
        this.events = events;
    }

    private static int idCount = 0;
    private final int id;
    private final List<IEvent> events;

    public int getId(){
        return id;
    }

    public List<IEvent> getEvents(){
        return events;
    }

    @Override
    public Iterator<IEvent> iterator() {
        return this.events.iterator();
    }

    @Override
    public void forEach(Consumer action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<IEvent> spliterator() {
        return Iterable.super.spliterator();
    }
}
