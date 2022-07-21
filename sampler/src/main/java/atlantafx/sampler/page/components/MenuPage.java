/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.fake.SampleMenuBar;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.util.Controls;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.kordamp.ikonli.feather.Feather;

import static atlantafx.sampler.util.Controls.menuItem;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

public class MenuPage extends AbstractPage {

    public static final String NAME = "Menu";

    @Override
    public String getName() { return NAME; }

    public MenuPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(
                menuBarSample().getRoot(),
                contextMenuExample().getRoot()
        );
    }

    private SampleBlock menuBarSample() {
        return new SampleBlock("Menu Bar", new SampleMenuBar(FAKER));
    }

    private SampleBlock contextMenuExample() {
        var contextMenu = new ContextMenu();

        var undoItem = Controls.menuItem("_Undo", Feather.CORNER_DOWN_LEFT, new KeyCodeCombination(KeyCode.Z, CONTROL_DOWN));
        undoItem.setMnemonicParsing(true);
        undoItem.setOnAction(PRINT_SOURCE);

        var redoItem = Controls.menuItem("_Redo", Feather.CORNER_DOWN_RIGHT, new KeyCodeCombination(KeyCode.Y, CONTROL_DOWN));
        redoItem.setMnemonicParsing(true);
        redoItem.setOnAction(PRINT_SOURCE);

        contextMenu.getItems().addAll(
                undoItem,
                redoItem,
                new SeparatorMenuItem(),
                Controls.menuItem("Cut", Feather.SCISSORS, new KeyCodeCombination(KeyCode.X, CONTROL_DOWN)),
                Controls.menuItem("Copy", Feather.COPY, new KeyCodeCombination(KeyCode.C, CONTROL_DOWN)),
                Controls.menuItem("Paste", null, new KeyCodeCombination(KeyCode.V, CONTROL_DOWN))
        );

        var sample = new Label("Right-Click Here");
        sample.setAlignment(Pos.CENTER);
        sample.setMinSize(400, 80);
        sample.setMaxSize(200, 80);
        sample.setContextMenu(contextMenu);
        sample.getStyleClass().add(Styles.BORDERED);

        return new SampleBlock("Context menu", sample);
    }
}
