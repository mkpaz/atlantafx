/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.util.Colour;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

final class ColorScaleBlock extends VBox {

    private static final double BLOCK_WIDTH = 200;
    private static final double BLOCK_HEIGHT = 40;

    private final Colour bgBaseColor;

    private ColorScaleBlock(Colour bgBaseColor) {
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
                    getSafeFgColor(label).toHex()
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

    private Colour getSafeFgColor(Label label) {
        // deliberately reduce luminance threshold from 0.55 to 0.4
        // to improve readability which is an experimental value anyway
        return getBgColor(label).flatten(bgBaseColor).getLuminance() < 0.4
            ? Colour.color(Color.WHITE)
            : Colour.color(Color.BLACK);
    }

    private Colour getBgColor(Label label) {
        return label.getBackground() != null && !label.getBackground().isEmpty()
            ? Colour.color((Color) label.getBackground().getFills().getFirst().getFill())
            : Colour.color(Color.WHITE);
    }

    //*************************************************************************

    public static ColorScaleBlock forColorPrefix(Colour bgBaseColor, String colorPrefix, int count) {
        var block = new ColorScaleBlock(bgBaseColor);
        for (int idx = 0; idx < count; idx++) {
            block.addCell(colorPrefix + idx);
        }
        return block;
    }

    public static ColorScaleBlock forColorName(Colour bgBaseColor, String... colors) {
        var block = new ColorScaleBlock(bgBaseColor);
        for (String colorName : colors) {
            block.addCell(colorName);
        }
        return block;
    }
}
