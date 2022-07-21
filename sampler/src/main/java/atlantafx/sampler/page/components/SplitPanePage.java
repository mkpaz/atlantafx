/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
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
    public String getName() { return NAME; }

    public SplitPanePage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(new FlowPane(20, 20,
                hSplitBlock().getRoot(),
                vSplitBlock().getRoot(),
                disabledSplitBlock().getRoot(),
                gridSplitBlock().getRoot()
        ));
    }

    private SampleBlock hSplitBlock() {
        var splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.5);
        splitPane.getItems().setAll(hBrick("Left"), hBrick("Right"));
        splitPane.setMinSize(200, 100);
        splitPane.setMaxSize(200, 100);

        return new SampleBlock("Horizontal", splitPane);
    }

    private SampleBlock vSplitBlock() {
        var splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPositions(0.5);
        splitPane.getItems().setAll(vBrick("Top"), hBrick("Bottom"));
        splitPane.setMinSize(100, 200);
        splitPane.setMaxSize(100, 200);

        return new SampleBlock("Vertical", splitPane);
    }

    private SampleBlock gridSplitBlock() {
        var topSplitPane = new SplitPane();
        topSplitPane.setOrientation(Orientation.HORIZONTAL);
        topSplitPane.setDividerPositions(0.5);
        topSplitPane.getItems().setAll(vBrick("Quarter 4"), hBrick("Quarter 1"));
        VBox.setVgrow(topSplitPane, Priority.ALWAYS);

        var bottomSplitPane = new SplitPane();
        bottomSplitPane.setOrientation(Orientation.HORIZONTAL);
        bottomSplitPane.setDividerPositions(0.5);
        bottomSplitPane.getItems().setAll(vBrick("Quarter 3"), hBrick("Quarter 2"));
        VBox.setVgrow(bottomSplitPane, Priority.ALWAYS);

        var doubleSplitPane = new SplitPane();
        doubleSplitPane.setOrientation(Orientation.VERTICAL);
        doubleSplitPane.setDividerPositions(0.5);
        doubleSplitPane.getItems().setAll(
                new VBox(topSplitPane) {{ setAlignment(Pos.CENTER); }},
                new VBox(bottomSplitPane) {{ setAlignment(Pos.CENTER); }}
        );
        doubleSplitPane.setMinSize(400, 200);
        doubleSplitPane.setMaxSize(400, 200);

        return new SampleBlock("Nested", doubleSplitPane);
    }

    private SampleBlock disabledSplitBlock() {
        var block = hSplitBlock();
        block.setText("Disabled");
        block.getContent().setDisable(true);

        return block;
    }

    private HBox hBrick(String text) {
        var brick = new HBox(new Text(text));
        brick.setAlignment(Pos.CENTER);
        return brick;
    }

    private VBox vBrick(String text) {
        var brick = new VBox(new Text(text));
        brick.setAlignment(Pos.CENTER);
        return brick;
    }
}
