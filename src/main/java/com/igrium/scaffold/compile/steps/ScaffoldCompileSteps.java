package com.igrium.scaffold.compile.steps;

import java.util.List;

import com.igrium.scaffold.compile.CompileConfig;
import com.igrium.scaffold.compile.CompileStep;

public final class ScaffoldCompileSteps {
    public static void register(List<CompileStep> steps, CompileConfig config) {
        steps.add(new LogicCompileStep());
    }

    public static void registerPost(List<CompileStep> steps, CompileConfig config) {
        steps.add(new SaveDatapackCompileStep());
    }
}
