/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * A control that displays a vertical timeline of events.
 */
public class Timeline extends Control {

    public enum Alignment { LEFT, ALTERNATE }

    private static final String DEFAULT_STYLE_CLASS = "timeline";

    public Timeline() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TimelineSkin(this);
    }

    private final ObservableList<TimelineItem> items =
            FXCollections.observableArrayList();

    public ObservableList<TimelineItem> getItems() {
        return items;
    }

    private final ObjectProperty<Alignment> alignment =
            new SimpleObjectProperty<>(this, "alignment", Alignment.LEFT);

    public ObjectProperty<Alignment> alignmentProperty() { return alignment; }
    public Alignment getAlignment() { return alignment.get(); }
    public void setAlignment(Alignment value) { alignment.set(value); }
}
