package com.igrium.scaffold.level.stack;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterables;
import com.igrium.scaffold.level.element.ScaffoldElement;
import com.igrium.scaffold.util.collections.IndexedList;

/**
 * A group on the level stack.
 */
public class StackGroup extends StackItem {
    private IndexedList<StackItem> children;

    public StackGroup() {
        children = new IndexedList<>();
        children.setListener(this::onChildCountUpdated);
    }

    public List<StackItem> getChildren() {
        return children;
    }

    @Override
    public Iterator<ScaffoldElement> iterator() {
        return Iterables.concat(children).iterator();
    }

    // Stack items may only be a part of one group at a time.
    private void onChildCountUpdated(StackItem child, int count) {
        if (count <= 0) {
            child.setParent(null);
        } else {
            child.detach();
            child.setParent(this);
        }
    }
}
