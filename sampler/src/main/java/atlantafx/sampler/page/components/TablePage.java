/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.CaptionMenuItem;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.fake.domain.Product;
import atlantafx.sampler.page.AbstractPage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIconTableCell;

import java.util.List;
import java.util.stream.IntStream;

import static atlantafx.base.theme.Styles.*;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.geometry.Orientation.HORIZONTAL;

public class TablePage extends AbstractPage {

    public static final String NAME = "TableView";

    @Override
    public String getName() { return NAME; }

    private TableView<Product> table;
    private final List<Product> dataList = IntStream.range(1, 51).boxed()
            .map(i -> Product.random(i, FAKER))
            .toList();

    public TablePage() {
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
        bordersToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(table, BORDERED));

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(table, DENSE));

        var stripesToggle = new ToggleSwitch("Striped");
        stripesToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(table, STRIPED));

        var disableToggle = new ToggleSwitch("Disable");
        disableToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) { table.setDisable(val); }
        });

        var maxValue = 100;
        var rowCountChoice = new ComboBox<>(observableArrayList(0, 5, 10, 25, maxValue));
        rowCountChoice.setValue(maxValue);

        var rowCountBox = new HBox(10, new Label("rows"), rowCountChoice);
        rowCountBox.setAlignment(Pos.CENTER_LEFT);

        var togglesBox = new HBox(20,
                bordersToggle,
                denseToggle,
                stripesToggle,
                disableToggle,
                new Spacer(HORIZONTAL),
                rowCountBox
        );
        togglesBox.setAlignment(Pos.CENTER_LEFT);

        // ~

        var filteredData = new FilteredList<>(observableArrayList(dataList));
        filteredData.predicateProperty().bind(Bindings.createObjectBinding(
                () -> product -> product.getId() <= rowCountChoice.getValue(),
                rowCountChoice.valueProperty()
        ));

        var sortedData = new SortedList<>(filteredData);

        table = table();
        table.setItems(sortedData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // ~

        var topBox = new HBox(
                new Label("Example:"),
                new Spacer(),
                settingsMenu(table)
        );
        topBox.setAlignment(Pos.CENTER_LEFT);

        var playground = new VBox(10);
        playground.setMinHeight(100);
        playground.getChildren().setAll(
                topBox,
                table,
                togglesBox
        );

        return playground;
    }

    @SuppressWarnings("unchecked")
    private TableView<Product> table() {
        var stateCol = new TableColumn<Product, Boolean>("Selected");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        stateCol.setCellFactory(CheckBoxTableCell.forTableColumn(stateCol));
        stateCol.setEditable(true);

        // an example of creating index column if data object
        // doesn't provide index property
        var indexCol = new TableColumn<Product, String>("Index");
        indexCol.setCellFactory(col -> {
            TableCell<Product, String> cell = new TableCell<>();
            StringBinding value = Bindings.when(cell.emptyProperty())
                    .then("")
                    .otherwise(cell.indexProperty().add(1).asString());
            cell.textProperty().bind(value);
            return cell;
        });

        var iconCol = new TableColumn<Product, Feather>("Logo");
        iconCol.setCellValueFactory(c -> new SimpleObjectProperty<>(randomIcon()));
        iconCol.setCellFactory(FontIconTableCell.forTableColumn());
        iconCol.setEditable(false);

        var brandCol = new TableColumn<Product, String>("Brand ✎");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        brandCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(
                generate(() -> FAKER.commerce().brand(), 10).toArray(String[]::new)
        ));
        brandCol.setEditable(true);

        var nameCol = new TableColumn<Product, String>("Name ✎");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(ComboBoxTableCell.forTableColumn(
                generate(() -> FAKER.commerce().productName(), 10).toArray(String[]::new)
        ));
        nameCol.setEditable(true);

        var priceCol = new TableColumn<Product, String>("Price ✎");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(TextFieldTableCell.forTableColumn());
        priceCol.setEditable(true);

        var stockCountCol = new TableColumn<Product, Integer>("Count");
        stockCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        stockCountCol.setEditable(false);

        var stockAvailCol = new TableColumn<Product, Double>("Available");
        stockAvailCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
        stockAvailCol.setCellFactory(ProgressBarTableCell.forTableColumn());
        stockAvailCol.setEditable(false);

        var stockCol = new TableColumn<Product, Double>("Stock");
        stockCol.getColumns().setAll(stockCountCol, stockAvailCol);

        var table = new TableView<Product>();
        table.getColumns().setAll(stateCol, indexCol, iconCol, brandCol, nameCol, priceCol, stockCol);

        return table;
    }

    private MenuButton settingsMenu(TableView<Product> table) {
        var resizePolicyCaption = new CaptionMenuItem("Resize Policy");
        var resizePolicyGroup = new ToggleGroup();
        resizePolicyGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof Callback policy) {
                //noinspection rawtypes,unchecked
                table.setColumnResizePolicy((Callback<TableView.ResizeFeatures, Boolean>) policy);
            }
        });

        var unconstrainedResizeItem = new RadioMenuItem("Unconstrained");
        unconstrainedResizeItem.setToggleGroup(resizePolicyGroup);
        unconstrainedResizeItem.setUserData(TableView.UNCONSTRAINED_RESIZE_POLICY);
        unconstrainedResizeItem.setSelected(true);

        var constrainedResizeItem = new RadioMenuItem("Constrained");
        constrainedResizeItem.setToggleGroup(resizePolicyGroup);
        constrainedResizeItem.setUserData(TableView.CONSTRAINED_RESIZE_POLICY);

        // ~

        var selectionModeCaption = new CaptionMenuItem("Selection Mode");
        var selectionModeGroup = new ToggleGroup();
        selectionModeGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof SelectionMode mode) {
                table.getSelectionModel().setSelectionMode(mode);
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

        var editCellsItem = new CheckMenuItem("Editable");
        table.editableProperty().bind(editCellsItem.selectedProperty());
        editCellsItem.setSelected(true);

        var cellSelectionItem = new CheckMenuItem("Enable cell selection");
        table.getSelectionModel().cellSelectionEnabledProperty().bind(cellSelectionItem.selectedProperty());
        cellSelectionItem.setSelected(false);

        var edge2edgeItem = new CheckMenuItem("Edge to edge");
        edge2edgeItem.selectedProperty().addListener((obs, old, val) -> {
            if (!val) {
                table.getStyleClass().remove(Tweaks.EDGE_TO_EDGE);
            } else {
                table.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
            }
        });

        var menuButtonItem = new CheckMenuItem("Show menu button");
        table.tableMenuButtonVisibleProperty().bind(menuButtonItem.selectedProperty());
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
                    editCellsItem,
                    cellSelectionItem,
                    edge2edgeItem,
                    menuButtonItem
            );
        }};
    }
}
