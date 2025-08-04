/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jspecify.annotations.Nullable;

/**
 * The default skin for the {@link Notification} control.
 */
public class NotificationSkin extends SkinBase<Notification> {

    protected final VBox container = new VBox();
    protected final HBox header = new HBox();

    protected final StackPane graphicSlot = new StackPane();
    protected final ChangeListener<@Nullable Node> graphicSlotListener = new SlotListener(graphicSlot);
    protected final TextFlow messageText = new TextFlow();

    protected final StackPane closeButton = new StackPane();
    protected final StackPane closeButtonIcon = new StackPane();
    protected final StackPane menuButton = new StackPane();
    protected final StackPane menuButtonIcon = new StackPane();
    protected final ContextMenu actionsMenu = new ContextMenu();
    protected final HBox actionsBox = new HBox();

    protected final ButtonBar buttonBar = new ButtonBar();

    protected NotificationSkin(Notification control) {
        super(control);

        // == GRAPHIC ==

        graphicSlot.getStyleClass().add("graphic");
        control.graphicProperty().addListener(graphicSlotListener);
        graphicSlotListener.changed(control.graphicProperty(), null, control.getGraphic());

        // == MESSAGE ==

        messageText.getStyleClass().add("message");
        HBox.setHgrow(messageText, Priority.ALWAYS);

        setMessageText();
        registerChangeListener(control.messageProperty(), o -> setMessageText());

        // text wrapping won't work without this
        messageText.setMaxWidth(Double.MAX_VALUE);
        messageText.setMinHeight(Region.USE_PREF_SIZE);

        // == TOP BUTTONS ==

        menuButton.getStyleClass().add("secondary-menu-button");
        menuButton.getChildren().setAll(menuButtonIcon);
        menuButton.setOnMouseClicked(e -> actionsMenu.show(
            menuButton,
            menuButton.localToScreen(menuButton.getLayoutBounds()).getMinX(),
            menuButton.localToScreen(menuButton.getLayoutBounds()).getMaxY()
        ));
        menuButton.setVisible(!getSkinnable().getSecondaryActions().isEmpty());
        menuButton.setManaged(!getSkinnable().getSecondaryActions().isEmpty());
        menuButtonIcon.getStyleClass().add("icon");

        Bindings.bindContent(actionsMenu.getItems(), getSkinnable().getSecondaryActions());
        registerListChangeListener(actionsMenu.getItems(), o -> {
            menuButton.setVisible(!getSkinnable().getSecondaryActions().isEmpty());
            menuButton.setManaged(!getSkinnable().getSecondaryActions().isEmpty());
        });

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

        actionsBox.getStyleClass().add("actions");
        actionsBox.getChildren().setAll(menuButton, closeButton);
        actionsBox.setFillHeight(false);
        HBox.setMargin(actionsBox, new Insets(-8, -8, 0, 0));

        // == HEADER ==

        // use pref size for slots, or they will be resized
        // to the bare minimum due to Priority.ALWAYS
        graphicSlot.setMinWidth(Region.USE_PREF_SIZE);
        actionsBox.setMinWidth(Region.USE_PREF_SIZE);

        // do not resize children or container won't restore
        // to its original size after expanding
        header.setFillHeight(false);
        header.getStyleClass().add("header");
        header.getChildren().setAll(graphicSlot, messageText, actionsBox);
        header.setAlignment(Pos.TOP_LEFT);

        // == BUTTON BAR ==

        buttonBar.getStyleClass().add("button-bar");
        buttonBar.setVisible(!getSkinnable().getPrimaryActions().isEmpty());
        buttonBar.setManaged(!getSkinnable().getPrimaryActions().isEmpty());

        Bindings.bindContent(buttonBar.getButtons(), getSkinnable().getPrimaryActions());
        registerListChangeListener(buttonBar.getButtons(), o -> {
            buttonBar.setVisible(!getSkinnable().getPrimaryActions().isEmpty());
            buttonBar.setManaged(!getSkinnable().getPrimaryActions().isEmpty());
        });

        // == CONTAINER ==

        container.getChildren().setAll(header, buttonBar);
        container.getStyleClass().add("container");
        getChildren().setAll(container);
    }

    protected void setMessageText() {
        if (!messageText.getChildren().isEmpty()) {
            messageText.getChildren().clear();
        }
        if (getSkinnable().getMessage() != null && !getSkinnable().getMessage().isBlank()) {
            messageText.getChildren().setAll(new Text(getSkinnable().getMessage()));
        }
    }

    protected void handleClose() {
        if (getSkinnable().getOnClose() != null) {
            getSkinnable().getOnClose().handle(new Event(Event.ANY));
        }
    }

    protected double calcHeight() {
        var messageHeight = messageText.getBoundsInLocal().getHeight();

        return Math.max(Math.max(graphicSlot.getHeight(), actionsBox.getHeight()), messageHeight)
            + (buttonBar.isManaged() ? buttonBar.getHeight() + container.getSpacing() : 0)
            + header.getPadding().getTop()
            + header.getPadding().getBottom()
            + container.getPadding().getTop()
            + container.getPadding().getBottom();
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return calcHeight();
    }

    @Override
    public void dispose() {
        Bindings.unbindContent(actionsMenu.getItems(), getSkinnable().getSecondaryActions());
        unregisterListChangeListeners(actionsMenu.getItems());

        Bindings.unbindContent(buttonBar.getButtons(), getSkinnable().getPrimaryActions());
        unregisterListChangeListeners(buttonBar.getButtons());

        getSkinnable().graphicProperty().removeListener(graphicSlotListener);
        unregisterChangeListeners(getSkinnable().messageProperty());
        unregisterChangeListeners(getSkinnable().onCloseProperty());

        super.dispose();
    }
}
