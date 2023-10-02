package com.igrium.scaffold.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.util.Identifier;

/**
 * A resourcepack of some sort, be it assets or data. This class is only
 * intended to be used during compilation; code that needs to modify resource
 * files in the project should access them directly.
 */
public class BaseResourcePack {
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

    private final ResourceType resourceType;
    private final Path src;

    private PackMeta packMeta = new PackMeta();

    public PackMeta getPackMeta() {
        return packMeta;
    }

    public void setPackMeta(PackMeta packMeta) {
        this.packMeta = packMeta;
    }

    /**
     * Create a new resourcepack.
     * 
     * @param resourceType The type of assets this resourcepack will hold.
     * @param src       The <code>data</code> or <code>assets</code> folder of the
     *                  resourcepack source files.
     */
    public BaseResourcePack(ResourceType resourceType, Path src) {
        this.resourceType = resourceType;
        this.src = src;
    }

    /**
     * The <code>data</code> or <code>assets</code> folder of the resourcepack
     * source files.
     */
    public Path getSrc() {
        return src;
    }

    /**
     * The resource type of this pack.
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    protected Path getFile(Identifier id) {
        return src.resolve(id.getNamespace()).resolve(id.getPath());
    }

    public SeekableByteChannel openFile(Identifier id) throws IOException {
        return Files.newByteChannel(getFile(id), StandardOpenOption.READ);
    }

    public InputStream openStream(Identifier id) throws IOException {
        return Files.newInputStream(src, StandardOpenOption.READ);
    }

    /**
     * Whether a file should be copied verbatim during compilation.
     * @param file File ID.
     * @return Should it be copied?
     */
    protected boolean shouldCopyFile(Identifier file) {
        return true;
    }

    public final void compile(PackWriter writer) throws IOException {
        compile(writer, this::shouldCopyFile);
    }

    public void compile(PackWriter writer, Predicate<Identifier> filePredicate) throws IOException {
        DirectoryStream<Path> dirs = Files.newDirectoryStream(src);
        for (Path dir : dirs) {
            compileNamespace(writer, dir.getFileName().toString(), filePredicate);
        }
    }

    private void compileNamespace(PackWriter writer, String namespace, Predicate<Identifier> filePredicate)
            throws IOException {
        Path namedPath = src.resolve(namespace);

        Stream<Path> stream = Files.walk(namedPath);
        for (Path path : ((Iterable<Path>) stream::iterator)) {
            Identifier id = new Identifier(namespace, namedPath.relativize(path).toString().replace("\\", "/"));
            compileFile(id, writer);
        }
        stream.close();
    }

    protected void compileFile(Identifier id, PackWriter writer) throws IOException {
        try(OutputStream out = writer.openStream(resourceType, id); InputStream in = openStream(id)) {
            in.transferTo(out);
        }
    }
}
