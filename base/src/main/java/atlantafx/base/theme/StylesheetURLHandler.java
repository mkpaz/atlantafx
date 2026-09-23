/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import atlantafx.base.util.Range;
import atlantafx.base.util.Resources;
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
 * <p>The manifest file MUST be located in the same directory as the target stylesheet, sharing
 * the same file name with a {@code .manifest} extension (e.g., {@code foo.css.manifest} for {@code foo.css}).
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
 * @see ThemeManifest
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
        String manifestPath = stylesheetPath + ".manifest";
        var manifest = new ThemeManifest();

        try (InputStream is = Resources.getResourceAsStream(manifestPath)) {
            if (is == null) {
                LOGGER.log(Level.ERROR, "Manifest file does not exist: {0}", manifestPath);
                return fallback.get();
            }
            manifest.load(is);
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Failed to parse manifest file: " + manifestPath, e);
            return fallback.get();
        }

        // retain only requested modules
        Map<String, Range> moduleRanges = new HashMap<>(manifest.getModules());
        if (moduleRanges.isEmpty()) {
            LOGGER.log(Level.WARNING, "Manifest was not read or contains no modules");
            return fallback.get();
        }
        moduleRanges.keySet().retainAll(modules);

        if (moduleRanges.isEmpty()) {
            LOGGER.log(Level.ERROR, "None of the requested modules {0} were found in manifest: {1}",
                modules, manifestPath
            );
            return fallback.get();
        }

        // read selected line ranges from the CSS file
        try {
            return readCSSFileRanges(stylesheetPath, moduleRanges.values());
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Error reading CSS ranges from " + stylesheetPath, e);
            return fallback.get();
        }
    }

    private byte[] readCSSFileRanges(String path, Collection<Range> ranges) throws IOException {
        List<Range> sortedRanges = ranges.stream()
            .sorted(Comparator.comparingInt(Range::start))
            .toList();

        var builder = new StringBuilder();

        try (InputStream is = Resources.getResourceAsStream(path)) {
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
        try (InputStream is = Resources.getResourceAsStream(path)) {
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
}