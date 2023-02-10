/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.fake;

import static atlantafx.sampler.util.Controls.menuItem;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;

import atlantafx.base.controls.CaptionMenuItem;
import java.util.stream.IntStream;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import net.datafaker.Faker;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

public class SampleMenuBar extends MenuBar {

    private static final EventHandler<ActionEvent> PRINT_SOURCE = System.out::println;

    public SampleMenuBar(Faker faker) {
        getMenus().addAll(
            fileMenu(faker),
            editMenu(),
            viewMenu(),
            toolsMenu(),
            aboutMenu()
        );
    }

    private Menu fileMenu(Faker faker) {
        var fileMenu = new Menu("_File");
        fileMenu.setMnemonicParsing(true);
        fileMenu.setOnAction(PRINT_SOURCE);

        var newMenu = menuItem("_New", null, new KeyCodeCombination(KeyCode.N, CONTROL_DOWN));
        newMenu.setMnemonicParsing(true);
        newMenu.setOnAction(PRINT_SOURCE);

        var openRecentMenu = new Menu("Open _Recent");
        openRecentMenu.setMnemonicParsing(true);
        openRecentMenu.setOnAction(PRINT_SOURCE);
        openRecentMenu.getItems().addAll(
            IntStream.range(0, 10).mapToObj(x -> new MenuItem(faker.file().fileName())).toList()
        );

        fileMenu.getItems().addAll(
            newMenu,
            new SeparatorMenuItem(),
            menuItem("Open", Feather.FOLDER, new KeyCodeCombination(KeyCode.O, CONTROL_DOWN)),
            openRecentMenu,
            new SeparatorMenuItem(),
            menuItem("Save", Feather.SAVE, new KeyCodeCombination(KeyCode.S, CONTROL_DOWN)),
            new MenuItem("Save As"),
            new SeparatorMenuItem(),
            new MenuItem("Exit")
        );
        return fileMenu;
    }

    private Menu editMenu() {
        var editMenu = new Menu("_Edit");
        editMenu.setMnemonicParsing(true);
        editMenu.setOnAction(PRINT_SOURCE);

        editMenu.getItems().addAll(
            menuItem("Undo", Feather.CORNER_DOWN_LEFT, new KeyCodeCombination(KeyCode.Z, CONTROL_DOWN)),
            menuItem("Redo", Feather.CORNER_DOWN_RIGHT, new KeyCodeCombination(KeyCode.Y, CONTROL_DOWN)),
            new SeparatorMenuItem(),
            menuItem("Cut", Feather.SCISSORS, new KeyCodeCombination(KeyCode.X, CONTROL_DOWN)),
            menuItem("Copy", Feather.COPY, new KeyCodeCombination(KeyCode.C, CONTROL_DOWN), true),
            menuItem("Paste", Feather.CORNER_DOWN_LEFT, new KeyCodeCombination(KeyCode.V, CONTROL_DOWN))
        );
        return editMenu;
    }

    private Menu viewMenu() {
        var viewMenu = new Menu("_View");
        viewMenu.setMnemonicParsing(true);
        viewMenu.setOnAction(PRINT_SOURCE);

        var showToolbarItem = new CheckMenuItem("Show Toolbar", new FontIcon(Feather.TOOL));
        showToolbarItem.setSelected(true);
        showToolbarItem.setAccelerator(new KeyCodeCombination(KeyCode.T, CONTROL_DOWN));

        var showGridItem = new CheckMenuItem("Show Grid", new FontIcon(Feather.GRID));

        var captionItem = new CaptionMenuItem("Layout");

        var viewToggleGroup = new ToggleGroup();

        var toggleItem1 = new RadioMenuItem("Single", new FontIcon(Material2OutlinedAL.LOOKS_ONE));
        toggleItem1.setSelected(true);
        toggleItem1.setToggleGroup(viewToggleGroup);

        var toggleItem2 = new RadioMenuItem("Two Columns", new FontIcon(Material2OutlinedAL.LOOKS_TWO));
        toggleItem2.setToggleGroup(viewToggleGroup);

        var toggleItem3 = new RadioMenuItem("Three Columns", new FontIcon(Material2OutlinedAL.LOOKS_3));
        toggleItem3.setToggleGroup(viewToggleGroup);

        viewMenu.getItems().addAll(
            showToolbarItem,
            showGridItem,
            new SeparatorMenuItem(),
            captionItem,
            toggleItem1,
            toggleItem2,
            toggleItem3
        );
        return viewMenu;
    }

    private Menu toolsMenu() {
        var toolsMenu = new Menu("_Tools");
        toolsMenu.setMnemonicParsing(true);
        toolsMenu.setOnAction(PRINT_SOURCE);
        toolsMenu.setDisable(true);
        return toolsMenu;
    }

    private Menu aboutMenu() {
        var aboutMenu = new Menu("_About", new FontIcon(Feather.HELP_CIRCLE));
        aboutMenu.setMnemonicParsing(true);
        aboutMenu.setOnAction(PRINT_SOURCE);

        var deeplyNestedMenu = new Menu("Very...", null,
            new Menu("Very...", null,
                new Menu("Deeply", null,
                    new Menu("Nested", null,
                        new MenuItem("Menu")
                    ))));
        // NOTE: this won't be displayed because right container is reserved for submenu indication
        deeplyNestedMenu.setAccelerator(new KeyCodeCombination(
            KeyCode.DIGIT1, SHIFT_DOWN, CONTROL_DOWN)
        );

        aboutMenu.getItems().addAll(
            new MenuItem("Help"),
            new MenuItem("About"),
            new SeparatorMenuItem(),
            deeplyNestedMenu
        );
        return aboutMenu;
    }
}
