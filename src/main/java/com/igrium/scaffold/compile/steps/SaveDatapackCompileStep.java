package com.igrium.scaffold.compile.steps;

import org.slf4j.Logger;

import com.igrium.scaffold.compile.CompileContext;
import com.igrium.scaffold.compile.CompileStep;
import com.igrium.scaffold.compile.ScaffoldCompiler;
import com.igrium.scaffold.pack.DataPack;
import com.igrium.scaffold.pack.FolderPackWriter;
import com.igrium.scaffold.pack.PackWriter;

public class SaveDatapackCompileStep implements CompileStep {

    @Override
    public String getDescription() {
        return "Writing datapack";
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public void execute(ScaffoldCompiler compiler, CompileContext context, Logger logger) throws Exception {
        DataPack dataPack = context.getDataPack();
        PackWriter writer = new FolderPackWriter(compiler.getConfig().getFinalDatapackTarget());

        dataPack.compile(writer);
    }
    
}
