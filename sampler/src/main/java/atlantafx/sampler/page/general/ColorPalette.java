/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.sampler.util.Controls.hyperlink;

import atlantafx.base.theme.Styles;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

class ColorPalette extends VBox {

    private final List<ColorPaletteBlock> blocks = new ArrayList<>();
    private final ReadOnlyObjectWrapper<Color> bgBaseColor = new ReadOnlyObjectWrapper<>(Color.WHITE);
    private final Consumer<ColorPaletteBlock> colorBlockActionHandler;

    public ColorPalette(Consumer<ColorPaletteBlock> blockClickedHandler) {
        super();

        this.colorBlockActionHandler = Objects.requireNonNull(blockClickedHandler);
        createView();
    }

    private void createView() {
        var headerLabel = new Label("Color Palette");
        headerLabel.getStyleClass().add(Styles.TITLE_4);

        var headerBox = new HBox();
        headerBox.getChildren().setAll(headerLabel);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getStyleClass().add("header");

        var noteText = new VBox(6);
        noteText.getChildren().setAll(
            new TextFlow(
                new Text("Color contrast between text and its background must meet "),
                hyperlink(
                    "required WCAG standards",
                    URI.create("https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html")
                ),
                new Text(":")
            ),
            new Text("  • 4.5:1 for normal text"),
            new Text("  • 3:1 for large text (>24px)"),
            new Text("  • 3:1 for UI elements and graphics"),
            new Text("  • no contrast requirement for decorative and disabled elements"),
            new Text(),
            new Text("Click on any color block to observe and modify color combination via built-in contrast checker.")
        );

        var colorGrid = colorGrid();

        backgroundProperty().addListener((obs, old, val) -> bgBaseColor.set(
            val != null && !val.getFills().isEmpty() ? (Color) val.getFills().get(0).getFill() : Color.WHITE
        ));

        getChildren().setAll(headerBox, noteText, colorGrid);
        setId("color-palette");
    }

    private GridPane colorGrid() {
        var grid = new GridPane();
        grid.getStyleClass().add("grid");

        grid.add(colorBlock("-color-fg-default", "-color-bg-default", "-color-border-default"), 0, 0);
        grid.add(colorBlock("-color-fg-default", "-color-bg-overlay", "-color-border-default"), 1, 0);
        grid.add(colorBlock("-color-fg-muted", "-color-bg-default", "-color-border-muted"), 2, 0);
        grid.add(colorBlock("-color-fg-subtle", "-color-bg-default", "-color-border-subtle"), 3, 0);

        grid.add(colorBlock("-color-fg-emphasis", "-color-accent-emphasis", "-color-accent-emphasis"), 0, 1);
        grid.add(colorBlock("-color-accent-fg", "-color-bg-default", "-color-accent-emphasis"), 1, 1);
        grid.add(colorBlock("-color-fg-default", "-color-accent-muted", "-color-accent-emphasis"), 2, 1);
        grid.add(colorBlock("-color-accent-fg", "-color-accent-subtle", "-color-accent-emphasis"), 3, 1);

        grid.add(
            colorBlock("-color-fg-emphasis", "-color-neutral-emphasis-plus", "-color-neutral-emphasis-plus"), 0, 2
        );
        grid.add(colorBlock("-color-fg-emphasis", "-color-neutral-emphasis", "-color-neutral-emphasis"), 1, 2);
        grid.add(colorBlock("-color-fg-default", "-color-neutral-muted", "-color-neutral-emphasis"), 2, 2);
        grid.add(colorBlock("-color-fg-default", "-color-neutral-subtle", "-color-neutral-emphasis"), 3, 2);

        grid.add(colorBlock("-color-fg-emphasis", "-color-success-emphasis", "-color-success-emphasis"), 0, 3);
        grid.add(colorBlock("-color-success-fg", "-color-bg-default", "-color-success-emphasis"), 1, 3);
        grid.add(colorBlock("-color-fg-default", "-color-success-muted", "-color-success-emphasis"), 2, 3);
        grid.add(colorBlock("-color-success-fg", "-color-success-subtle", "-color-success-emphasis"), 3, 3);

        grid.add(colorBlock("-color-fg-emphasis", "-color-warning-emphasis", "-color-warning-emphasis"), 0, 4);
        grid.add(colorBlock("-color-warning-fg", "-color-bg-default", "-color-warning-emphasis"), 1, 4);
        grid.add(colorBlock("-color-fg-default", "-color-warning-muted", "-color-warning-emphasis"), 2, 4);
        grid.add(colorBlock("-color-warning-fg", "-color-warning-subtle", "-color-warning-emphasis"), 3, 4);

        grid.add(colorBlock("-color-fg-emphasis", "-color-danger-emphasis", "-color-danger-emphasis"), 0, 5);
        grid.add(colorBlock("-color-danger-fg", "-color-bg-default", "-color-danger-emphasis"), 1, 5);
        grid.add(colorBlock("-color-fg-default", "-color-danger-muted", "-color-danger-emphasis"), 2, 5);
        grid.add(colorBlock("-color-danger-fg", "-color-danger-subtle", "-color-danger-emphasis"), 3, 5);

        return grid;
    }

    private ColorPaletteBlock colorBlock(String fgColor, String bgColor, String borderColor) {
        var block = new ColorPaletteBlock(fgColor, bgColor, borderColor, bgBaseColor.getReadOnlyProperty());
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

    public ReadOnlyObjectProperty<Color> bgBaseColorProperty() {
        return bgBaseColor.getReadOnlyProperty();
    }
}
