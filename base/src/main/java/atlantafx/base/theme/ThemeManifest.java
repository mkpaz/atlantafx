/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import atlantafx.base.util.Range;
import org.jspecify.annotations.Nullable;
import us.hebi.graalvm.reachability.annotations.Reachable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Represents a theme manifest containing module line mappings and custom theme variables.
 *
 * <p>The manifest file uses an INI-like format with sections. Lines starting with {@code #} or {@code ;}
 * are treated as comments and ignored.
 *
 * <p>Supported sections:
 * <ul>
 * <li>{@code [modules]}: Maps module names to line ranges in the format {@code moduleName=start:end},
 *     where {@code start} and {@code end} are 0-based inclusive line numbers.
 * <li>{@code [variables]}: Contains arbitrary key-value pairs in the format {@code key=value}.
 * </ul>
 *
 * <p>Example manifest:
 * <pre>{@code
 * # manifest for styles.css
 * [modules]
 * root=0:128
 * button=130:250
 * tooltip=252:290
 *
 * [variables]
 * primaryColor=#3b82f6
 * fontSize=14px
 * }</pre>
 */
@Reachable(resources = "*.manifest")
public class ThemeManifest {

    protected static final String SECTION_MODULES = "[modules]";
    protected static final String SECTION_VARIABLES = "[variables]";

    protected SortedMap<String, Range> modules = Collections.emptySortedMap();
    protected SortedMap<String, Object> variables = Collections.emptySortedMap();

    /**
     * Constructs a new, empty {@code ThemeManifest}.
     */
    public ThemeManifest() {
        // default constructor
    }

    /**
     * Returns an unmodifiable sorted map of modules.
     *
     * @return an unmodifiable {@link SortedMap} containing module names as keys
     *         and their line ranges as values
     */
    public SortedMap<String, Range> getModules() {
        return modules;
    }

    /**
     * Sets the module mappings for this manifest.
     *
     * @param modules a map of module names to their line ranges, or {@code null} to clear modules
     */
    public void setModules(@Nullable Map<String, Range> modules) {
        if (modules == null || modules.isEmpty()) {
            this.modules = Collections.emptySortedMap();
            return;
        }

        Comparator<String> rangeComparator = Comparator
            .comparing((String k) -> modules.get(k))
            .thenComparing(k -> k);

        SortedMap<String, Range> sorted = new TreeMap<>(rangeComparator);
        sorted.putAll(modules);
        this.modules = Collections.unmodifiableSortedMap(sorted);
    }

    /**
     * Returns an unmodifiable sorted map of theme variables.
     *
     * @return an unmodifiable {@link SortedMap} containing variable keys and their values
     */
    public SortedMap<String, Object> getVariables() {
        return variables;
    }

    /**
     * Sets the theme variables for this manifest.
     *
     * @param variables a map of key-value variable pairs, or {@code null} to clear variables
     */
    public void setVariables(@Nullable Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            this.variables = Collections.emptySortedMap();
            return;
        }

        SortedMap<String, Object> sorted = new TreeMap<>(variables);
        this.variables = Collections.unmodifiableSortedMap(sorted);
    }

    /**
     * Returns the line range for the specified module name.
     *
     * @param name the name of the module to retrieve
     * @return the {@link Range} associated with the given module name, or {@code null} if the module does not exist
     */
    public @Nullable Range getModule(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return modules.get(name);
    }

    /**
     * Returns the value of the specified theme variable.
     *
     * @param key the name of the variable to retrieve
     * @return the value associated with the given key, or {@code null} if the variable does not exist
     */
    public @Nullable Object getVariable(@Nullable String key) {
        if (key == null) {
            return null;
        }
        return variables.get(key);
    }

    @Override
    public String toString() {
        return "ThemeManifest{"
            + "modules=" + modules
            + ", variables=" + variables
            + '}';
    }

    /**
     * Reads and parses a theme manifest from the specified input stream.
     *
     * @param is the input stream to read from
     * @throws IOException if an I/O error occurs during processing
     */
    public void load(InputStream is) throws IOException {
        Objects.requireNonNull(is, "InputStream must not be null");

        var modules = new HashMap<String, Range>();
        var variables = new HashMap<String, Object>();

        try (var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String section = "";
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                    continue;
                }

                // section headers
                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line.toLowerCase();
                    continue;
                }

                // parse key-value pairs
                int sepPos = line.indexOf('=');
                if (sepPos <= 0) {
                    continue; // ignore lines without key=value structure
                }

                String key = line.substring(0, sepPos).trim();
                String value = line.substring(sepPos + 1).trim();

                switch (section) {
                    case SECTION_MODULES -> {
                        String[] parts = value.split(":");
                        if (parts.length == 2) {
                            try {
                                int start = Integer.parseInt(parts[0].trim());
                                int end = Integer.parseInt(parts[1].trim());
                                modules.put(key, new Range(start, end));
                            } catch (NumberFormatException ignored) {
                                // ignore malformed numeric ranges
                            }
                        }
                    }
                    case SECTION_VARIABLES -> variables.put(key, value);
                    default -> {
                        // ignore properties outside of known sections
                    }
                }
            }
        }

        setModules(modules);
        setVariables(variables);
    }

    /**
     * Writes the manifest to the specified output stream.
     *
     * @param os the output stream to write to
     * @throws IOException if an I/O error occurs
     */
    public void save(OutputStream os) throws IOException {
        Objects.requireNonNull(os, "OutputStream must not be null");

        try (var writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            boolean needsSeparator = false;

            // empty sections are omitted
            if (!modules.isEmpty()) {
                writer.write(SECTION_MODULES);
                writer.newLine();

                for (var entry : modules.entrySet()) {
                    Range range = entry.getValue();
                    writer.write("%s=%d:%d".formatted(entry.getKey(), range.start(), range.end()));
                    writer.newLine();
                }
                needsSeparator = true;
            }

            if (!variables.isEmpty()) {
                if (needsSeparator) {
                    writer.newLine();
                }

                writer.write(SECTION_VARIABLES);
                writer.newLine();

                for (var entry : variables.entrySet()) {
                    writer.write("%s=%s".formatted(entry.getKey(), entry.getValue()));
                    writer.newLine();
                }
            }

            writer.flush();
        }
    }
}
