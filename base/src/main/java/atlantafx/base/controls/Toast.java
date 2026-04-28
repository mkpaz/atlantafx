/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

/**
 * A lightweight, transient message that appears briefly to provide feedback
 * about an operation. Unlike {@link Notification}, Toast is minimal — just
 * a message and an optional close button.
 *
 * <p>Toast is designed to be placed in a container (e.g. VBox) for stacking
 * multiple instances. There is no manager class; lifecycle is managed by the
 * user or the parent container.
 *
 * <p>Color variants are set via external style classes:
 * <pre>{@code
 * toast.getStyleClass().add(Styles.SUCCESS);
 * }</pre>
 *
 * <p>Example:
 * <pre>{@code
 * var toast = new Toast("File saved successfully.");
 * toast.setOnClose(e -> toast.removeFromParent());
 * toast.setDuration(Duration.seconds(3));
 *
 * var container = new VBox(toast);
 * }</pre>
 */
public class Toast extends Control {

    /**
     * The default duration before the toast auto-hides.
     */
    public static final Duration DEFAULT_DURATION = Duration.seconds(5);

    /**
     * Creates an empty Toast.
     */
    public Toast() {
        this(null);
    }

    /**
     * Creates a Toast with initial message text.
     *
     * @param message A string for the toast message.
     */
    public Toast(@Nullable @NamedArg("message") String message) {
        super();
        setMessage(message);
        setDuration(DEFAULT_DURATION);
        setPrefWidth(400);
        setMaxWidth(Region.USE_PREF_SIZE);
        getStyleClass().add("toast");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ToastSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The text message to display in the toast.
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
     * The duration after which the toast will automatically hide.
     * When set to {@code null} or {@link Duration#INDEFINITE}, the toast
     * will not auto-hide.
     */
    public ObjectProperty<@Nullable Duration> durationProperty() {
        return duration;
    }

    private final ObjectProperty<@Nullable Duration> duration =
        new SimpleObjectProperty<>(this, "duration", DEFAULT_DURATION);

    public @Nullable Duration getDuration() {
        return duration.get();
    }

    public void setDuration(@Nullable Duration duration) {
        this.duration.set(duration);
    }

    /**
     * Specifies the close handler used to dismiss this toast.
     *
     * <p>The close button will not appear if the handler is not set.
     */
    public ObjectProperty<@Nullable EventHandler<? super Event>> onCloseProperty() {
        return onClose;
    }

    private final ObjectProperty<@Nullable EventHandler<? super Event>> onClose =
        new SimpleObjectProperty<>(this, "onClose");

    public @Nullable EventHandler<? super Event> getOnClose() {
        return onClose.get();
    }

    public void setOnClose(@Nullable EventHandler<? super Event> onClose) {
        this.onClose.set(onClose);
    }
}
