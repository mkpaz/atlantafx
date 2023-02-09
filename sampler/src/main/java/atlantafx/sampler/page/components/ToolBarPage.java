/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.BOTTOM;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.CENTER_PILL;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.base.theme.Styles.LEFT;
import static atlantafx.base.theme.Styles.LEFT_PILL;
import static atlantafx.base.theme.Styles.RIGHT;
import static atlantafx.base.theme.Styles.RIGHT_PILL;
import static atlantafx.base.theme.Styles.TOP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;
import static atlantafx.sampler.util.Controls.button;
import static atlantafx.sampler.util.Controls.iconButton;
import static atlantafx.sampler.util.Controls.toggleButton;
import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.fake.SampleMenuBar;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import java.util.ArrayList;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class ToolBarPage extends AbstractPage {

    public static final String NAME = "ToolBar";

    @Override
    public String getName() {
        return NAME;
    }

    private Side toolbarPos = Side.TOP;

    public ToolBarPage() {
        super();
        createView();
    }

    private void createView() {
        var toolbar = new ToolBar(createButtons(HORIZONTAL));
        var toolbarLayer = new BorderPane();
        toolbarLayer.setTop(new TopBar(toolbar));

        var controller = createController(toolbarLayer, toolbar);
        controller.setPrefSize(500, 300);
        var controllerLayer = new BorderPane();
        controllerLayer.setCenter(controller);
        controllerLayer.setMaxSize(500, 300);

        var root = new StackPane();
        root.getStyleClass().add(Styles.BORDERED);
        root.getChildren().addAll(toolbarLayer, controllerLayer);
        VBox.setVgrow(root, Priority.ALWAYS);

        setUserContent(new SampleBlock("Playground", root));
    }

    private TitledPane createController(BorderPane borderPane, ToolBar toolbar) {
        // == BUTTONS ==

        var toTopBtn = new Button("", new FontIcon(Feather.ARROW_UP));
        toTopBtn.getStyleClass().addAll(BUTTON_ICON);
        toTopBtn.setOnAction(e -> rotateToolbar(borderPane, toolbar, Side.TOP));

        var toRightBtn = new Button("", new FontIcon(Feather.ARROW_RIGHT));
        toRightBtn.getStyleClass().addAll(BUTTON_ICON);
        toRightBtn.setOnAction(e -> rotateToolbar(borderPane, toolbar, Side.RIGHT));

        var toBottomBtn = new Button("", new FontIcon(Feather.ARROW_DOWN));
        toBottomBtn.getStyleClass().addAll(BUTTON_ICON);
        toBottomBtn.setOnAction(e -> rotateToolbar(borderPane, toolbar, Side.BOTTOM));

        var toLeftBtn = new Button("", new FontIcon(Feather.ARROW_LEFT));
        toLeftBtn.getStyleClass().addAll(BUTTON_ICON);
        toLeftBtn.setOnAction(e -> rotateToolbar(borderPane, toolbar, Side.LEFT));

        var appendBtn = new Button("", new FontIcon(Feather.PLUS));
        appendBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);
        appendBtn.setOnAction(e -> {
            if (toolbar.getOrientation() == HORIZONTAL) {
                var textBtn = new Button(FAKER.animal().name(), new FontIcon(randomIcon()));
                toolbar.getItems().add(textBtn);
            } else {
                var iconBtn = new Button("", new FontIcon(randomIcon()));
                iconBtn.getStyleClass().addAll(BUTTON_ICON);
                toolbar.getItems().add(iconBtn);
            }
        });

        var buttonsPane = new BorderPane();
        buttonsPane.setMinSize(120, 120);
        buttonsPane.setMaxSize(120, 120);

        buttonsPane.setTop(toTopBtn);
        BorderPane.setAlignment(toTopBtn, Pos.CENTER);

        buttonsPane.setRight(toRightBtn);
        BorderPane.setAlignment(toRightBtn, Pos.CENTER);

        buttonsPane.setBottom(toBottomBtn);
        BorderPane.setAlignment(toBottomBtn, Pos.CENTER);

        buttonsPane.setLeft(toLeftBtn);
        BorderPane.setAlignment(toLeftBtn, Pos.CENTER);

        buttonsPane.setCenter(appendBtn);

        // == TOGGLES ==

        var menuBarToggle = new ToggleSwitch();
        menuBarToggle.selectedProperty().addListener((obs, old, value) -> {
            TopBar topBar = (TopBar) borderPane.getTop();
            if (value) {
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

        togglesGrid.add(createLabel("Show menu bar"), 0, 0);
        togglesGrid.add(menuBarToggle, 1, 0);

        togglesGrid.add(createLabel("Disable"), 0, 1);
        togglesGrid.add(disableToggle, 1, 1);

        // == LAYOUT ==

        var controls = new HBox(40, new Spacer(), buttonsPane, togglesGrid, new Spacer());
        controls.setAlignment(Pos.CENTER);

        var content = new VBox(BLOCK_VGAP);
        content.getChildren().setAll(controls);
        content.setAlignment(Pos.CENTER);

        var root = new TitledPane("Controller", content);
        root.setCollapsible(false);

        return root;
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
                    toolbar.setOrientation(HORIZONTAL);
                    Styles.addStyleClass(toolbar, TOP, RIGHT, BOTTOM, LEFT);
                    toolbar.getItems().setAll(createButtons(HORIZONTAL));
                    topBar.setToolBar(toolbar);
                }
                case RIGHT -> {
                    toolbar.setOrientation(VERTICAL);
                    Styles.addStyleClass(toolbar, RIGHT, TOP, BOTTOM, LEFT);
                    toolbar.getItems().setAll(createButtons(VERTICAL));
                    borderPane.setRight(toolbar);
                }
                case BOTTOM -> {
                    toolbar.setOrientation(HORIZONTAL);
                    Styles.addStyleClass(toolbar, BOTTOM, TOP, RIGHT, LEFT);
                    toolbar.getItems().setAll(createButtons(HORIZONTAL));
                    borderPane.setBottom(toolbar);
                }
                case LEFT -> {
                    toolbar.setOrientation(VERTICAL);
                    Styles.addStyleClass(toolbar, LEFT, RIGHT, TOP, BOTTOM);
                    toolbar.getItems().setAll(createButtons(VERTICAL));
                    borderPane.setLeft(toolbar);
                }
            }
        });
    }

    public Node[] createButtons(Orientation orientation) {
        var result = new ArrayList<Node>();
        result.add(iconButton(Feather.FILE, false));
        result.add(iconButton(Feather.FOLDER, false));
        result.add(iconButton(Feather.SAVE, false));
        result.add(new Separator());

        if (orientation == HORIZONTAL) {
            result.add(button("Undo", null, false));
            result.add(button("Redo", null, true));

            result.add(new Separator());

            result.add(toggleButton("", Feather.BOLD, null, true, BUTTON_ICON, LEFT_PILL));
            result.add(toggleButton("", Feather.ITALIC, null, false, BUTTON_ICON, CENTER_PILL));
            result.add(toggleButton("", Feather.UNDERLINE, null, false, BUTTON_ICON, RIGHT_PILL));

            result.add(new Spacer(5));

            var fontCombo = new ComboBox<>(FXCollections.observableArrayList(Font.getFamilies()));
            fontCombo.setPrefWidth(150);
            fontCombo.getSelectionModel().selectFirst();
            result.add(fontCombo);

            var settingsMenu = new MenuButton("Settings", new FontIcon(Feather.SETTINGS), createItems(5));
            settingsMenu.getStyleClass().add(FLAT);
            result.add(new Spacer());
            result.add(settingsMenu);
        }

        if (orientation == VERTICAL) {
            result.add(iconButton(Feather.CORNER_DOWN_LEFT, false));
            result.add(iconButton(Feather.CORNER_DOWN_RIGHT, true));
            result.add(new Spacer(orientation));
            result.add(iconButton(Feather.SETTINGS, false));
        }

        return result.toArray(Node[]::new);
    }

    private Label createLabel(String text) {
        var label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    public static MenuItem[] createItems(int count) {
        return IntStream.range(0, count).mapToObj(i -> new MenuItem(FAKER.babylon5().character()))
            .toArray(MenuItem[]::new);
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
}
