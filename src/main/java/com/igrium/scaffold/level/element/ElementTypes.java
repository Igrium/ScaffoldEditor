package com.igrium.scaffold.level.element;

import net.minecraft.util.Identifier;

public class ElementTypes {
        public static final ElementType<DemoElement> DEMO_ELEMENT = ElementType.register(new Identifier("scaffold:demo"), new ElementType<>(DemoElement::new));

}
