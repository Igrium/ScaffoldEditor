package com.igrium.scaffold.level.attribute;

public class AttributeInitializationException extends RuntimeException {

    private final String attributeName;

    public String getAttributeName() {
        return attributeName;
    }

    public AttributeInitializationException(String attributeName) {
        super("Unable to initialize attribute: " + attributeName);
        this.attributeName = attributeName;
    }

    public AttributeInitializationException(String attributeName, String message) {
        super(message);
        this.attributeName = attributeName;
    }

    public AttributeInitializationException(String attributeName, String message, Throwable cause) {
        super(message, cause);
        this.attributeName = attributeName;
    }

    public AttributeInitializationException(String attributeName, Throwable cause) {
        super("Unable to initialize attribute: " + attributeName, cause);
        this.attributeName = attributeName;
    }
}
