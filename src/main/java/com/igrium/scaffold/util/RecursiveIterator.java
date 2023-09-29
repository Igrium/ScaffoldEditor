package com.igrium.scaffold.util;

import java.util.Iterator;

/**
 * An iterator that recursively iterates through child iterables.
 */
public class RecursiveIterator<T> implements Iterator<T> {

    private Iterator<? extends Iterable<T>> masterIterator;
    private Iterator<T> currentIterator;

    public RecursiveIterator(Iterator<? extends Iterable<T>> masterIterator) {
        this.masterIterator = masterIterator;
    }

    public RecursiveIterator(Iterable<? extends Iterable<T>> masterIterable) {
        this.masterIterator = masterIterable.iterator();
    }

    @Override
    public boolean hasNext() {
        return masterIterator.hasNext() || (currentIterator != null && currentIterator.hasNext());
    }

    @Override
    public T next() {
        if (currentIterator != null) {
            T val = currentIterator.next();
            if (!currentIterator.hasNext()) currentIterator = null;
            return val;
        } else {
            currentIterator = masterIterator.next().iterator();
            return next();
        }
    }
    
}
