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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing two meshed rotating gears.
 */
public class GearsSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "gears-spin";
    public static final double DEFAULT_DURATION = 12.0;
    public static final double DEFAULT_SCALE = 0.8;

    protected static final double CANVAS_WIDTH = 60.0;
    protected static final double CANVAS_HEIGHT = 40.0;

    protected static final double LARGE_GEAR_RADIUS = 18.0;
    protected static final double LARGE_GEAR_HOLE_RADIUS = 6.0;
    protected static final double LARGE_GEAR_CUTOUT_RADIUS = 4.0;

    protected static final double SMALL_GEAR_RADIUS = 12.0;
    protected static final double SMALL_GEAR_HOLE_RADIUS = 4.0;
    protected static final double SMALL_GEAR_CUTOUT_RADIUS = 2.5;
    protected static final double SMALL_GEAR_OFFSET_X = 35.0;
    protected static final double SMALL_GEAR_OFFSET_Y = 15.0;

    protected static final double LARGE_GEAR_ROTATIONS_PER_CYCLE = 4.0; // 4 rotations per full cycle
    protected static final double SMALL_GEAR_ROTATIONS_PER_CYCLE = 3.0; // 3 rotations per full cycle

    protected static final int CUTOUT_COUNT = 8;

    protected Spin spin;
    protected Pane root;
    protected Shape largeGear;
    protected Shape smallGear;
    protected Rotate largeGearRotate;
    protected Rotate smallGearRotate;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double scale;

    /**
     * Constructs a new {@code GearsSpin} with default scale.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public GearsSpin(Spin spin) {
        this(spin, DEFAULT_SCALE);
    }

    /**
     * Constructs a new {@code GearsSpin} with specified scale.
     *
     * @param spin  the {@link Spin} control instance using this skin
     * @param scale the scale size for manipulating control dimensions
     */
    public GearsSpin(Spin spin, double scale) {
        this.spin = spin;
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
        spin.setSkin(new GearsSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        root = new Pane();

        // large gear
        double largeRadius = scale * LARGE_GEAR_RADIUS;
        var largeBase = new Circle(largeRadius, largeRadius, largeRadius);
        var largeHole = new Circle(largeRadius, largeRadius, scale * LARGE_GEAR_HOLE_RADIUS);

        largeGear = Shape.subtract(largeBase, largeHole);
        double largeCutoutRadius = scale * LARGE_GEAR_CUTOUT_RADIUS;

        for (int i = 0; i < CUTOUT_COUNT; i++) {
            double angleRad = Math.toRadians(i * (360.0 / CUTOUT_COUNT));
            double cx = largeRadius + largeRadius * Math.cos(angleRad);
            double cy = largeRadius + largeRadius * Math.sin(angleRad);

            Shape cutout = new Circle(cx, cy, largeCutoutRadius);
            largeGear = Shape.subtract(largeGear, cutout);
        }

        largeGear.setStrokeType(StrokeType.INSIDE);
        largeGearRotate = new Rotate(0.0, largeRadius, largeRadius);
        largeGear.getTransforms().add(largeGearRotate);

        // small gear
        double smallRadius = scale * SMALL_GEAR_RADIUS;
        var smallBase = new Circle(smallRadius, smallRadius, smallRadius);
        var smallHole = new Circle(smallRadius, smallRadius, scale * SMALL_GEAR_HOLE_RADIUS);

        smallGear = Shape.subtract(smallBase, smallHole);
        double smallCutoutRadius = scale * SMALL_GEAR_CUTOUT_RADIUS;

        for (int i = 0; i < CUTOUT_COUNT; i++) {
            double angleRad = Math.toRadians(i * (360.0 / CUTOUT_COUNT));
            double cx = smallRadius + smallRadius * Math.cos(angleRad);
            double cy = smallRadius + smallRadius * Math.sin(angleRad);

            var cutout = new Circle(cx, cy, smallCutoutRadius);
            smallGear = Shape.subtract(smallGear, cutout);
        }

        smallGear.setStrokeType(StrokeType.INSIDE);
        smallGear.setLayoutX(scale * SMALL_GEAR_OFFSET_X);
        smallGear.setLayoutY(scale * SMALL_GEAR_OFFSET_Y);

        smallGearRotate = new Rotate(0.0, smallRadius, smallRadius);
        smallGear.getTransforms().add(smallGearRotate);

        root.getChildren().addAll(largeGear, smallGear);

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

        updateColors();
    }

    protected Timeline initTimeline() {
        // the large gear rotates backwards, small gear rotates forwards,
        // we multiply 360 by the number of rotations per full cycle
        var nextTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(largeGearRotate.angleProperty(), 0.0, Interpolator.LINEAR),
                new KeyValue(smallGearRotate.angleProperty(), 0.0, Interpolator.LINEAR)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(
                    largeGearRotate.angleProperty(), -360.0 * LARGE_GEAR_ROTATIONS_PER_CYCLE, Interpolator.LINEAR
                ),
                new KeyValue(
                    smallGearRotate.angleProperty(), 360.0 * SMALL_GEAR_ROTATIONS_PER_CYCLE, Interpolator.LINEAR
                )
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
        return scale * CANVAS_WIDTH;
    }

    @Override
    public double computeMaxHeight(double width) {
        return scale * CANVAS_HEIGHT;
    }

    protected void updateColors() {
        largeGear.setFill(spin.getPrimaryColor());
        smallGear.setFill(spin.getSecondaryColor());
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
        updateColors();
    }
}