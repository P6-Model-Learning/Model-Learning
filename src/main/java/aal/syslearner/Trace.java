package aal.syslearner;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Trace implements Iterable<Event> {
    public Trace(List<Event> events){
        id = idCount++;
        this.events = events;
    }

    private static int idCount = 1;
    private final int id;
    private final List<Event> events;

    public int getId(){
        return id;
    }

    public List<Event> getEvents(){
        return events;
    }

    @Override
    public Iterator<Event> iterator() {
        return this.events.iterator();
    }

    @Override
    public void forEach(Consumer action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Event> spliterator() {
        return Iterable.super.spliterator();
    }
}
