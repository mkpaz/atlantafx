/* SPDX-License-Identifier: MIT */
package atlantafx.base.theme;

public final class NordDark implements Theme {

    /**
     * Initialize a new NordDark
     */
    public NordDark() {
        // Default constructor
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
