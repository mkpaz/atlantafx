/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.theme;

import atlantafx.base.theme.AbstractTheme;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

public class ExternalTheme extends AbstractTheme {

    private final String name;
    private final String stylesheet;
    private final boolean darkMode;

    public ExternalTheme(String name, String stylesheet, Set<URI> stylesheets, boolean darkMode) {
        super(stylesheets);

        this.name = Objects.requireNonNull(name);
        this.stylesheet = Objects.requireNonNull(stylesheet);
        this.darkMode = darkMode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUserAgentStylesheet() {
        return stylesheet;
    }

    @Override
    public boolean isDarkMode() {
        return darkMode;
    }
}
