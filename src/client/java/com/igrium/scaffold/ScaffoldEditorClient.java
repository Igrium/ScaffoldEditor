package com.igrium.scaffold;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ScaffoldEditorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (LevelEditor.getInstance() != null) {
                LevelEditor.getInstance().onShutdown();
            }
        });
    }
}