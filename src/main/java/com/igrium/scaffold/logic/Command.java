package com.igrium.scaffold.logic;

/**
 * A single Minecraft command.
 */
public interface Command {

    /**
     * Compile this command into a callable string.
     * @return Command string (without the leading slash).
     */
    public String compile();

    /**
     * Create a command object from a string.
     * @param command Command string (without the leading slash).
     * @return Command object.
     */
    public static Command literal(String command) {
        return new LiteralCommand(command);
    }

    static class LiteralCommand implements Command {
        final String command;

        LiteralCommand(String command) {
            this.command = command;
        }

        @Override
        public String compile() {
            return command;
        }
    }
}
