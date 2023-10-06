package com.igrium.scaffold.level.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * Handles attribute management via Java fields. Intended to ease the
 * programming of new Scaffold elements.
 * </p>
 * <p>
 * To define an attribute, simply mark a field in your class with
 * {@link Attrib @Attrib}. The field must be a type decending from
 * {@link Attribute}. Upon instantiation, the constructor will scan all the
 * fields of your class and its parents (subclasses first) and keep track of all
 * fields marked with this annotation. Then, these values can be dynamically
 * accessed and written to from various functions around the program.
 * </p>
 */
public class AttributeHolder {
    private final Map<String, Attribute<?>> attributes = new HashMap<>();

    public AttributeHolder() {
        setupClassReflection(getClass());
    }

    /**
     * Get an unmodifiable map of all the attributes.
     * 
     * @return A mapping between attribute names and their values.
     */
    public final Map<String, Attribute<?>> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Check if an attribute exists by a given name.
     * 
     * @param name Attribute name.
     * @return If the attribute exists.
     */
    public final boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    /**
     * Get an attribute by its name.
     * 
     * @param name Attribute name.
     * @return The attribute, or <code>null</code> of none exists by that name.
     */
    @Nullable
    public final Attribute<?> getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Get the value of an attribute by its name.
     * 
     * @param name Attribute name.
     * @return The attribute value, or <code>null</code> if the attribute does not
     *         exist.
     */
    @Nullable
    public final Object getAttributeValue(String name) {
        Attribute<?> attribute = attributes.get(name);
        return attribute != null ? attribute.getValue() : null;
    }
    
    protected <T> void onAttributeChanged(T oldValue, T newValue, String attributeName) {};

    private void setupClassReflection(Class<?> clazz) throws AttributeInitializationException {
        for (Field field : clazz.getDeclaredFields()) {
            String attributeName = field.getName();
            if (!field.isAnnotationPresent(Attrib.class)) continue;

            if (!Attribute.class.isAssignableFrom(field.getType())) {
                throw new AttributeInitializationException(attributeName, "Attribute field '" + attributeName + "' does not inherit from Attribute!");
            }

            field.setAccessible(true);
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            
            Attribute<?> attribute;
            try {
                attribute = (Attribute<?>) field.get(this);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new AttributeInitializationException(attributeName, e);
            }

            if (!isFinal || attribute == null) {
                throw new AttributeInitializationException(attributeName, "Attribute '" + attributeName + "' should be final and initialized inline.");
            }

            attributes.put(attributeName, attribute);

            Attrib attrib = field.getAnnotation(Attrib.class);
            String displayName = attrib.displayName();
            if (displayName == null || displayName.length() == 0) {
                displayName = attributeName;
            }

            attribute.setDisplayName(displayName);
            
            attribute.addChangeListener((oldVal, newVal) -> onAttributeChanged(null, null, attributeName));
        }
    }
}
