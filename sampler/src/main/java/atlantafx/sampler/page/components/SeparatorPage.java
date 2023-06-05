/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class SeparatorPage extends OutlinePage {

    public static final String NAME = "Separator";

    @Override
    public String getName() {
        return NAME;
    }

    public SeparatorPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A horizontal or vertical separator line. A horizontal separator occupies \
            the full horizontal space allocated to it (less padding), and a vertical \
            separator occupies the full vertical space allocated to it (less padding)."""
        );
        addSection("Orientation", orientationExample());
        addSection("Size", sizeExample());
    }

    private ExampleBox orientationExample() {
        //snippet_1:start
        var hbox = new HBox(
            createPane("Left", Orientation.VERTICAL),
            new Separator(Orientation.VERTICAL),
            createPane("Right", Orientation.VERTICAL)
        );

        var vbox = new VBox(
            createPane("Top", Orientation.HORIZONTAL),
            new Separator(Orientation.HORIZONTAL),
            createPane("Bottom", Orientation.HORIZONTAL)
        );
        //snippet_1:end

        var box = new HBox(50, hbox, vbox);

        return new ExampleBox(box, new Snippet(getClass(), 1));
    }

    private ExampleBox sizeExample() {
        //snippet_2:start
        var smallSep = new Separator(Orientation.VERTICAL);
        smallSep.getStyleClass().add(Styles.SMALL);
        var smallBox = new HBox(
            createPane("Left", Orientation.VERTICAL),
            smallSep,
            createPane("Right", Orientation.VERTICAL)
        );

        var mediumSep = new Separator(Orientation.VERTICAL);
        mediumSep.getStyleClass().add(Styles.MEDIUM);
        var mediumBox = new HBox(
            createPane("Left", Orientation.VERTICAL),
            mediumSep,
            createPane("Right", Orientation.VERTICAL)
        );

        var largeSep = new Separator(Orientation.VERTICAL);
        largeSep.getStyleClass().add(Styles.LARGE);
        var largeBox = new HBox(
            createPane("Left", Orientation.VERTICAL),
            largeSep,
            createPane("Right", Orientation.VERTICAL)
        );
        //snippet_2:end

        var grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(50);
        grid.addRow(0, smallBox, mediumBox);
        grid.addRow(1, largeBox);

        return new ExampleBox(grid, new Snippet(getClass(), 2));
    }

    private Pane createPane(String text, Orientation orientation) {
        var pane = new StackPane(new Label(text));
        pane.getStyleClass().add("bordered");
        pane.setMinSize(100, 100);

        if (orientation == Orientation.HORIZONTAL) {
            pane.setPrefHeight(100);
            pane.setMaxHeight(100);
        }

        if (orientation == Orientation.VERTICAL) {
            pane.setPrefWidth(100);
            pane.setMaxWidth(100);
        }

        return pane;
    }
}
