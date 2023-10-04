package com.igrium.scaffold.util;

import java.util.AbstractList;
import java.util.List;

/**
 * A read-only view over a number of lists.
 */
public class JoinedListView<T> extends AbstractList<T> {
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


}
