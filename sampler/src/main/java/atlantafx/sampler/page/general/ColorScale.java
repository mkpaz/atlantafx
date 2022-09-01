/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.general;

import atlantafx.base.theme.Styles;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class ColorScale extends VBox {

    private final ReadOnlyObjectWrapper<Color> bgBaseColor = new ReadOnlyObjectWrapper<>(Color.WHITE);
    private final List<ColorScaleBlock> blocks = Arrays.asList(
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-gray-", 10),
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-blue-", 10),
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-green-", 10),
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-yellow-", 10),
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-red-", 10),
            ColorScaleBlock.forColorName(bgBaseColor, "-color-black", "-color-white")
    );

    public ColorScale() {
        super();
        createView();
    }

    private void createView() {
        var headerLabel = new Label("Color Scale");
        headerLabel.getStyleClass().add(Styles.TITLE_4);

        var headerBox = new HBox();
        headerBox.getChildren().setAll(headerLabel);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getStyleClass().add("header");

        backgroundProperty().addListener((obs, old, val) -> bgBaseColor.set(
                val != null && !val.getFills().isEmpty() ? (Color) val.getFills().get(0).getFill() : Color.WHITE
        ));

        setId("color-scale");
        getChildren().setAll(
                headerBox,
                colorTable()
        );
    }

    public void updateColorInfo(Duration delay) {
        var t = new Timeline(new KeyFrame(delay));
        t.setOnFinished(e -> blocks.forEach(ColorScaleBlock::update));
        t.play();
    }

    private FlowPane colorTable() {
        var root = new FlowPane(20, 20);
        root.getStyleClass().add("table");
        root.getChildren().setAll(blocks);
        return root;
    }
}
