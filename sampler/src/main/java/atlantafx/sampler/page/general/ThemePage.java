/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.sampler.event.ThemeEvent.EventType;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.Resources;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.ThemeEvent;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.theme.SamplerTheme;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.util.Lazy;
import java.util.Objects;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

@SuppressWarnings("UnnecessaryLambda")
public class ThemePage extends OutlinePage {

    public static final String NAME = "Theme";

    private static final ThemeManager TM = ThemeManager.getInstance();
    private static final String DEFAULT_FONT_ID = "Default";
    private static final Image SCENE_BUILDER_ICON = new Image(
        Resources.getResourceAsStream("images/scene-builder_32.png")
    );

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return false;
    }

    @Override
    public boolean canChangeThemeSettings() {
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////

    private final Lazy<ThemeRepoManagerDialog> themeRepoManagerDialog;
    private final Lazy<ContrastCheckerDialog> contrastCheckerDialog;
    private final Lazy<SceneBuilderDialog> sceneBuilderDialog;
    private final ColorPalette colorPalette;
    private final ColorScale colorScale = new ColorScale();
    private final ChoiceBox<SamplerTheme> themeSelector = createThemeSelector();
    private final ComboBox<String> fontFamilyChooser = createFontFamilyChooser();
    private final Spinner<Integer> fontSizeSpinner = createFontSizeSpinner();

    public ThemePage() {
        super();

        colorPalette = new ColorPalette(colorBlock -> {
            ContrastCheckerDialog dialog = getContrastCheckerDialog();
            dialog.getContent().setValues(
                colorBlock.getFgColorName(),
                colorBlock.getFgColor(),
                colorBlock.getBgColorName(),
                colorBlock.getBgColor()
            );
            overlay.setContent(dialog, HPos.CENTER);
            overlay.toFront();
        });

        themeRepoManagerDialog = new Lazy<>(() -> {
            var dialog = new ThemeRepoManagerDialog();
            dialog.setOnCloseRequest(() -> {
                overlay.removeContent();
                overlay.toBack();
            });

            return dialog;
        });

        contrastCheckerDialog = new Lazy<>(() -> {
            var dialog = new ContrastCheckerDialog(getBgBaseColorProperty());
            dialog.setOnCloseRequest(() -> {
                overlay.removeContent();
                overlay.toBack();
            });

            return dialog;
        });

        sceneBuilderDialog = new Lazy<>(() -> {
            var dialog = new SceneBuilderDialog();
            dialog.setOnCloseRequest(() -> {
                overlay.removeContent();
                overlay.toBack();
                dialog.reset();
            });

            return dialog;
        });

        DefaultEventBus.getInstance().subscribe(ThemeEvent.class, e -> {
            var eventType = e.getEventType();
            if (eventType == EventType.THEME_ADD || eventType == EventType.THEME_REMOVE) {
                themeSelector.getItems().setAll(TM.getRepository().getAll());
                selectCurrentTheme();
            }
            if (eventType == EventType.THEME_CHANGE || eventType == EventType.COLOR_CHANGE) {
                colorPalette.updateColorInfo(Duration.seconds(1));
                colorScale.updateColorInfo(Duration.seconds(1));
            }
        });

        addSection("Theme", createThemeManagementSection());
        addSection("Scene Builder", createSceneBuilderSection());
        addSection("Color Palette", createColorPaletteSection());
        addSection("Color Scale", createColorScaleSection());

        selectCurrentTheme();
    }

    @Override
    protected void onRendered() {
        super.onRendered();
        colorPalette.updateColorInfo(Duration.ZERO);
        colorScale.updateColorInfo(Duration.ZERO);
    }

    private Node createThemeManagementSection() {
        var themeRepoBtn = new Button("", new FontIcon(Material2OutlinedMZ.SETTINGS));
        themeRepoBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        themeRepoBtn.setTooltip(new Tooltip("Settings"));
        themeRepoBtn.setOnAction(e -> {
            ThemeRepoManagerDialog dialog = themeRepoManagerDialog.get();
            overlay.setContent(dialog, HPos.CENTER);
            dialog.getContent().update();
            overlay.toFront();
        });

        var accentSelector = new AccentColorSelector();

        // ~

        var grid = new GridPane();
        grid.setHgap(HGAP_20);
        grid.setVgap(VGAP_10);
        grid.addRow(0, new Label("Color theme"), themeSelector, themeRepoBtn);
        grid.addRow(1, new Label("Accent color"), accentSelector);
        grid.addRow(2, new Label("Font"), new HBox(10, fontFamilyChooser, fontSizeSpinner));

        return grid;
    }

    private Node createSceneBuilderSection() {
        var sceneBuilderBtn = new Button("SceneBuilder Integration");
        sceneBuilderBtn.setGraphic(new ImageView(SCENE_BUILDER_ICON));
        sceneBuilderBtn.setOnAction(e -> {
            SceneBuilderDialog dialog = sceneBuilderDialog.get();
            overlay.setContent(dialog, HPos.CENTER);
            overlay.toFront();
        });

        var description = BBCodeParser.createFormattedText("""
            While SceneBuilder does not support adding custom themes, it is \
            possible to overwrite looked-up CSS paths to make the existing \
            SceneBuilder menu options load custom CSS files."""
        );

        return new VBox(VGAP_20, description, sceneBuilderBtn);
    }

    private Node createColorPaletteSection() {
        var description = createFormattedText("""
            AtlantaFX follows [url=https://primer.style/design/foundations/color]GitHub \
            Primer interface guidelines[/url] and color system.
                        
            Color contrast between text and its background must meet \
            [url=https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html]required WCAG standards[/url]:
                        
            [ul]
            [li]4.5:1 for normal text[/li]
            [li]3:1 for large text (>24px)[/li]
            [li]3:1 for UI elements and graphics[/li]
            [li]no contrast requirement for decorative and disabled elements[/li][/ul]
                        
            Click on any color block to observe and modify color combination via built-in contrast checker.
            """, true
        );

        return new VBox(VGAP_10, description, colorPalette);
    }

    private Node createColorScaleSection() {
        var description = createFormattedText("""
            Avoid referencing scale variables directly when building UI that needs \
            to adapt to different color themes. Instead, use the functional variables \
            listed above.""", false
        );

        return new VBox(VGAP_10, description, colorScale);
    }

    private ChoiceBox<SamplerTheme> createThemeSelector() {
        var choiceBox = new ChoiceBox<SamplerTheme>();
        choiceBox.getItems().setAll(TM.getRepository().getAll());
        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null && getScene() != null) {
                TM.setTheme(val);
            }
        });
        choiceBox.setPrefWidth(310);

        choiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SamplerTheme theme) {
                return theme != null ? theme.getName() : "";
            }

            @Override
            public SamplerTheme fromString(String themeName) {
                return TM.getRepository().getAll().stream()
                    .filter(t -> Objects.equals(themeName, t.getName()))
                    .findFirst()
                    .orElse(null);
            }
        });

        return choiceBox;
    }

    private ComboBox<String> createFontFamilyChooser() {
        var comboBox = new ComboBox<String>();
        comboBox.setPrefWidth(200);

        // keyword to reset font family to its default value
        comboBox.getItems().add(DEFAULT_FONT_ID);
        comboBox.getItems().addAll(FXCollections.observableArrayList(Font.getFamilies()));

        // select active font family value on page load
        comboBox.getSelectionModel().select(TM.getFontFamily());
        comboBox.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                TM.setFontFamily(DEFAULT_FONT_ID.equals(val) ? ThemeManager.DEFAULT_FONT_FAMILY_NAME : val);
            }
        });

        return comboBox;
    }

    private Spinner<Integer> createFontSizeSpinner() {
        var spinner = new Spinner<Integer>(
            ThemeManager.SUPPORTED_FONT_SIZE.get(0),
            ThemeManager.SUPPORTED_FONT_SIZE.get(ThemeManager.SUPPORTED_FONT_SIZE.size() - 1),
            TM.getFontSize()
        );
        spinner.setPrefWidth(100);

        // Instead of this we should obtain font size from a rendered node.
        // But since it's not trivial (thanks to JavaFX doesn't expose relevant API)
        // we just keep current font size inside ThemeManager singleton.
        // It works fine if ThemeManager default font size value matches
        // default theme font size value.
        spinner.getValueFactory().setValue(TM.getFontSize());

        spinner.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                TM.setFontSize(val);
            }
        });

        return spinner;
    }

    private void selectCurrentTheme() {
        if (TM.getTheme() != null) {
            themeSelector.getItems().stream()
                .filter(t -> Objects.equals(TM.getTheme().getName(), t.getName()))
                .findFirst()
                .ifPresent(t -> themeSelector.getSelectionModel().select(t));
        }
    }

    private ContrastCheckerDialog getContrastCheckerDialog() {
        return contrastCheckerDialog.get();
    }

    private ReadOnlyObjectProperty<Color> getBgBaseColorProperty() {
        return colorPalette.bgBaseColorProperty();
    }
}
