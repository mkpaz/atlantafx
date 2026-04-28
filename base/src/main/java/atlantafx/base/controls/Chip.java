/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import org.jspecify.annotations.Nullable;

/**
 * A compact inline control representing an input, attribute, or action.
 * Supports optional close button and selection via {@link ToggleGroup}.
 */
public class Chip extends Labeled implements Toggle {

    private static final String DEFAULT_STYLE_CLASS = "chip";
    private static final PseudoClass PSEUDO_CLASS_SELECTED =
            PseudoClass.getPseudoClass("selected");

    public Chip() {
        super();
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    public Chip(String text) {
        this();
        setText(text);
    }

    public Chip(String text, @Nullable Node graphic) {
        this();
        setText(text);
        setGraphic(graphic);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ChipSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Toggle                                                                //
    ///////////////////////////////////////////////////////////////////////////

    private @Nullable BooleanProperty selected;

    @Override
    public final BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new BooleanPropertyBase() {
                @Override
                protected void invalidated() {
                    final boolean val = get();
                    pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, val);

                    ToggleGroup tg = getToggleGroup();
                    if (tg != null) {
                        if (val) {
                            tg.selectToggle(Chip.this);
                        } else if (tg.getSelectedToggle() == Chip.this) {
                            tg.selectToggle(null);
                        }
                    }
                }

                @Override
                public Object getBean() {
                    return Chip.this;
                }

                @Override
                public String getName() {
                    return "selected";
                }
            };
        }
        return selected;
    }

    @Override
    public final void setSelected(boolean value) {
        selectedProperty().set(value);
    }

    @Override
    public final boolean isSelected() {
        return selected != null && selected.get();
    }

    private @Nullable ObjectProperty<@Nullable ToggleGroup> toggleGroup;

    @Override
    public final ObjectProperty<@Nullable ToggleGroup> toggleGroupProperty() {
        if (toggleGroup == null) {
            toggleGroup = new ObjectPropertyBase<>() {
                private @Nullable ToggleGroup old;

                @Override
                protected void invalidated() {
                    final ToggleGroup tg = get();
                    if (tg != null && !tg.getToggles().contains(Chip.this)) {
                        if (old != null) {
                            old.getToggles().remove(Chip.this);
                        }
                        tg.getToggles().add(Chip.this);
                    } else if (tg == null && old != null) {
                        old.getToggles().remove(Chip.this);
                    }
                    old = tg;
                }

                @Override
                public Object getBean() {
                    return Chip.this;
                }

                @Override
                public String getName() {
                    return "toggleGroup";
                }
            };
        }
        return toggleGroup;
    }

    @Override
    public final void setToggleGroup(@Nullable ToggleGroup value) {
        toggleGroupProperty().set(value);
    }

    @Override
    public final @Nullable ToggleGroup getToggleGroup() {
        return toggleGroup == null ? null : toggleGroup.get();
    }

    ///////////////////////////////////////////////////////////////////////////
    // onClose                                                               //
    ///////////////////////////////////////////////////////////////////////////

    private final ObjectProperty<@Nullable EventHandler<? super Event>> onClose =
            new SimpleObjectProperty<>(this, "onClose");

    public ObjectProperty<@Nullable EventHandler<? super Event>> onCloseProperty() {
        return onClose;
    }

    public @Nullable EventHandler<? super Event> getOnClose() {
        return onClose.get();
    }

    public void setOnClose(@Nullable EventHandler<? super Event> handler) {
        onClose.set(handler);
    }
}
