package atlantafx.base.theme;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ThemeTest {

    @Test
    void testServiceLoader() {
        ServiceLoader<Theme> loader = ServiceLoader.load(Theme.class);

        List<Theme> themes = loader.stream()
            .map(ServiceLoader.Provider::get)
            .toList();

        assertThat(themes).hasSize(7);
        assertThat(themes).hasExactlyElementsOfTypes(
            PrimerLight.class, PrimerDark.class,
            NordLight.class, NordDark.class,
            CupertinoLight.class, CupertinoDark.class,
            Dracula.class
        );
    }

    @Test
    @DisplayName("should append modules query parameter including core modules")
    void testAppendModulesToPlainPath() {
        var theme = Theme.of("test", "/theme.css", false);
        String result = theme.getUserAgentStylesheet(Set.of("button", "tooltip"));

        assertTrue(result.startsWith("stylesheet:/theme.css?modules="));

        List<String> modules = extractModules(result);
        assertThat(modules).containsExactlyInAnyOrder("button", "tooltip", "root", "text");
    }

    @Test
    @DisplayName("should prepend ampersand when path already has query params")
    void testAppendModulesWhenQueryParamsAlreadyExist() {
        var theme = Theme.of("test", "/theme.css?version=1.0", false);
        String result = theme.getUserAgentStylesheet(Set.of("button", "tooltip"));

        assertTrue(result.startsWith("stylesheet:/theme.css?version=1.0&modules="));

        List<String> modules = extractModules(result);
        assertThat(modules).containsExactlyInAnyOrder("button", "tooltip", "root", "text");
    }

    @Test
    @DisplayName("should not duplicate core modules if already present in requested set")
    void testDoNotDuplicateCoreModules() {
        var theme = Theme.of("test", "/theme.css", false);
        String result = theme.getUserAgentStylesheet(Set.of("root", "text", "button"));

        List<String> modules = extractModules(result);
        assertEquals(3, modules.size(), "Should contain exactly 3 modules without duplicates");
        assertThat(modules).containsExactlyInAnyOrder("root", "text", "button");
    }

    @Test
    @DisplayName("should return base stylesheet as-is when modules list is empty")
    void testReturnBaseWhenModulesListIsEmpty() {
        var theme = Theme.of("test", "/theme.css", false);
        String result = theme.getUserAgentStylesheet(Collections.emptySet());
        assertEquals("/theme.css", result);
    }

    @Test
    @DisplayName("should return base stylesheet as-is when modules list is null")
    void testReturnBaseWhenModulesListIsNull() {
        var theme = Theme.of("test", "/theme.css", false);
        String result = theme.getUserAgentStylesheet(null);
        assertEquals("/theme.css", result);
    }

    @Test
    @DisplayName("should throw exception when base stylesheet is blank")
    void testThrowExceptionWhenBaseStylesheetIsBlank() {
        var theme = Theme.of("test", "   ", false);
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> theme.getUserAgentStylesheet(Set.of("button"))
        );
        assertEquals("Theme stylesheet cannot be null or blank.", exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when modules param already present")
    void testThrowExceptionWhenModulesParamAlreadyPresent() {
        var theme = Theme.of("test", "/theme.css?modules=card", false);
        assertThrows(
            IllegalArgumentException.class,
            () -> theme.getUserAgentStylesheet(Set.of("button"))
        );
    }

    @Test
    @DisplayName("should normalize missing scheme and leading slash")
    void testNormalizeMissingSchemeAndLeadingSlash() {
        var theme = Theme.of("test", "theme.css", false);
        String result = theme.getUserAgentStylesheet(Set.of("tooltip"));

        assertTrue(result.startsWith("stylesheet:/theme.css?modules="));

        List<String> modules = extractModules(result);
        assertThat(modules).containsExactlyInAnyOrder("tooltip", "root", "text");
    }

    private static List<String> extractModules(String uri) {
        int index = uri.indexOf("modules=");
        if (index == -1) {
            return List.of();
        }

        String modulesQuery = uri.substring(index + 8);
        int argSeparator = modulesQuery.indexOf('&');
        if (argSeparator != -1) {
            modulesQuery = modulesQuery.substring(0, argSeparator);
        }

        return Arrays.asList(modulesQuery.split(","));
    }
}