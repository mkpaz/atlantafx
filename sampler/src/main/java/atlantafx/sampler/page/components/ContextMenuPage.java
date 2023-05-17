/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class ContextMenuPage extends AbstractPage {

    public static final String NAME = "ContextMenu";

    @Override
    public String getName() {
        return NAME;
    }

    public ContextMenuPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A popup control containing a list of menu items. It allows for any [i]MenuItem[/i], \
            including its subclasses, to be inserted. A common use case for this class is \
            creating and showing context menus to users."""
        );
        addNode(contextMenuExample());
    }

    private Label contextMenuExample() {
        var contextMenu = new ContextMenu();

        var undoItem = createItem("_Undo", Feather.CORNER_DOWN_LEFT,
            new KeyCodeCombination(KeyCode.Z, CONTROL_DOWN));
        undoItem.setMnemonicParsing(true);

        var redoItem = createItem("_Redo", Feather.CORNER_DOWN_RIGHT,
            new KeyCodeCombination(KeyCode.Y, CONTROL_DOWN));
        redoItem.setMnemonicParsing(true);

        contextMenu.getItems().addAll(
            undoItem,
            redoItem,
            new SeparatorMenuItem(),
            createItem("Cut", Feather.SCISSORS, new KeyCodeCombination(KeyCode.X, CONTROL_DOWN)),
            createItem("Copy", Feather.COPY, new KeyCodeCombination(KeyCode.C, CONTROL_DOWN)),
            createItem("Paste", null, new KeyCodeCombination(KeyCode.V, CONTROL_DOWN))
        );

        var clickArea = new Label("Right-Click Here");
        clickArea.setAlignment(Pos.CENTER);
        clickArea.setMinSize(MAX_WIDTH / 2d, 200);
        clickArea.setMaxSize(MAX_WIDTH / 2d, 200);
        clickArea.setContextMenu(contextMenu);
        clickArea.getStyleClass().add(Styles.BORDERED);

        return clickArea;
    }

    private MenuItem createItem(@Nullable String text,
                                @Nullable Ikon graphic,
                                @Nullable KeyCombination accelerator) {

        var item = new MenuItem(text);

        if (graphic != null) {
            item.setGraphic(new FontIcon(graphic));
        }

        if (accelerator != null) {
            item.setAccelerator(accelerator);
        }

        return item;
    }
}
