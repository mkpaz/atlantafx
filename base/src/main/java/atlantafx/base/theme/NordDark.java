/* SPDX-License-Identifier: MIT */
package atlantafx.base.theme;

import java.net.URI;
import java.util.Set;

public class NordDark extends AbstractTheme {

    public NordDark() {}

    public NordDark(URI... stylesheets) {
        super(stylesheets);
    }

    public NordDark(Set<URI> stylesheets) {
        super(stylesheets);
    }

    @Override
    public String getName() {
        return "Nord Dark";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/nord-dark.css";
    }

    @Override
    public boolean isDarkMode() {
        return true;
    }
}
