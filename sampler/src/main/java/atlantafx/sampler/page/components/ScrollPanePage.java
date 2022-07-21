/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ScrollPanePage extends AbstractPage {

    public static final String NAME = "ScrollPane";

    @Override
    public String getName() { return NAME; }

    public ScrollPanePage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(new FlowPane(20, 20,
                hScrollBlock().getRoot(),
                vScrollBlock().getRoot(),
                gridScrollBlock().getRoot(),
                disabledBlock().getRoot()
        ));
    }

    private SampleBlock hScrollBlock() {
        var scrollPane = new ScrollPane();
        scrollPane.setMaxHeight(100);
        scrollPane.setMaxWidth(300);
        scrollPane.setContent(new HBox(2,
                new Rectangle(200, 100, Color.GREEN),
                new Rectangle(200, 100, Color.RED)
        ));
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return new SampleBlock("Horizontal scrolling", scrollPane);
    }

    private SampleBlock vScrollBlock() {
        var scrollPane = new ScrollPane();
        scrollPane.setMaxHeight(100);
        scrollPane.setMaxWidth(300);
        scrollPane.setContent(new VBox(2,
                new Rectangle(300, 75, Color.GREEN),
                new Rectangle(300, 75, Color.RED)
        ));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return new SampleBlock("Vertical scrolling", scrollPane);
    }

    private SampleBlock gridScrollBlock() {
        var grid = new GridPane();
        grid.add(new Rectangle(200, 75, Color.GREEN), 0, 0);
        grid.add(new Rectangle(200, 75, Color.RED), 1, 0);
        grid.add(new Rectangle(200, 75, Color.RED), 0, 1);
        grid.add(new Rectangle(200, 75, Color.GREEN), 1, 1);
        grid.setHgap(2);
        grid.setVgap(2);

        var gridScroll = new ScrollPane();
        gridScroll.setMaxHeight(100);
        gridScroll.setMaxWidth(300);
        gridScroll.setContent(grid);

        return new SampleBlock("Horizontal & vertical scrolling", gridScroll);
    }

    private SampleBlock disabledBlock() {
        var block = gridScrollBlock();
        block.setText("Disabled");
        block.getContent().setDisable(true);

        return block;
    }
}
