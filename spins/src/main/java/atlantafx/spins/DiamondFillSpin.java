/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Subscription;
import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static javafx.animation.Timeline.*;

/**
 * Skin implementation for {@link Spin} representing a diamond (rhombus) made of four sectors.
 * During animation, sectors fill and empty in a step-by-step sequence.
 */
public class DiamondFillSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "diamond-fill-spin";
    public static final double DEFAULT_DURATION = 2.0;
    public static final double DEFAULT_SIZE = 28.0; // diagonal of the diamond

    protected Spin spin;
    protected Pane root;
    protected Rectangle[] sectors = new Rectangle[4]; // [0] top, [1] right, [2] bottom, [3] left

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;

    /**
     * Constructs a new {@code DiamondFillSpin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public DiamondFillSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code DiamondFillSpin} with specified size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the control
     */
    public DiamondFillSpin(Spin spin, double size) {
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
        spin.setSkin(new DiamondFillSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        double bigSide = size / Math.sqrt(2.0); // side length of the diamond
        double smallSide = bigSide / 2.0; // side length of each inner square sector

        var grid = new Group();

        var rectTop = new Rectangle(smallSide, smallSide);
        var rectRight = new Rectangle(smallSide, smallSide);
        var rectBottom = new Rectangle(smallSide, smallSide);
        var rectLeft = new Rectangle(smallSide, smallSide);

        rectTop.setX(0.0);
        rectTop.setY(0.0);

        rectRight.setX(smallSide);
        rectRight.setY(0.0);

        rectBottom.setX(smallSide);
        rectBottom.setY(smallSide);

        rectLeft.setX(0.0);
        rectLeft.setY(smallSide);

        sectors[0] = rectTop;
        sectors[1] = rectRight;
        sectors[2] = rectBottom;
        sectors[3] = rectLeft;

        // default state: all sectors visible with primary color
        Paint primaryColor = spin.getPrimaryColor();
        for (var rect : sectors) {
            rect.setFill(primaryColor);
            rect.setStroke(null);
            grid.getChildren().add(rect);
        }

        // rotate group by 45 degrees around local center
        grid.setRotate(45.0);

        // center group inside bounding pane
        double offset = (size - bigSide) / 2.0;
        grid.setLayoutX(offset);
        grid.setLayoutY(offset);

        root = new Pane(grid);

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
    }

    protected Timeline initTimeline() {
        Duration duration = spin.getDuration();
        Color transparent = Color.TRANSPARENT;
        Paint primaryColor = spin.getPrimaryColor();

        var nextTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(sectors[0].fillProperty(), transparent),
                new KeyValue(sectors[1].fillProperty(), transparent),
                new KeyValue(sectors[2].fillProperty(), transparent),
                new KeyValue(sectors[3].fillProperty(), transparent)
            ),
            new KeyFrame(duration.multiply(0.125),
                new KeyValue(sectors[0].fillProperty(), primaryColor),
                new KeyValue(sectors[1].fillProperty(), transparent),
                new KeyValue(sectors[2].fillProperty(), transparent),
                new KeyValue(sectors[3].fillProperty(), transparent)
            ),
            new KeyFrame(duration.multiply(0.250),
                new KeyValue(sectors[0].fillProperty(), primaryColor),
                new KeyValue(sectors[1].fillProperty(), primaryColor),
                new KeyValue(sectors[2].fillProperty(), transparent),
                new KeyValue(sectors[3].fillProperty(), transparent)
            ),
            new KeyFrame(duration.multiply(0.375),
                new KeyValue(sectors[0].fillProperty(), primaryColor),
                new KeyValue(sectors[1].fillProperty(), primaryColor),
                new KeyValue(sectors[2].fillProperty(), primaryColor),
                new KeyValue(sectors[3].fillProperty(), transparent)
            ),
            new KeyFrame(duration.multiply(0.500),
                new KeyValue(sectors[0].fillProperty(), primaryColor),
                new KeyValue(sectors[1].fillProperty(), primaryColor),
                new KeyValue(sectors[2].fillProperty(), primaryColor),
                new KeyValue(sectors[3].fillProperty(), primaryColor)
            ),
            new KeyFrame(duration.multiply(0.625),
                new KeyValue(sectors[0].fillProperty(), transparent),
                new KeyValue(sectors[1].fillProperty(), primaryColor),
                new KeyValue(sectors[2].fillProperty(), primaryColor),
                new KeyValue(sectors[3].fillProperty(), primaryColor)
            ),
            new KeyFrame(duration.multiply(0.750),
                new KeyValue(sectors[0].fillProperty(), transparent),
                new KeyValue(sectors[1].fillProperty(), transparent),
                new KeyValue(sectors[2].fillProperty(), primaryColor),
                new KeyValue(sectors[3].fillProperty(), primaryColor)
            ),
            new KeyFrame(duration.multiply(0.875),
                new KeyValue(sectors[0].fillProperty(), transparent),
                new KeyValue(sectors[1].fillProperty(), transparent),
                new KeyValue(sectors[2].fillProperty(), transparent),
                new KeyValue(sectors[3].fillProperty(), primaryColor)
            ),
            new KeyFrame(duration,
                new KeyValue(sectors[0].fillProperty(), transparent),
                new KeyValue(sectors[1].fillProperty(), transparent),
                new KeyValue(sectors[2].fillProperty(), transparent),
                new KeyValue(sectors[3].fillProperty(), transparent)
            )
        );

        nextTimeline.setCycleCount(INDEFINITE);
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
            boolean isRunning = activeTimeline.getStatus() == Status.RUNNING;
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
        Paint color = spin.getPrimaryColor();
        for (Rectangle sector : sectors) {
            sector.setFill(color);
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
        updateColors();
    }
}