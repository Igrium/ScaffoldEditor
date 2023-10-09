package com.igrium.scaffold.level.attributes;

import java.util.List;
import java.util.function.Supplier;

import org.dom4j.Element;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.util.InvalidXMLException;
import com.igrium.scaffold.util.XMLSerializable;

/**
 * A generic attribute that can take any XMLSerializable object.
 */
public class GenericAttribute<T extends XMLSerializable> extends BaseAttribute<T> {

    private Class<T> type;
    private Supplier<T> constructor;

    public GenericAttribute(Class<T> type, Supplier<T> constructor) {
        this(type, constructor, constructor.get());
    }

    public GenericAttribute(Class<T> type, Supplier<T> constructor, T initialValue) {
        super(initialValue);
        this.type = type;
        this.constructor = constructor;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public void readXML(Element element) throws InvalidXMLException {
        List<Element> elements = element.elements();
        if (elements.isEmpty()) {
            throw new InvalidXMLException(element, "Element is empty.");
        }
        T newValue = constructor.get();
        newValue.readXML(elements.iterator().next());
        setValue(newValue);
    }

    @Override
    public void writeXML(Element element) {
        Element child = element.addElement("attribute_data");
        getValue().writeXML(child);
    }
    
}
