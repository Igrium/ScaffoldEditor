package com.igrium.scaffold.util.collections;

/**
 * A collection that can be locked and unlocked. When the collection is
 * unlocked, it is modifiable. When it is locked, it will throw
 * {@link LockedCollectionException} when modified.
 */
public interface Lockable {

    /**
     * Thrown if you attempt to modify a collection that is locked.
     */
    public static class LockedCollectionException extends UnsupportedOperationException {
        public LockedCollectionException() {
            super("This list is locked. No modifications are allowed.");
        }

        public LockedCollectionException(String message) {
            super(message);
        }
    }

    /**
     * Set the lock state on this collection.
     * @param locked The lock state.
     */
    public void setLocked(boolean locked);

    /**
     * Lock this collection.
     */
    public default void lock() {
        setLocked(true);
    }

    /**
     * Unlock this collection.
     */
    public default void unlock() {
        setLocked(false);
    }
    
    /**
     * Get the lock state on this collection.
     * @return If the collection is locked.
     */
    public boolean isLocked();
}
