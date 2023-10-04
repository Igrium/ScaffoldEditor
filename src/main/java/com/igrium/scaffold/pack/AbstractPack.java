package com.igrium.scaffold.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Objects;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.igrium.scaffold.core.Project;

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
        return GSON.toJson(source);
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
        
    }
}
