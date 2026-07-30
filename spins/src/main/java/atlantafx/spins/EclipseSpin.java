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
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing two overlapping and oscillating circles.
 */
public class EclipseSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "eclipse-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_RADIUS = 10.0;
    public static final double DEFAULT_OPACITY = 0.95;

    // distance multipliers (from canvas center)
    protected static final double MIN_OFFSET_FACTOR = 0.6;
    protected static final double MAX_OFFSET_FACTOR = 2.0;

    // canvas boundary multipliers
    protected static final double WIDTH_MULTIPLIER = 6.0;
    protected static final double HEIGHT_MULTIPLIER = 2.0;

    protected Spin spin;
    protected Pane root;
    protected Circle circleLeft;
    protected Circle circleRight;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double radius;
    protected final double opacity;

    /**
     * Constructs a new {@code EclipseSpin} with default radius and opacity.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public EclipseSpin(Spin spin) {
        this(spin, DEFAULT_RADIUS, DEFAULT_OPACITY);
    }

    /**
     * Constructs a new {@code EclipseSpin} with specified radius and opacity.
     *
     * @param spin    the {@link Spin} control instance using this skin
     * @param radius  the radius of each circle
     * @param opacity the opacity level
     */
    public EclipseSpin(Spin spin, double radius, double opacity) {
        this.spin = spin;
        this.radius = radius > 0 ? radius : DEFAULT_RADIUS;
        this.opacity = opacity >= 0.0 && opacity <= 1.0 ? opacity : DEFAULT_OPACITY;

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
        spin.setSkin(new EclipseSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        Paint primaryColor = spin.getPrimaryColor();
        Paint secondaryColor = spin.getSecondaryColor();

        circleLeft = new Circle(radius);
        circleLeft.setFill(primaryColor);
        circleLeft.setOpacity(opacity);
        circleLeft.setStrokeType(StrokeType.INSIDE);

        circleRight = new Circle(radius);
        circleRight.setFill(secondaryColor);
        circleRight.setOpacity(opacity);
        circleRight.setStrokeType(StrokeType.INSIDE);

        circleRight.setBlendMode(BlendMode.SRC_OVER);

        double centerY = radius;
        double centerCanvasX = computeMaxWidth(0) / 2.0;

        circleLeft.setCenterY(centerY);
        circleRight.setCenterY(centerY);

        circleLeft.setCenterX(centerCanvasX - MIN_OFFSET_FACTOR * radius);
        circleRight.setCenterX(centerCanvasX + MIN_OFFSET_FACTOR * radius);

        root = new Pane(circleLeft, circleRight);

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
    }

    protected Timeline initTimeline() {
        updateColors();

        double centerCanvasX = computeMaxWidth(0) / 2.0;
        double minXLeft = centerCanvasX - MIN_OFFSET_FACTOR * radius;
        double minXRight = centerCanvasX + MIN_OFFSET_FACTOR * radius;
        double maxXLeft = centerCanvasX - MAX_OFFSET_FACTOR * radius;
        double maxXRight = centerCanvasX + MAX_OFFSET_FACTOR * radius;

        var nextTimeline = new Timeline(
            // start converging hold state
            new KeyFrame(Duration.ZERO,
                new KeyValue(circleLeft.centerXProperty(), minXLeft, Interpolator.EASE_IN),
                new KeyValue(circleRight.centerXProperty(), minXRight, Interpolator.EASE_IN)
            ),
            // hold state before parting
            new KeyFrame(spin.getDurationPercentage(25.0),
                new KeyValue(circleLeft.centerXProperty(), minXLeft, Interpolator.EASE_IN),
                new KeyValue(circleRight.centerXProperty(), minXRight, Interpolator.EASE_IN)
            ),
            // full expansion
            new KeyFrame(spin.getDuration(),
                new KeyValue(circleLeft.centerXProperty(), maxXLeft, Interpolator.EASE_IN),
                new KeyValue(circleRight.centerXProperty(), maxXRight, Interpolator.EASE_IN)
            )
        );

        nextTimeline.setCycleCount(Timeline.INDEFINITE);
        nextTimeline.setAutoReverse(true);

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
        return radius * WIDTH_MULTIPLIER;
    }

    @Override
    public double computeMaxHeight(double width) {
        return radius * HEIGHT_MULTIPLIER;
    }

    protected void updateColors() {
        circleLeft.setFill(spin.getPrimaryColor());
        circleRight.setFill(spin.getSecondaryColor());
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

        double centerCanvasX = computeMaxWidth(0) / 2.0;
        circleLeft.setCenterX(centerCanvasX - MIN_OFFSET_FACTOR * radius);
        circleRight.setCenterX(centerCanvasX + MIN_OFFSET_FACTOR * radius);

        updateColors();
    }
}