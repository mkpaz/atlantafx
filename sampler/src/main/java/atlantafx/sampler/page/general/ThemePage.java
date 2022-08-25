/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.general;

import atlantafx.base.theme.Theme;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.theme.ThemeManager;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.time.Duration;
import java.util.Objects;

public class ThemePage extends AbstractPage {

    public static final String NAME = "Theme";

    private final ColorPalette colorPalette = new ColorPalette();

    @Override
    public String getName() { return NAME; }

    public ThemePage() {
        super();
        createView();
    }

    @Override
    protected void onRendered() {
        super.onRendered();
        colorPalette.updateColorInfo(Duration.ZERO);
    }

    private void createView() {
        userContent.getChildren().addAll(
                optionsGrid(),
                colorPalette
        );
        sourceCodeToggleBtn.setVisible(false);
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
                colorPalette.updateColorInfo(Duration.ofSeconds(1));
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
}
