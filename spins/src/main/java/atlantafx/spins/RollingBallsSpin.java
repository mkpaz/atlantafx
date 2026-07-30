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
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing rolling balls sequentially moving left to right.
 */
public class RollingBallsSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "rolling-balls-spin";
    public static final double DEFAULT_DURATION = 2.0;
    public static final double DEFAULT_RADIUS = 5.0;
    public static final double DEFAULT_GAP = 4.0;

    // timeline steps (1/6 increments)
    protected static final double STEP_16_PERCENTAGE = (1.0 / 6.0) * 100.0;
    protected static final double STEP_33_PERCENTAGE = (2.0 / 6.0) * 100.0;
    protected static final double STEP_50_PERCENTAGE = 50.0;
    protected static final double STEP_66_PERCENTAGE = (4.0 / 6.0) * 100.0;
    protected static final double STEP_83_PERCENTAGE = (5.0 / 6.0) * 100.0;

    protected static final int BALL_COUNT = 3;

    protected Spin spin;
    protected Pane root;
    protected Circle[] balls = new Circle[BALL_COUNT];

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double radius;
    protected final double gap;

    /**
     * Constructs a new {@code RollingBallsSpin} with default radius and gap.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public RollingBallsSpin(Spin spin) {
        this(spin, DEFAULT_RADIUS, DEFAULT_GAP);
    }

    /**
     * Constructs a new {@code RollingBallsSpin} with specified radius and gap.
     *
     * @param spin   the {@link Spin} control instance using this skin
     * @param radius the radius of each ball
     * @param gap    the spacing gap between adjacent balls
     */
    public RollingBallsSpin(Spin spin, double radius, double gap) {
        this.spin = spin;
        this.radius = radius > 0 ? radius : DEFAULT_RADIUS;
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
        spin.setSkin(new RollingBallsSpin(spin));
        return spin;
    }

    //*************************************************************************

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected void construct() {
        root = new Pane();

        double diameter = radius * 2.0;
        double step = diameter + gap;

        // base positions for balls in row (from rightmost pos2 to leftmost pos0)
        double pos2 = radius;
        double pos1 = radius + step;
        double pos0 = radius + step * 2.0;
        double[] positions = {pos0, pos1, pos2};

        for (int i = 0; i < BALL_COUNT; i++) {
            var ball = new Circle(radius);
            ball.setFill(spin.getPrimaryColor());
            ball.setStrokeType(StrokeType.INSIDE);
            ball.setCenterX(positions[i]);
            ball.setCenterY(radius);
            ball.setVisible(true);
            ball.setTranslateX(0.0);

            balls[i] = ball;
            root.getChildren().add(ball);
        }

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(paint -> updateColors()),
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
        updateColors();

        double diameter = radius * 2.0;
        double step = diameter + gap;
        double totalWidth = computeMaxWidth(0);

        double pos2 = radius;
        double pos1 = radius + step;
        double pos0 = radius + step * 2.0;

        balls[0].setCenterX(pos0);
        balls[1].setCenterX(pos1);
        balls[2].setCenterX(pos2);

        double offLeftX = -(diameter + gap);
        double offRightX = totalWidth + diameter + gap;
        double offLeft0 = offLeftX - pos0;
        double offLeft1 = offLeftX - pos1;
        double offLeft2 = offLeftX - pos2;
        double offRight0 = offRightX - pos0;
        double offRight1 = offRightX - pos1;
        double offRight2 = offRightX - pos2;

        var nextTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(balls[0].visibleProperty(), true),
                new KeyValue(balls[0].translateXProperty(), offLeft0),
                new KeyValue(balls[1].visibleProperty(), false),
                new KeyValue(balls[1].translateXProperty(), offLeft1),
                new KeyValue(balls[2].visibleProperty(), false),
                new KeyValue(balls[2].translateXProperty(), offLeft2)
            ),
            new KeyFrame(spin.getDurationPercentage(STEP_16_PERCENTAGE),
                new KeyValue(balls[0].translateXProperty(), 0.0),
                new KeyValue(balls[1].visibleProperty(), true),
                new KeyValue(balls[1].translateXProperty(), offLeft1)
            ),
            new KeyFrame(spin.getDurationPercentage(STEP_33_PERCENTAGE),
                new KeyValue(balls[0].translateXProperty(), 0.0),
                new KeyValue(balls[1].translateXProperty(), 0.0),
                new KeyValue(balls[2].visibleProperty(), true),
                new KeyValue(balls[2].translateXProperty(), offLeft2)
            ),
            new KeyFrame(spin.getDurationPercentage(STEP_50_PERCENTAGE),
                new KeyValue(balls[0].translateXProperty(), 0.0),
                new KeyValue(balls[1].translateXProperty(), 0.0),
                new KeyValue(balls[2].translateXProperty(), 0.0)
            ),
            new KeyFrame(spin.getDurationPercentage(STEP_66_PERCENTAGE),
                new KeyValue(balls[0].translateXProperty(), offRight0),
                new KeyValue(balls[0].visibleProperty(), false),
                new KeyValue(balls[1].translateXProperty(), 0.0),
                new KeyValue(balls[2].translateXProperty(), 0.0)
            ),
            new KeyFrame(spin.getDurationPercentage(STEP_83_PERCENTAGE),
                new KeyValue(balls[1].translateXProperty(), offRight1),
                new KeyValue(balls[1].visibleProperty(), false),
                new KeyValue(balls[2].translateXProperty(), 0.0)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(balls[2].translateXProperty(), offRight2),
                new KeyValue(balls[2].visibleProperty(), false)
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
        return (radius * 2.0 * BALL_COUNT) + (gap * (BALL_COUNT - 1));
    }

    @Override
    public double computeMaxHeight(double width) {
        return radius * 2.0;
    }

    protected void updateColors() {
        Paint primaryColor = spin.getPrimaryColor();
        for (Circle ball : balls) {
            ball.setFill(primaryColor);
        }
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

        resetState();
        updateColors();
    }

    protected void resetState() {
        for (Circle ball : balls) {
            ball.setVisible(true);
            ball.setTranslateX(0.0);
        }
    }
}