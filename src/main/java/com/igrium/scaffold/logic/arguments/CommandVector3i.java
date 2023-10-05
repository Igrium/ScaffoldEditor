package com.igrium.scaffold.logic.arguments;

import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.igrium.scaffold.logic.arguments.CommandVector.Mode;

/**
 * A vector that can be inserted into a Minecraft command.
 */
public class CommandVector3i extends Vector3i {
    public final Mode mode;

    public CommandVector3i(int x, int y, int z) {
        super(x, y, z);
        this.mode = Mode.GLOBAL;
    }

    public CommandVector3i(int x, int y, int z, Mode mode) {
        super(x, y, z);
        this.mode = mode;
    }

    public CommandVector3i(Vector3ic vec) {
        super(vec);
        this.mode = Mode.GLOBAL;
    }

    public CommandVector3i(Vector3ic vec, Mode mode) {
        super(vec);
        this.mode = mode;
    }

    public String getString() {
        String prefix;
        switch(mode) {
            case GLOBAL:
                prefix = "";
                break;
            case RELATIVE:
                prefix = "~";
                break;
            case LOCAL:
                prefix = "^";
            default:
                prefix = "";
        }

        return String.format("%s%d %s%d %s%d", prefix, x, prefix, y, prefix, z);
    }

    @Override
    public String toString() {
        return getString();
    }

    public static CommandVector3i fromString(String in) {
        in = in.strip();
        char modeChar = in.charAt(0);
        Mode mode;

        if (modeChar == '~') mode = Mode.RELATIVE;
        else if (modeChar == '^') mode = Mode.LOCAL;
        else mode = Mode.GLOBAL;
        
        in = in.replace("~", "").replace("^", "");
        String[] split = in.split(" ");

        return new CommandVector3i(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), mode);
    }
}
