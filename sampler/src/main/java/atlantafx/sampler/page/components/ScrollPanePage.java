/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class ScrollPanePage extends AbstractPage {

    public static final String NAME = "ScrollPane";

    @Override
    public String getName() {
        return NAME;
    }

    public ScrollPanePage() {
        super();

        addPageHeader();
        addFormattedText("""
            A control that provides a scrolled, clipped viewport of its contents. \
            It allows the user to scroll the content around either directly (panning) \
            or by using scroll bars.
                        
            The [i]ScrollPane[/i] allows specification of the scroll bar policy, which \
            determines when scroll bars are displayed: always, never, or only when they \
            are needed. The scroll bar policy can be specified independently for the \
            horizontal and vertical scroll bars."""
        );
        addNode(new FlowPane(
            40, 40,
            horizontalScrollExample(),
            verticalScrollExample(),
            gridScrollExample()
        ));
    }

    private ScrollPane horizontalScrollExample() {
        var scrollPane = new ScrollPane();
        scrollPane.setMaxHeight(100);
        scrollPane.setMaxWidth(300);
        scrollPane.setContent(new HBox(1,
            createRegion(200, 100, "-color-success-emphasis"),
            createRegion(200, 100, "-color-danger-emphasis")
        ));
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return scrollPane;
    }

    private ScrollPane verticalScrollExample() {
        var scrollPane = new ScrollPane();
        scrollPane.setMaxHeight(100);
        scrollPane.setMaxWidth(300);
        scrollPane.setContent(new VBox(1,
            createRegion(300, 75, "-color-success-emphasis"),
            createRegion(300, 75, "-color-danger-emphasis")
        ));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return scrollPane;
    }

    private ScrollPane gridScrollExample() {
        var grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(1);
        grid.addColumn(0,
            createRegion(200, 75, "-color-success-emphasis"),
            createRegion(200, 75, "-color-danger-emphasis")
        );
        grid.addColumn(1,
            createRegion(200, 75, "-color-danger-emphasis"),
            createRegion(200, 75, "-color-success-emphasis")
        );

        var gridScroll = new ScrollPane(grid);
        gridScroll.setMaxHeight(100);
        gridScroll.setMaxWidth(300);

        return gridScroll;
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
