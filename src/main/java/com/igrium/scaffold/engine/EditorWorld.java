package com.igrium.scaffold.engine;

import java.util.List;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

import com.igrium.scaffold.world.ScaffoldWorld;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage.Session;
import net.minecraft.world.spawner.Spawner;

/**
 * A world that cannot be modified by players and does not tick. It is
 * controlled exclusively by code.
 */
public class EditorWorld extends ServerWorld {

    private ScaffoldWorld scaffoldWorld;

    public EditorWorld(MinecraftServer server, Executor workerExecutor, Session session,
            ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions,
            WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed,
            List<Spawner> spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, ScaffoldWorld scaffoldWorld) {

        super(server, workerExecutor, session, properties, worldKey, dimensionOptions, worldGenerationProgressListener,
                debugWorld, seed, spawners, shouldTickTime, randomSequencesState);
        this.scaffoldWorld = scaffoldWorld;
    }

    public ScaffoldWorld getScaffoldWorld() {
        return scaffoldWorld;
    }
    
    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        scaffoldWorld.setBlock(pos.getX(), pos.getY(), pos.getZ(), state);
        return true;
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        scaffoldWorld.setBlock(pos.getX(), pos.getY(), pos.getZ(), null);
        return true;
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        return removeBlock(pos, false);
    }

}
