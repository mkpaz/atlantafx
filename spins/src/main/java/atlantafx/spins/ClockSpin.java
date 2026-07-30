/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import javafx.animation.Interpolator;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing an analog clock indicator.
 *
 * <p>Renders a clock face with a center pin, an hour hand, and a minute hand.
 */
public class ClockSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "clock-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_RADIUS = 16.0;
    public static final double DEFAULT_OPACITY = 0.95;

    // geometric proportions relative to a base size of 48px
    protected static final double PIN_RADIUS_RATIO = 10.0 / 48.0;
    protected static final double HOUR_WIDTH_RATIO = 8.0 / 48.0;
    protected static final double HOUR_HEIGHT_RATIO = 32.0 / 48.0;
    protected static final double MINUTE_WIDTH_RATIO = 4.0 / 48.0;
    protected static final double MINUTE_HEIGHT_RATIO = 44.0 / 48.0;

    protected Spin spin;
    protected Pane root;
    protected Circle faceCircle;
    protected Circle centerPin;
    protected Rectangle hourHand;
    protected Rectangle minuteHand;
    protected Rotate hourRotate;
    protected Rotate minuteRotate;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double radius;

    /**
     * Constructs a new {@code ClockSpin} with default radius.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public ClockSpin(Spin spin) {
        this(spin, DEFAULT_RADIUS);
    }

    /**
     * Constructs a new {@code ClockSpin} with specified radius.
     *
     * @param spin   the {@link Spin} control instance using this skin
     * @param radius the ring radius of the clock face
     */
    public ClockSpin(Spin spin, double radius) {
        this.spin = spin;
        this.radius = radius > 0 ? radius : DEFAULT_RADIUS;

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
        spin.setSkin(new ClockSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        root = new Pane();

        // clock face (Background)
        faceCircle = new Circle(radius);
        faceCircle.setCenterX(radius);
        faceCircle.setCenterY(radius);
        faceCircle.setFill(spin.getPrimaryColor());
        faceCircle.setStrokeWidth(2.0);
        faceCircle.setStroke(spin.getSecondaryColor());
        faceCircle.setStrokeType(StrokeType.INSIDE);

        // center pin
        double pinRadius = radius * PIN_RADIUS_RATIO;
        centerPin = new Circle(pinRadius);
        centerPin.setCenterX(radius);
        centerPin.setCenterY(radius);
        centerPin.setFill(spin.getTertiaryColor());
        centerPin.setStroke(null);
        centerPin.setStrokeType(StrokeType.INSIDE);

        // hour hand
        double hourWidth = radius * HOUR_WIDTH_RATIO;
        double hourHeight = radius * HOUR_HEIGHT_RATIO;
        hourHand = createHand(hourWidth, hourHeight, spin.getTertiaryColor());

        // position upwards from center (towards 12 o'clock)
        hourHand.setX(radius - hourWidth / 2.0);
        hourHand.setY(radius - hourHeight);

        hourRotate = new Rotate(0.0, radius, radius);
        hourHand.getTransforms().add(hourRotate);

        // minute hand
        double minuteWidth = radius * MINUTE_WIDTH_RATIO;
        double minuteHeight = radius * MINUTE_HEIGHT_RATIO;
        minuteHand = createHand(minuteWidth, minuteHeight, spin.getTertiaryColor());

        // position upwards from center (towards 12 o'clock)
        minuteHand.setX(radius - minuteWidth / 2.0);
        minuteHand.setY(radius - minuteHeight);

        minuteRotate = new Rotate(0.0, radius, radius);
        minuteHand.getTransforms().add(minuteRotate);

        // order matter (face -> hour hand -> minute hand -> center pin)
        var clockGroup = new Group(faceCircle, hourHand, minuteHand, centerPin);
        root.getChildren().add(clockGroup);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(paint -> updateColors()),
            spin.secondaryColorProperty().subscribe(paint -> updateColors()),
            spin.tertiaryColorProperty().subscribe(paint -> updateColors()),
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
        // defines the time for one full rotation of the minute hand
        Duration minuteDuration = spin.getDuration();

        var nextTimeline = new Timeline();
        nextTimeline.setCycleCount(Timeline.INDEFINITE);

        // initial position (0 degrees / 12 o'clock)
        nextTimeline.getKeyFrames().add(
            new KeyFrame(Duration.ZERO,
                new KeyValue(hourRotate.angleProperty(), 0.0, Interpolator.LINEAR),
                new KeyValue(minuteRotate.angleProperty(), 0.0, Interpolator.LINEAR)
            )
        );

        // generate keyframes for 12 full rotations of the minute hand
        for (int i = 1; i <= 12; i++) {
            Duration frameTime = minuteDuration.multiply(i);
            double minuteAngle = 360 * i;
            double hourAngle = 360.0 / 12 * i;

            nextTimeline.getKeyFrames().add(
                new KeyFrame(frameTime,
                    new KeyValue(minuteRotate.angleProperty(), minuteAngle, Interpolator.LINEAR),
                    new KeyValue(hourRotate.angleProperty(), hourAngle, Interpolator.LINEAR)
                )
            );
        }

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
        return radius * 2.0;
    }

    @Override
    public double computeMaxHeight(double width) {
        return radius * 2.0;
    }

    protected Rectangle createHand(double width, double height, Paint fill) {
        var hand = new Rectangle(width, height);
        hand.setFill(fill);
        hand.setStroke(null);
        hand.setStrokeType(StrokeType.INSIDE);
        return hand;
    }

    protected void updateColors() {
        faceCircle.setFill(spin.getPrimaryColor());
        faceCircle.setStroke(spin.getSecondaryColor());
        centerPin.setFill(spin.getTertiaryColor());
        hourHand.setFill(spin.getTertiaryColor());
        minuteHand.setFill(spin.getTertiaryColor());
    }

    protected Timeline doStart() {
        Timeline activeTimeline = timeline.get();
        if (activeTimeline != null) {
            activeTimeline.stop();
        }
        Timeline nextTimeline = initTimeline();
        nextTimeline.playFromStart();
        return nextTimeline;
    }

    protected void doStop() {
        Timeline activeTimeline = timeline.get();
        if (activeTimeline != null) {
            activeTimeline.jumpTo(Duration.ZERO);
            activeTimeline.stop();
        }

        // reset hands back to 12 o'clock
        hourRotate.setAngle(0.0);
        minuteRotate.setAngle(0.0);

        updateColors();
    }
}