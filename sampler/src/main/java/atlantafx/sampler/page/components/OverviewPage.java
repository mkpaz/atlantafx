/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.IntegerStringConverter;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static atlantafx.base.theme.Styles.*;

public class OverviewPage extends AbstractPage {

    public static final String NAME = "Overview";

    private static final int BUTTON_WIDTH = 120;
    private static final int COMBO_BOX_WIDTH = 150;
    private static final int H_GAP = 20;
    private static final int V_GAP = 10;

    @Override
    public String getName() { return NAME; }

    public OverviewPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(
                buttonSample().getRoot(),
                new HBox(H_GAP * 2,
                         iconButtonSample().getRoot(),
                         dropdownMenuSample().getRoot()
                ),
                new HBox(H_GAP * 2,
                         checkBoxSample().getRoot(),
                         radioButtonSample().getRoot(),
                         toggleSwitchSample().getRoot()
                ),
                comboBoxSample().getRoot(),
                sliderSample().getRoot(),
                new HBox(H_GAP * 2,
                         textFieldSample().getRoot(),
                         spinnerSample().getRoot()
                ),
                textAreaSample().getRoot()
        );
    }

    private SampleBlock buttonSample() {
        var grid = new GridPane();
        grid.setVgap(V_GAP);
        grid.setHgap(H_GAP);

        var basicBtn = new Button("Basic");
        basicBtn.setPrefWidth(BUTTON_WIDTH);

        var defaultBtn = new Button("Default");
        defaultBtn.setDefaultButton(true);
        defaultBtn.setPrefWidth(BUTTON_WIDTH);

        var successBtn = new Button("Success");
        successBtn.getStyleClass().add(SUCCESS);
        successBtn.setPrefWidth(BUTTON_WIDTH);

        var dangerBtn = new Button("Danger");
        dangerBtn.getStyleClass().add(DANGER);
        dangerBtn.setPrefWidth(BUTTON_WIDTH);

        var flatBtn = new Button("Flat");
        flatBtn.getStyleClass().add(FLAT);
        flatBtn.setPrefWidth(BUTTON_WIDTH);

        var outlinedBtn = new Button("Outlined");
        outlinedBtn.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
        outlinedBtn.setPrefWidth(BUTTON_WIDTH);

        var twoButtonGroup = new ToggleGroup();
        var leftPill = toggleButton("Toggle 1", twoButtonGroup, true, LEFT_PILL);
        leftPill.setPrefWidth(BUTTON_WIDTH + grid.getHgap() / 2);
        var rightPill = toggleButton("Toggle 2", twoButtonGroup, false, RIGHT_PILL);
        rightPill.setPrefWidth(BUTTON_WIDTH + grid.getHgap() / 2);
        var twoButtonBox = new HBox(leftPill, rightPill);

        // ~
        grid.add(basicBtn, 0, 0);
        grid.add(flatBtn, 1, 0);
        grid.add(successBtn, 2, 0);
        grid.add(dangerBtn, 3, 0);

        grid.add(defaultBtn, 0, 1);
        grid.add(outlinedBtn, 1, 1);
        grid.add(twoButtonBox, 2, 1, 2, 1);

        return new SampleBlock("Buttons", grid);
    }

    private SampleBlock iconButtonSample() {
        var grid = new GridPane();
        grid.setVgap(V_GAP);
        grid.setHgap(H_GAP);

        var basicBtn = new Button("", new FontIcon(Feather.MORE_HORIZONTAL));
        basicBtn.getStyleClass().addAll(BUTTON_ICON);

        var successBtn = new Button("", new FontIcon(Feather.PLUS));
        successBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);

        var dangerBtn = new Button("", new FontIcon(Feather.TRASH));
        dangerBtn.getStyleClass().addAll(BUTTON_ICON, DANGER);

        var basicCircularBtn = new Button("", new FontIcon(Feather.MORE_VERTICAL));
        basicCircularBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        basicCircularBtn.setShape(new Circle(50));

        var flatBtn = new Button("", new FontIcon(Feather.MIC));
        flatBtn.getStyleClass().addAll(BUTTON_CIRCLE, FLAT);
        flatBtn.setShape(new Circle(50));

        // ~
        grid.add(basicBtn, 0, 0);
        grid.add(successBtn, 1, 0);
        grid.add(dangerBtn, 2, 0);
        grid.add(basicCircularBtn, 0, 1);
        grid.add(flatBtn, 1, 1);

        return new SampleBlock("Icon Buttons", grid);
    }

    private SampleBlock dropdownMenuSample() {
        var grid = new GridPane();
        grid.setVgap(V_GAP);
        grid.setHgap(H_GAP);

        var basicIconBtn = new MenuButton();
        basicIconBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        basicIconBtn.getItems().setAll(menuItems(5));
        basicIconBtn.getStyleClass().addAll(BUTTON_ICON);

        var accentIconBtn = new MenuButton();
        accentIconBtn.setGraphic(new FontIcon(Feather.MENU));
        accentIconBtn.getItems().setAll(menuItems(5));
        accentIconBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);

        var basicMenuBtn = new MenuButton("Menu Button");
        basicMenuBtn.getItems().setAll(menuItems(5));
        basicMenuBtn.setPrefWidth(COMBO_BOX_WIDTH);

        var basicSplitBtn = new SplitMenuButton(menuItems(5));
        basicSplitBtn.setText("Split Menu Button");

        var outlinedSplitBtn = new SplitMenuButton(menuItems(5));
        outlinedSplitBtn.setGraphic(new FontIcon(Feather.TRASH));
        outlinedSplitBtn.setText("Danger");
        outlinedSplitBtn.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
        outlinedSplitBtn.setPrefWidth(COMBO_BOX_WIDTH);

        // ~
        grid.add(basicIconBtn, 0, 0);
        grid.add(accentIconBtn, 1, 0);
        grid.add(basicMenuBtn, 2, 0);
        grid.add(basicSplitBtn, 0, 1, 2, 1);
        grid.add(outlinedSplitBtn, 2, 1);

        return new SampleBlock("Dropdown Menus", grid);
    }

    private SampleBlock checkBoxSample() {
        var box = new VBox(V_GAP);

        var opt1 = new CheckBox("Option 1");

        var opt2 = new CheckBox("Option 2");
        opt2.setSelected(true);

        var opt3 = new CheckBox("Option 3");
        opt3.setAllowIndeterminate(true);
        opt3.setIndeterminate(true);

        box.getChildren().setAll(opt1, opt2, opt3);
        return new SampleBlock("Check Boxes", box);
    }

    private SampleBlock radioButtonSample() {
        var box = new VBox(V_GAP);

        var group = new ToggleGroup();

        var opt1 = new RadioButton("Option 1");
        opt1.setToggleGroup(group);

        var opt2 = new RadioButton("Option 2");
        opt2.setToggleGroup(group);
        opt2.setSelected(true);

        var opt3 = new RadioButton("Option 3");
        opt3.setToggleGroup(group);

        box.getChildren().setAll(opt1, opt2, opt3);
        return new SampleBlock("Radio Buttons", box);
    }

    private SampleBlock toggleSwitchSample() {
        var box = new VBox(V_GAP);

        var switch1 = new ToggleSwitch();

        var switch2 = new ToggleSwitch();
        switch2.setSelected(true);

        box.getChildren().setAll(switch1, switch2);
        return new SampleBlock("Switches", box);
    }

    private SampleBlock comboBoxSample() {
        var box = new HBox(H_GAP);

        var comboBox = new ComboBox<String>();
        comboBox.getItems().setAll("Option 1", "Option 2", "Option 3");
        comboBox.getStyleClass().add(Tweaks.ALT_ICON);
        comboBox.getSelectionModel().selectFirst();
        comboBox.setPrefWidth(COMBO_BOX_WIDTH);

        var choiceBox = new ChoiceBox<String>();
        choiceBox.getItems().setAll("Option 1", "Option 2", "Option 3");
        choiceBox.getSelectionModel().selectFirst();
        choiceBox.setPrefWidth(COMBO_BOX_WIDTH);

        var datePicker = new DatePicker();
        datePicker.setPrefWidth(COMBO_BOX_WIDTH);
        datePicker.setValue(LocalDate.now());

        var colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(COMBO_BOX_WIDTH);
        colorPicker.setValue(Color.ORANGE);

        box.getChildren().setAll(comboBox, choiceBox, datePicker, colorPicker);
        return new SampleBlock("Combo Boxes", box);
    }

    private SampleBlock sliderSample() {
        var box = new HBox(H_GAP);

        var slider = new Slider(1, 5, 3);
        slider.setPrefWidth(BUTTON_WIDTH * 2);

        var tickSlider = new Slider(0, 5, 3);
        tickSlider.setShowTickLabels(true);
        tickSlider.setShowTickMarks(true);
        tickSlider.setMajorTickUnit(1);
        tickSlider.setBlockIncrement(1);
        tickSlider.setMinorTickCount(5);
        tickSlider.setSnapToTicks(true);
        tickSlider.setPrefWidth(BUTTON_WIDTH * 2);

        box.getChildren().setAll(slider, tickSlider);
        return new SampleBlock("Sliders", box);
    }

    private SampleBlock textFieldSample() {
        var box = new HBox(H_GAP);

        var textField = new TextField("Text");
        textField.setPrefWidth(BUTTON_WIDTH);

        var passwordField = new PasswordField();
        passwordField.setText(FAKER.internet().password());
        passwordField.setPrefWidth(BUTTON_WIDTH);

        box.getChildren().setAll(textField, passwordField);
        return new SampleBlock("Text Fields", box);
    }

    private SampleBlock spinnerSample() {
        var box = new HBox(H_GAP);

        var spinner1 = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner1);
        spinner1.setPrefWidth(BUTTON_WIDTH);

        var spinner2 = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner2);
        spinner2.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner2.setPrefWidth(BUTTON_WIDTH);

        box.getChildren().setAll(spinner1, spinner2);
        return new SampleBlock("Spinners", box);
    }

    private SampleBlock textAreaSample() {
        var textArea = new TextArea(String.join("\n\n", FAKER.lorem().paragraphs(3)));
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMinHeight(100);

        return new SampleBlock("Text Area", textArea);
    }

    private ToggleButton toggleButton(String text,
                                      ToggleGroup group,
                                      boolean selected,
                                      String... styleClasses) {
        var toggleButton = new ToggleButton(text);
        if (group != null) { toggleButton.setToggleGroup(group); }
        toggleButton.setSelected(selected);
        toggleButton.getStyleClass().addAll(styleClasses);

        return toggleButton;
    }

    @SuppressWarnings("SameParameterValue")
    private MenuItem[] menuItems(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new MenuItem(FAKER.babylon5().character()))
                .toArray(MenuItem[]::new);
    }
}
