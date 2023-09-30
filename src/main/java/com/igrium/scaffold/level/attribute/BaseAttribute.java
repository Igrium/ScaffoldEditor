package com.igrium.scaffold.level.attribute;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Element;

/**
 * A basic implementation of Attribute that takes care of managing listeners and the like.
 */
public abstract class BaseAttribute<T> implements Attribute<T> {
    private T value;

    private final Set<AttributeChangeListener<T>> changeListeners = new HashSet<>();

    private String displayName = "";

    public T getValue() {
        return value;
    }

    /**
     * Set the value of the attribute.
     * @param value New attribute value.
     */
    public void setValue(T value) {
        T oldValue = this.value;
        this.value = value;

        changeListeners.forEach(listener -> listener.onChanged(oldValue, value, this));
    }

    public final Set<AttributeChangeListener<T>> changeListeners() {
        return Collections.unmodifiableSet(changeListeners);
    }

    /**
     * Add an attribute change listener.
     * @param listener Change listener.
     */
    public void addChangeListener(AttributeChangeListener<T> listener) {
        changeListeners.add(listener);
    }

    /**
     * Remove an attribute change listener.
     * @param listener Change listener.
     * @return If this change listener was found and removed.
     */
    public boolean removeChangeListener(AttributeChangeListener<?> listener) {
        return changeListeners.remove(listener);
    }

    /**
     * Read the data from an XML element and apply it to this attribute.
     * 
     * @param element The element to read from.
     * @throws InvalidAttributeException If there is something wrong with the data
     *                                   in the element.
     */
    public abstract void readXML(Element element) throws InvalidAttributeException;

    /**
     * Write the data from this attribute into an XML element.
     * @param element The element to write to.
     */
    public abstract void writeXML(Element element);

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
