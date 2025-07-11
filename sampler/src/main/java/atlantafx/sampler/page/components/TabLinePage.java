/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Tab;
import atlantafx.base.controls.TabContextMenu;
import atlantafx.base.controls.TabLine;
import atlantafx.base.controls.TabMenuButton;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URI;
import java.util.UUID;

public final class TabLinePage extends OutlinePage {

    public static final String NAME = "TabLine";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public TabLinePage() {
        super();

        addPageHeader();
        addFormattedText("""
            TabLine is a basic tab view control. Unlike the standard JavaFX TabPane, it does not include \
            a content pane; you must manually update the view when the active tab changes. Additionally, \
            it does not support vertical orientation. However, it allows for the placement of custom controls, \
            such as buttons, alongside the tabs and gives you full control over the tab line overflow behavior."""
        );
        addSection("Usage", usageExample());
        addSection("Styles", stylesExample());
        addSection("Pinning Tabs", pinTabsExample());
        addSection("Custom Controls", customControlsExample());
        addSection("Closing Policy", closingPolicyExample());
        addSection("Resize Policy", resizePolicyExample());
        addSection("Dense", denseExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var tabLine = new TabLine();
        tabLine.getTabs().setAll(
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()))
        );
        tabLine.setTabDragPolicy(Tab.DragPolicy.REORDER);
        tabLine.setTabResizePolicy(Tab.ResizePolicy.FIXED_WIDTH);
        tabLine.setTabClosingPolicy(Tab.ClosingPolicy.SELECTED_TAB);

        var label = new Label();
        var content = new BorderPane(label);
        tabLine.getSelectionModel().selectedItemProperty().subscribe(tab -> {
            if (tab != null) {
                label.setText(tab.getText());
            } else {
                label.setText(null);
            }
        });
        //snippet_1:end

        var box = new VBox(10, tabLine, content);
        var description = BBCodeParser.createFormattedText("""
            Each tab should have a unique ID or index, which you will use to implement \
            the content-switching logic. While this may not seem convenient, it is a more \
            flexible approach that allows you to update a view partially within a complex layout"""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox stylesExample() {
        //snippet_2:start
        var tabLine1 = new TabLine();
        tabLine1.getTabs().setAll(
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()))
        );
        tabLine1.getStyleClass().add(Styles.TABS_FLOATING);
        tabLine1.setTabDragPolicy(Tab.DragPolicy.REORDER);
        tabLine1.setTabResizePolicy(Tab.ResizePolicy.FIXED_WIDTH);
        tabLine1.setTabClosingPolicy(Tab.ClosingPolicy.SELECTED_TAB);

