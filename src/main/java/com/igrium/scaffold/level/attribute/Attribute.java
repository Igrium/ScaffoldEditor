package com.igrium.scaffold.level.attribute;

import java.util.Set;

import org.dom4j.Element;

import com.igrium.scaffold.util.InvalidXMLException;
import com.igrium.scaffold.util.XMLSerializable;

/**
 * A field within an element that is saved into the level file and can be modified
 * in the UI.
 */
public interface Attribute<T> extends XMLSerializable {

    /**
     * Get the value of the attribute.
     * @return Current attribute value.
     */
    public T getValue();
    
    /**
     * Set the value of the attribute.
     * @param value New attribute value.
     */
    public void setValue(T value);
    
    /**
     * Attempt to get the value as a specific type.
     * @param <V> Type to get.
     * @param clazz Type class.
     * @return The value.
     * @throws ClassCastException If the value cannot be cast to this class.
     */
    public default <V> V getValue(Class<V> clazz) throws ClassCastException {
        return clazz.cast(getValue());
    }

    /**
     * Cast and set the value of this attribute.
     * @param value New attribute value.
     * @throws ClassCastException If the cast fails.
     */
    public default void trySetValue(Object value) throws ClassCastException {
        setValue(getType().cast(value));
    }

    /**
     * An unmodifiable set of all the registered change listeners.
     */
    public Set<AttributeChangeListener<T>> changeListeners();

    /**
     * Add an attribute change listener.
     * 
     * @param listener Change listener.
     */
    public void addChangeListener(AttributeChangeListener<T> listener);

    /**
     * Remove an attribute change listener.
     * 
     * @param listener Change listener.
     * @return If this change listener was found and removed.
     */
    public boolean removeChangeListener(AttributeChangeListener<?> listener);


    public void readXML(Element element) throws InvalidXMLException;

    public void writeXML(Element element);

    /**
     * Get the class this attribute will use.
     */
    public Class<T> getType();

    /**
     * The display name of this attribute. Often setup by reflection during init.
     */
    public String getDisplayName();

    public void setDisplayName(String displayName);
}
