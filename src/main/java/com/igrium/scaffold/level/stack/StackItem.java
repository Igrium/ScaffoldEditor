package com.igrium.scaffold.level.stack;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.igrium.scaffold.level.element.ScaffoldElement;

/**
 * A single item on the level stack. May be an element or a group.
 */
public abstract class StackItem implements Iterable<ScaffoldElement> {
    protected StackGroup parent;

    @Nullable
    public List<StackItem> getChildren() {
        return null;
    }

    @Nullable
    public StackGroup getParent() {
        return parent;
    }

    @Nullable
    public void setParent(StackGroup parent) {
        this.parent = parent;
    }

    /**
     * Detach this stack item from its parent.
     */
    public void detach() {
        if (parent == null) return;
        parent.getChildren().remove(this);
        parent = null;
    }
}
