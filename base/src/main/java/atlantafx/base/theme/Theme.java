/* SPDX-License-Identifier: MIT */
package atlantafx.base.theme;

import java.net.URI;
import java.util.Set;

import static javafx.application.Application.STYLESHEET_CASPIAN;
import static javafx.application.Application.STYLESHEET_MODENA;

// This is merely a wrapper around stylesheet paths.
// Let's hope JavaFX theme support will be merged.
// https://github.com/openjdk/jfx/pull/511
public interface Theme {

    String getName();

    String getUserAgentStylesheet();

    Set<URI> getStylesheets();

    boolean isDarkMode();

    default boolean isDefault() {
        return STYLESHEET_MODENA.equals(getUserAgentStylesheet()) || STYLESHEET_CASPIAN.equals(getUserAgentStylesheet());
    }
}
