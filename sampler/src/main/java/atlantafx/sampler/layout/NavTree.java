/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import atlantafx.base.controls.Spacer;
import atlantafx.sampler.page.Page;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.javafx.FontIcon;

public class NavTree extends TreeView<Nav> {

    public NavTree(MainModel model) {
        super();

        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (!(val instanceof Item item)) {
                return;
            }

            if (!item.isGroup()) {
                model.navigate(item.pageClass());
            }
        });

        setShowRoot(false);
        rootProperty().bind(model.navTreeProperty());
        setCellFactory(p -> new NavTreeCell());
    }

    ///////////////////////////////////////////////////////////////////////////

    public static final class NavTreeCell extends TreeCell<Nav> {

        private static final PseudoClass GROUP = PseudoClass.getPseudoClass("group");

        private final HBox root;
        private final Label titleLabel;
        private final Node arrowIcon;

        public NavTreeCell() {
            super();

            titleLabel = new Label();
            titleLabel.setGraphicTextGap(10);
            titleLabel.getStyleClass().add("title");

            arrowIcon = new FontIcon();
            arrowIcon.getStyleClass().add("arrow");

            root = new HBox();
            root.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().setAll(titleLabel, new Spacer(), arrowIcon);
            root.setCursor(Cursor.HAND);
            root.getStyleClass().add("container");
            root.setMaxWidth(MainLayer.SIDEBAR_WIDTH - 10);

            root.setOnMouseClicked(e -> {
                if (!(getTreeItem() instanceof Item item)) {
                    return;
                }

                if (item.isGroup() && e.getButton() == MouseButton.PRIMARY) {
                    item.setExpanded(!item.isExpanded());
                    // scroll slightly above the target
                    getTreeView().scrollTo(getTreeView().getRow(item) - 10);
                }
            });

            getStyleClass().add("nav-tree-cell");
        }

        @Override
        protected void updateItem(Nav nav, boolean empty) {
            super.updateItem(nav, empty);

            if (nav == null || empty) {
                setGraphic(null);
                titleLabel.setText(null);
                titleLabel.setGraphic(null);
            } else {
                setGraphic(root);

                titleLabel.setText(nav.title());
                titleLabel.setGraphic(nav.graphic());

                pseudoClassStateChanged(GROUP, nav.isGroup());
                arrowIcon.setVisible(nav.isGroup());
            }
        }
    }

    public static final class Item extends TreeItem<Nav> {

        private final Nav nav;

        private Item(Nav nav) {
            this.nav = Objects.requireNonNull(nav, "nav");
            setValue(nav);
        }

        public boolean isGroup() {
            return nav.isGroup();
        }

        public @Nullable Class<? extends Page> pageClass() {
            return nav.pageClass();
        }

        public static Item root() {
            return new Item(Nav.ROOT);
        }

        public static Item group(String title, Node graphic) {
            return new Item(new Nav(title, graphic, null, null));
        }

        public static Item page(String title,
                                @Nullable Class<? extends Page> pageClass) {
            Objects.requireNonNull(pageClass, "pageClass");
            return new Item(new Nav(title, null, pageClass, Collections.emptyList()));
        }

        public static Item page(String title,
                                @Nullable Class<? extends Page> pageClass,
                                String... searchKeywords) {
            Objects.requireNonNull(pageClass, "pageClass");
            return new Item(new Nav(title, null, pageClass, List.of(searchKeywords)));
        }
    }
}
