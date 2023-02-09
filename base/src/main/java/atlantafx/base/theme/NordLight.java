/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

public final class NordLight implements Theme {

    public NordLight() {
        // Default constructor
    }

    @Override
    public String getName() {
        return "Nord Light";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/nord-light.css";
    }

    @Override
    public boolean isDarkMode() {
        return false;
    }
}
