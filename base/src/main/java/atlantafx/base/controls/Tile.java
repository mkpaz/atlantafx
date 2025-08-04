/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import org.jspecify.annotations.Nullable;

/**
 * A versatile container that can used in various contexts such as dialog
 * headers, list items, and cards. It can contain a graphic, a title, description,
 * and optional actions.
 */
public class Tile extends TileBase {

    /**
     * Creates a new empty Tile.
     */
    public Tile() {
        this(null, null, null);
    }

    /**
     * Creates a new Tile with an initial title and description.
     *
     * @param title       A string for the title.
     * @param description A string for the description.
     */
    public Tile(@Nullable @NamedArg("title") String title,
                @Nullable @NamedArg("description") String description) {
        this(title, description, null);
    }

    /**
     * Creates a new Tile with an initial title, description and graphic.
     *
     * @param title       A string for the title.
     * @param description A string for the description.
     * @param graphic     A graphic or icon.
     */
    public Tile(@Nullable String title,
                @Nullable String description,
                @Nullable Node graphic) {
        super(title, description, graphic);
        getStyleClass().add("tile");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new TileSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents the node to be placed in the tile’s action slot. It is commonly
     * used to place action controls that are associated with the tile.
     */
    public ObjectProperty<Node> actionProperty() {
        return action;
    }

    private final ObjectProperty<Node> action = new SimpleObjectProperty<>(this, "action");

    public Node getAction() {
        return action.get();
    }

    public void setAction(Node action) {
        this.action.set(action);
    }

    /**
     * Represents the tile’s action handler.
     *
     * <p>Setting an action handler makes the tile interactive (or clickable).
     * When a user clicks on the interactive tile, the specified action handler will be called.
     */
    public ObjectProperty<@Nullable Runnable> actionHandlerProperty() {
        return actionHandler;
    }

    private final ObjectProperty<@Nullable Runnable> actionHandler
        = new SimpleObjectProperty<>(this, "actionHandler");

    public @Nullable Runnable getActionHandler() {
        return actionHandler.get();
    }

    public void setActionHandler(@Nullable Runnable actionHandler) {
        this.actionHandler.set(actionHandler);
    }
}
