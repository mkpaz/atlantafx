/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * The default skin for the {@link SegmentedControl}.
 */
public class SegmentedControlSkin extends BehaviorSkinBase<SegmentedControl, SegmentedControlBehavior> {

    protected final Pane rootContainer = new Pane();
    protected final Pane backgroundBox = new Pane();
    protected final HBox labelBox = new HBox();
    protected final Pane indicator = new Pane();

    protected final ListChangeListener<Node> segmentListener;
    protected final ChangeListener<Toggle> selectionListener;
    protected final ChangeListener<ToggleGroup> toggleGroupListener;

    protected boolean initialized = false;
    protected boolean animating = false;

    public SegmentedControlSkin(SegmentedControl control) {
        super(control);

        indicator.setMouseTransparent(true);
        indicator.getStyleClass().add("indicator");

        backgroundBox.getStyleClass().add("background");
        backgroundBox.getChildren().setAll(indicator);
        backgroundBox.setMouseTransparent(true);

        backgroundBox.prefWidthProperty().bind(labelBox.widthProperty());
        backgroundBox.prefHeightProperty().bind(labelBox.heightProperty());
        backgroundBox.paddingProperty().bind(labelBox.paddingProperty());

        labelBox.getStyleClass().add("labels");

        rootContainer.getStyleClass().add("root-container");
        rootContainer.getChildren().setAll(backgroundBox, labelBox);
        getChildren().add(rootContainer);

        segmentListener = change -> {
            while (change.next()) {
                for (var node : change.getAddedSubList()) {
                    if (node instanceof Toggle toggle) {
                        node.setOnMousePressed(e -> getBehavior().select(toggle));
                        toggle.setToggleGroup(getSkinnable().getToggleGroup());
                    }
                }
                for (var node : change.getRemoved()) {
                    disposeSegment(node);
                }
            }

            updateProperties();
        };
        labelBox.getChildren().addListener(segmentListener);

        selectionListener = (obs, old, val) -> {
            if (val instanceof ToggleLabel label) {
                animateIndicator(label);
            }
        };
        control.getToggleGroup().selectedToggleProperty().addListener(selectionListener);

        toggleGroupListener = (obs, old, val) ->
                                  control.getSegments().forEach(btn -> btn.setToggleGroup(val));
        control.toggleGroupProperty().addListener(toggleGroupListener);

        // populate container and watch for changes
        Bindings.bindContent(labelBox.getChildren(), control.getSegments());

        if (!control.getSegments().isEmpty()) {
            control.getSegments().getFirst().setSelected(true);
        }
    }

    @Override
    public SegmentedControlBehavior createDefaultBehavior() {
        return new SegmentedControlBehavior(getSkinnable(), this);
    }

    @Override
    public void dispose() {
        labelBox.getChildren().removeListener(segmentListener);
        getSkinnable().getToggleGroup().selectedToggleProperty().removeListener(selectionListener);
        getSkinnable().toggleGroupProperty().addListener(toggleGroupListener);

        Bindings.unbindContent(labelBox.getChildren(), getSkinnable().getSegments());

        getChildren().forEach(this::disposeSegment);
    }

    //=========================================================================

    protected void animateIndicator(ToggleLabel dest) {
        var duration = getSkinnable().getAnimationDuration();

        if (!initialized) {
            // To set initial state we do need to know the selected label position
            // and size, which is only available after control is displayed.
            Platform.runLater(() -> moveIndicator(dest));
            initialized = true;
            return;
        }

        if (Duration.ZERO.equals(duration)) {
            moveIndicator(dest);
            return;
        }

        animating = true;
        var timeline = new Timeline(
            new KeyFrame(
                getSkinnable().getAnimationDuration(),
                new KeyValue(indicator.layoutXProperty(), dest.getLayoutX()),
                new KeyValue(indicator.layoutYProperty(), dest.getLayoutY()),
                new KeyValue(indicator.prefWidthProperty(), dest.getWidth()),
                new KeyValue(indicator.prefHeightProperty(), dest.getHeight())
            )
        );
        timeline.setOnFinished(event -> {
            moveIndicator(dest);
            animating = false;
        });
        timeline.play();
    }

    protected void moveIndicator(ToggleLabel dest) {
        if (dest.getLayoutX() == indicator.getLayoutX()
            && dest.getLayoutY() == indicator.getLayoutY()
            && dest.getWidth() == indicator.getWidth()
            && dest.getHeight() == indicator.getHeight()
        ) {
            return;
        }

        indicator.setLayoutX(dest.getLayoutX());
        indicator.setLayoutY(dest.getLayoutY());
        indicator.setPrefWidth(dest.getWidth());
        indicator.setPrefHeight(dest.getHeight());
    }

    protected void updateProperties() {
        var segments = getSkinnable().getSegments();
        for (int i = 0; i < segments.size(); i++) {
            var segment = segments.get(i);
            segment.getProperties().put(ToggleLabel.INDEX_PROPERTY, i);
        }
    }

    protected void disposeSegment(Node node) {
        node.setOnMousePressed(null);
        node.getProperties().remove(ToggleLabel.INDEX_PROPERTY);
        if (node instanceof Toggle toggle) {
            toggle.setToggleGroup(null);
        }
    }

    //=========================================================================

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        var selected = getSkinnable().getToggleGroup().getSelectedToggle();
        if (!animating && selected instanceof ToggleLabel toggle) {
            moveIndicator(toggle);
        }
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset,
                                     double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }
}
