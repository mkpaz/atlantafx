/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

/**
 * A theme based on <a href="https://primer.style/">Github Primer</a> color palette.
 */
public final class PrimerDark implements Theme {

    public PrimerDark() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Primer Dark";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/primer-dark.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheetBSS() {
        return "/atlantafx/base/theme/primer-dark.bss";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return true;
    }
}
