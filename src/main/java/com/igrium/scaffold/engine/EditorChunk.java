package com.igrium.scaffold.engine;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import com.igrium.scaffold.level.ScaffoldWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.ChunkLightProvider;

/**
 * A boilerplate wrapper around a Scaffold world to abstract it for the Minecraft engine.
 */
public class EditorChunk extends WorldChunk {

    private final RegistryEntry<Biome> biomeEntry;
    private ScaffoldWorld scaffoldWorld;

    public EditorChunk(World world, ChunkPos pos, ScaffoldWorld scaffoldWorld, RegistryEntry<Biome> biomeEntry) {
        super(world, pos);
        this.scaffoldWorld = scaffoldWorld;
        this.biomeEntry = biomeEntry;
    }
    
    public ScaffoldWorld getScaffoldWorld() {
        return scaffoldWorld;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        // getBlockState takes global coordinates (for some reason), so we can query the world directly.
        BlockState state = scaffoldWorld.blockAt(pos.getX(), pos.getY(), pos.getZ());
        return state == null ? Blocks.AIR.getDefaultState() : state;
    }

    @Override
    public FluidState getFluidState(int x, int y, int z) {
        BlockState blockState = scaffoldWorld.blockAt(x, y, z);
        if (blockState == null) {
            return Fluids.EMPTY.getDefaultState();
        }
        return blockState.getFluidState();
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos blockPos, BlockState state, boolean moved) {
        Vector3i pos = new Vector3i(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (scaffoldWorld.blockAt(pos) == null && (state == null || state.isAir())) return null;

        BlockState oldState = scaffoldWorld.setBlock(pos, state);
        if (state.equals(oldState)) return null;

        // We don't need heightmaps because there's no gameplay.

        this.getWorld().getLightingProvider().setSectionStatus(blockPos, true);
        if (ChunkLightProvider.needsLightUpdate(this, blockPos, state, state)) {
            Profiler profiler = this.getWorld().getProfiler();
            profiler.push("updateSkyLightSources");
            this.chunkSkyLight.isSkyLightAccessible(this, blockPos.getX() & 0xF, blockPos.getY(), blockPos.getZ() & 0xF);
            profiler.swap("queueCheckLight");
            this.getWorld().getChunkManager().getLightingProvider().checkBlock(blockPos);
            profiler.pop();
        }

        return state;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType) {
        return null;
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
    }

    @Override
    public void setBlockEntity(BlockEntity blockEntity) {
    }

    @Override
    public void removeBlockEntity(BlockPos pos) {
    }
    
    @Override
    public ChunkLevelType getLevelType() {
        return ChunkLevelType.FULL;
    }
    
    @Override
    public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeEntry;
    }
}
