/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.BORDERED;
import static atlantafx.base.theme.Styles.DENSE;
import static atlantafx.base.theme.Styles.STRIPED;
import static atlantafx.base.theme.Styles.toggleStyleClass;
import static atlantafx.base.theme.Tweaks.ALIGN_CENTER;
import static atlantafx.base.theme.Tweaks.ALIGN_LEFT;
import static atlantafx.base.theme.Tweaks.ALIGN_RIGHT;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.base.controls.CaptionMenuItem;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.fake.domain.Product;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
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
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class TreeTablePage extends AbstractPage {

    public static final String NAME = "TreeTableView";

    @Override
    public String getName() {
        return NAME;
    }

    private TreeTableView<Product> treeTable;

    public TreeTablePage() {
        super();

        var sample = new SampleBlock("Playground", createPlayground());
        sample.setFillHeight(true);
        setUserContent(sample);
    }

    private VBox createPlayground() {
        // == FOOTER ==

        var bordersToggle = new ToggleSwitch("Bordered");
        bordersToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(treeTable, BORDERED));

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(treeTable, DENSE));

        var stripesToggle = new ToggleSwitch("Striped");
        stripesToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(treeTable, STRIPED));

        var altIconToggle = new ToggleSwitch("Alt icon");
        altIconToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(treeTable, Tweaks.ALT_ICON));

        var edge2edgeToggle = new ToggleSwitch("Edge to edge");
        edge2edgeToggle.selectedProperty().addListener(
            (obs, old, value) -> toggleStyleClass(treeTable, Tweaks.EDGE_TO_EDGE)
        );

        var footer = new HBox(BLOCK_HGAP, bordersToggle, denseToggle, stripesToggle, altIconToggle, edge2edgeToggle);
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
        treeTable.setRoot(root);
        VBox.setVgrow(treeTable, Priority.ALWAYS);

        // == HEADER ==

        var alignGroup = new ToggleGroup();

        var alignLeftBtn = new ToggleButton("", new FontIcon(Feather.ALIGN_LEFT));
        alignLeftBtn.getStyleClass().add(Styles.LEFT_PILL);
        alignLeftBtn.setToggleGroup(alignGroup);
        alignLeftBtn.setSelected(true);
        alignLeftBtn.setOnAction(e -> {
            for (TreeTableColumn<?, ?> c : treeTable.getColumns()) {
                c.getStyleClass().removeAll(ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT);
            }
        });

        var alignCenterBtn = new ToggleButton("", new FontIcon(Feather.ALIGN_CENTER));
        alignCenterBtn.getStyleClass().add(Styles.CENTER_PILL);
        alignCenterBtn.setToggleGroup(alignGroup);
        alignCenterBtn.selectedProperty().addListener((obs, old, val) -> {
            for (TreeTableColumn<?, ?> c : treeTable.getColumns()) {
                addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
            }
        });

        var alignRightBtn = new ToggleButton("", new FontIcon(Feather.ALIGN_RIGHT));
        alignRightBtn.getStyleClass().add(Styles.RIGHT_PILL);
        alignRightBtn.setToggleGroup(alignGroup);
        alignRightBtn.selectedProperty().addListener((obs, old, val) -> {
            for (TreeTableColumn<?, ?> c : treeTable.getColumns()) {
                addStyleClass(c, ALIGN_RIGHT, ALIGN_LEFT, ALIGN_CENTER);
            }
        });

        var alignBox = new HBox(alignLeftBtn, alignCenterBtn, alignRightBtn);

        var disableToggle = new ToggleSwitch("Disable");
        disableToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) {
                treeTable.setDisable(val);
            }
        });

        var header = new HBox(
            createPropertiesMenu(treeTable),
            new Spacer(),
            alignBox,
            new Spacer(),
            disableToggle
        );
        header.setAlignment(Pos.CENTER_LEFT);

        // ~

        var playground = new VBox(BLOCK_VGAP, header, treeTable, footer);
        playground.setMinHeight(100);

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

        var brandCol = new TreeTableColumn<Product, String>("Brand  🖉");
        brandCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("brand"));
        brandCol.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(
            generate(() -> FAKER.commerce().brand(), 10).toArray(String[]::new)
        ));
        brandCol.setEditable(true);

        var nameCol = new TreeTableColumn<Product, String>("Name  🖉");
        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameCol.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(
            generate(() -> FAKER.commerce().productName(), 10).toArray(String[]::new)
        ));
        nameCol.setEditable(true);
        nameCol.setMinWidth(200);

        var priceCol = new TreeTableColumn<Product, String>("Price  🖉");
        priceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        priceCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        priceCol.setEditable(true);

        var table = new TreeTableView<Product>();
        table.getColumns().setAll(arrowCol, stateCol, brandCol, idCol, nameCol, priceCol);

        return table;
    }

    private MenuButton createPropertiesMenu(TreeTableView<Product> treeTable) {
        final var resizePolicyCaption = new CaptionMenuItem("Resize Policy");
        final var resizePolicyGroup = new ToggleGroup();
        resizePolicyGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof Callback<?, ?> policy) {
                //noinspection rawtypes,unchecked
                treeTable.setColumnResizePolicy((Callback<TreeTableView.ResizeFeatures, Boolean>) policy);
            }
        });

        final var unconstrainedResizeItem = new RadioMenuItem("Unconstrained");
        unconstrainedResizeItem.setToggleGroup(resizePolicyGroup);
        unconstrainedResizeItem.setUserData(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
        unconstrainedResizeItem.setSelected(true);

        final var constrainedResizeItem = new RadioMenuItem("Constrained");
        constrainedResizeItem.setToggleGroup(resizePolicyGroup);
        constrainedResizeItem.setUserData(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        // ~

        final var selectionModeCaption = new CaptionMenuItem("Selection Mode");
        final var selectionModeGroup = new ToggleGroup();
        selectionModeGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof SelectionMode mode) {
                treeTable.getSelectionModel().setSelectionMode(mode);
            }
        });

        final var singleSelectionItem = new RadioMenuItem("Single");
        singleSelectionItem.setToggleGroup(selectionModeGroup);
        singleSelectionItem.setUserData(SelectionMode.SINGLE);

        final var multiSelectionItem = new RadioMenuItem("Multiple");
        multiSelectionItem.setToggleGroup(selectionModeGroup);
        multiSelectionItem.setUserData(SelectionMode.MULTIPLE);
        multiSelectionItem.setSelected(true);

        // ~

        final var showRootItem = new CheckMenuItem("Show root");
        treeTable.showRootProperty().bind(showRootItem.selectedProperty());
        showRootItem.setSelected(true);

        final var editCellsItem = new CheckMenuItem("Editable");
        treeTable.editableProperty().bind(editCellsItem.selectedProperty());
        editCellsItem.setSelected(true);

        final var cellSelectionItem = new CheckMenuItem("Enable cell selection");
        treeTable.getSelectionModel().cellSelectionEnabledProperty().bind(cellSelectionItem.selectedProperty());
        cellSelectionItem.setSelected(false);

        // ~

        final var menuButtonItem = new CheckMenuItem("Show menu button");
        treeTable.tableMenuButtonVisibleProperty().bind(menuButtonItem.selectedProperty());
        menuButtonItem.setSelected(true);

        final var propertiesMenu = new MenuButton("Properties");
        propertiesMenu.getItems().setAll(
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

        return propertiesMenu;
    }

    private static void addStyleClass(TreeTableColumn<?, ?> c, String styleClass, String... excludes) {
        Objects.requireNonNull(c);
        Objects.requireNonNull(styleClass);

        if (excludes != null && excludes.length > 0) {
            c.getStyleClass().removeAll(excludes);
        }
        c.getStyleClass().add(styleClass);
    }
}
