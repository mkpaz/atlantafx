/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a loupe/magnifying glass scanning animation.
 */
public class LoupeSearchSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "loupe-search-spin";
    public static final double DEFAULT_DURATION = 2.0;
    public static final double DEFAULT_SIZE = 36.0;

    // geometric proportions relative to a base size of 80px
    protected static final double LENS_DIAMETER_RATIO = 30.0 / 80.0;
    protected static final double BORDER_WIDTH_RATIO = 6.0 / 80.0;
    protected static final double HANDLE_WIDTH_RATIO = 6.0 / 80.0;
    protected static final double HANDLE_HEIGHT_RATIO = 24.0 / 80.0;
    protected static final double ANIM_OFFSET_RATIO = 10.0 / 80.0;

    // handle
    protected static final double HANDLE_ANGLE_DEG = -45.0;
    protected static final double HANDLE_OFFSET_FACTOR = 0.83;

    protected Spin spin;
    protected Pane root;
    protected Circle glassCircle;
    protected Rectangle handleRect;
    protected Group loupeGroup;

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;

    /**
     * Constructs a new {@code LoupeSearchSpin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public LoupeSearchSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code LoupeSearchSpin} with specified size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the control
     */
    public LoupeSearchSpin(Spin spin, double size) {
        this.spin = spin;
        this.size = size > 0 ? size : DEFAULT_SIZE;

        getSkinnable().getStyleClass().add(STYLE_CLASS);
        construct();
    }

    /** Creates a new {@link Spin} with the default duration. */
    public static Spin create() {
        return create(null);
    }

    /** Creates a new {@link Spin} with the given duration. */
    public static Spin create(@Nullable Duration duration) {
        Spin spin = new Spin(Objects.requireNonNullElse(duration, Duration.seconds(DEFAULT_DURATION)));
        spin.setSkin(new LoupeSearchSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        root = new Pane();

        double lensDiameter = size * LENS_DIAMETER_RATIO;
        double radius = lensDiameter / 2.0;
        double strokeWidth = size * BORDER_WIDTH_RATIO;
        double handleWidth = size * HANDLE_WIDTH_RATIO;
        double handleHeight = size * HANDLE_HEIGHT_RATIO;

        // lens with center at (0, 0)
        glassCircle = new Circle(radius);
        glassCircle.setFill(null);
        glassCircle.setStroke(spin.getPrimaryColor());
        glassCircle.setStrokeWidth(strokeWidth);
        glassCircle.setStrokeType(StrokeType.INSIDE);

        // loupe handle
        handleRect = new Rectangle(handleWidth, handleHeight);
        handleRect.setFill(spin.getPrimaryColor());
        handleRect.setStroke(null);
        handleRect.setArcWidth(handleWidth);
        handleRect.setArcHeight(handleWidth);

        // center horizontally
        handleRect.setX(-handleWidth / 2.0);

        // offset down to the lens rim
        double handleStartOffset = radius * HANDLE_OFFSET_FACTOR;
        handleRect.setY(handleStartOffset);

        // rotate around center (0, 0)
        var handleRotate = new Rotate(HANDLE_ANGLE_DEG, 0, 0);
        handleRect.getTransforms().add(handleRotate);

        loupeGroup = new Group(glassCircle, handleRect);

        double handleReach = (handleStartOffset + handleHeight) * (Math.sqrt(2) / 2.0);
        double fullVisualSpan = radius + handleReach;
        double targetAnimOffset = size * ANIM_OFFSET_RATIO;
        double requiredCanvasSize = fullVisualSpan + (2.0 * targetAnimOffset);

        double scale = size / requiredCanvasSize;
        loupeGroup.setScaleX(scale);
        loupeGroup.setScaleY(scale);

        double centerShift = (handleReach - radius) / 2.0 * scale;
        loupeGroup.setLayoutX((size / 2.0) - centerShift);
        loupeGroup.setLayoutY((size / 2.0) - centerShift);

        root.getChildren().add(loupeGroup);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(_ -> updateColors()),
            spin.sceneProperty().subscribe(scene -> {
                if (scene != null) {
                    if (autostart) {
                        start();
                    }
                } else {
                    stop();
                }
            })
        );

        updateColors();
    }

    protected Timeline initTimeline() {
        Duration totalDuration = spin.getDuration();

        double offset = size * ANIM_OFFSET_RATIO;

        // loupe path motion along search square
        var nextTimeline = new Timeline(
            // 0% - (-offset, -offset)
            new KeyFrame(Duration.ZERO,
                new KeyValue(loupeGroup.translateXProperty(), -offset),
                new KeyValue(loupeGroup.translateYProperty(), -offset)
            ),
            // 25% - (-offset, offset)
            new KeyFrame(totalDuration.multiply(0.25),
                new KeyValue(loupeGroup.translateXProperty(), -offset),
                new KeyValue(loupeGroup.translateYProperty(), offset)
            ),
            // 50% - (offset, offset)
            new KeyFrame(totalDuration.multiply(0.50),
                new KeyValue(loupeGroup.translateXProperty(), offset),
                new KeyValue(loupeGroup.translateYProperty(), offset)
            ),
            // 75% - (offset, -offset)
            new KeyFrame(totalDuration.multiply(0.75),
                new KeyValue(loupeGroup.translateXProperty(), offset),
                new KeyValue(loupeGroup.translateYProperty(), -offset)
            ),
            // 100% - (-offset, -offset)
            new KeyFrame(totalDuration,
                new KeyValue(loupeGroup.translateXProperty(), -offset),
                new KeyValue(loupeGroup.translateYProperty(), -offset)
            )
        );

        nextTimeline.setCycleCount(Timeline.INDEFINITE);

        return nextTimeline;
    }

    @Override
    public Spin getSkinnable() {
        return spin;
    }

    @Override
    public Node getNode() {
        return root;
    }

    @Override
    @SuppressWarnings("all")
    public void dispose() {
        getSkinnable().getStyleClass().remove(STYLE_CLASS);
        subscription.unsubscribe();
        doStop();
        timeline.set(null);
        spin = null;
    }

    @Override
    public void autostart(boolean autostart) {
        this.autostart = autostart;
    }

    @Override
    public void start() {
        Platform.runLater(() -> timeline.set(doStart()));
    }

    @Override
    public void stop() {
        Platform.runLater(() -> {
            doStop();
            timeline.set(null);
        });
    }

    @Override
    public double computeMaxWidth(double height) {
        return size;
    }

    @Override
    public double computeMaxHeight(double width) {
        return size;
    }

    protected void updateColors() {
        glassCircle.setStroke(spin.getPrimaryColor());
        handleRect.setFill(spin.getSecondaryColor());
    }

    protected Timeline doStart() {
        var nextTimeline = initTimeline();
        nextTimeline.playFromStart();
        return nextTimeline;
    }

    protected void doStop() {
        var activeTimeline = timeline.get();
        if (activeTimeline != null) {
            activeTimeline.jumpTo(Duration.ZERO);
            activeTimeline.stop();
        }

        loupeGroup.setTranslateX(0.0);
        loupeGroup.setTranslateY(0.0);

        updateColors();
    }
}