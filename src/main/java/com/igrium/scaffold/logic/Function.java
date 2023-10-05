package com.igrium.scaffold.logic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Identifier;

public class Function extends AbstractFunction {

    public final List<Command> commands = new ArrayList<>();

    public Function(Identifier id) {
        super(id);
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
    
}
