/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ScrollPanePage extends AbstractPage {

    public static final String NAME = "ScrollPane";
    private static final int SPACING = 1;

    @Override
    public String getName() {
        return NAME;
    }

    public ScrollPanePage() {
        super();
        setUserContent(new FlowPane(
            Page.PAGE_VGAP, Page.PAGE_HGAP,
            horizontalScrollSample(),
            verticalScrollSample(),
            gridScrollSample(),
            disabledSample()
        ));
    }

    private SampleBlock horizontalScrollSample() {
        var scrollPane = new ScrollPane();
        scrollPane.setMaxHeight(100);
        scrollPane.setMaxWidth(300);
        scrollPane.setContent(new HBox(SPACING,
            createRegion(200, 100, "-color-success-emphasis"),
            createRegion(200, 100, "-color-danger-emphasis")
        ));
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return new SampleBlock("Horizontal Scrolling", scrollPane);
    }

    private SampleBlock verticalScrollSample() {
        var scrollPane = new ScrollPane();
        scrollPane.setMaxHeight(100);
        scrollPane.setMaxWidth(300);
        scrollPane.setContent(new VBox(SPACING,
            createRegion(300, 75, "-color-success-emphasis"),
            createRegion(300, 75, "-color-danger-emphasis")
        ));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return new SampleBlock("Vertical Scrolling", scrollPane);
    }

    private SampleBlock gridScrollSample() {
        var grid = new GridPane();
        grid.add(createRegion(200, 75, "-color-success-emphasis"), 0, 0);
        grid.add(createRegion(200, 75, "-color-danger-emphasis"), 1, 0);
        grid.add(createRegion(200, 75, "-color-danger-emphasis"), 0, 1);
        grid.add(createRegion(200, 75, "-color-success-emphasis"), 1, 1);
        grid.setHgap(SPACING);
        grid.setVgap(SPACING);

        var gridScroll = new ScrollPane();
        gridScroll.setMaxHeight(100);
        gridScroll.setMaxWidth(300);
        gridScroll.setContent(grid);

        return new SampleBlock("Scrolling", gridScroll);
    }

    private SampleBlock disabledSample() {
        var block = gridScrollSample();
        block.setTitle("Disabled");
        block.getContent().setDisable(true);
        return block;
    }

    private Region createRegion(int width, int height, String bg) {
        var r = new Region();
        r.setMinSize(width, height);
        r.setPrefSize(width, height);
        r.setMaxSize(width, height);
        r.setStyle("-fx-background-color:" + bg + ";");
        return r;
    }
}
