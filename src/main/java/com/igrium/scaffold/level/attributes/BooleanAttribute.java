package com.igrium.scaffold.level.attributes;

import org.dom4j.Element;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.util.InvalidXMLException;

public class BooleanAttribute extends BaseAttribute<Boolean> {

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public void readXML(Element element) throws InvalidXMLException {
        String value = element.attributeValue("value");
        if (value == null) throw new InvalidXMLException(element, "No value attribute was found.");

        setValue(Boolean.valueOf(value));
    }

    @Override
    public void writeXML(Element element) {
        element.addAttribute("value", getValue().toString());
    }

    @Override
    protected Boolean defaultValue() {
        return false;
    }
    
}
