/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

/**
 * A theme based on <a href="https://www.nordtheme.com/">Nord</a> color palette.
 */
public final class NordLight implements Theme {

    public NordLight() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Nord Light";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/nord-light.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheetBSS() {
        return "/atlantafx/base/theme/nord-light.bss";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return false;
    }
}
