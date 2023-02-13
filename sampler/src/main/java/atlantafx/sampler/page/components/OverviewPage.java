/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.BUTTON_CIRCLE;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.BUTTON_OUTLINED;
import static atlantafx.base.theme.Styles.DANGER;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.base.theme.Styles.LEFT_PILL;
import static atlantafx.base.theme.Styles.RIGHT_PILL;
import static atlantafx.base.theme.Styles.SUCCESS;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.IntegerStringConverter;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.IntStream;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class OverviewPage extends AbstractPage {

    public static final String NAME = "Overview";

    private static final int BUTTON_WIDTH = 120;
    private static final int COMBO_BOX_WIDTH = 150;

    @Override
    public String getName() {
        return NAME;
    }

    public OverviewPage() {
        super();
        setUserContent(new VBox(
            PAGE_VGAP,
            buttonSample(),
            expandingHBox(iconButtonSample(), dropdownMenuSample()),
            expandingHBox(checkBoxSample(), radioButtonSample(), toggleSwitchSample()),
            comboBoxSample(),
            sliderSample(),
            expandingHBox(textFieldSample(), spinnerSample()),
            textAreaSample()
        ));
    }

    private SampleBlock buttonSample() {
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
        var leftPill = createToggleButton("Toggle 1", twoButtonGroup, true, LEFT_PILL);
        leftPill.setPrefWidth(BUTTON_WIDTH + BLOCK_HGAP / 2.0);
        var rightPill = createToggleButton("Toggle 2", twoButtonGroup, false, RIGHT_PILL);
        rightPill.setPrefWidth(BUTTON_WIDTH + BLOCK_HGAP / 2.0);
        var twoButtonBox = new HBox(leftPill, rightPill);

        var grid = new GridPane();
        grid.setVgap(BLOCK_VGAP);
        grid.setHgap(BLOCK_HGAP);
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

        var grid = new GridPane();
        grid.setVgap(BLOCK_VGAP);
        grid.setHgap(BLOCK_HGAP);
        grid.add(basicBtn, 0, 0);
        grid.add(successBtn, 1, 0);
        grid.add(dangerBtn, 2, 0);
        grid.add(basicCircularBtn, 0, 1);
        grid.add(flatBtn, 1, 1);

        return new SampleBlock("Icon Buttons", grid);
    }

    private SampleBlock dropdownMenuSample() {
        var basicMenuBtn = new MenuButton("Menu Button");
        basicMenuBtn.getItems().setAll(createMenuItems());
        basicMenuBtn.setPrefWidth(COMBO_BOX_WIDTH);

        var basicIconBtn = new MenuButton();
        basicIconBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        basicIconBtn.getItems().setAll(createMenuItems());
        basicIconBtn.getStyleClass().addAll(BUTTON_ICON);

        var accentIconBtn = new MenuButton();
        accentIconBtn.setGraphic(new FontIcon(Feather.MENU));
        accentIconBtn.getItems().setAll(createMenuItems());
        accentIconBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);

        var basicSplitBtn = new SplitMenuButton(createMenuItems());
        basicSplitBtn.setText("Split Menu Button");

        var outlinedSplitBtn = new SplitMenuButton(createMenuItems());
        outlinedSplitBtn.setGraphic(new FontIcon(Feather.TRASH));
        outlinedSplitBtn.setText("Danger");
        outlinedSplitBtn.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
        outlinedSplitBtn.setPrefWidth(COMBO_BOX_WIDTH);

        var grid = new GridPane();
        grid.setVgap(BLOCK_VGAP);
        grid.setHgap(BLOCK_HGAP);
        grid.add(basicMenuBtn, 0, 0);
        grid.add(basicIconBtn, 1, 0);
        grid.add(accentIconBtn, 2, 0);
        grid.add(basicSplitBtn, 1, 1, 2, 1);
        grid.add(outlinedSplitBtn, 0, 1);

        return new SampleBlock("Dropdown Menus", grid);
    }

    private SampleBlock checkBoxSample() {
        var opt1 = new CheckBox("Option 1");

        var opt2 = new CheckBox("Option 2");
        opt2.setSelected(true);

        var opt3 = new CheckBox("Option 3");
        opt3.setAllowIndeterminate(true);
        opt3.setIndeterminate(true);

        var container = new VBox(BLOCK_VGAP, opt1, opt2, opt3);
        return new SampleBlock("Check Boxes", container);
    }

    private SampleBlock radioButtonSample() {
        var group = new ToggleGroup();

        var opt1 = new RadioButton("Option 1");
        opt1.setToggleGroup(group);

        var opt2 = new RadioButton("Option 2");
        opt2.setToggleGroup(group);
        opt2.setSelected(true);

        var opt3 = new RadioButton("Option 3");
        opt3.setToggleGroup(group);

        var container = new VBox(BLOCK_VGAP, opt1, opt2, opt3);
        return new SampleBlock("Radio Buttons", container);
    }

    private SampleBlock toggleSwitchSample() {
        var switch1 = new ToggleSwitch();

        var switch2 = new ToggleSwitch();
        switch2.setSelected(true);

        var container = new VBox(BLOCK_VGAP, switch1, switch2);
        return new SampleBlock("Switches", container);
    }

    private SampleBlock comboBoxSample() {
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
        datePicker.setValue(LocalDate.now(ZoneId.systemDefault()));

        var colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(COMBO_BOX_WIDTH);
        colorPicker.setValue(Color.ORANGE);

        var container = new HBox(BLOCK_HGAP, comboBox, choiceBox, datePicker, colorPicker);
        return new SampleBlock("Combo Boxes", container);
    }

    private SampleBlock sliderSample() {
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
        tickSlider.setSkin(new ProgressSliderSkin(tickSlider));

        var container = new HBox(BLOCK_HGAP, slider, tickSlider);
        return new SampleBlock("Sliders", container);
    }

    private SampleBlock textFieldSample() {
        var textField = new TextField("Text");
        textField.setPrefWidth(COMBO_BOX_WIDTH);

        var passwordField = new PasswordField();
        passwordField.setText(FAKER.internet().password());
        passwordField.setPrefWidth(COMBO_BOX_WIDTH);

        var container = new HBox(BLOCK_HGAP, textField, passwordField);
        return new SampleBlock("Text Fields", container);
    }

    private SampleBlock spinnerSample() {
        var spinner1 = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner1);
        spinner1.setPrefWidth(COMBO_BOX_WIDTH);

        var spinner2 = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner2);
        spinner2.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner2.setPrefWidth(COMBO_BOX_WIDTH);

        var container = new HBox(BLOCK_HGAP, spinner1, spinner2);
        return new SampleBlock("Spinners", container);
    }

    private SampleBlock textAreaSample() {
        var textArea = new TextArea(String.join("\n\n", FAKER.lorem().paragraphs(3)));
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMinHeight(100);

        return new SampleBlock("Text Area", textArea);
    }

    private ToggleButton createToggleButton(String text,
                                            ToggleGroup group,
                                            boolean selected,
                                            String... styleClasses) {
        var toggleButton = new ToggleButton(text);
        if (group != null) {
            toggleButton.setToggleGroup(group);
        }
        toggleButton.setSelected(selected);
        toggleButton.getStyleClass().addAll(styleClasses);

        return toggleButton;
    }

    private MenuItem[] createMenuItems() {
        return IntStream.range(0, 5)
            .mapToObj(i -> new MenuItem(FAKER.babylon5().character()))
            .toArray(MenuItem[]::new);
    }
}
