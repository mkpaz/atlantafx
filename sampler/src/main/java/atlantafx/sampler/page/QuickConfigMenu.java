/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page;

import atlantafx.base.controls.Spacer;
import atlantafx.sampler.theme.ThemeManager;
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
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static atlantafx.base.theme.Styles.*;
import static javafx.geometry.Pos.CENTER_LEFT;
import static org.kordamp.ikonli.material2.Material2AL.ARROW_BACK;
import static org.kordamp.ikonli.material2.Material2AL.ARROW_FORWARD;

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

        public MainMenu(Consumer<String> navHandler) {
            super();

            Objects.requireNonNull(navHandler);

            var themeSelectionMenu = menu("Theme", HorizontalDirection.RIGHT);
            themeSelectionMenu.setOnMouseClicked(e -> navHandler.accept(ThemeSelectionMenu.ID));

            // ~

            var zoomInBtn = new Button("", new FontIcon(Material2MZ.MINUS));
            zoomInBtn.getStyleClass().addAll(BUTTON_CIRCLE, BUTTON_ICON, FLAT);

            var zoomOutBtn = new Button("", new FontIcon(Material2MZ.PLUS));
            zoomOutBtn.getStyleClass().addAll(BUTTON_CIRCLE, BUTTON_ICON, FLAT);

            var zoomLabel = new Label("100%");

            var zoomBox = new HBox(zoomInBtn, new Spacer(), zoomLabel, new Spacer(), zoomOutBtn);
            zoomBox.setAlignment(CENTER_LEFT);
            zoomBox.getStyleClass().addAll("row");
            zoomBox.setDisable(true); // not yet implemented

            // !

            getChildren().setAll(themeSelectionMenu, new Separator(), zoomBox);
        }
    }

    private static class ThemeSelectionMenu extends VBox {

        public static final String ID = "ThemeSelectionMenu";

        private final List<Node> items = new ArrayList<>();

        public ThemeSelectionMenu(Consumer<String> navHandler) {
            super();

            Objects.requireNonNull(navHandler);
            var tm = ThemeManager.getInstance();

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
