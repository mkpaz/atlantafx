/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.sampler.page.general.ColorPaletteBlock.validateColorName;
import static atlantafx.sampler.util.ContrastLevel.getColorLuminance;
import static atlantafx.sampler.util.ContrastLevel.getContrastRatioOpacityAware;
import static atlantafx.sampler.util.JColorUtils.flattenColor;
import static atlantafx.sampler.util.JColorUtils.toHexWithAlpha;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.util.ContrastLevel;
import atlantafx.sampler.util.JColor;
import atlantafx.sampler.util.JColorUtils;
import atlantafx.sampler.util.PlatformUtils;
import java.util.Map;
import java.util.Objects;
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
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

// Inspired by the https://colourcontrast.cc/
final class ContrastChecker extends GridPane {

    public static final double CONTRAST_RATIO_THRESHOLD = 1.5;
    public static final double LUMINANCE_THRESHOLD = 0.55;
    public static final PseudoClass PASSED = PseudoClass.getPseudoClass("passed");

    private static final String STATE_PASS = "PASS";
    private static final String STATE_FAIL = "FAIL";
    private static final int SLIDER_WIDTH = 300;

    private String bgColorName;
    private String fgColorName;

    private final ObservableHslaColor bgColor = new ObservableHslaColor(Color.WHITE);
    private final ObservableHslaColor fgColor = new ObservableHslaColor(Color.BLACK);
    private final ReadOnlyObjectProperty<Color> bgBaseColor;
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

