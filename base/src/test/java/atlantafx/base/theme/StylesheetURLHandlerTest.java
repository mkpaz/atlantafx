package atlantafx.base.theme;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class StylesheetURLHandlerTest {

    private StylesheetURLHandler handler;

    @BeforeEach
    void setup() {
        handler = new StylesheetURLHandler();
    }

    @Test
    @DisplayName("should exclude modules missing in manifest")
    void testMissingModulesExcluded() throws Exception {
        URL url = URL.of(new URI("stylesheet:/theme-modules/foo-theme.css?modules=tooltip,unknown"), handler);

        URLConnection connection = handler.openConnection(url);
        InputStream inputStream = connection.getInputStream();
        String css = new String(inputStream.readAllBytes(), UTF_8);

        // requested 'tooltip' module content is loaded
        assertTrue(css.contains(".tooltip"), "CSS should contain content for requested 'tooltip' module");
        assertTrue(css.contains("-fx-background-color: -color-border-default, -color-bg-overlay;"));

        // not requested 'button' module and missing 'unknown' module are excluded
        assertFalse(css.contains(".button"), "CSS should not contain 'button' module content");
        assertFalse(css.contains("unknown"), "CSS should not contain non-existent module data");
    }

    @Test
    @DisplayName("should fallback to full CSS when no modules specified")
    void testNoModulesSpecifiedFallback() throws Exception {
        URL url = URL.of(new URI("stylesheet:/theme-modules/foo-theme.css"), handler);

        URLConnection connection = handler.openConnection(url);
        String css = new String(connection.getInputStream().readAllBytes(), UTF_8);

        // full file including all modules and markers
        assertTrue(css.contains(".button"));
        assertTrue(css.contains(".tooltip"));
        assertTrue(css.contains("/*! @module:tooltip:start */"));
    }

    @Test
    @DisplayName("should fallback to full CSS when manifest is missing")
    void testManifestMissingFallback() throws Exception {
        URL url = URL.of(new URI("stylesheet:/theme-modules/foo-no-manifest.css?modules=tooltip"), handler);

        URLConnection connection = handler.openConnection(url);
        String css = new String(connection.getInputStream().readAllBytes(), UTF_8);

        assertTrue(css.contains(".button"));
        assertTrue(css.contains(".tooltip"));
        assertTrue(css.contains("/*! @module:tooltip:start */"));
    }

    @Test
    @DisplayName("should return text/css content type")
    void testContentTypeTextCss() throws Exception {
        URL url = URL.of(new URI("stylesheet:/theme-modules/foo-theme.css"), handler);
        URLConnection connection = handler.openConnection(url);
        assertEquals("text/css", connection.getContentType());
    }

    @Test
    @DisplayName("should handle empty modules query paramet")
    void testEmptyModulesQueryParamFallback() throws Exception {
        URL url = URL.of(new URI("stylesheet:/theme-modules/foo-theme.css?modules="), handler);

        URLConnection connection = handler.openConnection(url);
        String css = new String(connection.getInputStream().readAllBytes(), UTF_8);

        assertTrue(css.contains(".button"));
        assertTrue(css.contains(".tooltip"));
    }
}