/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import static javafx.application.Application.STYLESHEET_CASPIAN;
import static javafx.application.Application.STYLESHEET_MODENA;

// This is merely a wrapper around stylesheet paths.
// Let's hope JavaFX theme support will be merged.
// https://github.com/openjdk/jfx/pull/511
public interface Theme {

    String getName();

    String getUserAgentStylesheet();

    boolean isDarkMode();

    static Theme of(final String name, final String userAgentStylesheet, final boolean darkMode) {
        if (name == null) {
            throw new NullPointerException("Name cannot be null!");
        }
        if (userAgentStylesheet == null) {
            throw new NullPointerException("User agent stylesheet cannot be null!");
        }

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
            public boolean isDarkMode() {
                return darkMode;
            }
        };
    }

    default boolean isDefault() {
        return STYLESHEET_MODENA.equals(getUserAgentStylesheet())
            || STYLESHEET_CASPIAN.equals(getUserAgentStylesheet());
    }
}
