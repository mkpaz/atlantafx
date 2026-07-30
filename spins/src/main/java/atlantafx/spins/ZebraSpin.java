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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing a horizontal progress bar
 * with animated diagonal stripes moving continuously from left to right.
 */
public class ZebraSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "zebra-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_WIDTH = 100.0;
    public static final double DEFAULT_HEIGHT = 12.0;
    public static final double DEFAULT_CORNER_RADIUS = 4.0;
    public static final double DEFAULT_STRIPE_WIDTH = 6.0;
    public static final double DEFAULT_STRIPE_GAP = 6.0;

    protected Spin spin;
    protected Pane root;
    protected Rectangle backgroundRect;
    protected Path stripesPath;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double width;
    protected final double height;
    protected final double cornerRadius;
    protected final double stripeWidth;
    protected final double stripeGap;
    protected final double patternStep;
    protected final double cyclePeriod;

    /**
     * Constructs a new {@code StripesProgressSpin} with default dimensions.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public ZebraSpin(Spin spin) {
        this(spin, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_CORNER_RADIUS, DEFAULT_STRIPE_WIDTH, DEFAULT_STRIPE_GAP);
    }

    /**
     * Constructs a new {@code StripesProgressSpin} with specified geometry parameters.
     *
     * @param spin         the {@link Spin} control instance using this skin
     * @param width        the width of the progress bar
     * @param height       the height of the progress bar
     * @param cornerRadius the corner radius of the progress bar
     * @param stripeWidth  the width of diagonal stripes
     * @param stripeGap    the width of the gap between stripes
     */
    public ZebraSpin(
        Spin spin,
        double width,
        double height,
        double cornerRadius,
        double stripeWidth,
        double stripeGap
    ) {
        this.spin = spin;
        this.width = width > 0 ? width : DEFAULT_WIDTH;
        this.height = height > 0 ? height : DEFAULT_HEIGHT;
        this.cornerRadius = cornerRadius >= 0 ? cornerRadius : DEFAULT_CORNER_RADIUS;
        this.stripeWidth = stripeWidth > 0 ? stripeWidth : DEFAULT_STRIPE_WIDTH;
        this.stripeGap = stripeGap > 0 ? stripeGap : DEFAULT_STRIPE_GAP;

        // horizontal step along the X axis
        this.patternStep = this.stripeWidth + this.stripeGap;

        // one complete pattern period for linear translation (controls speed)
        this.cyclePeriod = this.patternStep * 2.0;

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
        spin.setSkin(new ZebraSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        root = new Pane();

        backgroundRect = new Rectangle(width, height);
        backgroundRect.setArcWidth(cornerRadius * 2.0);
        backgroundRect.setArcHeight(cornerRadius * 2.0);
        backgroundRect.setStroke(null);
        backgroundRect.setStrokeType(StrokeType.INSIDE);

        var clipRect = new Rectangle(width, height);
        clipRect.setArcWidth(cornerRadius * 2.0);
        clipRect.setArcHeight(cornerRadius * 2.0);
        clipRect.setStrokeType(StrokeType.INSIDE);

        stripesPath = createStripesPath();
        stripesPath.setStroke(null);
        stripesPath.setStrokeType(StrokeType.INSIDE);

        var stripesGroup = new Group(stripesPath);
        stripesGroup.setClip(clipRect);

        root.getChildren().addAll(backgroundRect, stripesGroup);

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

    protected Path createStripesPath() {
        var path = new Path();

        // add extra margin on both sides matching patternStep for seamless animation
        double startX = -height - (patternStep * 2.0);
        double endX = width + height + (patternStep * 2.0);

        for (double x = startX; x < endX; x += patternStep) {
            path.getElements().addAll(
                new MoveTo(x, 0),
                new LineTo(x + stripeWidth, 0),
                new LineTo(x + stripeWidth + height, height),
                new LineTo(x + height, height),
                new ClosePath()
            );
        }

        return path;
    }

    protected Timeline initTimeline() {
        var nextTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(stripesPath.translateXProperty(), 0.0, Interpolator.LINEAR)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(stripesPath.translateXProperty(), cyclePeriod, Interpolator.LINEAR)
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
        return this.width;
    }

    @Override
    public double computeMaxHeight(double width) {
        return this.height;
    }

    protected void updateColors() {
        backgroundRect.setFill(spin.getSecondaryColor());
        stripesPath.setFill(spin.getPrimaryColor());
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

        updateColors();
    }
}