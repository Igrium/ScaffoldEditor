package com.igrium.scaffold.asset;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handles the loading of files.
 */
public interface AssetLoader<T> {

    /**
     * Load an asset.
     * @param in Input stream to load from.
     * @param path Asset path of the file being loaded.
     * @return The loaded file, parsed however appropriate.
     */
    public T load(InputStream in, String path) throws IOException;

    /**
     * Get the object type that this loader will return.
     * @return Object type.
     */
    public T getType();
}
