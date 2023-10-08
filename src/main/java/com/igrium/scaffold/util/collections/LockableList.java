package com.igrium.scaffold.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * A list view that can be locked and unlocked. If the list is locked, any write
 * operations to it (directly or through iterators) will throw a
 * {@link LockedListException}.
 */
public class LockableList<T> implements List<T>, Lockable {

    private final List<T> base;

    /**
     * Create a lockable list.
     * @param baseList The underlying list.
     */
    public LockableList(List<T> baseList) {
        this.base = baseList;
    }

    private boolean locked;

    /**
     * Set this list's lock state.
     * @param locked New lock state.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Get this list's lock state.
     * @return Is this list locked?
     */
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
        return base.contains(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        assertNotLocked();
        return base.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        assertNotLocked();
        return base.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        assertNotLocked();
        return base.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        assertNotLocked();
        return base.retainAll(c);
    }

    @Override
    public void clear() {
        assertNotLocked();
        base.clear();
    }

    @Override
    public T get(int index) {
        return base.get(index);
    }

    @Override
    public T set(int index, T element) {
        assertNotLocked();
        return base.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        assertNotLocked();
        base.add(index, element);;
    }

    @Override
    public T remove(int index) {
        assertNotLocked();
        return base.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return base.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return base.indexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new LockableListIterator(base.listIterator());
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new LockableListIterator(base.listIterator(index));
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new LockableList<>(base.subList(fromIndex, toIndex));
    }

    private void assertNotLocked() {
        if (isLocked()) throw new LockedCollectionException();
    }

    private class LockableIterator implements Iterator<T> {

        Iterator<T> base;

        public LockableIterator(Iterator<T> base) {
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

    private class LockableListIterator implements ListIterator<T> {

        ListIterator<T> base;

        public LockableListIterator(ListIterator<T> base) {
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
        public boolean hasPrevious() {
            return base.hasPrevious();
        }

        @Override
        public T previous() {
            return base.previous();
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
            assertNotLocked();
            base.remove();
        }

        @Override
        public void set(T e) {
            assertNotLocked();
            base.set(e);
        }

        @Override
        public void add(T e) {
            assertNotLocked();
            base.add(e);
        }
        
    }
}
