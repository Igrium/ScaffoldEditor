package com.igrium.scaffold.compile.steps;

import org.slf4j.Logger;

import com.igrium.scaffold.compile.CompileContext;
import com.igrium.scaffold.compile.CompileStep;
import com.igrium.scaffold.compile.ScaffoldCompiler;

public class LogicCompileStep implements CompileStep {

    @Override
    public String getDescription() {
        return "Compiling level logic";
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public void execute(ScaffoldCompiler compiler, CompileContext context, Logger logger) throws Exception {
        compiler.getLevel().compileLogic(context.getDataPack());
    }
    
}
