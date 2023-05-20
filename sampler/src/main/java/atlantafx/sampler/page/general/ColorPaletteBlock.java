/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.base.theme.Styles.TITLE_3;
import static atlantafx.sampler.page.general.ContrastChecker.LUMINANCE_THRESHOLD;
import static atlantafx.sampler.page.general.ContrastChecker.PASSED;
import static atlantafx.sampler.util.ContrastLevel.getColorLuminance;
import static atlantafx.sampler.util.ContrastLevel.getContrastRatioOpacityAware;
import static atlantafx.sampler.util.JColorUtils.flattenColor;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.util.ContrastLevel;
import atlantafx.sampler.util.NodeUtils;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

final class ColorPaletteBlock extends VBox {

    private final String fgColorName;
    private final String bgColorName;
    private final String borderColorName;
    private final ReadOnlyObjectProperty<Color> bgBaseColor;

    private final AnchorPane colorRectangle;
    private final Text contrastRatioText;
    private final FontIcon contrastLevelIcon = new FontIcon();
    private final Label contrastLevelLabel = new Label();
    private final FontIcon editIcon = new FontIcon(Material2AL.COLORIZE);

    private Consumer<ColorPaletteBlock> actionHandler;

    public ColorPaletteBlock(String fgColorName,
                             String bgColorName,
                             String borderColorName,
                             ReadOnlyObjectProperty<Color> bgBaseColor) {
        this.fgColorName = validateColorName(fgColorName);
        this.bgColorName = validateColorName(bgColorName);
        this.borderColorName = validateColorName(borderColorName);
        this.bgBaseColor = bgBaseColor;

        contrastRatioText = new Text();
        contrastRatioText.setStyle("-fx-fill:" + fgColorName + ";");
        contrastRatioText.getStyleClass().addAll("contrast-ratio-text", TITLE_3);
        NodeUtils.setAnchors(contrastRatioText, new Insets(5, -1, -1, 5));

        contrastLevelLabel.setGraphic(contrastLevelIcon);
        contrastLevelLabel.getStyleClass().add("contrast-level-label");
        contrastLevelLabel.setVisible(false);
        NodeUtils.setAnchors(contrastLevelLabel, new Insets(-1, 3, 3, -1));

        editIcon.setIconSize(24);
        editIcon.getStyleClass().add("edit-icon");
        NodeUtils.toggleVisibility(editIcon, false);
        NodeUtils.setAnchors(editIcon, new Insets(3, 3, -1, -1));

        colorRectangle = new AnchorPane();
        colorRectangle.setStyle(
            String.format("-fx-background-color:%s;-fx-border-color:%s;", bgColorName, borderColorName)
        );
        colorRectangle.getStyleClass().add("rectangle");
        colorRectangle.getChildren().setAll(contrastRatioText, contrastLevelLabel, editIcon);
        colorRectangle.setOnMouseEntered(e -> {
            var bgFill = getBgColor();

            // this happens when css isn't updated yet
            if (bgFill == null) {
                return;
            }

            toggleHover(true);
            editIcon.setFill(getColorLuminance(flattenColor(bgBaseColor.get(), bgFill)) < LUMINANCE_THRESHOLD
                ? Color.WHITE : Color.BLACK
            );
        });
        colorRectangle.setOnMouseExited(e -> toggleHover(false));
        colorRectangle.setOnMouseClicked(e -> {
            if (actionHandler != null) {
                actionHandler.accept(this);
            }
        });

        getChildren().addAll(
            colorRectangle,
            colorNameText(fgColorName),
            colorNameText(bgColorName),
            colorNameText(borderColorName)
        );
        getStyleClass().add("block");
    }

    public void setOnAction(Consumer<ColorPaletteBlock> actionHandler) {
        this.actionHandler = actionHandler;
    }

    public void update() {
        var fgFill = getFgColor();
        var bgFill = getBgColor();

        if (fgFill == null || bgFill == null) {
            contrastRatioText.setText("");
            contrastLevelLabel.setText("");
            contrastLevelLabel.setVisible(false);
            return;
        }

        double contrastRatio = getContrastRatioOpacityAware(bgFill, fgFill, bgBaseColor.get());
        colorRectangle.pseudoClassStateChanged(PASSED, ContrastLevel.AA_NORMAL.satisfies(contrastRatio));

        contrastRatioText.setText(String.format("%.2f", contrastRatio));
        contrastLevelIcon.setIconCode(
            ContrastLevel.AA_NORMAL.satisfies(contrastRatio) ? Material2AL.CHECK : Material2AL.CLOSE
        );
        contrastLevelLabel.setVisible(true);
        contrastLevelLabel.setText(ContrastLevel.AAA_NORMAL.satisfies(contrastRatio) ? "AAA" : "AA");
    }

    public Color getFgColor() {
        return (Color) contrastRatioText.getFill();
    }

    public Color getBgColor() {
        return colorRectangle.getBackground() != null && !colorRectangle.getBackground().isEmpty()
            ? (Color) colorRectangle.getBackground().getFills().get(0).getFill() : null;
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

    private void toggleHover(boolean state) {
        NodeUtils.toggleVisibility(editIcon, state);
        contrastRatioText.setOpacity(state ? 0.5 : 1);
        contrastLevelLabel.setOpacity(state ? 0.5 : 1);
    }

    private Text colorNameText(String text) {
        var t = new Text(text);
        t.getStyleClass().addAll("color-name", Styles.TEXT_SMALL);
        return t;
    }

    static String validateColorName(String colorName) {
        if (colorName == null) {
            throw new NullPointerException("Color name cannot be null!");
        }
        if (!colorName.startsWith("-color")) {
            throw new IllegalArgumentException("Invalid color name: '" + colorName + "'.");
        }
        return colorName;
    }
}
