/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.jspecify.annotations.Nullable;

/**
 * A control that allows users to rate something by displaying a row of
 * star-shaped indicators. The rating value can be set programmatically
 * or interactively by clicking on the stars.
 *
 * <p>Example:
 * <pre>{@code
 * var rating = new Rating();
 * rating.setRating(3.5);
 * rating.setMax(5);
 * rating.setPartialRating(true);
 * }</pre>
 */
public class Rating extends Control {

    /**
     * The default maximum number of stars.
     */
    public static final int DEFAULT_MAX = 5;

    public Rating() {
        this(DEFAULT_MAX);
    }

    public Rating(int max) {
        super();
        setMax(max);
        getStyleClass().add("rating");
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RatingSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The current rating value. Default is 0.
     */
    public DoubleProperty ratingProperty() {
        return rating;
    }

    private final DoubleProperty rating = new SimpleDoubleProperty(this, "rating", 0);

    public double getRating() {
        return rating.get();
    }

    public void setRating(double rating) {
        this.rating.set(rating);
    }

    /**
     * The maximum number of stars. Default is 5.
     */
    public IntegerProperty maxProperty() {
        return max;
    }

    private final IntegerProperty max = new SimpleIntegerProperty(this, "max", DEFAULT_MAX);

    public int getMax() {
        return max.get();
    }

    public void setMax(int max) {
        this.max.set(max);
    }

    /**
     * Whether the rating can be changed by user interaction.
     * When false, the rating is read-only. Default is true.
     */
    public BooleanProperty editableProperty() {
        return editable;
    }

    private final BooleanProperty editable = new SimpleBooleanProperty(this, "editable", true);

    public boolean isEditable() {
        return editable.get();
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }

    /**
     * Whether to allow partial (fractional) ratings like 2.5.
     * When false, the rating is clamped to integer values. Default is false.
     */
    public BooleanProperty partialRatingProperty() {
        return partialRating;
    }

    private final BooleanProperty partialRating = new SimpleBooleanProperty(this, "partialRating", false);

    public boolean isPartialRating() {
        return partialRating.get();
    }

    public void setPartialRating(boolean partialRating) {
        this.partialRating.set(partialRating);
    }
}
