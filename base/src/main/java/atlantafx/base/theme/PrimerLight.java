/* SPDX-License-Identifier: MIT */
package atlantafx.base.theme;

import java.net.URI;
import java.util.Set;

public class PrimerLight extends AbstractTheme {

    public PrimerLight() {}

    public PrimerLight(URI... stylesheets) {
        super(stylesheets);
    }

    public PrimerLight(Set<URI> stylesheets) {
        super(stylesheets);
    }

    @Override
    public String getName() {
        return "Primer Light";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/primer-light.css";
    }

    @Override
    public boolean isDarkMode() {
        return false;
    }
}
