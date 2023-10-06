package com.igrium.scaffold.level.stack;

import java.util.Collections;
import java.util.Iterator;

import com.igrium.scaffold.level.element.ScaffoldElement;

/**
 * An element on the level stack.
 */
public class StackElement extends StackGroup {
    private final ScaffoldElement element;
    private Iterable<ScaffoldElement> iterable;

    public StackElement(ScaffoldElement element) {
            this.element = element;
            iterable = Collections.singleton(element);
        }

    public ScaffoldElement getElement() {
        return element;
    }

    @Override
    public Iterator<ScaffoldElement> iterator() {
        return iterable.iterator();
    }
}
