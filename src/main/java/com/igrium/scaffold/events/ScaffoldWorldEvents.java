package com.igrium.scaffold.events;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;

import com.igrium.scaffold.world.ScaffoldWorld;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;

public final class ScaffoldWorldEvents {

    private ScaffoldWorldEvents() {};

    public static Event<WorldModified> WORLD_MODIFIED = EventFactory.createArrayBacked(WorldModified.class,
            (listeners) -> (world, pos, oldState, newState) -> {
                for (WorldModified listener : listeners) {
                    listener.onWorldModified(world, pos, oldState, newState);
                }
            });

    @FunctionalInterface
    public static interface WorldModified {
        void onWorldModified(ScaffoldWorld world, Vector3ic pos, @Nullable BlockState oldState, @Nullable BlockState newState);
    }
}
