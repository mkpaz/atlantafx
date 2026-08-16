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
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing 4 merging circles.
 */
public class MergingBallsSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "merging-balls-spin";
    public static final double DEFAULT_DURATION = 1.4;
    public static final double DEFAULT_BALL_RADIUS = 8.0;
    public static final double DEFAULT_GAP = 8.0;

    protected static final double SCALE_MIN = 0.8;
    protected static final double SCALE_MAX = 1.0;
    protected static final double ROTATE_START = 0.0;
    protected static final double ROTATE_MID = 360.0;
    protected static final double ROTATE_END = 720.0;

    protected Spin spin;
    protected Pane root;
    protected Group innerGroup;
    protected Circle topLeftCircle;
    protected Circle topRightCircle;
    protected Circle bottomLeftCircle;
    protected Circle bottomRightCircle;

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double ballRadius;
    protected final double gap;

    /**
     * Constructs a new {@code MergingBallsSpin} with default ball radius and gap.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public MergingBallsSpin(Spin spin) {
        this(spin, DEFAULT_BALL_RADIUS, DEFAULT_GAP);
    }

    /**
     * Constructs a new {@code MergingBallsSpin} with specified ball radius and gap.
     *
     * @param spin       the {@link Spin} control instance using this skin
     * @param ballRadius the radius of each circle
     * @param gap        the spacing between adjacent circles
     */
    public MergingBallsSpin(Spin spin, double ballRadius, double gap) {
        this.spin = spin;
        this.ballRadius = ballRadius > 0 ? ballRadius : DEFAULT_BALL_RADIUS;
        this.gap = gap >= 0 ? gap : DEFAULT_GAP;

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
        spin.setSkin(new MergingBallsSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        double offset = ballRadius + gap / 2.0;

        topLeftCircle = createBall(-offset, -offset);
        topRightCircle = createBall(offset, -offset);
        bottomLeftCircle = createBall(-offset, offset);
        bottomRightCircle = createBall(offset, offset);

        innerGroup = new Group(topLeftCircle, topRightCircle, bottomLeftCircle, bottomRightCircle);

        double canvasSize = getCanvasSize();
        innerGroup.setLayoutX(canvasSize / 2.0);
        innerGroup.setLayoutY(canvasSize / 2.0);

        root = new Pane(innerGroup);
        root.setMinSize(canvasSize, canvasSize);
        root.setPrefSize(canvasSize, canvasSize);
        root.setMaxSize(canvasSize, canvasSize);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(_ -> updateColors()),
            spin.secondaryColorProperty().subscribe(_ -> updateColors()),
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

    protected Circle createBall(double centerX, double centerY) {
        var circle = new Circle(ballRadius);
        circle.setCenterX(centerX);
        circle.setCenterY(centerY);
        circle.setStrokeType(StrokeType.INSIDE);
        return circle;
    }

    protected Timeline initTimeline() {
        updateColors();

        double offset = ballRadius + gap / 2.0;

        var nextTimeline = new Timeline(
            // 0% (Start)
            new KeyFrame(Duration.ZERO,
                new KeyValue(innerGroup.rotateProperty(), ROTATE_START),
                new KeyValue(innerGroup.scaleXProperty(), SCALE_MIN),
                new KeyValue(innerGroup.scaleYProperty(), SCALE_MIN),

                new KeyValue(topLeftCircle.centerXProperty(), -offset),
                new KeyValue(topLeftCircle.centerYProperty(), -offset),

                new KeyValue(topRightCircle.centerXProperty(), offset),
                new KeyValue(topRightCircle.centerYProperty(), -offset),

                new KeyValue(bottomLeftCircle.centerXProperty(), -offset),
                new KeyValue(bottomLeftCircle.centerYProperty(), offset),

                new KeyValue(bottomRightCircle.centerXProperty(), offset),
                new KeyValue(bottomRightCircle.centerYProperty(), offset)
            ),

            // (merged at center)
            new KeyFrame(spin.getDurationPercentage(50.0),
                new KeyValue(innerGroup.rotateProperty(), ROTATE_MID),
                new KeyValue(innerGroup.scaleXProperty(), SCALE_MAX),
                new KeyValue(innerGroup.scaleYProperty(), SCALE_MAX),

                new KeyValue(topLeftCircle.centerXProperty(), 0.0),
                new KeyValue(topLeftCircle.centerYProperty(), 0.0),

                new KeyValue(topRightCircle.centerXProperty(), 0.0),
                new KeyValue(topRightCircle.centerYProperty(), 0.0),

                new KeyValue(bottomLeftCircle.centerXProperty(), 0.0),
                new KeyValue(bottomLeftCircle.centerYProperty(), 0.0),

                new KeyValue(bottomRightCircle.centerXProperty(), 0.0),
                new KeyValue(bottomRightCircle.centerYProperty(), 0.0)
            ),

            // 100% (finish cycle)
            new KeyFrame(spin.getDuration(),
                new KeyValue(innerGroup.rotateProperty(), ROTATE_END),
                new KeyValue(innerGroup.scaleXProperty(), SCALE_MIN),
                new KeyValue(innerGroup.scaleYProperty(), SCALE_MIN),

                new KeyValue(topLeftCircle.centerXProperty(), -offset),
                new KeyValue(topLeftCircle.centerYProperty(), -offset),

                new KeyValue(topRightCircle.centerXProperty(), offset),
                new KeyValue(topRightCircle.centerYProperty(), -offset),

                new KeyValue(bottomLeftCircle.centerXProperty(), -offset),
                new KeyValue(bottomLeftCircle.centerYProperty(), offset),

                new KeyValue(bottomRightCircle.centerXProperty(), offset),
                new KeyValue(bottomRightCircle.centerYProperty(), offset)
            )
        );

        nextTimeline.setCycleCount(Timeline.INDEFINITE);
        nextTimeline.setAutoReverse(false);

        return nextTimeline;
    }

    protected double getCanvasSize() {
        double centerOffset = ballRadius + gap / 2.0;
        double maxSpreadRadius = (centerOffset * Math.sqrt(2) + ballRadius) * SCALE_MIN;
        double mergedCenterRadius = ballRadius * SCALE_MAX;
        double maxRadius = Math.max(maxSpreadRadius, mergedCenterRadius);
        return maxRadius * 2.0;
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
        return getCanvasSize();
    }

    @Override
    public double computeMaxHeight(double width) {
        return getCanvasSize();
    }

    protected void updateColors() {
        topLeftCircle.setFill(spin.getPrimaryColor());
        bottomRightCircle.setFill(spin.getPrimaryColor());

        topRightCircle.setFill(spin.getSecondaryColor());
        bottomLeftCircle.setFill(spin.getSecondaryColor());
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

        double offset = ballRadius + gap / 2.0;

        innerGroup.setRotate(ROTATE_START);
        innerGroup.setScaleX(SCALE_MIN);
        innerGroup.setScaleY(SCALE_MIN);

        topLeftCircle.setCenterX(-offset);
        topLeftCircle.setCenterY(-offset);

        topRightCircle.setCenterX(offset);
        topRightCircle.setCenterY(-offset);

        bottomLeftCircle.setCenterX(-offset);
        bottomLeftCircle.setCenterY(offset);

        bottomRightCircle.setCenterX(offset);
        bottomRightCircle.setCenterY(offset);

        updateColors();
    }
}