/* SPDX-License-Identifier: MIT */
package atlantafx.base.controls;

import atlantafx.base.theme.Styles;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;

@SuppressWarnings("unused")
public class CaptionMenuItem extends CustomMenuItem {

    private final Label title = new Label();

    public CaptionMenuItem() {
        this(null);
    }

    public CaptionMenuItem(String text) {
        super();

        setTitle(text);
        setContent(title);
        setHideOnClick(false);
        getStyleClass().add(Styles.TEXT_CAPTION);
    }

    public String getTitle() {
        return title.getText();
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public StringProperty titleProperty() {
        return title.textProperty();
    }
}
