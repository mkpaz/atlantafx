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
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing an animated hourglass.
 */
public class HourglassSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "hourglass-spin";
    public static final double DEFAULT_DURATION = 5.0;
    public static final double DEFAULT_SIZE = 40.0;

    // geometric proportions relative to a base size of 50x80px
    protected static final double BASE_WIDTH = 50.0;
    protected static final double BASE_HEIGHT = 80.0;
    protected static final double ASPECT_RATIO = BASE_WIDTH / BASE_HEIGHT;
    protected static final double CAP_WIDTH = 50.0;
    protected static final double CAP_HEIGHT = 5.0;
    protected static final double BULB_HALF_WIDTH = 20.0;
    protected static final double MAX_SAND_HEIGHT = 35.0;
    protected static final double BULB_STRAIGHT_WALL = 15.0;
    protected static final double STREAM_WIDTH = 2.0;

    // glass opacity multiplier
    protected static final double GLASS_OPACITY = 0.25;

    // rotation phase bounds (in degrees)
    protected static final double INITIAL_ROTATION_ANGLE = 0.0;
    protected static final double HALF_ROTATION_ANGLE = 180.0;
    protected static final double FULL_ROTATION_ANGLE = 360.0;

    // timeline normalized step timings (fractions of total cycle duration)
    protected static final double PHASE1_STREAM_END_FRACTION = 0.48;
    protected static final double PHASE1_ROTATE_START_FRACTION = 0.49;
    protected static final double PHASE1_ROTATE_END_FRACTION = 0.51;
    protected static final double PHASE2_STREAM_START_FRACTION = 0.52;
    protected static final double PHASE2_STREAM_END_FRACTION = 0.97;
    protected static final double PHASE2_ROTATE_START_FRACTION = 0.98;

    protected Spin spin;
    protected Pane root;
    protected Pane hourglassContainer;
    protected Rotate containerRotate;
    protected Rectangle topBorder;
    protected Rectangle bottomBorder;
    protected Path topBulbGlass;
    protected Path bottomBulbGlass;
    protected Rectangle topSand;
    protected Rectangle bottomSand;
    protected Rectangle stream;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected final DoubleProperty topSandShift = new SimpleDoubleProperty();
    protected final DoubleProperty bottomSandShift = new SimpleDoubleProperty();
    protected boolean autostart = true;

    protected final double size;

    /**
     * Constructs a new {@code HourglassSpin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public HourglassSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code HourglassSpin} with specified size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the control
     */
    public HourglassSpin(Spin spin, double size) {
        this.spin = spin;
        this.size = size > 0 ? size : DEFAULT_SIZE;

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
        spin.setSkin(new HourglassSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        double height = size;
        double width = size * ASPECT_RATIO;
        double unit = height / BASE_HEIGHT;

        root = new Pane();

        hourglassContainer = new Pane();
        hourglassContainer.setMinSize(width, height);
        hourglassContainer.setPrefSize(width, height);
        hourglassContainer.setMaxSize(width, height);

        double centerX = width / 2.0;
        double centerY = height / 2.0;

        containerRotate = new Rotate(INITIAL_ROTATION_ANGLE, centerX, centerY);
        hourglassContainer.getTransforms().add(containerRotate);

        // top and bottom cap borders
        double capWidthPx = CAP_WIDTH * unit;
        double capHeightPx = CAP_HEIGHT * unit;
        topBorder = createRect(
            capWidthPx, capHeightPx, centerX - capWidthPx / 2.0, centerY - (BASE_HEIGHT / 2.0) * unit
        );
        bottomBorder = createRect(
            capWidthPx, capHeightPx, centerX - capWidthPx / 2.0, centerY + ((BASE_HEIGHT / 2.0) - CAP_HEIGHT) * unit
        );

        // bulb glass outlines
        topBulbGlass = createTopBulb(unit, centerX, centerY);
        bottomBulbGlass = createBottomBulb(unit, centerX, centerY);

        // sand rects clipped to their respective bulb boundaries
        double sandWidthPx = BULB_HALF_WIDTH * 2.0 * unit;
        double maxSandHeightPx = MAX_SAND_HEIGHT * unit;
        topSand = createRect(sandWidthPx, maxSandHeightPx, centerX - sandWidthPx / 2.0, centerY - maxSandHeightPx);
        bottomSand = createRect(sandWidthPx, 0, centerX - sandWidthPx / 2.0, centerY + maxSandHeightPx);

        Path topClip = createTopBulb(unit, centerX, centerY);
        topClip.setFill(Color.BLACK);
        topSand.setClip(topClip);

        Path bottomClip = createBottomBulb(unit, centerX, centerY);
        bottomClip.setFill(Color.BLACK);
        bottomSand.setClip(bottomClip);

        // dynamic sand stream connecting the sand surfaces
        double streamWidthPx = STREAM_WIDTH * unit;
        stream = createRect(streamWidthPx, 0, centerX - streamWidthPx / 2.0, centerY);
        stream.setOpacity(0.0);

        updateColors();

        hourglassContainer.getChildren().addAll(
            topBulbGlass,
            bottomBulbGlass,
            topSand,
            bottomSand,
            stream,
            topBorder,
            bottomBorder
        );

        root.getChildren().add(hourglassContainer);

        // react to shift and rotation changes to calculate exact sand & stream positions
        topSandShift.addListener((obs, old, val) -> updateSandLevels(unit, centerY));
        bottomSandShift.addListener((obs, old, val) -> updateSandLevels(unit, centerY));
        containerRotate.angleProperty().addListener((obs, old, val) -> updateSandLevels(unit, centerY));

        topSandShift.set(-maxSandHeightPx);
        bottomSandShift.set(maxSandHeightPx);

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

    protected Rectangle createRect(double width, double height, double x, double y) {
        var rect = new Rectangle(width, height);
        rect.setX(x);
        rect.setY(y);
        rect.setStroke(null);
        return rect;
    }

    protected Path createTopBulb(double unit, double centerX, double centerY) {
        double halfWidth = BULB_HALF_WIDTH * unit;
        double topY = centerY - MAX_SAND_HEIGHT * unit;
        double arcStartY = topY + (BULB_STRAIGHT_WALL * unit);

        var path = new Path();
        path.getElements().addAll(
            new MoveTo(centerX - halfWidth, topY),
            new LineTo(centerX + halfWidth, topY),
            new LineTo(centerX + halfWidth, arcStartY),
            new ArcTo(halfWidth, BULB_HALF_WIDTH * unit, 0, centerX - halfWidth, arcStartY, false, true),
            new ClosePath()
        );
        path.setStroke(null);

        return path;
    }

    protected Path createBottomBulb(double unit, double centerX, double centerY) {
        double halfWidth = BULB_HALF_WIDTH * unit;
        double bottomY = centerY + MAX_SAND_HEIGHT * unit;
        double arcEndY = centerY + (BULB_HALF_WIDTH * unit);

        var path = new Path();
        path.getElements().addAll(
            new MoveTo(centerX - halfWidth, bottomY),
            new LineTo(centerX + halfWidth, bottomY),
            new LineTo(centerX + halfWidth, arcEndY),
            new ArcTo(halfWidth, BULB_HALF_WIDTH * unit, 0, centerX - halfWidth, arcEndY, false, false),
            new ClosePath()
        );
        path.setStroke(null);

        return path;
    }

    protected Timeline initTimeline() {
        double unit = size / BASE_HEIGHT;
        double maxShift = MAX_SAND_HEIGHT * unit;

        // phase keyframe duration offsets
        Duration duration = spin.getDuration();
        Duration streamEndPhase1 = duration.multiply(PHASE1_STREAM_END_FRACTION);
        Duration rotationStartPhase1 = duration.multiply(PHASE1_ROTATE_START_FRACTION);
        Duration rotationEndPhase1 = duration.multiply(PHASE1_ROTATE_END_FRACTION);
        Duration streamStartPhase2 = duration.multiply(PHASE2_STREAM_START_FRACTION);
        Duration streamEndPhase2 = duration.multiply(PHASE2_STREAM_END_FRACTION);
        Duration rotationStartPhase2 = duration.multiply(PHASE2_ROTATE_START_FRACTION);

        var nextTimeline = new Timeline(
            // phase 1: sand trickles downwards
            new KeyFrame(Duration.ZERO,
                new KeyValue(topSandShift, -maxShift),
                new KeyValue(bottomSandShift, maxShift),
                new KeyValue(containerRotate.angleProperty(), INITIAL_ROTATION_ANGLE),
                new KeyValue(stream.opacityProperty(), 1.0)
            ),
            new KeyFrame(streamEndPhase1,
                new KeyValue(topSandShift, 0.0),
                new KeyValue(bottomSandShift, 0.0),
                new KeyValue(containerRotate.angleProperty(), INITIAL_ROTATION_ANGLE),
                new KeyValue(stream.opacityProperty(), 1.0)
            ),

            // hide stream before 180 deg rotation
            new KeyFrame(rotationStartPhase1,
                new KeyValue(topSandShift, 0.0),
                new KeyValue(bottomSandShift, 0.0),
                new KeyValue(containerRotate.angleProperty(), INITIAL_ROTATION_ANGLE),
                new KeyValue(stream.opacityProperty(), 0.0)
            ),

            // rotate 0 -> 180 deg
            new KeyFrame(rotationEndPhase1,
                new KeyValue(topSandShift, 0.0),
                new KeyValue(bottomSandShift, 0.0),
                new KeyValue(containerRotate.angleProperty(), HALF_ROTATION_ANGLE),
                new KeyValue(stream.opacityProperty(), 0.0)
            ),

            // phase 2: sand trickles downwards (180 deg)
            new KeyFrame(streamStartPhase2,
                new KeyValue(topSandShift, 0.0),
                new KeyValue(bottomSandShift, 0.0),
                new KeyValue(containerRotate.angleProperty(), HALF_ROTATION_ANGLE),
                new KeyValue(stream.opacityProperty(), 1.0)
            ),
            new KeyFrame(streamEndPhase2,
                new KeyValue(topSandShift, -maxShift),
                new KeyValue(bottomSandShift, maxShift),
                new KeyValue(containerRotate.angleProperty(), HALF_ROTATION_ANGLE),
                new KeyValue(stream.opacityProperty(), 1.0)
            ),

            // hide stream before 360 deg rotation
            new KeyFrame(rotationStartPhase2,
                new KeyValue(topSandShift, -maxShift),
                new KeyValue(bottomSandShift, maxShift),
                new KeyValue(containerRotate.angleProperty(), HALF_ROTATION_ANGLE),
                new KeyValue(stream.opacityProperty(), 0.0)
            ),

            // rotate 180 -> 360 deg
            new KeyFrame(duration,
                new KeyValue(topSandShift, -maxShift),
                new KeyValue(bottomSandShift, maxShift),
                new KeyValue(containerRotate.angleProperty(), FULL_ROTATION_ANGLE),
                new KeyValue(stream.opacityProperty(), 0.0)
            )
        );

        nextTimeline.setCycleCount(Timeline.INDEFINITE);
        return nextTimeline;
    }

    // Recalculates sand block bounds and stream dimensions based on rotation angle.
    protected void updateSandLevels(double unit, double centerY) {
        double currentAngle = containerRotate.getAngle() % FULL_ROTATION_ANGLE;
        if (currentAngle < 0) {
            currentAngle += FULL_ROTATION_ANGLE;
        }

        boolean isFlipped = (currentAngle > 90.0 && currentAngle < 270.0);

        double topShiftVal = topSandShift.get();
        double bottomShiftVal = bottomSandShift.get();
        double maxShift = MAX_SAND_HEIGHT * unit;

        if (!isFlipped) {
            // normal position (0 degrees)
            double topSandHeight = Math.max(0.0, -topShiftVal);
            topSand.setY(centerY - topSandHeight);
            topSand.setHeight(topSandHeight);

            double bottomSandHeight = Math.max(0.0, maxShift - bottomShiftVal);
            bottomSand.setY(centerY + maxShift - bottomSandHeight);
            bottomSand.setHeight(bottomSandHeight);
        } else {
            // inverted position (180 degrees)
            double bottomSandHeight = Math.max(0.0, maxShift - bottomShiftVal);
            bottomSand.setY(centerY);
            bottomSand.setHeight(bottomSandHeight);

            double topSandHeight = Math.max(0.0, -topShiftVal);
            topSand.setY(centerY - maxShift);
            topSand.setHeight(topSandHeight);
        }

        // connect the bottom of the upper sand to the top of the lower sand
        double streamStartY = topSand.getY() + topSand.getHeight();
        double streamEndY = bottomSand.getY();

        if (streamEndY > streamStartY) {
            stream.setY(streamStartY);
            stream.setHeight(streamEndY - streamStartY);
        } else {
            stream.setHeight(0.0);
        }
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
        return size * ASPECT_RATIO;
    }

    @Override
    public double computeMaxHeight(double width) {
        return size;
    }

    protected void updateColors() {
        Paint primaryColor = spin.getPrimaryColor();
        Paint secondaryColor = spin.getSecondaryColor();

        topBorder.setFill(primaryColor);
        bottomBorder.setFill(primaryColor);

        if (primaryColor instanceof Color color) {
            topBulbGlass.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), GLASS_OPACITY));
            bottomBulbGlass.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), GLASS_OPACITY));
        } else {
            topBulbGlass.setFill(primaryColor);
            bottomBulbGlass.setFill(primaryColor);
        }

        topSand.setFill(secondaryColor);
        bottomSand.setFill(secondaryColor);
        stream.setFill(secondaryColor);
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

        double unit = size / BASE_HEIGHT;
        double maxShift = MAX_SAND_HEIGHT * unit;

        topSandShift.set(-maxShift);
        bottomSandShift.set(maxShift);
        containerRotate.setAngle(INITIAL_ROTATION_ANGLE);
        stream.setOpacity(0.0);

        updateColors();
    }
}