/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.general;

import atlantafx.base.theme.Theme;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.theme.ThemeManager;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.Objects;

public class ThemePage extends AbstractPage {

    public static final String NAME = "Theme";

    @Override
    public String getName() { return NAME; }

    public ThemePage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().add(optionsGrid());
        sourceCodeToggleBtn.setVisible(false);
    }

    private GridPane optionsGrid() {
        ChoiceBox<Theme> themeSelector = themeSelector();
        themeSelector.setPrefWidth(200);

        Spinner<Integer> fontSizeSpinner = fontSizeSpinner();
        fontSizeSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        fontSizeSpinner.setPrefWidth(200);

        // ~

        var grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(20);

        grid.add(new Label("Color theme"), 0, 0);
        grid.add(themeSelector, 1, 0);

        grid.add(new Label("Font size"), 0, 1);
        grid.add(fontSizeSpinner, 1, 1);

        return grid;
    }

    private ChoiceBox<Theme> themeSelector() {
        var manager = ThemeManager.getInstance();
        var selector = new ChoiceBox<Theme>();
        selector.getItems().setAll(manager.getAvailableThemes());
        selector.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null && getScene() != null) {
                ThemeManager.getInstance().setTheme(getScene(), val);
            }
        });

        selector.setConverter(new StringConverter<>() {
            @Override
            public String toString(Theme theme) {
                return theme.getName();
            }

            @Override
            public Theme fromString(String themeName) {
                return manager.getAvailableThemes().stream().filter(t -> Objects.equals(themeName, t.getName()))
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

    private Spinner<Integer> fontSizeSpinner() {
        var spinner = new Spinner<Integer>(10, 24, 14);

        // Instead of this we should obtain font size from a rendered node.
        // But since it's not trivial (thanks to JavaFX doesn't expose relevant API)
        // we just keep current font size inside ThemeManager singleton.
        // It works fine if ThemeManager default font size value matches
        // default theme font size value.
        spinner.getValueFactory().setValue(ThemeManager.getInstance().getFontSize());

        spinner.valueProperty().addListener((obs, old, val) -> {
            if (val != null && getScene() != null) {
                ThemeManager.getInstance().setFontSize(getScene(), val);
            }
        });

        return spinner;
    }
}
