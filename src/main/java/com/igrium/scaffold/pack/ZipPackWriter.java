package com.igrium.scaffold.pack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.mojang.logging.LogUtils;

public class ZipPackWriter extends PackWriter {
    private static class EntryOutputStream extends OutputStream {
        private ZipOutputStream base;
        private boolean isClosed;

        public EntryOutputStream(ZipOutputStream out, ZipEntry entry) throws IOException {
            this.base = out;
            base.putNextEntry(entry);
        }

        @Override
        public void write(int b) throws IOException {
            if (isClosed) throw new IOException("The stream is closed.");
            base.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (isClosed) throw new IOException("The stream is closed.");
            base.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (isClosed) throw new IOException("The stream is closed.");
            base.write(b, off, len);
        }

        @Override
        public void close() throws IOException {
            if (isClosed) return;
            base.closeEntry();
            isClosed = true;

        }
    }

    private Set<EntryOutputStream> streams = Collections.newSetFromMap(new WeakHashMap<>());

    private final ZipOutputStream out;

    public ZipPackWriter(File file) throws FileNotFoundException {
        out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    }

    @Override
    public OutputStream openStream(String file) throws IOException {
        // Make sure there are no lingering streams.
        closeAllStreams();
        EntryOutputStream stream = new EntryOutputStream(out, new ZipEntry(file));
        return stream;
    }

    protected void closeAllStreams() {
        for (EntryOutputStream stream : streams) {
            try {
                stream.close();
            } catch (IOException e) {
                LogUtils.getLogger().error("Error closing zip entry stream.", e);
            }
            streams.remove(stream);
        }
    }

    @Override
    public void close() throws IOException {
        closeAllStreams();
        out.close();
    }
}
