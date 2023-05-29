/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import org.jetbrains.annotations.Nullable;

/**
 * A versatile container that can used in various contexts such as dialog headers,
 * list items, and cards. It can contain a graphic, a title, description, and optional
 * actions.
 */
public class Tile extends TileBase {

    public Tile() {
        this(null, null, null);
    }

    public Tile(@Nullable @NamedArg("title") String title,
                @Nullable @NamedArg("description") String description) {
        this(title, description, null);
    }

    public Tile(@Nullable String title,
                @Nullable String description,
                @Nullable Node graphic) {
        super(title, description, graphic);
        getStyleClass().add("tile");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TileSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

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
