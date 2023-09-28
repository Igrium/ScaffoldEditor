package com.igrium.scaffold.server;

import net.minecraft.server.MinecraftServer;

/**
 * A re-implementation of Minecraft's server for the level editor.
 */
public class ScaffoldServer {
    private final MinecraftServer wrappedServer;

    /**
     * Some functions which don't need to be fully re-implemented are passed through to here.
     */
    public MinecraftServer getWrappedServer() {
        return wrappedServer;
    }

    public ScaffoldServer(MinecraftServer wrapped) {
        this.wrappedServer = wrapped;
    }
}
