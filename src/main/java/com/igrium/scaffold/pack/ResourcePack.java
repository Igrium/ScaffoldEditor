package com.igrium.scaffold.pack;

import java.io.IOException;

import com.igrium.scaffold.core.Project;

public class ResourcePack extends AbstractPack {

    public ResourcePack(Project project) {
        super(project, "assets");
    }

    @Override
    protected void preCompile(PackWriter writer) throws IOException {
    }

    @Override
    protected void postCompile(PackWriter writer) throws IOException {
    }
    
}
