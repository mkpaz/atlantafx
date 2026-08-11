/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Colour;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.util.PlatformUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.Map;
import java.util.Objects;

import static atlantafx.sampler.page.general.ColorPaletteBlock.validateColorName;

// Inspired by the https://colourcontrast.cc/
final class ContrastChecker extends GridPane {

    public static final double CONTRAST_RATIO_THRESHOLD = 1.5;
    public static final double LUMINANCE_THRESHOLD = 0.55;
    public static final PseudoClass PASSED = PseudoClass.getPseudoClass("passed");

    private static final String STATE_PASS = "PASS";
    private static final String STATE_FAIL = "FAIL";
    private static final int SLIDER_WIDTH = 300;

    private @Nullable String bgColorName;
    private @Nullable String fgColorName;

    private final Colour bgColor = Colour.color(Color.WHITE);
    private final Colour fgColor = Colour.color(Color.BLACK);
    private final Colour bgBaseColor;
    private final DoubleBinding contrastRatio;

    private Label bgColorNameLabel;
    private Label fgColorNameLabel;
    private Slider bgHueSlider;
    private Slider bgSaturationSlider;
    private Slider bgLightnessSlider;
    private Slider bgAlphaSlider;
    private Slider fgHueSlider;
    private Slider fgSaturationSlider;
    private Slider fgLightnessSlider;
    private Slider fgAlphaSlider;

    public ContrastChecker(Colour bgBaseColor) {
        super();

        this.bgBaseColor = bgBaseColor;
        this.contrastRatio = Bindings.createDoubleBinding(
            () -> Colour.ContrastLevel.getContrastRatioOpacityAware(bgColor, fgColor, bgBaseColor),
            bgColor, fgColor, bgBaseColor
        );

        createView();
    }

    public void setValues(String fgColorName, Colour fgColor,
                          String bgColorName, Colour bgColor) {
        this.bgColorName = validateColorName(bgColorName);
        bgColorNameLabel.setText(bgColorName);
        setBackground(bgColor);

        this.fgColorName = validateColorName(fgColorName);
        fgColorNameLabel.setText(fgColorName);
        setForeground(fgColor);
    }

    public Colour getBgBaseColor() {
        return bgBaseColor;
    }

    public @Nullable String getBgColorName() {
        return bgColorName;
    }

    public @Nullable String getFgColorName() {
        return fgColorName;
    }

    public Colour getBgColor() {
        return bgColor;
    }

    public Colour getFgColor() {
        return fgColor;
    }

    public Colour getFlatBgColor() {
        return bgColor.flatten(bgBaseColor);
    }

    // Returns fg color that guaranteed to be visible on the current bg.
    public Colour getSafeFgColor() {
        if (contrastRatio.get() <= CONTRAST_RATIO_THRESHOLD) {
            var luminance = bgColor.flatten(bgBaseColor).getLuminance();
            return luminance < LUMINANCE_THRESHOLD ? Colour.color(Color.WHITE) : Colour.color(Color.BLACK);
        } else {
            return fgColor;
        }
    }

