package atlantafx.base.theme;

import atlantafx.base.util.Range;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class ThemeManifestTest {

    @Test
    @DisplayName("should load valid manifest")
    void shouldLoadValidManifest() throws IOException {
        String content = """
            # manifest description
            ; another style comment
            
            [modules]
            root=0:128
            button=130:250
            
            [variables]
            primaryColor=#3b82f6
            fontSize=14
            """;

        var manifest = new ThemeManifest();
        try (InputStream is = toInputStream(content)) {
            manifest.load(is);
        }

        assertThat(manifest.getModules())
            .hasSize(2)
            .containsEntry("root", new Range(0, 128))
            .containsEntry("button", new Range(130, 250));

        assertThat(manifest.getVariables())
            .hasSize(2)
            .containsEntry("primaryColor", "#3b82f6")
            .containsEntry("fontSize", "14");
    }

    @Test
    @DisplayName("should ignore unknown sections and malformed lines")
    void shouldIgnoreUnknownSectionsAndMalformedLines() throws IOException {
        String content = """
            [unknown_section]
            someKey=someValue
            
            [modules]
            root=0:100
            invalidRange=abc:def
            missingDelimiter=100
            
            [variables]
            validVar=123
            lineWithoutEquals
            """;

        var manifest = new ThemeManifest();
        try (InputStream is = toInputStream(content)) {
            manifest.load(is);
        }

        assertThat(manifest.getModules())
            .hasSize(1)
            .containsEntry("root", new Range(0, 100));

        assertThat(manifest.getVariables())
            .hasSize(1)
            .containsEntry("validVar", "123");
    }

    @Test
    @DisplayName("should save non-empty sections")
    void shouldSaveManifest() throws IOException {
        var manifest = new ThemeManifest();
        manifest.setModules(Map.of(
            "root", new Range(0, 100),
            "button", new Range(101, 200)
        ));
        manifest.setVariables(Map.of(
            "theme", "dark",
            "accent", "#ff0000"
        ));

        var os = new ByteArrayOutputStream();
        manifest.save(os);
        String result = os.toString(StandardCharsets.UTF_8);

        assertThat(result)
            .contains("[modules]")
            .contains("root=0:100")
            .contains("button=101:200")
            .contains("[variables]")
            .contains("theme=dark")
            .contains("accent=#ff0000");
    }

    @Test
    @DisplayName("should omit empty sections")
    void shouldOmitEmptySectionsWhenSaving() throws IOException {
        var manifest = new ThemeManifest();
        manifest.setVariables(Map.of("key", "value"));

        var os = new ByteArrayOutputStream();
        manifest.save(os);
        String result = os.toString(StandardCharsets.UTF_8);

        assertThat(result)
            .doesNotContain("[modules]")
            .contains("[variables]")
            .contains("key=value");
    }

    @Test
    @DisplayName("should save empty manifest")
    void shouldSaveEmptyManifest() throws IOException {
        var manifest = new ThemeManifest();

        var os = new ByteArrayOutputStream();
        manifest.save(os);
        String result = os.toString(StandardCharsets.UTF_8);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should preserve integrity through a save-then-load")
    void shouldPreserveIntegrity(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("styles.manifest");

        var original = new ThemeManifest();
        original.setModules(Map.of("button", new Range(10, 50)));
        original.setVariables(Map.of("padding", "8"));

        try (var os = Files.newOutputStream(file)) {
            original.save(os);
        }

        var restored = new ThemeManifest();
        try (var is = Files.newInputStream(file)) {
            restored.load(is);
        }

        assertThat(restored.getModules()).isEqualTo(original.getModules());
        assertThat(restored.getVariables()).isEqualTo(original.getVariables());
    }

    //*************************************************************************

    private InputStream toInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }
}