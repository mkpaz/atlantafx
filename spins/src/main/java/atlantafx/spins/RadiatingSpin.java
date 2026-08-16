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
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a classic radiating stripes spinner.
 */
public class RadiatingSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "radiating-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_SIZE = 32.0;

    protected static final int TICK_COUNT = 12;
    protected static final double TICK_WIDTH_RATIO = 0.08;
    protected static final double TICK_HEIGHT_RATIO = 0.30;
    protected static final double MAX_OPACITY = 1.0;
    protected static final double MIN_OPACITY = 0.0;

    protected Spin spin;
    protected Pane root;
    protected Rectangle[] ticks;

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;

    /**
     * Constructs a new {@code RadiatingStripesSpin} with the default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public RadiatingSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code RadiatingStripesSpin} with a specified bounding size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the control
     */
    public RadiatingSpin(Spin spin, double size) {
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
        spin.setSkin(new RadiatingSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        ticks = new Rectangle[TICK_COUNT];

        double tickWidth = size * TICK_WIDTH_RATIO;
        double tickHeight = size * TICK_HEIGHT_RATIO;
        double centerX = size / 2.0;
        double centerY = size / 2.0;

        for (int i = 0; i < TICK_COUNT; i++) {
            var tick = new Rectangle(tickWidth, tickHeight);
            // gives the stripes rounded caps similar to a rounded stroke
            tick.setArcWidth(tickWidth);
            tick.setArcHeight(tickWidth);
            tick.setFill(spin.getPrimaryColor());
            tick.setStrokeType(StrokeType.INSIDE);

            // top center
            tick.setX(centerX - (tickWidth / 2.0));
            tick.setY(0.0);

            // rotate radially around the center of the canvas
            Rotate rotate = new Rotate(i * (360.0 / TICK_COUNT), centerX, centerY);
            tick.getTransforms().add(rotate);

            ticks[i] = tick;
        }

        root = new Pane(ticks);

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

        resetState();
    }

    protected Timeline initTimeline() {
        var nextTimeline = new Timeline();
        double durationMillis = spin.getDuration().toMillis();
        double stepDuration = durationMillis / TICK_COUNT;

        // create discrete keyframes for 12 steps
        for (int step = 0; step <= TICK_COUNT; step++) {
            int effectiveStep = step % TICK_COUNT;
            List<KeyValue> keyValues = new ArrayList<>();

            for (int i = 0; i < TICK_COUNT; i++) {
                // calculate how far this tick is from the currently "brightest" tick
                int distance = (i - effectiveStep + TICK_COUNT) % TICK_COUNT;
                double opacity = MAX_OPACITY - (distance * ((MAX_OPACITY - MIN_OPACITY) / TICK_COUNT));

                keyValues.add(new KeyValue(ticks[i].opacityProperty(), opacity, Interpolator.DISCRETE));
            }

            Duration time = (step == TICK_COUNT)
                ? spin.getDuration()
                : Duration.millis(stepDuration * step);
            nextTimeline.getKeyFrames().add(new KeyFrame(time, keyValues.toArray(new KeyValue[0])));
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
        for (Rectangle tick : ticks) {
            tick.setFill(primaryColor);
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
        updateColors();
        for (int i = 0; i < TICK_COUNT; i++) {
            ticks[i].setOpacity(1.0);
        }
    }
}