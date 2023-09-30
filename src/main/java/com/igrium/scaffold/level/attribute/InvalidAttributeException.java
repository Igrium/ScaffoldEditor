package com.igrium.scaffold.level.attribute;

import org.dom4j.Element;

/**
 * Thrown when an attribute trys to parse an XML element that doesn't have the
 * proper data.
 */
public class InvalidAttributeException extends Exception {
    private final Element element;

    /**
     * The element that caused the issue.
     */
    public Element getElement() {
        return element;
    }

    public InvalidAttributeException(Element element) {
        this.element = element;
    }

    public InvalidAttributeException(Element element, String message) {
        super(message);
        this.element = element;
    }

    public InvalidAttributeException(Element element, Throwable cause) {
        super(cause);
        this.element = element;
    }

    public InvalidAttributeException(Element element, String message, Throwable cause) {
        super(message, cause);
        this.element = element;
    }
}