    public ContrastChecker(ReadOnlyObjectProperty<Color> bgBaseColor) {
        super();

        this.bgBaseColor = bgBaseColor;
        this.contrastRatio = Bindings.createDoubleBinding(
            () -> getContrastRatioOpacityAware(
                bgColor.getColor(),
                fgColor.getColor(),
                bgBaseColor.get()
            ),
            bgColor.colorProperty(),
            fgColor.colorProperty(),
            bgBaseColor
        );

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

    public Color getBgBaseColor() {
        return bgBaseColor.get();
    }

    public String getBgColorName() {
        return bgColorName;
    }

    public String getFgColorName() {
        return fgColorName;
    }

    public Color getBgColor() {
        return bgColor.colorProperty().get();
    }

    public Color getFlatBgColor() {
        double[] flatBg = JColorUtils.flattenColor(getBgBaseColor(), getBgColor());
        return Color.color(flatBg[0], flatBg[1], flatBg[2]);
    }

    public ReadOnlyObjectProperty<Color> bgColorProperty() {
        return bgColor.colorProperty();
    }

    public ReadOnlyObjectProperty<Color> fgColorProperty() {
        return fgColor.colorProperty();
    }

    // Returns fg color that guaranteed to be visible on the current bg.
    public Color getSafeFgColor() {
        if (contrastRatio.get() <= CONTRAST_RATIO_THRESHOLD) {
            return getColorLuminance(flattenColor(bgBaseColor.get(), bgColor.getColor())) < LUMINANCE_THRESHOLD
                ? Color.WHITE : Color.BLACK;
        } else {
            return fgColor.getColor();
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

        contrastRatio.addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }
            float ratio = val.floatValue();
            updateContrastLevelLabel(aaNormalLabel, ContrastLevel.AA_NORMAL.satisfies(ratio));
            updateContrastLevelLabel(aaLargeLabel, ContrastLevel.AA_LARGE.satisfies(ratio));
            updateContrastLevelLabel(aaaNormalLabel, ContrastLevel.AAA_NORMAL.satisfies(ratio));
            updateContrastLevelLabel(aaaLargeLabel, ContrastLevel.AAA_LARGE.satisfies(ratio));
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
        bgTextField.setContextMenu(new RightClickMenu(bgColor));

        fgColorNameLabel = new Label("Foreground Color");
        fgColorNameLabel.setPadding(new Insets(-15, 0, 0, 0));
        fgColorNameLabel.getStyleClass().add(Styles.TEXT_SMALL);

        var fgTextField = new CustomTextField();
        fgTextField.setEditable(false);
        fgTextField.setLeft(new FontIcon(Feather.HASH));
        fgTextField.textProperty().bind(Bindings.createStringBinding(
            () -> fgColor.getColorHexWithAlpha().substring(1), fgColor.colorProperty()
        ));
        fgTextField.setContextMenu(new RightClickMenu(fgColor));

        bgHueSlider = slider(1, 360, 1, 1);
        bgHueSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                bgColor.setHue(val.floatValue());
            }
        });
        var bgHueLabel = new Label("Hue °");
        bgHueLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Hue %.0f °", bgHueSlider.getValue()), bgHueSlider.valueProperty())
        );

        bgSaturationSlider = slider(0, 1, 0, 0.01);
        bgSaturationSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                bgColor.setSaturation(val.floatValue());
            }
        });
        var bgSaturationLabel = new Label("Saturation");
        bgSaturationLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Saturation %.2f", bgSaturationSlider.getValue()), bgSaturationSlider.valueProperty())
        );

        bgLightnessSlider = slider(0, 1, 0, 0.01);
        bgLightnessSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                bgColor.setLightness(val.floatValue());
            }
        });
        var bgLightnessLabel = new Label("Lightness");
        bgLightnessLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Lightness %.2f", bgLightnessSlider.getValue()), bgLightnessSlider.valueProperty())
        );

        bgAlphaSlider = slider(0, 1, 0, 0.01);
        bgAlphaSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                bgColor.setAlpha(val.floatValue());
            }
        });
        var bgAlphaLabel = new Label("Alpha");
        bgAlphaLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Alpha %.2f", bgAlphaSlider.getValue()), bgAlphaSlider.valueProperty())
        );

        // ~

        fgHueSlider = slider(1, 360, 1, 1);
        fgHueSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                fgColor.setHue(val.floatValue());
            }
        });
        var fgHueLabel = new Label("Hue °");
        fgHueLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Hue %.0f °", fgHueSlider.getValue()), fgHueSlider.valueProperty())
        );

        fgSaturationSlider = slider(0, 1, 0, 0.01);
        fgSaturationSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                fgColor.setSaturation(val.floatValue());
            }
        });
        var fgSaturationLabel = new Label("Saturation");
        fgSaturationLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Saturation %.2f", fgSaturationSlider.getValue()), fgSaturationSlider.valueProperty())
        );

        fgLightnessSlider = slider(0, 1, 0, 0.01);
        fgLightnessSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                fgColor.setLightness(val.floatValue());
            }
        });
        var fgLightnessLabel = new Label("Lightness");
        fgLightnessLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Lightness %.2f", fgLightnessSlider.getValue()), fgLightnessSlider.valueProperty())
        );

        fgAlphaSlider = slider(0, 1, 0, 0.01);
        fgAlphaSlider.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                fgColor.setAlpha(val.floatValue());
            }
        });
        var fgAlphaLabel = new Label("Alpha");
        fgAlphaLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("Alpha %.2f", fgAlphaSlider.getValue()), fgAlphaSlider.valueProperty())
        );

        // ~

        var flattenBtn = new Button("Flatten");
        flattenBtn.setOnAction(e -> {
            double[] flatBg = flattenColor(bgBaseColor.get(), bgColor.getColor());
            setBackground(Color.color(flatBg[0], flatBg[1], flatBg[2]));

            double[] flatFg = flattenColor(bgBaseColor.get(), fgColor.getColor());
            setForeground(Color.color(flatFg[0], flatFg[1], flatFg[2]));
        });

        var applyBtn = new Button("Apply");
        applyBtn.setOnAction(e -> ThemeManager.getInstance().setNamedColors(Map.of(
            getBgColorName(), bgColor.getColor(),
            getFgColorName(), fgColor.getColor()
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

        bgColor.colorProperty().addListener((obs, old, val) -> {
            if (val != null) {
                updateStyle();
            }
        });

        fgColor.colorProperty().addListener((obs, old, val) -> {
            if (val != null) {
                updateStyle();
            }
        });
    }

    private void updateStyle() {
        setStyle(String.format("-color-contrast-checker-bg:%s;-color-contrast-checker-fg:%s;",
            toHexWithAlpha(bgColor.getColor()),
            toHexWithAlpha(getSafeFgColor())
        ));
    }

    private void setBackground(Color color) {
        float[] hsl = JColorUtils.toHSL(color);
        bgHueSlider.setValue(hsl[0]);
        bgSaturationSlider.setValue(hsl[1]);
        bgLightnessSlider.setValue(hsl[2]);
        bgAlphaSlider.setValue(color.getOpacity());
    }

    private void setForeground(Color color) {
        float[] hsl = JColorUtils.toHSL(color);
        fgHueSlider.setValue(hsl[0]);
        fgSaturationSlider.setValue(hsl[1]);
        fgLightnessSlider.setValue(hsl[2]);
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

    ///////////////////////////////////////////////////////////////////////////

    private static class ObservableHslaColor {

        private final ObservableList<Float> values = FXCollections.observableArrayList(0f, 0f, 0f, 0f);
        private final ReadOnlyObjectWrapper<Color> color = new ReadOnlyObjectWrapper<>() {
        };

        public ObservableHslaColor(Color initialColor) {
            values.addListener((ListChangeListener<Float>) c -> {
                float[] rgb = getRgbaArithmeticColor();
                color.set(Color.color(rgb[0], rgb[1], rgb[2], getAlpha()));
            });
            setColor(initialColor);
        }

        public Color getColor() {
            return color.get();
        }

        public void setColor(Color color) {
            float[] hsl = JColorUtils.toHSL(color);
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

        public float[] getRgbaArithmeticColor() {
            float[] hsl = new float[] { getHue(), getSaturation(), getLightness() };
            var color = JColor.color(hsl, getAlpha());
            return new float[] {
                color.getRedArithmetic(),
                color.getGreenArithmetic(),
                color.getBlueArithmetic(),
                getAlpha()
            };
        }

        public String getColorHexWithAlpha() {
            float[] hsl = new float[] { getHue(), getSaturation(), getLightness() };
            return JColor.color(hsl, getAlpha()).getColorHexWithAlpha();
        }
    }

    private static class RightClickMenu extends ContextMenu {

        private final ObservableHslaColor color;

        public RightClickMenu(ObservableHslaColor color) {
            super();

            this.color = color;

            createMenu();
        }

        private void createMenu() {
            var hexItem = new MenuItem("Copy as HEX");
            hexItem.setOnAction(e -> {
                var c = JColor.color(
                    new float[] { color.getHue(), color.getSaturation(), color.getLightness(), color.getAlpha() });
                PlatformUtils.copyToClipboard(color.getAlpha() < 1
                    ? toHexWithAlpha(color.getColor()) : c.getColorHex()
                );
            });

            var rgbItem = new MenuItem("Copy as RGB");
            rgbItem.setOnAction(e -> {
                var c = JColor.color(
                    new float[] { color.getHue(), color.getSaturation(), color.getLightness(), color.getAlpha() });
                PlatformUtils.copyToClipboard(color.getAlpha() < 1
                        ? String.format(
                        "rgba(%d,%d,%d, %.1f)", c.getGreen(), c.getGreen(), c.getBlue(), c.getAlphaArithmetic()
                    )
                        : String.format("rgb(%d,%d,%d)", c.getGreen(), c.getGreen(), c.getBlue())
                );
            });

            var hslItem = new MenuItem("Copy as HSL");
            hslItem.setOnAction(e -> {
                var c = JColor.color(new float[] {
                    color.getHue(), color.getSaturation(), color.getLightness(), color.getAlpha()
                });
                PlatformUtils.copyToClipboard(
                    color.getAlpha() < 1
                        ? String.format(
                        "hsla(%.0f,%.2f,%.2f, %.1f)",
                        c.getHue(), c.getSaturation(), c.getLightness(), c.getAlphaArithmetic()
                    )
                        : String.format(
                        "hsl(%.0f,%.2f,%.2f)",
                        c.getHue(), c.getSaturation(), c.getLightness()
                    )
                );
            });

            getItems().setAll(hexItem, rgbItem, hslItem);
        }
    }
}
