package atlantafx.sampler.page.general;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.util.JColor;
import atlantafx.sampler.util.JColorUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.Objects;

import static atlantafx.sampler.page.general.ColorBlock.validateColorName;
import static atlantafx.sampler.util.JColorUtils.*;

// Inspired by the https://colourcontrast.cc/
public class ColorContrastChecker extends GridPane {

    static final PseudoClass PASSED = PseudoClass.getPseudoClass("passed");
    static final float[] COLOR_WHITE = new float[] { 255f, 255f, 255f, 1f };
    static final float[] COLOR_BLACK = new float[] { 0f, 0f, 0f, 1f };
    static final double LUMINANCE_THRESHOLD = 0.55;

    private static final int SLIDER_WIDTH = 300;

    private String bgColorName;
    private String fgColorName;

    private final ObservableHSLAColor bgColor = new ObservableHSLAColor(Color.WHITE);
    private final ObservableHSLAColor fgColor = new ObservableHSLAColor(Color.BLACK);
    private final DoubleBinding contrastRatio = Bindings.createDoubleBinding(
            () -> 1 / getContrastRatioOpacityAware(bgColor.getColor(), fgColor.getColor()),
            bgColor.colorProperty(),
            fgColor.colorProperty()
    );

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

    public ColorContrastChecker() {
        super();
        createView();
    }

    public void setValues(String fgColorName, Color fgColor,
                          String bgColorName, Color bgColor) {
        this.bgColorName = validateColorName(bgColorName);
        bgColorNameLabel.setText(bgColorName);
        setBackground(bgColor);

        this.fgColorName = validateColorName(fgColorName);
        fgColorNameLabel.setText(fgColorName);
        setForeground(fgColor);
    }

    public String getBgColorName() { return bgColorName; }

    public String getFgColorName() { return fgColorName; }

    public Color getBgColor() { return bgColor.colorProperty().get(); }

    public Color getFgColor() { return fgColor.colorProperty().get(); }

