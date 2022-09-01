/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.general;

import atlantafx.base.theme.Theme;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.theme.ThemeEvent.EventType;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.util.NodeUtils;
import javafx.geometry.HPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.Objects;
import java.util.function.Consumer;

public class ThemePage extends AbstractPage {

    public static final String NAME = "Theme";

    private final Consumer<ColorPaletteBlock> colorBlockActionHandler = colorBlock -> {
        ContrastCheckerDialog dialog = getOrCreateContrastCheckerDialog();
        dialog.getContent().setValues(colorBlock.getFgColorName(),
                colorBlock.getFgColor(),
                colorBlock.getBgColorName(),
                colorBlock.getBgColor()
        );
        overlay.setContent(dialog, HPos.CENTER);
        overlay.toFront();
    };

    private final ColorPalette colorPalette = new ColorPalette(colorBlockActionHandler);
    private final ColorScale colorScale = new ColorScale();

    private ContrastCheckerDialog contrastCheckerDialog;

    @Override
    public String getName() { return NAME; }

    public ThemePage() {
        super();
        createView();
        ThemeManager.getInstance().addEventListener(e -> {
            if (e.eventType() == EventType.THEME_CHANGE || e.eventType() == EventType.CUSTOM_CSS_CHANGE) {
                // only works for managed nodes
                colorPalette.updateColorInfo(Duration.seconds(1));
                colorScale.updateColorInfo(Duration.seconds(1));
            }
        });
    }

    @Override
    protected void onRendered() {
        super.onRendered();
        colorPalette.updateColorInfo(Duration.ZERO);
        colorScale.updateColorInfo(Duration.ZERO);
    }

    private void createView() {
        userContent.getChildren().addAll(
                optionsGrid(),
                colorPalette,
                colorScale
        );
        // if you want to enable quick menu don't forget that
        // theme selection choice box have to be updated accordingly
        NodeUtils.toggleVisibility(quickConfigBtn, false);
        NodeUtils.toggleVisibility(sourceCodeToggleBtn, false);
    }

    private GridPane optionsGrid() {
        ChoiceBox<Theme> themeSelector = themeSelector();
        themeSelector.setPrefWidth(200);

        // ~

        var grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(20);

        grid.add(new Label("Color theme"), 0, 0);
        grid.add(themeSelector, 1, 0);

        return grid;
    }

    private ChoiceBox<Theme> themeSelector() {
        var manager = ThemeManager.getInstance();
        var selector = new ChoiceBox<Theme>();
        selector.getItems().setAll(manager.getAvailableThemes());
        selector.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null && getScene() != null) {
                var tm = ThemeManager.getInstance();
                tm.setTheme(val);
                tm.reloadCustomCSS();
            }
        });

        selector.setConverter(new StringConverter<>() {
            @Override
            public String toString(Theme theme) {
                return theme.getName();
            }

            @Override
            public Theme fromString(String themeName) {
                return manager.getAvailableThemes().stream()
                        .filter(t -> Objects.equals(themeName, t.getName()))
                        .findFirst()
                        .orElse(null);
            }
        });

        // select current theme
        if (manager.getTheme() != null) {
            selector.getItems().stream()
                    .filter(t -> Objects.equals(manager.getTheme().getName(), t.getName()))
                    .findFirst()
                    .ifPresent(t -> selector.getSelectionModel().select(t));
        }

        return selector;
    }

    private ContrastCheckerDialog getOrCreateContrastCheckerDialog() {
        if (contrastCheckerDialog == null) {
            contrastCheckerDialog = new ContrastCheckerDialog(colorPalette.bgBaseColorProperty());
        }

        contrastCheckerDialog.setOnCloseRequest(() -> {
            overlay.removeContent();
            overlay.toBack();
        });

        return contrastCheckerDialog;
    }
}
