/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;

/**
 * A ProgressIndicator that displays progress value as a ring that gradually
 * empties out as a task is completed.
 */
public class RingProgressIndicator extends ProgressIndicator {

    /**
     * Creates a new indeterminate ProgressIndicator.
     */
    public RingProgressIndicator() {
        super();
    }

    /**
     * Creates a new ProgressIndicator with the given progress value.
     *
     * @param progress The progress, represented as a value between 0 and 1.
     */
    public RingProgressIndicator(@NamedArg("progress") double progress) {
        this(progress, false);
    }

    /**
     * Creates a new ProgressIndicator with the given progress value and type.
     *
     * @param progress The progress, represented as a value between 0 and 1.
     * @param reverse  A flag to indicate whether the indicator is reversed or not.
     */
    public RingProgressIndicator(@NamedArg("progress") double progress,
                                 @NamedArg("reverse") boolean reverse) {
        super(progress);
        this.reverse.set(reverse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new RingProgressIndicatorSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents the node to be displayed within the progress indicator. If null,
     * it will fall back to the Label with an integer progress value from 1 to 100.
     */
    public ObjectProperty<@Nullable Node> graphicProperty() {
        return graphic;
    }

    protected final ObjectProperty<@Nullable Node> graphic = new SimpleObjectProperty<>(this, "graphic", null);

    public @Nullable Node getGraphic() {
        return graphicProperty().get();
    }

    public void setGraphic(@Nullable Node graphic) {
        graphicProperty().set(graphic);
    }

    /**
     * Represents an optional converter to transform the progress value to a string.
     * It is only used if a custom graphic node is not set.
     *
     * @see #graphicProperty()
     */
    public ObjectProperty<@Nullable StringConverter<Double>> stringConverterProperty() {
        return stringConverter;
    }

    protected final ObjectProperty<@Nullable StringConverter<Double>> stringConverter =
        new SimpleObjectProperty<>(this, "converter", null);

    public @Nullable StringConverter<Double> getStringConverter() {
        return stringConverterProperty().get();
    }

    public void setStringConverter(@Nullable StringConverter<Double> stringConverter) {
        this.stringConverterProperty().set(stringConverter);
    }

    /**
     * Reverses the progress indicator scale. For the indeterminate variant,
     * this means it will be rotated counterclockwise.
     */
    public ReadOnlyBooleanProperty reverseProperty() {
        return reverse.getReadOnlyProperty();
    }

    protected final ReadOnlyBooleanWrapper reverse = new ReadOnlyBooleanWrapper(this, "reverse", false);

    public boolean isReverse() {
        return reverse.get();
    }
}
