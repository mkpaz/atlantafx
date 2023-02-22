/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import atlantafx.base.Preview;

/**
 * A theme based on <a href="https://developer.apple.com/design/">IOS</a> color palette.
 */
@Preview
public class CupertinoLight implements Theme {

    public CupertinoLight() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Cupertino Light";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/cupertino-light.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return false;
    }
}
