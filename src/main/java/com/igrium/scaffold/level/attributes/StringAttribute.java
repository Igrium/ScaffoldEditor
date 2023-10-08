package com.igrium.scaffold.level.attributes;

import org.dom4j.Element;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.util.InvalidXMLException;

public class StringAttribute extends BaseAttribute<String> {

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public void readXML(Element element) throws InvalidXMLException {
        String val = element.attributeValue("value");
        if (val == null) throw new InvalidXMLException(element, "No value attribute was found.");
        setValue(val);
    }

    @Override
    public void writeXML(Element element) {
        element.addAttribute("value", getValue());
    }

    @Override
    protected String defaultValue() {
        return "";
    }
    
}
