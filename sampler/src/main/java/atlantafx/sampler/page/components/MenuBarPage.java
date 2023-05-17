/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static javafx.scene.input.KeyCombination.CONTROL_DOWN;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;

import atlantafx.base.controls.CaptionMenuItem;
import atlantafx.sampler.page.AbstractPage;
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
import javafx.scene.input.KeyCombination;
import net.datafaker.Faker;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

public final class MenuBarPage extends AbstractPage {

    public static final String NAME = "MenuBar";

    @Override
    public String getName() {
        return NAME;
    }

    public MenuBarPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A menu bar is a user interface component that typically appears at the top of \
            an application window or screen, and provides a series of drop-down menus that \
            allow users to access various features and functions of the application."""
        );
        addNode(menuBarSample());
    }

    private ExampleMenuBar menuBarSample() {
        return new ExampleMenuBar();
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class ExampleMenuBar extends MenuBar {

        private static final Faker FAKER = new Faker();
        private static final EventHandler<ActionEvent> SYSTEM_OUT = System.out::println;

        public ExampleMenuBar() {
            getMenus().addAll(
                fileMenu(),
                editMenu(),
                viewMenu(),
                toolsMenu(),
                aboutMenu()
            );
        }

        private Menu fileMenu() {
            var menu = new Menu("_File");
            menu.setMnemonicParsing(true);
            menu.setOnAction(SYSTEM_OUT);

            var newMenu = createItem(
                "_New", null, new KeyCodeCombination(KeyCode.N, CONTROL_DOWN)
            );
            newMenu.setMnemonicParsing(true);
            newMenu.setOnAction(SYSTEM_OUT);

            var openRecentMenu = new Menu("Open _Recent");
            openRecentMenu.setMnemonicParsing(true);
            openRecentMenu.setOnAction(SYSTEM_OUT);
            openRecentMenu.getItems().addAll(
                IntStream.range(0, 10)
                    .mapToObj(x -> new MenuItem(FAKER.file().fileName()))
                    .toList()
            );

            menu.getItems().addAll(
                newMenu,
                new SeparatorMenuItem(),
                createItem(
                    "Open", Feather.FOLDER, new KeyCodeCombination(KeyCode.O, CONTROL_DOWN)
                ),
                openRecentMenu,
                new SeparatorMenuItem(),
                createItem(
                    "Save", Feather.SAVE, new KeyCodeCombination(KeyCode.S, CONTROL_DOWN)
                ),
                new MenuItem("Save As"),
                new SeparatorMenuItem(),
                new MenuItem("Exit")
            );
            return menu;
        }

        private Menu editMenu() {
            var menu = new Menu("_Edit");
            menu.setMnemonicParsing(true);
            menu.setOnAction(SYSTEM_OUT);
            menu.getItems().addAll(
                createItem(
                    "Undo", Feather.CORNER_DOWN_LEFT, new KeyCodeCombination(KeyCode.Z, CONTROL_DOWN)
                ),
                createItem(
                    "Redo", Feather.CORNER_DOWN_RIGHT, new KeyCodeCombination(KeyCode.Y, CONTROL_DOWN)
                ),
                new SeparatorMenuItem(),
                createItem(
                    "Cut", Feather.SCISSORS, new KeyCodeCombination(KeyCode.X, CONTROL_DOWN)
                ),
                createItem(
                    "Copy", Feather.COPY, new KeyCodeCombination(KeyCode.C, CONTROL_DOWN)
                ),
                createItem(
                    "Paste", Feather.CORNER_DOWN_LEFT, new KeyCodeCombination(KeyCode.V, CONTROL_DOWN)
                )
            );
            return menu;
        }

        private Menu viewMenu() {
            var menu = new Menu("_View");
            menu.setMnemonicParsing(true);
            menu.setOnAction(SYSTEM_OUT);

            var showToolbarItem = new CheckMenuItem(
                "Show Toolbar", new FontIcon(Feather.TOOL)
            );
            showToolbarItem.setSelected(true);
            showToolbarItem.setAccelerator(
                new KeyCodeCombination(KeyCode.T, CONTROL_DOWN)
            );

            var showGridItem = new CheckMenuItem(
                "Show Grid", new FontIcon(Feather.GRID));

            var captionItem = new CaptionMenuItem("Layout");

            var viewToggleGroup = new ToggleGroup();

            var toggleItem1 = new RadioMenuItem(
                "Single", new FontIcon(Material2OutlinedAL.LOOKS_ONE)
            );
            toggleItem1.setSelected(true);
            toggleItem1.setToggleGroup(viewToggleGroup);

            var toggleItem2 = new RadioMenuItem(
                "Two Columns", new FontIcon(Material2OutlinedAL.LOOKS_TWO)
            );
            toggleItem2.setToggleGroup(viewToggleGroup);

            var toggleItem3 = new RadioMenuItem(
                "Three Columns", new FontIcon(Material2OutlinedAL.LOOKS_3)
            );
            toggleItem3.setToggleGroup(viewToggleGroup);

            menu.getItems().addAll(
                showToolbarItem,
                showGridItem,
                new SeparatorMenuItem(),
                captionItem,
                toggleItem1,
                toggleItem2,
                toggleItem3
            );
            return menu;
        }

        private Menu toolsMenu() {
            var menu = new Menu("_Tools");
            menu.setMnemonicParsing(true);
            menu.setOnAction(SYSTEM_OUT);
            menu.setDisable(true);
            return menu;
        }

        private Menu aboutMenu() {
            var menu = new Menu(
                "_About", new FontIcon(Feather.HELP_CIRCLE)
            );
            menu.setMnemonicParsing(true);
            menu.setOnAction(SYSTEM_OUT);

            var deeplyNestedMenu = new Menu(
                "Very...", null,
                new Menu("Very...", null,
                    new Menu("Deeply", null,
                        new Menu("Nested", null,
                            new MenuItem("Menu")
                        ))));

            // NOTE: this won't be displayed because right container is reserved for submenu indication
            deeplyNestedMenu.setAccelerator(
                new KeyCodeCombination(KeyCode.DIGIT1, SHIFT_DOWN, CONTROL_DOWN)
            );

            menu.getItems().addAll(
                new MenuItem("Help"),
                new MenuItem("About"),
                new SeparatorMenuItem(),
                deeplyNestedMenu
            );
            return menu;
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
}
