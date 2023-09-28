package com.igrium.scaffold.level;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.ChunkSectionPos;

/**
 * The world that Scaffold uses when compiling levels. Note: this is the
 * collection of everything that is sent to the rendering engine; there is no
 * chunk loading system. If there are unloaded chunks, they are not present
 * here.
 */
public class ScaffoldWorld {
    @NotNull
    protected Map<Vector3i, ScaffoldChunk> chunks = new HashMap<>();

    @Nullable
    public ScaffoldChunk getChunk(int x, int y, int z) {
        return chunks.get(new Vector3i(x, y, z));
    }

    @Nullable
    public final ScaffoldChunk getChunk(Vector3ic pos) {
        return getChunk(pos.x(), pos.y(), pos.z());
    }
    
    public ScaffoldChunk getChunkOrCreate(int x, int y, int z) {
        Vector3i vec = new Vector3i(x, y, z);
        ScaffoldChunk chunk = chunks.get(vec);
        if (chunk == null) {
            chunk = new ScaffoldChunk();
            chunks.put(vec, chunk);
        }
        return chunk;
    }

    public Map<Vector3ic, ScaffoldChunk> chunks() {
        return Collections.unmodifiableMap(chunks);
    }

    @Nullable
    public BlockState blockAt(int x, int y, int z) {
        int chunkX = ChunkSectionPos.getSectionCoord(x);
        int chunkY = ChunkSectionPos.getSectionCoord(y);
        int chunkZ = ChunkSectionPos.getSectionCoord(z);

        ScaffoldChunk chunk = getChunk(chunkX, chunkY, chunkZ);
        if (chunk == null) return null;

        int localX = x & 0xF;
        int localY = y & 0xF;
        int localZ = z & 0xF;

        return chunk.getBlockState(localX, localY, localZ);
    }

    @Nullable
    public final BlockState blockAt(Vector3ic pos) {
        return blockAt(pos.x(), pos.y(), pos.z());
    }

    @Nullable
    public BlockState setBlock(int x, int y, int z, BlockState block) {
        int chunkX = ChunkSectionPos.getSectionCoord(x);
        int chunkY = ChunkSectionPos.getSectionCoord(y);
        int chunkZ = ChunkSectionPos.getSectionCoord(z);

        Vector3i chunkPos = new Vector3i(chunkX, chunkY, chunkZ);

        ScaffoldChunk chunk = chunks.get(chunkPos);
        if (chunk == null) {
            if (block == null) return null;
            chunk = new ScaffoldChunk();
            chunks.put(chunkPos, chunk);
        }

        int localX = x & 0xF;
        int localY = y & 0xF;
        int localZ = z & 0xF;

        return chunk.setBlockState(localX, localY, localZ, block);
    }
    
    @Nullable
    public final BlockState setBlock(Vector3ic pos, BlockState block) {
        return setBlock(pos.x(), pos.y(), pos.z(), block);
    }
}
