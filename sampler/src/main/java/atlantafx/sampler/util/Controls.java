/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.util;

import static atlantafx.base.theme.Styles.BUTTON_ICON;

import javafx.scene.control.Button;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

@Deprecated
public final class Controls {

    public static Button iconButton(Ikon icon, boolean disable) {
        return button("", icon, disable, BUTTON_ICON);
    }

    public static Button button(String text, Ikon icon, boolean disable, String... styleClasses) {
        var button = new Button(text);
        if (icon != null) {
            button.setGraphic(new FontIcon(icon));
        }
        button.setDisable(disable);
        button.getStyleClass().addAll(styleClasses);
        return button;
    }
}
