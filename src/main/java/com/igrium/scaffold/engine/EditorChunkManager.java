package com.igrium.scaffold.engine;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;

import com.igrium.scaffold.events.ScaffoldWorldEvents;
import com.igrium.scaffold.events.ScaffoldWorldEvents.WorldModified;
import com.igrium.scaffold.world.ScaffoldWorld;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;

import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class EditorChunkManager extends ServerChunkManager implements WorldModified {

    /**
     * Tells the engine if we're currently in the process of making an editor world. If so, the overworld dimension will be spawned as an editor world.
     */
    public static Optional<ScaffoldWorld> LAUNCHING_WORLD = Optional.empty();

    /**
     * A slight wrapper around WorldModified that allows this object to be garbage collected as needed.
     */
    protected static final class WeakUpdateListener implements WorldModified {
        
        private final WeakReference<WorldModified> listener;

        public WeakUpdateListener(WorldModified listener) {
            this.listener = new WeakReference<WorldModified>(listener);
        }

        @Override
        public void onWorldModified(ScaffoldWorld world, Vector3ic pos, @Nullable BlockState oldState,
                @Nullable BlockState newState) {
            WorldModified listener = this.listener.get();
            if (listener != null) listener.onWorldModified(world, pos, oldState, newState);
        }

    }

    protected Map<ChunkPos, WorldChunk> editorChunks = new HashMap<>();
    private final ScaffoldWorld scaffoldWorld;
    private EditorWorldNetworking networking;

    public EditorChunkManager(ServerWorld world, Session session, DataFixer dataFixer,
            StructureTemplateManager structureTemplateManager, Executor workerExecutor, ChunkGenerator chunkGenerator,
            int viewDistance, int simulationDistance, boolean dsync,
            WorldGenerationProgressListener worldGenerationProgressListener,
            ChunkStatusChangeListener chunkStatusChangeListener,
            Supplier<PersistentStateManager> persistentStateManagerFactory,
            ScaffoldWorld scaffoldWorld) {
        super(world, session, dataFixer, structureTemplateManager, workerExecutor, chunkGenerator, viewDistance,
                simulationDistance, dsync, worldGenerationProgressListener, chunkStatusChangeListener,
                persistentStateManagerFactory);
        this.scaffoldWorld = scaffoldWorld;
        this.networking = new EditorWorldNetworking(world);

        registerListeners();
    }

    public ScaffoldWorld getScaffoldWorld() {
        return scaffoldWorld;
    }

    private void registerListeners() {
        ScaffoldWorldEvents.WORLD_MODIFIED.register(new WeakUpdateListener(this));
    }
    
    @Override
    @Nullable
    public WorldChunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        ChunkPos pos = new ChunkPos(x, z);
        WorldChunk chunk = editorChunks.get(pos);
        if (chunk == null) {
            chunk = new EditorChunk(getWorld(), pos, scaffoldWorld, getWorld().getRegistryManager().get(RegistryKeys.BIOME).entryOf(BiomeKeys.PLAINS));
            // chunk = new EmptyChunk(getWorld(), pos, getWorld().getRegistryManager().get(RegistryKeys.BIOME).entryOf(BiomeKeys.PLAINS));
            editorChunks.put(pos, chunk);
        }
        return chunk;
    }
    

    @Override
    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFutureSyncOnMainThread(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        return CompletableFuture.completedFuture(Either.left(getChunk(chunkX, chunkZ, leastStatus, create)));
    }
    
    @Override
    @Nullable
    public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
        return getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
    }

    @Override
    public void onWorldModified(ScaffoldWorld world, Vector3ic pos, @Nullable BlockState oldState,
            @Nullable BlockState newState) {
        if (!world.equals(scaffoldWorld)) return;
        if (oldState.equals(newState)) return;

        networking.markForBlockUpdate(new BlockPos(pos.x(), pos.y(), pos.z()));
    }
    
    @Override
    public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {
        if (tickChunks)
            networking.syncChunks();
    }
    
    // Editor chunks are always "loaded"
    @Override
    public boolean isChunkLoaded(int x, int z) {
        return true;
    }

    // Editor chunks are not saved.
    public void save(boolean flush) {
        return;
    }

    @Override
    public String getDebugString() {
        return "Editor world loaded.";
    }

    // No block updates
    public void markForUpdate(BlockPos pos) {
        return;
    }
}
