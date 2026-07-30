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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a 3-bar vertical scale.
 *
 * <p>Animates three rounded vertical bars by interpolating their heights in a triangular wave pattern.
 */
public class BarsScaleSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "bars-scale-spin";
    public static final double DEFAULT_DURATION = 1.2;
    public static final double DEFAULT_SIZE = 28.0;

    // geometric proportions relative to a base size of 8x12px
    protected static final double BAR_WIDTH_RATIO = 1.0 / 8.0;
    protected static final double MIN_HEIGHT_RATIO = 1.0 / 12.0;
    protected static final double SPACING_RATIO = 4.0 / 12.0;

    protected static final int BAR_COUNT = 3; // [0] Left, [1] Center, [2] Right

    // static display height ratios before animation starts:
    // center bar = size / 2.5, sidebars = size / 1.5
    protected static final double CENTER_STATIC_HEIGHT_RATIO = 1.0 / 2.5;
    protected static final double SIDE_STATIC_HEIGHT_RATIO = 1.0 / 1.5;

    // phase offsets relative to full cycle duration (0s, 0.3s, 0.45s out of 0.6s)
    protected static final double[] PHASE_OFFSETS = {0.0, 0.5, 0.75};

    protected Spin spin;
    protected Pane root;
    protected final Rectangle[] bars = new Rectangle[BAR_COUNT];

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;

    /**
     * Constructs a new {@code BarsScaleSpin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public BarsScaleSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code BarsEqualizerSpin} with specified size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the control
     */
    public BarsScaleSpin(Spin spin, double size) {
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
        spin.setSkin(new BarsScaleSpin(spin));
        return spin;
    }

    //*************************************************************************

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected void construct() {
        double centerX = size / 2.0;
        double centerY = size / 2.0;

        Paint primaryColor = spin.getPrimaryColor();

        double barWidth = size * BAR_WIDTH_RATIO;
        double cornerRadius = barWidth;
        double spacing = size * SPACING_RATIO; // center-to-center offset

        root = new Pane();
        for (int i = 0; i < BAR_COUNT; i++) {
            var bar = new Rectangle();
            bar.setWidth(barWidth);
            bar.setArcWidth(cornerRadius);
            bar.setArcHeight(cornerRadius);
            bar.setFill(primaryColor);
            bar.setStroke(null);
            bar.setStrokeType(StrokeType.INSIDE);

            // horizontal centering
            double barCenterX = centerX + (i - 1) * spacing;
            bar.setX(barCenterX - (barWidth / 2.0));

            // static state before animation starts
            double initialH = (i == 1) ? (size * CENTER_STATIC_HEIGHT_RATIO) : (size * SIDE_STATIC_HEIGHT_RATIO);

            bar.setHeight(initialH);
            bar.setY(centerY - (initialH / 2.0));

            bars[i] = bar;
            root.getChildren().add(bar);
        }

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(paint -> updateColors()),
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

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected Timeline initTimeline() {
        double minH = size * MIN_HEIGHT_RATIO;
        double maxH = size;
        double centerY = size / 2.0;

        var nextTimeline = new Timeline();
        nextTimeline.setCycleCount(Timeline.INDEFINITE);

        for (int i = 0; i < BAR_COUNT; i++) {
            Rectangle bar = bars[i];
            double p = PHASE_OFFSETS[i];

            // point in time (in fractions 0..1) when column reaches Max and Min
            double tMax = p;
            double tMin = (p + 0.5) % 1.0;

            // collect keyframe timestamps in chronological order
            List<Double> fractions = new ArrayList<>(4);
            fractions.add(0.0);
            if (tMax > 0.0 && tMax < 1.0) {
                fractions.add(tMax);
            }
            if (tMin > 0.0 && tMin < 1.0) {
                fractions.add(tMin);
            }
            fractions.add(1.0);
            Collections.sort(fractions);

            for (double fraction : fractions) {
                // height calculation (linear triangular wave)
                double dMax = Math.min(Math.abs(fraction - p), 1.0 - Math.abs(fraction - p));
                double h = maxH - (maxH - minH) * dMax * 2.0;
                double y = centerY - (h / 2.0);

                Duration time = spin.getDuration().multiply(fraction);
                nextTimeline.getKeyFrames().add(new KeyFrame(time,
                    new KeyValue(bar.heightProperty(), h, Interpolator.LINEAR),
                    new KeyValue(bar.yProperty(), y, Interpolator.LINEAR)
                ));
            }
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

        // force rectangles back to a static position
        double centerY = size / 2.0;
        for (int i = 0; i < BAR_COUNT; i++) {
            double initialH = (i == 1) ? (size * CENTER_STATIC_HEIGHT_RATIO) : (size * SIDE_STATIC_HEIGHT_RATIO);
            bars[i].setHeight(initialH);
            bars[i].setY(centerY - (initialH / 2.0));
        }

        updateColors();
    }
}