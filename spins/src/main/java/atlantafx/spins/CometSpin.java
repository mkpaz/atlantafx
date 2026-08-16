/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a rotating comet
 * made of balls that decrease in size towards the tail.
 */
public class CometSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "comet-spin";
    public static final double DEFAULT_DURATION = 1.7;
    public static final double DEFAULT_PATH_RADIUS = 12;
    public static final double DEFAULT_DOT_RADIUS = 4;

    protected static final int DOT_COUNT = 5;
    protected static final double MAX_SPREAD_PER_DOT = 30.0; // max degrees of separation between adjacent dots
    protected static final double TAIL_SHRINK_RATE = 0.18;   // size reduction per tail dot

    protected static final double SPREAD_START_FRACTION = 0.05;
    protected static final double SPREAD_PEAK_FRACTION = 0.38;
    protected static final double SPREAD_END_FRACTION = 0.95;

    protected Spin spin;
    protected Pane root;
    protected Group spinningContent;
    protected final Circle[] dots = new Circle[DOT_COUNT];
    protected final Rotate[] dotRotates = new Rotate[DOT_COUNT];
    protected final DoubleProperty spreadProperty = new SimpleDoubleProperty(0);
    protected Rotate spinRotate;

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double pathRadius;
    protected final double dotRadius;
    protected final double center;
    protected final double totalSize;

    /**
     * Constructs a {@code CometSpin} skin with default dimensions.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public CometSpin(Spin spin) {
        this(spin, DEFAULT_PATH_RADIUS, DEFAULT_DOT_RADIUS);
    }

    /**
     * Constructs a {@code CometSpin} skin with custom path radius and dot radius.
     *
     * @param spin       the {@link Spin} control instance using this skin
     * @param pathRadius the radius of the circular path the comet travels on
     * @param dotRadius  the radius of the largest (head) dot of the comet
     */
    public CometSpin(Spin spin, double pathRadius, double dotRadius) {
        this.spin = spin;
        this.pathRadius = pathRadius > 0 ? pathRadius : DEFAULT_PATH_RADIUS;
        this.dotRadius = dotRadius > 0 ? dotRadius : DEFAULT_DOT_RADIUS;

        this.center = this.pathRadius + this.dotRadius;
        this.totalSize = this.center * 2.0;

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
        spin.setSkin(new CometSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        spinningContent = new Group();
        spinRotate = new Rotate(0, center, center);
        spinningContent.getTransforms().add(spinRotate);

        for (int i = 0; i < DOT_COUNT; i++) {
            // scale radius down for each subsequent tail dot
            double r = dotRadius * (1.0 - (i * TAIL_SHRINK_RATE));

            dots[i] = new Circle(center, center - pathRadius, Math.max(r, 0.5));
            dots[i].setFill(spin.getPrimaryColor());
            dots[i].setStroke(Color.TRANSPARENT);
            dots[i].setStrokeType(StrokeType.INSIDE);

            dotRotates[i] = new Rotate(0, center, center);
            dots[i].getTransforms().add(dotRotates[i]);

            // bind the rotation of each dot to create the expanding-contracting effect
            if (i > 0) {
                // negative multiplier ensures the tail lags behind the head (i=0)
                dotRotates[i].angleProperty().bind(spreadProperty.multiply(-i));
            }

            spinningContent.getChildren().add(dots[i]);
        }

        // exclude animated container from parent layout calculation
        spinningContent.setManaged(false);

        root = new Pane(spinningContent);

        resetState();

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(color -> {
                for (Circle dot : dots) {
                    dot.setFill(color);
                }
            }),
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
    }

    protected Timeline initTimeline() {
        Duration totalDuration = spin.getDuration();
        Duration startSpreadDuration = totalDuration.multiply(SPREAD_START_FRACTION);
        Duration peakSpreadDuration = totalDuration.multiply(SPREAD_PEAK_FRACTION);
        Duration endSpreadDuration = totalDuration.multiply(SPREAD_END_FRACTION);

        var nextTimeline = new Timeline(
            // 0%: start collapsed at top, 0 degrees global rotation
            new KeyFrame(Duration.ZERO,
                new KeyValue(spreadProperty, 0),
                new KeyValue(spinRotate.angleProperty(), 0)
            ),
            // 5%: start expanding the tail
            new KeyFrame(startSpreadDuration,
                new KeyValue(spreadProperty, 0)
            ),
            // 38%: tail reaches maximum spread across the circular path
            new KeyFrame(peakSpreadDuration,
                new KeyValue(spreadProperty, MAX_SPREAD_PER_DOT)
            ),
            // 95%: tail collapses back into the head
            new KeyFrame(endSpreadDuration,
                new KeyValue(spreadProperty, 0)
            ),
            // 100%: fully collapsed, complete one full 360-degree rotation
            new KeyFrame(totalDuration,
                new KeyValue(spreadProperty, 0),
                new KeyValue(spinRotate.angleProperty(), 360.0)
            )
        );

        nextTimeline.setCycleCount(Timeline.INDEFINITE);
        nextTimeline.setAutoReverse(false);

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
        return totalSize;
    }

    @Override
    public double computeMaxHeight(double width) {
        return totalSize;
    }

    protected void resetState() {
        spreadProperty.set(0);
        spinRotate.setAngle(0);
    }

    protected Timeline doStart() {
        var nextTimeline = initTimeline();
        nextTimeline.playFromStart();
        return nextTimeline;
    }

    protected void doStop() {
        var t = timeline.get();
        if (t != null) {
            t.stop();
        }
        resetState();
    }
}