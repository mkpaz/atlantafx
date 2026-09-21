/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import javafx.css.Stylesheet;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Utility tool for compiling JavaFX CSS files into binary format ({@code .bss}) and generating
 * stylesheet manifests ({@code .manifest}).
 */
public class PostCSS {

    /**
     * Main entry point accepting a single command-line argument: the path to the source directory
     * containing CSS files.
     *
     * <p>Usage example:
     * <pre>{@code
     * java atlantafx.base.theme.PostCSS <path-to-css-directory>
     * }</pre>
     *
     * @param args expects a single element containing the target directory path
     * @throws IllegalArgumentException if no arguments or more than one argument are provided
     * @throws RuntimeException         if an I/O error occurs during processing
     * @see #scan(Path)
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
            new PostCSS().scan(dir);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Scans the specified directory for all {@code .css} files, compiles each to binary format ({@code .bss}),
     * and generates a corresponding module manifest ({@code .manifest}).
     *
     * @param dir the source directory path to scan for CSS files
     * @throws IllegalArgumentException if {@code dir} is {@code null}, does not exist, or is not a directory
     * @throws IOException              if an I/O error occurs during processing
     */
    public void scan(@Nullable Path dir) throws IOException {
        if (dir == null || !Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Invalid directory: " + dir);
        }

        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(f -> f.toString().endsWith(".css"))
                .forEach(f -> {
                    try {
                        generateBSS(f, f.resolveSibling(getFilename(f) + ".bss"));
                        generateManifest(f, f.resolveSibling(getFilename(f) + ".manifest"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
    }

    /**
     * Compiles the specified CSS file into binary BSS format.
     *
     * @param in  the path to the input CSS file
     * @param out the path to the output BSS file
     * @throws IllegalArgumentException if either path is {@code null} or if {@code in} equals {@code out}
     * @throws IOException              if an I/O error occurs during processing
     */
    public void generateBSS(@Nullable Path in, @Nullable Path out) throws IOException {
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

    //region MANIFEST
    //*************************************************************************

    // Regex pattern to match markers like /*! @module:name:start */ and /*! @module:name:end */
    private static final Pattern MODULE_MARKER_PATTERN =
        Pattern.compile("/\\*!\\s*@module:([a-zA-Z0-9_-]+):(start|end)\\s*\\*/");

    private record CSSModule(String name, long startLine, long endLine) {
        public CSSModule {
            if (startLine > endLine) {
                throw new IllegalArgumentException(
                    "Invalid module boundaries for '%s': startLine (%d) > endLine (%d)".formatted(
                        name, startLine, endLine
                    )
                );
            }
        }
    }

    /**
     * Parses module boundary markers in the given CSS file and writes a manifest file.
     *
     * @param in  the path to the input CSS file (containing module markers)
     * @param out the path to the output manifest file
     * @throws IllegalStateException if module markers are nested, mismatched, overlapping, or left unclosed
     * @throws IOException           if an I/O error occurs during processing
     * @see StylesheetURLHandler
     */
    public void generateManifest(Path in, Path out) throws IOException {
        List<CSSModule> modules = findModulesForManifest(in);

        try (BufferedWriter writer = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            String header = "#" + String.join(",", modules.stream().map(CSSModule::name).toList());
            writer.write(header);
            writer.newLine();

            for (CSSModule module : modules) {
                writer.write("%s:%d:%d".formatted(module.name(), module.startLine(), module.endLine()));
                writer.newLine();
            }
        }
    }

    private List<CSSModule> findModulesForManifest(Path in) throws IOException {
        var modules = new ArrayList<CSSModule>();

        String currentModule = null;
        long currentStart = -1;
        long previousEnd = -1;

        try (BufferedReader reader = Files.newBufferedReader(in, StandardCharsets.UTF_8)) {
            String line;
            long lineNumber = -1;

            while ((line = reader.readLine()) != null) {
                lineNumber++; // line number is 0-based

                var matcher = MODULE_MARKER_PATTERN.matcher(line);
                if (matcher.find()) {
                    String module = matcher.group(1);
                    String action = matcher.group(2);

                    if ("start".equals(action)) {
                        // illegal nesting
                        if (currentModule != null) {
                            throw new IllegalStateException(
                                "Error at line %d: found start marker for '%s', but module '%s' is not closed."
                                    .formatted(lineNumber, module, currentModule)
                            );
                        }

                        currentModule = module;
                        currentStart = lineNumber + 1;
                    } else if ("end".equals(action)) {
                        // orphan end marker
                        if (currentModule == null) {
                            throw new IllegalStateException(
                                "Error at line %d: found end marker for '%s', but no module was opened."
                                    .formatted(lineNumber, module)
                            );
                        }

                        // mismatched module name
                        if (!currentModule.equals(module)) {
                            throw new IllegalStateException(
                                "Error at line %d: expected end marker for '%s', but found '%s'."
                                    .formatted(lineNumber, currentModule, module)
                            );
                        }

                        long currentEnd = lineNumber - 1;

                        // check for overlaps
                        if (currentStart <= previousEnd) {
                            throw new IllegalStateException(
                                "Module '%s' overlaps with the previous module (start: %d, previous end: %d)."
                                    .formatted(module, currentStart, previousEnd)
                            );
                        }

                        modules.add(new CSSModule(module, currentStart, currentEnd));

                        // reset state for the next module
                        previousEnd = currentEnd;
                        currentModule = null;
                        currentStart = -1;
                    }
                }
            }

            // ensure all opened modules were properly closed
            if (currentModule != null) {
                throw new IllegalStateException(
                    "Error: module '%s' was opened but never closed before EOF.".formatted(currentModule)
                );
            }
        }

        return modules;
    }
    //endregion
}
