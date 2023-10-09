package com.igrium.scaffold.level.attributes;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.dom4j.Element;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.util.InvalidXMLException;

public class PathAttribute extends BaseAttribute<Path> {

    public PathAttribute(Path defaultValue) {
        super(defaultValue);
    }

    @Override
    public Class<Path> getType() {
        return Path.class;
    }

    @Override
    public void readXML(Element element) throws InvalidXMLException {
        String val = element.attributeValue("value");
        if (val == null || val.isEmpty()) throw new InvalidXMLException(element, "No value attribute found.");
        
        try {
            setValue(Paths.get(val));
        } catch (InvalidPathException e) {
            throw new InvalidXMLException(element, "Invalid path.", e);
        }
    }

    @Override
    public void writeXML(Element element) {
        element.addAttribute("value", getValue().toString());
    }
    
}
