/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.Nullable;

/**
 * The Notification control is intended for displaying alerts and messages
 * to users as pop-ups. It's customizable with different colors and icons,
 * can contain a graphic or image, along with the message and additional actions
 * for users to take. The purpose of this control is to immediately notify users
 * of significant events and provide them with quick access to related actions without
 * interrupting their workflow.
 */
public class Notification extends Control {

    public Notification() {
        this(null, null);
    }

    public Notification(@Nullable @NamedArg("message") String message) {
        this(message, null);
    }

    public Notification(@Nullable @NamedArg("message") String message,
                        @Nullable @NamedArg("graphic") Node graphic) {
        super();

        setMessage(message);
        setGraphic(graphic);

        // set reasonable default width
        setPrefWidth(400);
        setMaxWidth(Region.USE_PREF_SIZE);

        getStyleClass().add("notification");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NotificationSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents an optional graphical component that can be displayed alongside
     * the notification message.
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
     * Stores a short text message that will be displayed to users when the
     * notification appears. This property doesn't support the formatted text.
     */
    private final StringProperty message = new SimpleStringProperty(this, "message");

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    /**
     * Specifies the primary actions associated with this notification.
     *
     * <p>This property is used to store one or more action buttons that will
     * be displayed at the bottom of the notification when it appears.
     */
    private final ReadOnlyObjectWrapper<ObservableList<Button>> primaryActions =
        new ReadOnlyObjectWrapper<>(this, "primaryActions", FXCollections.observableArrayList());

    public ObservableList<Button> getPrimaryActions() {
        return primaryActions.get();
    }

    public ReadOnlyObjectProperty<ObservableList<Button>> primaryActionsProperty() {
        return primaryActions.getReadOnlyProperty();
    }

    public void setPrimaryActions(ObservableList<Button> buttons) {
        this.primaryActions.set(buttons);
    }

    public void setPrimaryActions(Button... buttons) {
        getPrimaryActions().setAll(buttons);
    }

    /**
     * Specifies the secondary actions associated with this notification.
     *
     * <p>This property is used to store one or more menu items that will be displayed
     * as a dropdown menu at the top corner of the notification when it appears.
     */
    private final ReadOnlyObjectWrapper<ObservableList<MenuItem>> secondaryActions =
        new ReadOnlyObjectWrapper<>(this, "secondaryActions", FXCollections.observableArrayList());

    public ObservableList<MenuItem> getSecondaryActions() {
        return secondaryActions.get();
    }

    public ReadOnlyObjectProperty<ObservableList<MenuItem>> secondaryActionsProperty() {
        return secondaryActions.getReadOnlyProperty();
    }

    public void setSecondaryActions(ObservableList<MenuItem> items) {
        this.secondaryActions.set(items);
    }

    public void setSecondaryActions(MenuItem... items) {
        getSecondaryActions().setAll(items);
    }

    /**
     * Specifies the close handler used to dismiss this notification.
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
