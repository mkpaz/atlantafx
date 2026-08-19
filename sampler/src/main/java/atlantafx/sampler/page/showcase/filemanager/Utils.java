/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import org.jspecify.annotations.Nullable;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

final class Utils {

    private Utils() {
        // Default constructor
    }

    public static long fileSize(@Nullable Path path) {
        if (path == null) {
            return 0;
        }
        try {
            return Files.size(path);
        } catch (IOException e) {
            return 0;
        }
    }

    public static boolean isFileHidden(@Nullable Path path) {
        if (path == null) {
            return false;
        }
        try {
            return Files.isHidden(path);
        } catch (IOException e) {
            return false;
        }
    }

    public static @Nullable FileTime fileMTime(@Nullable Path path, LinkOption... options) {
        if (path == null) {
            return null;
        }
        try {
            return Files.getLastModifiedTime(path, options);
        } catch (IOException e) {
            return null;
        }
    }

    public static void openFile(Path path) {
        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(path.toFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public static @Nullable String getMimeType(Path path) {
        try {
            return Files.probeContentType(path);
        } catch (IOException e) {
            return null;
        }
    }
}
