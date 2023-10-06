package com.igrium.scaffold.level.stack;

import java.util.Collections;
import java.util.Iterator;

import com.igrium.scaffold.level.item.ScaffoldItem;

/**
 * An element on the level stack.
 */
public class StackElement extends StackGroup {
    private final ScaffoldItem item;
    private Iterable<ScaffoldItem> iterable;

    public StackElement(ScaffoldItem item) {
            this.item = item;
            iterable = Collections.singleton(item);
        }

    public ScaffoldItem getItem() {
        return item;
    }

    @Override
    public Iterator<ScaffoldItem> iterator() {
        return iterable.iterator();
    }
}
