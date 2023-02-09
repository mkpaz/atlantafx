/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.STATE_DANGER;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;
import static atlantafx.sampler.util.Containers.H_GROW_NEVER;
import static javafx.collections.FXCollections.observableArrayList;

import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class ComboBoxPage extends AbstractPage {

    public static final String NAME = "ComboBox";
    private static final int PREF_WIDTH = 200;

    @Override
    public String getName() {
        return NAME;
    }

    public ComboBoxPage() {
        super();
        setUserContent(new VBox(
            new SampleBlock("Examples", createPlayground())
        ));
    }

    private GridPane createPlayground() {
        var grid = new GridPane();
        grid.setHgap(BLOCK_HGAP);
        grid.setVgap(BLOCK_VGAP);
        grid.getColumnConstraints().setAll(H_GROW_NEVER, H_GROW_NEVER, H_GROW_NEVER);

        var comboLabel = new Label("C_omboBox");
        comboLabel.setMnemonicParsing(true);
        grid.add(comboLabel, 0, 0);

        var choiceLabel = new Label("C_hoiceBox");
        choiceLabel.setMnemonicParsing(true);
        grid.add(choiceLabel, 2, 0);

        // default
        grid.add(createComboBox(), 0, 1);
        grid.add(createLabel("empty"), 1, 1);
        grid.add(createChoiceBox(), 2, 1);

        // editable
        grid.add(createComboBoxWith(c -> {
            c.setItems(createItems(5));
            c.setEditable(true);
        }), 0, 2);
        grid.add(createLabel("editable"), 1, 2);

        // placeholder
        grid.add(createComboBoxWith(c -> c.setPlaceholder(new Label("Loading..."))), 0, 3);
        grid.add(createLabel("placeholder"), 1, 3);

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
        grid.add(createLabel("graphic"), 1, 4);

        // success
        grid.add(createComboBoxWith(c -> {
            c.setItems(createItems(5));
            c.pseudoClassStateChanged(STATE_SUCCESS, true);
            c.getSelectionModel().selectFirst();
        }), 0, 5);
        grid.add(createLabel("success"), 1, 5);
        grid.add(createChoiceBoxWith(c -> {
            c.setItems(createItems(5));
            c.pseudoClassStateChanged(STATE_SUCCESS, true);
            c.getSelectionModel().selectFirst();
        }), 2, 5);

        // negative
        grid.add(createComboBoxWith(c -> {
            c.setItems(createItems(5));
            c.pseudoClassStateChanged(STATE_DANGER, true);
            c.getSelectionModel().selectFirst();
        }), 0, 6);
        grid.add(createLabel("success"), 1, 6);
        grid.add(createChoiceBoxWith(c -> {
            c.setItems(createItems(5));
            c.pseudoClassStateChanged(STATE_DANGER, true);
            c.getSelectionModel().selectFirst();
        }), 2, 6);

        // alt icon
        grid.add(createComboBoxWith(c -> {
            c.setItems(createItems(5));
            c.getStyleClass().add(Tweaks.ALT_ICON);
            c.getSelectionModel().selectFirst();
        }), 0, 7);
        grid.add(createLabel("alt icon"), 1, 7);
        grid.add(createChoiceBoxWith(c -> {
            c.setItems(createItems(5));
            c.getStyleClass().add(Tweaks.ALT_ICON);
            c.getSelectionModel().selectFirst();
        }), 2, 7);

        // disabled
        grid.add(createComboBoxWith(c -> c.setDisable(true)), 0, 8);
        grid.add(createLabel("disabled"), 1, 8);
        grid.add(createChoiceBoxWith(c -> c.setDisable(true)), 2, 8);

        // vertical overflow
        grid.add(createComboBoxWith(c -> {
            c.setItems(createItems(50));
            c.getSelectionModel().selectFirst();
        }), 0, 9);
        grid.add(createLabel("large list"), 1, 9);
        grid.add(createChoiceBoxWith(c -> {
            c.setItems(createItems(50));
            c.getSelectionModel().selectFirst();
        }), 2, 9);

        // horizontal overflow
        grid.add(createComboBoxWith(c -> {
            c.setItems(observableArrayList(generate(() -> FAKER.chuckNorris().fact(), 5)));
            c.getSelectionModel().selectFirst();
        }), 0, 10);
        grid.add(createLabel("wide text"), 1, 10);
        grid.add(createChoiceBoxWith(c -> {
            c.setItems(observableArrayList(generate(() -> FAKER.chuckNorris().fact(), 5)));
            c.getSelectionModel().selectFirst();
        }), 2, 10);

        return grid;
    }

    private Label createLabel(String text) {
        var label = new Label(text);
        GridPane.setHalignment(label, HPos.CENTER);
        return label;
    }

    private ComboBox<String> createComboBox() {
        return createComboBoxWith(null);
    }

    private ComboBox<String> createComboBoxWith(Consumer<ComboBox<String>> mutator) {
        var c = new ComboBox<String>();
        c.setPrefWidth(PREF_WIDTH);
        if (mutator != null) {
            mutator.accept(c);
        }
        return c;
    }

    private ChoiceBox<String> createChoiceBox() {
        return createChoiceBoxWith(null);
    }

    private ChoiceBox<String> createChoiceBoxWith(Consumer<ChoiceBox<String>> mutator) {
        var c = new ChoiceBox<String>();
        c.setPrefWidth(PREF_WIDTH);
        if (mutator != null) {
            mutator.accept(c);
        }
        return c;
    }

    private ObservableList<String> createItems(int count) {
        return observableArrayList(generate(() -> FAKER.hipster().word(), count));
    }

    ///////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private record Badge(String text, Ikon icon) {
    }

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
