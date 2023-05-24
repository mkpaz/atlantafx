/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.theme.Styles;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TileSkin extends SkinBase<Tile> {

    private static final PseudoClass FILLED = PseudoClass.getPseudoClass("filled");

    protected final HBox root = new HBox();
    protected final StackPane graphicSlot;
    protected final ChangeListener<Node> graphicSlotListener;
    protected final VBox headerBox;
    protected final Label titleLbl;
    protected final Label subTitleLbl;
    protected final StackPane actionSlot;
    protected final ChangeListener<Node> actionSlotListener;

    public TileSkin(Tile control) {
        super(control);

        graphicSlot = new StackPane();
        graphicSlot.getStyleClass().add("graphic");
        graphicSlotListener = new SlotListener(graphicSlot);
        control.graphicProperty().addListener(graphicSlotListener);
        graphicSlotListener.changed(control.graphicProperty(), null, control.getGraphic());

        titleLbl = new Label(control.getTitle());
        titleLbl.getStyleClass().add("title");
        titleLbl.textProperty().bind(control.titleProperty());

        subTitleLbl = new Label(control.getSubTitle());
        subTitleLbl.setWrapText(true);
        subTitleLbl.getStyleClass().add("subtitle");
        subTitleLbl.setVisible(control.getSubTitle() != null);
        subTitleLbl.setManaged(control.getSubTitle() != null);

        headerBox = new VBox(titleLbl, subTitleLbl);
        headerBox.setFillWidth(true);
        headerBox.getStyleClass().add("header");
        HBox.setHgrow(headerBox, Priority.ALWAYS);
        headerBox.setMinHeight(Region.USE_COMPUTED_SIZE);
        headerBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        headerBox.setMaxHeight(Region.USE_COMPUTED_SIZE);
        headerBox.pseudoClassStateChanged(FILLED, control.getSubTitle() != null);

        registerChangeListener(control.subTitleProperty(), o -> {
            var value = getSkinnable().getSubTitle();
            subTitleLbl.setText(value);
            subTitleLbl.setVisible(value != null);
            subTitleLbl.setManaged(value != null);

            // header is considered to be “filled” when a subtitle is set
            // because a tile without a title is nonsense
            headerBox.pseudoClassStateChanged(FILLED, value != null);
        });

        actionSlot = new StackPane();
        actionSlot.getStyleClass().add("action");
        actionSlotListener = new SlotListener(actionSlot);
        control.actionProperty().addListener(actionSlotListener);
        actionSlotListener.changed(control.actionProperty(), null, control.getAction());

        // use pref size for slots, or they will be resized
        // to the bare minimum due to Priority.ALWAYS
        graphicSlot.setMinWidth(Region.USE_PREF_SIZE);
        actionSlot.setMinWidth(Region.USE_PREF_SIZE);

        // label text wrapping inside VBox won't work without this
        subTitleLbl.setMaxWidth(Region.USE_PREF_SIZE);
        subTitleLbl.setMinHeight(Region.USE_PREF_SIZE);

        // do not resize children or container won't restore
        // to its original size after expanding
        root.setFillHeight(false);

        root.getStyleClass().add("tile");
        root.getChildren().setAll(graphicSlot, headerBox, actionSlot);

        root.pseudoClassStateChanged(Styles.STATE_INTERACTIVE, control.getActionHandler() != null);
        registerChangeListener(
            control.actionHandlerProperty(),
            o -> root.pseudoClassStateChanged(Styles.STATE_INTERACTIVE, getSkinnable().getActionHandler() != null)
        );
        root.setOnMouseClicked(e -> {
            if (control.getActionHandler() != null) {
                control.getActionHandler().run();
            }
        });

        getChildren().setAll(root);
    }

    protected double calcHeight() {
        var headerHeight = headerBox.getSpacing()
            + headerBox.getInsets().getTop()
            + headerBox.getInsets().getBottom()
            + titleLbl.getBoundsInLocal().getHeight()
            + (subTitleLbl.isManaged() ? subTitleLbl.getBoundsInLocal().getHeight() : 0);

        return Math.max(Math.max(graphicSlot.getHeight(), actionSlot.getHeight()), headerHeight)
            + root.getPadding().getTop()
            + root.getPadding().getBottom();
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        // change the control height when label changed its height due to text wrapping,
        // no other way to do that, all JavaFX containers completely ignore _the actual_
        // height of its children
        return calcHeight();
    }

    @Override
    public void dispose() {
        titleLbl.textProperty().unbind();
        unregisterChangeListeners(getSkinnable().subTitleProperty());
        getSkinnable().graphicProperty().removeListener(graphicSlotListener);
        getSkinnable().actionProperty().removeListener(actionSlotListener);
        unregisterChangeListeners(getSkinnable().actionHandlerProperty());

        super.dispose();
    }
}
