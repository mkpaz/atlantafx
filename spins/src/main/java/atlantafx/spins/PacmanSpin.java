/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import javafx.animation.Interpolator;
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
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a Pacman character eating incoming balls.
 */
public class PacmanSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "pacman-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_BODY_RADIUS = 15.0;

    // geometric proportions
    protected static final double BALL_RADIUS_RATIO = 15.0 / 48.0;    // ball radius to body radius
    protected static final double CANVAS_BOUNDS_RATIO = 150.0 / 48.0; // canvas width bound ratio
    protected static final double START_BALL_X_RATIO = 75.0 / 48.0;   // initial spawn position X offset
    protected static final double END_BALL_X_RATIO = -5.0 / 48.0;     // stomach landing position X offset

    // arc angle constants (degrees)
    protected static final double PACMAN_START_ANGLE = 45.0;   // mouth rotation tilt (45 deg)
    protected static final double MOUTH_CLOSED_LENGTH = 360.0; // closed state (full circle)
    protected static final double MOUTH_OPEN_LENGTH = 270.0;   // open state (sector missing 90 deg)

    // timing percentage
    protected static final double HALF_PHASE_PERCENTAGE = 50.0;
    protected static final double INGEST_PHASE_PERCENTAGE = 52.0;
    protected static final double RESET_PHASE_PERCENTAGE = 53.0;

    protected Spin spin;
    protected Pane root;
    protected Arc pacmanBody;
    protected Circle ball;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected DoubleProperty mouthLength = new SimpleDoubleProperty(MOUTH_CLOSED_LENGTH);
    protected DoubleProperty ballX = new SimpleDoubleProperty(0);
    protected DoubleProperty ballOpacity = new SimpleDoubleProperty(0);
    protected boolean autostart = true;

    protected final double bodyRadius;
    protected final double ballRadius;
    protected final double centerX;
    protected final double centerY;
    protected final double totalWidth;
    protected final double totalHeight;

    /**
     * Constructs a new {@code PacmanSpin} with default body radius.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public PacmanSpin(Spin spin) {
        this(spin, DEFAULT_BODY_RADIUS);
    }

    /**
     * Constructs a new {@code PacmanSpin} with specified body radius.
     *
     * @param spin       the {@link Spin} control instance using this skin
     * @param bodyRadius the body radius of the Pacman shape
     */
    public PacmanSpin(Spin spin, double bodyRadius) {
        this.spin = spin;
        this.bodyRadius = bodyRadius > 0 ? bodyRadius : DEFAULT_BODY_RADIUS;

        this.ballRadius = this.bodyRadius * BALL_RADIUS_RATIO;
        this.centerX = this.bodyRadius;
        this.centerY = this.bodyRadius + this.ballRadius;

        this.totalWidth = this.bodyRadius * CANVAS_BOUNDS_RATIO;
        this.totalHeight = this.centerY * 2.0;

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
        spin.setSkin(new PacmanSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        pacmanBody = new Arc();
        pacmanBody.setCenterX(centerX);
        pacmanBody.setCenterY(centerY);
        pacmanBody.setRadiusX(bodyRadius);
        pacmanBody.setRadiusY(bodyRadius);
        pacmanBody.setStartAngle(PACMAN_START_ANGLE);
        pacmanBody.setLength(MOUTH_CLOSED_LENGTH);
        pacmanBody.setType(ArcType.ROUND);
        pacmanBody.setStrokeType(StrokeType.INSIDE);

        mouthLength.addListener((obs, old, val) ->
            pacmanBody.setLength(val.doubleValue())
        );

        ball = new Circle(centerX, centerY, ballRadius);
        ball.setStrokeType(StrokeType.INSIDE);
        ball.setOpacity(0.0);

        ball.translateXProperty().bind(ballX);
        ball.opacityProperty().bind(ballOpacity);

        Group content = new Group(ball, pacmanBody);
        content.setManaged(false);

        root = new Pane(content);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(paint -> updateColors()),
            spin.secondaryColorProperty().subscribe(paint -> updateColors()),
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

        resetState();
    }

    protected Timeline initTimeline() {
        updateColors();

        double startX = bodyRadius * START_BALL_X_RATIO;
        double endX = bodyRadius * END_BALL_X_RATIO;

        var nextTimeline = new Timeline(
            // mouth toggle
            new KeyFrame(Duration.ZERO,
                new KeyValue(mouthLength, MOUTH_OPEN_LENGTH, Interpolator.DISCRETE)
            ),
            new KeyFrame(spin.getDurationPercentage(HALF_PHASE_PERCENTAGE),
                new KeyValue(mouthLength, MOUTH_CLOSED_LENGTH, Interpolator.DISCRETE)
            ),

            // ball movement
            new KeyFrame(Duration.ZERO,
                new KeyValue(ballX, startX),
                new KeyValue(ballOpacity, 1.0)
            ),
            new KeyFrame(spin.getDurationPercentage(HALF_PHASE_PERCENTAGE),
                new KeyValue(ballX, 0.0),
                new KeyValue(ballOpacity, 1.0)
            ),
            new KeyFrame(spin.getDurationPercentage(INGEST_PHASE_PERCENTAGE),
                new KeyValue(ballX, endX),
                new KeyValue(ballOpacity, 0.0)
            ),
            new KeyFrame(spin.getDurationPercentage(RESET_PHASE_PERCENTAGE),
                new KeyValue(ballX, startX),
                new KeyValue(ballOpacity, 0.0)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(ballX, startX),
                new KeyValue(ballOpacity, 0.0)
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
        return totalWidth;
    }

    @Override
    public double computeMaxHeight(double width) {
        return totalHeight;
    }

    protected void updateColors() {
        pacmanBody.setFill(spin.getPrimaryColor());
        ball.setFill(spin.getSecondaryColor());
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
    }

    public void resetState() {
        mouthLength.set(MOUTH_CLOSED_LENGTH);
        ballX.set(bodyRadius * START_BALL_X_RATIO);
        ballOpacity.set(0.0);
        updateColors();
    }
}