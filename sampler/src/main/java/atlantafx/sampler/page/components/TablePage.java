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
import static javafx.collections.FXCollections.observableArrayList;

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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.FontIconTableCell;

public class TablePage extends AbstractPage {

    public static final String NAME = "TableView";

    @Override
    public String getName() {
        return NAME;
    }

    private TableView<Product> table;
    private final List<Product> dataList = IntStream.range(1, 51).boxed()
        .map(i -> Product.random(i, FAKER))
        .toList();

    public TablePage() {
        super();

        var sample = new SampleBlock("Playground", createPlayground());
        sample.setFillHeight(true);
        setUserContent(sample);
    }

    private VBox createPlayground() {
        // == FOOTER ==

        var bordersToggle = new ToggleSwitch("Bordered");
        bordersToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(table, BORDERED));

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(table, DENSE));

        var stripesToggle = new ToggleSwitch("Striped");
        stripesToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(table, STRIPED));

        var edge2edgeToggle = new ToggleSwitch("Edge to edge");
        edge2edgeToggle.selectedProperty().addListener(
            (obs, old, value) -> toggleStyleClass(table, Tweaks.EDGE_TO_EDGE)
        );

        var maxRowCount = 100;
        var rowCountChoice = new ComboBox<>(observableArrayList(0, 5, 10, 25, maxRowCount));
        rowCountChoice.setValue(maxRowCount);

        var rowCountBox = new HBox(BLOCK_HGAP, new Label("rows"), rowCountChoice);
        rowCountBox.setAlignment(Pos.CENTER_LEFT);

        var footer = new HBox(
            BLOCK_HGAP,
            new Spacer(),
            bordersToggle,
            denseToggle,
            stripesToggle,
            edge2edgeToggle,
            new Spacer(),
            rowCountBox
        );
        footer.setAlignment(Pos.CENTER_LEFT);

        // == TABLE ==

        var filteredData = new FilteredList<>(observableArrayList(dataList));
        filteredData.predicateProperty().bind(Bindings.createObjectBinding(
            () -> product -> product.getId() <= rowCountChoice.getValue(),
            rowCountChoice.valueProperty()
        ));

        var sortedData = new SortedList<>(filteredData);

        table = createTable();
        table.setItems(sortedData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        VBox.setVgrow(table, Priority.ALWAYS);

        // == HEADER ==

        var alignGroup = new ToggleGroup();

        var alignLeftBtn = new ToggleButton("", new FontIcon(Feather.ALIGN_LEFT));
        alignLeftBtn.getStyleClass().add(Styles.LEFT_PILL);
        alignLeftBtn.setToggleGroup(alignGroup);
        alignLeftBtn.setSelected(true);
        alignLeftBtn.setOnAction(e -> {
            for (TableColumn<?, ?> c : table.getColumns()) {
                c.getStyleClass().removeAll(ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT);
            }
        });

        var alignCenterBtn = new ToggleButton("", new FontIcon(Feather.ALIGN_CENTER));
        alignCenterBtn.getStyleClass().add(Styles.CENTER_PILL);
        alignCenterBtn.setToggleGroup(alignGroup);
        alignCenterBtn.selectedProperty().addListener((obs, old, val) -> {
            for (TableColumn<?, ?> c : table.getColumns()) {
                addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
            }
        });

        var alignRightBtn = new ToggleButton("", new FontIcon(Feather.ALIGN_RIGHT));
        alignRightBtn.getStyleClass().add(Styles.RIGHT_PILL);
        alignRightBtn.setToggleGroup(alignGroup);
        alignRightBtn.selectedProperty().addListener((obs, old, val) -> {
            for (TableColumn<?, ?> c : table.getColumns()) {
                addStyleClass(c, ALIGN_RIGHT, ALIGN_LEFT, ALIGN_CENTER);
            }
        });

        var alignBox = new HBox(alignLeftBtn, alignCenterBtn, alignRightBtn);

        var disableToggle = new ToggleSwitch("Disable");
        disableToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) {
                table.setDisable(val);
            }
        });

        var header = new HBox(
            createTablePropertiesMenu(table),
            new Spacer(),
            alignBox,
            new Spacer(),
            disableToggle
        );
        header.setAlignment(Pos.CENTER_LEFT);

        // ~

        var playground = new VBox(BLOCK_VGAP, header, table, footer);
        playground.setMinHeight(100);

        return playground;
    }

    @SuppressWarnings("unchecked")
    private TableView<Product> createTable() {
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

        var brandCol = new TableColumn<Product, String>("Brand  🖉");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        brandCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(
            generate(() -> FAKER.commerce().brand(), 10).toArray(String[]::new)
        ));
        brandCol.setEditable(true);

        var nameCol = new TableColumn<Product, String>("Name  🖉");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(ComboBoxTableCell.forTableColumn(
            generate(() -> FAKER.commerce().productName(), 10).toArray(String[]::new)
        ));
        nameCol.setEditable(true);

        var priceCol = new TableColumn<Product, String>("Price  🖉");
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

        var tableView = new TableView<Product>();
        tableView.getColumns().setAll(stateCol, indexCol, iconCol, brandCol, nameCol, priceCol, stockCol);

        return tableView;
    }

    private MenuButton createTablePropertiesMenu(TableView<Product> table) {
        final var resizePolicyCaption = new CaptionMenuItem("Resize Policy");
        final var resizePolicyGroup = new ToggleGroup();
        resizePolicyGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof Callback<?, ?> policy) {
                //noinspection rawtypes,unchecked
                table.setColumnResizePolicy((Callback<TableView.ResizeFeatures, Boolean>) policy);
            }
        });

        final var unconstrainedResizeItem = new RadioMenuItem("Unconstrained");
        unconstrainedResizeItem.setToggleGroup(resizePolicyGroup);
        unconstrainedResizeItem.setUserData(TableView.UNCONSTRAINED_RESIZE_POLICY);
        unconstrainedResizeItem.setSelected(true);

        final var constrainedResizeItem = new RadioMenuItem("Constrained");
        constrainedResizeItem.setToggleGroup(resizePolicyGroup);
        constrainedResizeItem.setUserData(TableView.CONSTRAINED_RESIZE_POLICY);

        // ~

        final var selectionModeCaption = new CaptionMenuItem("Selection Mode");
        final var selectionModeGroup = new ToggleGroup();
        selectionModeGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof SelectionMode mode) {
                table.getSelectionModel().setSelectionMode(mode);
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

        final var editCellsItem = new CheckMenuItem("Editable");
        table.editableProperty().bind(editCellsItem.selectedProperty());
        editCellsItem.setSelected(true);

        final var cellSelectionItem = new CheckMenuItem("Enable cell selection");
        table.getSelectionModel().cellSelectionEnabledProperty().bind(cellSelectionItem.selectedProperty());
        cellSelectionItem.setSelected(false);

        // ~

        final var menuButtonItem = new CheckMenuItem("Show menu button");
        table.tableMenuButtonVisibleProperty().bind(menuButtonItem.selectedProperty());
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
            editCellsItem,
            cellSelectionItem,
            menuButtonItem
        );

        return propertiesMenu;
    }

    private static void addStyleClass(TableColumn<?, ?> c, String styleClass, String... excludes) {
        Objects.requireNonNull(c);
        Objects.requireNonNull(styleClass);

        if (excludes != null && excludes.length > 0) {
            c.getStyleClass().removeAll(excludes);
        }
        c.getStyleClass().add(styleClass);
    }
}
