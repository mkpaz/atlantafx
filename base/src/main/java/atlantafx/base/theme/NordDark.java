/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

/**
 * A theme based on <a href="https://primer.style/">Nord</a> color palette.
 */
public final class NordDark implements Theme {

    public NordDark() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Nord Dark";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/nord-dark.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheetBSS() {
        return "/atlantafx/base/theme/nord-dark.bss";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return true;
    }
}
