/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class IOUtils {

    public IOUtils() {
        // utility
    }

    /**
     * Recursively deletes a directory and all of its contents.
     */
    public static void deleteDirectory(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            List<Path> paths = stream.sorted(Comparator.reverseOrder()).toList();
            for (var path : paths) {
                Files.delete(path);
            }
        }
    }

    /**
     * Extracts a ZIP archive to the specified target directory.
     * Deletes the target root extracted folder if it already exists prior to extraction.
     *
     * @param zipFile   path to the ZIP file
     * @param targetDir destination folder
     * @return Path to the root extracted directory inside targetDirectory
     */
    public static Path extractZipArchive(Path zipFile, Path targetDir) throws IOException {
        Path rootExtractedDir = null;

        try (InputStream fis = Files.newInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path resolvedPath = targetDir.resolve(entry.getName()).normalize();

                if (!resolvedPath.startsWith(targetDir.normalize())) {
                    throw new IOException("Bad zip entry path (Zip Slip attack detected): " + entry.getName());
                }

                if (rootExtractedDir == null) {
                    Path relativePath = targetDir.relativize(resolvedPath);
                    if (relativePath.getNameCount() > 0) {
                        rootExtractedDir = targetDir.resolve(relativePath.getName(0));
                        // Remove existing directory if it already exists before unpacking
                        if (Files.exists(rootExtractedDir)) {
                            IOUtils.deleteDirectory(rootExtractedDir);
                        }
                    }
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    Files.createDirectories(resolvedPath.getParent());
                    try (OutputStream os = Files.newOutputStream(resolvedPath)) {
                        zis.transferTo(os);
                    }
                }
                zis.closeEntry();
            }
        }

        return rootExtractedDir != null ? rootExtractedDir : targetDir;
    }
}
