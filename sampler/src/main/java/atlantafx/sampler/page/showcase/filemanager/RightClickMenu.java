/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

import javafx.scene.control.ContextMenu;
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
        var open = new MenuItem("Open");

        var cut = new MenuItem("Cut");
        cut.setAccelerator(new KeyCodeCombination(KeyCode.X, CONTROL_DOWN));

        var copy = new MenuItem("Copy");
        copy.setAccelerator(new KeyCodeCombination(KeyCode.C, CONTROL_DOWN));

        var rename = new MenuItem("Rename");
        rename.setAccelerator(new KeyCodeCombination(KeyCode.F2));

        var compress = new MenuItem("Compress");

        var properties = new MenuItem("Properties");

        getItems().setAll(
            open,
            new SeparatorMenuItem(),
            cut,
            copy,
            rename,
            new SeparatorMenuItem(),
            compress,
            new SeparatorMenuItem(),
            properties
        );
    }
}
