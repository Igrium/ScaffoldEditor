package com.igrium.scaffold.server;

import java.io.IOException;
import java.net.Proxy;

import org.slf4j.Logger;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;

import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ApiServices;
import net.minecraft.util.SystemDetails;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class ScaffoldNativeServer extends MinecraftServer {

    private static final Logger LOGGER = LogUtils.getLogger();

    public ScaffoldNativeServer(Thread serverThread, Session session, ResourcePackManager dataPackManager,
            SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices,
            WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(serverThread, session, dataPackManager, saveLoader, proxy, dataFixer, apiServices,
                worldGenerationProgressListenerFactory);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected boolean setupServer() throws IOException {
        this.setPlayerManager(new PlayerManager(this, this.getCombinedDynamicRegistries(), this.saveHandler, 1){});
        this.loadWorld();
        ServerWorld serverWorld = this.getOverworld();
        serverWorld.setWeather(20000000, 20000000, false, false);
        LOGGER.info("Started editor native server");
        return true;
    }

    @Override
    public int getOpPermissionLevel() {
        return 0;
    }

    @Override
    public int getFunctionPermissionLevel() {
        return 4;
    }

    @Override
    public boolean shouldBroadcastRconToOps() {
        return false;
    }

    @Override
    public SystemDetails addExtraSystemDetails(SystemDetails details) {
        details.addSection("Type", "Editor Server");
        return details;
    }

    @Override
    public boolean isDedicated() {
        return false;
    }

    @Override
    public int getRateLimit() {
        return 0;
    }

    @Override
    public boolean isUsingNativeTransport() {
        return false;
    }

    @Override
    public boolean areCommandBlocksEnabled() {
        return true;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }

    @Override
    public boolean isHost(GameProfile var1) {
       return false;
    }
    
}
