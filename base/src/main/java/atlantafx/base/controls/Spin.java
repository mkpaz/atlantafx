/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.css.converter.PaintConverter;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Spin represents an indeterminate progress/loading indicator.
 *
 * <p>For each skin, you can customize its primary and secondary colors.
 * Some skins may also allow further customization via constructor arguments.
 *
 * <p>The control comes with a wide variety of skins. Example usage:
 * <pre>{@code
 * var equalizerSpin = BarsEqualizerSpin.create();
 * var arcSpin = DoubleArcSpin.create(Duration.seconds(2.2));
 * }</pre>
 *
 * <p>Or, if you need more control over the skin dimensions:
 * <pre>{@code
 * double radius = 18.0, opacity = 0.5;
 * var spin = new Spin(Duration.seconds(2.2));
 * spin.setSkin(new EclipseSpin(spin, radius, opacity));
 * }</pre>
 */
public class Spin extends Control {

    public static final Color DEFAULT_PRIMARY_COLOR = Color.RED;
    public static final Color DEFAULT_SECONARY_COLOR = Color.GREEN;
    public static final Color DEFAULT_TERTIARY_COLOR = Color.BLUE;

    // Not an observable property to prevent the duration value from changing
    // during the animation; animation speed should remain constant.
    protected final Duration duration;

    /** Default constructor. */
    public Spin() {
        this(null);
    }

    /** Creates a new Spin with the given duration. */
    public Spin(@Nullable Duration duration) {
        super();

        this.duration = duration != null ? duration : Duration.ZERO;
        getStyleClass().add("spin");
    }

    /** {@inheritDoc} */
    @Override
    protected Skin<?> createDefaultSkin() {
        throw new IllegalStateException("There is no default skin for Spin control; you have to set one manually.");
    }

    /** Returns animation duration. */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Represents the text of the spin, when applicable.
     *
     * <p>The default value of this property is {@code null}.
     */
    private final ObjectProperty<@Nullable String> text = new SimpleObjectProperty<>(
        Spin.this, "text", null
    );

    /** See {@link #text}. */
    public @Nullable String getText() {
        return textProperty().get();
    }

    /** See {@link #text}. */
    public void setText(@Nullable String paint) {
        textProperty().set(paint);
    }

    /** See {@link #text}. */
    public ObjectProperty<@Nullable String> textProperty() {
        return text;
    }

    /**
     * Represents the primary color of the spin. It can also be set using CSS with the
     * {@code -spin-color-primary} property.
     *
     * <p>The default value of this property is {@link #DEFAULT_PRIMARY_COLOR}.
     */
    private final StyleableObjectProperty<Paint> primaryColor = new SimpleStyleableObjectProperty<>(
        StyleableProperties.COLOR_PRIMARY, Spin.this, "primaryColor", DEFAULT_PRIMARY_COLOR
    );

    /** See {@link #primaryColor}. */
    public Paint getPrimaryColor() {
        return primaryColorProperty().get();
    }

    /** See {@link #primaryColor}. */
    public void setPrimaryColor(Paint paint) {
        primaryColorProperty().set(paint);
    }

    /** See {@link #primaryColor}. */
    public ObjectProperty<Paint> primaryColorProperty() {
        return primaryColor;
    }

    /**
     * Represents the secondary color of the spin. It can also be set using CSS with the
     * {@code -spin-color-secondary} property.
     *
     * <p>The default value of this property is {@link #DEFAULT_SECONARY_COLOR}.
     */
    private final StyleableObjectProperty<Paint> secondaryColor = new SimpleStyleableObjectProperty<>(
        StyleableProperties.COLOR_SECONDARY, Spin.this, "secondaryColor", DEFAULT_SECONARY_COLOR
    );

    /** See {@link #secondaryColor}. */
    public Paint getSecondaryColor() {
        return secondaryColorProperty().get();
    }

    /** See {@link #secondaryColor}. */
    public void setSecondaryColor(Paint paint) {
        secondaryColorProperty().set(paint);
    }

    /** See {@link #secondaryColor}. */
    public ObjectProperty<Paint> secondaryColorProperty() {
        return secondaryColor;
    }

    /**
     * Represents the tertiary color of the spin. It can also be set using CSS with the
     * {@code -spin-color-tertiary} property.
     *
     * <p>The default value of this property is {@link #DEFAULT_TERTIARY_COLOR}.
     */
    private final StyleableObjectProperty<Paint> tertiaryColor = new SimpleStyleableObjectProperty<>(
        StyleableProperties.COLOR_TERTIARY, Spin.this, "tertiaryColor", DEFAULT_TERTIARY_COLOR
    );

