package com.igrium.scaffold;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igrium.scaffold.test.ScaffoldPlaceCommand;

public class ScaffoldEditorMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("scaffold-editor");

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(ScaffoldPlaceCommand::register);

        ServerLifecycleEvents.SERVER_STOPPED.register(event -> {
            if (LevelEditor.getInstance() != null) {
                LevelEditor.getInstance().onShutdown();
            }
        });
    }

}