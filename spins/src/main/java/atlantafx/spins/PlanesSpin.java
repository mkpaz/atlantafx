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
import javafx.scene.Group;
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

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing 3D layered planes expanding in space.
 */
public class PlanesSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "planes-spin";
    public static final double DEFAULT_DURATION = 1.2;
    public static final double DEFAULT_SIZE = 28.0;

    // geometric proportions relative to a base size of 48px
    protected static final double TOP_LAYER_OFFSET_RATIO = -25.0 / 48.0;
    protected static final double BOTTOM_LAYER_OFFSET_RATIO = 20.0 / 48.0;
    protected static final double BOTTOM_LAYER_SCALE = 40.0 / 48.0;

    protected static final double ROTATE_X_ANGLE = 65.0;
    protected static final double ROTATE_Z_ANGLE = 45.0;

    protected Spin spin;
    protected Pane root;
    protected Rectangle bottomLayer;
    protected Rectangle middleLayer;
    protected Rectangle topLayer;

    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;
    protected Subscription subscription = Subscription.EMPTY;

    protected final double size;

    /**
     * Constructs a new {@code PlanesSpin} with default size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public PlanesSpin(Spin spin) {
        this(spin, DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code PlanesSpin} with specified size.
     *
     * @param spin the {@link Spin} control instance using this skin
     * @param size the preferred size of the control
     */
    public PlanesSpin(Spin spin, double size) {
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
        spin.setSkin(new PlanesSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        root = new Pane();

        topLayer = createLayer(size, spin.getPrimaryColor(), 1.0);
        middleLayer = createLayer(size, spin.getSecondaryColor(), 1.0);
        bottomLayer = createLayer(size, spin.getTertiaryColor(), 1.0);

        double canvasWidth = getCanvasWidth();
        double canvasHeight = getCanvasHeight();
        double topLayerMaxOffset = computeProjectionY(Math.abs(size * TOP_LAYER_OFFSET_RATIO));
        double bottomLayerMaxOffset = computeProjectionY(Math.abs(size * BOTTOM_LAYER_OFFSET_RATIO));

        double offsetY = (topLayerMaxOffset - bottomLayerMaxOffset) / 2.0;
        double centerX = canvasWidth / 2.0;
        double centerY = (canvasHeight / 2.0) + offsetY;

        // z-order: bottom, middle, top
        var planesGroup = new Group(bottomLayer, middleLayer, topLayer);
        planesGroup.setLayoutX(centerX);
        planesGroup.setLayoutY(centerY);

        var rotateX = new Rotate(ROTATE_X_ANGLE, Rotate.X_AXIS);
        var rotateZ = new Rotate(ROTATE_Z_ANGLE, Rotate.Z_AXIS);
        planesGroup.getTransforms().addAll(rotateX, rotateZ);

        root.getChildren().add(planesGroup);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(paint -> updateColors()),
            spin.secondaryColorProperty().subscribe(paint -> updateColors()),
            spin.tertiaryColorProperty().subscribe(paint -> updateColors()),
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

    protected Rectangle createLayer(double size, Paint fill, double opacity) {
        var rect = new Rectangle(size, size);
        rect.setX(-size / 2.0);
        rect.setY(-size / 2.0);
        rect.setFill(fill);
        rect.setOpacity(opacity);
        rect.setStroke(null);
        rect.setStrokeType(StrokeType.INSIDE);

        rect.setCache(true);
        rect.setCacheHint(CacheHint.SPEED);

        return rect;
    }

    protected Timeline initTimeline() {
        updateColors();

        double topTargetOffset = size * TOP_LAYER_OFFSET_RATIO;
        double bottomTargetOffset = size * BOTTOM_LAYER_OFFSET_RATIO;

        var nextTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(topLayer.translateXProperty(), 0.0),
                new KeyValue(topLayer.translateYProperty(), 0.0),
                new KeyValue(bottomLayer.translateXProperty(), 0.0),
                new KeyValue(bottomLayer.translateYProperty(), 0.0),
                new KeyValue(bottomLayer.scaleXProperty(), 1.0),
                new KeyValue(bottomLayer.scaleYProperty(), 1.0)
            ),
            new KeyFrame(spin.getDurationPercentage(90.0),
                new KeyValue(bottomLayer.translateXProperty(), bottomTargetOffset),
                new KeyValue(bottomLayer.translateYProperty(), bottomTargetOffset),
                new KeyValue(bottomLayer.scaleXProperty(), BOTTOM_LAYER_SCALE),
                new KeyValue(bottomLayer.scaleYProperty(), BOTTOM_LAYER_SCALE)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(topLayer.translateXProperty(), topTargetOffset),
                new KeyValue(topLayer.translateYProperty(), topTargetOffset),
                new KeyValue(bottomLayer.translateXProperty(), bottomTargetOffset),
                new KeyValue(bottomLayer.translateYProperty(), bottomTargetOffset),
                new KeyValue(bottomLayer.scaleXProperty(), BOTTOM_LAYER_SCALE),
                new KeyValue(bottomLayer.scaleYProperty(), BOTTOM_LAYER_SCALE)
            )
        );

        nextTimeline.setCycleCount(Timeline.INDEFINITE);
        nextTimeline.setAutoReverse(true);

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
        return getCanvasWidth();
    }

    @Override
    public double computeMaxHeight(double width) {
        return getCanvasHeight();
    }

    protected double getCanvasWidth() {
        return size * Math.sqrt(2);
    }

    protected double getCanvasHeight() {
        double layerHeight = computeProjectionY(size);
        double topLayerMaxOffset = computeProjectionY(Math.abs(size * TOP_LAYER_OFFSET_RATIO));
        double bottomLayerMaxOffset = computeProjectionY(Math.abs(size * BOTTOM_LAYER_OFFSET_RATIO));
        return layerHeight + topLayerMaxOffset + bottomLayerMaxOffset;
    }

    protected double computeProjectionY(double layerSize) {
        return layerSize * Math.sqrt(2) * Math.cos(Math.toRadians(ROTATE_X_ANGLE));
    }

    protected void updateColors() {
        topLayer.setFill(spin.getPrimaryColor());
        middleLayer.setFill(spin.getSecondaryColor());
        bottomLayer.setFill(spin.getTertiaryColor());
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

        bottomLayer.setTranslateX(0.0);
        bottomLayer.setTranslateY(0.0);
        bottomLayer.setScaleX(1.0);
        bottomLayer.setScaleY(1.0);

        topLayer.setTranslateX(0.0);
        topLayer.setTranslateY(0.0);

        updateColors();
    }
}