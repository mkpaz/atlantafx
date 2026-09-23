/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import org.jspecify.annotations.Nullable;

import java.io.InputStream;

/**
 * Utility class for loading resources available on the classpath or module path.
 */
public final class Resources {

    private Resources() {
        // utility
    }

    /**
     * Finds and opens an input stream for the resource at the specified classpath location.
     * Path normalization is applied automatically (leading slashes are stripped for ClassLoader lookup).
     *
     * <p>Lookup Order:
     * <ul>
     * <li>Thread Context ClassLoader (TCCL)
     * <li>Class-level ClassLoader ({@code Resources.class.getClassLoader()})
     * <li>System ClassLoader ({@code ClassLoader#getSystemResourceAsStream(String)})
     * </ul>
     *
     * <p>Module-Path Behavior:
     * <ul>
     * <li>Current Module: Returns {@code null} if the resource is inside a packaged
     * module directory, unless the enclosing package is unconditionally open (via {@code opens}
     * in {@code module-info.java}), or located in the {@code root/META-INF/} directory.
     * <li>External Modules: Returns {@code null} unless the target module
     * explicitly opens the package containing the resource.
     * </ul>
     *
     * @param path the relative or absolute classpath resource path
     * @return an {@link InputStream} for reading the resource, or {@code null} if the path
     *     is blank, the resource cannot be found, or access is restricted by module encapsulation
     */
    public static @Nullable InputStream getResourceAsStream(String path) {
        if (path.isBlank()) {
            return null;
        }

        String absPath = path.startsWith("/") ? path : "/" + path;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            InputStream is = cl.getResourceAsStream(absPath.substring(1));
            if (is != null) {
                return is;
            }
        }

        cl = Resources.class.getClassLoader();
        if (cl != null) {
            return cl.getResourceAsStream(absPath.substring(1));
        }

        return ClassLoader.getSystemResourceAsStream(absPath.substring(1));
    }
}
