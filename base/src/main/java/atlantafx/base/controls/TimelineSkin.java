/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.InvalidationListener;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

/**
 * Default skin for the {@link Timeline} control.
 *
 * <p>LEFT mode: [timestamp] [node/line] [content]
 * ALTERNATE mode: items alternate sides, with timestamp and content
 * grouped together on the same side of the center line.
 */
public class TimelineSkin extends SkinBase<Timeline> {

    private static final double NODE_SIZE = 1.2; // em

    private final VBox container = new VBox();
    private final InvalidationListener itemsListener;

    protected TimelineSkin(Timeline control) {
        super(control);

        container.getStyleClass().add("container");
        getChildren().setAll(container);

        itemsListener = o -> rebuild();
        control.getItems().addListener(itemsListener);
        control.alignmentProperty().addListener(o -> rebuild());

        rebuild();
    }

    private void rebuild() {
        container.getChildren().clear();

        Timeline control = getSkinnable();
        boolean alternate = control.getAlignment() == Timeline.Alignment.ALTERNATE;
        var items = control.getItems();

        for (int i = 0; i < items.size(); i++) {
            TimelineItem item = items.get(i);
            container.getChildren().add(
                    alternate ? createAlternateRow(item, i, items.size(), i % 2 == 0)
                              : createLeftRow(item, i, items.size())
            );
        }
    }

    /**
     * LEFT mode: [timestamp] [center] [content]
     */
    private HBox createLeftRow(TimelineItem item, int index, int total) {
        var row = new HBox();
        row.getStyleClass().add("timeline-item");

        var timestampLabel = createTimestampLabel(item);
        var center = createCenter(item, index, total);
        var contentLabel = createContentLabel(item);

        HBox.setHgrow(contentLabel, Priority.ALWAYS);
        row.getChildren().addAll(timestampLabel, center, contentLabel);
        return row;
    }

    /**
     * ALTERNATE mode: timestamp and content on opposite sides of center line.
     * Uses equal-width side columns so center line stays vertically aligned.
     * Layout: [leftCol(ALWAYS)] [center] [rightCol(ALWAYS)]
     */
    private HBox createAlternateRow(TimelineItem item, int index, int total, boolean isLeft) {
        var row = new HBox();
        row.getStyleClass().add("timeline-item");
        row.getStyleClass().add(isLeft ? "left" : "right");

        var timestampLabel = createTimestampLabel(item);
        var contentLabel = createContentLabel(item);
        contentLabel.setWrapText(true);
        var center = createCenter(item, index, total);

        // equal-width side columns keep center line vertically aligned
        var leftCol = new HBox();
        var rightCol = new HBox();
        leftCol.getStyleClass().add("side");
        rightCol.getStyleClass().add("side");
        leftCol.setMinWidth(0);
        rightCol.setMinWidth(0);
        leftCol.setPrefWidth(0);
        rightCol.setPrefWidth(0);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        if (isLeft) {
            leftCol.setAlignment(Pos.TOP_RIGHT);
            leftCol.getChildren().add(timestampLabel);
            rightCol.setAlignment(Pos.TOP_LEFT);
            rightCol.getChildren().add(contentLabel);
        } else {
            leftCol.setAlignment(Pos.TOP_RIGHT);
            leftCol.getChildren().add(contentLabel);
            rightCol.setAlignment(Pos.TOP_LEFT);
            rightCol.getChildren().add(timestampLabel);
        }

        row.getChildren().addAll(leftCol, center, rightCol);
        return row;
    }

    private Label createTimestampLabel(TimelineItem item) {
        var label = new Label();
        label.getStyleClass().add("timestamp");
        label.textProperty().bind(item.timestampProperty());
        return label;
    }

    private Label createContentLabel(TimelineItem item) {
        var label = new Label();
        label.getStyleClass().add("content");
        label.textProperty().bind(item.contentProperty());
        label.setWrapText(true);
        return label;
    }

    private VBox createCenter(TimelineItem item, int index, int total) {
        // node (dot or custom graphic)
        var node = new StackPane();
        node.getStyleClass().add("node");
        item.activatePseudoClasses(node);
        item.statusProperty().addListener((obs, old, val) -> item.activatePseudoClasses(node));

        if (item.getGraphic() != null) {
            node.getChildren().setAll(item.getGraphic());
        }
        item.graphicProperty().addListener((obs, old, val) -> {
            if (val != null) {
                node.getChildren().setAll(val);
            } else {
                node.getChildren().clear();
            }
        });

        boolean isLast = index == total - 1;

        var center = new VBox();
        center.getStyleClass().add("center");
        center.getChildren().addAll(
                createSpacer(),
                node,
                isLast ? createSpacer() : createLine()
        );
        return center;
    }

    private Region createSpacer() {
        return new Region();
    }

    private Region createLine() {
        var line = new Region();
        line.getStyleClass().add("line");
        return line;
    }

    @Override
    public void dispose() {
        getSkinnable().getItems().removeListener(itemsListener);
        super.dispose();
    }
}
