package com.igrium.scaffold.level.attributes;

import org.dom4j.Element;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.util.InvalidXMLException;

public class TextAttribute extends BaseAttribute<String> {

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public void readXML(Element element) throws InvalidXMLException {
        setValue(element.getText());
    }

    @Override
    public void writeXML(Element element) {
        element.setText(getValue());
    }

    @Override
    protected String defaultValue() {
        return "";
    }
    
}
