package com.igrium.scaffold.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AsyncUtils {
    public static CompletableFuture<Void> runDangerousAsync(DangerousRunnable<?> runnable, Executor executor) {
        return supplyDangerousAsync(() -> {
            runnable.run();
            return null;
        }, executor);
    }

    public static <T> CompletableFuture<T> supplyDangerousAsync(DangerousSupplier<T, ?> supplier, Executor executor) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                future.complete(supplier.get());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
