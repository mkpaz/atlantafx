/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.general;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.util.Containers;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.function.Consumer;

import static atlantafx.sampler.page.general.ContrastChecker.*;
import static atlantafx.sampler.util.JColorUtils.flattenColor;
import static atlantafx.sampler.util.JColorUtils.getColorLuminance;

class ColorBlock extends VBox {

    private final String fgColorName;
    private final String bgColorName;
    private final String borderColorName;
    private final ReadOnlyObjectProperty<Color> bgBaseColor;

    private final AnchorPane colorBox;
    private final Text fgText;
    private final FontIcon wsagIcon = new FontIcon();
    private final Label wsagLabel = new Label();
    private final FontIcon expandIcon = new FontIcon(Feather.MAXIMIZE_2);

    private Consumer<ColorBlock> actionHandler;

    public ColorBlock(String fgColorName,
                      String bgColorName,
                      String borderColorName,
                      ReadOnlyObjectProperty<Color> bgBaseColor) {
        this.fgColorName = validateColorName(fgColorName);
        this.bgColorName = validateColorName(bgColorName);
        this.borderColorName = validateColorName(borderColorName);
        this.bgBaseColor = bgBaseColor;

        fgText = new Text();
        fgText.setStyle("-fx-fill:" + fgColorName + ";");
        fgText.getStyleClass().addAll("text", Styles.TITLE_3);
        Containers.setAnchors(fgText, new Insets(5, -1, -1, 5));

        wsagLabel.setGraphic(wsagIcon);
        wsagLabel.getStyleClass().add("wsag-label");
        wsagLabel.setVisible(false);
        Containers.setAnchors(wsagLabel, new Insets(-1, 3, 3, -1));

        expandIcon.setIconSize(24);
        expandIcon.getStyleClass().add("expand-icon");
        expandIcon.setVisible(false);
        expandIcon.setManaged(false);
        Containers.setAnchors(expandIcon, new Insets(3, 3, -1, -1));

        colorBox = new AnchorPane();
        colorBox.setStyle("-fx-background-color:" + bgColorName + ";" + "-fx-border-color:" + borderColorName + ";");
        colorBox.getStyleClass().add("box");
        colorBox.getChildren().setAll(fgText, wsagLabel, expandIcon);
        colorBox.setOnMouseEntered(e -> {
            var bgFill = getBgColor();

            // this happens when css isn't updated yet
            if (bgFill == null) { return; }

            toggleHover(true);
            expandIcon.setFill(getColorLuminance(flattenColor(bgBaseColor.get(), bgFill)) < LUMINANCE_THRESHOLD ?
                    Color.WHITE : Color.BLACK
            );
        });
        colorBox.setOnMouseExited(e -> toggleHover(false));
        colorBox.setOnMouseClicked(e -> {
            if (actionHandler != null) { actionHandler.accept(this); }
        });

        getChildren().addAll(
                colorBox,
                description(fgColorName),
                description(bgColorName),
                description(borderColorName)
        );
        getStyleClass().add("color-block");
    }

    static String validateColorName(String colorName) {
        if (colorName == null || !colorName.startsWith("-color")) {
            throw new IllegalArgumentException("Invalid color name: '" + colorName + "'.");
        }
        return colorName;
    }

    private void toggleHover(boolean state) {
        expandIcon.setVisible(state);
        expandIcon.setManaged(state);
        fgText.setOpacity(state ? 0.5 : 1);
        wsagLabel.setOpacity(state ? 0.5 : 1);
    }

    private Text description(String text) {
        var t = new Text(text);
        t.getStyleClass().addAll("description", Styles.TEXT_SMALL);
        return t;
    }

    public void update() {
        var fgFill = getFgColor();
        var bgFill = getBgColor();

        if (fgFill == null || bgFill == null) {
            fgText.setText("");
            wsagLabel.setText("");
            wsagLabel.setVisible(false);
            return;
        }

        double contrastRatio = 1 / getContrastRatioOpacityAware(bgFill, fgFill, bgBaseColor.get());
        colorBox.pseudoClassStateChanged(PASSED, contrastRatio >= 4.5);

        wsagIcon.setIconCode(contrastRatio >= 4.5 ? Material2AL.CHECK : Material2AL.CLOSE);
        wsagLabel.setVisible(true);
        wsagLabel.setText(contrastRatio >= 7 ? "AAA" : "AA");
        fgText.setText(String.format("%.2f", contrastRatio));
    }

    public Color getFgColor() {
        return (Color) fgText.getFill();
    }

    public Color getBgColor() {
        return colorBox.getBackground() != null && !colorBox.getBackground().isEmpty() ?
                (Color) colorBox.getBackground().getFills().get(0).getFill() : null;
    }

    public String getFgColorName() {
        return fgColorName;
    }

    public String getBgColorName() {
        return bgColorName;
    }

    public String getBorderColorName() {
        return borderColorName;
    }

    public void setOnAction(Consumer<ColorBlock> actionHandler) {
        this.actionHandler = actionHandler;
    }
}
