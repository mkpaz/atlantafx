/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Default skin for the {@link Chip} control.
 *
 * <p>Layout: [graphic] [label] [close-button]
 * The close button is only visible when {@code onClose} handler is set.
 */
public class ChipSkin extends SkinBase<Chip> {

    private static final PseudoClass HAS_GRAPHIC =
            PseudoClass.getPseudoClass("has-graphic");

    protected final HBox container = new HBox();
    protected final StackPane graphicSlot = new StackPane();
    protected final Label label = new Label();
    protected final StackPane closeButton = new StackPane();
    protected final StackPane closeButtonIcon = new StackPane();

    protected ChipSkin(Chip control) {
        super(control);

        // graphic slot
        graphicSlot.getStyleClass().add("graphic");
        graphicSlot.setVisible(control.getGraphic() != null);
        graphicSlot.setManaged(control.getGraphic() != null);

        // label
        label.getStyleClass().add("text");
        label.textProperty().bind(control.textProperty());

        // close button
        closeButton.getStyleClass().add("close-button");
        closeButtonIcon.getStyleClass().add("icon");
        closeButton.getChildren().setAll(closeButtonIcon);
        closeButton.setOnMouseClicked(e -> handleClose());

        boolean hasClose = control.getOnClose() != null;
        closeButton.setVisible(hasClose);
        closeButton.setManaged(hasClose);

        // container
        container.getStyleClass().add("container");
        container.getChildren().addAll(graphicSlot, label, closeButton);
        getChildren().setAll(container);

        // initial pseudo-class state
        control.pseudoClassStateChanged(HAS_GRAPHIC, control.getGraphic() != null);

        // listeners
        registerChangeListener(control.graphicProperty(), o -> {
            var graphic = getSkinnable().getGraphic();
            if (graphic != null) {
                graphicSlot.getChildren().setAll(graphic);
            } else {
                graphicSlot.getChildren().clear();
            }
            graphicSlot.setVisible(graphic != null);
            graphicSlot.setManaged(graphic != null);
            getSkinnable().pseudoClassStateChanged(HAS_GRAPHIC, graphic != null);
        });

        registerChangeListener(control.onCloseProperty(), o -> {
            boolean has = getSkinnable().getOnClose() != null;
            closeButton.setVisible(has);
            closeButton.setManaged(has);
        });
    }

    protected void handleClose() {
        if (getSkinnable().getOnClose() != null) {
            getSkinnable().getOnClose().handle(new Event(Event.ANY));
        }
    }

    @Override
    public void dispose() {
        label.textProperty().unbind();
        unregisterChangeListeners(getSkinnable().graphicProperty());
        unregisterChangeListeners(getSkinnable().onCloseProperty());
        super.dispose();
    }
}
