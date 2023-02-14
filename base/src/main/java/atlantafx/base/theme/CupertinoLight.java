/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import atlantafx.base.Preview;

@Preview
public class CupertinoLight implements Theme {

    public CupertinoLight() {
        // Default constructor
    }

    @Override
    public String getName() {
        return "Cupertino Light";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/cupertino-light.css";
    }

    @Override
    public boolean isDarkMode() {
        return false;
    }
}
