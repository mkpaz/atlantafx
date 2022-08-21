/* SPDX-License-Identifier: MIT */
package atlantafx.base.theme;

import java.net.URI;
import java.util.Set;

public class NordLight extends AbstractTheme {

    public NordLight() {}

    public NordLight(URI... stylesheets) {
        super(stylesheets);
    }

    public NordLight(Set<URI> stylesheets) {
        super(stylesheets);
    }

    @Override
    public String getName() {
        return "Nord Light";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/nord-light.css";
    }

    @Override
    public boolean isDarkMode() {
        return false;
    }
}
