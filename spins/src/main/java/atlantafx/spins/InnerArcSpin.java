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
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a circle with a rotating inner arc.
 */
public class InnerArcSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "inner-arc-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_RADIUS = 15.0;
    public static final double DEFAULT_STROKE_WIDTH = 3.0;
    public static final double DEFAULT_GAP = 5.0;

    protected Spin spin;
    protected Pane root;
    protected Circle circle;
    protected Arc arc;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double radius;
    protected final double strokeWidth;
    protected final double gap;

    /**
     * Constructs a new {@code InnerArcSpin} with default radius, stroke width, and gap.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public InnerArcSpin(Spin spin) {
        this(spin, DEFAULT_RADIUS, DEFAULT_STROKE_WIDTH, DEFAULT_GAP);
    }

    /**
     * Constructs a new {@code InnerArcSpin} with custom radius, stroke width, and gap values.
     *
     * @param spin        the {@link Spin} control instance using this skin
     * @param radius      the radius of the circle
     * @param strokeWidth the stroke width of the arc
     * @param gap         the spacing between the circle and arc
     */
    public InnerArcSpin(Spin spin, double radius, double strokeWidth, double gap) {
        this.spin = spin;
        this.radius = radius > 0 ? radius : DEFAULT_RADIUS;
        this.strokeWidth = strokeWidth > 0 ? strokeWidth : DEFAULT_STROKE_WIDTH;
        this.gap = gap;

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
        spin.setSkin(new InnerArcSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        circle = new Circle(radius, radius, radius);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(spin.getPrimaryColor());
        circle.setStrokeWidth(strokeWidth);
        circle.setStrokeType(StrokeType.INSIDE);

        double arcRadius = radius - gap;
        arc = new Arc(radius, radius, arcRadius, arcRadius, 0.0, 90.0);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(spin.getSecondaryColor());
        arc.setStrokeWidth(strokeWidth);
        arc.setStrokeType(StrokeType.INSIDE);

        root = new Pane(circle, arc);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(color -> circle.setStroke(color)),
            spin.secondaryColorProperty().subscribe(color -> arc.setStroke(color)),
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
        // update colors before creating a new timeline
        circle.setStroke(spin.getPrimaryColor());
        arc.setStroke(spin.getSecondaryColor());

        var nextTimeline = new Timeline(
            new KeyFrame(
                Duration.ZERO, new KeyValue(arc.startAngleProperty(), 0.0)
            ),
            new KeyFrame(
                spin.getDuration(), new KeyValue(arc.startAngleProperty(), -360.0)
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
        return radius * 2.0;
    }

    @Override
    public double computeMaxHeight(double width) {
        return radius * 2.0;
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
    }
}