/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SplitPanePage extends AbstractPage {

    public static final String NAME = "SplitPane";

    @Override
    public String getName() {
        return NAME;
    }

    public SplitPanePage() {
        super();
        setUserContent(new FlowPane(
            Page.PAGE_VGAP, Page.PAGE_HGAP,
            hSplitSample(),
            vSplitSample(),
            disabledSample(),
            gridSample()
        ));
    }

    private SampleBlock hSplitSample() {
        var splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.5);
        splitPane.getItems().setAll(createBox("Left"), createBox("Right"));
        splitPane.setMinSize(200, 100);
        splitPane.setMaxSize(200, 100);
        return new SampleBlock("Horizontal", splitPane);
    }

    private SampleBlock vSplitSample() {
        var splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPositions(0.5);
        splitPane.getItems().setAll(createBox("Top"), createBox("Bottom"));
        splitPane.setMinSize(100, 200);
        splitPane.setMaxSize(100, 200);
        return new SampleBlock("Vertical", splitPane);
    }

    private SampleBlock gridSample() {
        var topSplitPane = new SplitPane();
        topSplitPane.setOrientation(Orientation.HORIZONTAL);
        topSplitPane.setDividerPositions(0.5);
        topSplitPane.getItems().setAll(createBox("Quarter 4"), createBox("Quarter 1"));
        VBox.setVgrow(topSplitPane, Priority.ALWAYS);

        var topBox = new VBox(topSplitPane);
        topBox.setAlignment(Pos.CENTER);

        var bottomSplitPane = new SplitPane();
        bottomSplitPane.setOrientation(Orientation.HORIZONTAL);
        bottomSplitPane.setDividerPositions(0.5);
        bottomSplitPane.getItems().setAll(createBox("Quarter 3"), createBox("Quarter 2"));
        VBox.setVgrow(bottomSplitPane, Priority.ALWAYS);

        var bottomBox = new VBox(bottomSplitPane);
        bottomBox.setAlignment(Pos.CENTER);

        var doubleSplitPane = new SplitPane();
        doubleSplitPane.setOrientation(Orientation.VERTICAL);
        doubleSplitPane.setDividerPositions(0.5);
        doubleSplitPane.getItems().setAll(topBox, bottomBox);
        doubleSplitPane.setMinSize(400, 200);
        doubleSplitPane.setMaxSize(400, 200);

        return new SampleBlock("Nested", doubleSplitPane);
    }

    private SampleBlock disabledSample() {
        var block = hSplitSample();
        block.setTitle("Disabled");
        block.getContent().setDisable(true);

        return block;
    }

    private HBox createBox(String text) {
        var brick = new HBox(new Text(text));
        brick.setAlignment(Pos.CENTER);
        return brick;
    }
}
