package com.igrium.scaffold.util;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A read-only view over a list that applies a mapping function to each element.
 */
public class MappedListView<T, O> extends AbstractList<O> {

    private final Function<T, O> mappingFunction;
    private final Supplier<List<T>> baseSupplier;

    /**
     * Create a mapped list view.
     * @param base The base list.
     * @param mappingFunction The mapping function.
     */
    public MappedListView(List<T> base, Function<T, O> mappingFunction) {
        this.baseSupplier = () -> base;
        this.mappingFunction = mappingFunction;
    }

    /**
     * Create a mapped list view.
     * 
     * @param base            Supplier for the base list for if it might change at
     *                        some point. Note: the base list changing will not
     *                        affect any active iterators.
     * @param mappingFunction The mapping functino.
     */
    public MappedListView(Supplier<List<T>> base, Function<T, O> mappingFunction) {
        this.baseSupplier = base;
        this.mappingFunction = mappingFunction;
    }

    private List<T> base() {
        return baseSupplier.get();
    }

    @Override
    public int size() {
        return base().size();
    }

    @Override
    public O get(int index) {
        return mappingFunction.apply(base().get(index));
    }
    
    @Override
    public Iterator<O> iterator() {
        return new MappedIterator(base().iterator());
    }

    @Override
    public ListIterator<O> listIterator(int index) {
        return new MappedListIterator(base().listIterator(index));
    }

    private class MappedIterator implements Iterator<O> {
        final Iterator<T> base;

        MappedIterator(Iterator<T> base) {
            this.base = base;
        }

        @Override
        public boolean hasNext() {
            return base.hasNext();
        }

        @Override
        public O next() {
            return mappingFunction.apply(base.next());
        }
    }

    private class MappedListIterator implements ListIterator<O> {

        final ListIterator<T> base;

        MappedListIterator(ListIterator<T> base) {
            this.base = base;
        }

        @Override
        public boolean hasNext() {
            return base.hasNext();
        }

        @Override
        public O next() {
            return mappingFunction.apply(base.next());
        }

        @Override
        public boolean hasPrevious() {
            return base.hasPrevious();
        }

        @Override
        public O previous() {
            return mappingFunction.apply(base.previous());
        }

        @Override
        public int nextIndex() {
            return base.nextIndex();
        }

        @Override
        public int previousIndex() {
            return base.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(O e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(O e) {
            throw new UnsupportedOperationException();
        }

       
    }
}
