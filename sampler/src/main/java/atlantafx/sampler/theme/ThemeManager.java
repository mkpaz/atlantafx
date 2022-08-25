/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.theme;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import atlantafx.sampler.Launcher;
import atlantafx.sampler.Resources;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Scene;

import java.net.URI;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class ThemeManager {

    private static final String DUMMY_STYLESHEET = Resources.getResource("assets/styles/empty.css").toString();
    private static final PseudoClass USER_CUSTOM = PseudoClass.getPseudoClass("user-custom");

    // KEY           |  VALUE
    // -fx-property  |  value;
    private final Map<String, String> customCSSDeclarations = new LinkedHashMap<>();
    // .foo          |  -fx-property: value;
    private final Map<String, String> customCSSRules = new LinkedHashMap<>();

    private Theme currentTheme = null;
    private int currentFontSize = 14;
    private Scene scene;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Theme getTheme() {
        return currentTheme;
    }

    /**
     * Resets user agent stylesheet and then adds {@link Theme} styles to the {@link Scene}
     * stylesheets. This is necessary when we want to reload style changes at runtime, because
     * CSSFX doesn't monitor user agent stylesheet.
     * Also, some styles aren't applied when using {@link  Application#setUserAgentStylesheet(String)} ).
     * E.g. JavaFX ignores Ikonli -fx-icon-color and -fx-icon-size properties, but for an unknown
     * reason they won't be ignored when exactly the same stylesheet is set via {@link Scene#getStylesheets()}.
     */
    public void setTheme(Theme theme) {
        Objects.requireNonNull(scene);
        Objects.requireNonNull(theme);

        Application.setUserAgentStylesheet(Objects.requireNonNull(theme.getUserAgentStylesheet()));

        if (currentTheme != null) {
            scene.getStylesheets().removeIf(url -> currentTheme.getStylesheets().contains(URI.create(url)));
        }

        theme.getStylesheets().forEach(uri -> scene.getStylesheets().add(uri.toString()));
        currentTheme = theme;
    }

    public List<Theme> getAvailableThemes() {
        var themes = new ArrayList<Theme>();
        var appStylesheets = new URI[] {
                URI.create(Resources.resolve("assets/fonts/index.css")),
                URI.create(Resources.resolve("assets/styles/index.css"))
        };

        if (Launcher.IS_DEV_MODE) {
            themes.add(new ExternalTheme("Primer Light", DUMMY_STYLESHEET, merge(
                    Resources.getResource("theme-test/primer-light.css"),
                    appStylesheets
            ), false));
            themes.add(new ExternalTheme("Primer Dark", DUMMY_STYLESHEET, merge(
                    Resources.getResource("theme-test/primer-dark.css"),
                    appStylesheets
            ), true));
            themes.add(new ExternalTheme("Nord Light", DUMMY_STYLESHEET, merge(
                    Resources.getResource("theme-test/nord-light.css"),
                    appStylesheets
            ), false));
            themes.add(new ExternalTheme("Nord Dark", DUMMY_STYLESHEET, merge(
                    Resources.getResource("theme-test/nord-dark.css"),
                    appStylesheets
            ), true));
        } else {
            themes.add(new PrimerLight(appStylesheets));
            themes.add(new PrimerDark(appStylesheets));
        }
        return themes;
    }

    public int getFontSize() {
        return currentFontSize;
    }

    public void setFontSize(int fontSize) {
        setCustomDeclaration("-fx-font-size", fontSize + "px");
        setCustomRule(".ikonli-font-icon", String.format("-fx-icon-size: %dpx;", fontSize + 2));
        currentFontSize = fontSize;
    }

    private void setCustomDeclaration(String property, String value) {
        customCSSDeclarations.put(property, value);
    }

    private void setCustomRule(String selector, String rule) {
        customCSSRules.put(selector, rule);
    }

    public void reloadCustomCSS() {
        Objects.requireNonNull(scene);
        StringBuilder css = new StringBuilder();

        css.append(".root:");
        css.append(USER_CUSTOM.getPseudoClassName());
        css.append(" {\n");
        customCSSDeclarations.forEach((k, v) -> {
            css.append("\t");
            css.append(k);
            css.append(":\s");
            css.append(v);
            css.append(";\n");
        });
        css.append("}\n");

        customCSSRules.forEach((k, v) -> {
            css.append(".root:");
            css.append(USER_CUSTOM.getPseudoClassName());
            css.append(" ");
            css.append(k);
            css.append(" {");
            css.append(v);
            css.append("}\n");
        });

        System.out.println(css);

        scene.getStylesheets().removeIf(uri -> uri.startsWith("data:text/css"));
        scene.getStylesheets().add(
                "data:text/css;base64," + Base64.getEncoder().encodeToString(css.toString().getBytes(UTF_8))
        );
        scene.getRoot().pseudoClassStateChanged(USER_CUSTOM, true);
    }

    public void resetCustomCSS() {
        customCSSDeclarations.clear();
        customCSSRules.clear();
        scene.getRoot().pseudoClassStateChanged(USER_CUSTOM, false);
    }

    public HighlightJSTheme getMatchingHighlightJSTheme(Theme theme) {
        Objects.requireNonNull(theme);
        if ("Nord Light".equals(theme.getName())) { return HighlightJSTheme.nordLight(); }
        if ("Nord Dark".equals(theme.getName())) { return HighlightJSTheme.nordDark(); }
        return theme.isDarkMode() ? HighlightJSTheme.githubDark() : HighlightJSTheme.githubLight();
    }

    @SafeVarargs
    private <T> Set<T> merge(T first, T... arr) {
        var set = new LinkedHashSet<T>();
        set.add(first);
        Collections.addAll(set, arr);
        return set;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Singleton                                                             //
    ///////////////////////////////////////////////////////////////////////////

    private ThemeManager() { }

    private static class InstanceHolder {

        private static final ThemeManager INSTANCE = new ThemeManager();
    }

    public static ThemeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
