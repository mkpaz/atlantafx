package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.shape.FillRule;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a square spinner where semicircular
 * cutouts appear sequentially on each side to create a puzzle-like shape animation.
 */
public class PuzzleSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "puzzle-spin";
    public static final double DEFAULT_DURATION = 2.8;
    public static final double DEFAULT_SIZE = 28.0;
    public static final double DEFAULT_CORNER_RADIUS = 6.0;

    protected static final double CUTOUT_RADIUS_RATIO = 8.0 / 48.0;
    protected static final double EPSILON = 0.01;

    protected Spin spin;
    protected Pane root;
    protected Canvas canvas;
    protected GraphicsContext gc;

    protected final DoubleProperty topRadius = new SimpleDoubleProperty(0.0);
    protected final DoubleProperty rightRadius = new SimpleDoubleProperty(0.0);
    protected final DoubleProperty bottomRadius = new SimpleDoubleProperty(0.0);
    protected final DoubleProperty leftRadius = new SimpleDoubleProperty(0.0);

    // cache to prevent redundant redraws
    protected double lastTop = -1;
    protected double lastRight = -1;
    protected double lastBottom = -1;
    protected double lastLeft = -1;

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;
    protected final double cornerRadius;

    /**
     * Constructs a new {@code PuzzleCutoutSpin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public PuzzleSpin(Spin spin) {
        this(spin, DEFAULT_SIZE, DEFAULT_CORNER_RADIUS);
    }

    /**
     * Constructs a new {@code PuzzleCutoutSpin} with specified size.
     *
     * @param spin         the {@link Spin} control instance using this skin
     * @param size         the preferred size of the control
     * @param cornerRadius the corner radius
     */
    public PuzzleSpin(Spin spin, double size, double cornerRadius) {
        this.spin = spin;
        this.size = size > 0 ? size : DEFAULT_SIZE;
        this.cornerRadius = cornerRadius > 0 ? cornerRadius : DEFAULT_CORNER_RADIUS;

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
        spin.setSkin(new PuzzleSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        canvas = new Canvas(size, size);
        gc = canvas.getGraphicsContext2D();
        root = new Pane(canvas);

        Runnable redraw = () -> {
            double top = topRadius.get();
            double right = rightRadius.get();
            double bottom = bottomRadius.get();
            double left = leftRadius.get();

            if (Math.abs(top - lastTop) > EPSILON
                || Math.abs(right - lastRight) > EPSILON
                || Math.abs(bottom - lastBottom) > EPSILON
                || Math.abs(left - lastLeft) > EPSILON
            ) {

                lastTop = top;
                lastRight = right;
                lastBottom = bottom;
                lastLeft = left;

                draw();
            }
        };

        topRadius.addListener((_, _, _) -> redraw.run());
        rightRadius.addListener((_, _, _) -> redraw.run());
        bottomRadius.addListener((_, _, _) -> redraw.run());
        leftRadius.addListener((_, _, _) -> redraw.run());

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(_ -> draw()),
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

        draw();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    protected void draw() {
        // clear previous frame
        gc.clearRect(0, 0, size, size);

        gc.save();

        // clip the canvas
        gc.beginPath();
        roundRect(gc, 0, 0, size, size, cornerRadius);
        gc.clip();

        // subtract the circles from the square
        gc.setFillRule(FillRule.EVEN_ODD);
        gc.beginPath();

        // the base shape
        roundRect(gc, 0, 0, size, size, cornerRadius);

        double center = size / 2.0;
        double top = topRadius.get();
        double right = rightRadius.get();
        double bottom = bottomRadius.get();
        double left = leftRadius.get();

        // top cutout
        if (top > EPSILON) {
            gc.moveTo(center + top, 0);
            gc.arc(center, 0, top, top, 0, 360);
        }
        // right cutout
        if (right > EPSILON) {
            gc.moveTo(size + right, center);
            gc.arc(size, center, right, right, 0, 360);
        }
        // bottom cutout
        if (bottom > EPSILON) {
            gc.moveTo(center + bottom, size);
            gc.arc(center, size, bottom, bottom, 0, 360);
        }
        // left cutout
        if (left > EPSILON) {
            gc.moveTo(left, center);
            gc.arc(0, center, left, left, 0, 360);
        }

        // fill the resulting composite path
        gc.setFill(spin.getPrimaryColor());
        gc.fill();

        gc.restore();
    }

    protected Timeline initTimeline() {
        resetState();

        double maxRadius = size * CUTOUT_RADIUS_RATIO;

        var nextTimeline = new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(topRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(rightRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(bottomRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(leftRadius, 0.0, Interpolator.LINEAR)
            ),
            new KeyFrame(
                spin.getDurationPercentage(12.5),
                new KeyValue(topRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(rightRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(bottomRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(leftRadius, 0.0, Interpolator.LINEAR)
            ),
            new KeyFrame(
                spin.getDurationPercentage(25.0),
                new KeyValue(topRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(rightRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(bottomRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(leftRadius, 0.0, Interpolator.LINEAR)
            ),
            new KeyFrame(
                spin.getDurationPercentage(37.5),
                new KeyValue(topRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(rightRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(bottomRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(leftRadius, 0.0, Interpolator.LINEAR)
            ),
            new KeyFrame(
                spin.getDurationPercentage(45.0),
                new KeyValue(topRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(rightRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(bottomRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(leftRadius, maxRadius, Interpolator.LINEAR)
            ),
            new KeyFrame(
                spin.getDurationPercentage(55.0),
                new KeyValue(topRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(rightRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(bottomRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(leftRadius, maxRadius, Interpolator.LINEAR)
            ),
            new KeyFrame(
                spin.getDurationPercentage(62.5),
                new KeyValue(topRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(rightRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(bottomRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(leftRadius, maxRadius, Interpolator.LINEAR)
            ),
            new KeyFrame(
                spin.getDurationPercentage(75.0),
                new KeyValue(topRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(rightRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(bottomRadius, maxRadius, Interpolator.LINEAR),
                new KeyValue(leftRadius, maxRadius, Interpolator.LINEAR)
            ),
            new KeyFrame(
                spin.getDurationPercentage(87.5),
                new KeyValue(topRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(rightRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(bottomRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(leftRadius, maxRadius, Interpolator.LINEAR)
            ),
            new KeyFrame(
                spin.getDuration(),
                new KeyValue(topRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(rightRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(bottomRadius, 0.0, Interpolator.LINEAR),
                new KeyValue(leftRadius, 0.0, Interpolator.LINEAR)
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

        topRadius.unbind();
        rightRadius.unbind();
        bottomRadius.unbind();
        leftRadius.unbind();

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

    private void roundRect(GraphicsContext gc, double x, double y, double w, double h, double r) {
        gc.moveTo(x + r, y);
        gc.arcTo(x + w, y, x + w, y + h, r);
        gc.arcTo(x + w, y + h, x, y + h, r);
        gc.arcTo(x, y + h, x, y, r);
        gc.arcTo(x, y, x + w, y, r);
        gc.closePath();
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
        topRadius.set(0.0);
        rightRadius.set(0.0);
        bottomRadius.set(0.0);
        leftRadius.set(0.0);
        lastTop = -1;
        lastRight = -1;
        lastBottom = -1;
        lastLeft = -1;
        draw();
    }
}