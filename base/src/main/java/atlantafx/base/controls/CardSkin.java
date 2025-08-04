/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

/**
 * The default skin for the {@link Card} control.
 */
public class CardSkin implements Skin<Card> {

    protected static final PseudoClass HAS_HEADER = PseudoClass.getPseudoClass("has-header");
    protected static final PseudoClass HAS_SUBHEADER = PseudoClass.getPseudoClass("has-subheader");
    protected static final PseudoClass HAS_BODY = PseudoClass.getPseudoClass("has-body");
    protected static final PseudoClass HAS_FOOTER = PseudoClass.getPseudoClass("has-footer");
    protected static final PseudoClass HAS_IMAGE = PseudoClass.getPseudoClass("has-image");

    protected final Card control;
    protected final VBox root = new VBox();

    protected final StackPane headerSlot;
    protected final ChangeListener<@Nullable Node> headerSlotListener;

    protected final StackPane subHeaderSlot;
    protected final ChangeListener<@Nullable Node> subHeaderSlotListener;

    protected final StackPane bodySlot;
    protected final ChangeListener<@Nullable Node> bodySlotListener;

    protected final StackPane footerSlot;
    protected final ChangeListener<@Nullable Node> footerSlotListener;

    protected CardSkin(Card control) {
        this.control = control;

        headerSlot = new StackPane();
        headerSlot.getStyleClass().add("header");
        headerSlotListener = new SlotListener(
            headerSlot, (n, active) -> getSkinnable().pseudoClassStateChanged(HAS_HEADER, active)
        );
        control.headerProperty().addListener(headerSlotListener);
        headerSlotListener.changed(control.headerProperty(), null, control.getHeader());

        subHeaderSlot = new StackPane();
        subHeaderSlot.getStyleClass().add("sub-header");
        subHeaderSlotListener = new SlotListener(
            subHeaderSlot,
            (n, active) -> {
                getSkinnable().pseudoClassStateChanged(HAS_SUBHEADER, active);
                getSkinnable().pseudoClassStateChanged(HAS_IMAGE, n instanceof ImageView);
            }
        );
        control.subHeaderProperty().addListener(subHeaderSlotListener);
        subHeaderSlotListener.changed(control.subHeaderProperty(), null, control.getSubHeader());

        bodySlot = new StackPane();
        bodySlot.getStyleClass().add("body");
        VBox.setVgrow(bodySlot, Priority.ALWAYS);
        bodySlotListener = new SlotListener(
            bodySlot, (n, active) -> getSkinnable().pseudoClassStateChanged(HAS_BODY, active)
        );
        control.bodyProperty().addListener(bodySlotListener);
        bodySlotListener.changed(control.bodyProperty(), null, control.getBody());

        footerSlot = new StackPane();
        footerSlot.getStyleClass().add("footer");
        footerSlotListener = new SlotListener(
            footerSlot, (n, active) -> getSkinnable().pseudoClassStateChanged(HAS_FOOTER, active)
        );
        control.footerProperty().addListener(footerSlotListener);
        footerSlotListener.changed(control.footerProperty(), null, control.getFooter());

        root.getStyleClass().add("container");
        root.getChildren().setAll(headerSlot, subHeaderSlot, bodySlot, footerSlot);
    }

    @Override
    public Card getSkinnable() {
        return control;
    }

    @Override
    public Node getNode() {
        return root;
    }

    @Override
    public void dispose() {
        control.headerProperty().removeListener(headerSlotListener);
        control.subHeaderProperty().removeListener(subHeaderSlotListener);
        control.bodyProperty().removeListener(bodySlotListener);
        control.footerProperty().removeListener(footerSlotListener);
    }
}
