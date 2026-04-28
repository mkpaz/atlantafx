/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

/**
 * The default skin for the {@link Toast} control.
 *
 * <p>The skin consists of a horizontal layout: message text on the left and an
 * optional close button on the right. When an {@code onClose} handler is set on
 * the control, the close button becomes visible. The toast auto-hides after the
 * duration specified by {@link Toast#durationProperty()}, using a fade-out animation.
 */
public class ToastSkin extends SkinBase<Toast> {

    protected final HBox container = new HBox();
    protected final TextFlow messageText = new TextFlow();
    protected final StackPane closeButton = new StackPane();
    protected final StackPane closeButtonIcon = new StackPane();

    private @Nullable PauseTransition autoHideTimer;
    private @Nullable Animation fadeOutAnimation;

    protected ToastSkin(Toast control) {
        super(control);

        // == MESSAGE ==

        messageText.getStyleClass().add("message");
        HBox.setHgrow(messageText, Priority.ALWAYS);
        messageText.setMaxWidth(Double.MAX_VALUE);
        messageText.setMinHeight(Region.USE_PREF_SIZE);

        setMessageText();
        registerChangeListener(control.messageProperty(), o -> setMessageText());

        // == CLOSE BUTTON ==

        closeButton.getStyleClass().add("close-button");
        closeButton.getChildren().setAll(closeButtonIcon);
        closeButton.setOnMouseClicked(e -> handleClose());
        closeButton.setVisible(control.getOnClose() != null);
        closeButton.setManaged(control.getOnClose() != null);
        closeButtonIcon.getStyleClass().add("icon");

        registerChangeListener(control.onCloseProperty(), o -> {
            closeButton.setVisible(getSkinnable().getOnClose() != null);
            closeButton.setManaged(getSkinnable().getOnClose() != null);
        });

        // == CONTAINER ==

        container.getStyleClass().add("container");
        container.getChildren().setAll(messageText, closeButton);
        getChildren().setAll(container);

        // == AUTO-HIDE ==

        registerChangeListener(control.durationProperty(), o -> restartAutoHideTimer());
        if (control.getDuration() != null && control.getDuration() != Duration.INDEFINITE) {
            startAutoHideTimer(control.getDuration());
        }
    }

    protected void setMessageText() {
        messageText.getChildren().clear();
        if (getSkinnable().getMessage() != null && !getSkinnable().getMessage().isBlank()) {
            messageText.getChildren().setAll(new Text(getSkinnable().getMessage()));
        }
    }

    protected void handleClose() {
        if (getSkinnable().getOnClose() != null) {
            getSkinnable().getOnClose().handle(new Event(Event.ANY));
        }
    }

    private void startAutoHideTimer(Duration duration) {
        stopAutoHideTimer();
        autoHideTimer = new PauseTransition(duration);
        autoHideTimer.setOnFinished(e -> fadeOutAndClose());
        autoHideTimer.play();
    }

    private void restartAutoHideTimer() {
        Duration dur = getSkinnable().getDuration();
        if (dur != null && dur != Duration.INDEFINITE) {
            startAutoHideTimer(dur);
        } else {
            stopAutoHideTimer();
        }
    }

    private void stopAutoHideTimer() {
        if (autoHideTimer != null) {
            autoHideTimer.stop();
            autoHideTimer = null;
        }
    }

    private void fadeOutAndClose() {
        stopAutoHideTimer();
        fadeOutAnimation = atlantafx.base.util.Animations.fadeOut(getSkinnable(), Duration.millis(300));
        fadeOutAnimation.setOnFinished(e -> handleClose());
        fadeOutAnimation.play();
    }

    @Override
    public void dispose() {
        stopAutoHideTimer();
        if (fadeOutAnimation != null) {
            fadeOutAnimation.stop();
            fadeOutAnimation = null;
        }
        unregisterChangeListeners(getSkinnable().messageProperty());
        unregisterChangeListeners(getSkinnable().onCloseProperty());
        unregisterChangeListeners(getSkinnable().durationProperty());
        super.dispose();
    }
}
