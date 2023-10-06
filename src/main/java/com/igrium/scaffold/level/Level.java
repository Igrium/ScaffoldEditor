package com.igrium.scaffold.level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.igrium.scaffold.level.item.ScaffoldItem;
import com.igrium.scaffold.level.stack.StackGroup;

/**
 * A level within a scaffold project. Each level has a set of "objects" which compile into the world.
 */
public class Level {

    private final StackGroup levelStack = new StackGroup();

    /**
     * A cache of entity ids.
     */
    private Map<String, ScaffoldItem> idCache = new HashMap<>();

    /**
     * Get the level stack.
     * @return The root stack group.
     */
    public StackGroup getLevelStack() {
        return levelStack;
    }

    /**
     * Find an item by its ID. Note: the returned item may not still be in the
     * level.
     * 
     * @param id The ID to look for.
     * @return The item, or an empty optional if it was not found.
     */
    public Optional<ScaffoldItem> getItem(String id) {
        ScaffoldItem item = idCache.get(id);
        if (item != null && item.getLevel() == this) return Optional.of(item);

        for (ScaffoldItem levelItem : levelStack) {
            if (levelItem.getId().equals(id)) {
                idCache.put(id, levelItem);
                return Optional.of(levelItem);
            }
        }
        return Optional.empty();
    }

    /**
     * Rebuild the item ID cache.
     */
    public void refreshCache() {
        idCache.clear();
        for (ScaffoldItem item : levelStack) {
            idCache.put(item.getId(), item);
        }
    }

    public void removeItem(ScaffoldItem item) {
        if (item.getLevel() != this) {
            throw new IllegalArgumentException("The supplied item is not part of this level.");
        }
        item.getStackItem().detach();
        
    }
}
