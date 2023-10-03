package com.igrium.scaffold.util;

public interface DangerousSupplier<T, E extends Exception> {
    public T get() throws E;
}
