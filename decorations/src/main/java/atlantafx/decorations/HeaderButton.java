/* SPDX-License-Identifier: MIT */

package atlantafx.decorations;

import java.util.Objects;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.HeaderButtonType;

/**
 * Represents a header button component of a given {@link HeaderButtonType}.
 */
@SuppressWarnings("deprecation")
public class HeaderButton extends Control {

    private final HeaderButtonType type;

    /**
     * Constructs a HeaderButton with the specified HeaderButtonType.
     *
     * @param type the type of the header button; must not be null
     * @throws NullPointerException if the type is null
     */
    public HeaderButton(HeaderButtonType type) {
        super();

        Objects.requireNonNull(type, "HeaderButtonType type must not be null");
        this.type = type;

        getStyleClass().add("header-button");
        HeaderBar.setButtonType(this, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Skin<?> createDefaultSkin() {
        return new HeaderButtonSkin(this);
    }

    /**
     * Returns the type of the header button.
     */
    public HeaderButtonType getType() {
        return type;
    }
}
