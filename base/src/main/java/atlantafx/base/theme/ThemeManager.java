/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import javafx.application.Application;
import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;
import org.jspecify.annotations.Nullable;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.*;
import java.util.function.Function;

/**
 * Provides centralized control over application themes and dynamic style options.
 *
 * <p>This manager applies the user-agent stylesheet (via {@link Application#setUserAgentStylesheet(String)})
 * automatically based on the selected theme. It also allows registration of theme options that, when changed,
 * can execute user-provided actions to set dynamic CSS stylesheets to customize theme visuals. These actions
 * are applied to all existing and future scenes.
 *
 * <h6>Initial Theme Selection</h6>
 * The manager checks the {@code atlantafx.theme} system property on startup. If the property is set, it searches
 * for a matching theme class using {@link ServiceLoader}.
 *
 * <pre>{@code
 * java -Datlantafx.theme=NordDark -jar application.jar
 * }</pre>
 *
 * <p>If the property is empty or the class is not found, it falls back to the JavaFX platform color scheme
 * preference ({@code Platform.getPreferences().getColorScheme()}), defaulting to {@link PrimerLight} for
 * {@code LIGHT} and {@link PrimerDark} for {@code DARK}.
 *
 * <h6>Example</h6>
 *
 * <pre>{@code
 * // init once, if no further configuration is needed
 * ThemeManager.useDefault();
 *
 * // access the manager instance and change the active theme
 * ThemeManager manager = ThemeManager.instance().setTheme(new NordDark());
 *
 * // listen to theme changes
 * manager.themeProperty().addListener((obs, oldTheme, newTheme) -> {
 *     System.out.println("Theme changed to: " + newTheme.getClass().getSimpleName());
 *     for (var scene : manager.getScenes()) {
 *         // apply custom stylesheets for the theme
 *     }
 * });
 *
 * // define keys and register custom theme options
 * static final ThemeOption.Key<String> FONT_FAMILY = new ThemeOption.Key<>("font.family", String.class);
 *
 * manager.register(ThemeOption.of(
 *     FONT_FAMILY,
 *     "Inter",
 *     change -> change.applyStylesheet("font.family", change.scene(), value ->
 *         ".root { -fx-font-family: \"" + value + "\"; }"
 *     )
 * ));
 *
 * // modify option values to trigger a change
 * manager.setOption(FONT_FAMILY, "Roboto");
 *
 * // reset option to its default value
 * manager.resetOption(FONT_FAMILY);
 * }</pre>
 *
 * <p>Theme options are abstract. You don't need to tie them to specific "physical" instances
 * like font or color. This is basically a way to trigger a {@link Change} event and handle
 * it in any way. You can also group theme options into custom carrier classes, for example
 * {@code ThemeOption.Key<>("preset", Preset.class)}, especially when all changes can be made
 * in a single stylesheet.
 *
 * <p>This class is not thread-safe. All methods (except {@link #instance()}) must be called on the
 * JavaFX Application Thread.
 *
 * @see Theme
 * @see ThemeOption
 */
public final class ThemeManager {

    private static final Logger LOGGER = System.getLogger(ThemeManager.class.getName());

    private static final ThemeManager INSTANCE = new ThemeManager();

    /**
     * Returns the singleton instance of the theme manager.
     *
     * @return the global theme manager instance
     */
    public static ThemeManager instance() {
        return INSTANCE;
    }

    //*************************************************************************

    private final ObjectProperty<Theme> themeProperty = new SimpleObjectProperty<>();
    private final ChangeListener<Theme> themeListener;

    private final Map<ThemeOption.Key<?>, OptionValue<?>> options = new HashMap<>();
    private final Map<Window, ChangeListener<@Nullable Scene>> sceneListeners = new IdentityHashMap<>();
    private final ListChangeListener<Window> windowListener;

