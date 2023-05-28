/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

public class RingProgressIndicator extends ProgressIndicator {

    public RingProgressIndicator() {
        super();
    }

    public RingProgressIndicator(double progress) {
        this(progress, false);
    }

    public RingProgressIndicator(double progress, boolean reverse) {
        super(progress);
        this.reverse.set(reverse);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RingProgressIndicatorSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    protected final ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic", null);

    public Node getGraphic() {
        return graphicProperty().get();
    }

    public void setGraphic(Node graphic) {
        graphicProperty().set(graphic);
    }

    /**
     * Any node to be displayed within the progress indicator. If null,
     * it will fall back to the Label with integer progress value from 1 to 100.
     */
    public ObjectProperty<Node> graphicProperty() {
        return graphic;
    }

    // ~

    protected final ObjectProperty<StringConverter<Double>> stringConverter =
        new SimpleObjectProperty<>(this, "converter", null);

    public StringConverter<Double> getStringConverter() {
        return stringConverterProperty().get();
    }

    public void setStringConverter(StringConverter<Double> stringConverter) {
        this.stringConverterProperty().set(stringConverter);
    }

    /**
     * Optional converter to transform progress value to string.
     */
    public ObjectProperty<StringConverter<Double>> stringConverterProperty() {
        return stringConverter;
    }

    // ~

    private final ReadOnlyBooleanWrapper reverse = new ReadOnlyBooleanWrapper(this, "reverse", false);

    public boolean isReverse() {
        return reverse.get();
    }

    /**
     * Reverse progress indicator scale. For indeterminate variant
     * this means it will be rotated counterclockwise.
     */
    public ReadOnlyBooleanProperty reverseProperty() {
        return reverse.getReadOnlyProperty();
    }
}