        var tabLine2 = new TabLine();
        tabLine2.getTabs().setAll(
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()))
        );
        tabLine2.getStyleClass().add(Styles.TABS_BORDER_TOP);
        tabLine2.setTabDragPolicy(Tab.DragPolicy.REORDER);
        tabLine2.setTabResizePolicy(Tab.ResizePolicy.FIXED_WIDTH);
        tabLine2.setTabClosingPolicy(Tab.ClosingPolicy.SELECTED_TAB);
        //snippet_2:end

        var box = new VBox(10, tabLine1, tabLine2);
        var description = BBCodeParser.createFormattedText("""
            You can use two additional styles classes to modify tabs style:
            
            [ul]
            [li][code]Styles.TABS_FLOATING[/code][/li]
            [li][code]Styles.TABS_BORDER_TOP[/code][/li][/ul]"""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox pinTabsExample() {
        //snippet_3:start
        var tabLine = new TabLine();
        tabLine.getTabs().setAll(
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()))
        );

        var contextMenu = new TabContextMenu();
        tabLine.getTabs().forEach(tab -> tab.setContextMenu(contextMenu));

        tabLine.setTabResizePolicy(Tab.ResizePolicy.FIXED_WIDTH);
        tabLine.setTabClosingPolicy(Tab.ClosingPolicy.SELECTED_TAB);

        var pin = new MenuItem("Pin");
        pin.setOnAction(e -> {
            if (contextMenu.getOwnerTab() != null) {
                contextMenu.getOwnerTab().setPinned(true);
            }
        });

        var unpin = new MenuItem("Unpin");
        unpin.setOnAction(e -> {
            if (contextMenu.getOwnerTab() != null) {
                contextMenu.getOwnerTab().setPinned(false);
            }
        });

        contextMenu.getItems().setAll(pin, unpin);
        //snippet_3:end

        var box = new VBox(10, tabLine);
        var description = BBCodeParser.createFormattedText("""
            You can pin tabs using the [code]pinnedProperty()[/code]. \
            The context menu is suitable for this purpose. \
            The pinned tab will be automatically placed after the last pinned tab \
            or at the beginning of the container."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox customControlsExample() {
        //snippet_4:start
        var addBtn = new Button("", new FontIcon(Feather.PLUS));
        addBtn.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);

        var tabLine = new TabLine();
        tabLine.getTabs().setAll(
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()))
        );
        tabLine.setTabResizePolicy(Tab.ResizePolicy.FIXED_WIDTH);
        tabLine.setTabClosingPolicy(Tab.ClosingPolicy.SELECTED_TAB);

        tabLine.setLeftNode(hbox(addBtn));
        tabLine.setRightNode(hbox(new TabMenuButton(tabLine)));

        addBtn.setOnAction(e -> {
            var tab = new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()));
            tabLine.getTabs().add(tab);
            tabLine.getSelectionModel().select(tab);
        });
        //snippet_4:end

        var box = new VBox(10, tabLine);
        var description = BBCodeParser.createFormattedText("""
            There are two slots available for placing user controls: one on the left side \
            and one on the right side. Note that you should style the controls to appear flat using CSS."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }

    private ExampleBox closingPolicyExample() {
        //snippet_5:start
        var addBtn = new Button("", new FontIcon(Feather.PLUS));
        addBtn.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);

        var tabLine = new TabLine();
        tabLine.getTabs().setAll(
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()))
        );
        tabLine.setTabResizePolicy(Tab.ResizePolicy.FIXED_WIDTH);
        tabLine.setTabClosingPolicy(Tab.ClosingPolicy.SELECTED_TAB);

        var allTabsPolicy = new MenuItem("All");
        allTabsPolicy.setOnAction(e ->
            tabLine.setTabClosingPolicy(Tab.ClosingPolicy.ALL_TABS)
        );

        var selectedTabsPolicy = new MenuItem("Selected");
        selectedTabsPolicy.setOnAction(e ->
            tabLine.setTabClosingPolicy(Tab.ClosingPolicy.SELECTED_TAB)
        );

        var noTabsPolicy = new MenuItem("None");
        noTabsPolicy.setOnAction(e ->
            tabLine.setTabClosingPolicy(Tab.ClosingPolicy.NO_TABS)
        );

        addBtn.setOnAction(e -> {
            var tab = new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()));
            tabLine.getTabs().add(tab);
            tabLine.getSelectionModel().select(tab);
        });

        var menuButton = new MenuButton("Policy");
        menuButton.setMinWidth(100);
        menuButton.getStyleClass().add(Styles.FLAT);
        menuButton.setPadding(Insets.EMPTY);
        menuButton.getItems().setAll(
            allTabsPolicy, selectedTabsPolicy, noTabsPolicy
        );

        tabLine.setLeftNode(hbox(addBtn));
        tabLine.setRightNode(hbox(menuButton));
        //snippet_5:end

        var box = new VBox(10, tabLine);
        var description = BBCodeParser.createFormattedText("""
            The ClosingPolicy defines whether a tab can be closed or pinned. \
            The standard TabPane policies are all available, but you can also implement your own."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 5), description);
    }

    private ExampleBox resizePolicyExample() {
        //snippet_6:start
        var addBtn = new Button("", new FontIcon(Feather.PLUS));
        addBtn.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);

        var tabLine = new TabLine();
        tabLine.getTabs().setAll(
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()))
        );
        tabLine.setTabResizePolicy(Tab.ResizePolicy.FIXED_WIDTH);
        tabLine.setTabClosingPolicy(Tab.ClosingPolicy.SELECTED_TAB);

        var fixedTabsPolicy = new MenuItem("Fixed");
        fixedTabsPolicy.setOnAction(e ->
            tabLine.setTabResizePolicy(Tab.ResizePolicy.FIXED_WIDTH)
        );

        var computedTabsPolicy = new MenuItem("Computed");
        computedTabsPolicy.setOnAction(e ->
            tabLine.setTabResizePolicy(Tab.ResizePolicy.COMPUTED_WIDTH)
        );

        var adaptiveTabsPolicy = new MenuItem("Adaptive");
        adaptiveTabsPolicy.setOnAction(e ->
            tabLine.setTabResizePolicy(Tab.ResizePolicy.ADAPTIVE)
        );

        var stretchTabsPolicy = new MenuItem("Stretch");
        stretchTabsPolicy.setOnAction(e ->
            tabLine.setTabResizePolicy(Tab.ResizePolicy.STRETCH)
        );

        addBtn.setOnAction(e -> {
            var tab = new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()));
            tabLine.getTabs().add(tab);
            tabLine.getSelectionModel().select(tab);
        });

        var menuButton = new MenuButton("Policy");
        menuButton.setMinWidth(100);
        menuButton.getStyleClass().add(Styles.FLAT);
        menuButton.getItems().setAll(
            fixedTabsPolicy, computedTabsPolicy, adaptiveTabsPolicy, stretchTabsPolicy
        );

        tabLine.setLeftNode(hbox(addBtn));
        tabLine.setRightNode(hbox(menuButton));
        //snippet_6:end

        var box = new VBox(10, tabLine);
        var description = BBCodeParser.createFormattedText("""
            The ResizePolicy defines the strategy for calculating the width of tabs. \
            It determines what to do when the tabs overflow the viewport size: can they be scrolled? \
            Should the tab width adjust to match the tab content, or should it be fixed? And so on."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 6), description);
    }

    private ExampleBox denseExample() {
        //snippet_7:start
        var tabLine1 = new TabLine();
        tabLine1.getTabs().setAll(
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()))
        );
        tabLine1.getStyleClass().addAll(Styles.DENSE);

        var tabLine2 = new TabLine();
        tabLine2.getTabs().setAll(
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon())),
            new Tab(uuid(), FAKER.animal().name(), new FontIcon(randomIcon()))
        );
        tabLine2.getStyleClass().addAll(Styles.TABS_FLOATING, Styles.DENSE);
        //snippet_7:end

        var box = new VBox(10, tabLine1, tabLine2);
        var description = BBCodeParser.createFormattedText("""
            There's also [code]Styles.DENSE[/code] to make tabs look more compact."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 7), description);
    }

    /// ////////////////////////////////////////////////////////////////////////

    private String uuid() {
        return UUID.randomUUID().toString();
    }

    private HBox hbox(Node items) {
        var hbox = new HBox(items);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(0, 5, 0, 5));
        return hbox;
    }
}
