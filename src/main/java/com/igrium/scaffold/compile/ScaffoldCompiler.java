package com.igrium.scaffold.compile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.igrium.scaffold.util.collections.LockableList;
import com.mojang.logging.LogUtils;

public class ScaffoldCompiler {

    public static enum CompileStatus {
        COMPLETE, FAILED
    }

    public static record CompileResult(CompileStatus status, @Nullable Exception exception) {

        public static CompileResult complete() {
            return new CompileResult(CompileStatus.COMPLETE, null);
        }

        public static CompileResult failed(Exception exception) {
            return new CompileResult(CompileStatus.FAILED, exception);
        }
    }

    private final LockableList<CompileStep> compileSteps = new LockableList<>(new LinkedList<>());
    
    private final CompileConfig config = new CompileConfig();

    protected List<CompileStep> getCompileSteps() {
        return compileSteps;
    }

    public void addCompileStep(CompileStep compileStep) {
        compileSteps.add(compileStep);
        Collections.synchronizedList(null);
    }

    public CompileResult compile() {
        compileSteps.lock();
        config.lock();

        for (CompileStep compileStep : compileSteps) {
            LogUtils.getLogger().info(compileStep.getDescription());
            try {
                compileStep.execute(this, config);
            } catch (Exception e) {
                if (compileStep.isRequired()) {
                    return CompileResult.failed(e);
                } else {
                    LogUtils.getLogger().warn("Failed to complete compile step.", e);
                }
            }
        }

        return CompileResult.complete();
    }
}
