package com.igrium.scaffold.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    /**
     * Get the directories to search for assets in. Directories closer to zero in
     * the list are prioritized.
     * 
     * @return A list of absolute file paths.
     */
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
        Optional<URL> fileOpt = findAsset(name);
        if (fileOpt.isEmpty()) {
            throw new FileNotFoundException("Unable to find asset: " + name);
        }

        try(InputStream in = fileOpt.get().openStream()) {
            return loader.load(in, name);
        }
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

    /**
     * Search all the loaded asset sources for a file. <br>
     * Begins by searching the project folder and other search directories. If the
     * file is not found there, it searches Scaffold's builtin resources.
     * 
     * @param name Pathname of file to find relative to project root.
     * @return URL of the located file or an empty optional if it doesn't exist. May
     *         be a file or a reference to a file within a jar.
     */
    public Optional<URL> findAsset(String name) {
        for (Path folder : searchDirectories) {
            Path file = folder.resolve(name);
            if (Files.isRegularFile(file)) {
                try {
                    return Optional.of(file.toUri().toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return Optional.ofNullable(getClass().getResource("/scaffold/" + name));
    }

    /**
     * Get a list of all instances of an asset in search order, where assets earlier
     * in the list are prioritized.
     * Follows the search order defined in {@link #findAsset(String)}.
     * 
     * @param in Asset to search for.
     * @return All instances of the asset.
     * @throws IOException If an IO exception occurs.
     */
    public List<URL> findAssets(String name) throws IOException {
        List<URL> assets = new ArrayList<>();
        for (Path folder : getSearchDirectories()) {
            Path file = folder.resolve(name);
            if (Files.isRegularFile(file)) {
                assets.add(file.toUri().toURL());
            }
        }

        assets.addAll(Collections.list(getClass().getClassLoader().getResources("/scaffold/" + name)));
        return assets;
    }

    /**
     * Get a file as an asset relative to the project folder, as defined by the
     * first entry in the asset manager's search directories.
     * 
     * @param path Path to file.
     * @return Local asset path.
     */
    public String relativise(Path path) {
        String name = searchDirectories.get(0).relativize(path).toString();
        return name.replace('\\', '/');
    }
}
