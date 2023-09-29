package com.igrium.scaffold.level;

/**
 * A level within a scaffold project. Each level has a set of "objects" which compile into the world.
 */
public class Level {

    private final LevelStack levelStack = new LevelStack();

    public LevelStack getLevelStack() {
        return levelStack;
    }
}
