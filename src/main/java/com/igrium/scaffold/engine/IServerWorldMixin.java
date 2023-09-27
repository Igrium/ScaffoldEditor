package com.igrium.scaffold.engine;

import java.util.Optional;

import com.igrium.scaffold.level.ScaffoldWorld;

public interface IServerWorldMixin {
    public default boolean isEditorWorld() {
        return getScaffoldWorld().isPresent();
    }

    public Optional<ScaffoldWorld> getScaffoldWorld();
}
