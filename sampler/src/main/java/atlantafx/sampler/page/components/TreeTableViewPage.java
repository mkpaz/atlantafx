/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.BORDERED;
import static atlantafx.base.theme.Styles.DENSE;
import static atlantafx.base.theme.Styles.STRIPED;
import static atlantafx.base.theme.Styles.toggleStyleClass;
import static atlantafx.base.theme.Tweaks.ALIGN_CENTER;
import static atlantafx.base.theme.Tweaks.ALIGN_LEFT;
import static atlantafx.base.theme.Tweaks.ALIGN_RIGHT;

import atlantafx.base.controls.CaptionMenuItem;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.fake.domain.Product;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public final class TreeTableViewPage extends OutlinePage {

    public static final String NAME = "TreeTableView";

    @Override
    public String getName() {
        return NAME;
    }

    private TreeTableView<Product> treeTable;

    public TreeTableViewPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]TreeTableView[/i] control is conceptually very similar to the \
            [i]TreeView[/i] and [i]TableView[/i] controls and basically supports the \
            same features. Please, see the corresponding pages for more examples."""
        );
        addSection("Usage", usageExample());
        addSection("Playground", playground());
    }

    @SuppressWarnings("unchecked")
    private ExampleBox usageExample() {
        //snippet_1:start
        record Vehicle(String brand, String model) {

            public static Vehicle random(String make) {
                return new Vehicle(make, FAKER.vehicle().model());
            }
        }

        var col1 = new TreeTableColumn<Vehicle, String>("Brand");
        var col2 = new TreeTableColumn<Vehicle, String>("Model");

        col1.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().getValue().brand())
        );
        col2.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().getValue().model())
        );

        var treeTable = new TreeTableView<Vehicle>();
        treeTable.setColumnResizePolicy(
            TreeTableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        treeTable.getColumns().setAll(col1, col2);

        var make1 = FAKER.vehicle().make();
        var treeItem1 = new TreeItem<Vehicle>(new Vehicle(make1, "..."));
        treeItem1.getChildren().setAll(
            new TreeItem<>(Vehicle.random(make1)),
            new TreeItem<>(Vehicle.random(make1)),
            new TreeItem<>(Vehicle.random(make1))
        );

        var make2 = FAKER.vehicle().make();
        var treeItem2 = new TreeItem<Vehicle>(new Vehicle(make2, "..."));
        treeItem2.getChildren().setAll(
            new TreeItem<>(Vehicle.random(make1)),
            new TreeItem<>(Vehicle.random(make1)),
            new TreeItem<>(Vehicle.random(make1))
        );

        var rootItem = new TreeItem<Vehicle>(new Vehicle("Vehicles", "..."));
        rootItem.getChildren().setAll(treeItem1, treeItem2);
        rootItem.setExpanded(true);

        treeTable.setRoot(rootItem);
        //snippet_1:end

        treeTable.setMaxWidth(Double.MAX_VALUE);
        treeTable.setMinHeight(300);
        HBox.setHgrow(treeTable, Priority.ALWAYS);

        var box = new HBox(treeTable);
        var description = BBCodeParser.createFormattedText("""
            You can create a table view by instantiating the \
            [font=monospace]javafx.scene.control.TreeTableView[/font] class."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playground                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private VBox playground() {
        // == FOOTER ==

        var borderToggle = new ToggleSwitch("Bordered");
        borderToggle.selectedProperty().addListener(
            (obs, old, val) -> toggleStyleClass(treeTable, BORDERED)
        );

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener(
            (obs, old, val) -> toggleStyleClass(treeTable, DENSE)
        );

        var stripeToggle = new ToggleSwitch("Striped");
        stripeToggle.selectedProperty().addListener(
            (obs, old, val) -> toggleStyleClass(treeTable, STRIPED)
        );

        var altIconToggle = new ToggleSwitch("Alt icon");
        altIconToggle.selectedProperty().addListener(
            (obs, old, val) -> toggleStyleClass(treeTable, Tweaks.ALT_ICON)
        );

        var edge2edgeToggle = new ToggleSwitch("Edge to edge");
        edge2edgeToggle.selectedProperty().addListener(
            (obs, old, value) -> toggleStyleClass(treeTable, Tweaks.EDGE_TO_EDGE)
        );

        var footer = new HBox(
            HGAP_20, borderToggle, denseToggle, stripeToggle,
            altIconToggle, edge2edgeToggle
        );
        footer.setAlignment(Pos.CENTER);

        // == TREE TABLE ==

        var rootVal = Product.empty(0);
        rootVal.setBrand("Root");
        var root = new TreeItem<>(rootVal);

        for (int idx = 1; idx <= FAKER.random().nextInt(5, 10); idx++) {
            String brand = FAKER.commerce().brand();
            var groupVal = Product.empty(0);
            groupVal.setBrand(brand);

            var group = new TreeItem<>(groupVal);
            group.getChildren().setAll(
                createTreeItems(idx * 100, FAKER.random().nextInt(5, 10), brand)
            );
            root.getChildren().add(group);
        }

        treeTable = createTreeTable();
        treeTable.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
        treeTable.setRoot(root);
        VBox.setVgrow(treeTable, Priority.ALWAYS);

        // == HEADER ==

        var header = new HBox(createPropertiesMenu(treeTable));
        header.setAlignment(Pos.CENTER_LEFT);

        // ~

        var description = BBCodeParser.createFormattedText("""
            The playground demonstrates the most important [i]TreeTableView[/i] features \
            and also serves as an object for monkey testing."""
        );

        var playground = new VBox(VGAP_10, description, header, treeTable, footer);
        playground.setMinHeight(500);

        return playground;
    }

    private List<TreeItem<Product>> createTreeItems(int startId, int count, String brand) {
        return IntStream.range(startId, startId + count + 1).boxed()
            .map(id -> Product.random(id, brand, FAKER))
            .map(TreeItem::new)
            .toList();
    }

    @SuppressWarnings("unchecked")
    private TreeTableView<Product> createTreeTable() {
        var arrowCol = new TreeTableColumn<Product, String>("#");
        // This is placeholder column for disclosure nodes. We need to fill it
        // with empty strings or each .tree-table-cell will be marked as :empty,
        // which in turn leads to absent borders.
        arrowCol.setCellValueFactory(cell -> new SimpleStringProperty(""));
        arrowCol.setMinWidth(50);
        arrowCol.setMaxWidth(50);

        var stateCol = new TreeTableColumn<Product, Boolean>("State");
        stateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("state"));
        stateCol.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(stateCol));
        stateCol.setEditable(true);

        var idCol = new TreeTableColumn<Product, String>("ID");
        idCol.setCellValueFactory(cell -> {
            Product product = cell.getValue().getValue();
            return new SimpleStringProperty(
                product != null && product.getId() != 0 ? String.valueOf(product.getId()) : ""
            );
        });
        idCol.setEditable(false);
        idCol.setMinWidth(80);

        var brandCol = new TreeTableColumn<Product, String>("Brand");
        brandCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("brand"));
        brandCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(
            generate(() -> FAKER.commerce().brand(), 10).toArray(String[]::new)
        ));
        brandCol.setEditable(true);

        var nameCol = new TreeTableColumn<Product, String>("Name");
        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameCol.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(
            generate(() -> FAKER.commerce().productName(), 10).toArray(String[]::new)
        ));
        nameCol.setEditable(true);
        nameCol.setMinWidth(200);

        var priceCol = new TreeTableColumn<Product, String>("Price");
        priceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        priceCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        priceCol.setEditable(true);

        var table = new TreeTableView<Product>();
        table.getColumns().setAll(
            arrowCol, stateCol, brandCol, idCol, nameCol, priceCol
        );

        return table;
    }

    private MenuButton createPropertiesMenu(TreeTableView<Product> treeTable) {
        final var resizePolCaption = new CaptionMenuItem("Resize Policy");
        final var resizePolGroup = new ToggleGroup();
        resizePolGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof Callback<?, ?> policy) {
                //noinspection rawtypes,unchecked
                treeTable.setColumnResizePolicy((Callback<TreeTableView.ResizeFeatures, Boolean>) policy);
            }
        });

        final var sizeUnconstrainedItem = new RadioMenuItem("Unconstrained");
        sizeUnconstrainedItem.setToggleGroup(resizePolGroup);
        sizeUnconstrainedItem.setUserData(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
        sizeUnconstrainedItem.setSelected(true);

        final var sizeAllColumnsItem = new RadioMenuItem("Resize All Columns");
        sizeAllColumnsItem.setToggleGroup(resizePolGroup);
        sizeAllColumnsItem.setUserData(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        final var sizeLastColumnItem = new RadioMenuItem("Resize Last Column");
        sizeLastColumnItem.setToggleGroup(resizePolGroup);
        sizeLastColumnItem.setUserData(TreeTableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);

        final var sizeNextColumnItem = new RadioMenuItem("Resize Next Column");
        sizeNextColumnItem.setToggleGroup(resizePolGroup);
        sizeNextColumnItem.setUserData(TreeTableView.CONSTRAINED_RESIZE_POLICY_NEXT_COLUMN);

        final var sizeSubsequentItem = new RadioMenuItem("Resize Subsequent Column");
        sizeSubsequentItem.setToggleGroup(resizePolGroup);
        sizeSubsequentItem.setUserData(TreeTableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        final var sizeFlexHeadItem = new RadioMenuItem("Flex Head");
        sizeFlexHeadItem.setToggleGroup(resizePolGroup);
        sizeFlexHeadItem.setUserData(TreeTableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);

        final var sizeFlexTailItem = new RadioMenuItem("Flex Tail");
        sizeFlexTailItem.setToggleGroup(resizePolGroup);
        sizeFlexTailItem.setUserData(TreeTableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // ~

        final var selModeCaption = new CaptionMenuItem("Selection Mode");
        final var selModeGroup = new ToggleGroup();
        selModeGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof SelectionMode mode) {
                treeTable.getSelectionModel().setSelectionMode(mode);
            }
        });

        final var singleSelItem = new RadioMenuItem("Single");
        singleSelItem.setToggleGroup(selModeGroup);
        singleSelItem.setUserData(SelectionMode.SINGLE);

        final var multiSelItem = new RadioMenuItem("Multiple");
        multiSelItem.setToggleGroup(selModeGroup);
        multiSelItem.setUserData(SelectionMode.MULTIPLE);
        multiSelItem.setSelected(true);

        // ~

        final var alignLeftItem = new RadioMenuItem("Left");
        alignLeftItem.setSelected(true);
        alignLeftItem.setOnAction(e -> {
            for (TreeTableColumn<?, ?> c : treeTable.getColumns()) {
                c.getStyleClass().removeAll(ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT);
            }
        });

        final var alignCenterItem = new RadioMenuItem("Center");
        alignCenterItem.setOnAction(e -> {
            for (TreeTableColumn<?, ?> c : treeTable.getColumns()) {
                addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
            }
        });

        final var alignRightItem = new RadioMenuItem("Right");
        alignRightItem.setOnAction(e -> {
            for (TreeTableColumn<?, ?> c : treeTable.getColumns()) {
                addStyleClass(c, ALIGN_RIGHT, ALIGN_LEFT, ALIGN_CENTER);
            }
        });

        final var alignItem = new Menu("Alignment");
        alignItem.getItems().setAll(alignLeftItem, alignCenterItem, alignRightItem);

        final var alignGroup = new ToggleGroup();
        alignGroup.getToggles().setAll(alignLeftItem, alignCenterItem, alignRightItem);

        // ~

        final var showRootItem = new CheckMenuItem("Show root");
        treeTable.showRootProperty().bind(showRootItem.selectedProperty());

        final var editItem = new CheckMenuItem("Editable");
        treeTable.editableProperty().bind(editItem.selectedProperty());
        editItem.setSelected(true);

        final var cellSelItem = new CheckMenuItem("Enable cell selection");
        treeTable.getSelectionModel().cellSelectionEnabledProperty().bind(cellSelItem.selectedProperty());
        cellSelItem.setSelected(false);

        // ~

        final var menuBtnItem = new CheckMenuItem("Show menu button");
        treeTable.tableMenuButtonVisibleProperty().bind(menuBtnItem.selectedProperty());
        menuBtnItem.setSelected(true);

        final var propsMenu = new MenuButton("Properties");
        propsMenu.getItems().setAll(
            resizePolCaption,
            sizeUnconstrainedItem,
            sizeAllColumnsItem,
            sizeLastColumnItem,
            sizeNextColumnItem,
            sizeSubsequentItem,
            sizeFlexHeadItem,
            sizeFlexTailItem,
            new SeparatorMenuItem(),
            selModeCaption,
            singleSelItem,
            multiSelItem,
            new SeparatorMenuItem(),
            alignItem,
            showRootItem,
            editItem,
            cellSelItem,
            menuBtnItem
        );

        return propsMenu;
    }

    private void addStyleClass(TreeTableColumn<?, ?> c,
                               String styleClass,
                               String... excludes) {
        Objects.requireNonNull(c);
        Objects.requireNonNull(styleClass);

        if (excludes != null && excludes.length > 0) {
            c.getStyleClass().removeAll(excludes);
        }
        c.getStyleClass().add(styleClass);
    }
}
