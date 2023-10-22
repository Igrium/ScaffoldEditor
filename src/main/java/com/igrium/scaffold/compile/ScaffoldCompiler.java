package com.igrium.scaffold.compile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.igrium.scaffold.compile.steps.SetupCompileStep;
import com.igrium.scaffold.core.Project;
import com.igrium.scaffold.level.Level;
import com.igrium.scaffold.util.collections.LockableList;
import com.mojang.logging.LogUtils;

public class ScaffoldCompiler {

    public static enum CompileStatus {
        COMPLETE, FAILED
    }

    public static enum CompileStage {
        PRE_COMPILE,
        COMPILE,
        POST_COMPILE
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
    
    private final CompileConfig config;

    private final Level level;

    private final Project project;

    private volatile boolean isCompiling;

    public ScaffoldCompiler(CompileConfig config, Level level, Project project) {
        this.config = config;
        this.level = level;
        this.project = project;
    }

    public void setupCompileSteps() {
        CompileRegistrationCallback.EVENT.invoker().register(compileSteps, config);
        // Setup must always be first.
        compileSteps.add(0, new SetupCompileStep());
    }

    public CompileConfig getConfig() {
        return config;
    }

    public Level getLevel() {
        return level;
    }
    
    public Project getProject() {
        return project;
    }

    public List<CompileStep> getCompileSteps() {
        return compileSteps;
    }   

    public void addCompileStep(CompileStep compileStep) {
        compileSteps.add(compileStep);
        Collections.synchronizedList(null);
    }

    public boolean isCompiling() {
        return isCompiling;
    }

    public CompileResult compile() {
        if (isCompiling) {
            throw new IllegalStateException("This compiler is in use.");
        }

        isCompiling = true;
        compileSteps.lock();
        Logger logger = LogUtils.getLogger();

        CompileContext context = new CompileContext(this);

        for (CompileStep compileStep : compileSteps) {
            LogUtils.getLogger().info(compileStep.getDescription());
            try {
                compileStep.execute(this, context, logger);
            } catch (Exception e) {
                if (compileStep.isRequired()) {
                    return CompileResult.failed(e);
                } else {
                    LogUtils.getLogger().warn("Failed to complete compile step.", e);
                }
            }
        }

        isCompiling = false;
        return CompileResult.complete();
    }

}
