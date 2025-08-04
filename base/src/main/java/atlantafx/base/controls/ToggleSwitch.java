/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2015, ControlsFX
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package atlantafx.base.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.event.ActionEvent;
import javafx.geometry.HorizontalDirection;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import org.jspecify.annotations.Nullable;

/**
 * A control that provides users with the ability to choose between two distinct values.
 * It is functionally similar, though aesthetically different, from the RadioButton
 * and Checkbox.
 */
public class ToggleSwitch extends Labeled implements Toggle {

    protected static final String DEFAULT_STYLE_CLASS = "toggle-switch";
    protected static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    protected static final PseudoClass PSEUDO_CLASS_RIGHT = PseudoClass.getPseudoClass("right");

    /**
     * Creates a toggle switch with empty string for its label.
     */
    public ToggleSwitch() {
        initialize();
    }

    /**
     * Creates a toggle switch with the specified label.
     *
     * @param text The label string of the control.
     */
    public ToggleSwitch(String text) {
        super(text);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new ToggleSwitchSkin(this);
    }

    private void initialize() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns whether this Toggle Switch is selected.
     */
    @Override
    public final BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new BooleanPropertyBase() {

                @Override
                protected void invalidated() {
                    final boolean selected = get();
                    final ToggleGroup tg = getToggleGroup();
                    pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, selected);
                    notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTED);

                    if (tg != null) {
                        if (selected) {
                            tg.selectToggle(ToggleSwitch.this);
                        } else if (tg.getSelectedToggle() == ToggleSwitch.this) {
                            // This code was copied from ToggleButton, and originally
                            // it should use the following method, which is like almost
                            // everything in JavaFX is private. Probably it fixes some
                            // internal toggle group state.
                            // tg.clearSelectedToggle();

                            // This is kind of an equivalent code even though
                            // "!tg.getSelectedToggle().isSelected()"
                            // looks like absurd and should always return false.
                            if (!tg.getSelectedToggle().isSelected()) {
                                for (Toggle toggle : tg.getToggles()) {
                                    if (toggle.isSelected()) {
                                        return;
                                    }
                                }
                            }

                            tg.selectToggle(null);
                        }
                    }
                }

                @Override
                public Object getBean() {
                    return ToggleSwitch.this;
                }

                @Override
                public String getName() {
                    return "selected";
                }
            };
        }

        return selected;
    }

    private @Nullable BooleanProperty selected;

    @Override
    public final void setSelected(boolean value) {
        selectedProperty().set(value);
    }

    @Override
    public final boolean isSelected() {
        return selected != null && selected.get();
    }

    /**
     * The {@link ToggleGroup} to which this ToggleSwitch belongs. A toggle can only
     * be in one group at any one time. If the group is changed, then the toggle is
     * removed from the old group prior to being added to the new group.
     */
    @Override
    public final ObjectProperty<@Nullable ToggleGroup> toggleGroupProperty() {
        if (toggleGroup == null) {
            toggleGroup = new ObjectPropertyBase<>() {
                private @Nullable ToggleGroup old;

                @Override
                protected void invalidated() {
                    final ToggleGroup tg = get();
                    if (tg != null && !tg.getToggles().contains(ToggleSwitch.this)) {
                        if (old != null) {
                            old.getToggles().remove(ToggleSwitch.this);
                        }
                        tg.getToggles().add(ToggleSwitch.this);
                    } else if (tg == null) {
                        old.getToggles().remove(ToggleSwitch.this);
                    }

                    old = tg;
                }

                @Override
                public Object getBean() {
                    return ToggleSwitch.this;
                }

                @Override
                public String getName() {
                    return "toggleGroup";
                }
            };
        }
        return toggleGroup;
    }

    private @Nullable ObjectProperty<@Nullable ToggleGroup> toggleGroup;

    @Override
    public final void setToggleGroup(@Nullable ToggleGroup value) {
        toggleGroupProperty().set(value);
    }

    @Override
    public final @Nullable ToggleGroup getToggleGroup() {
        return toggleGroup == null ? null : toggleGroup.get();
    }

    /**
     * Specifies the side where {@link #textProperty()} value should be placed.
     * Default is {@link HorizontalDirection#LEFT}.
     */
    public final ObjectProperty<@Nullable HorizontalDirection> labelPositionProperty() {
        if (labelPosition == null) {
            labelPosition = new StyleableObjectProperty<>(HorizontalDirection.LEFT) {

                @Override
                public Object getBean() {
                    return ToggleSwitch.this;
                }

                @Override
                public String getName() {
                    return "labelPosition";
                }

                @Override
                protected void invalidated() {
                    final HorizontalDirection v = get();
                    pseudoClassStateChanged(ToggleSwitch.PSEUDO_CLASS_RIGHT, v == HorizontalDirection.RIGHT);
                }

                @Override
                public CssMetaData<ToggleSwitch, HorizontalDirection> getCssMetaData() {
                    return StyleableProperties.LABEL_POSITION;
                }
            };
        }

        return labelPosition;
    }

    private @Nullable ObjectProperty<HorizontalDirection> labelPosition;

    public final void setLabelPosition(@Nullable HorizontalDirection pos) {
        labelPositionProperty().setValue(pos);
    }

    public final HorizontalDirection getLabelPosition() {
        return labelPosition == null ? HorizontalDirection.LEFT : labelPosition.getValue();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods                                                               //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Toggles the state of the switch, cycling through the selected and unselected states.
     */
    public void fire() {
        if (!isDisabled()) {
            setSelected(!isSelected());
            fireEvent(new ActionEvent());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Styleable Properties                                                  //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    private static class StyleableProperties {

        private static final CssMetaData<ToggleSwitch, HorizontalDirection> LABEL_POSITION = new CssMetaData<>(
            "-fx-label-position", new EnumConverter<>(HorizontalDirection.class), HorizontalDirection.LEFT
        ) {

            @Override
            @SuppressWarnings("ConstantValue")
            public boolean isSettable(ToggleSwitch c) {
                return c.labelPositionProperty() == null || !c.labelPositionProperty().isBound();
            }

            @Override
            public StyleableProperty<HorizontalDirection> getStyleableProperty(ToggleSwitch c) {
                var val = (WritableValue<HorizontalDirection>) c.labelPositionProperty();
                return (StyleableProperty<HorizontalDirection>) val;
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Labeled.getClassCssMetaData());
            styleables.add(LABEL_POSITION);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
}
