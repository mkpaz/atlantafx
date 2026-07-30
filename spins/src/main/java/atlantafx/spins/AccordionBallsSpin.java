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
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing three horizontal balls
 * (a center ball and two outer balls), where the outer balls move symmetrically.
 */
public class AccordionBallsSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "accordion-balls-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_RADIUS = 6.0;

    protected static final double RADIUS_WIDTH_MULTIPLIER = 10.0;

    protected Spin spin;
    protected Pane root;
    protected Circle centerCircle;
    protected Circle leftCircle;
    protected Circle rightCircle;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double radius;

    /**
     * Constructs a new {@code AccordionBallsSpin} with default radius.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public AccordionBallsSpin(Spin spin) {
        this(spin, DEFAULT_RADIUS);
    }

    /**
     * Constructs a new {@code AccordionBallsSpin} with specified radius.
     *
     * @param spin   the {@link Spin} control instance using this skin
     * @param radius the radius of each ball
     */
    public AccordionBallsSpin(Spin spin, double radius) {
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
        spin.setSkin(new AccordionBallsSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        // container dimensions based on maximum expected animation range
        double width = radius * RADIUS_WIDTH_MULTIPLIER;
        double height = radius * 2.0;

        double centerX = width / 2.0;
        double centerY = height / 2.0;

        Paint primaryColor = spin.getPrimaryColor();
        Paint secondaryColor = spin.getSecondaryColor();

        leftCircle = createCircle(centerX - radius * 2.0, centerY, radius, primaryColor);
        centerCircle = createCircle(centerX, centerY, radius, secondaryColor);
        rightCircle = createCircle(centerX + radius * 2.0, centerY, radius, primaryColor);

        root = new Pane(leftCircle, centerCircle, rightCircle);

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

        updateColors();
    }

    protected Timeline initTimeline() {
        updateColors();

        double moveDistance = radius * 2.0;

        var nextTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(leftCircle.translateXProperty(), 0.0),
                new KeyValue(rightCircle.translateXProperty(), 0.0)
            ),
            new KeyFrame(spin.getDurationPercentage(50.0),
                new KeyValue(leftCircle.translateXProperty(), -moveDistance),
                new KeyValue(rightCircle.translateXProperty(), moveDistance)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(leftCircle.translateXProperty(), 0.0),
                new KeyValue(rightCircle.translateXProperty(), 0.0)
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
        return radius * RADIUS_WIDTH_MULTIPLIER;
    }

    @Override
    public double computeMaxHeight(double width) {
        return radius * 2.0;
    }

    protected Circle createCircle(double centerX, double centerY, double radius, Paint fill) {
        var circle = new Circle(centerX, centerY, radius);
        circle.setFill(fill);
        circle.setStroke(fill);
        circle.setStrokeType(StrokeType.INSIDE);
        circle.setStrokeWidth(0);

        circle.setCache(true);
        circle.setCacheHint(CacheHint.SPEED);

        return circle;
    }

    protected void updateColors() {
        Paint primaryColor = spin.getPrimaryColor();
        Paint secondaryColor = spin.getSecondaryColor();

        leftCircle.setFill(primaryColor);
        leftCircle.setStroke(primaryColor);

        centerCircle.setFill(secondaryColor);
        centerCircle.setStroke(secondaryColor);

        rightCircle.setFill(primaryColor);
        rightCircle.setStroke(primaryColor);
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

        leftCircle.setTranslateX(0.0);
        rightCircle.setTranslateX(0.0);

        updateColors();
    }
}