package com.igrium.scaffold.compile.steps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;

import com.igrium.scaffold.compile.CompileContext;
import com.igrium.scaffold.compile.CompileStep;
import com.igrium.scaffold.compile.ScaffoldCompiler;
import com.igrium.scaffold.pack.DataPack;

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
    public void execute(ScaffoldCompiler compiler, CompileContext context, Logger logger) throws IOException {
        Path target = compiler.getConfig().getCompileTarget();
        target = compiler.getProject().makePathGlobal(target);
        logger.info("Writing to {}", target);

        Files.createDirectories(target);
        context.setDataPack(new DataPack(compiler.getProject()));
    }

}
