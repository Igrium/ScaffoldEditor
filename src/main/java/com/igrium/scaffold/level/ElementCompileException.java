package com.igrium.scaffold.level;

import com.igrium.scaffold.level.element.ScaffoldElement;

/**
 * Called when a Scaffold element fails to compile.
 */
public class ElementCompileException extends RuntimeException {
    private final ScaffoldElement element;

    /**
     * The element that caused the exception.
     */
    public ScaffoldElement getElement() {
        return element;
    }

    public ElementCompileException(ScaffoldElement element, Throwable cause) {
        super(createMessage(element), cause);
        this.element = element;
    }

    public ElementCompileException(ScaffoldElement element, String message, Throwable cause) {
        super(message, cause);
        this.element = element;
    }

    public ElementCompileException(ScaffoldElement element, String message) {
        super(message);
        this.element = element;
    }

    public ElementCompileException(ScaffoldElement element) {
        super(createMessage(element));
        this.element = element;
    }

    private static String createMessage(ScaffoldElement element) {
        return "Exception compiling element " + element.getId();
    }
}
