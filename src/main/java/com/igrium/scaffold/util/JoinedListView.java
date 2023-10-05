package com.igrium.scaffold.util;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

/**
 * A read-only view over a number of lists.
 */
public class JoinedListView<T> extends AbstractList<T> {
    @SafeVarargs
    public static <T> JoinedListView<T> create(List<T>... lists) {
        return new JoinedListView<>(lists);
    }

    private List<T>[] lists;
    

    public JoinedListView(List<T>[] lists) {
        this.lists = lists.clone();
    }
    
    @SuppressWarnings("unchecked")
    public JoinedListView(List<T> lists) {
        this.lists = lists.toArray(List[]::new);
    }

    private int listForIndex(int index) {
        int globalIndex = 0;
        for (int i = 0; i < lists.length; i++) {
            globalIndex += lists[0].size();
            if (index < globalIndex) return i;
        }
        throw new IndexOutOfBoundsException(index);
    }
    
    private int startIndex(int listIndex) {
        int globalIndex = 0;
        for (int i = 0; i < listIndex; i++) {
            globalIndex += lists[i].size();
        }
        return globalIndex;
    }

    @Override
    public int size() {
        int size = 0;
        for (int i = 0; i < lists.length; i++) {
            size += lists[i].size();
        }
        return size;
    }


    @Override
    public T get(int index) {
        int listIndex = listForIndex(index);
        return lists[listIndex].get(index - startIndex(listIndex));
    }

    @Override
    public Iterator<T> iterator() {
        return new JoinedIterator();
    }

    private class JoinedIterator implements Iterator<T> {
        Iterator<T> currentIterator;
        int listIndex;

        public JoinedIterator() {
            currentIterator = lists[0].iterator();
            listIndex = 0;
        }

        @Override
        public boolean hasNext() {
            if (currentIterator.hasNext()) return true;

            // See if there's any more lists with items.
            for (int i = listIndex + 1; i < lists.length; i++) {
                if (!lists[i].isEmpty()) return true;
            }
            return false;
        }

        @Override
        public T next() {
            while (!currentIterator.hasNext()) {
                listIndex++;
                currentIterator = lists[listIndex].iterator();
            }

            return currentIterator.next();
        }
    }

    // TODO: implement list iterator.
}
