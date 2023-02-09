/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.FLAT;

import atlantafx.sampler.theme.AccentColor;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.util.JColorUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public class AccentColorSelector extends HBox {

    public AccentColorSelector() {
        super();
        createView();
    }

    private void createView() {
        var resetBtn = new Button("", new FontIcon(Material2AL.CLEAR));
        resetBtn.getStyleClass().addAll(BUTTON_ICON, FLAT);
        resetBtn.setOnAction(e -> ThemeManager.getInstance().resetAccentColor());

        setAlignment(Pos.CENTER_LEFT);
        getChildren().setAll(
            colorButton(AccentColor.primerPurple()),
            colorButton(AccentColor.primerPink()),
            colorButton(AccentColor.primerCoral()),
            resetBtn
        );
        getStyleClass().add("color-selector");
    }

    private Button colorButton(AccentColor accentColor) {
        var icon = new Region();
        icon.getStyleClass().add("icon");

        var btn = new Button("", icon);
        btn.getStyleClass().addAll(BUTTON_ICON, FLAT, "color-button");
        btn.setStyle("-color-primary:" + JColorUtils.toHexWithAlpha(accentColor.primaryColor()) + ";");
        btn.setUserData(accentColor);
        btn.setOnAction(e -> ThemeManager.getInstance().setAccentColor((AccentColor) btn.getUserData()));

        return btn;
    }
}
