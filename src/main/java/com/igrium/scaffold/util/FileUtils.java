package com.igrium.scaffold.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

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
}
