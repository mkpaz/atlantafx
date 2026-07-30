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
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static javafx.animation.Animation.Status;

/**
 * Skin implementation for {@link Spin} representing a text label with an animated progress line underneath.
 */
public class TextProgressSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "text-progress-spin";
    public static final double DEFAULT_DURATION = 2.0;
    public static final double DEFAULT_FONT_SIZE = 24.0;

    protected static final double LINE_HEIGHT_RATIO = 1.0 / 12.0;
    protected static final double LINE_GAP_RATIO = 1.0 / 24.0;
    protected static final String DEFAULT_TEXT = "Loading";

    protected Spin spin;
    protected Pane root;
    protected Text labelText;
    protected Rectangle progressBar;
    protected Pane wrapper;

    protected Subscription subscription = Subscription.EMPTY;
    protected ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double fontSize;
    protected final double lineHeight;
    protected final double lineGap;
    protected double calculatedWidth;
    protected double calculatedHeight;

    /**
     * Constructs a new {@code TextProgressSpin} with default font size.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public TextProgressSpin(Spin spin) {
        this(spin, DEFAULT_FONT_SIZE);
    }

    /**
     * Constructs a new {@code TextProgressSpin} with specified font size.
     *
     * @param spin     the {@link Spin} control instance using this skin
     * @param fontSize the font size
     */
    public TextProgressSpin(Spin spin, double fontSize) {
        this.spin = spin;
        this.fontSize = fontSize > 0 ? fontSize : DEFAULT_FONT_SIZE;
        this.lineHeight = Math.max(1.0, this.fontSize * LINE_HEIGHT_RATIO);
        this.lineGap = Math.max(1.0, this.fontSize * LINE_GAP_RATIO);

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
        spin.setSkin(new TextProgressSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        Font font = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, fontSize);

        labelText = new Text();
        labelText.setFont(font);
        labelText.setTextOrigin(VPos.TOP);
        labelText.setStrokeType(StrokeType.INSIDE);

        labelText.textProperty().bind(
            spin.textProperty().map(text -> (text != null && !text.isBlank()) ? text : DEFAULT_TEXT)
        );

        progressBar = new Rectangle(0.0, lineHeight);
        progressBar.setStrokeType(StrokeType.INSIDE);
        progressBar.setStroke(null);

        wrapper = new Pane(labelText, progressBar);

        labelText.textProperty().subscribe(text -> updateLayout());
        labelText.fillProperty().bind(spin.primaryColorProperty());

        updateLayout();
        updateColors();

        root = new Pane(wrapper);

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
        updateColors();

        var nextTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(progressBar.widthProperty(), 0.0, Interpolator.LINEAR)
            ),
            new KeyFrame(spin.getDuration(),
                new KeyValue(progressBar.widthProperty(), calculatedWidth, Interpolator.LINEAR)
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

        labelText.textProperty().unbind();
        labelText.fillProperty().unbind();
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
        return calculatedWidth;
    }

    @Override
    public double computeMaxHeight(double width) {
        return calculatedHeight;
    }

    protected void updateLayout() {
        double textWidth = labelText.getLayoutBounds().getWidth();
        double textHeight = labelText.getLayoutBounds().getHeight();

        labelText.setX(0.0);
        labelText.setY(0.0);

        progressBar.setX(0.0);
        progressBar.setY(textHeight + lineGap);

        calculatedWidth = textWidth;
        calculatedHeight = textHeight + lineGap + lineHeight;

        wrapper.setPrefSize(calculatedWidth, calculatedHeight);
        wrapper.setMinSize(calculatedWidth, calculatedHeight);
        wrapper.setMaxSize(calculatedWidth, calculatedHeight);

        restart();
    }

    protected void updateColors() {
        progressBar.setFill(spin.getSecondaryColor());
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

        progressBar.setWidth(0.0);
        updateColors();
    }

    protected void restart() {
        var activeTimeline = timeline.get();
        if (activeTimeline != null) {
            boolean isRunning = activeTimeline.getStatus() == Status.RUNNING;
            doStop();

            var nextTimeline = initTimeline();
            timeline.set(nextTimeline);

            if (isRunning) {
                nextTimeline.playFromStart();
            }
        }
    }
}