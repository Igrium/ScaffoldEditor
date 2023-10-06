package com.igrium.scaffold.level.element;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.igrium.scaffold.level.Level;

import net.minecraft.util.Identifier;

/**
 * A prototype for a scaffold element.
 */
public class ElementType<T extends ScaffoldElement> {
    public static interface ElementFactory<T extends ScaffoldElement> {
        public T create(ElementType<?> type, Level level);
    }
    
    public static final ElementType<DemoElement> DEMO_ELEMENT = register(new Identifier("scaffold:demo"), new ElementType<>(DemoElement::new));

    /**
     * The registry of scaffold element types.
     */
    public static final BiMap<Identifier, ElementType<?>> REGISTRY = HashBiMap.create();

    public static <T extends ScaffoldElement> ElementType<T> register(Identifier id, ElementType<T> type) {
        REGISTRY.put(id, type);
        return type;
    }
    
    public static <T extends ScaffoldElement> ElementType<T> register(String id, ElementType<T> type) {
        return register(new Identifier(id), type);
    }

    /**
     * Get an element type.
     * @param id The element type's ID.
     * @return The element type by that ID.
     */
    public static Optional<ElementType<?>> get(Identifier id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    /**
     * Get the identifier of an element type.
     * @param type The element type.
     * @return The element type's ID, or <code>null</code> if it is not registered.
     */
    @Nullable
    public static Identifier getId(ElementType<?> type) {
        return REGISTRY.inverse().get(type);
    }

    /**
     * Get the identifier of this element type.
     * @return The element type's ID, or <code>null</code> if it is not registered.
     */
    @Nullable
    public Identifier getId() {
        return getId(this);
    }

    /**
     * Create an instance of an element.
     * @param id Element ID.
     * @param level Level to assign to.
     * @return The created element, or an empty optional if none by that ID exists.
     */
    public static Optional<ScaffoldElement> create(Identifier id, Level level) {
        Optional<ElementType<?>> type = get(id);
        if (type.isPresent()) {
            return Optional.of(type.get().create(level));
        } else {
            return Optional.empty();
        }
    }

    private ElementFactory<T> factory;

    public ElementType(ElementFactory<T> factory) {
        this.factory = factory;
    }

    /**
     * Create an instance of this element.
     * @param level Level to assign to.
     * @return The created element.
     */
    public T create(Level level) {
        return factory.create(this, level);
    }

    public ElementFactory<T> getFactory() {
        return factory;
    }
}
