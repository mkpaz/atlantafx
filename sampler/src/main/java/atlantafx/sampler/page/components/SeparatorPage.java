/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;
import static javafx.geometry.Pos.CENTER;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class SeparatorPage extends AbstractPage {

    public static final String NAME = "Separator";
    private static final int SPACING = 50;
    private static final int PANE_SIZE = 100;

    @Override
    public String getName() {
        return NAME;
    }

    public SeparatorPage() {
        super();
        setUserContent(new FlowPane(
            Page.PAGE_HGAP, Page.PAGE_VGAP,
            orientationSample(),
            sizeSample()
        ));
    }

    private SampleBlock orientationSample() {
        var hBox = new HBox(
            createPane("Left", VERTICAL),
            new Separator(VERTICAL),
            createPane("Right", VERTICAL)
        );
        hBox.setAlignment(CENTER);

        var vBox = new VBox(
            createPane("Top", HORIZONTAL),
            new Separator(HORIZONTAL),
            createPane("Bottom", HORIZONTAL)
        );
        vBox.setAlignment(CENTER);

        return new SampleBlock("Orientation", new HBox(SPACING, hBox, vBox));
    }

    private SampleBlock sizeSample() {
        var smallSep = new Separator(VERTICAL);
        smallSep.getStyleClass().add(Styles.SMALL);
        var smallBox = new HBox(
            createPane("Left", VERTICAL),
            smallSep,
            createPane("Right", VERTICAL)
        );
        smallBox.setAlignment(CENTER);

        var mediumSep = new Separator(VERTICAL);
        mediumSep.getStyleClass().add(Styles.MEDIUM);
        var mediumBox = new HBox(
            createPane("Left", VERTICAL),
            mediumSep,
            createPane("Right", VERTICAL)
        );
        mediumBox.setAlignment(CENTER);

        var largeSep = new Separator(VERTICAL);
        largeSep.getStyleClass().add(Styles.LARGE);
        var largeBox = new HBox(
            createPane("Left", VERTICAL),
            largeSep,
            createPane("Right", VERTICAL)
        );
        largeBox.setAlignment(CENTER);

        return new SampleBlock("Size", new HBox(SPACING, smallBox, mediumBox, largeBox));
    }

    private Pane createPane(String text, Orientation orientation) {
        var pane = new StackPane();
        pane.getChildren().setAll(new Label(text));
        pane.getStyleClass().add("bordered");

        if (orientation == HORIZONTAL) {
            pane.setMinHeight(PANE_SIZE);
            pane.setPrefHeight(PANE_SIZE);
            pane.setMaxHeight(PANE_SIZE);
            pane.setMinWidth(PANE_SIZE);
        }

        if (orientation == VERTICAL) {
            pane.setMinWidth(PANE_SIZE);
            pane.setPrefWidth(PANE_SIZE);
            pane.setMaxWidth(PANE_SIZE);
            pane.setMinHeight(PANE_SIZE);
        }

        return pane;
    }
}
