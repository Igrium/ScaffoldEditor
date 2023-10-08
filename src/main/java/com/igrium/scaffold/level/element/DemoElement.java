package com.igrium.scaffold.level.element;

import com.igrium.scaffold.level.Level;
import com.igrium.scaffold.logic.Command;
import com.igrium.scaffold.logic.Function;
import com.igrium.scaffold.pack.DataPack;

import net.minecraft.util.Identifier;

public class DemoElement extends ScaffoldElement {

    public DemoElement(ElementType<?> type, Level level) {
        super(type, level);
    }
    
    @Override
    public void compileLogic(DataPack datapack) {
        Function function = new Function(new Identifier("scaffold", getId() + "/execute"));
        function.commands.add(Command.literal("say Hello from entity " + getId()));
        datapack.functions().add(function);
    }
}
