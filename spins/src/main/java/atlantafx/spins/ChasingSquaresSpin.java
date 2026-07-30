/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
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

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a 2x2 grid loading indicator.
 *
 * <p>Animates four small squares that sequentially unfold from a stack into a 2x2 grid
 */
public class ChasingSquaresSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "chasing-squares-spin";
    public static final double DEFAULT_DURATION = 4.0;
    public static final double DEFAULT_SIZE = 24.0;

    // geometric proportions
    protected static final double SQUARE_SIZE_RATIO = 3.0 / 7.0;
    protected static final double GAP_RATIO = 1.0 / 7.0;

    protected static final int SQUARE_COUNT = 4;

    // state transition matrix defining corner index for each of the 4 squares across 13 animation steps:
    // 0: top-left, 1: top-right, 2: bottom-right, 3: bottom-left
    protected static final int[][] GRID_STATES = {
        {0, 0, 0, 0}, // 0.0%    (all stacked at top-left)
        {0, 1, 1, 1}, // 8.33%   (three move to top-right)
        {0, 1, 2, 2}, // 16.66%  (two down to bottom-right)
        {0, 1, 2, 3}, // 24.99%  (one to bottom-left - fully unfolded grid)
        {0, 1, 2, 0}, // 33.32%  (starts folding: bottom-left moves to top-left)
        {1, 1, 2, 1}, // 41.65%
        {2, 2, 2, 2}, // 49.98%  (all stacked at bottom-right)
        {3, 3, 2, 3}, // 58.31%  (unfolding sequence begins again)
        {0, 0, 2, 3}, // 66.64%
        {0, 1, 2, 3}, // 74.97%  (fully unfolded grid)
        {0, 2, 2, 3}, // 83.30%
        {0, 3, 3, 3}, // 91.63%
        {0, 0, 0, 0}  // 100.0%  (returns to initial top-left stack)
    };

    // normalized time offsets (0.0 to 1.0) corresponding to each step in GRID_STATES
    protected static final double[] TIME_FRACTIONS = {
        0.0, 0.0833, 0.1666, 0.2499, 0.3332, 0.4165, 0.4998, 0.5831, 0.6664, 0.7497, 0.8330, 0.9163, 1.0
    };

    protected Spin spin;
    protected Pane root;
    protected Rectangle[] squares = new Rectangle[SQUARE_COUNT];

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;

    /**
     * Constructs a new {@code ChasingSquaresSpin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public ChasingSquaresSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code ChasingSquaresSpin} with specified size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the control
     */
    public ChasingSquaresSpin(Spin spin, double size) {
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
        spin.setSkin(new ChasingSquaresSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        root = new Pane();

        double squareSize = size * SQUARE_SIZE_RATIO;

        for (int i = 0; i < SQUARE_COUNT; i++) {
            var square = new Rectangle(squareSize, squareSize);
            square.setStrokeType(StrokeType.INSIDE);
            square.setStroke(null);

            squares[i] = square;
            root.getChildren().add(square);
        }

        // set initial positions so all 4 squares are visible in a 2x2 grid when stopped
        resetState();
        updateColors();

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

    protected Timeline initTimeline() {
        updateColors();

        double gap = size * GAP_RATIO;
        double squareSize = size * SQUARE_SIZE_RATIO;

        // X and Y offsets for the 4 grid corners
        double[] xPositions = {0.0, squareSize + gap, squareSize + gap, 0.0};
        double[] yPositions = {0.0, 0.0, squareSize + gap, squareSize + gap};

        var nextTimeline = new Timeline();

        for (int step = 0; step < GRID_STATES.length; step++) {
            Duration frameTime = spin.getDuration().multiply(TIME_FRACTIONS[step]);
            int[] currentCornerIndices = GRID_STATES[step];

            nextTimeline.getKeyFrames().add(new KeyFrame(frameTime,
                new KeyValue(squares[0].xProperty(), xPositions[currentCornerIndices[0]]),
                new KeyValue(squares[0].yProperty(), yPositions[currentCornerIndices[0]]),

                new KeyValue(squares[1].xProperty(), xPositions[currentCornerIndices[1]]),
                new KeyValue(squares[1].yProperty(), yPositions[currentCornerIndices[1]]),

                new KeyValue(squares[2].xProperty(), xPositions[currentCornerIndices[2]]),
                new KeyValue(squares[2].yProperty(), yPositions[currentCornerIndices[2]]),

                new KeyValue(squares[3].xProperty(), xPositions[currentCornerIndices[3]]),
                new KeyValue(squares[3].yProperty(), yPositions[currentCornerIndices[3]])
            ));
        }

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

    protected void updateColors() {
        Paint primaryColor = spin.getPrimaryColor();
        for (var square : squares) {
            square.setFill(primaryColor);
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
            activeTimeline.stop();
        }

        resetState();
        updateColors();
    }

    protected void resetState() {
        double gap = size * GAP_RATIO;
        double squareSize = size * SQUARE_SIZE_RATIO;

        double[] xPositions = {0.0, squareSize + gap, squareSize + gap, 0.0};
        double[] yPositions = {0.0, 0.0, squareSize + gap, squareSize + gap};

        for (int i = 0; i < SQUARE_COUNT; i++) {
            squares[i].setX(xPositions[i]);
            squares[i].setY(yPositions[i]);
        }
    }
}