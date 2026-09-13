/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import javafx.application.Application;
import org.jspecify.annotations.Nullable;
import us.hebi.graalvm.reachability.annotations.Reachable;

import java.util.*;

import static javafx.application.Application.STYLESHEET_CASPIAN;
import static javafx.application.Application.STYLESHEET_MODENA;

/**
 * The basic theme interface.
 */
@Reachable(resources = {"*.css", "*.bss"})
public interface Theme {

    /**
     * The list of mandatory CSS modules that cannot be excluded from any theme.
     *
     * @see #getUserAgentStylesheet(Set)
     */
    List<String> CORE_MODULES = List.of("root", "text");

    /**
     * Returns theme name.
     */
    String getName();

    /**
     * Returns the path to the theme user-agent stylesheet.
     * See {@link Application#setUserAgentStylesheet(String)} for more info.
     */
    String getUserAgentStylesheet();

    /**
     * Returns the path to the theme user-agent stylesheet in binary
     * (BSS) format. See {@link Application#setUserAgentStylesheet(String)} for more info.
     * All built-in themes are available in BSS format, but custom themes may not,
     * hence the method may return null value.
     */
    @Nullable String getUserAgentStylesheetBSS();

    /**
     * Signifies whether the theme uses a light font on a dark background
     * or vise versa.
     */
    boolean isDarkMode();

    /**
     * A simple factory method for instantiating a new theme.
     */
    static Theme of(final String name, final String userAgentStylesheet, final boolean darkMode) {
        Objects.requireNonNull(name, "Name cannot be null!");
        Objects.requireNonNull(userAgentStylesheet, "User agent stylesheet cannot be null!");

        return new Theme() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getUserAgentStylesheet() {
                return userAgentStylesheet;
            }

            @Override
            public @Nullable String getUserAgentStylesheetBSS() {
                return null;
            }

            @Override
            public boolean isDarkMode() {
                return darkMode;
            }
        };
    }

    /**
     * Returns the URI to the theme user-agent stylesheet, optionally filtered by the specified modules.
     *
     * <p>If the provided list of modules is {@code null} or empty, this method returns the base
     * {@link #getUserAgentStylesheet()} value as-is. Otherwise, it appends or updates the {@code modules}
     * query parameter using the custom {@value StylesheetURLHandler#SCHEME} scheme.
     *
     * @param modules the list of module names to include
     * @return the stylesheet URI configured to include only the specified modules
     * @see Application#setUserAgentStylesheet(String)
     * @see StylesheetURLHandler
     */
    default String getUserAgentStylesheet(@Nullable Set<String> modules) {
        String baseStylesheet = getUserAgentStylesheet();
        if (baseStylesheet.isBlank()) {
            throw new IllegalArgumentException("Theme stylesheet cannot be null or blank.");
        }

        if (modules == null || modules.isEmpty()) {
            return baseStylesheet;
        }

        if (baseStylesheet.matches(".*\\?[^#]*\\bmodules=.*")) {
            throw new IllegalArgumentException(
                "Base stylesheet already contains a 'modules' query parameter: " + baseStylesheet
            );
        }

        // add mandatory modules
        Set<String> finalModules = modules;
        if (!modules.containsAll(CORE_MODULES)) {
            finalModules = new HashSet<>(modules);
            finalModules.addAll(CORE_MODULES);
        }

        String queryValue = String.join(",", finalModules);
        String prefix = StylesheetURLHandler.SCHEME + ":";
        String path = baseStylesheet;

        // strip scheme if present to normalize path handling
        if (path.startsWith(prefix)) {
            path = path.substring(prefix.length());
        }

        // ensure path starts with a leading slash for proper parsing
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        char querySeparator = path.contains("?") ? '&' : '?';
        return prefix + path + querySeparator + "modules=" + queryValue;
    }

    /**
     * Returns whether the theme is a standard theme provided by the OpenJFX or a custom theme.
     */
    default boolean isDefault() {
        return STYLESHEET_MODENA.equals(getUserAgentStylesheet())
            || STYLESHEET_CASPIAN.equals(getUserAgentStylesheet());
    }

    /**
     * Returns the mutable map of properties associated with this theme's class.
     *
     * <p>Properties are shared across all instances of the same theme class.
     *
     * @return a mutable map containing the properties for this theme's class
     * @see ThemeProperties#getPropertiesFor(Class)
     */
    default Map<String, @Nullable Object> getProperties() {
        return ThemeProperties.getPropertiesFor(this.getClass());
    }

    /**
     * Retrieves a property value by its key, casting it to the inferred return type.
     *
     * @param <T> the expected type of the property value
     * @param key the key of the property to retrieve
     * @return the value associated with the specified key, or {@code null} if no mapping exists
     * @throws ClassCastException if the property value cannot be cast to type {@code T}
     */
    @SuppressWarnings("unchecked")
    default <T> @Nullable T getProperty(String key) {
        return (T) getProperties().get(key);
    }

    /**
     * Sets or updates a property value for this theme's class.
     *
     * @param key   the key of the property to set
     * @param value the value to associate with the key, or {@code null} to explicitly map to null
     */
    default void setProperty(String key, @Nullable Object value) {
        getProperties().put(key, value);
    }

    /**
     * Removes the property associated with the specified key from this theme's class.
     *
     * @param key the key of the property to remove
     */
    default void removeProperty(String key) {
        getProperties().remove(key);
    }
}
