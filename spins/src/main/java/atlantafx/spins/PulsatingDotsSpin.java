/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a three pulsating horizontal dots.
 */
public class PulsatingDotsSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "pulsating-dots-spin";
    public static final double DEFAULT_DURATION = 1.5;
    public static final double DEFAULT_RADIUS = 4.0;
    public static final double DEFAULT_GAP = 3.0;
    public static final double DEFAULT_SCALE = 1.5;

    protected static final double CIRCLE_CENTER_OFFSET = 0.5;

    protected Spin spin;
    protected Pane root;
    protected Circle circle1;
    protected Circle circle2;
    protected Circle circle3;

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double radius;
    protected final double gap;
    protected final double scale;

    /**
     * Constructs a new {@code PulsatingDotsSpin} with default parameters.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public PulsatingDotsSpin(Spin spin) {
        this(spin, DEFAULT_RADIUS, DEFAULT_GAP, DEFAULT_SCALE);
    }

    /**
     * Constructs a new {@code PulsatingDotsSpin} with specified radius, gap, and scale.
     *
     * @param spin   the {@link Spin} control instance using this skin
     * @param radius the radius of each circle
     * @param gap    the horizontal spacing between adjacent circles
     * @param scale  the size multiplier for the active circle state
     */
    public PulsatingDotsSpin(Spin spin, double radius, double gap, double scale) {
        this.spin = spin;
        this.radius = radius > 0 ? radius : DEFAULT_RADIUS;
        this.gap = gap > 0 ? gap : DEFAULT_GAP;
        this.scale = scale > 0 ? scale : DEFAULT_SCALE;

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
        spin.setSkin(new PulsatingDotsSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        double maxRadius = maxRadius();
        double centerY = computeMaxHeight(-1) * CIRCLE_CENTER_OFFSET;

        // align centers with the maximum bounding box of each cell
        circle1 = new Circle(maxRadius, centerY, radius);
        circle1.setFill(spin.getPrimaryColor());

        circle2 = new Circle((maxRadius * 3.0) + gap, centerY, radius);
        circle2.setFill(spin.getPrimaryColor());

        circle3 = new Circle((maxRadius * 5.0) + (gap * 2.0), centerY, radius);
        circle3.setFill(spin.getPrimaryColor());

        root = new Pane(circle1, circle2, circle3);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(_ -> updateTimeline()),
            spin.secondaryColorProperty().subscribe(_ -> updateTimeline()),

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

        Paint secondaryColor = spin.getSecondaryColor();
        double maxRadius = maxRadius();

        var nextTimeline = new Timeline(
            // 1st scales up to maxRadius, 1st and 2nd take secondary color
            new KeyFrame(spin.getDurationPercentage(33.0),
                new KeyValue(circle1.radiusProperty(), maxRadius),
                new KeyValue(circle2.radiusProperty(), radius),
                new KeyValue(circle3.radiusProperty(), radius),
                new KeyValue(circle1.fillProperty(), secondaryColor, Interpolator.DISCRETE),
                new KeyValue(circle2.fillProperty(), secondaryColor, Interpolator.DISCRETE)
            ),
            // 2nd scales up to maxRadius, 3rd takes secondary color
            new KeyFrame(spin.getDurationPercentage(66.0),
                new KeyValue(circle1.radiusProperty(), radius),
                new KeyValue(circle2.radiusProperty(), maxRadius),
                new KeyValue(circle3.radiusProperty(), radius),
                new KeyValue(circle3.fillProperty(), secondaryColor, Interpolator.DISCRETE)
            ),
            // 3rd scales up to maxRadius
            new KeyFrame(spin.getDurationPercentage(99.0),
                new KeyValue(circle1.radiusProperty(), radius),
                new KeyValue(circle2.radiusProperty(), radius),
                new KeyValue(circle3.radiusProperty(), maxRadius)
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
    public double computeMaxWidth(double width) {
        return (maxRadius() * 6.0) + (gap * 2.0);
    }

    @Override
    public double computeMaxHeight(double height) {
        return maxRadius() * 2.0;
    }

    protected double maxRadius() {
        return radius * scale;
    }

    protected void updateTimeline() {
        Timeline activeTimeline = timeline.get();
        if (activeTimeline != null) {
            boolean isRunning = activeTimeline.getStatus() == Animation.Status.RUNNING;
            Duration currentTime = activeTimeline.getCurrentTime();

            activeTimeline.stop();
            var nextTimeline = initTimeline();
            timeline.set(nextTimeline);

            if (isRunning) {
                nextTimeline.playFrom(currentTime);
            } else {
                updateColors();
            }
        } else {
            updateColors();
        }
    }

    protected void updateColors() {
        circle1.setFill(spin.getPrimaryColor());
        circle2.setFill(spin.getPrimaryColor());
        circle3.setFill(spin.getPrimaryColor());
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

    protected void resetState() {
        var primaryColor = spin.getPrimaryColor();
        circle1.setRadius(radius);
        circle2.setRadius(radius);
        circle3.setRadius(radius);
        circle1.setFill(primaryColor);
        circle2.setFill(primaryColor);
        circle3.setFill(primaryColor);
    }
}