/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.theme;

import static atlantafx.sampler.Launcher.IS_DEV_MODE;
import static atlantafx.sampler.Resources.resolve;
import static atlantafx.sampler.theme.ThemeManager.APP_STYLESHEETS;
import static atlantafx.sampler.theme.ThemeManager.DUMMY_STYLESHEET;
import static atlantafx.sampler.theme.ThemeManager.PROJECT_THEMES;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import atlantafx.base.theme.Theme;
import atlantafx.sampler.FileResource;
import atlantafx.sampler.Launcher;
import atlantafx.sampler.Resources;
import fr.brouillard.oss.cssfx.CSSFX;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.scene.Scene;

/**
 * The {@link Theme} decorator to work around some JavaFX CSS limitations.
 * <br/><br/>
 * JavaFX doesn't provide the theme API. Moreover, there is no such notion, it's called
 * "user agent stylesheet". There are two ways to change the platform stylesheet:
 * <ul>
 * <li>{@link Scene#setUserAgentStylesheet(String)}</li>
 * <li>{@link Application#setUserAgentStylesheet(String)}</li>
 * </ul>
 * The first one is observable and the latter is not. Both fail to provide hot reload with
 * {@link CSSFX}. This means that if we'd set up some CSS file as the platform stylesheet
 * and then change (re-compile) this file, nothing will happen.
 * <br/><br/>
 * To work around this issue we use dev mode trick. In this mode, we set the dummy stylesheet
 * to the Application, while real theme CSS is added to the {@link Scene#getStylesheets()}
 * along with app own stylesheet. When CSSFX detects any of those CSS files changes, it forces
 * observable list update and thus reloading CSS with changes.
 * <br/><br/>
 * Also, note that some styles aren't applied when using {@link Application#setUserAgentStylesheet(String)}.
 * E.g. JavaFX ignores Ikonli -fx-icon-color and -fx-icon-size properties, but for an unknown
 * reason they won't be ignored when exactly the same stylesheet is set via {@link Scene#getStylesheets()}.
 * </p>
 */
public final class SamplerTheme implements Theme {

    private static final int PARSE_LIMIT = 250;
    private static final Pattern COLOR_PATTERN =
        Pattern.compile("\s*?(-color-(fg|bg|accent|success|danger)-.+?):\s*?(.+?);");

    private final Theme theme;

    private FileTime lastModified;
    private Map<String, String> colors;

    public SamplerTheme(Theme theme) {
        Objects.requireNonNull(theme);

        if (theme instanceof SamplerTheme) {
            throw new IllegalArgumentException("Sampler theme must not be wrapped into itself.");
        }

        this.theme = theme;
    }

    @Override
    public String getName() {
        return theme.getName();
    }

    // Application.setUserAgentStylesheet() only accepts URL (or URL string representation),
    // any external file path must have "file://" prefix
    @Override
    public String getUserAgentStylesheet() {
        return IS_DEV_MODE ? DUMMY_STYLESHEET : getResource().toURI().toString();
    }

    @Override
    public boolean isDarkMode() {
        return theme.isDarkMode();
    }

    public Set<String> getAllStylesheets() {
        return IS_DEV_MODE ? merge(getResource().toURI().toString(), APP_STYLESHEETS) : Set.of(APP_STYLESHEETS);
    }

    // Checks whether wrapped theme is a project theme or user external theme.
    public boolean isProjectTheme() {
        return PROJECT_THEMES.contains(theme.getClass());
    }

    // Tries to parse theme CSS and extract conventional looked-up colors. There are few limitations:
    // - minified CSS files are not supported
    // - only first PARSE_LIMIT lines will be read
    public Map<String, String> parseColors() throws IOException {
        FileResource file = getResource();
        return file.internal() ? parseColorsForClasspath(file) : parseColorsForFilesystem(file);
    }

    private Map<String, String> parseColors(BufferedReader br) throws IOException {
        Map<String, String> colors = new HashMap<>();

        String line;
        int lineCount = 0;

        while ((line = br.readLine()) != null) {
            Matcher matcher = COLOR_PATTERN.matcher(line);
            if (matcher.matches()) {
                colors.put(matcher.group(1), matcher.group(3));
            }

            lineCount++;
            if (lineCount > PARSE_LIMIT) {
                break;
            }
        }

        return colors;
    }

    private Map<String, String> parseColorsForClasspath(FileResource file) throws IOException {
        // classpath resources are static, no need to parse project theme more than once
        if (colors != null) {
            return colors;
        }

        try (var br = new BufferedReader(new InputStreamReader(file.getInputStream(), UTF_8))) {
            colors = parseColors(br);
        }

        return colors;
    }

    private Map<String, String> parseColorsForFilesystem(FileResource file) throws IOException {
        // return cached colors if file wasn't changed since the last read
        FileTime fileTime = Files.getLastModifiedTime(file.toPath(), NOFOLLOW_LINKS);
        if (Objects.equals(fileTime, lastModified)) {
            return colors;
        }

        try (var br = new BufferedReader(new InputStreamReader(file.getInputStream(), UTF_8))) {
            colors = parseColors(br);
        }

        // don't save time before parsing is finished to avoid
        // remembering operation that might end up with an error
        lastModified = fileTime;

        return colors;
    }

    public String getPath() {
        return getResource().toPath().toString();
    }

    public FileResource getResource() {
        if (!isProjectTheme()) {
            return FileResource.createExternal(theme.getUserAgentStylesheet());
        }

        FileResource classpathTheme = FileResource.createInternal(theme.getUserAgentStylesheet(), Theme.class);
        if (!IS_DEV_MODE) {
            return classpathTheme;
        }

        String filename = classpathTheme.getFilename();

        try {
            FileResource testTheme = FileResource.createInternal(
                Resources.resolve("theme-test/" + filename), Launcher.class
            );
            if (!testTheme.exists()) {
                throw new IOException();
            }
            return testTheme;
        } catch (Exception e) {
            var failedPath = resolve("theme-test/" + filename);
            System.err.println(
                "[WARNING] Unable to find theme file \"" + failedPath + "\". Fall back to the classpath.");
            return classpathTheme;
        }
    }

    public Theme unwrap() {
        return theme;
    }

    @SafeVarargs
    private <T> Set<T> merge(T first, T... arr) {
        var set = new LinkedHashSet<T>();
        set.add(first);
        Collections.addAll(set, arr);
        return set;
    }
}
