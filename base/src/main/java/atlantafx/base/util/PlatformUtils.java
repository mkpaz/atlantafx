/* SPDX-License-Identifier: MIT */
package atlantafx.base.util;

public final class PlatformUtils {

    private static final String OS = System.getProperty("os.name");
    private static final boolean MAC = OS.startsWith("Mac");

    public static boolean isMac() {
        return MAC;
    }
}
