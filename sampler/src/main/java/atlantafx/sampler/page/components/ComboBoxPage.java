/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class ComboBoxPage extends OutlinePage {

    public static final String NAME = "ComboBox";
    private static final int PREF_WIDTH = 200;

    @Override
    public String getName() {
        return NAME;
    }

    public ComboBoxPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A user interface component which shows a list of items out of which \
            user can select at most one item. JavaFX provides two pretty similar \
            controls for that purpose, namely the [i]ComboBox[/i] and [i]ChoiceBox[/i]."""
        );
        addSection("Usage", usageExample());
        addSection("Editable", editableExample());
        addSection("Placeholder", placeholderExample());
        addSection("Custom Items", customItemsExample());
        addSection("Color", colorExample());
        addSection("Overflow", overflowExample());
        addSection("Alternative Icon", altIconExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var cmb1 = new ComboBox<String>();
        cmb1.setPrefWidth(PREF_WIDTH);
        cmb1.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 5)
        ));
        cmb1.getSelectionModel().selectFirst();

        var cmb2 = new ComboBox<String>();
        cmb2.setPrefWidth(PREF_WIDTH);

        var chb1 = new ChoiceBox<String>();
        chb1.setPrefWidth(PREF_WIDTH);
        chb1.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 5)
        ));
        chb1.getSelectionModel().selectFirst();

        var chb2 = new ChoiceBox<String>();
        chb2.setPrefWidth(PREF_WIDTH);
        //snippet_1:end

        var grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(10);
        grid.addRow(0, captionLabel("ComboBox"), cmb1, cmb2);
        grid.addRow(1, captionLabel("ChoiceBox"), chb1, chb2);

        var description = BBCodeParser.createFormattedText("""
            The [i]ComboBox[/i] is an implementation of the [i]ComboBoxBase[/i] abstract class, \
            whereas the [i]CheckBox[/i] is more similar to the [i]MenuButton[/i]. Both controls \
            provide a selection model to manage the selected state."""
        );

        return new ExampleBox(grid, new Snippet(ComboBoxPage.class, 1), description);
    }

    private ExampleBox editableExample() {
        //snippet_2:start
        var cmb = new ComboBox<String>();
        cmb.setPrefWidth(PREF_WIDTH);
        cmb.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 5)
        ));
        cmb.getSelectionModel().selectFirst();
        cmb.setEditable(true);
        //snippet_2:end

        var box = new HBox(cmb);
        var description = BBCodeParser.createFormattedText("""
            The [i]ComboBox[/i] provides a way for end-users to input values \
            that are not available as options."""
        );

        return new ExampleBox(box, new Snippet(ComboBoxPage.class, 2), description);
    }

    private ExampleBox placeholderExample() {
        //snippet_3:start
        var cmb = new ComboBox<String>();
        cmb.setPrefWidth(PREF_WIDTH);
        cmb.setPlaceholder(new Label("Loading..."));
        //snippet_3:end

        var box = new HBox(cmb);
        var description = BBCodeParser.createFormattedText("""
            Placeholder is a node that is shown to the user when the [i]ComboBox[/i] \
            has no content to display."""
        );

        return new ExampleBox(box, new Snippet(ComboBoxPage.class, 3), description);
    }

    private ExampleBox customItemsExample() {
        //snippet_4:start
        record Badge(String text, Ikon icon) {
        }

        class BadgeCell extends ListCell<Badge> {

            @Override
            protected void updateItem(Badge badge, boolean isEmpty) {
                super.updateItem(badge, isEmpty);

                if (isEmpty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(new FontIcon(badge.icon()));
                    setText(badge.text());
                }
            }
        }

        var items = IntStream.range(0, 5).boxed()
            .map(i -> new Badge(FAKER.hipster().word(), randomIcon()))
            .collect(Collectors.toCollection(
                FXCollections::observableArrayList
            ));

        var cmb = new ComboBox<Badge>(items);
        cmb.setPrefWidth(PREF_WIDTH);
        cmb.setButtonCell(new BadgeCell());
        cmb.setCellFactory(c -> new BadgeCell());
        cmb.getSelectionModel().selectFirst();
        //snippet_4:end

        var box = new HBox(cmb);
        var description = BBCodeParser.createFormattedText("""
            The [i]ComboBox[/i] provides a custom cell factory that allows for \
            complete customization of how the items are rendered."""
        );

        return new ExampleBox(box, new Snippet(ComboBoxPage.class, 4), description);
    }

    private ExampleBox colorExample() {
        //snippet_5:start
        var cmb1 = new ComboBox<String>();
        cmb1.setPrefWidth(PREF_WIDTH);
        cmb1.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 5)
        ));
        cmb1.getSelectionModel().selectFirst();
        cmb1.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);

        var cmb2 = new ComboBox<String>();
        cmb2.setPrefWidth(PREF_WIDTH);
        cmb2.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 5)
        ));
        cmb2.getSelectionModel().selectFirst();
        cmb2.pseudoClassStateChanged(Styles.STATE_DANGER, true);

        var chb1 = new ChoiceBox<String>();
        chb1.setPrefWidth(PREF_WIDTH);
        chb1.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 5)
        ));
        chb1.getSelectionModel().selectFirst();
        chb1.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);

        var chb2 = new ChoiceBox<String>();
        chb2.setPrefWidth(PREF_WIDTH);
        chb2.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 5)
        ));
        chb2.getSelectionModel().selectFirst();
        chb2.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        //snippet_5:end

        var grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(10);
        grid.addRow(0, captionLabel("ComboBox"), cmb1, cmb2);
        grid.addRow(1, captionLabel("ChoiceBox"), chb1, chb2);

        var description = BBCodeParser.createFormattedText("""
            You can use [code]Styles.STATE_SUCCESS[/code] or [code]Styles.STATE_DANGER[/code] \
            pseudo-classes to change the control color. This especially useful to indicate \
            the validation result."""
        );

        return new ExampleBox(grid, new Snippet(ComboBoxPage.class, 5), description);
    }

    private ExampleBox overflowExample() {
        //snippet_7:start
        var cmb1 = new ComboBox<String>();
        cmb1.setPrefWidth(PREF_WIDTH);
        cmb1.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 50)
        ));
        cmb1.getSelectionModel().selectFirst();

        var cmb2 = new ComboBox<String>();
        cmb2.setPrefWidth(PREF_WIDTH);
        cmb2.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.chuckNorris().fact(), 10)
        ));
        cmb2.getSelectionModel().selectFirst();

        var chb1 = new ChoiceBox<String>();
        chb1.setPrefWidth(PREF_WIDTH);
        chb1.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 50)
        ));
        chb1.getSelectionModel().selectFirst();

        var chb2 = new ChoiceBox<String>();
        chb2.setPrefWidth(PREF_WIDTH);
        chb2.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.chuckNorris().fact(), 10)
        ));
        chb2.getSelectionModel().selectFirst();
        //snippet_7:end

        var grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(10);
        grid.addRow(0, captionLabel("ComboBox"), cmb1, cmb2);
        grid.addRow(1, captionLabel("ChoiceBox"), chb1, chb2);

        var description = BBCodeParser.createFormattedText("""
            This is just a simple example to test what happens when the item size \
            exceeds the limits of the popup window."""
        );

        return new ExampleBox(grid, new Snippet(ComboBoxPage.class, 7), description);
    }

    private ExampleBox altIconExample() {
        //snippet_6:start
        var cmb = new ComboBox<String>();
        cmb.setPrefWidth(PREF_WIDTH);
        cmb.getStyleClass().add(Tweaks.ALT_ICON);
        cmb.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 5)
        ));
        cmb.getSelectionModel().selectFirst();

        var chb = new ChoiceBox<String>();
        chb.setPrefWidth(PREF_WIDTH);
        chb.getStyleClass().add(Tweaks.ALT_ICON);
        chb.setItems(FXCollections.observableArrayList(
            generate(() -> FAKER.hipster().word(), 5)
        ));
        chb.getSelectionModel().selectFirst();
        //snippet_6:end

        var box = new HBox(30, cmb, chb);
        var description = BBCodeParser.createFormattedText("""
            There's additional tweak [code]Tweaks.ALT_ICON[/code] to change the control \
            dropdown icon."""
        );

        return new ExampleBox(box, new Snippet(ComboBoxPage.class, 6), description);
    }
}
