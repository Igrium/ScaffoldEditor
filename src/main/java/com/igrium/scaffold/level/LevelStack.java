package com.igrium.scaffold.level;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.igrium.scaffold.util.RecursiveIterator;

public class LevelStack {

    /**
     * An item that can appear in the outliner.
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
            return new RecursiveIterator<>(children);
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
}
