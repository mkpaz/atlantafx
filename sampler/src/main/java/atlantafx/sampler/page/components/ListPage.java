/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.sampler.fake.domain.Book;
import atlantafx.sampler.page.AbstractPage;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static atlantafx.base.theme.Styles.*;

public class ListPage extends AbstractPage {

    public static final String NAME = "ListView";

    @Override
    public String getName() { return NAME; }

    private VBox playground;
    private ComboBox<Example> exampleSelect;

    private final List<Book> dataList = generate(() -> Book.random(FAKER), 50);
    private final StringConverter<Book> bookStringConverter = new StringConverter<>() {

        @Override
        public String toString(Book book) {
            if (book == null) { return null; }
            return String.format("\"%s\" by %s", book.getTitle(), book.getAuthor());
        }

        @Override
        public Book fromString(String s) {
            if (s == null) { return null; }

            int sep = s.indexOf("\" by");
            String title = s.substring(1, sep);
            String author = s.substring(sep + "\" by".length());

            return dataList.stream()
                    .filter(b -> Objects.equals(b.getTitle(), title) && Objects.equals(b.getAuthor(), author))
                    .findFirst()
                    .orElse(null);
        }
    };

    public ListPage() {
        super();
        createView();
    }

    private void createView() {
        exampleSelect = exampleSelect();
        playground = playground(exampleSelect);
        userContent.getChildren().setAll(playground);
    }

    @Override
    protected void onRendered() {
        super.onRendered();
        exampleSelect.getSelectionModel().selectFirst();
    }

    private VBox playground(ComboBox<Example> exampleSelect) {
        var playground = new VBox(10);
        playground.setMinHeight(100);

        var borderedToggle = new ToggleSwitch("Bordered");
        borderedToggle.selectedProperty().addListener((obs, old, value) -> toggleListProperty(lv -> toggleStyleClass(lv, BORDERED)));

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener((obs, old, value) -> toggleListProperty(lv -> toggleStyleClass(lv, DENSE)));

        var stripedToggle = new ToggleSwitch("Striped");
        stripedToggle.selectedProperty().addListener((obs, old, value) -> toggleListProperty(lv -> toggleStyleClass(lv, STRIPED)));

        var disableToggle = new ToggleSwitch("Disable");
        disableToggle.selectedProperty().addListener((obs, old, val) -> findDisplayedList().ifPresent(lv -> {
            if (val != null) { lv.setDisable(val); }
        }));

        var controls = new HBox(20,
                                new Spacer(),
                                borderedToggle,
                                denseToggle,
                                stripedToggle,
                                disableToggle,
                                new Spacer()
        );

        playground.getChildren().setAll(
                new Label("Select an example:"),
                exampleSelect,
                new Spacer(Orientation.VERTICAL), // placeholder for ListView<?>
                controls
        );

        return playground;
    }

    private ComboBox<Example> exampleSelect() {
        var select = new ComboBox<Example>();

        select.setMaxWidth(Double.MAX_VALUE);
        select.getItems().setAll(Example.values());

        select.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val == null) { return; }
            if (playground.getChildren().size() != 4) {
                throw new RuntimeException("Unexpected container size.");
            }

            ListView<?> newList = createList(val);

            // copy existing style classes and properties to the new list
            findDisplayedList().ifPresent(lv -> {
                List<String> currentStyles = lv.getStyleClass();
                currentStyles.remove("list-view");
                newList.getStyleClass().addAll(currentStyles);

                newList.setDisable(lv.isDisable());
            });

            playground.getChildren().set(2, newList);
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

    private Optional<ListView<?>> findDisplayedList() {
        if (playground == null) { return Optional.empty(); }
        return playground.getChildren().stream()
                .filter(c -> c instanceof ListView<?>)
                .findFirst()
                .map(c -> (ListView<?>) c);
    }

    private void toggleListProperty(Consumer<ListView<?>> consumer) {
        findDisplayedList().ifPresent(lv -> {
            if (consumer != null) { consumer.accept(lv); }
        });
    }

    private ListView<?> createList(Example example) {
        switch (example) {
            case TEXT -> { return stringList(); }
            case EDITABLE -> { return editableList(); }
            case CHECK_BOX -> { return checkBoxList(); }
            case CHOICE_BOX -> { return choiceBoxList(); }
            case COMBO_BOX -> { return comboBoxList(); }
            case NESTED_CONTROLS -> { return nestedControlsList(); }
            default -> throw new IllegalArgumentException("Unexpected enum value: " + example);
        }
    }

    private ListView<String> stringList() {
        var lv = new ListView<String>();
        lv.getItems().setAll(dataList.stream().map(bookStringConverter::toString).collect(Collectors.toList()));
        return lv;
    }

    private ListView<String> editableList() {
        var lv = new ListView<String>();
        lv.setEditable(true);
        lv.setCellFactory(TextFieldListCell.forListView());
        lv.getItems().setAll(
                // small size to see the empty cells
                dataList.stream().limit(5).map(bookStringConverter::toString).collect(Collectors.toList())
        );
        return lv;
    }

    private ListView<Book> checkBoxList() {
        var lv = new ListView<Book>();
        lv.setCellFactory(CheckBoxListCell.forListView(Book::stateProperty, bookStringConverter));
        lv.getItems().setAll(dataList.stream().limit(10).collect(Collectors.toList()));
        return lv;
    }

    private ListView<Book> choiceBoxList() {
        var lv = new ListView<Book>();
        lv.setEditable(true);
        lv.setCellFactory(ChoiceBoxListCell.forListView(bookStringConverter, dataList.subList(0, 10).toArray(Book[]::new)));
        lv.getItems().setAll(dataList.stream().limit(10).collect(Collectors.toList()));
        return lv;
    }

    private ListView<Book> comboBoxList() {
        var lv = new ListView<Book>();
        lv.setEditable(true);
        lv.setCellFactory(ComboBoxListCell.forListView(bookStringConverter, dataList.subList(0, 10).toArray(Book[]::new)));
        lv.getItems().setAll(dataList.stream().limit(10).collect(Collectors.toList()));
        return lv;
    }

    private ListView<Book> nestedControlsList() {
        var lv = new ListView<Book>();
        lv.setCellFactory(book -> new NestedControlsListCell());
        lv.getItems().setAll(dataList.stream().limit(10).collect(Collectors.toList()));
        return lv;
    }

    ///////////////////////////////////////////////////////////////////////////

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

    private static class NestedControlsListCell extends ListCell<Book> {

        private final HBox root;
        private final Label titleLabel;
        private final Hyperlink authorLink;

        public NestedControlsListCell() {
            titleLabel = new Label();
            authorLink = new Hyperlink();

            var purchaseBtn = new Button("Purchase");
            purchaseBtn.getStyleClass().addAll(ACCENT);
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
