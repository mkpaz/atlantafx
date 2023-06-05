/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

final class ColorPalette extends GridPane {

    private final List<ColorPaletteBlock> blocks = new ArrayList<>();
    private final Consumer<ColorPaletteBlock> colorBlockActionHandler;
    private final ReadOnlyObjectProperty<Color> bgBaseColor;

    public ColorPalette(Consumer<ColorPaletteBlock> actionHandler,
                        ReadOnlyObjectProperty<Color> bgBaseColor) {
        super();

        this.colorBlockActionHandler = Objects.requireNonNull(actionHandler, "actionHandler");
        this.bgBaseColor = bgBaseColor;

        add(colorBlock("-color-fg-default", "-color-bg-default", "-color-border-default"), 0, 0);
        add(colorBlock("-color-fg-default", "-color-bg-overlay", "-color-border-default"), 1, 0);
        add(colorBlock("-color-fg-muted", "-color-bg-default", "-color-border-muted"), 2, 0);
        add(colorBlock("-color-fg-subtle", "-color-bg-default", "-color-border-subtle"), 3, 0);

        add(colorBlock("-color-fg-emphasis", "-color-accent-emphasis", "-color-accent-emphasis"), 0, 1);
        add(colorBlock("-color-accent-fg", "-color-bg-default", "-color-accent-emphasis"), 1, 1);
        add(colorBlock("-color-fg-default", "-color-accent-muted", "-color-accent-emphasis"), 2, 1);
        add(colorBlock("-color-accent-fg", "-color-accent-subtle", "-color-accent-emphasis"), 3, 1);

        add(colorBlock("-color-fg-emphasis", "-color-neutral-emphasis-plus", "-color-neutral-emphasis-plus"), 0, 2);
        add(colorBlock("-color-fg-emphasis", "-color-neutral-emphasis", "-color-neutral-emphasis"), 1, 2);
        add(colorBlock("-color-fg-default", "-color-neutral-muted", "-color-neutral-emphasis"), 2, 2);
        add(colorBlock("-color-fg-default", "-color-neutral-subtle", "-color-neutral-emphasis"), 3, 2);

        add(colorBlock("-color-fg-emphasis", "-color-success-emphasis", "-color-success-emphasis"), 0, 3);
        add(colorBlock("-color-success-fg", "-color-bg-default", "-color-success-emphasis"), 1, 3);
        add(colorBlock("-color-fg-default", "-color-success-muted", "-color-success-emphasis"), 2, 3);
        add(colorBlock("-color-success-fg", "-color-success-subtle", "-color-success-emphasis"), 3, 3);

        add(colorBlock("-color-fg-emphasis", "-color-warning-emphasis", "-color-warning-emphasis"), 0, 4);
        add(colorBlock("-color-warning-fg", "-color-bg-default", "-color-warning-emphasis"), 1, 4);
        add(colorBlock("-color-fg-default", "-color-warning-muted", "-color-warning-emphasis"), 2, 4);
        add(colorBlock("-color-warning-fg", "-color-warning-subtle", "-color-warning-emphasis"), 3, 4);

        add(colorBlock("-color-fg-emphasis", "-color-danger-emphasis", "-color-danger-emphasis"), 0, 5);
        add(colorBlock("-color-danger-fg", "-color-bg-default", "-color-danger-emphasis"), 1, 5);
        add(colorBlock("-color-fg-default", "-color-danger-muted", "-color-danger-emphasis"), 2, 5);
        add(colorBlock("-color-danger-fg", "-color-danger-subtle", "-color-danger-emphasis"), 3, 5);

        setId("color-palette");
    }

    private ColorPaletteBlock colorBlock(String fgColor, String bgColor, String borderColor) {
        var block = new ColorPaletteBlock(fgColor, bgColor, borderColor, bgBaseColor);
        block.setOnAction(colorBlockActionHandler);
        blocks.add(block);
        return block;
    }

    // To calculate contrast ratio, we have to obtain all components colors first.
    // Unfortunately, JavaFX doesn't provide an API to observe when stylesheet changes has been applied.
    // The timer is introduced to defer widget update to a time when scene changes supposedly will be finished.
    public void updateColorInfo(Duration delay) {
        var t = new Timeline(new KeyFrame(delay));
        t.setOnFinished(e -> blocks.forEach(ColorPaletteBlock::update));
        t.play();
    }
}
