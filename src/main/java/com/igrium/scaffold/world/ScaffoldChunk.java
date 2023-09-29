package com.igrium.scaffold.world;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.chunk.PalettedContainer;

/**
 * A 16x16x16 collection of blocks.
 */
public class ScaffoldChunk {
    private final PalettedContainer<BlockState> blockStateContainer;

    public PalettedContainer<BlockState> getBlockStateContainer() {
        return blockStateContainer;
    }

    private short nonEmptyBlockCount;

    public short getNonEmptyBlockCount() {
        return nonEmptyBlockCount;
    }

    public ScaffoldChunk() {
        this.blockStateContainer = new PalettedContainer<BlockState>(Block.STATE_IDS, Blocks.AIR.getDefaultState(),
                PalettedContainer.PaletteProvider.BLOCK_STATE);
    }

    public BlockState getBlockState(int x, int y, int z) {
        return this.blockStateContainer.get(x, y, z);
    }

    public FluidState getFluidState(int x, int y, int z) {
        return this.blockStateContainer.get(x, y, z).getFluidState();
    }

    public void lock() {
        this.blockStateContainer.lock();
    }

    public void unlock() {
        this.blockStateContainer.unlock();
    }

    public BlockState setBlockState(int x, int y, int z, BlockState state) {
        return this.setBlockState(x, y, z, state, true);
    }

    public BlockState setBlockState(int x, int y, int z, BlockState state, boolean lock) {
        if (state == null) {
            state = Blocks.AIR.getDefaultState();
        }

        BlockState oldState;
        synchronized (this) {
            oldState = lock ? this.blockStateContainer.swap(x, y, z, state)
                    : this.blockStateContainer.swapUnsafe(x, y, z, state);
        }
        
        if (state.getBlock() != Blocks.AIR && oldState.getBlock() == Blocks.AIR) {
            nonEmptyBlockCount++;
        } else if (state.getBlock() == Blocks.AIR && oldState.getBlock() != Blocks.AIR) {
            nonEmptyBlockCount--;
        }

        return oldState;
    }

    public int getPacketSize() {
        return 2 + this.blockStateContainer.getPacketSize();
    }

    public boolean hasAny(Predicate<BlockState> predicate) {
        return this.blockStateContainer.hasAny(predicate);
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeShort(nonEmptyBlockCount);
        this.blockStateContainer.writePacket(buf);
    }
}
