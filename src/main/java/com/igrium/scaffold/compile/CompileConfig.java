package com.igrium.scaffold.compile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class CompileConfig {
    private Path compileTarget = Paths.get("");

    public Path getCompileTarget() {
        return compileTarget;
    }

    public void setCompileTarget(Path compileTarget) {
        this.compileTarget = Objects.requireNonNull(compileTarget);
    }

    private Optional<Path> datapackTarget = Optional.empty();

    public Optional<Path> getDatapackTarget() {
        return datapackTarget;
    }

    public void setDatapackTarget(Optional<Path> datapackTarget) {
        this.datapackTarget = Objects.requireNonNull(datapackTarget);
    }

    public final void setDatapackTarget(@Nullable Path datapackTarget) {
        setDatapackTarget(Optional.ofNullable(datapackTarget));
    }

    public Path getFinalDatapackTarget() {
        if (datapackTarget.isPresent()) {
            return datapackTarget.get();
        } else {
            return compileTarget.resolve("datapacks/level");
        }
    }
}
