/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * A versatile container that can used in various contexts such as dialog headers,
 * list items, and cards. It can contain a graphic, a title, subtitle, and optional
 * actions.
 */
public class Tile extends Control {

    public Tile() {
        this(null, null, null);
    }

    public Tile(@NamedArg("title") String title,
                @NamedArg("subtitle") String subtitle) {
        this(title, subtitle, null);
    }

    public Tile(@NamedArg("title") String title,
                @NamedArg("subtitle") String subtitle,
                @NamedArg("graphic") Node graphic) {
        super();
        setTitle(title);
        setSubTitle(subtitle);
        setGraphic(graphic);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TileSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The property representing the tile’s graphic node. It is commonly used
     * to add images or icons that are associated with the tile.
     */
    private final ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic");

    public Node getGraphic() {
        return graphic.get();
    }

    public ObjectProperty<Node> graphicProperty() {
        return graphic;
    }

    public void setGraphic(Node graphic) {
        this.graphic.set(graphic);
    }

    /**
     * The property representing the tile’s title. Although it is not mandatory,
     * you typically would not want to have a tile without a title.
     */
    private final StringProperty title = new SimpleStringProperty(this, "title");

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * The property representing the tile’s subtitle. This is usually an optional
     * text or a description.
     */
    private final StringProperty subTitle = new SimpleStringProperty(this, "subTitle");

    public String getSubTitle() {
        return subTitle.get();
    }

    public StringProperty subTitleProperty() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle.set(subTitle);
    }

    /**
     * The property representing the tile’s action node. It is commonly used
     * to place an action controls that are associated with the tile.
     */
    private final ObjectProperty<Node> action = new SimpleObjectProperty<>(this, "action");

    public Node getAction() {
        return action.get();
    }

    public ObjectProperty<Node> actionProperty() {
        return action;
    }

    public void setAction(Node action) {
        this.action.set(action);
    }

    /**
     * The property representing the tile’s action handler. Setting an action handler
     * makes the tile interactive or clickable. When a user clicks on the interactive
     * tile, the specified action handler will be called.
     */
    private final ObjectProperty<Runnable> actionHandler = new SimpleObjectProperty<>(this, "actionHandler");

    public Runnable getActionHandler() {
        return actionHandler.get();
    }

    public ObjectProperty<Runnable> actionHandlerProperty() {
        return actionHandler;
    }

    public void setActionHandler(Runnable actionHandler) {
        this.actionHandler.set(actionHandler);
    }
}
