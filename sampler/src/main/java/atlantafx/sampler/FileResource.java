/* SPDX-License-Identifier: MIT */

package atlantafx.sampler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class FileResource {

    private final String location;
    private final boolean internal;
    private final Class<?> anchor;

    private FileResource(String location, boolean internal, Class<?> anchor) {
        this.location = location;
        this.internal = internal;
        this.anchor = anchor;
    }

    public String location() {
        return location;
    }

    public boolean internal() {
        return internal;
    }

    public boolean exists() {
        return internal ? anchor.getResource(location) != null : Files.exists(toPath());
    }

    public Path toPath() {
        return Paths.get(location);
    }

    public URI toURI() {
        // the latter adds "file://" scheme to the URI
        return internal ? URI.create(location) : Paths.get(location).toUri();
    }

    public String getFilename() {
        return Paths.get(location).getFileName().toString();
    }

    public InputStream getInputStream() throws IOException {
        if (internal) {
            var is = anchor.getResourceAsStream(location);
            if (is == null) {
                throw new IOException("Resource not found: " + location);
            }
            return is;
        }
        return new FileInputStream(toPath().toFile());
    }

    ///////////////////////////////////////////////////////////////////////////

    public static FileResource createInternal(String location) {
        return createInternal(location, FileResource.class);
    }

    public static FileResource createInternal(String location, Class<?> anchor) {
        return new FileResource(location, true, Objects.requireNonNull(anchor));
    }

    public static FileResource createExternal(String location) {
        return new FileResource(location, false, null);
    }
}