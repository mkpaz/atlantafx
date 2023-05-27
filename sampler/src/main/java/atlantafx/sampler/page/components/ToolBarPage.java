/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Breadcrumbs;
import atlantafx.base.controls.CaptionMenuItem;
import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import net.datafaker.Faker;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

public final class ToolBarPage extends OutlinePage {

    public static final String NAME = "ToolBar";

    @Override
    public String getName() {
        return NAME;
    }

    public ToolBarPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A [i]ToolBar[/i] is a control which displays items horizontally or vertically."""
        );
        addSection("Usage", usageExample());
        addSection("Playground", playground());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        final var toolbar1 = new ToolBar(
            new Button("New", new FontIcon(Feather.PLUS)),
            new Button("Open", new FontIcon(Feather.FILE)),
            new Button("Save", new FontIcon(Feather.SAVE)),
            new Separator(Orientation.VERTICAL),
            new Button("Clean", new FontIcon(Feather.ROTATE_CCW)),
            new Button("Compile", new FontIcon(Feather.LAYERS)),
            new Button("Run", new FontIcon(Feather.PLAY))
        );

        // ~
        var fontFamilyCmb = new ComboBox<>(
            FXCollections.observableArrayList(Font.getFamilies())
        );
        fontFamilyCmb.setPrefWidth(150);
        fontFamilyCmb.getSelectionModel().selectFirst();

        var fontSizeCmb = new ComboBox<>(
            IntStream.range(6, 32).mapToObj(String::valueOf).collect(
                Collectors.toCollection(FXCollections::observableArrayList)
            ));
        fontSizeCmb.getSelectionModel().select(6);

        final var toolbar2 = new ToolBar(
            fontFamilyCmb,
            fontSizeCmb,
            new Separator(Orientation.VERTICAL),
            toggleIconButton(Feather.BOLD, true),
            toggleIconButton(Feather.ITALIC),
            toggleIconButton(Feather.UNDERLINE),
            new Separator(Orientation.VERTICAL),
            toggleIconButton(Feather.ALIGN_LEFT),
            toggleIconButton(Feather.ALIGN_CENTER),
            toggleIconButton(Feather.ALIGN_RIGHT),
            new Separator(Orientation.VERTICAL),
            iconButton(Feather.IMAGE)
        );

        // ~
        var textField = new CustomTextField("https://example.com");
        textField.setPromptText("Search Doodle of type an URL");
        textField.setLeft(new FontIcon(Feather.LOCK));
        textField.setRight(new FontIcon(Feather.STAR));
        HBox.setHgrow(textField, Priority.ALWAYS);

        var dropdown = new MenuButton("", new FontIcon(Feather.MENU));
        dropdown.getItems().setAll(
            new MenuItem("Action 1"),
            new MenuItem("Action 2"),
            new MenuItem("Action 3")
        );

        final var toolbar3 = new ToolBar(
            iconButton(Feather.CHEVRON_LEFT),
            iconButton(Feather.CHEVRON_RIGHT),
            new Separator(Orientation.VERTICAL),
            iconButton(Feather.REFRESH_CW),
            new Spacer(10),
            textField,
            new Spacer(10),
            iconButton(Feather.BOOKMARK),
            iconButton(Feather.USER),
            dropdown
        );

        // ~
        var root = new Breadcrumbs.BreadCrumbItem<>("Home");
        var l1Item = new Breadcrumbs.BreadCrumbItem<>("Documents");
        var l2Item = new Breadcrumbs.BreadCrumbItem<>("Bills");
        root.getChildren().add(l1Item);
        l1Item.getChildren().add(l2Item);

        var crumbs = new Breadcrumbs<>(root);
        crumbs.setSelectedCrumb(l2Item);
        crumbs.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(crumbs, Priority.ALWAYS);

        var toolbar4 = new ToolBar(
            iconButton(Feather.CHEVRON_LEFT),
            iconButton(Feather.CHEVRON_RIGHT),
            new Spacer(10),
            crumbs,
            new Spacer(10),
            iconButton(Feather.SEARCH),
            iconButton(Material2OutlinedAL.FORMAT_LIST_NUMBERED),
            iconButton(Material2OutlinedMZ.SETTINGS)
        );
        //snippet_1:end

        var box = new VBox(VGAP_20, toolbar1, toolbar2, toolbar3, toolbar4);

        var description = BBCodeParser.createFormattedText("""
            The most common items to place within a toolbar are [i]Button[/i], [i]ToggleButtons[/i] \
            and [i]Separator[/i], but you are not restricted to just these, and can insert any node \
            into it."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playground                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private Side toolbarPos = Side.TOP;

