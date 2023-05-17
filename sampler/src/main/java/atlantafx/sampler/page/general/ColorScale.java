/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import java.util.Arrays;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

final class ColorScale extends FlowPane {

    private final ReadOnlyObjectWrapper<Color> bgBaseColor = new ReadOnlyObjectWrapper<>(Color.WHITE);
    private final List<ColorScaleBlock> blocks = Arrays.asList(
        ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-base-", 10),
        ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-accent-", 10),
        ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-success-", 10),
        ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-warning-", 10),
        ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-danger-", 10),
        ColorScaleBlock.forColorName(bgBaseColor, "-color-dark", "-color-light")
    );

    public ColorScale() {
        super();

        backgroundProperty().addListener((obs, old, val) -> bgBaseColor.set(
            val != null && !val.getFills().isEmpty()
                ? (Color) val.getFills().get(0).getFill()
                : Color.WHITE
        ));

        setId("color-scale");
        getChildren().setAll(blocks);
    }

    public void updateColorInfo(Duration delay) {
        var t = new Timeline(new KeyFrame(delay));
        t.setOnFinished(e -> blocks.forEach(ColorScaleBlock::update));
        t.play();
    }
}