    /** See {@link #tertiaryColor}. */
    public Paint getTertiaryColor() {
        return tertiaryColorProperty().get();
    }

    /** See {@link #tertiaryColor}. */
    public void setTertiaryColor(Paint paint) {
        tertiaryColorProperty().set(paint);
    }

    /** See {@link #tertiaryColor}. */
    public ObjectProperty<Paint> tertiaryColorProperty() {
        return tertiaryColor;
    }

    /** Returns the percentage of the current animation duration. */
    public Duration getDurationPercentage(double percent) {
        return duration.divide(100).multiply(percent);
    }

    /**
     * Sets whether the animation should be started immediately after connecting
     * a skin to a Scene.
     */
    public void autostart(boolean autostart) {
        if (getSkin() instanceof SpinSkin skin) {
            skin.autostart(autostart);
        }
    }

    /** Starts the animation. */
    public void start() {
        if (getSkin() instanceof SpinSkin skin) {
            skin.start();
        }
    }

    /** Stops the animation. */
    public void stop() {
        if (getSkin() instanceof SpinSkin skin) {
            skin.stop();
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isResizable() {
        if (getSkin() instanceof SpinSkin) {
            return true;
        }
        return super.isResizable();
    }

    /** {@inheritDoc} */
    @Override
    protected double computeMinWidth(double height) {
        if (getSkin() instanceof SpinSkin skin) {
            return skin.computeMaxWidth(height);
        }
        return super.computeMinWidth(height);
    }

    /** {@inheritDoc} */
    @Override
    protected double computeMinHeight(double width) {
        if (getSkin() instanceof SpinSkin skin) {
            return skin.computeMaxHeight(width);
        }
        return super.computeMinHeight(width);
    }

    /** {@inheritDoc} */
    @Override
    protected double computeMaxWidth(double height) {
        return computeMinWidth(height);
    }

    /** {@inheritDoc} */
    @Override
    protected double computeMaxHeight(double width) {
        return computeMinHeight(width);
    }

    /** {@inheritDoc} */
    @Override
    protected double computePrefWidth(double height) {
        return computeMinWidth(height);
    }

    /** {@inheritDoc} */
    @Override
    protected double computePrefHeight(double width) {
        return computeMinHeight(width);
    }

    /** {@inheritDoc} */
    @Override
    protected void layoutChildren() {
        if (getSkin() instanceof SpinSkin) {
            Node node = getSkin().getNode();
            if (node != null) {
                double x = snappedLeftInset();
                double y = snappedTopInset();
                double w = snapSizeX(getWidth()) - x - snappedRightInset();
                double h = snapSizeY(getHeight()) - y - snappedBottomInset();
                node.resizeRelocate(x, y, w, h);
            }
            return;
        }
        super.layoutChildren();
    }

    //*************************************************************************
    // Styleable
    //*************************************************************************

    static class StyleableProperties {

        private static final CssMetaData<Spin, Paint> COLOR_PRIMARY = new CssMetaData<>(
            "-spin-color-primary", PaintConverter.getInstance(), DEFAULT_PRIMARY_COLOR
        ) {
            @Override
            @SuppressWarnings("ConstantValue")
            public boolean isSettable(Spin c) {
                return c.primaryColorProperty() == null || !c.primaryColorProperty().isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(Spin c) {
                return (StyleableObjectProperty<Paint>) c.primaryColorProperty();
            }
        };

        private static final CssMetaData<Spin, Paint> COLOR_SECONDARY = new CssMetaData<>(
            "-spin-color-secondary", PaintConverter.getInstance(), DEFAULT_SECONARY_COLOR
        ) {
            @Override
            @SuppressWarnings("ConstantValue")
            public boolean isSettable(Spin c) {
                return c.secondaryColorProperty() == null || !c.secondaryColorProperty().isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(Spin c) {
                return (StyleableObjectProperty<Paint>) c.secondaryColorProperty();
            }
        };

        private static final CssMetaData<Spin, Paint> COLOR_TERTIARY = new CssMetaData<>(
            "-spin-color-tertiary", PaintConverter.getInstance(), DEFAULT_TERTIARY_COLOR
        ) {
            @Override
            @SuppressWarnings("ConstantValue")
            public boolean isSettable(Spin c) {
                return c.tertiaryColorProperty() == null || !c.tertiaryColorProperty().isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(Spin c) {
                return (StyleableObjectProperty<Paint>) c.tertiaryColorProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            styleables.add(COLOR_PRIMARY);
            styleables.add(COLOR_SECONDARY);
            styleables.add(COLOR_TERTIARY);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /** {@inheritDoc} */
    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}
