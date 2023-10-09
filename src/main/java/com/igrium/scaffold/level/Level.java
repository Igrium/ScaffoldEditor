package com.igrium.scaffold.level;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.igrium.scaffold.compile.CompileConfig;
import com.igrium.scaffold.compile.ScaffoldCompiler;
import com.igrium.scaffold.compile.ScaffoldCompiler.CompileResult;
import com.igrium.scaffold.core.Project;
import com.igrium.scaffold.level.attributes.PathAttribute;
import com.igrium.scaffold.level.element.ScaffoldElement;
import com.igrium.scaffold.level.stack.StackGroup;

/**
 * A level within a scaffold project. Each level has a set of "objects" which compile into the world.
 */
public class Level {

    private final Project project;

    private final StackGroup levelStack = new StackGroup();

    public Level(Project project) {
        this.project = project;
    }

    /**
     * A cache of entity ids.
     */
    private Map<String, ScaffoldElement> idCache = new HashMap<>();

    public final Project getProject() {
        return project;
    }

    /**
     * Get the level stack.
     * @return The root stack group.
     */
    public StackGroup getLevelStack() {
        return levelStack;
    }

    /**
     * Find an element by its ID. Note: the returned element may not still be in the
     * level.
     * 
     * @param id The ID to look for.
     * @return The element, or an empty optional if it was not found.
     */
    public Optional<ScaffoldElement> getElement(String id) {
        ScaffoldElement element = idCache.get(id);
        if (element != null && element.getLevel() == this) return Optional.of(element);

        for (ScaffoldElement levelElement : levelStack) {
            if (levelElement.getId().equals(id)) {
                idCache.put(id, levelElement);
                return Optional.of(levelElement);
            }
        }
        return Optional.empty();
    }

    /**
     * Rebuild the element ID cache.
     */
    public void refreshCache() {
        idCache.clear();
        for (ScaffoldElement element : levelStack) {
            idCache.put(element.getId(), element);
        }
    }

    public void removeElement(ScaffoldElement element) {
        if (element.getLevel() != this) {
            throw new IllegalArgumentException("The supplied element is not part of this level.");
        }
        element.getStackItem().detach();
        
    }

    public CompileResult compile(Path target) {
        CompileConfig compileConfig = new CompileConfig();
        compileConfig.setOption("target", new PathAttribute(target));

        ScaffoldCompiler compiler = new ScaffoldCompiler(compileConfig, this, getProject());
        compiler.setupCompileSteps();
        return compiler.compile();
    }
}
