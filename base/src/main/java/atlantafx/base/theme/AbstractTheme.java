/* SPDX-License-Identifier: MIT */
package atlantafx.base.theme;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractTheme implements Theme {

    private final Set<URI> stylesheets;

    public AbstractTheme() {
        this(new LinkedHashSet<>());
    }

    public AbstractTheme(URI... stylesheets) {
        this(Set.of(stylesheets));
    }

    public AbstractTheme(Set<URI> stylesheets) {
        this.stylesheets = Objects.requireNonNull(stylesheets);
    }

    @Override
    public Set<URI> getStylesheets() {
        return stylesheets;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{" +
                "name=" + getName() +
                ", userAgentStylesheet=" + getUserAgentStylesheet() +
                ", stylesheets=" + stylesheets +
                ", isDarkMode=" + isDarkMode() +
                '}';
    }
}
