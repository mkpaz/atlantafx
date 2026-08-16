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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a 4-bar equalizer.
 *
 * <p>Animates four rounded vertical bars by interpolating their heights around a central horizontal axis
 */
public class BarsEqualizerSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "bars-equalizer-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_SIZE = 28.0;

    // geometric proportions relative to a base size of 62px
    protected static final double BAR_WIDTH_RATIO = 8.0 / 62.0;
    protected static final double BAR_STEP_RATIO = 18.0 / 62.0;

    // in static state (before start) bars are displayed with height size / 2
    protected static final double STATIC_HEIGHT_RATIO = 0.5;

    protected static final int BAR_COUNT = 4;

    // matrix of bar heights for 4 columns at each keyframe step (0%, 25%, 50%, 75%, 100%)
    protected static final double[][] HEIGHT_RATIOS = {
        {0.2, 0.6, 1.0, 0.6}, // 0%
        {0.6, 1.0, 0.6, 0.2}, // 25%
        {1.0, 0.6, 0.2, 0.6}, // 50%
        {0.6, 0.2, 0.6, 1.0}, // 75%
        {0.2, 0.6, 1.0, 0.6}  // 100% (same as 0% for seamless looping)
    };

    protected static final double[] KEY_FRAME_PROGRESSIONS = {0.0, 0.25, 0.50, 0.75, 1.0};

    protected Spin spin;
    protected Pane root;
    protected final Rectangle[] bars = new Rectangle[BAR_COUNT]; // [0], [1], [2], [3]

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;

    /**
     * Constructs a new {@code BarsEqualizerSpin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public BarsEqualizerSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code BarsEqualizerSpin} with specified size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the control
     */
    public BarsEqualizerSpin(Spin spin, double size) {
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
        spin.setSkin(new BarsEqualizerSpin(spin));
        return spin;
    }

    //*************************************************************************

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected void construct() {
        double centerY = size / 2.0;

        double barWidth = size * BAR_WIDTH_RATIO;
        double barStep = size * BAR_STEP_RATIO;
        double cornerRadius = barWidth; // end-cap rounding
        double staticHeight = size * STATIC_HEIGHT_RATIO;

        root = new Pane();
        for (int i = 0; i < BAR_COUNT; i++) {
            var bar = new Rectangle();
            bar.setWidth(barWidth);
            bar.setArcWidth(cornerRadius);
            bar.setArcHeight(cornerRadius);
            bar.setFill(spin.getPrimaryColor());
            bar.setStroke(null);
            bar.setStrokeType(StrokeType.INSIDE);

            // position on X axis according to offsets (0, 18, 36, 54)
            double barX = i * barStep;
            bar.setX(barX);

            // initial static height (size / 2) centered along Y axis
            bar.setHeight(staticHeight);
            bar.setY(centerY - (staticHeight / 2.0));

            bars[i] = bar;
            root.getChildren().add(bar);
        }

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
    }

    protected Timeline initTimeline() {
        double centerY = size / 2.0;

        var nextTimeline = new Timeline();
        nextTimeline.setCycleCount(Timeline.INDEFINITE);

        for (int frameIdx = 0; frameIdx < KEY_FRAME_PROGRESSIONS.length; frameIdx++) {
            Duration frameTime = spin.getDuration().multiply(KEY_FRAME_PROGRESSIONS[frameIdx]);
            var keyValues = new ArrayList<KeyValue>(BAR_COUNT * 2);

            for (int barIdx = 0; barIdx < BAR_COUNT; barIdx++) {
                double targetHeight = size * HEIGHT_RATIOS[frameIdx][barIdx];
                double targetY = centerY - (targetHeight / 2.0); // center relative to vertical axis

                keyValues.add(
                    new KeyValue(bars[barIdx].heightProperty(), targetHeight, Interpolator.LINEAR)
                );
                keyValues.add(
                    new KeyValue(bars[barIdx].yProperty(), targetY, Interpolator.LINEAR)
                );
            }

            nextTimeline.getKeyFrames().add(
                new KeyFrame(frameTime, keyValues.toArray(new KeyValue[0]))
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
        return size;
    }

    @Override
    public double computeMaxHeight(double width) {
        return size;
    }

    protected void updateColors() {
        Timeline activeTimeline = timeline.get();
        if (activeTimeline == null || activeTimeline.getStatus() != Timeline.Status.STOPPED) {
            Paint color = spin.getPrimaryColor();
            for (Rectangle bar : bars) {
                bar.setFill(color);
            }
        }
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
            activeTimeline.stop();
        }

        // reset all bars to static state
        double centerY = size / 2.0;
        double staticHeight = size * STATIC_HEIGHT_RATIO;
        for (Rectangle bar : bars) {
            bar.setHeight(staticHeight);
            bar.setY(centerY - (staticHeight / 2.0));
        }

        updateColors();
    }
}