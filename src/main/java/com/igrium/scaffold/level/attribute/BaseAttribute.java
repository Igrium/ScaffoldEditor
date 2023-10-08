package com.igrium.scaffold.level.attribute;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.dom4j.Element;

import com.igrium.scaffold.util.InvalidXMLException;

/**
 * A basic implementation of Attribute that takes care of managing listeners and the like.
 */
public abstract class BaseAttribute<T> implements Attribute<T> {
    private T value;

    private final Set<AttributeChangeListener<T>> changeListeners = new HashSet<>();

    private String displayName = "";

    public BaseAttribute() {
        value = defaultValue();
    }

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

        changeListeners.forEach(listener -> listener.onChanged(oldValue, value));
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

    public abstract void readXML(Element element) throws InvalidXMLException;

    public abstract void writeXML(Element element);

    /**
     * Get the default value that this attribute will hold at spawn.
     * 
     * @return The default value. If the value is mutable, this must be a new
     *         instance.
     */
    protected abstract T defaultValue();

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Attribute attribute) {
            return Objects.equals(getValue(), attribute.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
