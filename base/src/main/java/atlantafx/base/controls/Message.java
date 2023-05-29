/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import org.jetbrains.annotations.Nullable;

/**
 * The Message is a component for displaying notifications or alerts
 * and is specifically designed to grab the user’s attention.
 * It is based on the Tile layout and shares its structure.
 */
public class Message extends TileBase {

    /**
     * See {@link Tile#Tile()}.
     */
    public Message() {
        this(null, null, null);
    }

    /**
     * See {@link Tile#Tile(String, String)}.
     */
    public Message(@Nullable @NamedArg("title") String title,
                   @Nullable @NamedArg("description") String description) {
        this(title, description, null);
    }

    /**
     * See {@link Tile#Tile(String, String, Node)}.
     */
    public Message(@Nullable String title,
                   @Nullable String description,
                   @Nullable Node graphic) {
        super(title, description, graphic);
        getStyleClass().add("message");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MessageSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The property representing the message’s action handler. Setting an action handler
     * makes the message interactive or clickable. When a user clicks on the interactive
     * message, the specified action handler will be called.
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

    /**
     * The property representing the user specified close handler.
     */
    protected final ObjectProperty<EventHandler<? super Event>> onClose =
        new SimpleObjectProperty<>(this, "onClose");

    public EventHandler<? super Event> getOnClose() {
        return onClose.get();
    }

    public ObjectProperty<EventHandler<? super Event>> onCloseProperty() {
        return onClose;
    }

    public void setOnClose(EventHandler<? super Event> onClose) {
        this.onClose.set(onClose);
    }
}
