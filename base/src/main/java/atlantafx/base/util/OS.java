/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

/**
 * A utility class that provides just some platform methods that's commonly
 * necessary for control/skin development.
 */
public final class OS {

    private OS() {
        // utility
    }

    private static final String NAME = System.getProperty("os.name", "generic").toLowerCase();
    private static final boolean WINDOWS = NAME.startsWith("windows");
    private static final boolean MAC = NAME.contains("mac") || NAME.contains("darwin");
    private static final boolean LINUX = NAME.startsWith("linux");
    private static final boolean FREE_BSD = NAME.startsWith("freebsd");
    private static final boolean OPEN_BSD = NAME.startsWith("openbsd");
    private static final boolean NET_BSD = NAME.startsWith("netbsd");
    private static final boolean SOLARIS = NAME.startsWith("sunos") || NAME.startsWith("solaris");
    private static final boolean AIX = NAME.startsWith("aix");

    /**
     * Returns the raw, lower-cased value of the {@code os.name} system property.
     *
     * @return the operating system name in lower case, or {@code "generic"} if the
     *         {@code os.name} property is unavailable
     */
    public static String getName() {
        return NAME;
    }

    /**
     * Checks whether the application is running on Windows.
     *
     * @return {@code true} if the current OS is Windows
     */
    public static boolean isWindows() {
        return WINDOWS;
    }

    /**
     * Checks whether the application is running on macOS.
     *
     * @return {@code true} if the current OS is macOS
     */
    public static boolean isMac() {
        return MAC;
    }

    /**
     * Checks whether the application is running on Linux.
     *
     * <p>Note: Chrome OS also reports {@code os.name = "Linux"} when running a JVM
     * inside its Linux container (Crostini), so this method returns {@code true} there
     * as well. The same applies to WSL (Windows Subsystem for Linux).
     *
     * @return {@code true} if the current OS is Linux
     */
    public static boolean isLinux() {
        return LINUX;
    }

    /**
     * Checks whether the current OS belongs to the BSD family.
     *
     * @return {@code true} if the current OS is FreeBSD, OpenBSD, or NetBSD
     */
    public static boolean isBSD() {
        return FREE_BSD || OPEN_BSD || NET_BSD;
    }

    /**
     * Checks whether the current OS belongs to the UNIX-like family.
     *
     * <p>Note: macOS is intentionally excluded from this check, even though its kernel
     * (Darwin) is BSD-based. Use {@link #isMac()} separately if macOS should also be
     * treated as UNIX-like.
     *
     * @return {@code true} if the current OS is Linux, a BSD variant, Solaris, or AIX
     */
    public static boolean isUnix() {
        return LINUX || FREE_BSD || OPEN_BSD || NET_BSD || SOLARIS || AIX;
    }
}
