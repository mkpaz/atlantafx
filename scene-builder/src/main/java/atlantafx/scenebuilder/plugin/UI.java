/* SPDX-License-Identifier: MIT */

package atlantafx.scenebuilder.plugin;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@NullMarked
public final class UI {

    private static final String CUSTOM_MENU_KEY = "@atlantafx.menu";
    private static final String STYLE_CLASS_MARKER = "atlantafx-scene-builder";

    private static final String DOCUMENT_WINDOW_ID = "DocumentWindow";
    private static final String MENUBAR_SELECTOR = "#menuBar";
    private static final String PREVIEW_MENU_ID = "previewMenu";
    private static final String THEME_MENU_ID = "themeMenu";

    private UI() {
        // utility
    }

    public static void createPreviewMenu(@Nullable Scene scene, Consumer<Runnable> cleanupRegistrar) {
        if (scene == null || scene.getRoot() == null) {
            return;
        }

        Parent root = scene.getRoot();
        if (!Helpers.is(DOCUMENT_WINDOW_ID, root)) {
            return;
        }

        Menu themeMenu = findThemeMenu(root);
        if (themeMenu == null || themeMenu.getProperties().containsKey(CUSTOM_MENU_KEY)) {
            return;
        } else {
            System.err.printf("Theme menu not found for root: %s%n", root.getId());
        }

        themeMenu.getProperties().put(CUSTOM_MENU_KEY, "");

        var toggleGroup = themeMenu.getItems().stream()
            .filter(item -> item instanceof RadioMenuItem r && r.getToggleGroup() != null)
            .map(item -> ((RadioMenuItem) item).getToggleGroup())
            .findFirst()
            .orElse(null);

        themeMenu.getItems().addFirst(new ExtraThemesMenu(scene, toggleGroup));
        themeMenu.getItems().add(1, createSeparator());

        cleanupRegistrar.accept(() -> {
            List<MenuItem> items = findCustomMenuItems(themeMenu);
            for (var item : items) {
                if (item instanceof ExtraThemesMenu menu) {
                    menu.dispose();
                }
            }
            themeMenu.getItems().removeAll(items);
            themeMenu.getProperties().remove(CUSTOM_MENU_KEY);
        });
    }

    //*************************************************************************

    private static @Nullable Menu findThemeMenu(Parent root) {
        if (!(root.lookup(MENUBAR_SELECTOR) instanceof MenuBar menuBar)) {
            return null;
        }

        for (Menu previewMenu : menuBar.getMenus()) {
            if (!PREVIEW_MENU_ID.equals(previewMenu.getId())) {
                continue;
            }

            for (MenuItem item : previewMenu.getItems()) {
                if (THEME_MENU_ID.equals(item.getId()) && item instanceof Menu menu) {
                    return menu;
                }
            }
        }

        return null;
    }

    private static List<MenuItem> findCustomMenuItems(Menu menu) {
        return menu.getItems().stream()
            .filter(item -> item.getStyleClass().contains(STYLE_CLASS_MARKER))
            .toList();
    }

    // This menu isn't synchronized with Preferences selected theme.
    public static class ExtraThemesMenu extends Menu {

        public ExtraThemesMenu(Scene scene, @Nullable ToggleGroup toggleGroup) {
            setText("AtlantaFX");
            getStyleClass().add(STYLE_CLASS_MARKER);

            if (toggleGroup == null) {
                toggleGroup = new ToggleGroup();
            }

            var items = new ArrayList<MenuItem>(ThemeProvider.getThemeList().size());
            for (var theme : ThemeProvider.getThemeList()) {
                var item = new RadioMenuItem(theme.getName());
                item.setToggleGroup(toggleGroup);
                item.setOnAction(_ -> {
                    var controller = SceneBuilderBridge.findEditorController(scene);
                    if (controller != null) {
                        controller.setTheme(ThemeProvider.createTheme(theme));
                    }
                });
                items.add(item);
            }

            getItems().setAll(items);
        }

        public void dispose() {
            for (var item : getItems()) {
                if (item instanceof RadioMenuItem radio) {
                    radio.setToggleGroup(null);
                }
                item.setOnAction(null);
            }
            getItems().clear();
        }
    }

    private static MenuItem createSeparator() {
        var item = new SeparatorMenuItem();
        item.getStyleClass().add(STYLE_CLASS_MARKER);
        return item;
    }
}
