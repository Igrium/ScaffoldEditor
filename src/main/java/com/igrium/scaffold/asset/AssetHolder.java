package com.igrium.scaffold.asset;

import java.util.concurrent.CompletableFuture;

public interface AssetHolder<T> {
    public CompletableFuture<T> get();

    public static <T> AssetHolder<T> ofFuture(CompletableFuture<T> future) {
        Impl<T> holder = new Impl<>(future);
        holder.setupCompleteListener();
        return holder;
    }

    public static <T> AssetHolder<T> ofValue(T value) {
        return new Impl<T>(value);
    }

    static class Impl<T> implements AssetHolder<T> {
        private boolean isLoaded;

        private CompletableFuture<T> loadingFuture;
        private T value;

        private Throwable exception;

        Impl(CompletableFuture<T> future) {
            this.loadingFuture = future;
            isLoaded = false;
        }

        Impl(T value) {
            this.value = value;
            isLoaded = true;
        }

        void setupCompleteListener() {
            if (loadingFuture == null) return;
            loadingFuture.whenComplete(this::onFutureComplete);
        }

        @Override
        public CompletableFuture<T> get() {
            if (isLoaded) {
                if (exception != null) {
                    return CompletableFuture.failedFuture(exception);
                }
                return CompletableFuture.completedFuture(value);
            }
            else return loadingFuture;
        }

        private void onFutureComplete(T val, Throwable e) {
            if (e != null) {
                exception = e;
            } else {
                value = val;
            }
            isLoaded = true;
        }
    }
    
}
