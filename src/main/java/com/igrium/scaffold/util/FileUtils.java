package com.igrium.scaffold.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.mojang.logging.LogUtils;

public final class FileUtils {
    private FileUtils() {};

    public static void deleteDirectory(Path directory) throws IOException {
        try (Stream<Path> walk = Files.walk(directory)) {
            walk.sorted(Comparator.reverseOrder())
            .forEach(t -> {
                try {
                    Files.deleteIfExists(t);
                } catch (IOException e) {
                    LogUtils.getLogger().error("Error deleting file.", e);
                }
            });
        }
    }

    public static void extractZip(InputStream in, Path targetDir) throws IOException {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(in));
       
        ZipEntry currentEntry;
        while ((currentEntry = zis.getNextEntry()) != null) {
            Path extractedFile = targetDir.resolve(currentEntry.getName());
            Path parent = extractedFile.getParent();

            if (Files.isRegularFile(parent))
                Files.delete(parent);
            Files.createDirectories(parent);

            try(OutputStream out = new BufferedOutputStream(Files.newOutputStream(extractedFile))) {
                zis.transferTo(out);
            }
        }
    }
}
