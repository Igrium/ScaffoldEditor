package com.igrium.scaffold.util;

import org.dom4j.Element;

/**
 * An object that can be serialized to XML
 */
public interface XMLSerializable {
    /**
     * Read an XML element and apply its state to this object. Should be compatible
     * with {@link #writeXML(Element)}.
     * 
     * @param element XML element to read.
     * @throws InvalidXMLException If the passed XML cannot be parsed (contains the wrong data, etc).
     */
    public void readXML(Element element) throws InvalidXMLException;

    /**
     * Serialize this object's state into an XML element. Should be compatible with
     * {@link #readXML(Element)}.
     * 
     * @param element XML element to write into.
     */
    public void writeXML(Element element);
}
