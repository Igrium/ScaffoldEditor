package com.igrium.scaffold.level.attributes;

import org.dom4j.Element;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.level.attribute.InvalidAttributeException;

public class EnumAttribute<E extends Enum<E>> extends BaseAttribute<E> {

    private Class<E> enumClass;

    public EnumAttribute(Class<E> enumClass) {
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
    public void readXML(Element element) throws InvalidAttributeException {
        String value = element.attributeValue("value");
        if (value == null || value.length() == 0)
            throw new InvalidAttributeException(element, "No value attribute found.");

        try {
            int ordinal = Integer.parseInt(value);
            E[] constants = enumClass.getEnumConstants();
            if (ordinal < 0 || ordinal >= constants.length)
                throw new InvalidAttributeException(element, "Enum ordinal is out of range.");

        // If there was a number format exception, this is text-based serialization.
        } catch (NumberFormatException e) {

            try {
                setValue(Enum.valueOf(enumClass, value));
            } catch (IllegalArgumentException t) {
                throw new InvalidAttributeException(element, "Name is invalid for this enum.", t);
            }

        }
    }

    @Override
    public void writeXML(Element element) {
        element.addAttribute("value", getValue().name());
    }

    @Override
    protected E defaultValue() {
        return enumClass.getEnumConstants()[0];
    }

}
