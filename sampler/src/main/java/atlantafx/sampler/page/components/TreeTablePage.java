/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.CaptionMenuItem;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.sampler.fake.domain.Product;
import atlantafx.sampler.page.AbstractPage;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.List;
import java.util.stream.IntStream;

import static atlantafx.base.theme.Styles.*;

public class TreeTablePage extends AbstractPage {

    public static final String NAME = "TreeTableView";

    @Override
    public String getName() { return NAME; }

    private TreeTableView<Product> treeTable;

    public TreeTablePage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(
                playground()
        );
    }

    private VBox playground() {
        var bordersToggle = new ToggleSwitch("Bordered");
        bordersToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(treeTable, BORDERED));

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(treeTable, DENSE));

        var stripesToggle = new ToggleSwitch("Striped");
        stripesToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(treeTable, STRIPED));

        var disableToggle = new ToggleSwitch("Disable");
        disableToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) { treeTable.setDisable(val); }
        });

        var togglesBox = new HBox(20,
                                  bordersToggle,
                                  denseToggle,
                                  stripesToggle,
                                  disableToggle
        );
        togglesBox.setAlignment(Pos.CENTER_LEFT);

        // ~

        var rootVal = Product.empty(0);
        rootVal.setBrand("Root");
        var root = new TreeItem<>(rootVal);

        for (int idx = 1; idx <= FAKER.random().nextInt(5, 10); idx++) {
            String brand = FAKER.commerce().brand();
            var groupVal = Product.empty(0);
            groupVal.setBrand(brand);

            var group = new TreeItem<>(groupVal);
            group.getChildren().setAll(
                    treeItems(idx * 100, FAKER.random().nextInt(5, 10), brand)
            );
            root.getChildren().add(group);
        }

        treeTable = treeTable();
        treeTable.setRoot(root);

        // ~

        var topBox = new HBox(
                new Label("Example:"),
                new Spacer(),
                settingsMenu(treeTable)
        );
        topBox.setAlignment(Pos.CENTER_LEFT);

        var playground = new VBox(10);
        playground.setMinHeight(100);
        playground.getChildren().setAll(
                topBox,
                treeTable,
                togglesBox
        );

        return playground;
    }

    private List<TreeItem<Product>> treeItems(int startId, int count, String brand) {
        return IntStream.range(startId, startId + count + 1).boxed()
                .map(id -> Product.random(id, brand, FAKER))
                .map(TreeItem::new)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private TreeTableView<Product> treeTable() {
        var arrowCol = new TreeTableColumn<Product, String>("#");
        // This is placeholder column for disclosure nodes. We need to fill it
        // with empty strings or all .tree-table-cell will be marked as :empty,
        // which in turn leads to absent borders.
        arrowCol.setCellValueFactory(cell -> new SimpleStringProperty(""));
        arrowCol.setMinWidth(50);
        arrowCol.setMaxWidth(50);

        var stateCol = new TreeTableColumn<Product, Boolean>("Selected");
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

        var brandCol = new TreeTableColumn<Product, String>("Brand ✎");
        brandCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("brand"));
        brandCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(
                generate(() -> FAKER.commerce().brand(), 10).toArray(String[]::new)
        ));
        brandCol.setEditable(true);

        var nameCol = new TreeTableColumn<Product, String>("Name ✎");
        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameCol.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(
                generate(() -> FAKER.commerce().productName(), 10).toArray(String[]::new)
        ));
        nameCol.setEditable(true);
        nameCol.setMinWidth(200);

        var priceCol = new TreeTableColumn<Product, String>("Price ✎");
        priceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        priceCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        priceCol.setEditable(true);

        var table = new TreeTableView<Product>();
        table.getColumns().setAll(arrowCol, stateCol, brandCol, idCol, nameCol, priceCol);

        return table;
    }

    private MenuButton settingsMenu(TreeTableView<Product> treeTable) {
        var resizePolicyCaption = new CaptionMenuItem("Resize Policy");
        var resizePolicyGroup = new ToggleGroup();
        resizePolicyGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof Callback policy) {
                //noinspection rawtypes,unchecked
                treeTable.setColumnResizePolicy((Callback<TreeTableView.ResizeFeatures, Boolean>) policy);
            }
        });

        var unconstrainedResizeItem = new RadioMenuItem("Unconstrained");
        unconstrainedResizeItem.setToggleGroup(resizePolicyGroup);
        unconstrainedResizeItem.setUserData(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
        unconstrainedResizeItem.setSelected(true);

        var constrainedResizeItem = new RadioMenuItem("Constrained");
        constrainedResizeItem.setToggleGroup(resizePolicyGroup);
        constrainedResizeItem.setUserData(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        // ~

        var selectionModeCaption = new CaptionMenuItem("Selection Mode");
        var selectionModeGroup = new ToggleGroup();
        selectionModeGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof SelectionMode mode) {
                treeTable.getSelectionModel().setSelectionMode(mode);
            }
        });

        var singleSelectionItem = new RadioMenuItem("Single");
        singleSelectionItem.setToggleGroup(selectionModeGroup);
        singleSelectionItem.setUserData(SelectionMode.SINGLE);

        var multiSelectionItem = new RadioMenuItem("Multiple");
        multiSelectionItem.setToggleGroup(selectionModeGroup);
        multiSelectionItem.setUserData(SelectionMode.MULTIPLE);
        multiSelectionItem.setSelected(true);

        // ~

        var showRootItem = new CheckMenuItem("Show root");
        treeTable.showRootProperty().bind(showRootItem.selectedProperty());
        showRootItem.setSelected(true);

        var editCellsItem = new CheckMenuItem("Editable");
        treeTable.editableProperty().bind(editCellsItem.selectedProperty());
        editCellsItem.setSelected(true);

        var cellSelectionItem = new CheckMenuItem("Enable cell selection");
        treeTable.getSelectionModel().cellSelectionEnabledProperty().bind(cellSelectionItem.selectedProperty());
        cellSelectionItem.setSelected(false);

        var menuButtonItem = new CheckMenuItem("Show menu button");
        treeTable.tableMenuButtonVisibleProperty().bind(menuButtonItem.selectedProperty());
        menuButtonItem.setSelected(true);

        return new MenuButton("Settings") {{
            getItems().setAll(
                    resizePolicyCaption,
                    unconstrainedResizeItem,
                    constrainedResizeItem,
                    selectionModeCaption,
                    singleSelectionItem,
                    multiSelectionItem,
                    new SeparatorMenuItem(),
                    showRootItem,
                    editCellsItem,
                    cellSelectionItem,
                    menuButtonItem
            );
        }};
    }
}