    private void createView() {
        var largeFontLabel = new Label("Aa");
        largeFontLabel.getStyleClass().add("large-font");

        var contrastRatioLabel = new Label("0.0");
        contrastRatioLabel.getStyleClass().add("ratio");
        contrastRatioLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("%.2f", contrastRatio.get()), contrastRatio
        ));

        var contrastRatioBox = new HBox(20, largeFontLabel, contrastRatioLabel);
        contrastRatioBox.getStyleClass().add("contrast-ratio");
        contrastRatioBox.setAlignment(Pos.BASELINE_LEFT);

        // !

        var aaNormalLabel = contrastLevelLabel();
        var aaNormalBox = contrastLevelBox(aaNormalLabel, "AA Normal");

        var aaLargeLabel = contrastLevelLabel();
        var aaLargeBox = contrastLevelBox(aaLargeLabel, "AA Large");

        var aaaNormalLabel = contrastLevelLabel();
        var aaaNormalBox = contrastLevelBox(aaaNormalLabel, "AAA Normal");

        var aaaLargeLabel = contrastLevelLabel();
        var aaaLargeBox = contrastLevelBox(aaaLargeLabel, "AAA Large");

        final var contrastLevels = new HBox(20, aaNormalBox, aaLargeBox, aaaNormalBox, aaaLargeBox);

        contrastRatio.addListener((_, _, val) -> {
            if (val == null) {
                return;
            }
            float ratio = val.floatValue();
            updateContrastLevelLabel(aaNormalLabel, Colour.ContrastLevel.AA_NORMAL.satisfies(ratio));
            updateContrastLevelLabel(aaLargeLabel, Colour.ContrastLevel.AA_LARGE.satisfies(ratio));
            updateContrastLevelLabel(aaaNormalLabel, Colour.ContrastLevel.AAA_NORMAL.satisfies(ratio));
            updateContrastLevelLabel(aaaLargeLabel, Colour.ContrastLevel.AAA_LARGE.satisfies(ratio));
        });

        // ~

        bgColorNameLabel = new Label("Background Color");
        bgColorNameLabel.setPadding(new Insets(-15, 0, 0, 0));
        bgColorNameLabel.getStyleClass().add(Styles.TEXT_SMALL);

        var bgTextField = new CustomTextField();
        bgTextField.setEditable(false);
        bgTextField.setLeft(new FontIcon(Feather.HASH));
        bgTextField.textProperty().bind(Bindings.createStringBinding(
            () -> bgColor.toHex(true).substring(1), bgColor
        ));
        bgTextField.setContextMenu(new RightClickMenu(bgColor));

        fgColorNameLabel = new Label("Foreground Color");
        fgColorNameLabel.setPadding(new Insets(-15, 0, 0, 0));
        fgColorNameLabel.getStyleClass().add(Styles.TEXT_SMALL);

        var fgTextField = new CustomTextField();
        fgTextField.setEditable(false);
        fgTextField.setLeft(new FontIcon(Feather.HASH));
        fgTextField.textProperty().bind(Bindings.createStringBinding(
            () -> fgColor.toHex(true).substring(1), fgColor
        ));
        fgTextField.setContextMenu(new RightClickMenu(fgColor));

        bgHueSlider = slider(1, 360, 1, 1);
        bgHueSlider.valueProperty().addListener((_, _, val) -> {
            if (val != null) {
                var hsl = new Colour.HSL(val.floatValue(), bgSaturationSlider.getValue(), bgLightnessSlider.getValue());
                updateColor(bgColor, hsl);
            }
        });
        var bgHueLabel = new Label("Hue °");
        bgHueLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Hue %.0f °", bgHueSlider.getValue()), bgHueSlider.valueProperty())
        );

        bgSaturationSlider = slider(0, 1, 0, 0.01);
        bgSaturationSlider.valueProperty().addListener((_, _, val) -> {
            if (val != null) {
                var hsl = new Colour.HSL(bgHueSlider.getValue(), val.floatValue(), bgLightnessSlider.getValue());
                updateColor(bgColor, hsl);
            }
        });
        var bgSaturationLabel = new Label("Saturation");
        bgSaturationLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Saturation %.2f", bgSaturationSlider.getValue()), bgSaturationSlider.valueProperty())
        );

        bgLightnessSlider = slider(0, 1, 0, 0.01);
        bgLightnessSlider.valueProperty().addListener((_, _, val) -> {
            if (val != null) {
                var hsl = new Colour.HSL(bgHueSlider.getValue(), bgSaturationSlider.getValue(), val.floatValue());
                updateColor(bgColor, hsl);
            }
        });
        var bgLightnessLabel = new Label("Lightness");
        bgLightnessLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Lightness %.2f", bgLightnessSlider.getValue()), bgLightnessSlider.valueProperty())
        );

        bgAlphaSlider = slider(0, 1, 0, 0.01);
        bgAlphaSlider.valueProperty().addListener((_, _, val) -> {
            if (val != null) {
                bgColor.setOpacity(val.floatValue());
            }
        });
        var bgAlphaLabel = new Label("Alpha");
        bgAlphaLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Alpha %.2f", bgAlphaSlider.getValue()), bgAlphaSlider.valueProperty())
        );

        // ~

        fgHueSlider = slider(1, 360, 1, 1);
        fgHueSlider.valueProperty().addListener((_, _, val) -> {
            if (val != null) {
                var hsl = new Colour.HSL(val.floatValue(), fgSaturationSlider.getValue(), fgLightnessSlider.getValue());
                updateColor(fgColor, hsl);
            }
        });
        var fgHueLabel = new Label("Hue °");
        fgHueLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Hue %.0f °", fgHueSlider.getValue()), fgHueSlider.valueProperty())
        );

        fgSaturationSlider = slider(0, 1, 0, 0.01);
        fgSaturationSlider.valueProperty().addListener((_, _, val) -> {
            if (val != null) {
                var hsl = new Colour.HSL(fgHueSlider.getValue(), val.floatValue(), fgLightnessSlider.getValue());
                updateColor(fgColor, hsl);
            }
        });
        var fgSaturationLabel = new Label("Saturation");
        fgSaturationLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Saturation %.2f", fgSaturationSlider.getValue()), fgSaturationSlider.valueProperty())
        );

        fgLightnessSlider = slider(0, 1, 0, 0.01);
        fgLightnessSlider.valueProperty().addListener((_, _, val) -> {
            if (val != null) {
                var hsl = new Colour.HSL(fgHueSlider.getValue(), fgSaturationSlider.getValue(), val.floatValue());
                updateColor(fgColor, hsl);
            }
        });
        var fgLightnessLabel = new Label("Lightness");
        fgLightnessLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Lightness %.2f", fgLightnessSlider.getValue()), fgLightnessSlider.valueProperty())
        );

        fgAlphaSlider = slider(0, 1, 0, 0.01);
        fgAlphaSlider.valueProperty().addListener((_, _, val) -> {
            if (val != null) {
                fgColor.setOpacity(val.floatValue());
            }
        });
        var fgAlphaLabel = new Label("Alpha");
        fgAlphaLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Alpha %.2f", fgAlphaSlider.getValue()), fgAlphaSlider.valueProperty())
        );
        BooleanBinding isFgAlphaUseless = Bindings.createBooleanBinding(
            () -> !fgColor.hasFlattenEffect(bgBaseColor),
            fgColor, bgBaseColor
        );
        fgAlphaSlider.disableProperty().bind(isFgAlphaUseless);

        // ~

        var flattenBtn = new Button("Flatten");
        flattenBtn.setOnAction(_ -> {
            setBackground(bgColor.flatten(bgBaseColor));
            setForeground(fgColor.flatten(bgBaseColor));
        });

        var applyBtn = new Button("Apply");
        applyBtn.setOnAction(_ -> ThemeManager.getInstance().setNamedColors(Map.of(
            Objects.requireNonNullElse(getBgColorName(), ""), bgColor.toColor(),
            Objects.requireNonNullElse(getFgColorName(), ""), fgColor.toColor()
        )));

        var actionsBox = new HBox(20, new Spacer(), flattenBtn, applyBtn);
        actionsBox.getStyleClass().add("actions");
        actionsBox.setAlignment(Pos.CENTER_LEFT);
        actionsBox.setPadding(new Insets(10, 0, 0, 0));

        // ~

        getStyleClass().add("contrast-checker");

        // column 0
        add(new HBox(contrastRatioBox, new Spacer(), contrastLevels), 0, 0, REMAINING, 1);
        add(new Label("Background Color"), 0, 1);
        add(bgColorNameLabel, 0, 2);
        add(bgTextField, 0, 3);
        add(bgHueLabel, 0, 4);
        add(bgHueSlider, 0, 5);
        add(bgSaturationLabel, 0, 6);
        add(bgSaturationSlider, 0, 7);
        add(bgLightnessLabel, 0, 8);
        add(bgLightnessSlider, 0, 9);
        add(bgAlphaLabel, 0, 10);
        add(bgAlphaSlider, 0, 11);

        add(actionsBox, 0, 12, REMAINING, 1);

        // column 1
        add(new Label("Foreground Color"), 1, 1);
        add(fgColorNameLabel, 1, 2);
        add(fgTextField, 1, 3);
        add(fgHueLabel, 1, 4);
        add(fgHueSlider, 1, 5);
        add(fgSaturationLabel, 1, 6);
        add(fgSaturationSlider, 1, 7);
        add(fgLightnessLabel, 1, 8);
        add(fgLightnessSlider, 1, 9);
        add(fgAlphaLabel, 1, 10);
        add(fgAlphaSlider, 1, 11);

        bgColor.addListener((_, _, _) -> updateStyle());
        fgColor.addListener((_, _, _) -> updateStyle());
    }

    private void updateColor(Colour color, Colour.HSL hsl) {
        var rgb = hsl.toRGB();
        color.setRGB(rgb);
    }

    private void updateStyle() {
        setStyle(String.format("-color-contrast-checker-bg:%s;-color-contrast-checker-fg:%s;",
            bgColor.toHex(),
            getSafeFgColor().toHex()
        ));
    }

    private void setBackground(Colour color) {
        Colour.HSL hsl = color.toHSL();
        bgHueSlider.setValue(hsl.hue());
        bgSaturationSlider.setValue(hsl.saturation());
        bgLightnessSlider.setValue(hsl.lightness());
        bgAlphaSlider.setValue(color.getOpacity());
    }

    private void setForeground(Colour color) {
        Colour.HSL hsl = color.toHSL();
        fgHueSlider.setValue(hsl.hue());
        fgSaturationSlider.setValue(hsl.saturation());
        fgLightnessSlider.setValue(hsl.lightness());
        fgAlphaSlider.setValue(color.getOpacity());
    }

    private void updateContrastLevelLabel(Label label, boolean success) {
        FontIcon icon = Objects.requireNonNull((FontIcon) label.getGraphic());
        if (success) {
            label.setText(STATE_PASS);
            icon.setIconCode(Material2AL.CHECK);
        } else {
            label.setText(STATE_FAIL);
            icon.setIconCode(Material2AL.CLOSE);
        }
        label.pseudoClassStateChanged(PASSED, success);
    }

    private Label contrastLevelLabel() {
        var label = new Label(STATE_FAIL);
        label.getStyleClass().add("state");
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setGraphic(new FontIcon(Material2AL.CLOSE));
        return label;
    }

    private VBox contrastLevelBox(Label label, String description) {
        var box = new VBox(10, label, new Label(description));
        box.getStyleClass().add("contrast-level");
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Slider slider(double min, double max, double value, double increment) {
        var slider = new Slider(min, max, value);
        slider.setMinWidth(SLIDER_WIDTH);
        slider.setMajorTickUnit(increment);
        slider.setBlockIncrement(increment);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        return slider;
    }

    //*************************************************************************

    private static class RightClickMenu extends ContextMenu {

        private final Colour color;

        public RightClickMenu(Colour color) {
            super();

            this.color = color;

            createMenu();
        }

        private void createMenu() {
            var hexItem = new MenuItem("Copy as HEX");
            hexItem.setOnAction(_ -> PlatformUtils.copyToClipboard(color.toHex()));

            var rgbItem = new MenuItem("Copy as RGB");
            rgbItem.setOnAction(_ -> PlatformUtils.copyToClipboard(color.toRGB().toString()));

            var hslItem = new MenuItem("Copy as HSL");
            hslItem.setOnAction(_ -> PlatformUtils.copyToClipboard(color.toHSL().toString()));

            getItems().setAll(hexItem, rgbItem, hslItem);
        }
    }
}
