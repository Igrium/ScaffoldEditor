package com.igrium.scaffold.level;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;

import net.minecraft.block.BlockState;

/**
 * A 16x16x16 collection of blocks.
 */
public class ScaffoldChunk {
    // Because Java stores by reference, we can directly store blockstates.
    private final BlockState[][][] blocks = new BlockState[16][16][16];

    /**
     * Get the block at a given position.
     * @param x Local X.
     * @param y Local Y.
     * @param z Local Z.
     * @return The block. Null if there is none.
     */
    @Nullable
    public BlockState blockAt(int x, int y, int z) throws IndexOutOfBoundsException {
        return blocks[x][y][z];
    }

    /**
     * Get the block at as given position.
     * @param pos Local position.
     * @return The block. Null if there is none.
     */
    public BlockState blockAt(Vector3ic pos) throws IndexOutOfBoundsException {
        return blockAt(pos.x(), pos.y(), pos.z());
    }

    /**
     * Set the block at a given position.
     * @param x Local X
     * @param y Local Y
     * @param z Local Z
     * @param block The block to set.
     * @return The old block.
     */
    public BlockState setBlock(int x, int y, int z, BlockState block) {
        BlockState old = blocks[x][y][z];
        blocks[x][y][z] = block;
        return old;
    }
}
