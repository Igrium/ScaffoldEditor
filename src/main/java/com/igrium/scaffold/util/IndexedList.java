package com.igrium.scaffold.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * A list that keeps track of how many instances of each element it has.
 */
public class IndexedList<T> extends AbstractList<T> {

    public static interface ListListener<T> {
        public void onCountUpdated(T element, int count);
    }

    @SuppressWarnings("rawtypes")
    private static ListListener DEFAULT_LISTENER = (val, count) -> {};
    @SuppressWarnings("unchecked")
    public static <T> ListListener<T> emptyListener() {
        return DEFAULT_LISTENER;
    }

    private List<T> base;

    /**
     * Use a weak hash map so the list doesn't hold references to objects its no
     * longer using.
     */
    private Map<T, Integer> itemCounts = new WeakHashMap<>();

    protected ListListener<T> listener = emptyListener();
    
    public IndexedList() {
        base = new ArrayList<>();
    }
    
    // public IndexedList(Supplier<List<T>> listFactory) {
    //     base = listFactory.get();
    // }

    public void setListener(ListListener<T> listener) {
        this.listener = listener != null ? listener : emptyListener();
    }

    public ListListener<T> getListener() {
        return listener;
    }

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public T get(int index) {
        return base.get(index);
    }

    @Override
    public T set(int index, T element) {
        T prev = base.set(index, element);
        if (Objects.equals(element, prev))
            return prev;

        if (element != null) {
            updateCount(element, i -> i + 1);
            listener.onCountUpdated(element, itemCounts.get(element));
        }

        if (prev != null) {
            updateCount(prev, i -> i - 1);
            listener.onCountUpdated(element, itemCounts.get(prev));
        }

        return prev;
    }

    @Override
    public void add(int index, T element) {
        base.add(index, element);
        if (element != null) {
            updateCount(element, i -> i + 1);
            listener.onCountUpdated(element, count(element));
        }
    }

    @Override
    public T remove(int index) {
        T val = base.remove(index);
        if (val != null) {
            updateCount(val, i -> i - 1);
            listener.onCountUpdated(val, count(val));
        }
        return val;
    }

    public int count(T element) {
        Integer count = itemCounts.get(element);
        return count != null ? count : 0;
    }

    private void updateCount(T key, Function<Integer, Integer> func) {
        itemCounts.put(key, func.apply(count(key)));
    }

    public void recount() {
        itemCounts.clear();

        for (T element : base) {
            updateCount(element, i -> i + 1);
        }
    }
}
