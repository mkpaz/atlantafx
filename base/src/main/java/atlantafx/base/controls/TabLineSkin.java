/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SkinBase;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import org.jspecify.annotations.Nullable;

public class TabLineSkin extends SkinBase<TabLine> {

    protected static final boolean IS_TOUCH_SUPPORTED = Platform.isSupported(ConditionalFeature.INPUT_TOUCH);

    protected final HBox rootContainer = new HBox();
    protected final TabsContainer tabsContainer;
    protected final Rectangle clipRect;

    protected final TabLineBehavior behavior;
    protected final InvalidationListener selectionChangeListener = o -> getSkinnable().requestLayout();
    protected @Nullable SelectionModel<Tab> selectionModel;

    public TabLineSkin(TabLine control) {
        super(control);

        rootContainer.getStyleClass().add("root-container");

        behavior = new TabLineBehavior(control, this);
        tabsContainer = new TabsContainer(control, behavior);
        HBox.setHgrow(tabsContainer, Priority.ALWAYS);

        clipRect = new Rectangle(control.getWidth(), control.getHeight());
        control.setClip(clipRect);

        if (!control.getTabs().isEmpty()) {
            tabsContainer.addTabs(control.getTabs(), 0);
            tabsContainer.updatePseudoClasses();
        }

        if (control.getLeftNode() != null) {
            control.getLeftNode().getStyleClass().add("left-node");
            HBox.setHgrow(control.getLeftNode(), Priority.NEVER);
            rootContainer.getChildren().add(control.getLeftNode());
        }
        rootContainer.getChildren().addAll(tabsContainer);
        if (control.getRightNode() != null) {
            control.getRightNode().getStyleClass().add("right-node");
            HBox.setHgrow(control.getRightNode(), Priority.NEVER);
            rootContainer.getChildren().add(control.getRightNode());
        }
        getChildren().setAll(rootContainer);

        updateSelectionModel();

        registerChangeListener(control.selectionModelProperty(), o -> updateSelectionModel());
        registerChangeListener(control.widthProperty(), o -> {
            tabsContainer.invalidateScrollOffset();
            clipRect.setWidth(control.getWidth());
        });
        registerChangeListener(control.heightProperty(), o -> {
            tabsContainer.invalidateScrollOffset();
            clipRect.setHeight(control.getHeight());
        });
        registerChangeListener(control.ellipsisStringProperty(), o -> {
            for (Node node : tabsContainer.getChildrenUnmodifiable()) {
                ((TabSkin) node).updateEllipsisString();
            }
        });
        registerChangeListener(control.leftNodeProperty(), o -> {
            Node node = control.getLeftNode();
            if (node == null) {
                rootContainer.getChildren().removeIf(c -> c.getStyleClass().contains("left-node"));
            } else {
                if (!node.getStyleClass().contains("left-node")) {
                    node.getStyleClass().add("left-node");
                }
                HBox.setHgrow(control.getLeftNode(), Priority.NEVER);
                rootContainer.getChildren().addFirst(node);
            }
        });
        registerChangeListener(control.rightNodeProperty(), o -> {
            Node node = control.getRightNode();
            if (node == null) {
                rootContainer.getChildren().removeIf(c -> c.getStyleClass().contains("right-node"));
            } else {
                if (!node.getStyleClass().contains("right-node")) {
                    node.getStyleClass().add("right-node");
                }
                HBox.setHgrow(control.getRightNode(), Priority.NEVER);
                rootContainer.getChildren().add(node);
            }
        });

        // set initial selection
        Tab selectedTab = control.getSelectionModel().getSelectedItem();
        int selectedIndex = control.getSelectionModel().getSelectedIndex();

        //noinspection ConstantValue, Intellij is being stupid
        if (selectedTab == null && selectedIndex != -1) {
            // could not find the selected tab try and get the selected tab using the selected index
            control.getSelectionModel().select(selectedIndex);
            selectedTab = control.getSelectionModel().getSelectedItem();
        }

        //noinspection ConstantValue, Intellij is being stupid
        if (selectedTab == null) {
            // selectedItem and selectedIndex have failed, so select the first tab
            control.getSelectionModel().selectFirst();
        }

        if (IS_TOUCH_SUPPORTED) {
            control.addEventHandler(SwipeEvent.SWIPE_LEFT, o -> behavior.selectNextTab());
            control.addEventHandler(SwipeEvent.SWIPE_RIGHT, o -> behavior.selectPreviousTab());
        }
    }

    protected void updateSelectionModel() {
        // selection model is null when skin is initialized
        if (selectionModel != null) {
            selectionModel.selectedItemProperty().removeListener(selectionChangeListener);
            if (selectionModel instanceof TabLine.TabLineSelectionModel m) {
                m.dispose();
            }
        }

        // getSelectionModel() guarantees non-null value
        selectionModel = getSkinnable().getSelectionModel();
        selectionModel.selectedItemProperty().addListener(selectionChangeListener);
    }

    @Override
    public void dispose() {
        unregisterListChangeListeners(getSkinnable().getTabs());
        unregisterChangeListeners(getSkinnable().selectionModelProperty());
        unregisterChangeListeners(getSkinnable().widthProperty());
        unregisterChangeListeners(getSkinnable().heightProperty());
        unregisterChangeListeners(getSkinnable().ellipsisStringProperty());

        tabsContainer.dispose();
        behavior.dispose();

        getChildren().remove(tabsContainer);

        if (selectionModel != null) {
            selectionModel.selectedItemProperty().removeListener(selectionChangeListener);
            selectionModel = null;
        }

        super.dispose();
    }
}
