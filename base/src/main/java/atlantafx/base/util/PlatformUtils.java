/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

/**
 * A utility class that provides just some platform methods that's commonly
 * necessary for control/skin development.
 */
public final class PlatformUtils {

    private PlatformUtils() {
        // Default constructor
    }

    private static final String OS = System.getProperty("os.name", "generic").toLowerCase();
    private static final boolean WINDOWS = OS.startsWith("windows");
    private static final boolean MAC = OS.contains("mac") || OS.contains("darwin");
    private static final boolean LINUX = OS.startsWith("linux");
    private static final boolean FREE_BSD = OS.startsWith("freebsd");
    private static final boolean OPEN_BSD = OS.startsWith("openbsd");
    private static final boolean NET_BSD = OS.startsWith("netbsd");

    /**
     * Returns "true" if this is Windows.
     */
    public static boolean isWindows() {
        return WINDOWS;
    }

    /**
     * Returns "true" if this is Mac.
     */
    public static boolean isMac() {
        return MAC;
    }

    /**
     * Returns "true" if this is Linux.
     */
    public static boolean isLinux() {
        return LINUX;
    }

    /**
     * Returns "true" if this is a UNIX like system.
     */
    public static boolean isUnix() {
        return LINUX || FREE_BSD || OPEN_BSD || NET_BSD;
    }
}
