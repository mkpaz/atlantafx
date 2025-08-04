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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

/**
 * A control that is intended for displaying notifications to users as pop-ups.
 * It is customizable with different colors and icons, can contain a graphic or image,
 * along with the message and additional actions for users to take.
 */
public class Notification extends Control {

    /**
     * Creates an empty Notification.
     */
    public Notification() {
        this(null, null);
    }

    /**
     * Creates a Notification with initial message text.
     *
     * @param message A string for the notification message.
     */
    public Notification(@Nullable @NamedArg("message") String message) {
        this(message, null);
    }

    /**
     * Creates a Notification with initial message text and graphic.
     *
     * @param message A string for the notification message.
     * @param graphic A graphic or icon.
     */
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
    public ObjectProperty<@Nullable Node> graphicProperty() {
        return graphic;
    }

    private final ObjectProperty<@Nullable Node> graphic = new SimpleObjectProperty<>(this, "graphic");

    public @Nullable Node getGraphic() {
        return graphic.get();
    }

    public void setGraphic(@Nullable Node graphic) {
        this.graphic.set(graphic);
    }

    /**
     * Stores a short text message that will be displayed to users when the
     * notification appears. This property doesn't support the formatted text.
     */
    public StringProperty messageProperty() {
        return message;
    }

    private final StringProperty message = new SimpleStringProperty(this, "message");

    public @Nullable String getMessage() {
        return message.get();
    }

    public void setMessage(@Nullable String message) {
        this.message.set(message);
    }

    /**
     * Specifies the primary actions associated with this notification.
     *
     * <p>This property is used to store one or more action buttons that will
     * be displayed at the bottom of the notification when it appears. These
     * buttons will be placed inside the {@link ButtonBar} and use the alignment
     * that is described in the ButtonBar documentation.
     */
    public ReadOnlyObjectProperty<ObservableList<Button>> primaryActionsProperty() {
        return primaryActions.getReadOnlyProperty();
    }

    private final ReadOnlyObjectWrapper<ObservableList<Button>> primaryActions =
        new ReadOnlyObjectWrapper<>(this, "primaryActions", FXCollections.observableArrayList());

    public ObservableList<Button> getPrimaryActions() {
        return primaryActions.get();
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
     *
     * <p>The dropdown menu button will not appear if the list is empty.
     */
    public ReadOnlyObjectProperty<ObservableList<MenuItem>> secondaryActionsProperty() {
        return secondaryActions.getReadOnlyProperty();
    }

    private final ReadOnlyObjectWrapper<ObservableList<MenuItem>> secondaryActions =
        new ReadOnlyObjectWrapper<>(this, "secondaryActions", FXCollections.observableArrayList());

    public ObservableList<MenuItem> getSecondaryActions() {
        return secondaryActions.get();
    }

    public void setSecondaryActions(ObservableList<MenuItem> items) {
        this.secondaryActions.set(items);
    }

    public void setSecondaryActions(MenuItem... items) {
        getSecondaryActions().setAll(items);
    }

    /**
     * Specifies the close handler used to dismiss this notification.
     *
     * <p>The close button will not appear if the handler is not set for it.
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
