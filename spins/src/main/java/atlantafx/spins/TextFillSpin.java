/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing text filled horizontally with color over time.
 */
public class TextFillSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "text-fill-spin";
    public static final double DEFAULT_DURATION = 3.0;
    public static final double DEFAULT_FONT_SIZE = 24.0;

    protected static final String DEFAULT_TEXT = "Loading";

    protected Spin spin;
    protected Pane root;
    protected Text backText;
    protected Text frontText;
    protected Rectangle clipRect;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double fontSize;
    protected double calculatedWidth;
    protected double calculatedHeight;

    /**
     * Constructs a new {@code TextFillSpin} with default font size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public TextFillSpin(Spin spin) {
        this(spin, DEFAULT_FONT_SIZE);
    }

    /**
     * Constructs a new {@code TextDotsSpin} with specified font size.
     *
     * @param spin     the {@link Spin} control instance using this skin
     * @param fontSize the font size
     */
    public TextFillSpin(Spin spin, double fontSize) {
        this.spin = spin;
        this.fontSize = fontSize > 0 ? fontSize : DEFAULT_FONT_SIZE;

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
        spin.setSkin(new TextFillSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        Font font = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, fontSize);

        backText = new Text();
        backText.setFont(font);
        backText.setTextOrigin(VPos.BASELINE);
        backText.setStrokeType(StrokeType.INSIDE);
        backText.setStrokeWidth(1.0);

        backText.setCache(true);
        backText.setCacheHint(CacheHint.SPEED);

        backText.textProperty().bind(
            spin.textProperty().map(text -> text != null && !text.isBlank() ? text : DEFAULT_TEXT)
        );
        backText.strokeProperty().bind(spin.primaryColorProperty());

        // prevents CSS from overriding transparent color
        backText.fillProperty().bind(Bindings.createObjectBinding(() -> Color.TRANSPARENT));

        frontText = new Text();
        frontText.setFont(font);
        frontText.setTextOrigin(VPos.BASELINE);
        frontText.setStroke(null);
        frontText.textProperty().bind(backText.textProperty());
        frontText.fillProperty().bind(spin.primaryColorProperty());

        frontText.setCache(true);
        frontText.setCacheHint(CacheHint.SPEED);

        clipRect = new Rectangle(0.0, 0.0, 0.0, 0.0);
        clipRect.setSmooth(false); // disable smoothing for speed
        frontText.setClip(clipRect);

        // listen for layout changes (not text changes)
        backText.layoutBoundsProperty().subscribe(bounds -> updateLayout());

        updateLayout();

        root = new Pane(backText, frontText);

        subscription = Subscription.combine(
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
        var nextTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(clipRect.widthProperty(), 0.0, Interpolator.LINEAR)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(clipRect.widthProperty(), calculatedWidth, Interpolator.LINEAR)
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
        backText.textProperty().unbind();
        backText.strokeProperty().unbind();
        backText.fillProperty().unbind();
        frontText.textProperty().unbind();
        frontText.fillProperty().unbind();

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
        return calculatedWidth;
    }

    @Override
    public double computeMaxHeight(double width) {
        return calculatedHeight;
    }

    protected void updateLayout() {
        calculatedWidth = backText.getLayoutBounds().getWidth();
        calculatedHeight = backText.getLayoutBounds().getHeight();

        double startX = 0.0;
        double startY = backText.getBaselineOffset();

        backText.setX(startX);
        backText.setY(startY);
        frontText.setX(startX);
        frontText.setY(startY);

        clipRect.setHeight(calculatedHeight);

        var activeTimeline = timeline.get();
        if (activeTimeline != null) {
            restart();
        } else {
            resetState();
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

    protected void restart() {
        var activeTimeline = timeline.get();
        if (activeTimeline != null) {
            boolean isRunning = activeTimeline.getStatus() == Animation.Status.RUNNING;
            doStop();

            var nextTimeline = initTimeline();
            timeline.set(nextTimeline);

            if (isRunning) {
                nextTimeline.playFromStart();
            }
        }
    }

    protected void resetState() {
        clipRect.setWidth(calculatedWidth);
    }
}