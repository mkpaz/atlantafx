/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

final class SearchDialog extends ModalDialog {

    private final MainModel model;

    private CustomTextField searchField;
    private ListView<NavTree.Item> resultList;

    public SearchDialog(MainModel model) {
        super();

        this.model = model;

        setId("search-dialog");
        header.setTitle("Search");
        content.setBody(createContent());
        content.setFooter(createDefaultFooter());
        content.setPrefSize(600, 440);

        init();
    }

    private VBox createContent() {
        var placeholder = new Label("Your search results will appear here");
        placeholder.getStyleClass().add(Styles.TITLE_4);

        searchField = new CustomTextField();
        searchField.setLeft(new FontIcon(Material2MZ.SEARCH));
        VBox.setVgrow(searchField, Priority.NEVER);

        Consumer<NavTree.Item> clickHandler = item -> {
            if (item.pageClass() != null) {
                close();
                model.navigate(item.pageClass());
            }
        };

        resultList = new ListView<>();
        resultList.setPlaceholder(placeholder);
        resultList.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
        resultList.setCellFactory(c -> new ResultListCell(clickHandler));
        VBox.setVgrow(resultList, Priority.ALWAYS);

        return new VBox(10, searchField, resultList);
    }

    private void init() {
        searchField.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.length() <= 2) {
                resultList.getItems().clear();
                return;
            }

            resultList.getItems().setAll(model.findPages(val));
        });

        searchField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.DOWN && !resultList.getItems().isEmpty()) {
                resultList.getSelectionModel().selectFirst();
                resultList.requestFocus();
            }
        });

        resultList.setOnKeyPressed(e -> {
            var selectionModel = resultList.getSelectionModel();
            if (e.getCode() == KeyCode.ENTER && !selectionModel.isEmpty()) {
                close();
                model.navigate(selectionModel.getSelectedItem().pageClass());
            }
        });
    }

    void begForFocus() {
        searchField.requestFocus();
    }

    ///////////////////////////////////////////////////////////////////////////

    public static final class ResultListCell extends ListCell<NavTree.Item> {

        private final HBox root;
        private final Label parentLabel;
        private final Label targetLabel;

        public ResultListCell(Consumer<NavTree.Item> clickHandler) {
            super();

            parentLabel = new Label();
            parentLabel.getStyleClass().add(Styles.TEXT_MUTED);

            var separatorIcon = new FontIcon(Material2AL.CHEVRON_RIGHT);
            separatorIcon.getStyleClass().add("icon-subtle");

            var returnIcon = new FontIcon(Material2AL.KEYBOARD_RETURN);
            returnIcon.getStyleClass().add("icon-subtle");

            targetLabel = new Label();
            targetLabel.getStyleClass().add(Styles.TEXT_BOLD);

            root = new HBox(parentLabel, separatorIcon, targetLabel, new Spacer(), returnIcon);
            root.setAlignment(Pos.CENTER_LEFT);

            setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    clickHandler.accept(getItem());
                }
            });
        }

        @Override
        protected void updateItem(NavTree.Item item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
            } else {
                parentLabel.setText(item.getParent().getValue().title());
                targetLabel.setText(item.getValue().title());
                setGraphic(root);
            }
        }
    }
}
