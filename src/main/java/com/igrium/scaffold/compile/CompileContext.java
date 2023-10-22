package com.igrium.scaffold.compile;

import com.igrium.scaffold.pack.DataPack;

/**
 * Holds variables that need to persist between compile steps. Addons can add
 * custom fields via mixins.
 */
public class CompileContext {
    private final ScaffoldCompiler compiler;

    public CompileContext(ScaffoldCompiler compiler) {
        this.compiler = compiler;
    }

    public ScaffoldCompiler getCompiler() {
        return compiler;
    }

    private DataPack dataPack;

    public DataPack getDataPack() {
        return dataPack;
    }

    public void setDataPack(DataPack dataPack) {
        this.dataPack = dataPack;
    }
}
