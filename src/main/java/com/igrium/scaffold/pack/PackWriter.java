package com.igrium.scaffold.pack;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import com.igrium.scaffold.pack.BaseResourcePack.ResourceType;

import net.minecraft.util.Identifier;

/**
 * Writes a datapack or resourcepack to disk.
 */
public abstract class PackWriter implements Closeable {

    /**
     * Open an output stream for a certian file within the pack.
     * @param file The file to write to, relative to the pack root.
     * @return The stream.
     * @throws IOException If an IO exception occurs.
     */
    public abstract OutputStream openStream(String file) throws IOException;
    
    /**
     * Get the filename within the pack that an identifier should use.
     * @param resourceType The asset type.
     * @param id The identifier.
     * @return The filename, relative to the pack root.
     */
    public String getFileName(ResourceType resourceType, Identifier id) {
        return resourceType.getFolderName() + "/" + id.getNamespace() + "/" + id.getPath(); 
    }

    /**
     * Open an output stream for the file corresponding to a given identifier.
     * @param resourceType The asset type.
     * @param id The identifier.
     * @return The stream.
     * @throws IOException If an IO exception occurs.
     */
    public OutputStream openStream(ResourceType resourceType, Identifier id) throws IOException {
        return openStream(getFileName(resourceType, id));
    }
}
