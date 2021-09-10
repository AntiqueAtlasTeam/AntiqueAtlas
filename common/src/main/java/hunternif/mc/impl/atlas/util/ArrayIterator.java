package hunternif.mc.impl.atlas.util;

import java.util.Iterator;

/**
 * A simple array iterator.
 *
 * @author Hunternif
 */
public class ArrayIterator<T> implements Iterator<T> {
    private final T[] array;
    private int currentIndex = 0;

    public ArrayIterator(T[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < array.length;
    }

    @Override
    public T next() {
        return array[currentIndex++];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("cannot remove items from an array");
    }
}
