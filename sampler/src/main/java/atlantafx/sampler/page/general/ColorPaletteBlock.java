/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.Colour;
import atlantafx.sampler.util.NodeUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.jspecify.annotations.Nullable;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.function.Consumer;

import static atlantafx.base.theme.Styles.TITLE_3;
import static atlantafx.sampler.page.general.ContrastChecker.LUMINANCE_THRESHOLD;
import static atlantafx.sampler.page.general.ContrastChecker.PASSED;

final class ColorPaletteBlock extends VBox {

    private final String fgColorName;
    private final String bgColorName;
    private final String borderColorName;
    private final Colour bgBaseColor;

    private final AnchorPane colorRect;
    private final Text contrastRatioText;
    private final FontIcon contrastLevelIcon = new FontIcon();
    private final Label contrastLevelLabel = new Label();
    private final FontIcon editIcon = new FontIcon(Material2AL.COLORIZE);

    private @Nullable Consumer<ColorPaletteBlock> actionHandler;

    public ColorPaletteBlock(String fgColorName,
                             String bgColorName,
                             String borderColorName,
                             Colour bgBaseColor) {
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

        colorRect = new AnchorPane();
        colorRect.setStyle(
            String.format("-fx-background-color:%s;-fx-border-color:%s;", bgColorName, borderColorName)
        );
        colorRect.getStyleClass().add("rectangle");
        colorRect.getChildren().setAll(contrastRatioText, contrastLevelLabel, editIcon);
        colorRect.setOnMouseEntered(_ -> {
            var bgFill = getBgColor();

            // this happens when CSS isn't updated yet
            if (bgFill.isTransparent()) {
                return;
            }

            toggleHover(true);

            var luminance = bgFill.flatten(bgBaseColor).getLuminance();
            var fill = luminance < LUMINANCE_THRESHOLD ? Colour.color(Color.WHITE) : Colour.color(Color.BLACK);

            editIcon.setFill(fill.toColor());
        });
        colorRect.setOnMouseExited(_ -> toggleHover(false));
        colorRect.setOnMouseClicked(_ -> {
            if (actionHandler != null) {
                actionHandler.accept(this);
            }
        });

        getChildren().addAll(
            colorRect,
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

        if (fgFill.isTransparent() || bgFill.isTransparent()) {
            contrastRatioText.setText("");
            contrastLevelLabel.setText("");
            contrastLevelLabel.setVisible(false);
            return;
        }

        double contrastRatio = Colour.ContrastLevel.getContrastRatioOpacityAware(bgFill, fgFill, bgBaseColor);
        colorRect.pseudoClassStateChanged(PASSED, Colour.ContrastLevel.AA_NORMAL.satisfies(contrastRatio));

        contrastRatioText.setText(String.format("%.2f", contrastRatio));
        contrastLevelIcon.setIconCode(
            Colour.ContrastLevel.AA_NORMAL.satisfies(contrastRatio) ? Material2AL.CHECK : Material2AL.CLOSE
        );
        contrastLevelLabel.setVisible(true);
        contrastLevelLabel.setText(Colour.ContrastLevel.AAA_NORMAL.satisfies(contrastRatio) ? "AAA" : "AA");
    }

    public Colour getFgColor() {
        var color = (Color) contrastRatioText.getFill();
        return color != null ? Colour.color(color) : Colour.color(Color.TRANSPARENT);
    }

    public Colour getBgColor() {
        return colorRect.getBackground() != null && !colorRect.getBackground().isEmpty()
            ? Colour.color((Color) colorRect.getBackground().getFills().getFirst().getFill())
            : Colour.color(Color.TRANSPARENT);
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

    static String validateColorName(@Nullable String colorName) {
        if (colorName == null) {
            throw new NullPointerException("Color name cannot be null!");
        }
        if (!colorName.startsWith("-color")) {
            throw new IllegalArgumentException("Invalid color name: '" + colorName + "'.");
        }
        return colorName;
    }
}
