package com.igrium.scaffold.util;

public interface DangerousRunnable<E extends Exception> {
    public void run() throws E;
}
