/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import atlantafx.base.util.Range;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A custom {@link URLStreamHandler} that intercepts URLs using the {@value #SCHEME} scheme
 * to support modular CSS stylesheet loading.
 *
 * <p>The handler processes URIs formatted as {@code stylesheet:/path/to/theme.css?modules=mod1,mod2}.
 * When a request contains a {@code modules} query parameter, the handler looks for an accompanying
 * manifest file (e.g., {@code theme.manifest}) alongside the base CSS file to load and serve
 * only the specified style modules.
 *
 * <p>If the {@code modules} parameter is omitted, the manifest file is missing, or an error occurs
 * during module extraction, the handler falls back to reading and returning the entire CSS file.
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // loads only the 'button' and 'tooltip' modules from foo.css
 * String url = "stylesheet:/foo.css?modules=button,tooltip";
 * Application.setUserAgentStylesheet(url);
 * }</pre>
 *
 * <h3>Manifest Format</h3>
 *
 * <p>The manifest file must be located in the same directory as the target stylesheet, sharing
 * the same base name with a {@code .manifest} extension (e.g., {@code foo.manifest} for {@code foo.css}).
 *
 * <p>To generate a manifest, the CSS file is marked up with comment markers in the following format:
 * <pre>
 * /*! @module:moduleName:start *&#47;
 * ... CSS rules ...
 * /*! @module:moduleName:end *&#47;
 * </pre>
 *
 * <p>The manifest contains line mapping directives formatted as {@code moduleName:start:end},
 * where {@code start} and {@code end} specify the 0-based inclusive line ranges within the CSS file.
 * Lines starting with {@code #} are treated as comments and ignored:
 *
 * <pre>{@code
 * # manifest for foo.css
 * root:0:128
 * button:130:250
 * tooltip:252:290
 * }</pre>
 *
 * @see PostCSS#generateManifest(Path, Path)
 */
public final class StylesheetURLHandler extends URLStreamHandler {

    private static final System.Logger LOGGER = System.getLogger(StylesheetURLHandler.class.getName());

    public static final String SCHEME = "stylesheet";

    @Override
    protected URLConnection openConnection(URL u) {
        return new URLConnection(u) {

            @Override
            public void connect() {
                // not required for local files
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(loadStylesheet(url));
            }

            @Override
            public String getContentType() {
                return "text/css";
            }
        };
    }

    private byte[] loadStylesheet(URL url) {
        String stylesheetPath = url.getPath();
        Supplier<byte[]> fallback = () -> readCSSFile(stylesheetPath);

        // validate 'modules' query param
        Map<String, String> query = parseQuery(url.getQuery());
        String modulesParam = query.get("modules");

        if (modulesParam == null || modulesParam.isBlank()) {
            LOGGER.log(Level.ERROR, "No 'modules' query param specified for URL: {0}", url);
            return fallback.get();
        }

        Set<String> modules = Arrays.stream(modulesParam.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        if (modules.isEmpty()) {
            LOGGER.log(Level.ERROR, "Parsed 'modules' list is empty for URL: {0}", url);
            return fallback.get();
        }

        // read the manifest file
        String manifestPath = getManifestPath(stylesheetPath);

        Map<String, Range> manifest;
        try {
            manifest = loadManifest(manifestPath);
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Failed to parse manifest file: " + manifestPath, e);
            return fallback.get();
        }

        if (manifest.isEmpty()) {
            LOGGER.log(Level.WARNING, "Manifest was not read or is empty");
            return fallback.get();
        }

        // retain only requested modules
        manifest.keySet().retainAll(modules);

        if (manifest.isEmpty()) {
            LOGGER.log(Level.ERROR, "None of the requested modules {0} were found in manifest: {1}",
                modules, manifestPath
            );
            return fallback.get();
        }

        // read selected line ranges from the main CSS file
        try {
            return readCSSFileRanges(stylesheetPath, manifest.values());
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Error reading CSS ranges from " + stylesheetPath, e);
            return fallback.get();
        }
    }

    private String getManifestPath(String stylesheetPath) {
        int dotIndex = stylesheetPath.lastIndexOf('.');
        if (dotIndex != -1) {
            return stylesheetPath.substring(0, dotIndex) + ".manifest";
        }
        return stylesheetPath + ".manifest";
    }

    private Map<String, Range> loadManifest(String path) throws IOException {
        var manifest = new HashMap<String, Range>();

        try (InputStream is = getResourceAsStream(path)) {
            if (is == null) {
                LOGGER.log(Level.ERROR, "Manifest file does not exist: {0}", path);
                return manifest;
            }

            try (var reader = new BufferedReader(new InputStreamReader(is, UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue; // ignore empty lines and comments
                    }

                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        String module = parts[0].trim();
                        int start = Integer.parseInt(parts[1].trim());
                        int end = Integer.parseInt(parts[2].trim());
                        manifest.put(module, new Range(start, end));
                    }
                }
            }
        }

        return manifest;
    }

    private byte[] readCSSFileRanges(String path, Collection<Range> ranges) throws IOException {
        List<Range> sortedRanges = ranges.stream()
            .sorted(Comparator.comparingInt(Range::start))
            .toList();

        var builder = new StringBuilder();

        try (InputStream is = getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8))) {
                String line;
                int currentLine = 0; // line number is 0-based

                for (Range range : sortedRanges) {
                    while (currentLine < range.start() && reader.readLine() != null) {
                        currentLine++;
                    }

                    // read lines inside the range [start, end] (inclusive)
                    while (currentLine <= range.end() && (line = reader.readLine()) != null) {
                        builder.append(line).append('\n');
                        currentLine++;
                    }
                }
            }
        }

        return builder.toString().getBytes(UTF_8);
    }

    private byte[] readCSSFile(String path) {
        try (InputStream is = getResourceAsStream(path)) {
            if (is != null) {
                return is.readAllBytes();
            }
            LOGGER.log(Level.ERROR, "CSS file not found: {0}", path);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Failed to read CSS file: " + path, e);
        }

        return new byte[0];
    }

    private Map<String, String> parseQuery(@Nullable String query) {
        if (query == null || query.isBlank()) {
            return Map.of();
        }

        var result = new HashMap<String, String>();
        for (var param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2) {
                result.put(
                    URLDecoder.decode(kv[0], UTF_8),
                    URLDecoder.decode(kv[1], UTF_8)
                );
            }
        }

        return result;
    }

    private @Nullable InputStream getResourceAsStream(String path) {
        if (path.isBlank()) {
            return null;
        }

        String absPath = path.startsWith("/") ? path : "/" + path;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            InputStream is = cl.getResourceAsStream(absPath.substring(1));
            if (is != null) {
                return is;
            }
        }

        cl = StylesheetURLHandler.class.getClassLoader();
        if (cl != null) {
            return cl.getResourceAsStream(absPath.substring(1));
        }

        return ClassLoader.getSystemResourceAsStream(absPath.substring(1));
    }
}