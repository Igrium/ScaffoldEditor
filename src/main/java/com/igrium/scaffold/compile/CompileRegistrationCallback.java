package com.igrium.scaffold.compile;

import java.util.List;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

/**
 * Called when the compiler initializes its compile steps.
 */
public interface CompileRegistrationCallback {
    public static final Identifier PRE_COMPILE_PHASE = new Identifier("scaffold", "pre_compile");
    public static final Identifier COMPILE_PHASE = new Identifier("scaffold", "compile");
    public static final Identifier POST_COMPILE_PHASE = new Identifier("scaffold", "post_compile");

    public static Event<CompileRegistrationCallback> EVENT = EventFactory
            .createWithPhases(CompileRegistrationCallback.class, listeners -> (steps, config) -> {
                for (CompileRegistrationCallback listener : listeners) {
                    listener.register(steps, config);
                }
            }, PRE_COMPILE_PHASE, Event.DEFAULT_PHASE, COMPILE_PHASE, POST_COMPILE_PHASE);

    /**
     * Register all compile steps.
     * 
     * @param compileSteps List of compile steps. New steps should be inserted into
     *                     this list where needed.
     */
    public void register(List<CompileStep> compileSteps, CompileConfig config);
}
