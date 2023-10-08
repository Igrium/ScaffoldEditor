package com.igrium.scaffold.util.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class LockableMap<K, V> implements Map<K, V>, Lockable {

    private final Map<K, V> base;
    private final LockableSet<K> keySet;
    private final LockableCollection<V> values;
    private final LockableSet<Map.Entry<K, V>> entrySet;

    public LockableMap(Map<K, V> base) {
        this.base = base;
        this.keySet = new LockableSet<>(base.keySet());
        this.values = new LockableCollection<>(base.values());
        this.entrySet = new LockableSet<>(base.entrySet());
    }

    private boolean locked;

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
        keySet.setLocked(locked);
        values.setLocked(locked);
        entrySet.setLocked(locked);
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
    public boolean containsKey(Object key) {
        return base.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return base.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return base.get(key);
    }

    @Override
    public V put(K key, V value) {
        assertNotLocked();
        return base.put(key, value);
    }

    @Override
    public V remove(Object key) {
        assertNotLocked();
        return base.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        assertNotLocked();
        base.putAll(m);;
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public Set<K> keySet() {
        return keySet();
    }

    @Override
    public Collection<V> values() {
        return values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet();
    }
    
    private void assertNotLocked() {
        if (isLocked()) throw new LockedCollectionException();
    }
}