    private void createView() {
        var textLabel = new Label("Aa");
        textLabel.getStyleClass().add("text");

        var ratioLabel = new Label("0.0");
        ratioLabel.getStyleClass().add("ratio");
        ratioLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("%.2f", contrastRatio.get()), contrastRatio
        ));

        var fontBox = new HBox(20, textLabel, ratioLabel);
        fontBox.getStyleClass().add("font-box");
        fontBox.setAlignment(Pos.BASELINE_LEFT);

        // !

        var aaNormalLabel = wsagLabel();
        var aaNormalBox = wsagBox(aaNormalLabel, "AA Normal");

        var aaLargeLabel = wsagLabel();
        var aaLargeBox = wsagBox(aaLargeLabel, "AA Large");

        var aaaNormalLabel = wsagLabel();
        var aaaNormalBox = wsagBox(aaaNormalLabel, "AAA Normal");

        var aaaLargeLabel = wsagLabel();
        var aaaLargeBox = wsagBox(aaaLargeLabel, "AAA Large");

        var wsagBox = new HBox(20, aaNormalBox, aaLargeBox, aaaNormalBox, aaaLargeBox);
        wsagBox.getStyleClass().add("wsag-box");

        contrastRatio.addListener((obs, old, val) -> {
            if (val == null) { return; }
            float ratio = val.floatValue();
            if (ratio >= 7) {
                updateWsagLabel(aaNormalLabel, true);
                updateWsagLabel(aaLargeLabel, true);
                updateWsagLabel(aaaNormalLabel, true);
                updateWsagLabel(aaaLargeLabel, true);
            } else if (ratio < 7 && ratio >= 4.5) {
                updateWsagLabel(aaNormalLabel, true);
                updateWsagLabel(aaLargeLabel, true);
                updateWsagLabel(aaaNormalLabel, false);
                updateWsagLabel(aaaLargeLabel, true);
            } else if (ratio < 4.5 && ratio >= 3) {
                updateWsagLabel(aaNormalLabel, false);
                updateWsagLabel(aaLargeLabel, true);
                updateWsagLabel(aaaNormalLabel, false);
                updateWsagLabel(aaaLargeLabel, false);
            } else {
                updateWsagLabel(aaNormalLabel, false);
                updateWsagLabel(aaLargeLabel, false);
                updateWsagLabel(aaaNormalLabel, false);
                updateWsagLabel(aaaLargeLabel, false);
            }
        });

        // ~

        bgColorNameLabel = new Label("Background Color");
        bgColorNameLabel.setPadding(new Insets(-15, 0, 0, 0));
        bgColorNameLabel.getStyleClass().add(Styles.TEXT_SMALL);

        var bgTextField = new CustomTextField();
        bgTextField.setEditable(false);
        bgTextField.setLeft(new FontIcon(Feather.HASH));
        bgTextField.textProperty().bind(Bindings.createStringBinding(
                () -> bgColor.getColorHexWithAlpha().substring(1), bgColor.colorProperty()
        ));

        fgColorNameLabel = new Label("Foreground Color");
        fgColorNameLabel.setPadding(new Insets(-15, 0, 0, 0));
        fgColorNameLabel.getStyleClass().add(Styles.TEXT_SMALL);

        var fgTextField = new CustomTextField();
        fgTextField.setEditable(false);
        fgTextField.setLeft(new FontIcon(Feather.HASH));
        fgTextField.textProperty().bind(Bindings.createStringBinding(
                () -> fgColor.getColorHexWithAlpha().substring(1), fgColor.colorProperty()
        ));

        bgHueSlider = slider(1, 360, 1, 1);
        bgHueSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) { bgColor.setHue(val.floatValue()); }
        });
        var bgHueLabel = new Label("Hue °");
        bgHueLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Hue %.0f °", bgHueSlider.getValue()), bgHueSlider.valueProperty())
        );

        bgSaturationSlider = slider(0, 1, 0, 0.01);
        bgSaturationSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) { bgColor.setSaturation(val.floatValue()); }
        });
        var bgSaturationLabel = new Label("Saturation");
        bgSaturationLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Saturation %.2f", bgSaturationSlider.getValue()), bgSaturationSlider.valueProperty())
        );

        bgLightnessSlider = slider(0, 1, 0, 0.01);
        bgLightnessSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) { bgColor.setLightness(val.floatValue()); }
        });
        var bgLightnessLabel = new Label("Lightness");
        bgLightnessLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Lightness %.2f", bgLightnessSlider.getValue()), bgLightnessSlider.valueProperty())
        );

        bgAlphaSlider = slider(0, 1, 0, 0.01);
        bgAlphaSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) { bgColor.setAlpha(val.floatValue()); }
        });
        var bgAlphaLabel = new Label("Alpha");
        bgAlphaLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Alpha %.2f", bgAlphaSlider.getValue()), bgAlphaSlider.valueProperty())
        );

        // ~

        fgHueSlider = slider(1, 360, 1, 1);
        fgHueSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) { fgColor.setHue(val.floatValue()); }
        });
        var fgHueLabel = new Label("Hue °");
        fgHueLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Hue %.0f °", fgHueSlider.getValue()), fgHueSlider.valueProperty())
        );

        fgSaturationSlider = slider(0, 1, 0, 0.01);
        fgSaturationSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) { fgColor.setSaturation(val.floatValue()); }
        });
        var fgSaturationLabel = new Label("Saturation");
        fgSaturationLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Saturation %.2f", fgSaturationSlider.getValue()), fgSaturationSlider.valueProperty())
        );

        fgLightnessSlider = slider(0, 1, 0, 0.01);
        fgLightnessSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) { fgColor.setLightness(val.floatValue()); }
        });
        var fgLightnessLabel = new Label("Lightness");
        fgLightnessLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Lightness %.2f", fgLightnessSlider.getValue()), fgLightnessSlider.valueProperty())
        );

        fgAlphaSlider = slider(0, 1, 0, 0.01);
        fgAlphaSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) { fgColor.setAlpha(val.floatValue()); }
        });
        var fgAlphaLabel = new Label("Alpha");
        fgAlphaLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Alpha %.2f", fgAlphaSlider.getValue()), fgAlphaSlider.valueProperty())
        );

        // ~

        getStyleClass().add("contrast-checker");

        // column 0
        add(fontBox, 0, 0);
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

        // column 1
        add(wsagBox, 1, 0);
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

        bgColor.colorProperty().addListener((obs, old, val) -> {
            if (val != null) { updateStyle(); }
        });

        fgColor.colorProperty().addListener((obs, old, val) -> {
            if (val != null) { updateStyle(); }
        });
    }

    private void updateStyle() {
        float[] bg = bgColor.getRGBAColor();
        float[] fg = fgColor.getRGBAColor();

        // use fallback color if contrast ratio is too low
        if (contrastRatio.get() <= LUMINANCE_THRESHOLD) {
            fg = getColorLuminance(flattenColor(Color.WHITE, bgColor.getColor())) < 0.55 ? COLOR_WHITE : COLOR_BLACK;
        }

        setStyle(String.format("-color-contrast-checker-bg:rgba(%.0f,%.0f,%.0f,%.2f);-color-contrast-checker-fg:rgba(%.0f,%.0f,%.0f,%.2f);",
                               bg[0], bg[1], bg[2], bg[3],
                               fg[0], fg[1], fg[2], fg[3]
        ));
    }

    private void setBackground(Color color) {
        float[] hsl = JColorUtils.toHSL(
                (float) color.getRed(),
                (float) color.getGreen(),
                (float) color.getBlue()
        );
        bgHueSlider.setValue(hsl[0]);
        bgSaturationSlider.setValue(hsl[1]);
        bgLightnessSlider.setValue(hsl[2]);
        bgAlphaSlider.setValue(color.getOpacity());
    }

    private void setForeground(Color color) {
        float[] hsl = JColorUtils.toHSL(
                (float) color.getRed(),
                (float) color.getGreen(),
                (float) color.getBlue()
        );
        fgHueSlider.setValue(hsl[0]);
        fgSaturationSlider.setValue(hsl[1]);
        fgLightnessSlider.setValue(hsl[2]);
        fgAlphaSlider.setValue(color.getOpacity());
    }

    private void updateWsagLabel(Label label, boolean success) {
        FontIcon icon = Objects.requireNonNull((FontIcon) label.getGraphic());
        if (success) {
            label.setText("PASS");
            icon.setIconCode(Material2AL.CHECK);
        } else {
            label.setText("FAIL");
            icon.setIconCode(Material2AL.CLOSE);
        }
        label.pseudoClassStateChanged(PASSED, success);
    }

    private Label wsagLabel() {
        var label = new Label("FAIL");
        label.getStyleClass().add("wsag-label");
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setGraphic(new FontIcon(Material2AL.CLOSE));
        return label;
    }

    private VBox wsagBox(Label label, String description) {
        var box = new VBox(10, label, new Label(description));
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

    static double getContrastRatioOpacityAware(Color bgColor, Color fgColor) {
        double luminance1 = getColorLuminance(flattenColor(Color.WHITE, bgColor));
        double luminance2 = getColorLuminance(flattenColor(Color.WHITE, fgColor));
        return getContrastRatio(luminance1, luminance2);
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class ObservableHSLAColor {

        private final ObservableList<Float> values = FXCollections.observableArrayList(0f, 0f, 0f, 0f);
        private final ReadOnlyObjectWrapper<Color> color = new ReadOnlyObjectWrapper<>() { };

        public ObservableHSLAColor(Color initialColor) {
            color.set(initialColor);
            values.addListener((ListChangeListener<Float>) c -> {
                float[] rgb = getRGBAArithmeticColor();
                color.set(Color.color(rgb[0], rgb[1], rgb[2], getAlpha()));
            });
        }

        public Color getColor() {
            return color.get();
        }

        public void setColor(Color color) {
            float[] hsl = JColorUtils.toHSL(
                    (float) color.getRed(),
                    (float) color.getGreen(),
                    (float) color.getBlue()
            );
            values.setAll(hsl[0], hsl[1], hsl[2], (float) color.getOpacity());
        }

        public ReadOnlyObjectProperty<Color> colorProperty() {
            return color.getReadOnlyProperty();
        }

        public float getHue() {
            return values.get(0);
        }

        public void setHue(float value) {
            values.set(0, value);
        }

        public float getSaturation() {
            return values.get(1);
        }

        public void setSaturation(float value) {
            values.set(1, value);
        }

        public float getLightness() {
            return values.get(2);
        }

        public void setLightness(float value) {
            values.set(2, value);
        }

        public float getAlpha() {
            return values.get(3);
        }

        public void setAlpha(float value) {
            values.set(3, value);
        }

        public float[] getRGBAArithmeticColor() {
            float[] hsl = new float[] { getHue(), getSaturation(), getLightness() };
            var color = JColor.color(hsl, getAlpha());
            return new float[] {
                    color.getRedArithmetic(),
                    color.getGreenArithmetic(),
                    color.getBlueArithmetic(),
                    getAlpha()
            };
        }

        public float[] getRGBAColor() {
            float[] hsl = new float[] { getHue(), getSaturation(), getLightness() };
            var color = JColor.color(hsl, getAlpha());
            return new float[] {
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    getAlpha()
            };
        }

        public String getColorHexWithAlpha() {
            float[] hsl = new float[] { getHue(), getSaturation(), getLightness() };
            return JColor.color(hsl, getAlpha()).getColorHexWithAlpha();
        }
    }
}
