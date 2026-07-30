/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import javafx.animation.Animation;
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
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a pie chart filling step-by-step.
 */
public class PieFillSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "pie-fill-spin";
    public static final double DEFAULT_DURATION = 1.2;
    public static final double DEFAULT_RADIUS = 14.0;

    protected static final int SECTOR_COUNT = 4;
    protected static final double ARC_ANGLE = 90.0;

    protected Spin spin;
    protected Pane root;
    protected Arc[] sectors = new Arc[SECTOR_COUNT]; // [0] Top, [1] Right, [2] Bottom, [3] Left

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double radius;

    /**
     * Constructs a new {@code PieFillSpin} with default radius.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public PieFillSpin(Spin spin) {
        this(spin, DEFAULT_RADIUS);
    }

    /**
     * Constructs a new {@code PieFillSpin} with specified radius.
     *
     * @param spin   the {@link Spin} control instance using this skin
     * @param radius the radius of the pie control
     */
    public PieFillSpin(Spin spin, double radius) {
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
        spin.setSkin(new PieFillSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        root = new Pane();

        // starting angles for 4 pie sectors clockwise: top, right, bottom, left
        double[] startAngles = {45.0, 315.0, 225.0, 135.0};

        for (int i = 0; i < SECTOR_COUNT; i++) {
            var arc = new Arc(radius, radius, radius, radius, startAngles[i], ARC_ANGLE);
            arc.setType(ArcType.ROUND);
            arc.setStroke(null);

            sectors[i] = arc;
            root.getChildren().add(arc);
        }

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(paint -> updateTimeline()),
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
        Paint transparent = Color.TRANSPARENT;
        Paint primaryColor = spin.getPrimaryColor();

        var nextTimeline = new Timeline(
            // 0% - 19%: all transparent
            new KeyFrame(Duration.ZERO,
                new KeyValue(sectors[0].fillProperty(), transparent),
                new KeyValue(sectors[1].fillProperty(), transparent),
                new KeyValue(sectors[2].fillProperty(), transparent),
                new KeyValue(sectors[3].fillProperty(), transparent)
            ),
            // 20% - 39%: 1 visible
            new KeyFrame(spin.getDurationPercentage(20.0),
                new KeyValue(sectors[0].fillProperty(), primaryColor),
                new KeyValue(sectors[1].fillProperty(), transparent),
                new KeyValue(sectors[2].fillProperty(), transparent),
                new KeyValue(sectors[3].fillProperty(), transparent)
            ),
            // 40% - 59%: 1 & 2 visible
            new KeyFrame(spin.getDurationPercentage(40.0),
                new KeyValue(sectors[0].fillProperty(), primaryColor),
                new KeyValue(sectors[1].fillProperty(), primaryColor),
                new KeyValue(sectors[2].fillProperty(), transparent),
                new KeyValue(sectors[3].fillProperty(), transparent)
            ),
            // 60% - 79%: 1, 2 & 3 visible
            new KeyFrame(spin.getDurationPercentage(60.0),
                new KeyValue(sectors[0].fillProperty(), primaryColor),
                new KeyValue(sectors[1].fillProperty(), primaryColor),
                new KeyValue(sectors[2].fillProperty(), primaryColor),
                new KeyValue(sectors[3].fillProperty(), transparent)
            ),
            // 80% - 100%: all sectors visible
            new KeyFrame(spin.getDurationPercentage(80.0),
                new KeyValue(sectors[0].fillProperty(), primaryColor),
                new KeyValue(sectors[1].fillProperty(), primaryColor),
                new KeyValue(sectors[2].fillProperty(), primaryColor),
                new KeyValue(sectors[3].fillProperty(), primaryColor)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(sectors[0].fillProperty(), primaryColor),
                new KeyValue(sectors[1].fillProperty(), primaryColor),
                new KeyValue(sectors[2].fillProperty(), primaryColor),
                new KeyValue(sectors[3].fillProperty(), primaryColor)
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
        for (Arc sector : sectors) {
            sector.setFill(spin.getPrimaryColor());
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

        updateColors();
    }
}