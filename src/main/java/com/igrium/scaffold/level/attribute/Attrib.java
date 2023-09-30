package com.igrium.scaffold.level.attribute;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field for processing by the attribute system.
 * @see AttributeHolder
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Attrib {
    /**
     * The name this attribute will appear under in the attribute UI.
     */
    String displayName() default "";
}
