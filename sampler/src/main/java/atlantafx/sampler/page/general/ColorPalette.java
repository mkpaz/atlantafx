/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.general;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

class ColorPalette extends VBox {

    private final List<ColorBlock> blocks = new ArrayList<>();
    private final ReadOnlyObjectWrapper<Color> bgBaseColor = new ReadOnlyObjectWrapper<>(Color.WHITE);
    private final Consumer<ColorBlock> colorBlockActionHandler;

    public ColorPalette(Consumer<ColorBlock> blockClickedHandler) {
        super();

        this.colorBlockActionHandler = Objects.requireNonNull(blockClickedHandler);
        createView();
    }

    private void createView() {
        var headerLabel = new Label("Color Palette");
        headerLabel.getStyleClass().add(Styles.TITLE_4);

        var headerBox = new HBox();
        headerBox.getChildren().setAll(headerLabel, new Spacer());
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getStyleClass().add("header");

        var colorGrid = colorGrid();

        backgroundProperty().addListener((obs, old, val) -> bgBaseColor.set(
                val != null && !val.getFills().isEmpty() ? (Color) val.getFills().get(0).getFill() : Color.WHITE
        ));

        getChildren().setAll(headerBox, colorGrid);
        setId("color-palette");
    }

    private GridPane colorGrid() {
        var grid = new GridPane();
        grid.getStyleClass().add("grid");

        grid.add(colorBlock("-color-fg-default", "-color-bg-default", "-color-border-default"), 0, 0);
        grid.add(colorBlock("-color-fg-muted", "-color-bg-default", "-color-border-muted"), 1, 0);
        grid.add(colorBlock("-color-fg-subtle", "-color-bg-default", "-color-border-subtle"), 2, 0);

        grid.add(colorBlock("-color-fg-emphasis", "-color-accent-emphasis", "-color-accent-emphasis"), 0, 1);
        grid.add(colorBlock("-color-accent-fg", "-color-bg-default", "-color-accent-emphasis"), 1, 1);
        grid.add(colorBlock("-color-accent-fg", "-color-accent-muted", "-color-accent-muted"), 2, 1);
        grid.add(colorBlock("-color-accent-fg", "-color-accent-subtle", "-color-accent-subtle"), 3, 1);

        grid.add(colorBlock("-color-fg-emphasis", "-color-neutral-emphasis-plus", "-color-neutral-emphasis-plus"), 0, 2);
        grid.add(colorBlock("-color-fg-emphasis", "-color-neutral-emphasis", "-color-neutral-emphasis"), 1, 2);
        grid.add(colorBlock("-color-fg-muted", "-color-neutral-muted", "-color-neutral-muted"), 2, 2);
        grid.add(colorBlock("-color-fg-subtle", "-color-neutral-subtle", "-color-neutral-subtle"), 3, 2);

        grid.add(colorBlock("-color-fg-emphasis", "-color-success-emphasis", "-color-success-emphasis"), 0, 3);
        grid.add(colorBlock("-color-success-fg", "-color-bg-default", "-color-success-emphasis"), 1, 3);
        grid.add(colorBlock("-color-success-fg", "-color-success-muted", "-color-success-muted"), 2, 3);
        grid.add(colorBlock("-color-success-fg", "-color-success-subtle", "-color-success-subtle"), 3, 3);

        grid.add(colorBlock("-color-fg-emphasis", "-color-warning-emphasis", "-color-warning-emphasis"), 0, 4);
        grid.add(colorBlock("-color-warning-fg", "-color-bg-default", "-color-warning-emphasis"), 1, 4);
        grid.add(colorBlock("-color-warning-fg", "-color-warning-muted", "-color-warning-muted"), 2, 4);
        grid.add(colorBlock("-color-warning-fg", "-color-warning-subtle", "-color-warning-subtle"), 3, 4);

        grid.add(colorBlock("-color-fg-emphasis", "-color-danger-emphasis", "-color-danger-emphasis"), 0, 5);
        grid.add(colorBlock("-color-danger-fg", "-color-bg-default", "-color-danger-emphasis"), 1, 5);
        grid.add(colorBlock("-color-danger-fg", "-color-danger-muted", "-color-danger-muted"), 2, 5);
        grid.add(colorBlock("-color-danger-fg", "-color-danger-subtle", "-color-danger-subtle"), 3, 5);

        return grid;
    }

    private ColorBlock colorBlock(String fgColor, String bgColor, String borderColor) {
        var block = new ColorBlock(fgColor, bgColor, borderColor, bgBaseColor.getReadOnlyProperty());
        block.setOnAction(colorBlockActionHandler);
        blocks.add(block);
        return block;
    }

    // To calculate contrast ratio, we have to obtain all components colors first.
    // Unfortunately, JavaFX doesn't provide an API to observe when stylesheet changes has been applied.
    // The timer is introduced to defer widget update to a time when scene changes supposedly will be finished.
    public void updateColorInfo(Duration delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> blocks.forEach(ColorBlock::update));
            }
        }, delay.toMillis());
    }

    public ReadOnlyObjectProperty<Color> bgBaseColorProperty() {
        return bgBaseColor.getReadOnlyProperty();
    }
}
