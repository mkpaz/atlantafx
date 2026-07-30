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
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a flipping square.
 */
public class FlipSquareSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "flip-square-spin";
    public static final double DEFAULT_DURATION = 1.5;
    public static final double DEFAULT_SIZE = 18.0;

    protected static final double INITIAL_ROTATE_ANGLE = 0.0;
    protected static final double TARGET_FLIP_ANGLE = -180.0;
    protected static final double PIVOT_RATIO = 0.5;

    protected Spin spin;
    protected Pane root;
    protected Rectangle rect;
    protected Rotate rotateX;
    protected Rotate rotateY;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;

    /**
     * Constructs a new {@code FlipSquareSkin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public FlipSquareSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code FlipSquareSkin} with specified size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the control
     */
    public FlipSquareSpin(Spin spin, double size) {
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
        spin.setSkin(new FlipSquareSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        rect = new Rectangle(size, size);
        rect.setFill(spin.getPrimaryColor());

        double pivot = size * PIVOT_RATIO;
        rotateX = new Rotate(INITIAL_ROTATE_ANGLE, pivot, pivot, 0, Rotate.X_AXIS);
        rotateY = new Rotate(INITIAL_ROTATE_ANGLE, pivot, pivot, 0, Rotate.Y_AXIS);
        rect.getTransforms().addAll(rotateX, rotateY);

        root = new Pane(rect);

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
        rect.setFill(spin.getPrimaryColor());

        rotateX.setAngle(INITIAL_ROTATE_ANGLE);
        rotateY.setAngle(INITIAL_ROTATE_ANGLE);

        Paint primaryColor = spin.getPrimaryColor();
        Paint secondaryColor = spin.getSecondaryColor();

        // 0%  - 50%  : flip X axis (-180°), transition primaryColor -> secondaryColor
        // 50% - 100% : flip Y axis (-180°), transition secondaryColor -> primaryColor
        var nextTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotateX.angleProperty(), INITIAL_ROTATE_ANGLE, Interpolator.EASE_BOTH),
                new KeyValue(rotateY.angleProperty(), INITIAL_ROTATE_ANGLE, Interpolator.EASE_BOTH),
                new KeyValue(rect.fillProperty(), primaryColor, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(spin.getDurationPercentage(50.0),
                new KeyValue(rotateX.angleProperty(), TARGET_FLIP_ANGLE, Interpolator.EASE_BOTH),
                new KeyValue(rotateY.angleProperty(), INITIAL_ROTATE_ANGLE, Interpolator.EASE_BOTH),
                new KeyValue(rect.fillProperty(), secondaryColor, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(rotateX.angleProperty(), TARGET_FLIP_ANGLE, Interpolator.EASE_BOTH),
                new KeyValue(rotateY.angleProperty(), TARGET_FLIP_ANGLE, Interpolator.EASE_BOTH),
                new KeyValue(rect.fillProperty(), primaryColor, Interpolator.EASE_BOTH)
            )
        );

        nextTimeline.setCycleCount(Timeline.INDEFINITE);
        nextTimeline.setAutoReverse(false);

        return nextTimeline;
    }

    protected void updateColors() {
        var activeTimeline = timeline.get();
        if (activeTimeline != null) {
            boolean isRunning = activeTimeline.getStatus() == Animation.Status.RUNNING;
            Duration currentTime = activeTimeline.getCurrentTime();

            activeTimeline.stop();

            var newTimeline = initTimeline();
            timeline.set(newTimeline);

            if (isRunning) {
                newTimeline.playFrom(currentTime);
            }
        } else {
            rect.setFill(spin.getPrimaryColor());
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
        return size;
    }

    @Override
    public double computeMaxHeight(double width) {
        return size;
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
        rotateX.setAngle(INITIAL_ROTATE_ANGLE);
        rotateY.setAngle(INITIAL_ROTATE_ANGLE);
        rect.setFill(spin.getPrimaryColor());
    }
}
