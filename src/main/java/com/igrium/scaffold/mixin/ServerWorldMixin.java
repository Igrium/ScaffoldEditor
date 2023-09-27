package com.igrium.scaffold.mixin;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.scaffold.engine.EditorChunkManager;
import com.igrium.scaffold.engine.IServerWorldMixin;
import com.igrium.scaffold.level.ScaffoldWorld;

import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements IServerWorldMixin {

    private Optional<ScaffoldWorld> scaffoldWorld = Optional.empty();

    @Final
    @Shadow
    @Mutable
    private ServerChunkManager chunkManager;

    @Shadow
    private ServerEntityManager<Entity> entityManager;

    public Optional<ScaffoldWorld> getScaffoldWorld() {
        return scaffoldWorld;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterInit(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session,
            ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions,
            WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed,
            List<Spawner> spawners, boolean shouldTickTime, @Nullable RandomSequencesState randomSequencesState,
            CallbackInfo ci) {
        
        ScaffoldWorld scaffoldWorld = EditorChunkManager.LAUNCHING_WORLD.orElse(null);
        LogManager.getLogger().info("Called ServerWorld Mixin. Scaffold World: {}, registry key: ", scaffoldWorld, ((World)(Object) this).getRegistryKey());
        if (scaffoldWorld != null) {
            this.scaffoldWorld = Optional.of(scaffoldWorld);
            LogManager.getLogger().info("Loading editor world");
            chunkManager = new EditorChunkManager((ServerWorld) (Object) this, session, server.getDataFixer(),
                    server.getStructureTemplateManager(), workerExecutor, dimensionOptions.chunkGenerator(),
                    server.getPlayerManager().getViewDistance(), server.getPlayerManager().getSimulationDistance(),
                    server.syncChunkWrites(), worldGenerationProgressListener, entityManager::updateTrackingStatus, () -> server.getOverworld().getPersistentStateManager(), scaffoldWorld);
        }
    }
}
