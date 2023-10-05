package com.igrium.scaffold.pack;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.igrium.scaffold.core.Project;
import com.igrium.scaffold.util.FileUtils;

/**
 * Represents a Minecraft pack, such as a datapack or resourcepack. Used during
 * compile-time only.
 */
public abstract class AbstractPack {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public enum ResourceType {
        ASSETS("assets"),
        DATA("data");

        private final String folderName;

        ResourceType(String folderName) {
            this.folderName = folderName;
        }

        public String getFolderName() {
            return folderName;
        }
    }

    public static class PackMeta {

        @SerializedName("pack_format")
        private int packFormat = 18;

        public int getPackFormat() {
            return packFormat;
        }

        public void setPackFormat(int packFormat) {
            this.packFormat = packFormat;
        }

        @SerializedName("description")
        private String description = "";

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = Objects.requireNonNull(description);
        }

        // Gson constructor
        public PackMeta() {}
    }

    private final Project project;

    private final String source;

    public Project getProject() {
        return project;
    }

    public String getSource() {
        return source;
    }

    /**
     * Create a pack.
     * 
     * @param project The project to compile with.
     * @param source  The source folder (or zip file) that we will attempt to
     *                compile from, as an asset path.
     */
    public AbstractPack(Project project, String source) {
        this.project = project;
        this.source = source;
    }

    private PackMeta meta = new PackMeta();

    /**
     * Get the pack metadata.
     * @return Pack meta.
     */
    public PackMeta getMeta() {
        return meta;
    }

    /**
     * Set the pack metadata.
     * @param meta Pack meta.
     */
    public void setMeta(PackMeta meta) {
        this.meta = Objects.requireNonNull(meta);
    }

    /**
     * Get the description of this pack. Shortcut for
     * <code>getMeta().getDescription()</code>.
     * 
     * @return Pack description.
     */
    public final String getDescription() {
        return getMeta().getDescription();
    }

    /**
     * Set the description of this pack. Shortcut for
     * <code>getMeta().setDescription()</code>.
     * 
     * @param description Pack description.
     */
    public final void setDescription(String description) {
        getMeta().setDescription(description);
    }

    /**
     * Get the format number of this pack. Shortcut for
     * <code>getMeta().getPackFormat()</code>.
     * 
     * @return Pack version number.
     */
    public final int getPackFormat() {
        return getMeta().getPackFormat();
    }

    /**
     * Set the format number of this pack. Shortcut for
     * <code>getMeta().setPackFormat()</code>.
     * 
     * @param packFormat Pack version number
     */
    public final void setPackFormat(int packFormat) {
        getMeta().setPackFormat(packFormat);
    }

    private static class PackMCMeta {
        PackMeta pack;
    }

    /**
     * Write the pack.mcmeta file.
     * @return File contents.
     */
    public String writePackMeta() {
        var mcmeta = new PackMCMeta();
        mcmeta.pack = this.getMeta();
        return GSON.toJson(mcmeta);
    }

    /**
     * Read and apply a pack.mcmeta file.
     * @param meta File contents.
     */
    public void readPackMeta(String meta) {
        Objects.requireNonNull(meta);

        PackMCMeta mcmeta = GSON.fromJson(meta, PackMCMeta.class);
        this.setMeta(mcmeta.pack);
    }

    /**
     * Copy a file from the input stream to the output stream. Default
     * implementation simply copies the bytes over. Subclasses can override this to
     * apply processing to the files.
     * 
     * @param in       Input file.
     * @param out      Output file.
     * @param filepath Path relative to data root of the file (sibiling to
     *                 <code>pack.mcmeta</code>).
     * @throws IOException If an IO exception occurs.
     */
    protected void processFile(InputStream in, OutputStream out, String filepath) throws IOException {
        in.transferTo(out);
    }

    /**
     * Compile this pack.
     * @param writer The pack writer to use.
     * @throws IOException If an IO exception occurs.
     */
    public void compile(PackWriter writer) throws IOException {
        // Files that have already been written don't need to be written again.
        Set<String> writtenFiles = new HashSet<>();
        preCompile(writer);

        for (Path path : project.getSearchPaths()) {
            Path source = path.resolve(this.source);
            if (Files.isDirectory(source)) {
                compileDirectory(writer, source, this.source, null, writtenFiles::add);
            }
        }

        Iterable<URL> zips = project.getAssetManager().findAssets(source + ".zip");
        for (URL zip : zips) {
            try(InputStream in = new BufferedInputStream(zip.openStream())) {
                compileZip(writer, source, in, filename -> !writtenFiles.contains(filename), writtenFiles::add);
            }
        }

        try(BufferedWriter metaWriter = new BufferedWriter(new OutputStreamWriter(writer.openStream("pack.mcmeta")))) {
            metaWriter.write(writePackMeta());
        }

        postCompile(writer);
    }

    /**
     * Compile a directory into the pack.
     * 
     * @param writer    The pack writer.
     * @param directory The directory to compile.
     * @param prefix    A prefix to append to each file (<code>data</code> or
     *                  <code>assets</code>)
     * @param predicate Test to run on each file for whether we should compile it.
     * @param onWrite   A listener to call when a file has been written. Accepts an
     *                  abstract, relative pathname.
     * @throws IOException If an IO exception occurs.
     */
    protected void compileDirectory(PackWriter writer, Path directory, String prefix, Predicate<String> predicate,
            Consumer<String> onWrite)
            throws IOException {
        if (!Files.isDirectory(directory))
            throw new FileNotFoundException(directory.toString() + " is not a directory!");

        if (predicate == null)
            predicate = f -> true;

        // TODO: add ignore file
        List<Path> files = Files.walk(directory).filter(Files::isRegularFile).toList();
        for (Path file : files) {
            String relative = directory.relativize(file).toString().replace('\\', '/');
            if (!predicate.test(relative))
                continue;
            
            // The name relative to the resource packs root (the file with pack.mcmeta)
            String filename = prefix != null && prefix.length() > 0 ? prefix + "/" + relative : relative;

            try (InputStream in = new BufferedInputStream(Files.newInputStream(file));
                    OutputStream out = writer.openStream(filename)) {

                processFile(in, out, relative);
            }
            if (onWrite != null) onWrite.accept(relative);
        }
    }

    /**
     * Compile a zip file into the pack. Zip file should contain a single folder
     * entry titled <code>assets</code> or <code>data</code>, which houses all the
     * resources.
     * 
     * @param writer     Output writer to write with.
     * @param sourceName Name of the root pack folder (<code>data</code> or
     *                   <code>assets</code>.)
     * @param is         An input stream with the zip contents.
     * @param predicate  Test to run on each file for whether we should compile it.
     * @param onWrite    A listener to call when a file has been written. Accepts an
     *                   abstract, relative pathname.
     * @throws IOException If an IO exception occurs.
     */
    protected void compileZip(PackWriter writer, String sourceName, InputStream is, Predicate<String> predicate,
            Consumer<String> onWrite) throws IOException {
        Path tmpDir = Files.createTempDirectory("scaffold-compile");
        FileUtils.extractZip(is, tmpDir);

        compileDirectory(writer, tmpDir, null, predicate, onWrite);
    }

    /**
     * Called after the compile has initialized but before any files are processed.
     * 
     * @param writer The pack writer.
     * @throws IOException If an IO exception occurs.
     */
    protected abstract void preCompile(PackWriter writer) throws IOException;

    /**
     * Called after files have been processed but befolre the compile has ben finalized.
     * @param writer The pack writer.
     * @throws IOException If an IO exception occurs.
     */
    protected abstract void postCompile(PackWriter writer) throws IOException;
}
;