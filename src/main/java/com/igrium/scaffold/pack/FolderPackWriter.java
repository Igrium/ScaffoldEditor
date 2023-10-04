package com.igrium.scaffold.pack;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.igrium.scaffold.util.FileUtils;

/**
 * Writes a pack to lose files on disk.
 */
public class FolderPackWriter extends PackWriter {

    private final Path folder;

    protected Set<OutputStream> streams = Collections.newSetFromMap(new WeakHashMap<>());

    public FolderPackWriter(Path folder) throws IOException {
        this.folder = folder;

        if (Files.isDirectory(folder)) {
            FileUtils.deleteDirectory(folder);
        }
    }

    public Path getFolder() {
        return folder;
    }

    @Override
    public OutputStream openStream(String file) throws IOException {
        Path globalFile = folder.resolve(file);
        Files.createDirectories(globalFile.getParent());
        return new BufferedOutputStream(Files.newOutputStream(globalFile));
    }

    
    @Override
    public void close() throws IOException {
        for (OutputStream stream : streams) {
            stream.close();
        }
    }
    
}
