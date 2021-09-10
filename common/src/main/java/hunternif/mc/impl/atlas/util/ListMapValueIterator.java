package hunternif.mc.impl.atlas.util;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Iterates over the values in a map of lists, similar to
 * {@link Maps#valueIterator}. This iterator will reflect changes made to the
 * map if map.values() does.
 *
 * @author Hunternif
 */
public class ListMapValueIterator<E> implements Iterator<E> {
    private final Iterator<List<E>> valuesIter;
    private Iterator<E> nextListIter;
    private E next;
    private boolean immutable = false;

    public ListMapValueIterator(Map<?, List<E>> map) {
        valuesIter = map.values().iterator();
    }

    public ListMapValueIterator<E> setImmutable(boolean value) {
        this.immutable = value;
        return this;
    }

    @Override
    public boolean hasNext() {
        if (this.next == null) {
            this.next = findNext();
        }
        return next != null;
    }

    @Override
    public E next() {
        if (this.next == null) {
            this.next = findNext();
        }
        E next = this.next;
        this.next = null;
        return next;
    }

    @Override
    public void remove() {
        if (!immutable) {
            nextListIter.remove();
        }
    }

    private E findNext() {
        while (nextListIter == null
                || !nextListIter.hasNext()) { // This condition allows to skip empty lists.
            if (valuesIter.hasNext()) {
                nextListIter = valuesIter.next().iterator();
            } else {
                return null;
            }
        }
        return nextListIter.next();
    }
}
