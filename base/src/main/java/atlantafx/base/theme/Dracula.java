/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import atlantafx.base.Preview;

/**
 * A theme based on <a href="https://draculatheme.com">Dracula</a> color palette.
 */
@Preview
public class Dracula implements Theme {

    public Dracula() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Dracula";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/dracula.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return true;
    }
}
