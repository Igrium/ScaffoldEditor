package com.igrium.scaffold;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import com.igrium.scaffold.engine.EditorChunkManager;
import com.igrium.scaffold.level.ScaffoldWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;

public class LevelEditor {
    private static LevelEditor instance;

    /**
     * Get the current editor instance.
     * @return The active editor, or <code>null</code> if it is not open.
     */
    @Nullable
    public static LevelEditor getInstance() {
        return instance;
    }

    public static final LevelInfo EDITOR_LEVEL_INFO = new LevelInfo("Demo World", GameMode.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(), DataConfiguration.SAFE_MODE);

    protected MinecraftClient client;
    protected ScaffoldWorld world;
    protected IntegratedServer server;

    private boolean isRunning;
    
    public final boolean isRunning() {
        return isRunning;
    }

    public ScaffoldWorld getWorld() {
        return world;
    }

    /**
     * Launch the editor. Must be called when Minecraft is NOT ingame.
     */
    public void launch(MinecraftClient client) {
        // TODO: update to work with multiplayer.
        this.client = client;

        if (instance != null)
            throw new IllegalStateException("Only one instance of the editor may be open at a time.");

        if (client.world != null) throw new IllegalStateException("Scaffold may not be launched when the client is ingame.");
        
        LogManager.getLogger().info("Launching Scaffold");
        instance = this;

        world = new ScaffoldWorld();
        EditorChunkManager.LAUNCHING_WORLD = Optional.of(world);
        client.createIntegratedServerLoader().createAndStart("scaffold.editor", EDITOR_LEVEL_INFO, GeneratorOptions.DEMO_OPTIONS, WorldPresets::createDemoOptions);
        isRunning = true;

        world.setUpdateExecutor(client.getServer());
        placeTestBlocks();
        // world.setBlock(0, 0, 0, Blocks.STONE.getDefaultState());
        // world.setBlock(0, 128, 0, Blocks.GRASS_BLOCK.getDefaultState());
    }

    public CompletableFuture<Void> placeTestBlocks() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int x = 0; x < 64; x++) {
            for (int z = 0; z < 64; z++) {
                futures.add(placeBlockAsync(x, 64, z, Blocks.OAK_PLANKS.getDefaultState()));
            }
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<Void> placeBlockAsync(int x, int y, int z, BlockState block) {
        return CompletableFuture.runAsync(() -> {
            world.setBlock(x, 7, z, Blocks.OAK_PLANKS.getDefaultState());
        }, Util.getMainWorkerExecutor());
    }

    public void onShutdown() {
        EditorChunkManager.LAUNCHING_WORLD = Optional.empty();
        instance = null;
        isRunning = false;
    }
}
