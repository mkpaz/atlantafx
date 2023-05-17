/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class SplitPanePage extends OutlinePage {

    public static final String NAME = "SplitPane";

    @Override
    public String getName() {
        return NAME;
    }

    public SplitPanePage() {
        super();

        addPageHeader();
        addFormattedText("""
            A control that has two or more sides, each separated by a divider, which can be \
            dragged by the user to give more space to one of the sides, resulting in the other \
            side shrinking by an equal amount."""
        );
        addSection("Horizontal", hSplitExample());
        addSection("Vertical", vSplitExample());
        addSection("Nested", nestedSplitExample());
        addSection("Multiple Dividers", multiDividersSplitExample());
    }

    private ExampleBox hSplitExample() {
        //snippet_1:start
        var sp = new SplitPane(
            createBox("Left"),
            createBox("Right")
        );
        sp.setOrientation(Orientation.HORIZONTAL);
        sp.setDividerPositions(0.5);
        //snippet_1:end

        sp.setMinSize(400, 100);
        sp.setMaxSize(400, 100);
        var box = new HBox(sp);

        return new ExampleBox(box, new Snippet(getClass(), 1));
    }

    private ExampleBox vSplitExample() {
        //snippet_2:start
        var sp = new SplitPane(
            createBox("Top"),
            createBox("Bottom")
        );
        sp.setOrientation(Orientation.VERTICAL);
        sp.setDividerPositions(0.5);
        //snippet_2:end

        sp.setMinSize(400, 200);
        sp.setMaxSize(400, 200);
        var box = new HBox(sp);

        return new ExampleBox(box, new Snippet(getClass(), 2));
    }

    private ExampleBox nestedSplitExample() {
        //snippet_3:start
        var topSplitPane = new SplitPane(
            createBox("Quarter 4"),
            createBox("Quarter 1")
        );
        topSplitPane.setOrientation(Orientation.HORIZONTAL);
        topSplitPane.setDividerPositions(0.5);
        VBox.setVgrow(topSplitPane, Priority.ALWAYS);

        var topBox = new VBox(topSplitPane);
        topBox.setAlignment(Pos.CENTER);

        var bottomSplitPane = new SplitPane(
            createBox("Quarter 3"),
            createBox("Quarter 2")
        );
        bottomSplitPane.setOrientation(Orientation.HORIZONTAL);
        bottomSplitPane.setDividerPositions(0.5);
        VBox.setVgrow(bottomSplitPane, Priority.ALWAYS);

        var bottomBox = new VBox(bottomSplitPane);
        bottomBox.setAlignment(Pos.CENTER);

        var doubleSplitPane = new SplitPane(topBox, bottomBox);
        doubleSplitPane.setOrientation(Orientation.VERTICAL);
        doubleSplitPane.setDividerPositions(0.5);
        //snippet_3:end

        doubleSplitPane.setMinSize(400, 200);
        doubleSplitPane.setMaxSize(400, 200);
        var box = new HBox(doubleSplitPane);

        return new ExampleBox(box, new Snippet(getClass(), 3));
    }

    private ExampleBox multiDividersSplitExample() {
        //snippet_4:start
        var sp = new SplitPane(
            createBox("First"),
            createBox("Second"),
            createBox("Third"),
            createBox("Fourth")
        );
        sp.setOrientation(Orientation.HORIZONTAL);
        sp.setDividerPositions(0.25, 0.5, 0.75);
        //snippet_4:end

        sp.setMinSize(600, 200);
        sp.setMaxSize(600, 200);
        var box = new HBox(sp);

        return new ExampleBox(box, new Snippet(getClass(), 4));
    }

    private HBox createBox(String s) {
        var label = new Label(s);
        label.setMinSize(120, 80);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color:-color-accent-subtle;");

        var box = new HBox(label);
        box.setAlignment(Pos.CENTER);

        return box;
    }
}
