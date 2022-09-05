/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.page.AbstractPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static atlantafx.base.theme.Styles.STATE_DANGER;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;
import static atlantafx.sampler.util.Containers.H_GROW_NEVER;
import static javafx.collections.FXCollections.observableArrayList;

public class ComboBoxPage extends AbstractPage {

    public static final String NAME = "ComboBox";
    private static final int PREF_WIDTH = 200;

    @Override
    public String getName() { return NAME; }

    public ComboBoxPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(
                createGrid()
        );
    }

    private Pane createGrid() {
        var grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.getColumnConstraints().setAll(H_GROW_NEVER, H_GROW_NEVER, H_GROW_NEVER);
        grid.setMaxWidth((PREF_WIDTH * 3) + 100);

        var comboLabel = new Label("C_omboBox");
        comboLabel.setMnemonicParsing(true);

        comboLabel.setStyle("-fx-font-weight: bold;");
        grid.add(comboLabel, 0, 0);

        var choiceLabel = new Label("C_hoiceBox");
        choiceLabel.setMnemonicParsing(true);

        choiceLabel.setStyle("-fx-font-weight: bold;");
        grid.add(choiceLabel, 2, 0);

        // default
        grid.add(comboBox(), 0, 1);
        grid.add(label("empty"), 1, 1);
        grid.add(choiceBox(), 2, 1);

        // editable
        grid.add(comboBox(c -> {
            c.setItems(createItems(5));
            c.setEditable(true);
        }), 0, 2);
        grid.add(label("editable"), 1, 2);

        // placeholder
        grid.add(comboBox(c -> c.setPlaceholder(new Label("Loading..."))), 0, 3);
        grid.add(label("placeholder"), 1, 3);

        // with icons
        var badges = IntStream.range(0, 5).boxed()
                .map(i -> new Badge(FAKER.hipster().word(), randomIcon()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        var badgeCombo = new ComboBox<>(badges);
        badgeCombo.setPrefWidth(PREF_WIDTH);
        badgeCombo.setButtonCell(new BadgeCell());
        badgeCombo.setCellFactory(lv -> new BadgeCell());
        badgeCombo.getSelectionModel().selectFirst();
        grid.add(badgeCombo, 0, 4);
        grid.add(label("graphic"), 1, 4);

        // success
        grid.add(comboBox(c -> {
            c.setItems(createItems(5));
            c.pseudoClassStateChanged(STATE_SUCCESS, true);
            c.getSelectionModel().selectFirst();
        }), 0, 5);
        grid.add(label("success"), 1, 5);
        grid.add(choiceBox(c -> {
            c.setItems(createItems(5));
            c.pseudoClassStateChanged(STATE_SUCCESS, true);
            c.getSelectionModel().selectFirst();
        }), 2, 5);

        // negative
        grid.add(comboBox(c -> {
            c.setItems(createItems(5));
            c.pseudoClassStateChanged(STATE_DANGER, true);
            c.getSelectionModel().selectFirst();
        }), 0, 6);
        grid.add(label("success"), 1, 6);
        grid.add(choiceBox(c -> {
            c.setItems(createItems(5));
            c.pseudoClassStateChanged(STATE_DANGER, true);
            c.getSelectionModel().selectFirst();
        }), 2, 6);

        // alt icon
        grid.add(comboBox(c -> {
            c.setItems(createItems(5));
            c.getStyleClass().add(Tweaks.ALT_ICON);
            c.getSelectionModel().selectFirst();
        }), 0, 7);
        grid.add(label("alt icon"), 1, 7);
        grid.add(choiceBox(c -> {
            c.setItems(createItems(5));
            c.getStyleClass().add(Tweaks.ALT_ICON);
            c.getSelectionModel().selectFirst();
        }), 2, 7);

        // disabled
        grid.add(comboBox(c -> c.setDisable(true)), 0, 8);
        grid.add(label("disabled"), 1, 8);
        grid.add(choiceBox(c -> c.setDisable(true)), 2, 8);

        // overflow
        grid.add(comboBox(c -> {
            c.setItems(createItems(50));
            c.getSelectionModel().selectFirst();
        }), 0, 9);
        grid.add(label("large list"), 1, 9);
        grid.add(choiceBox(c -> {
            c.setItems(createItems(50));
            c.getSelectionModel().selectFirst();
        }), 2, 9);

        // overflow
        grid.add(comboBox(c -> {
            c.setItems(observableArrayList(generate(() -> FAKER.chuckNorris().fact(), 5)));
            c.getSelectionModel().selectFirst();
        }), 0, 10);
        grid.add(label("wide text"), 1, 10);
        grid.add(choiceBox(c -> {
            c.setItems(observableArrayList(generate(() -> FAKER.chuckNorris().fact(), 5)));
            c.getSelectionModel().selectFirst();
        }), 2, 10);

        return grid;
    }

    private ObservableList<String> createItems(int count) {
        return observableArrayList(generate(() -> FAKER.hipster().word(), count));
    }

    private Label label(String text) {
        return new Label(text) {{
            GridPane.setHalignment(this, HPos.CENTER);
        }};
    }

    private ComboBox<String> comboBox() {
        return comboBox(null);
    }

    private ComboBox<String> comboBox(Consumer<ComboBox<String>> mutator) {
        var c = new ComboBox<String>();
        c.setPrefWidth(PREF_WIDTH);
        if (mutator != null) { mutator.accept(c); }
        return c;
    }

    private ChoiceBox<String> choiceBox() {
        return choiceBox(null);
    }

    private ChoiceBox<String> choiceBox(Consumer<ChoiceBox<String>> mutator) {
        var c = new ChoiceBox<String>();
        c.setPrefWidth(PREF_WIDTH);
        if (mutator != null) { mutator.accept(c); }
        return c;
    }

    ///////////////////////////////////////////////////////////////////////////

    private record Badge(String text, Ikon icon) { }

    private static class BadgeCell extends ListCell<Badge> {

        @Override
        protected void updateItem(Badge item, boolean isEmpty) {
            super.updateItem(item, isEmpty);

            if (isEmpty) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new FontIcon(item.icon()));
                setText(item.text());
            }
        }
    }
}
