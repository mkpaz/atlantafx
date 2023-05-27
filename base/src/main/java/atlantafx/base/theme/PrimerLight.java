/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

/**
 * A theme based on <a href="https://primer.style/">Github Primer</a> color palette.
 */
public final class PrimerLight implements Theme {

    public PrimerLight() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Primer Light";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/primer-light.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheetBSS() {
        return "/atlantafx/base/theme/primer-light.bss";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return false;
    }
}