    private VBox playground() {
        var toolbar = new ToolBar(createButtons(Orientation.HORIZONTAL));
        var toolbarLayer = new BorderPane();
        toolbarLayer.setTop(new TopBar(toolbar));

        var controller = createController(toolbarLayer, toolbar);
        controller.setPrefSize(500, 300);

        var controllerLayer = new BorderPane(controller);
        controllerLayer.setMaxSize(500, 300);

        var description = BBCodeParser.createFormattedText("""
            The playground demonstrates the most important [i]Toolbar[/i] features \
            and also serves as an object for monkey testing."""
        );

        var stack = new StackPane(toolbarLayer, controllerLayer);
        stack.getStyleClass().add(Styles.BORDERED);
        stack.setMinSize(600, 500);

        return new VBox(VGAP_10, description, stack);
    }

    private TitledPane createController(BorderPane borderPane, ToolBar toolbar) {
        // == BUTTONS ==

        var toTopBtn = new Button(null, new FontIcon(Feather.ARROW_UP));
        toTopBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        toTopBtn.setOnAction(e -> rotateToolbar(borderPane, toolbar, Side.TOP));

        var toRightBtn = new Button(null, new FontIcon(Feather.ARROW_RIGHT));
        toRightBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        toRightBtn.setOnAction(e -> rotateToolbar(borderPane, toolbar, Side.RIGHT));

        var toBottomBtn = new Button(null, new FontIcon(Feather.ARROW_DOWN));
        toBottomBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        toBottomBtn.setOnAction(e -> rotateToolbar(borderPane, toolbar, Side.BOTTOM));

        var toLeftBtn = new Button(null, new FontIcon(Feather.ARROW_LEFT));
        toLeftBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        toLeftBtn.setOnAction(e -> rotateToolbar(borderPane, toolbar, Side.LEFT));

        var appendBtn = new Button(null, new FontIcon(Feather.PLUS));
        appendBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT);
        appendBtn.setOnAction(e -> {
            if (toolbar.getOrientation() == Orientation.HORIZONTAL) {
                var textBtn = new Button(FAKER.animal().name(), new FontIcon(randomIcon()));
                toolbar.getItems().add(textBtn);
            } else {
                var iconBtn = new Button(null, new FontIcon(randomIcon()));
                iconBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
                toolbar.getItems().add(iconBtn);
            }
        });

        var buttonsPane = new BorderPane();
        buttonsPane.setMinSize(120, 120);
        buttonsPane.setMaxSize(120, 120);

        buttonsPane.setCenter(appendBtn);

        buttonsPane.setTop(toTopBtn);
        BorderPane.setAlignment(toTopBtn, Pos.CENTER);

        buttonsPane.setRight(toRightBtn);
        BorderPane.setAlignment(toRightBtn, Pos.CENTER);

        buttonsPane.setBottom(toBottomBtn);
        BorderPane.setAlignment(toBottomBtn, Pos.CENTER);

        buttonsPane.setLeft(toLeftBtn);
        BorderPane.setAlignment(toLeftBtn, Pos.CENTER);

        // == TOGGLES ==

        var menuBarToggle = new ToggleSwitch();
        menuBarToggle.selectedProperty().addListener((obs, old, val) -> {
            TopBar topBar = (TopBar) borderPane.getTop();
            if (val) {
                topBar.showOrCreateMenuBar();
            } else {
                topBar.hideMenuBar();
            }
        });

