package com.igrium.scaffold.engine;

import com.igrium.scaffold.world.ScaffoldChunk;
import com.igrium.scaffold.world.ScaffoldWorld;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;

/**
 * More bullshit forcing Minecraft to read the Scaffold world as its own.
 * This time, it's simply a wrapper around the scaffold world to translate
 * minecraft "sections" into scaffold sections.
 */
public class EditorChunkSection extends ChunkSection {

    private ChunkSectionPos pos;
    private ScaffoldChunk scaffoldChunk;
    
    public ChunkSectionPos getPos() {
        return pos;
    }

    private ScaffoldWorld world;

    public ScaffoldWorld getWorld() {
        return world;
    }

    public EditorChunkSection(Registry<Biome> biomeRegistry, ChunkSectionPos pos, ScaffoldWorld world) {
        super(biomeRegistry);
        this.pos = pos;
        this.world = world;
        scaffoldChunk = world.getChunkOrCreate(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return scaffoldChunk.getBlockState(x, y, z);
    }

    @Override
    public FluidState getFluidState(int x, int y, int z) {
        return scaffoldChunk.getFluidState(x, y, z);
    }

    public int getPacketSize() {
        return scaffoldChunk.getPacketSize() + this.getBiomeContainer().getPacketSize();
    }

    // This is all we really need for networking. Everything else is handled by the chunk.
    public void toPacket(PacketByteBuf buf) {
        scaffoldChunk.toPacket(buf);
        this.getBiomeContainer().writePacket(buf);
    }
}
