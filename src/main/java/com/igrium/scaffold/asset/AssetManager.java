package com.igrium.scaffold.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

import com.igrium.scaffold.util.AsyncUtils;

import net.minecraft.util.Util;

@SuppressWarnings("unchecked")
public class AssetManager {
    /**
     * Directories to search for assets in.
     * Directories closer to zero in the list are prioritized.
     */
    private List<Path> searchDirectories = new LinkedList<>();

    public List<Path> getSearchDirectories() {
        return Collections.unmodifiableList(searchDirectories);
    }

    public void setSearchDirectories(List<Path> searchDirectories) {
        this.searchDirectories = searchDirectories;
    }

    // The amount of generics in play here doesn't work very well. These methods
    // must remain private because they must be visually checked for errors by a
    // human.
    private final Map<AssetLoader<?>, Map<String, Object>> cache = new ConcurrentHashMap<>();

    private final Map<AssetLoader<?>, Map<String, CompletableFuture<?>>> loadingCache = new ConcurrentHashMap<>();

    @Nullable
    private <T> T getCached(AssetLoader<T> loader, String name) {
        Map<String, Object> cache = this.cache.get(loader);
        if (cache == null) return null;
        return (T) cache.get(name);
    }

    private <T> CompletableFuture<T> getLoadingCached(AssetLoader<T> loader, String name) {
        Map<String, CompletableFuture<?>> cache = this.loadingCache.get(loader);
        if (cache == null) return null;
        return (CompletableFuture<T>) cache.get(name);
    }


    private <T> void putInCache(AssetLoader<T> loader, String name, T val) {
        Map<String, Object> cache = this.cache.computeIfAbsent(loader, l -> new ConcurrentHashMap<>());
        cache.put(name, val);
    }

    private <T> void putInLoadingCache(AssetLoader<T> loader, String name, CompletableFuture<T> val) {
        Map<String, CompletableFuture<?>> cache = this.loadingCache.computeIfAbsent(loader, l -> new ConcurrentHashMap<>());
        cache.put(name, val);
    }

    // ---

        
    /**
     * Load an asset from disk.
     * @param <T> The asset type.
     * @param loader The asset loader.
     * @param name The asset name.
     * @return The loaded asset.
     * @throws IOException If an IO exception occurs.
     */
    protected <T> T loadFromDisk(AssetLoader<T> loader, String name) throws IOException {
        Optional<Path> fileOpt = findOnDisk(name);
        if (fileOpt.isEmpty()) {
            throw new FileNotFoundException("Unable to find asset: " + name);
        }

        try(InputStream in = Files.newInputStream(fileOpt.get())) {
            return loader.load(in, name);
        }
    }

    /**
     * Locate a file on the disk based on the active search directories. This method blocks for IO operations.
     * @param filename The asset name.
     * @return The file path, or an empty optional if it was not found.
     */
    public Optional<Path> findOnDisk(String filename) {
        for (Path path : searchDirectories) {
            Path file = path.resolve(filename);
            if (Files.isRegularFile(path)) return Optional.of(file);
        }
        return Optional.empty();
    }

    /**
     * Open an asset. If this asset has already been loaded and is still in the
     * cache, it won't be loaded again.
     * 
     * @param <T>      The asset type.
     * @param loader   The asset loader to use.
     * @param name     The name of the asset (path relative to project root).
     * @param force    Force-reload this asset (disregard the cache).
     * @param executor The executor to load this asset on.
     * @return A future that completes once the asset is loaded.
     */
    public <T> CompletableFuture<T> load(AssetLoader<T> loader, String name, boolean force, Executor executor) {
        T cached = getCached(loader, name);
        if (cached != null && !force) {
            return CompletableFuture.completedFuture(cached);
        }
        // We might already be loading this.
        CompletableFuture<T> loading = getLoadingCached(loader, name);
        if (loading != null) {
            return loading;
        }

        // Because loading can happen off-thread, we cache any futures requested for a
        // given asset so that subsequent calls to that asset aren't repeated before the
        // loaded asset is added to the cache.
        CompletableFuture<T> future = AsyncUtils.supplyDangerousAsync(() -> loadFromDisk(loader, name), executor).whenComplete((val, e) -> {
            var subcache = this.loadingCache.get(loader);
            if (subcache != null) subcache.remove(name);

            if (e != null) return;
            putInCache(loader, name, val);
        });
        putInLoadingCache(loader, name, future);

        return future;
    }

    /**
     * Open an asset using the worker thread executor. If this asset has already
     * been loaded and is still in the cache, it won't be loaded again.
     * 
     * @param <T>      The asset type.
     * @param loader   The asset loader to use.
     * @param name     The name of the asset (path relative to project root).
     * @param force    Force-reload this asset (disregard the cache).
     * @return A future that completes once the asset is loaded.
     */
    public <T> CompletableFuture<T> load(AssetLoader<T> loader, String name, boolean force) {
        return load(loader, name, force, Util.getMainWorkerExecutor());
    }

    /**
     * Open an asset. If this asset has already been loaded and is still in the
     * cache, it won't be loaded again.
     * 
     * @param <T>      The asset type.
     * @param loader   The asset loader to use.
     * @param name     The name of the asset (path relative to project root).
     * @return A future that completes once the asset is loaded.
     */
    public <T> CompletableFuture<T> load(AssetLoader<T> loader, String name, Executor executor) {
        return load(loader, name, false, executor);
    }

    /**
     * Open an asset using the worker thread executor. If this asset has already
     * been loaded and is still in the cache, it won't be loaded again.
     * 
     * @param <T>      The asset type.
     * @param loader   The asset loader to use.
     * @param name     The name of the asset (path relative to project root).
     * @return A future that completes once the asset is loaded.
     */
    public <T> CompletableFuture<T> load(AssetLoader<T> loader, String name) {
        return load(loader, name, false, Util.getMainWorkerExecutor());
    }

}