    private ThemeManager() {
        var initialTheme = findInitialTheme();
        themeProperty.set(initialTheme);
        applyTheme(initialTheme);

        ObservableList<Window> windows = Window.getWindows();

        windowListener = change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Window window : change.getAddedSubList()) {
                        track(window);
                    }
                }
                if (change.wasRemoved()) {
                    for (Window window : change.getRemoved()) {
                        untrack(window);
                    }
                }
            }
        };

        for (Window window : windows) {
            track(window);
        }

        windows.addListener(windowListener);

        themeListener = (_, _, theme) -> {
            applyTheme(theme);
            applyOptions();
        };
        themeProperty.addListener(themeListener);
    }

    // Resolves the initial theme using system properties or system preferences.
    private static Theme findInitialTheme() {
        String prop = System.getProperty("atlantafx.theme");
        if (prop != null && !prop.isBlank()) {
            String name = prop.trim();

            ClassLoader cl = Objects.requireNonNullElse(
                Thread.currentThread().getContextClassLoader(), // FX-thread
                Theme.class.getClassLoader() // Theme ClassLoader thread
            );

            ServiceLoader<Theme> loader = ServiceLoader.load(Theme.class, cl);
            Theme theme = loader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(t -> name.equalsIgnoreCase(t.getClass().getSimpleName()))
                .findFirst()
                .orElse(null);

            if (theme != null) {
                return theme;
            }

            String msg = "Theme '%s' not found. Ensure 'app.theme' matches a theme class name (e.g., PrimerLight)."
                .formatted(name);
            LOGGER.log(Level.WARNING, msg);
        }

        return Platform.getPreferences().getColorScheme() == ColorScheme.LIGHT
            ? new PrimerLight()
            : new PrimerDark();
    }

    // Starts tracking window scene changes to apply options.
    private void track(Window window) {
        if (window.getScene() != null) {
            applyOptions(window.getScene());
        }

        ChangeListener<@Nullable Scene> sceneListener = (_, _, scene) -> {
            if (scene != null) {
                applyOptions(scene);
            }
        };

        window.sceneProperty().addListener(sceneListener);
        sceneListeners.put(window, sceneListener);
    }

    // Stops tracking window scene changes.
    private void untrack(Window window) {
        ChangeListener<@Nullable Scene> listener = sceneListeners.remove(window);
        if (listener != null) {
            window.sceneProperty().removeListener(listener);
        }
    }

    //region THEME
    //*************************************************************************

    /**
     * Initializes the theme manager and applies the default theme logic.
     * Call this method on application startup if no custom theme options are needed.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void useDefault() {
        instance();
    }

    /**
     * Returns the property containing the current theme.
     *
     * @return the theme property
     */
    public ObjectProperty<Theme> themeProperty() {
        return themeProperty;
    }

    /**
     * Returns the active theme.
     *
     * @return the current theme instance
     */
    public Theme getTheme() {
        return themeProperty.get();
    }

    /**
     * Sets a new active theme and applies it.
     *
     * @param theme the new theme to set
     */
    public ThemeManager setTheme(Theme theme) {
        this.themeProperty.set(theme);
        return this;
    }

    /**
     * Returns a list of all active scenes from currently open windows.
     *
     * @return a list containing scenes attached to open windows
     */
    public List<Scene> getScenes() {
        var scenes = new ArrayList<Scene>();
        for (var window : Window.getWindows()) {
            var scene = window.getScene();
            if (scene != null) {
                scenes.add(scene);
            }
        }
        return scenes;
    }
    //endregion

    //region OPTIONS
    //*************************************************************************

    /**
     * Registers a new theme option.
     *
     * @param option the option to register
     * @param <T>    the value type of the option
     * @throws IllegalStateException if an option with the same identifier is already registered
     */
    public <T> ThemeManager register(ThemeOption<T> option) {
        ThemeOption.Key<T> key = option.key();

        boolean exists = options.values().stream().anyMatch(o ->
            Objects.equals(key.id(), o.option().key().id())
        );

        if (exists) {
            throw new IllegalStateException("Theme option with id '" + key.id() + "' is already registered");
        }

        options.put(key, new OptionValue<>(option, option.defaultValue()));
        return this;
    }

    /**
     * Unregisters a theme option.
     *
     * @param option the option to remove
     * @param <T>    the value type of the option
     */
    public <T> ThemeManager unregister(ThemeOption<T> option) {
        ThemeOption.Key<T> key = option.key();
        options.remove(key);
        return this;
    }

    /**
     * Checks if an option is registered.
     *
     * @param key the option key to check
     * @return {@code true} if registered, otherwise {@code false}
     */
    public boolean supports(ThemeOption.Key<?> key) {
        return options.containsKey(key);
    }

    /**
     * Retrieves the current value of an option.
     *
     * @param key the option key
     * @param <T> the value type
     * @return the current option value, or {@code null} if no value is set
     * @throws IllegalArgumentException if the option is not registered
     */
    public <T> @Nullable T getOption(ThemeOption.Key<T> key) {
        return requireOption(key).get();
    }

    /**
     * Sets a new value for a registered option and applies it.
     *
     * @param key   the option key
     * @param value the new value to set
     * @param <T>   the value type
     * @throws IllegalArgumentException if the option is not registered
     * @throws ClassCastException       if the value type does not match the key type
     */
    public <T> ThemeManager setOption(ThemeOption.Key<T> key, @Nullable T value) {
        OptionValue<T> option = requireOption(key);

        if (value != null && !key.type().isInstance(value)) {
            throw new ClassCastException("Value for '" + key.id() + "' must be instance of " + key.type().getName());
        }

        option.set(value);
        applyOption(option);

        return this;
    }

    /**
     * Resets a single option to its default value and applies it.
     *
     * @param key the option key
     * @param <T> the value type
     * @throws IllegalArgumentException if the option is not registered
     */
    public <T> ThemeManager resetOption(ThemeOption.Key<T> key) {
        OptionValue<T> option = requireOption(key);
        option.set(option.option().defaultValue());
        applyOption(option);
        return this;
    }

    /**
     * Resets all registered options to their default values and applies them.
     */
    public ThemeManager resetOptions() {
        for (OptionValue<?> option : options.values()) {
            option.reset();
        }
        applyOptions();
        return this;
    }

    /**
     * Disposes this manager by detaching all internal listeners.
     */
    public void dispose() {
        Window.getWindows().removeListener(windowListener);
        themeProperty.removeListener(themeListener);

        sceneListeners.forEach((window, listener) ->
            window.sceneProperty().removeListener(listener));
        sceneListeners.clear();
    }

    //*************************************************************************

    private void applyTheme(Theme theme) {
        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
    }

    // Applies a single option value to all open scenes.
    private void applyOption(OptionValue<?> value) {
        var theme = themeProperty.get();
        for (Scene scene : getScenes()) {
            value.apply(theme, scene);
        }
    }

    // Applies all registered options to a specific scene.
    private void applyOptions(Scene scene) {
        Theme theme = themeProperty.get();
        for (var option : options.values()) {
            option.apply(theme, scene);
        }
    }

    // Applies all registered options to all open scenes.
    private void applyOptions() {
        for (var scene : getScenes()) {
            applyOptions(scene);
        }
    }

    // Finds an option wrapper or throws an exception.
    @SuppressWarnings("unchecked")
    private <T> OptionValue<T> requireOption(ThemeOption.Key<T> key) {
        OptionValue<?> option = options.get(key);
        if (option == null) {
            throw new IllegalArgumentException("Option not registered: " + key);
        }
        return (OptionValue<T>) option;
    }

    // Internal wrapper that stores an option and its current value.
    private static final class OptionValue<T> {

        private final ThemeOption<T> option;
        private @Nullable T value;

        OptionValue(ThemeOption<T> option, @Nullable T value) {
            this.option = option;
            this.value = value;
        }

        public ThemeOption<T> option() {
            return option;
        }

        public @Nullable T get() {
            return value;
        }

        public void set(@Nullable T value) {
            this.value = value;
        }

        public void reset() {
            this.value = option.defaultValue();
        }

        public void apply(Theme theme, Scene scene) {
            option.apply(new Change<>(theme, value, scene));
        }
    }

    //*************************************************************************

    /**
     * Carries option change data for a specific scene.
     *
     * @param theme the active theme
     * @param value the current option value
     * @param scene the target scene being updated
     * @param <T>   the option value type
     */
    public record Change<T>(Theme theme, @Nullable T value, Scene scene) {

        /**
         * Applies or replaces a dynamic CSS stylesheet on a scene's root.
         *
         * <p>If the value is present, the generated CSS is encoded and appended or updated in place.
         * If the value is {@code null}, any previously added CSS with the same identifier is removed.
         *
         * @param id            the unique option identifier
         * @param scene         the target scene
         * @param styleSupplier function generating plain CSS from the option value
         */
        public void applyStylesheet(String id, Scene scene, Function<T, String> styleSupplier) {
            applyStylesheet(id, scene.getRoot(), styleSupplier);
        }

        /**
         * Applies or replaces a dynamic CSS stylesheet on a parent node.
         *
         * <p>If the value is present, the generated CSS is encoded and appended or updated in place.
         * If the value is {@code null}, any previously added CSS with the same identifier is removed.
         *
         * @param id            the unique option identifier
         * @param node          the target parent node
         * @param styleSupplier function generating plain CSS from the option value
         */
        public void applyStylesheet(String id, Parent node, Function<T, String> styleSupplier) {
            var stylesheets = node.getStylesheets();
            String marker = "/*option:" + id + "*/";

            int index = -1;
            for (int i = 0; i < stylesheets.size(); i++) {
                if (stylesheets.get(i).endsWith(marker)) {
                    index = i;
                    break;
                }
            }

            if (value != null) {
                String css = Styles.encode(styleSupplier.apply(value) + "\n" + marker);

                if (index != -1) {
                    stylesheets.set(index, css);
                } else {
                    stylesheets.add(css);
                }
            } else if (index != -1) {
                stylesheets.remove(index);
            }
        }
    }
    //endregion
}