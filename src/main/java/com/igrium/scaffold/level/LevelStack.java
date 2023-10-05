package com.igrium.scaffold.level;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterables;
import com.igrium.scaffold.level.item.ScaffoldItem;

public class LevelStack {

    /**
     * An item that can appear in the outliner. Each item is an iterable of all the
     * scaffold items found in it, recursive.
     */
    public static abstract class StackItem implements Iterable<ScaffoldItem> {

        @Nullable
        public List<StackItem> getChildren() {
            return null;
        }
    }

    public static class StackGroup extends StackItem {
        private List<StackItem> children;

        public List<StackItem> getChildren() {
            return children;
        }

        @Override
        public Iterator<ScaffoldItem> iterator() {
            return Iterables.concat(children).iterator();
        }
    }
    

    public static class StackSingleItem extends StackItem {
        private final ScaffoldItem item;
        private Iterable<ScaffoldItem> iterable;

        public StackSingleItem(ScaffoldItem item) {
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

    private final StackGroup rootGroup = new StackGroup();

    public StackGroup getRootGroup() {
        return rootGroup;
    }
    
    public Iterable<ScaffoldItem> items() {
        return rootGroup;
    }
}
