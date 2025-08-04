/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import org.jspecify.annotations.Nullable;

/**
 * A MenuItem that is intended to contain a caption for a group of menu items
 * that share a common purpose.
 */
public class CaptionMenuItem extends CustomMenuItem {

    protected final Label title = new Label();

    /**
     * Creates an empty menu item.
     */
    public CaptionMenuItem() {
        this(null);
    }

    /**
     * Creates a CaptionMenuItem with the specified text as the title.
     */
    public CaptionMenuItem(@Nullable @NamedArg("text") String text) {
        super();

        setTitle(text);
        setContent(title);
        setHideOnClick(false);
        getStyleClass().addAll("caption-menu-item");
    }

    /**
     * Contains the title of the menu item.
     */
    public StringProperty titleProperty() {
        return title.textProperty();
    }

    public String getTitle() {
        return title.getText();
    }

    public void setTitle(@Nullable String text) {
        title.setText(text);
    }
}
