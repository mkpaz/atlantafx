/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.util;

import atlantafx.sampler.Launcher;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import static atlantafx.base.util.PlatformUtils.*;

public final class PlatformUtils {

    /**
     * The user home directory.
     */
    public static final Path HOME_DIR = findHomeDir();

    /**
     * Returns the user home directory.
     */
    public static Path findHomeDir() {
        var prop = System.getProperty("user.home");
        return prop != null ? Paths.get(prop) : Paths.get("home"); // prevent NPE
    }

    /**
     * Returns the data directory.
     */
    public static Path findUserDataDir() {
        if (isMac()) {
            return HOME_DIR.resolve("Library")
                .resolve("Application Support")
                .resolve(Launcher.APP_NAME);
        }

        if (isWindows()) {
            Path localAppData = getLocalAppDataDir();
            if (localAppData == null) {
                localAppData = HOME_DIR.resolve("AppData").resolve("Local");
            }
            return localAppData.resolve(Launcher.APP_NAME);
        }

        if (isUnix()) {
            Path xdgDataHome = getXdgDataDir();
            if (xdgDataHome == null) {
                xdgDataHome = HOME_DIR.resolve(".local").resolve("share");
            }
            return xdgDataHome.resolve(Launcher.APP_NAME);
        }

        return HOME_DIR.resolve("." + Launcher.APP_NAME.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the operating system's temp directory.
     */
    public static Path findTempDir() {
        var prop = System.getProperty("java.io.tmpdir");
        return prop != null ? Paths.get(prop) : Paths.get("temp"); // prevent NPE
    }

    /**
     * Resolves the '$XDG_DATA_HOME' environment variable.
     */
    public static @Nullable Path getXdgDataDir() {
        var value = System.getenv("XDG_DATA_HOME");
        return value != null && !value.isBlank() ? Paths.get(value) : null;
    }

    /**
     * Resolves the '%LOCALAPPDATA%' environment variable.
     */
    public static @Nullable Path getLocalAppDataDir() {
        var value = System.getenv("LOCALAPPDATA");
        return value != null && !value.isBlank() ? Paths.get(value) : null;
    }

    /**
     * Sends string to the system clipboard.
     */
    public static void copyToClipboard(String s) {
        var content = new ClipboardContent();
        content.putString(s);
        Clipboard.getSystemClipboard().setContent(content);
    }
}
