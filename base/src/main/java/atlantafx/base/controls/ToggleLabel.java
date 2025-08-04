/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.WritableValue;
import javafx.css.PseudoClass;
import javafx.css.StyleableProperty;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.jspecify.annotations.Nullable;

/**
 * {@code ToggleLabel} is a utility component rather than an independent control.
 * It' i's similar to a {@link ToggleButton} but does not have a pressed state or action handler,
 * making it more like a {@code Tab}. It's intended to be used as a segment in controls like
 * {@link SegmentedControl}.
 */
public class ToggleLabel extends Labeled implements Toggle {

    public static final String INDEX_PROPERTY = "index";
    protected static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");

    /**
     * Creates a toggle with an empty string for its label.
     */
    public ToggleLabel() {
        initialize();
    }

    /**
     * Creates a toggle with the specified text as its label.
     *
     * @param text A text string for its label.
     */
    public ToggleLabel(String text) {
        setText(text);
        initialize();
    }

    /**
     * Creates a toggle with the specified text and icon for its label.
     *
     * @param text    A text string for its label.
     * @param graphic the icon for its label.
     */
    public ToggleLabel(String text, Node graphic) {
        setText(text);
        setGraphic(graphic);
        initialize();
    }

    @SuppressWarnings("RedundantCast")
    protected void initialize() {
        getStyleClass().setAll("toggle-label");
        setAccessibleRole(AccessibleRole.TAB_ITEM);

        // Alignment is styleable through CSS. Calling setAlignment() makes it look to CSS
        // like the user set the value and css will not override. Initializing alignment by
        // calling set on the CssMetaData ensures that CSS will be able to override the value.
        ((StyleableProperty<Pos>) (WritableValue<Pos>) alignmentProperty()).applyStyle(null, Pos.CENTER);
        setMnemonicParsing(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new ToggleLabelSkin(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        if (Objects.requireNonNull(attribute) == AccessibleAttribute.SELECTED) {
            return isSelected();
        }
        return super.queryAccessibleAttribute(attribute, parameters);
    }

    /**
     * Returns the index of this toggle in its parent,
     * or {@code -1} if the toggle has no parent.
     */
    public int getIndex() {
        var prop = getProperties().get(INDEX_PROPERTY);
        if (prop instanceof Integer index) {
            return index;
        }
        return -1;
    }

    //=========================================================================
    // Properties
    //=========================================================================

    /**
     * Returns whether this toggle is selected.
     */
    @Override
    public final BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new BooleanPropertyBase() {
                @Override
                protected void invalidated() {
                    boolean selected = get();
                    var group = getToggleGroup();
                    pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, selected);
                    notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTED);

                    if (group != null) {
                        if (selected) {
                            group.selectToggle(ToggleLabel.this);
                        } else if (group.getSelectedToggle() == ToggleLabel.this) {
                            if (!group.getSelectedToggle().isSelected()) {
                                for (Toggle toggle : group.getToggles()) {
                                    if (toggle.isSelected()) {
                                        return;
                                    }
                                }
                            }

                            group.selectToggle(null);
                        }
                    }
                }

                @Override
                public Object getBean() {
                    return ToggleLabel.this;
                }

                @Override
                public String getName() {
                    return "selected";
                }
            };
        }
        return selected;
    }

    protected @Nullable BooleanProperty selected;

    @Override
    public boolean isSelected() {
        return selected != null && selected.get();
    }

    @Override
    public void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    /**
     * The {@link ToggleGroup} to which this toggle belongs. A toggle can only
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
                    if (tg != null && !tg.getToggles().contains(ToggleLabel.this)) {
                        if (old != null) {
                            old.getToggles().remove(ToggleLabel.this);
                        }
                        tg.getToggles().add(ToggleLabel.this);
                    } else if (tg == null) {
                        old.getToggles().remove(ToggleLabel.this);
                    }

                    old = tg;
                }

                @Override
                public Object getBean() {
                    return ToggleLabel.this;
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
}
