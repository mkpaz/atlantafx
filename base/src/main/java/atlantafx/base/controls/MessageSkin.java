/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.theme.Styles;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.StackPane;

/**
 * The default skin for the {@link Message} control.
 */
public class MessageSkin extends TileSkinBase<Message> {

    protected static final PseudoClass CLOSEABLE = PseudoClass.getPseudoClass("closeable");

    protected final StackPane closeButton = new StackPane();
    protected final StackPane closeButtonIcon = new StackPane();

    public MessageSkin(Message control) {
        super(control);

        // ACTION

        pseudoClassStateChanged(Styles.STATE_INTERACTIVE, control.getActionHandler() != null);
        registerChangeListener(
            control.actionHandlerProperty(),
            o -> pseudoClassStateChanged(Styles.STATE_INTERACTIVE, getSkinnable().getActionHandler() != null)
        );

        container.setOnMouseClicked(e -> {
            if (getSkinnable().getActionHandler() != null) {
                getSkinnable().getActionHandler().run();
            }
        });

        // CLOSE BUTTON

        closeButton.getStyleClass().add("close-button");
        closeButton.getChildren().setAll(closeButtonIcon);
        closeButton.setOnMouseClicked(e -> handleClose());
        closeButton.setVisible(control.getOnClose() != null);
        closeButton.setManaged(control.getOnClose() != null);

        closeButtonIcon.getStyleClass().add("icon");
        getChildren().add(closeButton);

        pseudoClassStateChanged(CLOSEABLE, control.getOnClose() != null);
        registerChangeListener(control.onCloseProperty(), o -> {
            closeButton.setVisible(getSkinnable().getOnClose() != null);
            closeButton.setManaged(getSkinnable().getOnClose() != null);
            pseudoClassStateChanged(CLOSEABLE, getSkinnable().getOnClose() != null);
        });
    }

    protected void handleClose() {
        if (getSkinnable().getOnClose() != null) {
            getSkinnable().getOnClose().handle(new Event(Event.ANY));
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        if (closeButton.isManaged()) {
            var lb = closeButton.getLayoutBounds();
            layoutInArea(closeButton, w - lb.getWidth() - 5, 5, lb.getWidth(), lb.getHeight(), -1, HPos.RIGHT,
                VPos.TOP);
        }
        layoutInArea(container, x, y, w, h, -1, HPos.CENTER, VPos.CENTER);
    }

    @Override
    public void dispose() {
        unregisterChangeListeners(getSkinnable().actionHandlerProperty());
        unregisterChangeListeners(getSkinnable().onCloseProperty());

        super.dispose();
    }
}
