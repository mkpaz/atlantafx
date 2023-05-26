/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static javafx.collections.FXCollections.observableArrayList;

import atlantafx.base.controls.CaptionMenuItem;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.fake.domain.Product;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Pagination;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIconTableCell;

@SuppressWarnings("unchecked")
public final class TableViewPage extends OutlinePage {

    public static final String NAME = "TableView";

    @Override
    public String getName() {
        return NAME;
    }

    public record Book(String author,
                       String title,
                       String genre,
                       String publisher,
                       SimpleBooleanProperty status) {

        public static Book random() {
            return new Book(
                FAKER.book().author(),
                FAKER.book().title(),
                FAKER.book().genre(),
                FAKER.book().publisher(),
                new SimpleBooleanProperty()
            );
        }

        public static ObservableList<Book> random(int count) {
            return IntStream.range(0, count).mapToObj(i -> Book.random())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }
    }

    public record Flight(String arrival,
                         String city,
                         String aircraft,
                         String airline,
                         String flight) {

        public static Flight random() {
            return new Flight(
                FAKER.date().past(12, TimeUnit.HOURS, "HH:mm"),
                FAKER.country().capital(),
                FAKER.aviation().aircraft().toUpperCase(Locale.ROOT),
                FAKER.aviation().airline(),
                FAKER.aviation().flight()
            );
        }

        public static ObservableList<Flight> random(int count) {
            return IntStream.range(0, count).mapToObj(i -> Flight.random())
                .sorted(Comparator.comparing(Flight::arrival))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public TableViewPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]TableView[/i] control is designed to visualize an unlimited number of rows of data, \
            broken out into columns."""
        );
        addSection("Usage", usageExample());
        addSection("Row Style", rowStyleExample());
        addSection("Selection Color", selectionColorExample());
        addSection("Edge-to-Edge", edge2EdgeExample());
        addSection("Alignment", alignmentExample());
        addSection("Editable", editableExample());
        addSection("Column Grouping", columnGroupingExample());
        addSection("Pagination", paginationExample());
        addSection("Playground", playground());
    }

    public ExampleBox usageExample() {
        //snippet_1:start
        var col1 = new TableColumn<Flight, String>("Arrival");
        col1.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().arrival())
        );

        var col2 = new TableColumn<Flight, String>("City");
        col2.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().city())
        );

        var col3 = new TableColumn<Flight, String>("Airline");
        col3.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().airline())
        );

        var col4 = new TableColumn<Flight, String>("Flight");
        col4.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().flight())
        );

        var table = new TableView<>(Flight.random(5));
        table.getColumns().setAll(col1, col2, col3, col4);
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        table.getSelectionModel().selectFirst();
        //snippet_1:end

        table.setMaxWidth(Double.MAX_VALUE);
        table.setMinHeight(300);
        HBox.setHgrow(table, Priority.ALWAYS);

        var box = new HBox(table);
        var description = BBCodeParser.createFormattedText("""
            You can create a table view by instantiating the \
            [font=monospace]javafx.scene.control.TableView[/font] class."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox rowStyleExample() {
        //snippet_2:start
        var col1 = new TableColumn<Flight, String>("Arrival");
        col1.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().arrival())
        );

        var col2 = new TableColumn<Flight, String>("City");
        col2.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().city())
        );

        var col3 = new TableColumn<Flight, String>("Airline");
        col3.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().airline())
        );

        var col4 = new TableColumn<Flight, String>("Flight");
        col4.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().flight())
        );

        var table = new TableView<>(Flight.random(5));
        table.getColumns().setAll(col1, col2, col3, col4);
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        table.getSelectionModel().selectFirst();

        var borderToggle = new ToggleSwitch("Bordered");
        borderToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(table, Styles.BORDERED)
        );

        var stripeToggle = new ToggleSwitch("Striped");
        stripeToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(table, Styles.STRIPED)
        );

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(table, Styles.DENSE)
        );
        //snippet_2:end

        table.setMaxWidth(Double.MAX_VALUE);
        table.setMinHeight(300);
        HBox.setHgrow(table, Priority.ALWAYS);

        var togglesBox = new HBox(HGAP_20, borderToggle, stripeToggle, denseToggle);
        togglesBox.setAlignment(Pos.CENTER);

        var box = new VBox(VGAP_10, table, togglesBox);
        var description = BBCodeParser.createFormattedText("""
            The [i]TableView[/i] rows can be styled simply by adding CSS classes:
                        
            [ul]
            [li][code]Styles.BORDERED[/code] - adds borders between the columns.[/li]
            [li][code]Styles.STRIPED[/code] - adds zebra-striping.[/li]
            [li][code]Styles.DENSE[/code] - makes the table more compact by cutting cell padding.[/li][/ul]"""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox selectionColorExample() {
        var style = """
            -color-cell-bg-selected: -color-accent-emphasis;
            -color-cell-fg-selected: -color-fg-emphasis;
            -color-cell-bg-selected-focused: -color-accent-emphasis;
            -color-cell-fg-selected-focused: -color-fg-emphasis;""";

        //snippet_3:start
        var col1 = new TableColumn<Flight, String>("Arrival");
        col1.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().arrival())
        );

        var col2 = new TableColumn<Flight, String>("City");
        col2.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().city())
        );

        var col3 = new TableColumn<Flight, String>("Airline");
        col3.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().airline())
        );

        var col4 = new TableColumn<Flight, String>("Flight");
        col4.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().flight())
        );

        var table = new TableView<>(Flight.random(5));
        table.getColumns().setAll(col1, col2, col3, col4);
        // -color-cell-bg-selected: -color-accent-emphasis;
        // -color-cell-fg-selected: -color-fg-emphasis;
        // -color-cell-bg-selected-focused: -color-accent-emphasis;
        // -color-cell-fg-selected-focused: -color-fg-emphasis;
        table.setStyle(style);
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        table.getSelectionModel().selectFirst();
        //snippet_3:end

        table.setMaxWidth(Double.MAX_VALUE);
        table.setMinHeight(250);
        HBox.setHgrow(table, Priority.ALWAYS);

        var box = new HBox(table);
        var description = BBCodeParser.createFormattedText("""
            Cell selection color (and more) can be changed via looked-up color variables. \
            You can find all supported color variables in the docs."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox edge2EdgeExample() {
        //snippet_5:start
        var col1 = new TableColumn<Flight, String>("Arrival");
        col1.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().arrival())
        );

        var col2 = new TableColumn<Flight, String>("City");
        col2.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().city())
        );

        var col3 = new TableColumn<Flight, String>("Airline");
        col3.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().airline())
        );

        var col4 = new TableColumn<Flight, String>("Flight");
        col4.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().flight())
        );

        var table = new TableView<>(Flight.random(5));
        table.getColumns().setAll(col1, col2, col3, col4);
        table.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        table.getSelectionModel().selectFirst();
        //snippet_5:end

        table.setMaxWidth(Double.MAX_VALUE);
        table.setMinHeight(250);
        HBox.setHgrow(table, Priority.ALWAYS);

        var box = new HBox(table);
        box.setStyle("""
            -fx-border-color: -color-accent-emphasis;
            -fx-border-width: 2px;"""
        );
        var description = BBCodeParser.createFormattedText("""
            Use [code]Tweaks.EDGE_TO_EDGE[/code] style class to remove the [i]TableView[/i] outer borders. \
            This is useful if you want to place the table into external container that already has its \
            own borders."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 5), description);
    }

    private ExampleBox alignmentExample() {
        //snippet_6:start
        var col1 = new TableColumn<Flight, String>("Arrival");
        col1.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col1.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().arrival())
        );

        var col2 = new TableColumn<Flight, String>("City");
        col2.getStyleClass().add(Tweaks.ALIGN_RIGHT);
        col2.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().city())
        );

        var col3 = new TableColumn<Flight, String>("Airline");
        col3.getStyleClass().add(Tweaks.ALIGN_RIGHT);
        col3.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().airline())
        );

        var col4 = new TableColumn<Flight, String>("Flight");
        col4.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col4.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().flight())
        );

        var table = new TableView<>(Flight.random(5));
        table.getColumns().setAll(col1, col2, col3, col4);
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        table.getSelectionModel().selectFirst();
        //snippet_6:end

        table.setMaxWidth(Double.MAX_VALUE);
        table.setMinHeight(300);
        HBox.setHgrow(table, Priority.ALWAYS);

        var box = new HBox(table);
        var description = BBCodeParser.createFormattedText("""
            Column content can be aligned by adding one of the following style class modifiers:
                            
            [ul]
            [li][code]Tweaks.ALIGN_LEFT[/code] (default)[/li]
            [li][code]Tweaks.ALIGN_CENTER[/code][/li]
            [li][code]Tweaks.ALIGN_RIGHT[/code][/li][/ul]"""
        );

        return new ExampleBox(box, new Snippet(getClass(), 6), description);
    }

    private ExampleBox editableExample() {
        //snippet_7:start
        var selectAll = new CheckBox();

        var col0 = new TableColumn<Book, Boolean>();
        col0.setGraphic(selectAll);
        col0.setSortable(false);
        col0.setCellValueFactory(c -> c.getValue().status());
        col0.setCellFactory(CheckBoxTableCell.forTableColumn(col0));
        col0.setEditable(true);

        var col1 = new TableColumn<Book, String>("Author");
        col1.setCellFactory(TextFieldTableCell.forTableColumn());
        col1.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().author())
        );

        var col2 = new TableColumn<Book, String>("Title");
        col2.setCellFactory(TextFieldTableCell.forTableColumn());
        col2.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().title())
        );

        var col3 = new TableColumn<Book, String>("Genre");
        col3.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().genre())
        );
        col3.setCellFactory(ChoiceBoxTableCell.forTableColumn(
            generate(() -> FAKER.book().genre(), 10).toArray(String[]::new)
        ));

        var col4 = new TableColumn<Book, String>("Publisher");
        col4.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().publisher())
        );
        col4.setCellFactory(ComboBoxTableCell.forTableColumn(
            generate(() -> FAKER.book().publisher(), 10).toArray(String[]::new)
        ));

        var table = new TableView<>(Book.random(5));
        table.getColumns().setAll(col0, col1, col2, col3, col4);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectFirst();
        selectAll.setOnAction(evt -> {
            table.getItems().forEach(
                item -> item.status().set(selectAll.isSelected())
            );
            evt.consume();
        });
        //snippet_7:end

        table.setMaxWidth(Double.MAX_VALUE);
        table.setMinHeight(300);
        table.setEditable(true);
        HBox.setHgrow(table, Priority.ALWAYS);

        var box = new HBox(table);
        var description = BBCodeParser.createFormattedText(
            "the [i]TableView[/i] cells can be made editable by setting an appropriate cell factory."
        );

        return new ExampleBox(box, new Snippet(getClass(), 7), description);
    }

    private ExampleBox columnGroupingExample() {
        //snippet_8:start
        var col11 = new TableColumn<Flight, String>("Time");
        col11.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().arrival())
        );

        var col12 = new TableColumn<Flight, String>("City");
        col12.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().city())
        );

        var col1 = new TableColumn<Flight, String>("Arrival");
        col1.getColumns().setAll(col11, col12);

        var col21 = new TableColumn<Flight, String>("Airline");
        col21.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().airline())
        );

        var col22 = new TableColumn<Flight, String>("Aircraft");
        col22.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().aircraft())
        );

        var col2 = new TableColumn<Flight, String>("Jet");
        col2.getColumns().setAll(col21, col22);

        var col3 = new TableColumn<Flight, String>("Flight");
        col3.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().flight())
        );

        var table = new TableView<>(Flight.random(5));
        table.getColumns().setAll(col1, col2, col3);
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        table.getSelectionModel().selectFirst();
        //snippet_8:end

        table.setMaxWidth(Double.MAX_VALUE);
        table.setMinHeight(300);
        HBox.setHgrow(table, Priority.ALWAYS);

        var box = new HBox(table);
        var description = BBCodeParser.createFormattedText(
            "Any table column can contain nested columns which allows to group common data properties."
        );

        return new ExampleBox(box, new Snippet(getClass(), 8), description);
    }

    private ExampleBox paginationExample() {
        //snippet_9:start
        var col1 = new TableColumn<Flight, String>("Arrival");
        col1.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().arrival())
        );

        var col2 = new TableColumn<Flight, String>("City");
        col2.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().city())
        );

        var col3 = new TableColumn<Flight, String>("Airline");
        col3.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().airline())
        );

        var col4 = new TableColumn<Flight, String>("Flight");
        col4.setCellValueFactory(
            c -> new SimpleStringProperty(c.getValue().flight())
        );

        var table = new TableView<>(Flight.random(50));
        table.getColumns().setAll(col1, col2, col3, col4);
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        table.getSelectionModel().selectFirst();

        var pg = new Pagination(25, 0);
        pg.setMaxPageIndicatorCount(5);
        pg.setPageFactory(pageNum -> {
            table.getItems().setAll(Flight.random(50));
            return new StackPane(); // null isn't allowed
        });
        //snippet_9:end

        table.setMaxWidth(Double.MAX_VALUE);
        table.setMinHeight(300);
        HBox.setHgrow(table, Priority.ALWAYS);

        var box = new VBox(table, pg);
        var description = BBCodeParser.createFormattedText(
            "Pagination can be used to split up large amounts of data into smaller chunks."
        );

        return new ExampleBox(box, new Snippet(getClass(), 9), description);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playground                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private TableView<Product> table;
    private final List<Product> dataList = IntStream.range(1, 51).boxed()
        .map(i -> atlantafx.sampler.fake.domain.Product.random(i, FAKER))
        .toList();

    private VBox playground() {
        var maxRowCount = 200;
        var rowCountChoice = new ComboBox<>(
            observableArrayList(0, 1, 5, 10, 25, maxRowCount)
        );
        rowCountChoice.setValue(5);

        var rowCountBox = new HBox(HGAP_20, new Label("rows"), rowCountChoice);
        rowCountBox.setAlignment(Pos.CENTER_LEFT);

        // == FOOTER ==

        var bordersToggle = new ToggleSwitch("Bordered");
        bordersToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(table, Styles.BORDERED)
        );

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(table, Styles.DENSE)
        );

        var stripesToggle = new ToggleSwitch("Striped");
        stripesToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(table, Styles.STRIPED)
        );

        var edge2edgeToggle = new ToggleSwitch("Edge to edge");
        edge2edgeToggle.selectedProperty().addListener(
            (obs, old, value) -> Styles.toggleStyleClass(table, Tweaks.EDGE_TO_EDGE)
        );

        var footer = new HBox(HGAP_20, bordersToggle, denseToggle, stripesToggle, edge2edgeToggle);
        footer.setAlignment(Pos.CENTER);

        // == TABLE ==

        var filteredData = new FilteredList<>(observableArrayList(dataList));
        filteredData.predicateProperty().bind(Bindings.createObjectBinding(
            () -> product -> product.getId() <= rowCountChoice.getValue(),
            rowCountChoice.valueProperty()
        ));

        var sortedData = new SortedList<>(filteredData);

        table = createTable();
        table.setItems(sortedData);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        VBox.setVgrow(table, Priority.ALWAYS);

        // == HEADER ==

        var header = new HBox(createTablePropertiesMenu(table), new Spacer(), rowCountBox);
        header.setAlignment(Pos.CENTER_LEFT);

        // ~

        var description = BBCodeParser.createFormattedText("""
            The playground demonstrates the most important [i]TableView[/i] features \
            and also serves as an object for monkey testing."""
        );

        var playground = new VBox(VGAP_10, description, header, table, footer);
        playground.setMinHeight(500);

        return playground;
    }

    @SuppressWarnings("unchecked")
    private TableView<Product> createTable() {
        // an example of creating index column if data object
        // doesn't provide index property
        var indexCol = new TableColumn<Product, String>("Index");
        indexCol.setCellFactory(col -> {
            var cell = new TableCell<Product, String>();
            StringBinding value = Bindings.when(cell.emptyProperty())
                .then("")
                .otherwise(cell.indexProperty().add(1).asString());
            cell.textProperty().bind(value);
            return cell;
        });

        var iconCol = new TableColumn<Product, Feather>("Logo");
        iconCol.setCellValueFactory(c -> new SimpleObjectProperty<>(randomIcon()));
        iconCol.setCellFactory(FontIconTableCell.forTableColumn());

        var brandCol = new TableColumn<Product, String>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));

        var nameCol = new TableColumn<Product, String>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        var priceCol = new TableColumn<Product, String>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        var stockCountCol = new TableColumn<Product, Integer>("Count");
        stockCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        var stockAvailCol = new TableColumn<Product, Double>("Available");
        stockAvailCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
        stockAvailCol.setCellFactory(ProgressBarTableCell.forTableColumn());

        var stockCol = new TableColumn<Product, Double>("Stock");
        stockCol.getColumns().setAll(stockCountCol, stockAvailCol);

        var tableView = new TableView<Product>();
        tableView.getColumns().setAll(indexCol, iconCol, brandCol, nameCol, priceCol, stockCol);

        return tableView;
    }

    private MenuButton createTablePropertiesMenu(TableView<Product> table) {
        final var resizePolCaption = new CaptionMenuItem("Resize Policy");
        final var resizePolGroup = new ToggleGroup();
        resizePolGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof Callback<?, ?> policy) {
                //noinspection rawtypes,unchecked
                table.setColumnResizePolicy((Callback<TableView.ResizeFeatures, Boolean>) policy);
            }
        });

        final var sizeUnconstrainedItem = new RadioMenuItem("Unconstrained");
        sizeUnconstrainedItem.setToggleGroup(resizePolGroup);
        sizeUnconstrainedItem.setUserData(TableView.UNCONSTRAINED_RESIZE_POLICY);
        sizeUnconstrainedItem.setSelected(true);

        final var sizeAllColumnsItem = new RadioMenuItem("Resize All Columns");
        sizeAllColumnsItem.setToggleGroup(resizePolGroup);
        sizeAllColumnsItem.setUserData(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        final var sizeLastColumnItem = new RadioMenuItem("Resize Last Column");
        sizeLastColumnItem.setToggleGroup(resizePolGroup);
        sizeLastColumnItem.setUserData(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);

        final var sizeNextColumnItem = new RadioMenuItem("Resize Next Column");
        sizeNextColumnItem.setToggleGroup(resizePolGroup);
        sizeNextColumnItem.setUserData(TableView.CONSTRAINED_RESIZE_POLICY_NEXT_COLUMN);

        final var sizeSubsequentItem = new RadioMenuItem("Resize Subsequent Column");
        sizeSubsequentItem.setToggleGroup(resizePolGroup);
        sizeSubsequentItem.setUserData(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        final var sizeFlexHeadItem = new RadioMenuItem("Flex Head");
        sizeFlexHeadItem.setToggleGroup(resizePolGroup);
        sizeFlexHeadItem.setUserData(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);

        final var sizeFlexTailItem = new RadioMenuItem("Flex Tail");
        sizeFlexTailItem.setToggleGroup(resizePolGroup);
        sizeFlexTailItem.setUserData(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // ~

        final var selModeCaption = new CaptionMenuItem("Selection Mode");
        final var selModeGroup = new ToggleGroup();
        selModeGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof SelectionMode mode) {
                table.getSelectionModel().setSelectionMode(mode);
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

        final var cellSelItem = new CheckMenuItem("Enable cell selection");
        table.getSelectionModel().cellSelectionEnabledProperty().bind(cellSelItem.selectedProperty());
        cellSelItem.setSelected(false);

        // ~

        final var menuBtnItem = new CheckMenuItem("Show menu button");
        table.tableMenuButtonVisibleProperty().bind(menuBtnItem.selectedProperty());
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
            cellSelItem,
            menuBtnItem
        );

        return propsMenu;
    }
}
