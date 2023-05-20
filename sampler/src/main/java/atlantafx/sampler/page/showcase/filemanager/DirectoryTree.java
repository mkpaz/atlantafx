/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import static atlantafx.sampler.page.showcase.filemanager.Utils.isFileHidden;

import atlantafx.base.theme.Tweaks;
import java.io.File;
import java.util.Comparator;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

final class DirectoryTree extends TreeView<File> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public DirectoryTree(Model model, File rootPath) {
        super();

        getStyleClass().add(Tweaks.ALT_ICON);

        var root = new TreeItem<>(rootPath);
        root.setExpanded(true);
        setRoot(root);

        // scan file tree two levels deep for starters
        scan(root, 2);

        setCellFactory(c -> new TreeCell<>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());

                    var image = new ImageView(FileIconRepository.FOLDER);
                    image.setFitWidth(20);
                    image.setFitHeight(20);
                    setGraphic(image);

                    pseudoClassStateChanged(Model.HIDDEN, isFileHidden(item.toPath()));
                }
            }
        });

        // scan deeper as the user navigates down the tree
        root.addEventHandler(TreeItem.branchExpandedEvent(), event -> {
            TreeItem parent = event.getTreeItem();
            parent.getChildren().forEach(child -> {
                var item = (TreeItem<File>) child;
                if (item.getChildren().isEmpty()) {
                    scan(item, 1);
                }
            });
        });

        getSelectionModel().selectedItemProperty().addListener((obs, old, item) -> {
            if (item != null && item.getValue() != null) {
                model.navigate(item.getValue().toPath(), true);
            }
        });
    }

    public static void scan(TreeItem<File> parent, int depth) {
        File[] files = parent.getValue().listFiles();
        depth--;

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    var item = new TreeItem<>(f);
                    parent.getChildren().add(item);

                    if (depth > 0) {
                        scan(item, depth);
                    }
                }
            }
            parent.getChildren().sort(
                Comparator.comparing(TreeItem::getValue)
            );
        }
    }
}
