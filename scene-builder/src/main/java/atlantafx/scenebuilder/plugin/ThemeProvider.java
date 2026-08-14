/* SPDX-License-Identifier: MIT */

package atlantafx.scenebuilder.plugin;

import atlantafx.base.theme.*;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.editor.ExternalThemeProvider;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public final class ThemeProvider implements ExternalThemeProvider {

    private static final String THEME_ID_PREFIX = "ATLANTAFX_";
    private static final List<Theme> THEMES = List.of(
        new PrimerLight(), new PrimerDark(),
        new NordLight(), new NordDark(),
        new CupertinoLight(), new CupertinoDark(),
        new Dracula()
    );
    private static final List<EditorPlatform.Theme> EDITOR_THEMES = THEMES.stream()
        .map(ThemeProvider::createTheme)
        .toList();

    public ThemeProvider() {
        // Agent prevents leaking Modena and Gluon stylesheets in the Workspace
        // and the Preview while an AtlantaFX theme is active.
        var agent = new Agent();
        Platform.runLater(agent::watch);
    }

    @Override
    public List<EditorPlatform.Theme> getExternalThemes() {
        return EDITOR_THEMES;
    }

    @Override
    public List<String> getExternalStylesheets() {
        return List.of();
    }

    @Override
    public boolean hasClassFromExternalPlugin(@Nullable String text) {
        // If you do this (which is supposed to be correct), Scene Builder
        // will show an unclickable alert suggesting you enable the Gluon theme ¯\_(ツ)_/¯.
        // It also blocks the document window and can only be closed with Alt+F4.
        // return text != null && !text.isBlank()
        //    && (text.startsWith("atlantafx") || text.contains("atlantafx"));
        return false;
    }

    @Override
    public void showThemeAlert(Stage owner, EditorPlatform.Theme currentTheme,
                               Consumer<EditorPlatform.Theme> onSuccess) {
    }

    @Override
    public void showImportAlert(Stage owner) {
    }

    @Override
    public String getExternalJavadocURL() {
        return "https://mkpaz.github.io/atlantafx/apidocs/atlantafx.base/module-summary.html";
    }

    public static List<Theme> getThemeList() {
        return List.copyOf(THEMES);
    }

    public static List<EditorPlatform.Theme> getEditorThemeList() {
        return List.copyOf(EDITOR_THEMES);
    }

    public static boolean isAtlantaFXTheme(EditorPlatform.@Nullable Theme theme) {
        return theme != null && theme.name() != null && theme.name().startsWith(THEME_ID_PREFIX);
    }

    public static boolean isAtlantaFXStyleSheet(String url) {
        return getThemeList().stream().anyMatch(t -> Objects.equals(t.getUserAgentStylesheet(), url));
    }

    //*************************************************************************

    static EditorPlatform.Theme createTheme(Theme delegate) {
        String id = delegate.getName().replaceAll(" ", "_").toUpperCase(Locale.ROOT);
        return new EditorPlatform.Theme(
            THEME_ID_PREFIX + id,
            delegate.getName(),
            delegate.getUserAgentStylesheet()
        );
    }
}