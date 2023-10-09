package com.igrium.scaffold.compile.steps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;

import com.igrium.scaffold.compile.CompileConfig;
import com.igrium.scaffold.compile.CompileOptions;
import com.igrium.scaffold.compile.CompileStep;
import com.igrium.scaffold.compile.ScaffoldCompiler;
import com.igrium.scaffold.core.ProjectSettings;

public class SetupCompileStep implements CompileStep {

    @Override
    public String getDescription() {
        return "Initializing compile...";
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public void execute(ScaffoldCompiler compiler, CompileConfig config, Logger logger) throws IOException {
        Path target = config.getOptionOrDefault(CompileOptions.TARGET, Path.class, Paths.get(""));
        target = compiler.getProject().makePathGlobal(target);
        logger.info("Writing to {}", target);

        Files.createDirectories(target);
        Path jsonFile = target.resolve("project.json");
        String json = ProjectSettings.toJson(compiler.getProject().getProjectSettings());

        Files.writeString(jsonFile, json);
    }

}
