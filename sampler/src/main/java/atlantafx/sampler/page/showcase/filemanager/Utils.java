/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase.filemanager;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

final class Utils {

    public static long fileSize(Path path) {
        if (path == null) { return 0; }
        try {
            return Files.size(path);
        } catch (IOException e) {
            return 0;
        }
    }

    public static boolean isFileHidden(Path path) {
        if (path == null) { return false; }
        try {
            return Files.isHidden(path);
        } catch (IOException e) {
            return false;
        }
    }

    public static FileTime fileMTime(Path path) {
        if (path == null) { return null; }
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException e) {
            return null;
        }
    }

    public static String humanReadableByteCount(long bytes) {
        if (-1000 < bytes && bytes < 1000) { return bytes + " B"; }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static void openFile(Path path) {
        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(path.toFile());
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }).start();
        }
    }
}
