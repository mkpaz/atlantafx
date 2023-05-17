/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public final class ColorPickerPage extends OutlinePage {

    public static final String NAME = "ColorPicker";

    @Override
    public String getName() {
        return NAME;
    }

    public ColorPickerPage() {
        super();

        addPageHeader();
        addFormattedText("""
            ColorPicker control allows the user to select a color from either \
            a standard palette of colors with a simple one click selection or \
            define their own custom color."""
        );
        addSection("Usage", usageExample());
        addSection("Style", styleExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var cp = new ColorPicker();
        cp.setValue(Color.RED);
        //snippet_1:end

        var box = new HBox(cp);
        box.setMinHeight(50);

        var description = BBCodeParser.createFormattedText("""
            The [i]ColorPicker[/i] control provides a color palette with a predefined \
            set of colors. If the user does not want to choose from the predefined set, \
            they can create a custom color by interacting with a custom color dialog. \
            This dialog provides RGB, HSB and Web modes of interaction, to create new \
            colors. It also lets the opacity of the color to be modified."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox styleExample() {
        //snippet_2:start
        var cp1 = new ColorPicker();
        cp1.setValue(Color.RED);
        cp1.getStyleClass().add(ColorPicker.STYLE_CLASS_BUTTON);

        var cp2 = new ColorPicker();
        cp2.setValue(Color.GREEN);
        cp2.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);

        var cp3 = new ColorPicker();
        cp3.setValue(Color.BLUE);
        cp3.setStyle("-fx-color-label-visible: false");
        //snippet_2:end

        var grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(10);
        grid.addRow(0,
            captionLabel("STYLE_CLASS_BUTTON"),
            captionLabel("STYLE_CLASS_SPLIT_BUTTON"),
            captionLabel("-fx-color-label-visible")
        );
        grid.addRow(1, cp1, cp2, cp3);

        var description = BBCodeParser.createFormattedText("""
            The [i]ColorPicker[/i] control can be styled in two ways: a simple \
            [i]Button[/i] mode or the default [i]MenuButton[/i] mode. While there \
            is also a [i]SplitMenuButton[/i] mode available, it is not supported by \
            AtlantaFX and looks the same as the default option."""
        );

        return new ExampleBox(grid, new Snippet(getClass(), 2), description);
    }
}
