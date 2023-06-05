/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static javafx.scene.control.TabPane.TabClosingPolicy;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class TabPanePage extends OutlinePage {

    public static final String NAME = "TabPane";

    @Override
    public String getName() {
        return NAME;
    }

    public TabPanePage() {
        super();

        addPageHeader();
        addFormattedText("""
            [i]TabPane[/i] is a control that provides a container for a group of tabs. By clicking \
            on a tab, the content of that tab becomes visible, while the content of the previously \
            selected tab gets hidden."""
        );
        addSection("Usage", usageExample());
        addSection("Tab Style", tabStyleExample());
        addSection("Dense", denseExample());
        addSection("Playground", playground());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var tab1 = new Tab("Tab 1");
        tab1.setGraphic(new FontIcon(randomIcon()));

        var tab2 = new Tab("Tab 2");
        tab2.setGraphic(new FontIcon(randomIcon()));

        var tab3 = new Tab("Tab 3");
        tab3.setGraphic(new FontIcon(randomIcon()));

        var tabs = new TabPane(tab1, tab2, tab3);
        tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabs.setMinWidth(450);
        //snippet_1:end

        var box = new HBox(tabs);
        box.setMinHeight(50);

        var description = BBCodeParser.createFormattedText("""
            Tabs are placed within a [font=monospace]TabPane[/font], where each tab represents a single \
            "page". Any node such as controls or groups of nodes added to a tab layout container. When \
            the user clicks on a tab in the tab content becomes visible."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox tabStyleExample() {
        //snippet_2:start
        var defaultTabs = new TabPane(
            new Tab("Tab 1"), new Tab("Tab 1"), new Tab("Tab 3")
        );
        defaultTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        defaultTabs.setMinWidth(450);

        var floatingTabs = new TabPane(
            new Tab("Tab 1"), new Tab("Tab 2"), new Tab("Tab 3")
        );
        floatingTabs.getStyleClass().add(Styles.TABS_FLOATING);
        floatingTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        floatingTabs.setMinWidth(450);

        var classicTabs = new TabPane(
            new Tab("Tab 1"), new Tab("Tab 2"), new Tab("Tab 3")
        );
        classicTabs.getStyleClass().add(Styles.TABS_CLASSIC);
        classicTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        classicTabs.setMinWidth(450);
        //snippet_2:end

        var box = new VBox(VGAP_20, defaultTabs, floatingTabs, classicTabs);
        box.setMinHeight(200);

        var description = BBCodeParser.createFormattedText("""
            You can use two additional styles classes to modify tab pane style:
                        
            [ul]
            [li][code]Styles.TABS_FLOATING[/code][/li]
            [li][code]Styles.TABS_CLASSIC[/code][/li][/ul]"""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox denseExample() {
        //snippet_4:start
        var tab1 = new Tab("One");
        tab1.setGraphic(new FontIcon(randomIcon()));

        var tab2 = new Tab("Two");
        tab2.setGraphic(new FontIcon(randomIcon()));

        var tab3 = new Tab("Three");
        tab3.setGraphic(new FontIcon(randomIcon()));

        var tabs = new TabPane(tab1, tab2, tab3);
        tabs.getStyleClass().add(Styles.DENSE);
        tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabs.setMinWidth(450);
        //snippet_4:end

        var box = new HBox(tabs);
        box.setMinHeight(50);

        var description = BBCodeParser.createFormattedText("""
            There's also [code]Styles.DENSE[/code] to make tabs look more compact by cutting label padding."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playground                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private Side tabSide = Side.TOP;
    private boolean fullWidth = false;

    private Pane playground() {
        var tabs = createTabPane();

        var tabsLayer = new BorderPane();
        tabsLayer.setTop(tabs);
        tabs.getTabs().addListener((ListChangeListener<Tab>) c ->
            updateTabsWidth(tabsLayer, tabs, fullWidth)
        );

        var controller = createController(tabsLayer, tabs);

        var controllerLayer = new BorderPane(controller);
        controllerLayer.setMinSize(500, 300);
        controllerLayer.setMaxSize(500, 300);

        var description = BBCodeParser.createFormattedText("""
            The playground demonstrates the most important [i]TabPane[/i] features \
            and also serves as an object for monkey testing."""
        );

        var stack = new StackPane(tabsLayer, controllerLayer);
        stack.getStyleClass().add(Styles.BORDERED);
        stack.setMinSize(600, 500);

        return new VBox(VGAP_10, description, stack);
    }

    @SuppressWarnings("unchecked")
    private TitledPane createController(BorderPane borderPane, TabPane tabs) {
        // == BUTTONS ==

        var toTopBtn = new Button(null, new FontIcon(Feather.ARROW_UP));
        toTopBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        toTopBtn.setOnAction(e -> rotateTabs(borderPane, tabs, Side.TOP));

        var toRightBtn = new Button(null, new FontIcon(Feather.ARROW_RIGHT));
        toRightBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        toRightBtn.setOnAction(e -> rotateTabs(borderPane, tabs, Side.RIGHT));

        var toBottomBtn = new Button(null, new FontIcon(Feather.ARROW_DOWN));
        toBottomBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        toBottomBtn.setOnAction(e -> rotateTabs(borderPane, tabs, Side.BOTTOM));

        var toLeftBtn = new Button(null, new FontIcon(Feather.ARROW_LEFT));
        toLeftBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        toLeftBtn.setOnAction(e -> rotateTabs(borderPane, tabs, Side.LEFT));

        var appendBtn = new Button(null, new FontIcon(Feather.PLUS));
        appendBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT);
        appendBtn.setOnAction(e -> tabs.getTabs().add(createRandomTab()));

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

        var closeableToggle = new ToggleSwitch();
        closeableToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
            } else {
                tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
            }
        });

        var animatedToggle = new ToggleSwitch();
        animatedToggle.setSelected(true);
        animatedToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null && val) {
                tabs.setStyle("");
            } else {
                tabs.setStyle("""
                    -fx-open-tab-animation:none;\
                    -fx-close-tab-animation:none;"""
                );
            }
        });

        var fullWidthToggle = new ToggleSwitch();
        fullWidthToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) {
                updateTabsWidth(borderPane, tabs, val);
                fullWidth = val;
            }
        });

        var denseToggle = new ToggleSwitch();
        denseToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(tabs, Styles.DENSE)
        );

        var disableToggle = new ToggleSwitch();
        disableToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) {
                tabs.setDisable(val);
            }
        });

        var togglesGrid = new GridPane();
        togglesGrid.setHgap(10);
        togglesGrid.setVgap(10);
        togglesGrid.addRow(0, createGridLabel("Closeable"), closeableToggle);
        togglesGrid.addRow(1, createGridLabel("Animated"), animatedToggle);
        togglesGrid.addRow(2, createGridLabel("Full width"), fullWidthToggle);
        togglesGrid.addRow(3, createGridLabel("Dense"), denseToggle);
        togglesGrid.addRow(4, createGridLabel("Disable"), disableToggle);

        // == TAB STYLE ==

        var styleToggleGroup = new ToggleGroup();

        var defaultStyleToggle = new ToggleButton("Default");
        defaultStyleToggle.setToggleGroup(styleToggleGroup);
        defaultStyleToggle.setUserData(
            List.of("whatever", Styles.TABS_FLOATING, Styles.TABS_CLASSIC)
        );
        defaultStyleToggle.setSelected(true);

        var floatingStyleToggle = new ToggleButton("Floating");
        floatingStyleToggle.setToggleGroup(styleToggleGroup);
        floatingStyleToggle.setUserData(
            List.of(Styles.TABS_FLOATING, "whatever", Styles.TABS_CLASSIC)
        );

        var classicStyleToggle = new ToggleButton("Classic");
        classicStyleToggle.setToggleGroup(styleToggleGroup);
        classicStyleToggle.setUserData(
            List.of(Styles.TABS_CLASSIC, "whatever", Styles.TABS_FLOATING)
        );

        styleToggleGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null) {
                List<String> classes = (List<String>) val.getUserData();
                Styles.addStyleClass(tabs, classes.get(0), classes.get(1), classes.get(2));
            }
        });

        var styleBox = new InputGroup(
            defaultStyleToggle, floatingStyleToggle, classicStyleToggle
        );
        styleBox.setAlignment(Pos.CENTER);

        // == LAYOUT ==

        var controls = new HBox(
            40, new Spacer(), buttonsPane, togglesGrid, new Spacer()
        );
        controls.setAlignment(Pos.CENTER);

        var root = new TitledPane("Controller", new VBox(30, controls, styleBox));
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

    private TabPane createTabPane() {
        var tabs = new TabPane();
        tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabs.setMinHeight(60);

        // NOTE: Individually disabled tab is still closeable even while it looks
        //       like disabled. To prevent it from closing one can use "black hole"
        //       event handler. #javafx-bug
        tabs.getTabs().addAll(
            createRandomTab(),
            createRandomTab(),
            createRandomTab()
        );

        return tabs;
    }

    private void rotateTabs(BorderPane borderPane, TabPane tabs, Side side) {
        if (tabSide == side) {
            return;
        }

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

    private Tab createRandomTab() {
        var tab = new Tab(FAKER.cat().name());
        tab.setGraphic(new FontIcon(randomIcon()));
        return tab;
    }

    private Label createGridLabel(String text) {
        var label = new Label(text);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }
}
