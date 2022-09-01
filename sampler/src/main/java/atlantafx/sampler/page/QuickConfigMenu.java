/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page;

import atlantafx.base.controls.Spacer;
import atlantafx.sampler.theme.ThemeManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.HorizontalDirection;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static atlantafx.base.theme.Styles.*;
import static javafx.geometry.Pos.CENTER_LEFT;
import static org.kordamp.ikonli.material2.Material2AL.ARROW_BACK;
import static org.kordamp.ikonli.material2.Material2AL.ARROW_FORWARD;

// This should really be refactored to more generic control someday.
// - the whole component to PopoverMenu, that reuses JavaFX MenuItem API
// - font size switcher to flat SpinnerMenuItem
public class QuickConfigMenu extends StackPane {

    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private static final String EXIT_ID = "Exit";

    private MainMenu mainMenu;
    private ThemeSelectionMenu themeSelectionMenu;
    private Runnable exitHandler;

    private final Consumer<String> navHandler = s -> {
        Pane pane = null;
        switch (s) {
            case MainMenu.ID -> pane = getOrCreateMainMenu();
            case ThemeSelectionMenu.ID -> {
                ThemeSelectionMenu menu = getOrCreateThemeSelectionMenu();
                menu.update();
                pane = menu;
            }
            default -> {
                if (exitHandler != null) {
                    exitHandler.run();
                    return;
                }
            }
        }
        getChildren().setAll(Objects.requireNonNull(pane));
    };

    public void setExitHandler(Runnable exitHandler) {
        this.exitHandler = exitHandler;
    }

    public QuickConfigMenu() {
        super();

        getChildren().setAll(getOrCreateMainMenu());
        setId("quick-config-menu");
    }

    private MainMenu getOrCreateMainMenu() {
        if (mainMenu == null) { mainMenu = new MainMenu(navHandler); }
        return mainMenu;
    }

    private ThemeSelectionMenu getOrCreateThemeSelectionMenu() {
        if (themeSelectionMenu == null) { themeSelectionMenu = new ThemeSelectionMenu(navHandler); }
        return themeSelectionMenu;
    }

    private static Pane menu(String title, HorizontalDirection direction) {
        var label = new Label(title);
        label.getStyleClass().add(TEXT_CAPTION);

        var root = new HBox();
        root.setAlignment(CENTER_LEFT);
        root.getStyleClass().addAll("row", "action");

        switch (direction) {
            case LEFT -> root.getChildren().setAll(new FontIcon(ARROW_BACK), new Spacer(), label, new Spacer());
            case RIGHT -> root.getChildren().setAll(label, new Spacer(), new FontIcon(ARROW_FORWARD));
        }

        return root;
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class MainMenu extends VBox {

        private static final String ID = "MainMenu";
        private static final List<Integer> FONT_SCALE = List.of(
                50, 75, 80, 90, 100, 110, 125, 150, 175, 200
        );

        private final IntegerProperty fontScale = new SimpleIntegerProperty(100);

        public MainMenu(Consumer<String> navHandler) {
            super();

            Objects.requireNonNull(navHandler);

            var themeSelectionMenu = menu("Theme", HorizontalDirection.RIGHT);
            themeSelectionMenu.setOnMouseClicked(e -> navHandler.accept(ThemeSelectionMenu.ID));

            // ~

            var zoomInBtn = new Button("", new FontIcon(Feather.ZOOM_IN));
            zoomInBtn.getStyleClass().addAll(BUTTON_CIRCLE, BUTTON_ICON, FLAT);
            zoomInBtn.setOnAction(e -> {
                int idx = FONT_SCALE.indexOf(fontScale.get());
                if (idx < FONT_SCALE.size() - 1) { fontScale.set(FONT_SCALE.get(idx + 1)); }
            });
            zoomInBtn.disableProperty().bind(Bindings.createBooleanBinding(
                    () -> FONT_SCALE.indexOf(fontScale.get()) >= FONT_SCALE.size() - 1, fontScale)
            );

            var zoomOutBtn = new Button("", new FontIcon(Feather.ZOOM_OUT));
            zoomOutBtn.getStyleClass().addAll(BUTTON_CIRCLE, BUTTON_ICON, FLAT);
            zoomOutBtn.setOnAction(e -> {
                int idx = FONT_SCALE.indexOf(fontScale.get());
                if (idx >= 1) { fontScale.set(FONT_SCALE.get(idx - 1)); }
            });
            zoomOutBtn.disableProperty().bind(Bindings.createBooleanBinding(
                    () -> FONT_SCALE.indexOf(fontScale.get()) <= 0, fontScale)
            );

            // FIXME: Default zoom value is always 100% which isn't correct because it may have been changed earlier
            var zoomLabel = new Label();
            zoomLabel.textProperty().bind(Bindings.createStringBinding(() -> fontScale.get() + "%", fontScale));

            var zoomBox = new HBox(zoomOutBtn, new Spacer(), zoomLabel, new Spacer(), zoomInBtn);
            zoomBox.setAlignment(CENTER_LEFT);
            zoomBox.getStyleClass().addAll("row");

            final var tm = ThemeManager.getInstance();
            fontScale.addListener((obs, old, val) -> {
                if (val != null) {
                    double fontSize = val.intValue() != 100 ?
                            ThemeManager.DEFAULT_FONT_SIZE / 100.0 * val.intValue() :
                            ThemeManager.DEFAULT_FONT_SIZE;
                    tm.setFontSize((int) Math.ceil(fontSize));
                    tm.reloadCustomCSS();
                }
            });

            // ~

            getChildren().setAll(themeSelectionMenu, new Separator(), zoomBox);
        }
    }

    private static class ThemeSelectionMenu extends VBox {

        public static final String ID = "ThemeSelectionMenu";

        private final List<Node> items = new ArrayList<>();

        public ThemeSelectionMenu(Consumer<String> navHandler) {
            super();

            Objects.requireNonNull(navHandler);
            final var tm = ThemeManager.getInstance();

            var mainMenu = menu("Theme", HorizontalDirection.LEFT);
            mainMenu.setOnMouseClicked(e -> navHandler.accept(MainMenu.ID));

            getChildren().setAll(mainMenu, new Separator());

            tm.getAvailableThemes().forEach(theme -> {
                var icon = new FontIcon(Material2AL.CHECK);

                var item = new HBox(20, icon, new Label(theme.getName()));
                item.getStyleClass().addAll("row", "action", "radio");
                item.setUserData(theme.getName());
                item.setOnMouseClicked(e -> {
                    tm.setTheme(theme);
                    tm.reloadCustomCSS();
                    navHandler.accept(MainMenu.ID);
                    navHandler.accept(QuickConfigMenu.EXIT_ID);
                });

                items.add(item);
                getChildren().add(item);
            });
        }

        public void update() {
            items.forEach(item -> item.pseudoClassStateChanged(
                    SELECTED,
                    Objects.equals(item.getUserData(), ThemeManager.getInstance().getTheme().getName())
            ));
        }
    }
}
