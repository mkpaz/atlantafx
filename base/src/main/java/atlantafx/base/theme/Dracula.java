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

    @Override
    public String getName() {
        return "Dracula";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/dracula.css";
    }

    @Override
    public boolean isDarkMode() {
        return true;
    }
}
