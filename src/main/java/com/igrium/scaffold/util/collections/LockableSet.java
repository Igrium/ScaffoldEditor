package com.igrium.scaffold.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * A set view that can be locked and unlocked. If the list is locked, any write
 * operations to it (directly or through iterators) will throw a
 * {@link LockedListException}.
 */
public class LockableSet<T> implements Set<T>, Lockable {

    private final Set<T> base;

    /**
     * Create a lockable set.
     * @param base The underlying set.
     */
    public LockableSet(Set<T> base) {
        this.base = base;
    }

    private boolean locked;

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public final boolean isLocked() {
        return locked;
    }

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return base.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new LockableIterator(base.iterator());
    }

    @Override
    public Object[] toArray() {
        return base.toArray();
    }

    @Override
    public <A> A[] toArray(A[] a) {
        return base.toArray(a);
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return base.toArray(generator);
    }

    @Override
    public boolean add(T e) {
        assertNotLocked();
        return base.add(e);
    }

    @Override
    public boolean remove(Object o) {
        assertNotLocked();
        return base.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return base.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        assertNotLocked();
        return base.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        assertNotLocked();
        return base.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        assertNotLocked();
        return base.removeAll(c);
    }
    
    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        assertNotLocked();
        return base.removeIf(filter);
    }

    @Override
    public void clear() {
        assertNotLocked();
        base.clear();
    }
    
    private void assertNotLocked() {
        if (isLocked()) throw new LockedCollectionException();
    }

    private class LockableIterator implements Iterator<T> {

        final Iterator<T> base;

        LockableIterator(Iterator<T> base) {
            this.base = base;
        }

        @Override
        public boolean hasNext() {
            return base.hasNext();
        }

        @Override
        public T next() {
            return base.next();
        }
        
        @Override
        public void remove() {
            assertNotLocked();
            base.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            base.forEachRemaining(action);
        }
    }
}
