/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.InvalidationListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.util.Callback;
import org.jspecify.annotations.Nullable;

/**
 * Default skin for the {@link TreeSelect} control.
 *
 * <p>Layout: [label] [arrow-button]
 * Popup contains a TreeView with the user's tree data.
 * In multi-select mode, tree cells display checkboxes with tri-state behavior:
 * parent nodes show checked / indeterminate / unchecked based on children state.
 */
public class TreeSelectSkin<T> extends SkinBase<TreeSelect<T>> {

    private final HBox trigger;
    private final Label valueLabel;
    private final StackPane arrowButton;
    private final Region arrow;
    private final Popup popup;
    private final TreeView<T> treeView;

    protected TreeSelectSkin(TreeSelect<T> control) {
        super(control);

        // value label
        valueLabel = new Label();
        valueLabel.getStyleClass().add("value");
        valueLabel.textProperty().bind(control.promptTextProperty());
        valueLabel.setStyle("-fx-text-fill: -color-fg-muted;");

        // arrow
        arrow = new Region();
        arrow.getStyleClass().add("arrow");

        // arrow button
        arrowButton = new StackPane(arrow);
        arrowButton.getStyleClass().add("arrow-button");

        // trigger
        trigger = new HBox(valueLabel, arrowButton);
        trigger.getStyleClass().add("trigger");
        getChildren().setAll(trigger);

        // tree view
        treeView = new TreeView<>();
        treeView.setShowRoot(control.isShowRoot());
        treeView.setRoot(control.getRoot());
        treeView.setMaxHeight(300);
        treeView.getSelectionModel().setSelectionMode(control.getSelectionMode());
        treeView.getStyleClass().add("tree-select-popup");

        // popup
        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        popup.getContent().setAll(treeView);
        popup.setAutoFix(true);

        // listeners
        control.rootProperty().addListener((obs, old, val) -> treeView.setRoot(val));
        control.showRootProperty().addListener((obs, old, val) -> treeView.setShowRoot(val));

        // sync selection mode
        control.selectionModeProperty().addListener((obs, old, val) -> {
            treeView.getSelectionModel().setSelectionMode(val);
            updateCellFactory();
            updateValueLabel();
        });
        updateCellFactory();

        // selection -> update label and refresh cells for tri-state
        treeView.getSelectionModel().getSelectedItems().addListener(
                (InvalidationListener) o -> {
                    updateValueLabel();
                    if (treeView.getSelectionModel().getSelectionMode() == SelectionMode.MULTIPLE) {
                        treeView.refresh();
                    }
                }
        );

        // single selection: close popup on select
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (treeView.getSelectionModel().getSelectionMode() == SelectionMode.SINGLE) {
                popup.hide();
            }
        });

        // click trigger -> toggle popup
        trigger.setOnMouseClicked(e -> {
            if (popup.isShowing()) {
                popup.hide();
            } else {
                showPopup();
            }
        });

        // sync initial state
        updateValueLabel();
    }

    private void updateCellFactory() {
        if (getSkinnable().getSelectionMode() == SelectionMode.MULTIPLE) {
            treeView.setCellFactory(new CheckBoxCellFactory<>());
        } else {
            treeView.setCellFactory(null);
        }
    }

    private void showPopup() {
        var control = getSkinnable();
        var window = control.getScene().getWindow();
        var bounds = control.localToScreen(control.getBoundsInLocal());
        if (bounds == null) return;

        treeView.setPrefWidth(control.getWidth());
        popup.show(window, bounds.getMinX(), bounds.getMaxY());
    }

    private void updateValueLabel() {
        var model = treeView.getSelectionModel();
        var selected = model.getSelectedItems();

        valueLabel.textProperty().unbind();
        valueLabel.setStyle(null);

        if (selected.isEmpty()) {
            valueLabel.textProperty().bind(getSkinnable().promptTextProperty());
            valueLabel.setStyle("-fx-text-fill: -color-fg-muted;");
        } else if (model.getSelectionMode() == SelectionMode.SINGLE) {
            TreeItem<T> item = model.getSelectedItem();
            valueLabel.setText(item != null && item.getValue() != null
                    ? item.getValue().toString() : "");
        } else {
            long count = selected.stream()
                    .filter(java.util.Objects::nonNull)
                    .count();
            valueLabel.setText(count + " items selected");
        }
    }

    /**
     * Returns the TreeView used inside the popup for programmatic access.
     */
    public TreeView<T> getTreeView() {
        return treeView;
    }

    @Override
    public void dispose() {
        valueLabel.textProperty().unbind();
        popup.hide();
        super.dispose();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Tri-state Helpers                                                      //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Check state of a tree item's descendants.
     * Returns:
     *   0 = none selected
     *   1 = some selected (indeterminate)
     *   2 = all selected
     */
    private int getCheckState(TreeItem<T> item) {
        var children = item.getChildren();
        if (children.isEmpty()) {
            int row = treeView.getRow(item);
            return row >= 0 && treeView.getSelectionModel().isSelected(row) ? 2 : 0;
        }

        boolean hasSelected = false;
        boolean hasUnselected = false;

        for (var child : children) {
            int state = getCheckState(child);
            if (state == 2) {
                hasSelected = true;
            } else if (state == 0) {
                hasUnselected = true;
            } else {
                // indeterminate child → parent is indeterminate
                return 1;
            }
            if (hasSelected && hasUnselected) return 1;
        }
        if (hasSelected && !hasUnselected) return 2;
        return 0;
    }

    private void selectWithDescendants(TreeItem<T> item) {
        var model = treeView.getSelectionModel();
        int row = treeView.getRow(item);
        if (row >= 0 && !model.isSelected(row)) {
            model.select(row);
        }
        for (var child : item.getChildren()) {
            selectWithDescendants(child);
        }
    }

    private void clearWithDescendants(TreeItem<T> item) {
        var model = treeView.getSelectionModel();
        int row = treeView.getRow(item);
        if (row >= 0) {
            model.clearSelection(row);
        }
        for (var child : item.getChildren()) {
            clearWithDescendants(child);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // CheckBox Cell Factory                                                  //
    ///////////////////////////////////////////////////////////////////////////

    private class CheckBoxCellFactory<S> implements Callback<TreeView<S>, TreeCell<S>> {
        @Override
        public TreeCell<S> call(TreeView<S> tv) {
            return new TreeCell<>() {
                final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setAllowIndeterminate(true);
                    checkBox.addEventHandler(
                            javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                            e -> {
                                TreeItem<S> treeItem = getTreeItem();
                                if (treeItem == null) return;

                                @SuppressWarnings("unchecked")
                                var skinRef = (TreeSelectSkin<S>) TreeSelectSkin.this;
                                int state = skinRef.getCheckState(treeItem);
                                // unchecked or indeterminate → select all; checked → deselect all
                                if (state < 2) {
                                    skinRef.selectWithDescendants(treeItem);
                                } else {
                                    skinRef.clearWithDescendants(treeItem);
                                }
                                e.consume();
                            }
                    );
                }

                @Override
                protected void updateItem(S item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        checkBox.setText(item.toString());
                        setGraphic(checkBox);
                        setText(null);

                        @SuppressWarnings("unchecked")
                        var skinRef = (TreeSelectSkin<S>) TreeSelectSkin.this;
                        TreeItem<S> treeItem = getTreeItem();
                        int state = treeItem != null ? skinRef.getCheckState(treeItem) : 0;

                        // prevent listener from firing during visual update
                        checkBox.setSelected(state == 2);
                        checkBox.setIndeterminate(state == 1);
                    }
                }
            };
        }
    }
}
