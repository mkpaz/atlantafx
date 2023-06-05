/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.sampler.util.ContrastLevel.getColorLuminance;
import static atlantafx.sampler.util.JColorUtils.flattenColor;

import atlantafx.sampler.util.JColorUtils;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

final class ColorScaleBlock extends VBox {

    private static final double BLOCK_WIDTH = 200;
    private static final double BLOCK_HEIGHT = 40;

    private final ReadOnlyObjectProperty<Color> bgBaseColor;

    private ColorScaleBlock(ReadOnlyObjectProperty<Color> bgBaseColor) {
        super();

        this.bgBaseColor = bgBaseColor;
        createView();
    }

    private void addCell(String colorName) {
        getChildren().add(label(colorName));
    }

    public void update() {
        getChildren().forEach(c -> {
            if (c instanceof Label label) {
                String colorName = (String) label.getUserData();
                label.setStyle(String.format("-fx-background-color:%s;-fx-text-fill:%s;",
                    colorName,
                    JColorUtils.toHexWithAlpha(getSafeFgColor(label))
                ));
            }
        });
    }

    private void createView() {
        getStyleClass().add("column");
    }

    private static Label label(String colorName) {
        var label = new Label(colorName);
        label.setMinHeight(BLOCK_HEIGHT);
        label.setMinWidth(BLOCK_WIDTH);
        label.setPrefWidth(BLOCK_WIDTH);
        label.setMaxWidth(BLOCK_WIDTH);
        label.setAlignment(Pos.CENTER_LEFT);
        label.getStyleClass().add("cell");
        label.setUserData(colorName);
        label.setStyle(String.format("-fx-background-color:%s;", colorName));
        return label;
    }

    private Color getSafeFgColor(Label label) {
        var bg = getBgColor(label);
        // deliberately reduce luminance threshold from 0.55 to 0.4
        // to improve readability which is an experimental value anyway
        return getColorLuminance(flattenColor(bgBaseColor.get(), bg)) < 0.4 ? Color.WHITE : Color.BLACK;
    }

    private Color getBgColor(Label label) {
        return label.getBackground() != null && !label.getBackground().isEmpty()
            ? (Color) label.getBackground().getFills().get(0).getFill() : Color.WHITE;
    }

    ///////////////////////////////////////////////////////////////////////////

    public static ColorScaleBlock forColorPrefix(ReadOnlyObjectProperty<Color> bgBaseColor,
                                                 String colorPrefix,
                                                 int count) {
        var block = new ColorScaleBlock(bgBaseColor);
        for (int idx = 0; idx < count; idx++) {
            block.addCell(colorPrefix + idx);
        }
        return block;
    }

    public static ColorScaleBlock forColorName(ReadOnlyObjectProperty<Color> bgBaseColor,
                                               String... colors) {
        var block = new ColorScaleBlock(bgBaseColor);
        for (String colorName : colors) {
            block.addCell(colorName);
        }
        return block;
    }
}
