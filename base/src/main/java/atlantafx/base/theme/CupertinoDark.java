/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import atlantafx.base.Preview;

@Preview
public class CupertinoDark implements Theme {

    public CupertinoDark() {
        // Default constructor
    }

    @Override
    public String getName() {
        return "Cupertino Dark";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/cupertino-dark.css";
    }

    @Override
    public boolean isDarkMode() {
        return true;
    }
}
