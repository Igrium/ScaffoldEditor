package com.igrium.scaffold;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igrium.scaffold.compile.CompileRegistrationCallback;
import com.igrium.scaffold.compile.steps.ScaffoldCompileSteps;
import com.igrium.scaffold.test.ScaffoldPlaceCommand;
import com.igrium.scaffold.test.TestProjectCommand;

public class ScaffoldEditorMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("scaffold-editor");

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(ScaffoldPlaceCommand::register);
        CommandRegistrationCallback.EVENT.register(TestProjectCommand::register);

        CompileRegistrationCallback.EVENT.register(Event.DEFAULT_PHASE, ScaffoldCompileSteps::register);
        CompileRegistrationCallback.EVENT.register(CompileRegistrationCallback.POST_COMPILE_PHASE,
                ScaffoldCompileSteps::registerPost);
    }

}