/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.jspecify.annotations.Nullable;

/**
 * A dropdown control that wraps a {@link TreeView} inside a popup.
 * Supports both single and multiple selection via a {@code selectionMode} property.
 * In multiple selection mode, tree cells display checkboxes for intuitive toggling.
 */
public class TreeSelect<T> extends Control {

    private static final String DEFAULT_STYLE_CLASS = "tree-select";

    public TreeSelect() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    public TreeSelect(@Nullable TreeItem<T> root) {
        this();
        setRoot(root);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TreeSelectSkin<>(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    // ~ root
    private final ObjectProperty<@Nullable TreeItem<T>> root =
            new SimpleObjectProperty<>(this, "root");

    public ObjectProperty<@Nullable TreeItem<T>> rootProperty() {
        return root;
    }

    public @Nullable TreeItem<T> getRoot() {
        return root.get();
    }

    public void setRoot(@Nullable TreeItem<T> value) {
        root.set(value);
    }

    // ~ promptText
    private final StringProperty promptText = new SimpleStringProperty(this, "promptText", "");

    public StringProperty promptTextProperty() {
        return promptText;
    }

    public String getPromptText() {
        return promptText.get();
    }

    public void setPromptText(String value) {
        promptText.set(value);
    }

    // ~ showRoot
    private final BooleanProperty showRoot = new SimpleBooleanProperty(this, "showRoot", true);

    public BooleanProperty showRootProperty() {
        return showRoot;
    }

    public boolean isShowRoot() {
        return showRoot.get();
    }

    public void setShowRoot(boolean value) {
        showRoot.set(value);
    }

    // ~ selectionMode
    private final ObjectProperty<javafx.scene.control.SelectionMode> selectionMode =
            new SimpleObjectProperty<>(this, "selectionMode", javafx.scene.control.SelectionMode.SINGLE);

    public ObjectProperty<javafx.scene.control.SelectionMode> selectionModeProperty() {
        return selectionMode;
    }

    public javafx.scene.control.SelectionMode getSelectionMode() {
        return selectionMode.get();
    }

    public void setSelectionMode(javafx.scene.control.SelectionMode value) {
        selectionMode.set(value);
    }
}
