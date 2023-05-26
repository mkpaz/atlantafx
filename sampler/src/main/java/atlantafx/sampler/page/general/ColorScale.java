/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import java.util.Arrays;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

final class ColorScale extends FlowPane {

    private final List<ColorScaleBlock> blocks;

    public ColorScale(ReadOnlyObjectProperty<Color> bgBaseColor) {
        super();

        blocks = Arrays.asList(
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-base-", 10),
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-accent-", 10),
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-success-", 10),
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-warning-", 10),
            ColorScaleBlock.forColorPrefix(bgBaseColor, "-color-danger-", 10),
            ColorScaleBlock.forColorName(bgBaseColor, "-color-dark", "-color-light")
        );

        setId("color-scale");
        getChildren().setAll(blocks);
    }

    public void updateColorInfo(Duration delay) {
        var t = new Timeline(new KeyFrame(delay));
        t.setOnFinished(e -> blocks.forEach(ColorScaleBlock::update));
        t.play();
    }
}
