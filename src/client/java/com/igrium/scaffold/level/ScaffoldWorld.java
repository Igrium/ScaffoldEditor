package com.igrium.scaffold.level;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

/**
 * The world that Scaffold uses when compiling levels. Note: this is the
 * collection of everything that is sent to the rendering engine; there is no
 * chunk loading system. If there are unloaded chunks, they are not present
 * here.
 */
public class ScaffoldWorld {
    @NotNull
    protected Map<Vector3i, ScaffoldChunk> chunks = new HashMap<>();

    // public ScaffoldChunk chunkAt(int x, int y, int z) {
    //     chunks.get();
    // }
}
