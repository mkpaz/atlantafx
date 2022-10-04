/* SPDX-License-Identifier: MIT */
package atlantafx.base.util;

public final class PlatformUtils {

    /**
     * Initialize a new PlatformUtils
     */
    private PlatformUtils() {
        // Default constructor
    }

    private static final String OS = System.getProperty("os.name", "generic").toLowerCase();
    private static final boolean MAC = OS.contains("mac") || OS.contains("darwin");

    public static boolean isMac() {
        return MAC;
    }
}
