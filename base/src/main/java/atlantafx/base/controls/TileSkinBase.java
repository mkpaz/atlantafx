/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.util.BBCodeParser;
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
import javafx.scene.text.TextFlow;
import org.jspecify.annotations.Nullable;

/**
 * A common skin for implementing tile-based controls, specifically the
 * {@link MessageSkin} and the {@link TileSkin}.
 */
public abstract class TileSkinBase<T extends TileBase> extends SkinBase<T> {

    protected static final PseudoClass HAS_GRAPHIC = PseudoClass.getPseudoClass("has-graphic");
    protected static final PseudoClass HAS_TITLE = PseudoClass.getPseudoClass("has-title");
    protected static final PseudoClass HAS_DESCRIPTION = PseudoClass.getPseudoClass("has-description");
    protected static final PseudoClass HAS_ACTION = PseudoClass.getPseudoClass("has-action");

    protected final HBox container = new HBox();
    protected final StackPane graphicSlot;
    protected final ChangeListener<@Nullable Node> graphicSlotListener;
    protected final VBox headerBox;
    protected final Label titleLbl;
    protected final TextFlow descriptionText;
    protected final StackPane actionSlot;
    protected final ChangeListener<@Nullable Node> actionSlotListener;

    public TileSkinBase(T control) {
        super(control);

        graphicSlot = new StackPane();
        graphicSlot.getStyleClass().add("graphic");
        graphicSlotListener = new SlotListener(
            graphicSlot, (n, active) -> getSkinnable().pseudoClassStateChanged(HAS_GRAPHIC, active)
        );
        control.graphicProperty().addListener(graphicSlotListener);
        graphicSlotListener.changed(control.graphicProperty(), null, control.getGraphic());

        titleLbl = new Label(control.getTitle());
        titleLbl.getStyleClass().add("title");
        titleLbl.setVisible(control.getTitle() != null);
        titleLbl.setManaged(control.getTitle() != null);

        descriptionText = new TextFlow();
        descriptionText.getStyleClass().add("description");
        descriptionText.setVisible(control.getDescription() != null);
        descriptionText.setManaged(control.getDescription() != null);
        setDescriptionText();

        headerBox = new VBox(titleLbl, descriptionText);
        headerBox.setFillWidth(true);
        headerBox.getStyleClass().add("header");
        HBox.setHgrow(headerBox, Priority.ALWAYS);
        headerBox.setMinHeight(Region.USE_COMPUTED_SIZE);
        headerBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        headerBox.setMaxHeight(Region.USE_COMPUTED_SIZE);

        control.pseudoClassStateChanged(HAS_TITLE, control.getTitle() != null);
        registerChangeListener(control.titleProperty(), o -> {
            var value = getSkinnable().getTitle();
            titleLbl.setText(value);
            titleLbl.setVisible(value != null);
            titleLbl.setManaged(value != null);
            getSkinnable().pseudoClassStateChanged(HAS_TITLE, value != null);
        });

        control.pseudoClassStateChanged(HAS_DESCRIPTION, control.getDescription() != null);
        registerChangeListener(control.descriptionProperty(), o -> {
            var value = getSkinnable().getDescription();
            setDescriptionText();
            descriptionText.setVisible(value != null);
            descriptionText.setManaged(value != null);
            getSkinnable().pseudoClassStateChanged(HAS_DESCRIPTION, value != null);
        });

        actionSlot = new StackPane();
        actionSlot.getStyleClass().add("action");
        actionSlotListener = new SlotListener(
            actionSlot, (n, active) -> getSkinnable().pseudoClassStateChanged(HAS_ACTION, active)
        );

        // use pref size for slots, or they will be resized
        // to the bare minimum due to Priority.ALWAYS
        graphicSlot.setMinWidth(Region.USE_PREF_SIZE);
        actionSlot.setMinWidth(Region.USE_PREF_SIZE);

        // text wrapping inside VBox won't work without this
        descriptionText.setMaxWidth(Region.USE_PREF_SIZE);
        descriptionText.setMinHeight(Region.USE_PREF_SIZE);

        // do not resize children or container won't restore
        // to its original size after expanding
        container.setFillHeight(false);

        container.getChildren().setAll(graphicSlot, headerBox, actionSlot);
        container.getStyleClass().add("container");
        getChildren().setAll(container);
    }

    protected void setDescriptionText() {
        if (!descriptionText.getChildren().isEmpty()) {
            descriptionText.getChildren().clear();
        }
        if (getSkinnable().getDescription() != null && !getSkinnable().getDescription().isBlank()) {
            BBCodeParser.createLayout(getSkinnable().getDescription(), descriptionText);
        }
    }

    protected double calcHeight() {
        var headerHeight = headerBox.getSpacing()
            + headerBox.getInsets().getTop()
            + headerBox.getInsets().getBottom()
            + titleLbl.getBoundsInLocal().getHeight()
            + (descriptionText.isManaged() ? descriptionText.getBoundsInLocal().getHeight() : 0);

        return Math.max(Math.max(graphicSlot.getHeight(), actionSlot.getHeight()), headerHeight)
            + container.getPadding().getTop()
            + container.getPadding().getBottom();
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
        unregisterChangeListeners(getSkinnable().titleProperty());
        unregisterChangeListeners(getSkinnable().descriptionProperty());
        getSkinnable().graphicProperty().removeListener(graphicSlotListener);

        super.dispose();
    }
}
