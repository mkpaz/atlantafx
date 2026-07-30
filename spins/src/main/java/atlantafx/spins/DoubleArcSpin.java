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
 * Skin implementation for {@link Spin} representing two expanding and merging arcs.
 */
public class DoubleArcSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "double-arc-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_RADIUS = 14;
    public static final double DEFAULT_STROKE_WIDTH = 3;

    protected Spin spin;
    protected Pane root;
    protected Group spinningContent;
    protected Circle arcBefore;
    protected Circle arcAfter;
    protected Rotate spinRotate;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected DoubleProperty primaryArcLength = new SimpleDoubleProperty(0);
    protected DoubleProperty secondaryArcLength = new SimpleDoubleProperty(0);
    protected boolean autostart = true;

    protected final double radius;
    protected final double strokeWidth;
    protected final double center;
    protected final double totalSize;

    /**
     * Constructs a {@code DoubleArcSpin} skin with default dimensions.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public DoubleArcSpin(Spin spin) {
        this(spin, DEFAULT_RADIUS, DEFAULT_STROKE_WIDTH);
    }

    /**
     * Constructs a {@code DoubleArcSpin} skin with custom radius and stroke width.
     *
     * @param spin        the {@link Spin} control instance using this skin
     * @param radius      the radius of the inner spinner path
     * @param strokeWidth the thickness of the spinner arcs
     */
    public DoubleArcSpin(Spin spin, double radius, double strokeWidth) {
        this.spin = spin;
        this.radius = radius > 0 ? radius : DEFAULT_RADIUS;
        this.strokeWidth = strokeWidth > 0 ? strokeWidth : DEFAULT_STROKE_WIDTH;

        this.center = this.radius + (this.strokeWidth / 2.0);
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
        spin.setSkin(new DoubleArcSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        double circumference = 2 * Math.PI * radius;
        double topOffset = circumference / 4.0; // offset to 12 o'clock position

        arcBefore = createCircle();
        arcBefore.setStroke(spin.getSecondaryColor());
        arcBefore.setStrokeDashOffset(topOffset);
        arcBefore.setStrokeType(StrokeType.INSIDE);

        arcAfter = createCircle();
        arcAfter.setStroke(spin.getPrimaryColor());
        arcAfter.setStrokeType(StrokeType.INSIDE);

        double minGap = 0.01;

        // first arc grows clockwise starting from the 12 o'clock position
        primaryArcLength.addListener((obs, old, val) -> {
            double len = val.doubleValue();
            arcBefore.getStrokeDashArray().setAll(len, Math.max(minGap, circumference - len));
        });

        // second arc follows closely, aligned against the end of the first arc
        secondaryArcLength.addListener((obs, old, val) -> {
            double len = val.doubleValue();
            arcAfter.getStrokeDashArray().setAll(len, Math.max(minGap, circumference - len));
            // shift offset to ensure smooth connection
            arcAfter.setStrokeDashOffset(topOffset - len);
        });

        spinRotate = new Rotate(0, center, center);

        // animated inner container
        spinningContent = new Group(arcBefore, arcAfter);
        spinningContent.getTransforms().add(spinRotate);

        // exclude animated container from parent layout calculation
        spinningContent.setManaged(false);

        root = new Pane(spinningContent);

        // initialize static state (full circle) before animation starts
        resetState();

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(color -> arcAfter.setStroke(color)),
            spin.secondaryColorProperty().subscribe(color -> arcBefore.setStroke(color)),
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
        arcBefore.setStroke(spin.getSecondaryColor());
        arcAfter.setStroke(spin.getPrimaryColor());

        double circumference = 2 * Math.PI * radius;
        double quarterCircumference = circumference / 4.0; // 90 degrees
        double halfCircumference = circumference / 2.0;    // 180 degrees

        Duration totalDuration = spin.getDuration();
        Duration halfDuration = totalDuration.divide(2);
        Duration threeQuarterDuration = totalDuration.multiply(0.75);

        var timeline = new Timeline(
            // 0%: both arcs start at zero length
            new KeyFrame(Duration.ZERO,
                new KeyValue(primaryArcLength, 0),
                new KeyValue(secondaryArcLength, 0),
                new KeyValue(spinRotate.angleProperty(), 0)
            ),
            // 50%: primary arc reaches 180, secondary arc reaches 90
            new KeyFrame(halfDuration,
                new KeyValue(primaryArcLength, halfCircumference),
                new KeyValue(secondaryArcLength, quarterCircumference)
            ),
            // 75%: secondary arc catches up to 180 deg
            new KeyFrame(threeQuarterDuration,
                new KeyValue(primaryArcLength, halfCircumference),
                new KeyValue(secondaryArcLength, halfCircumference)
            ),
            // 100%: both arcs hold 180 deg each, full 360 deg rotation performed
            new KeyFrame(totalDuration,
                new KeyValue(primaryArcLength, halfCircumference),
                new KeyValue(secondaryArcLength, halfCircumference),
                new KeyValue(spinRotate.angleProperty(), 360.0)
            )
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);

        return timeline;
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

    protected Circle createCircle() {
        var circle = new Circle(center, center, radius);
        circle.setFill(Color.TRANSPARENT);
        circle.setStrokeWidth(strokeWidth);
        return circle;
    }

    protected Timeline doStart() {
        var t = initTimeline();
        t.playFromStart();
        return t;
    }

    protected void doStop() {
        var t = timeline.get();
        if (t != null) {
            t.stop();
        }
        resetState();
    }

    protected void resetState() {
        double halfCircumference = Math.PI * radius;
        primaryArcLength.set(halfCircumference);
        secondaryArcLength.set(halfCircumference);
        spinRotate.setAngle(0);
    }
}