/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.controls.Spacer;
import atlantafx.sampler.page.AbstractPage;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.ACCENT;
import static javafx.scene.control.TabPane.TabClosingPolicy.ALL_TABS;
import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE;

public class TabPanePage extends AbstractPage {

    public static final String NAME = "TabPane";
    private static final double TAB_MIN_HEIGHT = 60;

    @Override
    public String getName() { return NAME; }

    private Side tabSide = Side.TOP;
    private boolean fullWidth = false;

    public TabPanePage() {
        super();
        createView();
    }

    private void createView() {
        var tabs = tabPane();
        var tabsLayer = new BorderPane();
        tabsLayer.setTop(tabs);
        tabs.getTabs().addListener((ListChangeListener<Tab>) c -> updateTabsWidth(tabsLayer, tabs, fullWidth));

        var controller = createController(tabsLayer, tabs);
        controller.setPrefSize(500, 300);
        var controllerLayer = new BorderPane();
        controllerLayer.setCenter(controller);
        controllerLayer.setMaxSize(500, 300);

        var root = new StackPane();
        root.getStyleClass().add(Styles.BORDERED);
        root.getChildren().addAll(tabsLayer, controllerLayer);
        VBox.setVgrow(root, Priority.ALWAYS);

        userContent.getChildren().setAll(root);
    }

    private TitledPane createController(BorderPane borderPane, TabPane tabs) {
        // == BUTTONS ==

        var toTopBtn = new Button("", new FontIcon(Feather.ARROW_UP));
        toTopBtn.getStyleClass().addAll(BUTTON_ICON);
        toTopBtn.setOnAction(e -> rotateTabs(borderPane, tabs, Side.TOP));

        var toRightBtn = new Button("", new FontIcon(Feather.ARROW_RIGHT));
        toRightBtn.getStyleClass().addAll(BUTTON_ICON);
        toRightBtn.setOnAction(e -> rotateTabs(borderPane, tabs, Side.RIGHT));

        var toBottomBtn = new Button("", new FontIcon(Feather.ARROW_DOWN));
        toBottomBtn.getStyleClass().addAll(BUTTON_ICON);
        toBottomBtn.setOnAction(e -> rotateTabs(borderPane, tabs, Side.BOTTOM));

        var toLeftBtn = new Button("", new FontIcon(Feather.ARROW_LEFT));
        toLeftBtn.getStyleClass().addAll(BUTTON_ICON);
        toLeftBtn.setOnAction(e -> rotateTabs(borderPane, tabs, Side.LEFT));

        var appendBtn = new Button("", new FontIcon(Feather.PLUS));
        appendBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);
        appendBtn.setOnAction(e -> tabs.getTabs().add(randomTab()));

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

        var closeableToggle = new ToggleSwitch();
        closeableToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                tabs.setTabClosingPolicy(ALL_TABS);
            } else {
                tabs.setTabClosingPolicy(UNAVAILABLE);
            }
        });

        var floatingToggle = new ToggleSwitch();
        floatingToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) { Styles.toggleStyleClass(tabs, TabPane.STYLE_CLASS_FLOATING); }
        });

        var fullWidthToggle = new ToggleSwitch();
        fullWidthToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) {
                updateTabsWidth(borderPane, tabs, val);
                fullWidth = val;
            }
        });

        var disableToggle = new ToggleSwitch();
        disableToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) { tabs.setDisable(val); }
        });

        var togglesGrid = new GridPane();
        togglesGrid.setHgap(10);
        togglesGrid.setVgap(10);

        togglesGrid.add(gridLabel("Closeable"), 0, 0);
        togglesGrid.add(closeableToggle, 1, 0);

        togglesGrid.add(gridLabel("Floating"), 0, 1);
        togglesGrid.add(floatingToggle, 1, 1);

        togglesGrid.add(gridLabel("Full width"), 0, 2);
        togglesGrid.add(fullWidthToggle, 1, 2);

        togglesGrid.add(gridLabel("Disable"), 0, 3);
        togglesGrid.add(disableToggle, 1, 3);

        // == LAYOUT ==

        var controls = new HBox(40,
                new Spacer(),
                buttonsPane,
                togglesGrid,
                new Spacer()
        );
        controls.setAlignment(Pos.CENTER);

        var content = new VBox(20);
        content.getChildren().setAll(controls);
        content.setAlignment(Pos.CENTER);

        var root = new TitledPane("Controller", content);
        root.setCollapsible(false);

        return root;
    }

    private void updateTabsWidth(BorderPane borderPane, TabPane tabs, boolean val) {
        tabs.tabMinWidthProperty().unbind();

        // reset tab width
        if (!val) {
            tabs.setTabMinWidth(Region.USE_COMPUTED_SIZE);
            return;
        }

        // There are two issues with full-width tabs.
        // - minWidth is applied to the tab itself but to internal .tab-container,
        //   thus we have to subtract tab paddings that are normally set via CSS.
        // - .control-buttons-tab appears automatically and can't be disabled via
        //   TabPane property.
        // Overall this feature should be supported by the TabPane internally, otherwise
        // it's hard to make it work properly.

        if (tabs.getSide() == Side.TOP || tabs.getSide() == Side.BOTTOM) {
            tabs.tabMinWidthProperty().bind(borderPane.widthProperty()
                    .subtract(18) // .control-buttons-tab width
                    .divide(tabs.getTabs().size())
                    .subtract(28) // .tab paddings
            );
        }
        if (tabs.getSide() == Side.LEFT || tabs.getSide() == Side.RIGHT) {
            tabs.tabMinWidthProperty().bind(borderPane.heightProperty()
                    .subtract(18) // same as above
                    .divide(tabs.getTabs().size())
                    .subtract(28)
            );
        }
    }

    private TabPane tabPane() {
        var tabs = new TabPane();
        tabs.setTabClosingPolicy(UNAVAILABLE);
        tabs.setMinHeight(TAB_MIN_HEIGHT);

        // NOTE: Individually disabled tab is still closeable even while it looks
        //       like disabled. To prevent it from closing one can use "black hole"
        //       event handler. #javafx-bug
        tabs.getTabs().addAll(
                randomTab(),
                randomTab(),
                randomTab()
        );

        return tabs;
    }

    private void rotateTabs(BorderPane borderPane, TabPane tabs, Side side) {
        if (tabSide == side) { return; }

        borderPane.getChildren().removeAll(tabs);
        tabSide = side;

        Platform.runLater(() -> {
            tabs.setSide(side);
            switch (side) {
                case TOP -> borderPane.setTop(tabs);
                case RIGHT -> borderPane.setRight(tabs);
                case BOTTOM -> borderPane.setBottom(tabs);
                case LEFT -> borderPane.setLeft(tabs);
            }
            updateTabsWidth(borderPane, tabs, fullWidth);
        });
    }

    private Tab randomTab() {
        var tab = new Tab(FAKER.cat().name());
        tab.setGraphic(new FontIcon(randomIcon()));
        return tab;
    }

    private Label gridLabel(String text) {
        var label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }
}
