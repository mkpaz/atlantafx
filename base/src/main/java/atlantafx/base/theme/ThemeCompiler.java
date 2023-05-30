/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;
import javafx.css.Stylesheet;

/**
 * A lazy man CSS to BSS compiler wrapper.
 */
public class ThemeCompiler {

    /**
     * The main class that accepts exactly one parameter, which is the path to
     * the source directory to be scanned for CSS files.
     *
     * <p>Usage:
     * <pre>{@code
     * java ThemeCompiler <path>
     * }</pre>
     *
     * @see #convertToBinary(Path)
     */
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                throw new IllegalArgumentException("You must provide the source directory path");
            }

            if (args.length > 1) {
                throw new IllegalArgumentException(
                    "Unexpected arguments were found: "
                        + Arrays.toString(Arrays.copyOfRange(args, 1, args.length))
                );
            }

            var dir = Paths.get(args[0]);
            new ThemeCompiler().convertToBinary(dir);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Converts all CSS files in the specified directory to BSS.
     *
     * @param dir The source directory to scan for CSS files.
     * @throws IOException to punish you for using Java
     */
    public void convertToBinary(Path dir) throws IOException {
        if (dir == null || !Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Invalid directory: " + dir);
        }

        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(f -> f.toString().endsWith(".css"))
                .forEach(f -> {
                    try {
                        convertToBinary(f, f.resolveSibling(getFilename(f) + ".bss"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
    }

    /**
     * Converts the specified CSS file to BSS. If no output file is given,
     * then the input file name is used with an extension of 'bss'.
     *
     * @param in  The input file path.
     * @param out The output file path.
     * @throws IOException to punish you for using Java
     */
    public void convertToBinary(Path in, Path out) throws IOException {
        if (in == null || out == null) {
            throw new IllegalArgumentException("Both input and output files must be specified.");
        }

        if (in.equals(out)) {
            throw new IllegalArgumentException("Input file and output file cannot be the same.");
        }

        Stylesheet.convertToBinary(in.toFile(), out.toFile());
    }

    private String getFilename(Path f) {
        String name = f.getFileName().toString();
        return name.substring(0, name.lastIndexOf('.'));
    }
}
