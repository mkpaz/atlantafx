/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.fake.SampleMenuBar;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.util.Controls;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;

public class MenuPage extends AbstractPage {

    public static final String NAME = "Menu";

    @Override
    public String getName() {
        return NAME;
    }

    public MenuPage() {
        super();
        setUserContent(new VBox(Page.PAGE_VGAP,
            menuBarSample(),
            contextMenuExample()
        ));
    }

    private SampleBlock menuBarSample() {
        return new SampleBlock("Menu Bar", new SampleMenuBar(FAKER));
    }

    private SampleBlock contextMenuExample() {
        var contextMenu = new ContextMenu();

        var undoItem =
            Controls.menuItem("_Undo", Feather.CORNER_DOWN_LEFT, new KeyCodeCombination(KeyCode.Z, CONTROL_DOWN));
        undoItem.setMnemonicParsing(true);

        var redoItem =
            Controls.menuItem("_Redo", Feather.CORNER_DOWN_RIGHT, new KeyCodeCombination(KeyCode.Y, CONTROL_DOWN));
        redoItem.setMnemonicParsing(true);

        contextMenu.getItems().addAll(
            undoItem,
            redoItem,
            new SeparatorMenuItem(),
            Controls.menuItem("Cut", Feather.SCISSORS, new KeyCodeCombination(KeyCode.X, CONTROL_DOWN)),
            Controls.menuItem("Copy", Feather.COPY, new KeyCodeCombination(KeyCode.C, CONTROL_DOWN)),
            Controls.menuItem("Paste", null, new KeyCodeCombination(KeyCode.V, CONTROL_DOWN))
        );

        var content = new Label("Right-Click Here");
        content.setAlignment(Pos.CENTER);
        content.setMinSize(400, 80);
        content.setMaxSize(400, 80);
        content.setContextMenu(contextMenu);
        content.getStyleClass().add(Styles.BORDERED);

        return new SampleBlock("Context Menu", content);
    }
}
