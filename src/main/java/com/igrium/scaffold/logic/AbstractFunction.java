package com.igrium.scaffold.logic;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import net.minecraft.util.Identifier;

/**
 * A series of commands that can be compiled into an <code>.mcfunction</code> file.
 */
public abstract class AbstractFunction {
    private Identifier id;

    public AbstractFunction(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    /**
     * Get all of the commands in this function.
     * @return Function commands in order.
     */
    public abstract List<Command> getCommands();

    /**
     * Compile this function to a writer.
     * @param writer Writer to write the file contents into.
     */
    public void compile(Writer writer) {
        PrintWriter printWriter = new PrintWriter(writer);

        for (Command command : getCommands()) {
            printWriter.println(command.compile());
        }

        printWriter.flush();
    }

    /**
     * Compile this function to a string.
     * @return Function file contents.
     */
    public String compile() {
        StringWriter writer = new StringWriter();
        compile(writer);
        return writer.toString();
    }
}
