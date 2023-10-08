package com.igrium.scaffold.pack;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;

import com.igrium.scaffold.core.Project;
import com.igrium.scaffold.logic.AbstractFunction;

public class DataPack extends AbstractPack {

    private Collection<AbstractFunction> functions = new HashSet<>();

    public Collection<AbstractFunction> functions() {
        return functions;
    }

    public DataPack(Project project, String source) {
        super(project, source);
    }

    @Override
    protected void preCompile(PackWriter writer) throws IOException {

    }

    @Override
    protected void postCompile(PackWriter writer) throws IOException {

        // Compile functions
        for (AbstractFunction function : functions) {
            try (Writer bufWriter = new OutputStreamWriter(
                    writer.openStream(ResourceType.DATA, function.getResourceId()));) {
                function.compile(bufWriter);
            }
        }
    }

}
