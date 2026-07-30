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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a box with a bouncing ball.
 */
public class PerimeterBallSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "perimeter-ball-spin";
    public static final double DEFAULT_DURATION = 1.2;
    public static final double DEFAULT_SIZE = 28.0;

    // geometric proportions relative to a base size of 54px
    protected static final double DOT_DIAMETER_RATIO = 20.0 / 54.0;
    protected static final double PADDING_RATIO = 3.0 / 54.0;
    protected static final double CORNER_RADIUS_RATIO = 8.0 / 54.0;

    protected Spin spin;
    protected Pane root;
    protected Rectangle backgroundRect;
    protected Circle dot;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;

    /**
     * Constructs a new {@code PerimeterBallSpin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public PerimeterBallSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code PerimeterBallSpin} with specified size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the bounding square container
     */
    public PerimeterBallSpin(Spin spin, double size) {
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
        spin.setSkin(new PerimeterBallSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        double dotDiameter = size * DOT_DIAMETER_RATIO;
        double dotRadius = dotDiameter / 2.0;
        double padding = size * PADDING_RATIO;
        double cornerRadius = size * CORNER_RADIUS_RATIO;

        backgroundRect = new Rectangle(size, size);
        backgroundRect.setArcWidth(cornerRadius * 2.0);
        backgroundRect.setArcHeight(cornerRadius * 2.0);
        backgroundRect.setFill(spin.getPrimaryColor());
        backgroundRect.setStrokeType(StrokeType.INSIDE);

        dot = new Circle(padding + dotRadius, padding + dotRadius, dotRadius);
        dot.setFill(spin.getSecondaryColor());
        dot.setStrokeType(StrokeType.INSIDE);

        root = new Pane(backgroundRect, dot);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(paint -> updateTimeline()),
            spin.secondaryColorProperty().subscribe(paint -> updateTimeline()),
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
        Paint primaryColor = spin.getPrimaryColor();
        Paint secondaryColor = spin.getSecondaryColor();

        // reset state prior to timeline creation
        backgroundRect.setFill(primaryColor);
        dot.setFill(secondaryColor);
        dot.setTranslateX(0.0);
        dot.setTranslateY(0.0);

        // calculate actual travel distance: size - (2 * padding) - dotDiameter
        double padding = size * PADDING_RATIO;
        double dotDiameter = size * DOT_DIAMETER_RATIO;
        double translateMax = size - (2.0 * padding) - dotDiameter;

        var nextTimeline = new Timeline(
            // top-left
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(dot.translateXProperty(), 0.0, Interpolator.LINEAR),
                new KeyValue(dot.translateYProperty(), 0.0, Interpolator.LINEAR)
            ),
            // top-right
            new KeyFrame(
                spin.getDurationPercentage(25.0),
                new KeyValue(dot.translateXProperty(), translateMax, Interpolator.LINEAR),
                new KeyValue(dot.translateYProperty(), 0.0, Interpolator.LINEAR)
            ),
            // bottom-right
            new KeyFrame(
                spin.getDurationPercentage(50.0),
                new KeyValue(dot.translateXProperty(), translateMax, Interpolator.LINEAR),
                new KeyValue(dot.translateYProperty(), translateMax, Interpolator.LINEAR)
            ),
            // bottom-left
            new KeyFrame(
                spin.getDurationPercentage(75.0),
                new KeyValue(dot.translateXProperty(), 0.0, Interpolator.LINEAR),
                new KeyValue(dot.translateYProperty(), translateMax, Interpolator.LINEAR)
            ),
            // top-left
            new KeyFrame(
                spin.getDuration(),
                new KeyValue(dot.translateXProperty(), 0.0, Interpolator.LINEAR),
                new KeyValue(dot.translateYProperty(), 0.0, Interpolator.LINEAR)
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
        return size;
    }

    @Override
    public double computeMaxHeight(double width) {
        return size;
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
        backgroundRect.setFill(spin.getPrimaryColor());
        dot.setFill(spin.getSecondaryColor());
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
        backgroundRect.setFill(spin.getPrimaryColor());
        dot.setFill(spin.getSecondaryColor());
        dot.setTranslateX(0.0);
        dot.setTranslateY(0.0);
    }
}