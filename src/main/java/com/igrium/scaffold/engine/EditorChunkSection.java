package com.igrium.scaffold.engine;

import com.igrium.scaffold.level.ScaffoldWorld;

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
    }

    // This is all we really need for networking. Everything else is handled by the chunk.
    public void toPacket(PacketByteBuf buf) {
        world.getChunkOrCreate(pos.getX(), pos.getY(), pos.getZ()).toPacket(buf);
    }
}
