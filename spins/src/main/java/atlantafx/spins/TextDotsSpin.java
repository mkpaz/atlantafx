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
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing text followed by 3 animated dots.
 */
public class TextDotsSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "text-dots-spin";
    public static final double DEFAULT_DURATION = 1.2;
    public static final double DEFAULT_FONT_SIZE = 24.0;

    protected static final double DEFAULT_TEXT_GAP_RATIO = 0.5;
    protected static final String DEFAULT_TEXT = "Loading";

    protected Spin spin;
    protected Pane root;
    protected Text labelText;
    protected final Circle[] dots = new Circle[3];

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double fontSize;
    protected final double textToDotsGap;
    protected double calculatedWidth;
    protected double calculatedHeight;

    /**
     * Constructs a new {@code TextDotsSpin} with default font size and gap.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public TextDotsSpin(Spin spin) {
        this(spin, DEFAULT_FONT_SIZE);
    }

    /**
     * Constructs a new {@code TextDotsSpin} with specified font size and default gap.
     *
     * @param spin     the {@link Spin} control instance using this skin
     * @param fontSize the font size
     */
    public TextDotsSpin(Spin spin, double fontSize) {
        this(spin, fontSize, (fontSize / 8.0 * 1.5) * DEFAULT_TEXT_GAP_RATIO);
    }

    /**
     * Constructs a new {@code DotsTextSpin} with custom font size and gap.
     *
     * @param spin          the {@link Spin} control instance using this skin
     * @param fontSize      the font size
     * @param textToDotsGap the gap between text end and the first dot
     */
    public TextDotsSpin(Spin spin, double fontSize, double textToDotsGap) {
        this.spin = spin;
        this.fontSize = fontSize > 0 ? fontSize : DEFAULT_FONT_SIZE;
        this.textToDotsGap = textToDotsGap >= 0 ? textToDotsGap : 0;

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
        spin.setSkin(new TextDotsSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        Font font = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, fontSize);

        labelText = new Text();
        labelText.setFont(font);
        labelText.setTextOrigin(VPos.BASELINE);
        labelText.textProperty().bind(spin.textProperty());

        double dotRadius = fontSize / 8.0;
        double dotGap = dotRadius * 1.5;

        for (int i = 0; i < dots.length; i++) {
            var dot = new Circle(dotRadius);
            dot.setStrokeType(StrokeType.INSIDE);
            dot.setOpacity(1.0);
            dots[i] = dot;
        }

        var wrapper = new Pane();

        double startX = 0;
        double startY = labelText.getBaselineOffset();

        labelText.textProperty().bind(
            spin.textProperty().map(text -> text != null && !text.isBlank() ? text : DEFAULT_TEXT)
        );
        labelText.textProperty().subscribe(_ -> updateLayout(dotRadius, dotGap, wrapper));
        labelText.fillProperty().bind(spin.primaryColorProperty());

        labelText.setX(startX);
        labelText.setY(startY);
        wrapper.getChildren().add(labelText);

        for (Circle dot : dots) {
            wrapper.getChildren().add(dot);
        }

        updateLayout(dotRadius, dotGap, wrapper);
        updateColors();

        root = new Pane(wrapper);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(_ -> updateColors()),
            spin.secondaryColorProperty().subscribe(_ -> updateColors()),
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

        var nextTimeline = new Timeline();
        nextTimeline.setCycleCount(Timeline.INDEFINITE);

        nextTimeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO,
                new KeyValue(dots[0].opacityProperty(), 0.0, Interpolator.DISCRETE),
                new KeyValue(dots[1].opacityProperty(), 0.0, Interpolator.DISCRETE),
                new KeyValue(dots[2].opacityProperty(), 0.0, Interpolator.DISCRETE)
            ),
            new KeyFrame(duration.multiply(0.25),
                new KeyValue(dots[0].opacityProperty(), 1.0, Interpolator.DISCRETE),
                new KeyValue(dots[1].opacityProperty(), 0.0, Interpolator.DISCRETE),
                new KeyValue(dots[2].opacityProperty(), 0.0, Interpolator.DISCRETE)
            ),
            new KeyFrame(duration.multiply(0.50),
                new KeyValue(dots[0].opacityProperty(), 1.0, Interpolator.DISCRETE),
                new KeyValue(dots[1].opacityProperty(), 1.0, Interpolator.DISCRETE),
                new KeyValue(dots[2].opacityProperty(), 0.0, Interpolator.DISCRETE)
            ),
            new KeyFrame(duration.multiply(0.75),
                new KeyValue(dots[0].opacityProperty(), 1.0, Interpolator.DISCRETE),
                new KeyValue(dots[1].opacityProperty(), 1.0, Interpolator.DISCRETE),
                new KeyValue(dots[2].opacityProperty(), 1.0, Interpolator.DISCRETE)
            ),
            new KeyFrame(duration,
                new KeyValue(dots[0].opacityProperty(), 1.0, Interpolator.DISCRETE),
                new KeyValue(dots[1].opacityProperty(), 1.0, Interpolator.DISCRETE),
                new KeyValue(dots[2].opacityProperty(), 1.0, Interpolator.DISCRETE)
            )
        );

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

    protected void updateLayout(double dotRadius, double dotGap, Pane parent) {
        double textWidth = labelText.getLayoutBounds().getWidth();
        double textHeight = labelText.getLayoutBounds().getHeight();
        double totalDotsWidth = (dots.length * dotRadius * 2) + ((dots.length - 1) * dotGap);

        calculatedWidth = textWidth + textToDotsGap + totalDotsWidth;
        calculatedHeight = textHeight;

        parent.setPrefSize(calculatedWidth, calculatedHeight);
        parent.setMinSize(calculatedWidth, calculatedHeight);
        parent.setMaxSize(calculatedWidth, calculatedHeight);

        double startX = 0;
        double startY = labelText.getBaselineOffset();
        double dotsStartX = startX + textWidth + textToDotsGap + dotRadius;
        double dotsY = startY - dotRadius * 1.2;

        for (int i = 0; i < dots.length; i++) {
            dots[i].setCenterX(dotsStartX + i * (dotRadius * 2 + dotGap));
            dots[i].setCenterY(dotsY);
        }
    }

    protected void updateColors() {
        for (Circle dot : dots) {
            dot.setFill(spin.getSecondaryColor());
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
        for (Circle dot : dots) {
            dot.setOpacity(1.0);
        }
    }
}