        var disableToggle = new ToggleSwitch();
        disableToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) {
                toolbar.setDisable(val);
            }
        });

        var togglesGrid = new GridPane();
        togglesGrid.setHgap(10);
        togglesGrid.setVgap(10);
        togglesGrid.addRow(0, createLabel("Show menu bar"), menuBarToggle);
        togglesGrid.addRow(1, createLabel("Disable"), disableToggle);

        // == LAYOUT ==

        var controls = new HBox(40, buttonsPane, togglesGrid);
        controls.setAlignment(Pos.CENTER);
        controls.setFillHeight(true);

        var wrapper = new VBox(VGAP_20, controls);
        wrapper.setAlignment(Pos.CENTER);

        var controller = new TitledPane("Controller", wrapper);
        controller.setCollapsible(false);

        return controller;
    }

    private void rotateToolbar(BorderPane borderPane, ToolBar toolbar, Side pos) {
        if (toolbarPos == pos) {
            return;
        }

        var topBar = (TopBar) borderPane.getTop();
        toolbarPos = pos;

        boolean changed = borderPane.getChildren().removeAll(toolbar);
        if (!changed) {
            topBar.removeToolBar();
        }

        // WARNING:
        // Rotating existing buttons seems tempting, but it won't work.
        // JavaFX doesn't recalculate their size correctly (even after
        // reattaching controls to the scene), and you'll end up creating
        // new objects anyway.

        Platform.runLater(() -> {
            switch (pos) {
                case TOP -> {
                    toolbar.setOrientation(Orientation.HORIZONTAL);
                    Styles.addStyleClass(
                        toolbar, Styles.TOP, Styles.RIGHT, Styles.BOTTOM, Styles.LEFT
                    );
                    toolbar.getItems().setAll(createButtons(Orientation.HORIZONTAL));
                    topBar.setToolBar(toolbar);
                }
                case RIGHT -> {
                    toolbar.setOrientation(Orientation.VERTICAL);
                    Styles.addStyleClass(
                        toolbar, Styles.RIGHT, Styles.TOP, Styles.BOTTOM, Styles.LEFT
                    );
                    toolbar.getItems().setAll(createButtons(Orientation.VERTICAL));
                    borderPane.setRight(toolbar);
                }
                case BOTTOM -> {
                    toolbar.setOrientation(Orientation.HORIZONTAL);
                    Styles.addStyleClass(
                        toolbar, Styles.BOTTOM, Styles.TOP, Styles.RIGHT, Styles.LEFT
                    );
                    toolbar.getItems().setAll(createButtons(Orientation.HORIZONTAL));
                    borderPane.setBottom(toolbar);
                }
                case LEFT -> {
                    toolbar.setOrientation(Orientation.VERTICAL);
                    Styles.addStyleClass(
                        toolbar, Styles.LEFT, Styles.RIGHT, Styles.TOP, Styles.BOTTOM
                    );
                    toolbar.getItems().setAll(createButtons(Orientation.VERTICAL));
                    borderPane.setLeft(toolbar);
                }
            }
        });
    }

    public Node[] createButtons(Orientation orientation) {
        var result = new ArrayList<Node>();
        result.add(iconButton(Feather.FILE));
        result.add(iconButton(Feather.FOLDER));
        result.add(iconButton(Feather.SAVE));
        result.add(new Separator());

        if (orientation == Orientation.HORIZONTAL) {
            result.add(new Button("Undo"));
            result.add(new Button("Redo"));
            result.add(new Separator());
            result.add(toggleIconButton(Feather.BOLD, true));
            result.add(toggleIconButton(Feather.ITALIC));
            result.add(toggleIconButton(Feather.UNDERLINE));
        }

        if (orientation == Orientation.VERTICAL) {
            result.add(iconButton(Feather.CORNER_DOWN_LEFT));
            result.add(iconButton(Feather.CORNER_DOWN_RIGHT));
            result.add(new Spacer(orientation));
            result.add(iconButton(Feather.SETTINGS));
        }

        return result.toArray(Node[]::new);
    }

    private Label createLabel(String text) {
        var label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    public static Button iconButton(Ikon icon) {
        var btn = new Button(null);
        if (icon != null) {
            btn.setGraphic(new FontIcon(icon));
        }
        btn.getStyleClass().addAll(Styles.BUTTON_ICON);

        return btn;
    }

    public ToggleButton toggleIconButton(@Nullable Ikon icon,
                                         String... styleClasses) {
        return toggleIconButton(icon, null, false, styleClasses);
    }

    public ToggleButton toggleIconButton(@Nullable Ikon icon,
                                         boolean selected,
                                         String... styleClasses) {
        return toggleIconButton(icon, null, selected, styleClasses);
    }

    public ToggleButton toggleIconButton(@Nullable Ikon icon,
                                         @Nullable ToggleGroup group,
                                         boolean selected,
                                         String... styleClasses) {
        var btn = new ToggleButton("");
        if (icon != null) {
            btn.setGraphic(new FontIcon(icon));
        }
        if (group != null) {
            btn.setToggleGroup(group);
        }
        btn.getStyleClass().addAll(styleClasses);
        btn.setSelected(selected);

        return btn;
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class TopBar extends VBox {

        private static final Region DUMMY_MENUBAR = new Region();
        private static final Region DUMMY_TOOLBAR = new Region();

        public TopBar(ToolBar toolBar) {
            super();
            getChildren().setAll(DUMMY_MENUBAR, toolBar);
        }

        public void showOrCreateMenuBar() {
            if (getChildren().get(0) instanceof MenuBar menuBar) {
                menuBar.setVisible(true);
                menuBar.setManaged(true);
            } else {
                getChildren().set(0, new SampleMenuBar(FAKER));
            }
        }

        public void hideMenuBar() {
            var any = getChildren().get(0);
            any.setVisible(false);
            any.setManaged(false);
        }

        public void setToolBar(ToolBar toolBar) {
            getChildren().set(1, toolBar);
        }

        public void removeToolBar() {
            getChildren().set(1, DUMMY_TOOLBAR);
        }
    }

    public static class SampleMenuBar extends MenuBar {

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

            var newMenu = menuItem(
                "_New", null,
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN)
            );
            newMenu.setMnemonicParsing(true);
            newMenu.setOnAction(PRINT_SOURCE);

            var openRecentMenu = new Menu("Open _Recent");
            openRecentMenu.setMnemonicParsing(true);
            openRecentMenu.setOnAction(PRINT_SOURCE);
            openRecentMenu.getItems().addAll(
                IntStream.range(0, 10)
                    .mapToObj(x -> new MenuItem(faker.file().fileName()))
                    .toList()
            );

            fileMenu.getItems().addAll(
                newMenu,
                new SeparatorMenuItem(),
                menuItem(
                    "Open", Feather.FOLDER,
                    new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN)
                ),
                openRecentMenu,
                new SeparatorMenuItem(),
                menuItem(
                    "Save", Feather.SAVE,
                    new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
                ),
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

            var copyItem = menuItem(
                "Copy", Feather.COPY,
                new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN)
            );
            copyItem.setDisable(true);

            editMenu.getItems().addAll(
                menuItem(
                    "Undo", Feather.CORNER_DOWN_LEFT,
                    new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN)
                ),
                menuItem("Redo", Feather.CORNER_DOWN_RIGHT,
                    new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN)
                ),
                new SeparatorMenuItem(),
                menuItem(
                    "Cut", Feather.SCISSORS,
                    new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN)
                ),
                copyItem,
                menuItem(
                    "Paste", Feather.CORNER_DOWN_LEFT,
                    new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN)
                )
            );
            return editMenu;
        }

        private Menu viewMenu() {
            var viewMenu = new Menu("_View");
            viewMenu.setMnemonicParsing(true);
            viewMenu.setOnAction(PRINT_SOURCE);

            var showToolbarItem = new CheckMenuItem(
                "Show Toolbar", new FontIcon(Feather.TOOL)
            );
            showToolbarItem.setSelected(true);
            showToolbarItem.setAccelerator(
                new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN)
            );

            var showGridItem = new CheckMenuItem(
                "Show Grid", new FontIcon(Feather.GRID)
            );

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
                KeyCode.DIGIT1, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN)
            );

            aboutMenu.getItems().addAll(
                new MenuItem("Help"),
                new MenuItem("About"),
                new SeparatorMenuItem(),
                deeplyNestedMenu
            );
            return aboutMenu;
        }

        public MenuItem menuItem(String text, Ikon graphic, KeyCombination accelerator) {
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
