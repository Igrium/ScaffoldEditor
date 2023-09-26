package com.igrium.scaffold.level;

import org.joml.Vector3ic;

import net.minecraft.block.BlockState;

/**
 * A 16x16x16 collection of blocks.
 */
public class ScaffoldChunk {
    // Because Java stores by reference, we can directly store blockstates.
    private final BlockState[][][] blocks = new BlockState[16][16][16];

    public BlockState blockAt(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public BlockState blockAt(Vector3ic pos) {
        return blockAt(pos.x(), pos.y(), pos.z());
    }
}
