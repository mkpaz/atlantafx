/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import org.jspecify.annotations.Nullable;

/**
 * A control for displaying banners or alerts that is specifically
 * designed to grab the user’s attention. It is based on the {@link Tile}
 * layout and shares its structure.
 */
public class Message extends TileBase {

    /**
     * Creates an empty Message.
     */
    public Message() {
        this(null, null, null);
    }

    /**
     * Creates a new Message with an initial title and description.
     *
     * @param title       A string for the title.
     * @param description A string for the description.
     */
    public Message(@Nullable @NamedArg("title") String title,
                   @Nullable @NamedArg("description") String description) {
        this(title, description, null);
    }

    /**
     * Creates a new Message with an initial title, description and graphic.
     *
     * @param title       A string for the title.
     * @param description A string for the description.
     * @param graphic     A graphic or icon.
     */
    public Message(@Nullable String title,
                   @Nullable String description,
                   @Nullable Node graphic) {
        super(title, description, graphic);
        getStyleClass().add("message");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MessageSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents the message’s action handler. Setting an action handler makes the
     * message interactive (or clickable). When a user clicks on the interactive
     * message, the specified action handler will be called.
     */
    public ObjectProperty<@Nullable Runnable> actionHandlerProperty() {
        return actionHandler;
    }

    private final ObjectProperty<@Nullable Runnable> actionHandler = new SimpleObjectProperty<>(this, "actionHandler");

    public @Nullable Runnable getActionHandler() {
        return actionHandler.get();
    }

    public void setActionHandler(@Nullable Runnable actionHandler) {
        this.actionHandler.set(actionHandler);
    }

    /**
     * Represents the user-specified close handler, which is intended to be used to close
     * or dismiss the message. When a user clicks on the message's close button, the specified
     * close handler will be called.
     */
    public ObjectProperty<@Nullable EventHandler<? super Event>> onCloseProperty() {
        return onClose;
    }

    protected final ObjectProperty<@Nullable EventHandler<? super Event>> onClose =
        new SimpleObjectProperty<>(this, "onClose");

    public @Nullable EventHandler<? super Event> getOnClose() {
        return onClose.get();
    }

    public void setOnClose(@Nullable EventHandler<? super Event> onClose) {
        this.onClose.set(onClose);
    }
}
