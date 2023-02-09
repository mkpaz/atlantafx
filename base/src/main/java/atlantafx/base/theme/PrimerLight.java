/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

public final class PrimerLight implements Theme {

    public PrimerLight() {
        // Default constructor
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
