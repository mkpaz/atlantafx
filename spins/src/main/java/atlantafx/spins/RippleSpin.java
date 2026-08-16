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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing expanding circular ripples.
 */
public class RippleSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "ripple-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_RADIUS = 6.0;
    public static final double DEFAULT_STROKE_WIDTH = 2.0;
    public static final double DEFAULT_MAX_SCALE = 4.0;

    protected static final double INITIAL_OPACITY = 0.8;
    protected static final double MID_OPACITY_RATIO = 0.5;

    protected Spin spin;
    protected Pane root;
    protected Circle centerCircle;
    protected Circle ripple1;
    protected Circle ripple2;

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double radius;
    protected final double strokeWidth;
    protected final double maxScale;

    /**
     * Constructs a new {@code RippleSpin} with default dimensions.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public RippleSpin(Spin spin) {
        this(spin, DEFAULT_RADIUS, DEFAULT_STROKE_WIDTH, DEFAULT_MAX_SCALE);
    }

    /**
     * Constructs a new {@code RippleSpin} with specified dimensions.
     *
     * @param spin        the {@link Spin} control instance using this skin
     * @param radius      the radius of the central circle
     * @param strokeWidth the stroke width for ripple borders
     * @param maxScale    the maximum scale factor to which ripples expand
     */
    public RippleSpin(Spin spin, double radius, double strokeWidth, double maxScale) {
        this.spin = spin;
        this.radius = radius > 0 ? radius : DEFAULT_RADIUS;
        this.strokeWidth = strokeWidth > 0 ? strokeWidth : DEFAULT_STROKE_WIDTH;
        this.maxScale = maxScale > 1.0 ? maxScale : DEFAULT_MAX_SCALE;

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
        spin.setSkin(new RippleSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        double maxRadius = radius * maxScale;

        centerCircle = new Circle(maxRadius, maxRadius, radius);
        centerCircle.setFill(spin.getPrimaryColor());

        ripple1 = createRippleCircle(maxRadius, spin.getPrimaryColor(), strokeWidth);
        ripple2 = createRippleCircle(maxRadius, spin.getPrimaryColor(), strokeWidth);

        root = new Pane(centerCircle, ripple1, ripple2);

        resetState();

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

    protected Circle createRippleCircle(double centerXY, Paint strokeColor, double strokeW) {
        var circle = new Circle(centerXY, centerXY, radius);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(strokeColor);
        circle.setStrokeWidth(strokeW);
        circle.setStrokeType(StrokeType.INSIDE);
        circle.setOpacity(0.0);
        circle.setScaleX(1.0);
        circle.setScaleY(1.0);
        return circle;
    }


    protected Timeline initTimeline() {
        updateColors();

        Duration halfDuration = spin.getDurationPercentage(50.0);

        // intermediate scale for ripple 2
        double midScale = 1.0 + (maxScale - 1.0) * 0.5;
        double midOpacity = INITIAL_OPACITY * MID_OPACITY_RATIO;

        var nextTimeline = new Timeline(
            // ripple 1: scale 1.0 to maxScale
            new KeyFrame(Duration.ZERO,
                new KeyValue(ripple1.scaleXProperty(), 1.0),
                new KeyValue(ripple1.scaleYProperty(), 1.0),
                new KeyValue(ripple1.opacityProperty(), 1.0)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(ripple1.scaleXProperty(), maxScale),
                new KeyValue(ripple1.scaleYProperty(), maxScale),
                new KeyValue(ripple1.opacityProperty(), 0.0)
            ),

            // ripple 2: shifted by 50%
            new KeyFrame(Duration.ZERO,
                new KeyValue(ripple2.scaleXProperty(), midScale),
                new KeyValue(ripple2.scaleYProperty(), midScale),
                new KeyValue(ripple2.opacityProperty(), midOpacity)
            ),
            new KeyFrame(halfDuration,
                new KeyValue(ripple2.scaleXProperty(), maxScale),
                new KeyValue(ripple2.scaleYProperty(), maxScale),
                new KeyValue(ripple2.opacityProperty(), 0.0)
            ),
            new KeyFrame(halfDuration,
                new KeyValue(ripple2.scaleXProperty(), 1.0),
                new KeyValue(ripple2.scaleYProperty(), 1.0),
                new KeyValue(ripple2.opacityProperty(), INITIAL_OPACITY)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(ripple2.scaleXProperty(), midScale),
                new KeyValue(ripple2.scaleYProperty(), midScale),
                new KeyValue(ripple2.opacityProperty(), midOpacity)
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
        return (radius * 2.0) * maxScale;
    }

    @Override
    public double computeMaxHeight(double width) {
        return (radius * 2.0) * maxScale;
    }

    protected void updateColors() {
        centerCircle.setFill(spin.getPrimaryColor());
        ripple1.setStroke(spin.getPrimaryColor());
        ripple2.setStroke(spin.getPrimaryColor());
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
        ripple1.setScaleX(1.0);
        ripple1.setScaleY(1.0);
        ripple1.setOpacity(0.0);

        ripple2.setScaleX(1.0);
        ripple2.setScaleY(1.0);
        ripple2.setOpacity(0.0);
    }
}