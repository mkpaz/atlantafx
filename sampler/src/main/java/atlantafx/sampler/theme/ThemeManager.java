/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.theme;

import atlantafx.sampler.Resources;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import atlantafx.sampler.Launcher;
import javafx.application.Application;
import javafx.scene.Scene;

import java.net.URI;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class ThemeManager {

    private static final String DUMMY_STYLESHEET = Resources.getResource("assets/styles/empty.css").toString();

    private Theme currentTheme = null;
    private int currentFontSize = 14;

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
    public void setTheme(Scene scene, Theme theme) {
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
        } else {
            themes.add(new PrimerLight(appStylesheets));
            themes.add(new PrimerDark(appStylesheets));
        }
        return themes;
    }

    public int getFontSize() {
        return currentFontSize;
    }

    public void setFontSize(Scene scene, int fontSize) {
        String css = String.format(".root { -fx-font-size: %dpx; } .ikonli-font-icon { -fx-icon-size: %dpx; }",
                                   fontSize,
                                   fontSize + 2
        );
        scene.getStylesheets().removeIf(uri -> uri.startsWith("data:text/css"));
        scene.getStylesheets().add(
                "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(UTF_8))
        );

        currentFontSize = fontSize;
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
