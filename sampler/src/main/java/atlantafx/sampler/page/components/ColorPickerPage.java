/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.controls.Spacer;
import atlantafx.sampler.page.AbstractPage;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ColorPickerPage extends AbstractPage {

    public static final String NAME = "ColorPicker";

    @Override
    public String getName() { return NAME; }

    public ColorPickerPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(
                playground()
        );
    }

    private VBox playground() {
        var colorPicker = new ColorPicker();
        colorPicker.setValue(Color.AQUA);

        var pickerBox = new HBox(
                new Spacer(),
                colorPicker,
                new Spacer()
        );

        var labelToggle = new ToggleSwitch("Show label");
        labelToggle.setSelected(true);
        labelToggle.selectedProperty().addListener((obs, old, val) -> {
            colorPicker.setStyle("-fx-color-label-visible: false;");
            if (val) { colorPicker.setStyle("-fx-color-label-visible: true;"); }
        });

        var disableToggle = new ToggleSwitch("Disable");
        colorPicker.disableProperty().bind(disableToggle.selectedProperty());

        var pickerStyleBox = new HBox(5, new Label("Picker Style"), pickerStyleChoice(colorPicker));
        pickerStyleBox.setAlignment(Pos.CENTER);

        var controls = new HBox(20,
                                new Spacer(),
                                pickerStyleBox,
                                labelToggle,
                                disableToggle,
                                new Spacer()
        );
        controls.setAlignment(Pos.CENTER);

        // ~

        var root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().setAll(
                pickerBox,
                new Separator(),
                controls
        );

        return root;
    }

    private ChoiceBox<String> pickerStyleChoice(ColorPicker colorPicker) {
        var optDefault = "Default";
        var optButton = "Button";
        var optSplitButton = "Split Button";

        var choice = new ChoiceBox<String>();
        choice.getItems().setAll(optDefault, optButton, optSplitButton);
        choice.setPrefWidth(120);
        choice.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val == null) { return; }
            colorPicker.getStyleClass().removeAll(ColorPicker.STYLE_CLASS_BUTTON, ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
            if (optButton.equals(val)) { colorPicker.getStyleClass().add(ColorPicker.STYLE_CLASS_BUTTON); }
            if (optSplitButton.equals(val)) { colorPicker.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON); }
        });
        choice.getSelectionModel().select(optDefault);

        return choice;
    }
}
