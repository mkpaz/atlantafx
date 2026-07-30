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
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a 4-square horizontal filling indicator.
 *
 * <p>Sequentially changes the fill color of four horizontally aligned squares from primary to secondary color.
 */
public class FourSquaresSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "four-squares-spin";
    public static final double DEFAULT_DURATION = 1.6;
    public static final double DEFAULT_SQUARE_SIZE = 10.0;
    public static final double DEFAULT_GAP = 8.0;

    protected static final int SQUARE_COUNT = 4;

    protected Spin spin;
    protected Pane root;
    protected Rectangle[] squares;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double squareSize;
    protected final double gap;

    /**
     * Constructs a new {@code FourSquaresSpin} with default square size and gap.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public FourSquaresSpin(Spin spin) {
        this(spin, DEFAULT_SQUARE_SIZE, DEFAULT_GAP);
    }

    /**
     * Constructs a new {@code FourSquaresSpin} with specified square size and gap.
     *
     * @param spin       the {@link Spin} control instance using this skin
     * @param squareSize the size of each square
     * @param gap        the horizontal spacing between adjacent squares
     */
    public FourSquaresSpin(Spin spin, double squareSize, double gap) {
        this.spin = spin;
        this.squareSize = squareSize > 0 ? squareSize : DEFAULT_SQUARE_SIZE;
        this.gap = gap >= 0 ? gap : DEFAULT_GAP;

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
        spin.setSkin(new FourSquaresSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        squares = new Rectangle[SQUARE_COUNT];

        for (int i = 0; i < SQUARE_COUNT; i++) {
            double xPosition = i * (squareSize + gap);
            double yPosition = 0.0;

            var rect = new Rectangle(xPosition, yPosition, squareSize, squareSize);
            rect.setFill(spin.getPrimaryColor());
            squares[i] = rect;
        }

        root = new Pane(squares);

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

        // reset base color prior to timeline creation
        for (Rectangle rect : squares) {
            rect.setFill(primaryColor);
        }

        var nextTimeline = new Timeline(
            // 1st square switches to secondary color
            new KeyFrame(
                spin.getDurationPercentage(20.0),
                new KeyValue(squares[0].fillProperty(), secondaryColor, Interpolator.DISCRETE)
            ),
            // 2nd square switches to secondary color
            new KeyFrame(
                spin.getDurationPercentage(40.0),
                new KeyValue(squares[1].fillProperty(), secondaryColor, Interpolator.DISCRETE)
            ),
            // 3rd square switches to secondary color
            new KeyFrame(
                spin.getDurationPercentage(60.0),
                new KeyValue(squares[2].fillProperty(), secondaryColor, Interpolator.DISCRETE)
            ),
            // 4th square switches to secondary color
            new KeyFrame(
                spin.getDurationPercentage(80.0),
                new KeyValue(squares[3].fillProperty(), secondaryColor, Interpolator.DISCRETE)
            ),
            // reset all squares back to primary color for next cycle iteration
            new KeyFrame(
                spin.getDuration(),
                new KeyValue(squares[0].fillProperty(), primaryColor, Interpolator.DISCRETE),
                new KeyValue(squares[1].fillProperty(), primaryColor, Interpolator.DISCRETE),
                new KeyValue(squares[2].fillProperty(), primaryColor, Interpolator.DISCRETE),
                new KeyValue(squares[3].fillProperty(), primaryColor, Interpolator.DISCRETE)
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
    public double computeMaxWidth(double width) {
        return (squareSize * SQUARE_COUNT) + (gap * (SQUARE_COUNT - 1));
    }

    @Override
    public double computeMaxHeight(double height) {
        return squareSize;
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
        for (Rectangle rect : squares) {
            rect.setFill(spin.getPrimaryColor());
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
        resetState();
    }

    protected void resetState() {
        Paint primaryColor = spin.getPrimaryColor();
        for (Rectangle rect : squares) {
            rect.setFill(primaryColor);
        }
    }
}