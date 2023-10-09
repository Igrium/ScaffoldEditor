package com.igrium.scaffold.level.attributes;

import org.dom4j.Element;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.util.InvalidXMLException;

public class EnumAttribute<E extends Enum<E>> extends BaseAttribute<E> {

    private Class<E> enumClass;

    public EnumAttribute(E initialValue) {
        super(initialValue);
        if (!initialValue.getClass().isEnum()) {
            throw new IllegalArgumentException("The supplied class must be an enum.");
        }
        this.enumClass = initialValue.getDeclaringClass();
    }

    public EnumAttribute(Class<E> enumClass) {
        super(enumClass.getEnumConstants()[0]);
        this.enumClass = enumClass;
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("The supplied class must be an enum.");
        }
    }

    @Override
    public Class<E> getType() {
        return enumClass;
    }

    @Override
    public void readXML(Element element) throws InvalidXMLException {
        String value = element.attributeValue("value");
        if (value == null || value.length() == 0)
            throw new InvalidXMLException(element, "No value attribute found.");

        try {
            int ordinal = Integer.parseInt(value);
            E[] constants = enumClass.getEnumConstants();
            if (ordinal < 0 || ordinal >= constants.length)
                throw new InvalidXMLException(element, "Enum ordinal is out of range.");

        // If there was a number format exception, this is text-based serialization.
        } catch (NumberFormatException e) {

            try {
                setValue(Enum.valueOf(enumClass, value));
            } catch (IllegalArgumentException t) {
                throw new InvalidXMLException(element, "Name is invalid for this enum.", t);
            }

        }
    }

    @Override
    public void writeXML(Element element) {
        element.addAttribute("value", getValue().name());
    }
}
