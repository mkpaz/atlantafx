/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.HPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ColorPickerPage extends AbstractPage {

    public static final String NAME = "ColorPicker";

    @Override
    public String getName() {
        return NAME;
    }

    public ColorPickerPage() {
        super();
        setUserContent(new VBox(
            new SampleBlock("Playground", createPlayground())
        ));
    }

    private GridPane createPlayground() {
        var colorPicker = new ColorPicker();
        colorPicker.setValue(Color.DEEPSKYBLUE);

        var labelToggle = new ToggleSwitch();
        labelToggle.setSelected(true);
        labelToggle.selectedProperty().addListener((obs, old, val) -> {
            colorPicker.setStyle("-fx-color-label-visible: false;");
            if (val) {
                colorPicker.setStyle("-fx-color-label-visible: true;");
            }
        });

        var disableToggle = new ToggleSwitch();
        colorPicker.disableProperty().bind(disableToggle.selectedProperty());

        var grid = new GridPane();
        grid.setHgap(BLOCK_HGAP);
        grid.setVgap(BLOCK_VGAP);
        grid.add(colorPicker, 0, 0, 1, GridPane.REMAINING);
        grid.add(createLabel("Show label"), 1, 0);
        grid.add(labelToggle, 2, 0);
        grid.add(createLabel("Picker style"), 1, 1);
        grid.add(createPickerStyleChoice(colorPicker), 2, 1);
        grid.add(createLabel("Disable"), 1, 2);
        grid.add(disableToggle, 2, 2);

        grid.getColumnConstraints().setAll(
            new ColumnConstraints(200),
            new ColumnConstraints(),
            new ColumnConstraints()
        );

        return grid;
    }

    private Label createLabel(String text) {
        var label = new Label(text);
        GridPane.setHalignment(label, HPos.RIGHT);
        return label;
    }

    private ChoiceBox<String> createPickerStyleChoice(ColorPicker colorPicker) {
        var optDefault = "Default";
        var optButton = "Button";
        var optSplitButton = "Split Button";

        var choice = new ChoiceBox<String>();
        choice.getItems().setAll(optDefault, optButton, optSplitButton);
        choice.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }

            colorPicker.getStyleClass().removeAll(
                ColorPicker.STYLE_CLASS_BUTTON,
                ColorPicker.STYLE_CLASS_SPLIT_BUTTON
            );

            if (optButton.equals(val)) {
                colorPicker.getStyleClass().add(ColorPicker.STYLE_CLASS_BUTTON);
            }
            if (optSplitButton.equals(val)) {
                colorPicker.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
            }
        });
        choice.getSelectionModel().select(optDefault);

        return choice;
    }
}
