/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.fake.domain.Book;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class ListViewPage extends OutlinePage {

    public static final String NAME = "ListView";

    @Override
    public String getName() {
        return NAME;
    }

    public ListViewPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A [i]ListView[/i] displays a horizontal or vertical list of items from which the \
            user may select, or with which the user may interact. A ListView is able to \
            have its generic type set to represent the type of data in the backing model."""
        );
        addSection("Usage", usageExample());
        addSection("Row Style", rowStyleExample());
        addSection("Selection Color", selectionColorExample());
        addSection("Edge-to-Edge", edge2EdgeExample());
        addSection("Playground", playground());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var names = FXCollections.observableArrayList("Julia", "Ian", "Sue");
        var lv = new ListView<>(names);
        lv.setMinHeight(200);
        //snippet_1:end

        var box = new VBox(20, lv);
        var description = BBCodeParser.createFormattedText("""
            You can create a list view by instantiating the \
            [font=monospace]javafx.scene.control.ListView[/font] class."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox rowStyleExample() {
        //snippet_2:start
        var names = FXCollections.observableArrayList("Julia", "Ian", "Sue");
        var lv = new ListView<>(names);
        lv.setMinHeight(200);

        var borderToggle = new ToggleSwitch("Bordered");
        borderToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(lv, Styles.BORDERED)
        );

        var stripeToggle = new ToggleSwitch("Striped");
        stripeToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(lv, Styles.STRIPED)
        );

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(lv, Styles.DENSE)
        );
        //snippet_2:end

        var togglesBox = new HBox(HGAP_20, borderToggle, stripeToggle, denseToggle);
        togglesBox.setAlignment(Pos.CENTER);

        var box = new VBox(VGAP_10, lv, togglesBox);

        var description = BBCodeParser.createFormattedText("""
            The [i]ListView[/i] rows can be styled simply by adding CSS classes:
                        
            [ul]
            [li][code]Styles.BORDERED[/code] - adds borders between the rows.[/li]
            [li][code]Styles.STRIPED[/code] - adds zebra-striping.[/li]
            [li][code]Styles.DENSE[/code] - makes the [i]ListView[/i] more compact by cutting \
            cell padding.[/li][/ul]"""
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
        var names = FXCollections.observableArrayList("Julia", "Ian", "Sue");
        var lv = new ListView<>(names);
        lv.setMinHeight(200);
        // -color-cell-bg-selected: -color-accent-emphasis;
        // -color-cell-fg-selected: -color-fg-emphasis;
        // -color-cell-bg-selected-focused: -color-accent-emphasis;
        // -color-cell-fg-selected-focused: -color-fg-emphasis;
        lv.setStyle(style);
        lv.getSelectionModel().selectFirst();
        //snippet_3:end

        var box = new HBox(lv);
        var description = BBCodeParser.createFormattedText("""
            Cell selection color (and more) can be changed via looked-up color variables. \
            You can find all supported color variables in the docs."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox edge2EdgeExample() {
        //snippet_5:start
        var names = FXCollections.observableArrayList("Julia", "Ian", "Sue");
        var lv = new ListView<>(names);
        lv.setMinHeight(150);
        lv.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
        //snippet_5:end

        var box = new VBox(lv);
        box.setStyle("""
            -fx-border-color: -color-accent-emphasis;
            -fx-border-width: 2px;"""
        );

        var description = BBCodeParser.createFormattedText("""
            Use [code]Tweaks.EDGE_TO_EDGE[/code] style class to remove the [i]ListView[/i] \
            outer borders. This is useful if you want to place the table into external \
            container that already has its own borders."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 5), description);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playground                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private final List<Book> dataList = generate(() -> Book.random(FAKER), 50);
    private final StringConverter<Book> bookStringConverter = new BookStringConverter(dataList);
    private final StackPane listWrapper = new StackPane();
    private final ComboBox<Example> exampleSelect = createExampleSelect();

    private VBox playground() {
        var borderToggle = new ToggleSwitch("Bordered");
        borderToggle.selectedProperty().addListener(
            (obs, old, value) -> toggleListProperty(lv ->
                Styles.toggleStyleClass(lv, Styles.BORDERED)
            ));

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener(
            (obs, old, value) -> toggleListProperty(lv ->
                Styles.toggleStyleClass(lv, Styles.DENSE)
            ));

        var stripeToggle = new ToggleSwitch("Striped");
        stripeToggle.selectedProperty().addListener(
            (obs, old, value) -> toggleListProperty(lv ->
                Styles.toggleStyleClass(lv, Styles.STRIPED)
            ));

        var edge2edgeToggle = new ToggleSwitch("Edge to edge");
        edge2edgeToggle.selectedProperty().addListener(
            (obs, old, value) -> toggleListProperty(lv ->
                Styles.toggleStyleClass(lv, Tweaks.EDGE_TO_EDGE)
            ));

        var controls = new HBox(
            HGAP_20,
            borderToggle, denseToggle, stripeToggle, edge2edgeToggle
        );
        controls.setAlignment(Pos.CENTER);

        listWrapper.setMinHeight(400);
        VBox.setVgrow(listWrapper, Priority.ALWAYS);

        var description = BBCodeParser.createFormattedText("""
            The playground demonstrates the most important [i]ListView[/i] features \
            and also serves as an object for monkey testing."""
        );

        return new VBox(
            VGAP_10,
            description,
            new HBox(new Label("Select an example:")),
            exampleSelect,
            listWrapper,
            controls
        );
    }

    private ComboBox<Example> createExampleSelect() {
        var select = new ComboBox<Example>();
        select.setMaxWidth(Double.MAX_VALUE);
        select.getItems().setAll(Example.values());

        select.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }

            ListView<?> newList = createList(val);

            // copy existing style classes and properties to the new list
            findDisplayedList().ifPresent(lv -> {
                List<String> currentStyles = lv.getStyleClass();
                currentStyles.remove("list-view");
                newList.getStyleClass().addAll(currentStyles);
                newList.setDisable(lv.isDisable());
            });

            listWrapper.getChildren().setAll(newList);
        });

        select.setConverter(new StringConverter<>() {
            @Override
            public String toString(Example example) {
                return example == null ? "" : example.getName();
            }

            @Override
            public Example fromString(String s) {
                return Example.find(s);
            }
        });

        return select;
    }

    @Override
    protected void onRendered() {
        super.onRendered();
        exampleSelect.getSelectionModel().selectFirst();
    }

    private Optional<ListView<?>> findDisplayedList() {
        return listWrapper.getChildren().size() > 0
            ? Optional.of((ListView<?>) listWrapper.getChildren().get(0))
            : Optional.empty();
    }

    private void toggleListProperty(Consumer<ListView<?>> consumer) {
        findDisplayedList().ifPresent(lv -> {
            if (consumer != null) {
                consumer.accept(lv);
            }
        });
    }

    private ListView<?> createList(Example example) {
        switch (example) {
            case TEXT -> {
                return stringList();
            }
            case EDITABLE -> {
                return editableList();
            }
            case CHECK_BOX -> {
                return checkBoxList();
            }
            case CHOICE_BOX -> {
                return choiceBoxList();
            }
            case COMBO_BOX -> {
                return comboBoxList();
            }
            case NESTED_CONTROLS -> {
                return nestedControlsList();
            }
            default -> throw new IllegalArgumentException("Unexpected enum value: " + example);
        }
    }

    private ListView<String> stringList() {
        var lv = new ListView<String>();
        lv.getItems().setAll(dataList.stream()
            .map(bookStringConverter::toString).collect(Collectors.toList())
        );
        return lv;
    }

    private ListView<String> editableList() {
        var lv = new ListView<String>();
        lv.setEditable(true);
        lv.setCellFactory(TextFieldListCell.forListView());
        lv.getItems().setAll(
            // small size to see the empty cells
            dataList.stream().limit(5).map(
                bookStringConverter::toString).collect(Collectors.toList())
        );
        return lv;
    }

    private ListView<Book> checkBoxList() {
        var lv = new ListView<Book>();
        lv.setCellFactory(
            CheckBoxListCell.forListView(Book::stateProperty, bookStringConverter)
        );
        lv.getItems().setAll(
            dataList.stream().limit(10).collect(Collectors.toList())
        );
        return lv;
    }

    private ListView<Book> choiceBoxList() {
        var lv = new ListView<Book>();
        lv.setEditable(true);
        lv.setCellFactory(
            ChoiceBoxListCell.forListView(
                bookStringConverter,
                dataList.subList(0, 10).toArray(Book[]::new))
        );
        lv.getItems().setAll(
            dataList.stream().limit(10).collect(Collectors.toList())
        );
        return lv;
    }

    private ListView<Book> comboBoxList() {
        var lv = new ListView<Book>();
        lv.setEditable(true);
        lv.setCellFactory(
            ComboBoxListCell.forListView(
                bookStringConverter,
                dataList.subList(0, 10).toArray(Book[]::new))
        );
        lv.getItems().setAll(
            dataList.stream().limit(10).collect(Collectors.toList())
        );
        return lv;
    }

    private ListView<Book> nestedControlsList() {
        var lv = new ListView<Book>();
        lv.setCellFactory(book -> new NestedControlsListCell());
        lv.getItems().setAll(
            dataList.stream().limit(10).collect(Collectors.toList())
        );
        return lv;
    }

    private enum Example {
        TEXT("Text"),
        EDITABLE("TextFieldListCell"),
        CHECK_BOX("CheckBoxListCell"),
        CHOICE_BOX("ChoiceBoxListCell"),
        COMBO_BOX("ComboBoxListCell"),
        NESTED_CONTROLS("Nested controls");

        private final String name;

        Example(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Example find(String name) {
            return Arrays.stream(Example.values())
                .filter(example -> Objects.equals(example.getName(), name))
                .findFirst()
                .orElse(null);
        }
    }

    private static class BookStringConverter extends StringConverter<Book> {

        private final List<Book> dataList;

        public BookStringConverter(List<Book> dataList) {
            this.dataList = dataList;
        }

        @Override
        public String toString(Book book) {
            if (book == null) {
                return null;
            }
            return String.format("\"%s\" by %s", book.getTitle(), book.getAuthor());
        }

        @Override
        public Book fromString(String s) {
            if (s == null) {
                return null;
            }

            int sep = s.indexOf("\" by");
            String title = s.substring(1, sep);
            String author = s.substring(sep + "\" by".length());

            return dataList.stream()
                .filter(b -> Objects.equals(b.getTitle(), title)
                    && Objects.equals(b.getAuthor(), author)
                )
                .findFirst()
                .orElse(null);
        }
    }

    private static class NestedControlsListCell extends ListCell<Book> {

        private final HBox root;
        private final Label titleLabel;
        private final Hyperlink authorLink;

        public NestedControlsListCell() {
            titleLabel = new Label();
            authorLink = new Hyperlink();

            var purchaseBtn = new Button("Purchase");
            purchaseBtn.getStyleClass().addAll(Styles.ACCENT);
            purchaseBtn.setGraphic(new FontIcon(Feather.SHOPPING_CART));

            root = new HBox(5,
                titleLabel,
                new Label(" by"),
                authorLink,
                new Spacer(),
                purchaseBtn
            );
            root.setAlignment(Pos.CENTER_LEFT);
        }

        @Override
        public void updateItem(Book book, boolean empty) {
            super.updateItem(book, empty);

            if (empty) {
                setGraphic(null);
                return;
            }

            titleLabel.setText("\"" + book.getTitle() + "\"");
            authorLink.setText(book.getAuthor());
            setGraphic(root);
        }
    }
}
