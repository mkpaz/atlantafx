/* SPDX-License-Identifier: MIT */
package atlantafx.base.theme;

import java.net.URI;
import java.util.Set;

public class PrimerDark extends AbstractTheme {

    public PrimerDark() {}

    public PrimerDark(URI... stylesheets) {
        super(stylesheets);
    }

    public PrimerDark(Set<URI> stylesheets) {
        super(stylesheets);
    }

    @Override
    public String getName() {
        return "Primer Dark";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/primer-dark.css";
    }

    @Override
    public boolean isDarkMode() {
        return true;
    }
}
