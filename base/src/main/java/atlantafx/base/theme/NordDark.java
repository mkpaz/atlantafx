/* SPDX-License-Identifier: MIT */
package atlantafx.base.theme;

public final class NordDark implements Theme {

    public NordDark() { }

    @Override
    public String getName() {
        return "Nord Dark";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/atlantafx/base/theme/nord-dark.css";
    }

    @Override
    public boolean isDarkMode() {
        return true;
    }
}
