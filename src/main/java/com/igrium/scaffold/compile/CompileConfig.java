package com.igrium.scaffold.compile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.igrium.scaffold.level.attribute.Attribute;
import com.igrium.scaffold.util.collections.LockableMap;

public class CompileConfig {
    private LockableMap<String, Attribute<?>> options = new LockableMap<>(new HashMap<>());

    public final Map<String, Attribute<?>> getOptions() {
        return options;
    }

    /**
     * Get an option as a specific class.
     * 
     * @param <T>   Option type.
     * @param name  Option name.
     * @param clazz Class to cast to.
     * @return The option value. An empty optional if the option is not found or is
     *         of the wrong class.
     */
    public <T> Optional<T> getOption(String name, Class<T> clazz) {
        Attribute<?> attribute = options.get(name);
        if (attribute == null)
            return Optional.empty();
        try {
            return Optional.of(attribute.getValue(clazz));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    /**
     * Get an option as a specific class.
     * 
     * @param <T>   Option type.
     * @param name  Option name.
     * @param clazz Class to cast to.
     * @param T     Value to use if the option isn't present or is of the wrong
     *              class.
     * @return The option value.
     */
    public <T> T getOptionOrDefault(String name, Class<T> clazz, T other) {
        return getOption(name, clazz).orElse(other);
    }

    public void setOption(String name, Attribute<?> value) {
        options.put(name, value);
    }

    protected void lock() {
        options.lock();
    }

}
