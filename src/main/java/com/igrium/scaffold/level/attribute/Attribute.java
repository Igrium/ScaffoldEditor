package com.igrium.scaffold.level.attribute;

import java.util.Set;

import org.dom4j.Element;

/**
 * A field within an item that is saved into the level file and can be modified
 * in the UI.
 */
public interface Attribute<T> {

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

    /**
     * Read the data from an XML element and apply it to this attribute.
     * 
     * @param element The element to read from.
     * @throws InvalidAttributeException If there is something wrong with the data
     *                                   in the element.
     */
    public void readXML(Element element) throws InvalidAttributeException;

    /**
     * Write the data from this attribute into an XML element.
     * 
     * @param element The element to write to.
     */
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