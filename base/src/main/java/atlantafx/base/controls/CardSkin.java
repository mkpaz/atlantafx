/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CardSkin implements Skin<Card> {

    protected final Card control;
    protected final VBox root = new VBox();

    protected final StackPane headerSlot;
    protected final ChangeListener<Node> headerSlotListener;

    protected final StackPane subHeaderSlot;
    protected final ChangeListener<Node> subHeaderSlotListener;

    protected final StackPane bodySlot;
    protected final ChangeListener<Node> bodySlotListener;

    protected final StackPane footerSlot;
    protected final ChangeListener<Node> footerSlotListener;

    protected CardSkin(Card control) {
        this.control = control;

        headerSlot = new StackPane();
        headerSlot.getStyleClass().add("header");
        headerSlotListener = new SlotListener(headerSlot);
        control.headerProperty().addListener(headerSlotListener);
        headerSlotListener.changed(control.headerProperty(), null, control.getHeader());

        subHeaderSlot = new StackPane();
        subHeaderSlot.getStyleClass().add("sub-header");
        subHeaderSlotListener = new SlotListener(subHeaderSlot);
        control.subHeaderProperty().addListener(subHeaderSlotListener);
        subHeaderSlotListener.changed(control.subHeaderProperty(), null, control.getSubHeader());

        bodySlot = new StackPane();
        bodySlot.getStyleClass().add("body");
        VBox.setVgrow(bodySlot, Priority.ALWAYS);
        bodySlotListener = new SlotListener(bodySlot);
        control.bodyProperty().addListener(bodySlotListener);
        bodySlotListener.changed(control.bodyProperty(), null, control.getBody());

        footerSlot = new StackPane();
        footerSlot.getStyleClass().add("footer");
        footerSlotListener = new SlotListener(footerSlot);
        control.footerProperty().addListener(footerSlotListener);
        footerSlotListener.changed(control.footerProperty(), null, control.getFooter());

        root.getStyleClass().add("card");
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
