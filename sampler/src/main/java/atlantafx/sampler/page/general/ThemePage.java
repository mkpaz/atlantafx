/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.general;

import atlantafx.base.theme.Theme;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.ThemeEvent;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.util.NodeUtils;
import javafx.geometry.HPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;

import static atlantafx.sampler.event.ThemeEvent.EventType.COLOR_CHANGE;
import static atlantafx.sampler.event.ThemeEvent.EventType.THEME_CHANGE;
import static atlantafx.sampler.util.Controls.hyperlink;

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
        DefaultEventBus.getInstance().subscribe(ThemeEvent.class, e -> {
            if (e.getEventType() == THEME_CHANGE || e.getEventType() == COLOR_CHANGE) {
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
        var noteText = new TextFlow(
                new Text("AtlantaFX follows "),
                hyperlink("Github Primer interface guidelines",
                        URI.create("https://primer.style/design/foundations/color")
                ),
                new Text(" and color system.")
        );

        userContent.getChildren().addAll(
                optionsGrid(),
                noteText,
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

        var accentSelector = new AccentColorSelector();

        // ~

        var grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(20);

        grid.add(new Label("Color theme"), 0, 0);
        grid.add(themeSelector, 1, 0);
        grid.add(new Label("Accent color"), 0, 1);
        grid.add(accentSelector, 1, 1);

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
