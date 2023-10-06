package com.igrium.scaffold.level.item;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.igrium.scaffold.level.Level;

import net.minecraft.util.Identifier;

/**
 * A prototype for a scaffold item.
 */
public class ItemType<T extends ScaffoldItem> {
    public static interface ItemFactory<T extends ScaffoldItem> {
        public T create(ItemType<?> type, Level level);
    }
    
    public static final ItemType<DemoItem> DEMO_ITEM = register(new Identifier("scaffold:demo"), new ItemType<>(DemoItem::new));

    /**
     * The registry of scaffold item types.
     */
    public static final BiMap<Identifier, ItemType<?>> REGISTRY = HashBiMap.create();

    public static <T extends ScaffoldItem> ItemType<T> register(Identifier id, ItemType<T> type) {
        REGISTRY.put(id, type);
        return type;
    }
    
    public static <T extends ScaffoldItem> ItemType<T> register(String id, ItemType<T> type) {
        return register(new Identifier(id), type);
    }

    /**
     * Get an item type.
     * @param id The item type's ID.
     * @return The item type by that ID.
     */
    public static Optional<ItemType<?>> get(Identifier id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    /**
     * Get the identifier of an item type.
     * @param type The item type.
     * @return The item type's ID, or <code>null</code> if it is not registered.
     */
    @Nullable
    public static Identifier getId(ItemType<?> type) {
        return REGISTRY.inverse().get(type);
    }

    /**
     * Get the identifier of this item type.
     * @return The item type's ID, or <code>null</code> if it is not registered.
     */
    @Nullable
    public Identifier getId() {
        return getId(this);
    }

    /**
     * Create an instance of an item.
     * @param id Item ID.
     * @param level Level to assign to.
     * @return The created item, or an empty optional if none by that ID exists.
     */
    public static Optional<ScaffoldItem> create(Identifier id, Level level) {
        Optional<ItemType<?>> type = get(id);
        if (type.isPresent()) {
            return Optional.of(type.get().create(level));
        } else {
            return Optional.empty();
        }
    }

    private ItemFactory<T> factory;

    public ItemType(ItemFactory<T> factory) {
        this.factory = factory;
    }

    /**
     * Create an instance of this item.
     * @param level Level to assign to.
     * @return The created item.
     */
    public T create(Level level) {
        return factory.create(this, level);
    }

    public ItemFactory<T> getFactory() {
        return factory;
    }
}
