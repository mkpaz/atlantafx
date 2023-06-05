/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

final class RightClickMenu extends ContextMenu {

    public RightClickMenu() {
        super();
        createMenu();
    }

    private void createMenu() {
        var openItem = new MenuItem("Open");

        var cutItem = new MenuItem("Cut");
        cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, CONTROL_DOWN));

        var copyItem = new MenuItem("Copy");
        copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, CONTROL_DOWN));

        var renameItem = new MenuItem("Rename");
        renameItem.setAccelerator(new KeyCodeCombination(KeyCode.F2));

        var compressItem = new MenuItem("Compress");

        var propsItem = new MenuItem("Properties");

        getItems().setAll(
            new DemoMenuItem(),
            new SeparatorMenuItem(),
            openItem,
            new SeparatorMenuItem(),
            cutItem,
            copyItem,
            renameItem,
            new SeparatorMenuItem(),
            compressItem,
            new SeparatorMenuItem(),
            propsItem
        );
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class DemoMenuItem extends CustomMenuItem {

        public DemoMenuItem() {
            super();

            var label = new Label("This is a demo menu. None of \nthe options below are working.");
            label.setWrapText(true);
            label.setStyle("-fx-text-fill:-color-fg-muted");

            setContent(label);
            setHideOnClick(false);
            getStyleClass().add("demo-menu-item");
        }
    }
}
