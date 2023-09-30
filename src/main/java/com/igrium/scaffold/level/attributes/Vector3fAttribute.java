package com.igrium.scaffold.level.attributes;

import org.dom4j.Element;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.level.attribute.InvalidAttributeException;

public class Vector3fAttribute extends BaseAttribute<Vector3fc> {

    @Override
    public void setValue(Vector3fc value) {
        // Make sure we don't have any unwanted mutable references.
        super.setValue(new Vector3f(value));
    }

    @Override
    public void readXML(Element element) throws InvalidAttributeException {
        String value = element.attributeValue("value");
        if (value == null || value.length() == 0)
            throw new InvalidAttributeException(element, "No value attribute found.");

        value = value.replace("[", "").replaceAll("]", "");
        String[] values = value.split(",");
        if (values.length != 3)
            throw new InvalidAttributeException(element,
                    String.format("Incorrect number of elements in array. %d != %d", values.length, 3));

        try {
            setValue(new Vector3f(
                    Float.parseFloat(values[0]),
                    Float.parseFloat(values[1]),
                    Float.parseFloat(values[2])));
        } catch (NumberFormatException e) {
            throw new InvalidAttributeException(element, "Unable to parse number value(s).", e);
        }

    }

    @Override
    public void writeXML(Element element) {
        Vector3fc value = getValue();
        String text = String.format("[%f, %f, %f]", value.x(), value.y(), value.z());
        element.addAttribute("value", text);
    }

    @Override
    public Class<Vector3fc> getType() {
        return Vector3fc.class;
    }

}