package org.ubimix.resources.adapters.zip;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author kotelnikov
 */
public class ZipBuilder {

    private ZipOutputStream fZipStream;

    public ZipBuilder(OutputStream output) {
        fZipStream = new ZipOutputStream(new BufferedOutputStream(output));
    }

    public ZipBuilder addEntry(String path, InputStream input)
        throws IOException {
        ZipEntry entry = newZipEntry(path);
        fZipStream.setMethod(ZipEntry.DEFLATED);
        entry.setMethod(ZipEntry.DEFLATED);
        addEntry(entry, input);
        return this;
    }

    public ZipBuilder addEntry(String path, String content) throws IOException {
        byte[] array = content.getBytes("UTF-8");
        InputStream input = new ByteArrayInputStream(array);
        return addEntry(path, input);
    }

    protected void addEntry(ZipEntry entry, InputStream in) throws IOException {
        try {
            fZipStream.putNextEntry(entry);
            int len;
            byte[] buf = new byte[1000 * 10];
            while ((len = in.read(buf)) > 0) {
                fZipStream.write(buf, 0, len);
            }
            fZipStream.closeEntry();
        } finally {
            in.close();
        }
    }

    public ZipBuilder addStoredEntry(String path, InputStream input)
        throws IOException {
        ZipEntry entry = newZipEntry(path);
        fZipStream.setMethod(ZipEntry.STORED);
        entry.setMethod(ZipEntry.STORED);
        addEntry(entry, input);
        return this;
    }

    public ZipBuilder addStoredEntry(String path, String content)
        throws IOException {
        byte[] array = content.getBytes("UTF-8");
        InputStream input = new ByteArrayInputStream(array);
        ZipEntry entry = newZipEntry(path);
        fZipStream.setMethod(ZipEntry.STORED);
        entry.setMethod(ZipEntry.STORED);
        entry.setSize(array.length);
        CRC32 crc = new CRC32();
        crc.update(array);
        entry.setCrc(crc.getValue());
        addEntry(entry, input);
        return this;
    }

    public void close() throws IOException {
        fZipStream.close();
    }

    protected ZipEntry newZipEntry(String path) {
        path = path.replace('\\', '/');
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        ZipEntry entry = new ZipEntry(path);
        return entry;
    }
}