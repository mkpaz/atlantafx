/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;
import static javafx.geometry.Pos.CENTER;

public final class SeparatorPage extends AbstractPage {

    public static final String NAME = "Separator";
    private static final int BRICK_SIZE = 100;

    @Override
    public String getName() { return NAME; }

    public SeparatorPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(
                orientationSamples(),
                sizeSamples()
        );
    }

    private FlowPane orientationSamples() {
        var hBox = new HBox(brick("Left", VERTICAL), new Separator(VERTICAL), brick("Right", VERTICAL));
        hBox.setAlignment(CENTER);
        var hBlock = new SampleBlock("Vertical", hBox);

        var vBox = new VBox(brick("Top", HORIZONTAL), new Separator(HORIZONTAL), brick("Bottom", HORIZONTAL));
        vBox.setAlignment(CENTER);
        var vBlock = new SampleBlock("Horizontal", vBox);

        var root = new FlowPane(20, 20);
        root.getChildren().setAll(
                hBlock.getRoot(),
                vBlock.getRoot()
        );

        return root;
    }

    private FlowPane sizeSamples() {
        var smallSep = new Separator(VERTICAL);
        smallSep.getStyleClass().add(Styles.SMALL);
        var smallBox = new HBox(brick("Left", VERTICAL), smallSep, brick("Right", VERTICAL));
        smallBox.setAlignment(CENTER);
        var smallBlock = new SampleBlock("Small", smallBox);

        var mediumSep = new Separator(VERTICAL);
        mediumSep.getStyleClass().add(Styles.MEDIUM);
        var mediumBox = new HBox(brick("Left", VERTICAL), mediumSep, brick("Right", VERTICAL));
        mediumBox.setAlignment(CENTER);
        var mediumBlock = new SampleBlock("Medium", mediumBox);

        var largeSep = new Separator(VERTICAL);
        largeSep.getStyleClass().add(Styles.LARGE);
        var largeBox = new HBox(brick("Left", VERTICAL), largeSep, brick("Right", VERTICAL));
        largeBox.setAlignment(CENTER);
        var largeBlock = new SampleBlock("Large", largeBox);

        var root = new FlowPane(20, 20);
        root.getChildren().setAll(
                smallBlock.getRoot(),
                mediumBlock.getRoot(),
                largeBlock.getRoot()
        );

        return root;
    }

    private Pane brick(String text, Orientation orientation) {
        var root = new StackPane();
        root.getChildren().setAll(new Label(text));
        root.getStyleClass().add("bordered");

        if (orientation == HORIZONTAL) {
            root.setMinHeight(BRICK_SIZE);
            root.setPrefHeight(BRICK_SIZE);
            root.setMaxHeight(BRICK_SIZE);
            root.setMinWidth(BRICK_SIZE);
        }

        if (orientation == VERTICAL) {
            root.setMinWidth(BRICK_SIZE);
            root.setPrefWidth(BRICK_SIZE);
            root.setMaxWidth(BRICK_SIZE);
            root.setMinHeight(BRICK_SIZE);
        }

        return root;
    }
}
