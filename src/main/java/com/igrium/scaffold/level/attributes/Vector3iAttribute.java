package com.igrium.scaffold.level.attributes;

import org.dom4j.Element;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.level.attribute.InvalidAttributeException;

public class Vector3iAttribute extends BaseAttribute<Vector3ic> {

    @Override
    public void setValue(Vector3ic value) {
        // Make sure we don't have any unwanted mutable references.
        super.setValue(new Vector3i(value));
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
            setValue(new Vector3i(
                    Integer.parseInt(values[0]),
                    Integer.parseInt(values[1]),
                    Integer.parseInt(values[2])));
        } catch (NumberFormatException e) {
            throw new InvalidAttributeException(element, "Unable to parse number value(s).", e);
        }

    }

    @Override
    public void writeXML(Element element) {
        Vector3ic value = getValue();
        String text = String.format("[%d, %d, %d]", value.x(), value.y(), value.z());
        element.addAttribute("value", text);
    }

    @Override
    public Class<Vector3ic> getType() {
        return Vector3ic.class;
    }

    @Override
    protected Vector3ic defaultValue() {
        return new Vector3i();
    }

}