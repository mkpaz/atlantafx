/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * The default skin for the {@link RingProgressIndicator} control.
 */
public class RingProgressIndicatorSkin extends SkinBase<RingProgressIndicator> {

    protected static final double DEFAULT_ANIMATION_TIME = 3;

    protected final StackPane container = new StackPane();
    protected final Circle trackCircle = new Circle();
    protected final Arc progressArc = new Arc();
    protected final Label progressLabel = new Label();
    protected final RotateTransition transition = new RotateTransition(
        Duration.seconds(DEFAULT_ANIMATION_TIME), progressArc
    );

    public RingProgressIndicatorSkin(RingProgressIndicator indicator) {
        super(indicator);

        trackCircle.getStyleClass().add("track");
        trackCircle.setManaged(false);
        trackCircle.setFill(Color.TRANSPARENT);

        progressArc.getStyleClass().add("ring");
        progressArc.setManaged(false);
        progressArc.setStartAngle(90);
        progressArc.setLength(calcProgressArcLength());
        progressArc.setCache(true);
        progressArc.setCacheHint(CacheHint.ROTATE);
        progressArc.setFill(Color.TRANSPARENT);

        transition.setAutoReverse(false);
        transition.setByAngle(-getMaxAngle());
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setDelay(Duration.ZERO);
        transition.setInterpolator(Interpolator.LINEAR);

        progressLabel.getStyleClass().add("progress");

        container.getStyleClass().addAll("container");
        container.setMaxHeight(Region.USE_PREF_SIZE);
        container.setMaxWidth(Region.USE_PREF_SIZE);
        container.getChildren().addAll(trackCircle, progressArc);
        container.getChildren().add(indicator.getGraphic() != null ? indicator.getGraphic() : progressLabel);

        indicator.getStyleClass().add("ring-progress-indicator");
        indicator.setMaxHeight(Region.USE_PREF_SIZE);
        indicator.setMaxWidth(Region.USE_PREF_SIZE);
        getChildren().add(container);

        // == INIT LISTENERS ==

        updateProgressLabel();
        toggleIndeterminate();

        registerChangeListener(indicator.progressProperty(), e -> {
            updateProgressLabel();
            progressArc.setLength(calcProgressArcLength());
        });

        registerChangeListener(indicator.indeterminateProperty(), e -> toggleIndeterminate());

        registerChangeListener(indicator.visibleProperty(), e -> {
            if (indicator.isVisible() && indicator.isIndeterminate()) {
                transition.play();
            } else {
                transition.pause();
            }
        });

        registerChangeListener(indeterminateAnimationTimeProperty(), e -> {
            transition.setDuration(Duration.seconds(getIndeterminateAnimationTime()));
            if (indicator.isIndeterminate()) {
                transition.playFromStart();
            }
        });

        registerChangeListener(indicator.graphicProperty(), e -> {
            if (indicator.getGraphic() != null) {
                container.getChildren().remove(progressLabel);
                container.getChildren().add(indicator.getGraphic());
            } else {
                if (container.getChildren().size() > 1) {
                    container.getChildren().remove(1);
                    container.getChildren().add(progressLabel);
                    updateProgressLabel();
                }
            }
        });
    }

    private int getMaxAngle() {
        return getSkinnable().isReverse() ? 360 : -360;
    }

    private double calcProgressArcLength() {
        var progress = getSkinnable().getProgress();
        return getSkinnable().isReverse() ? (1 - progress) * getMaxAngle() : progress * getMaxAngle();
    }

    protected void updateProgressLabel() {
        var progress = getSkinnable().getProgress();

        if (getSkinnable().getStringConverter() != null) {
            progressLabel.setText(getSkinnable().getStringConverter().toString(progress));
            return;
        }

        if (progress >= 0) {
            progressLabel.setText((int) Math.ceil(progress * 100) + "%");
        }
    }

    protected void toggleIndeterminate() {
        var indeterminate = getSkinnable().isIndeterminate();
        progressLabel.setManaged(!indeterminate);
        progressLabel.setVisible(!indeterminate);

        if (indeterminate) {
            if (getSkinnable().isVisible()) {
                transition.play();
            }
        } else {
            progressArc.setRotate(0);
            transition.stop();
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        var size = Math.max(w, h);
        var radius = (size / 2) - (progressArc.getStrokeWidth() / 2);

        trackCircle.setCenterX(size / 2);
        trackCircle.setCenterY(size / 2);
        trackCircle.setRadius(radius);

        progressArc.setCenterX(size / 2);
        progressArc.setCenterY(size / 2);
        progressArc.setRadiusX(radius);
        progressArc.setRadiusY(radius);

        container.resizeRelocate(x, y, size, size);
    }

    // Control height is always equal to its width.
    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return super.computeMinWidth(0, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset,
                                       double bottomInset, double leftInset) {
        return super.computePrefWidth(0, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return super.computeMaxWidth(0, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    public void dispose() {
        transition.stop();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Styleable Properties                                                  //
    ///////////////////////////////////////////////////////////////////////////

    protected DoubleProperty indeterminateAnimationTime = null;

    private DoubleProperty indeterminateAnimationTimeProperty() {
        if (indeterminateAnimationTime == null) {
            indeterminateAnimationTime = new StyleableDoubleProperty(DEFAULT_ANIMATION_TIME) {

                @Override
                public Object getBean() {
                    return RingProgressIndicatorSkin.this;
                }

                @Override
                public String getName() {
                    return "indeterminateAnimationTime";
                }

                @Override
                public CssMetaData<RingProgressIndicator, Number> getCssMetaData() {
                    return StyleableProperties.INDETERMINATE_ANIMATION_TIME;
                }
            };
        }
        return indeterminateAnimationTime;
    }

    public double getIndeterminateAnimationTime() {
        return indeterminateAnimationTime == null ? DEFAULT_ANIMATION_TIME : indeterminateAnimationTime.get();
    }

    private static class StyleableProperties {

        private static final CssMetaData<RingProgressIndicator, Number> INDETERMINATE_ANIMATION_TIME =
            new CssMetaData<>("-fx-indeterminate-animation-time", SizeConverter.getInstance(), DEFAULT_ANIMATION_TIME) {

                @Override
                public boolean isSettable(RingProgressIndicator n) {
                    return n.getSkin() instanceof RingProgressIndicatorSkin s
                        && (s.indeterminateAnimationTime == null || !s.indeterminateAnimationTime.isBound());
                }

                @Override
                @SuppressWarnings("unchecked")
                public StyleableProperty<Number> getStyleableProperty(RingProgressIndicator n) {
                    final RingProgressIndicatorSkin skin = (RingProgressIndicatorSkin) n.getSkin();
                    return (StyleableProperty<Number>) skin.indeterminateAnimationTimeProperty();
                }
            };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
                SkinBase.getClassCssMetaData()
            );
            styleables.add(INDETERMINATE_ANIMATION_TIME);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return RingProgressIndicatorSkin.StyleableProperties.STYLEABLES;
    }
}
