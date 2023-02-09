/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

public final class PrimerDark implements Theme {

    /**
     * Initialize a new PrimerDark
     */
    public PrimerDark() {
        // Default constructor
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
