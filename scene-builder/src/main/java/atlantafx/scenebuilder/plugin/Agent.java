/* SPDX-License-Identifier: MIT */

package atlantafx.scenebuilder.plugin;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

// If anyone wants to fix something, here is a checklist:
// - Create an FXML containing a TabPane (if there are any third-party stylesheets leaked into
//   the scene-graph, TabPane will be a mess).
// - Start Scene Builder with the AtlantaFX theme and check for style artifacts.
// - Open the Preview window (Ctrl+P).
// - Switch between different AtlantaFX themes and check for style artifacts (w/o closing the Preview).
// - Switch to Modena and check for style artifacts.
@NullMarked
public final class Agent {

    public static final String PREVIEW_ROOT_ID = "previewRoot";

    private final ThemeGuard guard = new ThemeGuard();

    public Agent() {
        // default constructor
    }

    public void watch() {
        System.out.println("[AtlantaFX-Plugin] Initializing StyleEnforcer watcher...");

        Window.getWindows().addListener((ListChangeListener<Window>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Window window : change.getAddedSubList()) {
                        if (window instanceof Stage stage) {
                            StageController.attach(stage, guard);
                        }
                    }
                }
            }
        });

        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage) {
                StageController.attach(stage, guard);
            }
        }
    }

    // Watches for stage scene-graph changes and filters out unwanted stylesheets when
    // an AtlantaFX theme is active.
    private static final class StageController {

        private static final String STAGE_CONTROLLER_KEY = "@atlantafx.stage.controller";
        private static final String LAYOUT_LISTENER_KEY = "@atlantafx.layout";

        private final Stage stage;
        private final ThemeGuard guard;
        private final WorkspaceContent workspace = new WorkspaceContent();
        private final List<Runnable> cleanupTasks = new ArrayList<>();

        private StageController(Stage stage, ThemeGuard guard) {
            this.stage = stage;
            this.guard = guard;
        }

        public static void attach(Stage stage, ThemeGuard guard) {
            if (stage.getProperties().containsKey(STAGE_CONTROLLER_KEY)) {
                return;
            }

            var self = new StageController(stage, guard);
            stage.getProperties().put(STAGE_CONTROLLER_KEY, self);
            self.init();
        }

        private void init() {
            // cleanup on closing stage
            ChangeListener<Boolean> showingListener = (_, _, showing) -> {
                if (!showing) {
                    cleanup();
                }
            };
            stage.showingProperty().addListener(showingListener);
            cleanupTasks.add(() -> stage.showingProperty().removeListener(showingListener));

            // watch for scene changes
            ChangeListener<@Nullable Scene> sceneListener = (_, _, scene) -> {
                if (scene != null) {
                    watchScene(scene);
                }
            };
            stage.sceneProperty().addListener(sceneListener);
            cleanupTasks.add(() -> stage.sceneProperty().removeListener(sceneListener));

            if (stage.getScene() != null) {
                watchScene(stage.getScene());
            }

            // watch for Scene Builder theme changes
            EditorController editorController = SceneBuilderBridge.findEditorController(stage);
            if (editorController == null) {
                return;
            }

            ChangeListener<EditorPlatform.Theme> themeListener = (_, _, _) -> attachThemeGuard();
            editorController.themeProperty().addListener(themeListener);
            cleanupTasks.add(() -> editorController.themeProperty().removeListener(themeListener));

            attachThemeGuard();
        }

        private void watchScene(Scene scene) {
            // press F12 to print the scene graph to console for debugging
            Helpers.registerDebugger(scene, KeyCode.F12);
            UI.createPreviewMenu(scene, cleanupTasks::add);

            ChangeListener<@Nullable Parent> rootListener = (_, _, root) -> {
                if (root != null) {
                    watchSceneRoot(scene, root);
                }
            };
            scene.rootProperty().addListener(rootListener);
            cleanupTasks.add(() -> scene.rootProperty().removeListener(rootListener));

            if (scene.getRoot() != null) {
                watchSceneRoot(scene, scene.getRoot());
            }
        }

        private void watchSceneRoot(Scene scene, Parent root) {
            if (isPreviewStage()) {
                guard.protectScene(scene, cleanupTasks::add);
            } else {
                watchWorkspace(root);
                scanWorkspace(root);
            }
        }

        private void watchWorkspace(Parent root) {
            if (root.getProperties().containsKey(LAYOUT_LISTENER_KEY)) {
                return;
            }

            InvalidationListener layoutListener = _ -> Platform.runLater(() -> {
                if (stage.isShowing() && stage.getScene() != null) {
                    scanWorkspace(root);
                }
            });

            // the easiest way to detect something has been changed is by watching layout bounds
            root.layoutBoundsProperty().addListener(layoutListener);
            root.getProperties().put(LAYOUT_LISTENER_KEY, layoutListener);

            cleanupTasks.add(() -> {
                root.layoutBoundsProperty().removeListener(layoutListener);
                root.getProperties().remove(LAYOUT_LISTENER_KEY);
            });
        }

        // Searches the given root node top-down for the workspace and filters out unwanted
        // stylesheets, if necessary.
        private void scanWorkspace(Parent root) {
            workspace.traverse(root, group -> guard.protectContentGroup(stage, group, cleanupTasks::add));

            for (SubScene subScene : workspace.getSubScenes()) {
                guard.protectSubScene(stage, subScene, cleanupTasks::add);
            }

            for (Group group : workspace.getContentGroups()) {
                if (ThemeProvider.isAtlantaFXTheme(SceneBuilderBridge.findTheme(stage))) {
                    guard.purgeStylesheets(group.getStylesheets());
                }
            }
        }

        private void attachThemeGuard() {
            if (stage.getScene() == null) {
                return;
            }

            if (isPreviewStage()) {
                guard.protectScene(stage.getScene(), cleanupTasks::add);
            } else {
                for (SubScene subScene : workspace.getSubScenes()) {
                    guard.protectSubScene(stage, subScene, cleanupTasks::add);
                }
                for (Group group : workspace.getContentGroups()) {
                    if (ThemeProvider.isAtlantaFXTheme(SceneBuilderBridge.findTheme(stage))) {
                        guard.purgeStylesheets(group.getStylesheets());
                    }
                }
            }
        }

        private boolean isPreviewStage() {
            return stage.getScene() != null
                && stage.getScene().getRoot() != null
                && Agent.PREVIEW_ROOT_ID.equals(stage.getScene().getRoot().getId());
        }

        private void cleanup() {
            for (var runnable : cleanupTasks) {
                try {
                    runnable.run();
                } catch (Exception _) {
                    // ignored
                }
            }
            cleanupTasks.clear();
            workspace.clear();
            stage.getProperties().remove(STAGE_CONTROLLER_KEY);
        }
    }

    // CSS filtering and UserAgentGuard state enforcement.
    private static final class ThemeGuard {

        private static final String SCENE_LISTENER_KEY = "@atlantafx.scene.guard";
        private static final String SUBSCENE_LISTENER_KEY = "@atlantafx.subscene.guard";
        private static final Set<String> BLOCKED_KEYWORDS = Set.of(
            "modena", "gluon", "glisten", "swatch"
        );

        private final Set<ObservableList<String>> guardedLists = Collections.newSetFromMap(new IdentityHashMap<>());

        public boolean shouldBlockStylesheet(String url) {
            String lower = url.toLowerCase(Locale.ROOT);
            return BLOCKED_KEYWORDS.stream().anyMatch(lower::contains)
                || EditorPlatform.isPlatformThemeStylesheetURL(url);
        }

        public void protectScene(Scene scene, Consumer<Runnable> cleanupRegistrar) {
            EditorPlatform.Theme theme = SceneBuilderBridge.findTheme(scene);
            setUserAgentStylesheet(scene.userAgentStylesheetProperty(), theme);

            guardUserAgentStylesheet(
                scene.userAgentStylesheetProperty(),
                () -> SceneBuilderBridge.findTheme(scene),
                scene.getProperties(),
                SCENE_LISTENER_KEY,
                cleanupRegistrar
            );

            guardStylesheets(scene.getStylesheets(), () -> SceneBuilderBridge.findTheme(scene), cleanupRegistrar);
            updateStylesheets(scene.getStylesheets(), theme);
        }

        public void protectSubScene(Stage stage, SubScene subScene, Consumer<Runnable> cleanupRegistrar) {
            EditorPlatform.Theme theme = SceneBuilderBridge.findTheme(stage);
            setUserAgentStylesheet(subScene.userAgentStylesheetProperty(), theme);

            guardUserAgentStylesheet(
                subScene.userAgentStylesheetProperty(),
                () -> SceneBuilderBridge.findTheme(stage),
                subScene.getProperties(),
                SUBSCENE_LISTENER_KEY,
                cleanupRegistrar
            );
        }

        public void protectContentGroup(Stage stage, Group group, Consumer<Runnable> cleanupRegistrar) {
            guardStylesheets(group.getStylesheets(), () -> SceneBuilderBridge.findTheme(stage), cleanupRegistrar);
        }

        public void purgeStylesheets(ObservableList<String> stylesheets) {
            var toRemove = new ArrayList<String>();
            for (var url : stylesheets) {
                if (shouldBlockStylesheet(url)) {
                    toRemove.add(url);
                }
            }
            if (!toRemove.isEmpty()) {
                stylesheets.removeAll(toRemove);
            }
        }

        private void setUserAgentStylesheet(ObjectProperty<@Nullable String> userAgentStylesheetProperty,
                                            EditorPlatform.@Nullable Theme theme) {
            String uaCurrent = userAgentStylesheetProperty.get();

            if (ThemeProvider.isAtlantaFXTheme(theme) && theme.stylesheetURLs().length > 0) {
                String uaTheme = theme.stylesheetURLs()[0];
                if (!Objects.equals(uaTheme, uaCurrent)) {
                    userAgentStylesheetProperty.set(uaTheme);
                }
            } else if (uaCurrent != null && ThemeProvider.isAtlantaFXStyleSheet(uaCurrent)) {
                userAgentStylesheetProperty.set(null);
            }
        }

        private void updateStylesheets(ObservableList<String> stylesheets, EditorPlatform.@Nullable Theme theme) {
            if (ThemeProvider.isAtlantaFXTheme(theme)) {
                purgeStylesheets(stylesheets);
            } else {
                purgeOwnStylesheets(stylesheets);
            }
        }

        private void purgeOwnStylesheets(ObservableList<String> stylesheets) {
            var toRemove = new ArrayList<String>();
            for (var url : stylesheets) {
                if (ThemeProvider.isAtlantaFXStyleSheet(url)) {
                    toRemove.add(url);
                }
            }
            if (!toRemove.isEmpty()) {
                stylesheets.removeAll(toRemove);
            }
        }

        // Scene Builder uses Modena as the UA stylesheet, and themes are simply appended to the bottom of
        // node.getStylesheets(), whereas we want our stylesheet to be the UA stylesheet itself.
        // And enforce it we shall.
        private void guardUserAgentStylesheet(ObjectProperty<@Nullable String> userAgentStylesheetProperty,
                                              Supplier<EditorPlatform.@Nullable Theme> themeResolver,
                                              Map<Object, Object> nodeProperties,
                                              String listenerKey,
                                              Consumer<Runnable> cleanupRegistrar) {
            if (nodeProperties.containsKey(listenerKey)) {
                return;
            }

            ChangeListener<String> listener = new ChangeListener<>() {
                private boolean updating = false;

                @Override
                public void changed(ObservableValue<? extends String> obs, String old, String val) {
                    if (updating) {
                        return;
                    }

                    // not AtlantaFX, do nothing
                    EditorPlatform.Theme currentTheme = themeResolver.get();
                    if (!ThemeProvider.isAtlantaFXTheme(currentTheme) || currentTheme.stylesheetURLs().length == 0) {
                        return;
                    }

                    // replace UA stylesheet with AtlantaFX
                    String uaStylesheet = currentTheme.stylesheetURLs()[0];
                    if (!Objects.equals(uaStylesheet, val)) {
                        updating = true;
                        try {
                            userAgentStylesheetProperty.set(uaStylesheet);
                        } finally {
                            updating = false;
                        }
                    }
                }
            };

            userAgentStylesheetProperty.addListener(listener);
            nodeProperties.put(listenerKey, listener);

            cleanupRegistrar.accept(() -> {
                userAgentStylesheetProperty.removeListener(listener);
                nodeProperties.remove(listenerKey);
            });
        }

        // This method filters everything unwanted from the node.getStylesheets() list, preventing
        // Gluon styles from leaking into node styles while the AtlantaFX theme is active.
        private void guardStylesheets(ObservableList<String> stylesheets,
                                      Supplier<EditorPlatform.@Nullable Theme> themeResolver,
                                      Consumer<Runnable> cleanupRegistrar) {
            if (!guardedLists.add(stylesheets)) {
                return;
            }

            // we MUST clean up own stylesheets or PreviewController won't be able to detect changes
            updateStylesheets(stylesheets, themeResolver.get());

            ListChangeListener<String> listener = change -> {
                boolean touched = false;
                while (change.next()) {
                    if (change.wasAdded() || change.wasRemoved()) {
                        touched = true;
                    }
                }

                if (touched) {
                    updateStylesheets(stylesheets, themeResolver.get());
                }
            };

            stylesheets.addListener(listener);
            cleanupRegistrar.accept(() -> {
                stylesheets.removeListener(listener);
                guardedLists.remove(stylesheets);
            });
        }
    }

    // Manages discovery and registry of workspace targets (SubScene and ContentGroup).
    private static final class WorkspaceContent {

        static final String CONTENT_SUBSCENE_ID = "contentSubScene";
        static final String CONTENT_GROUP_ID = "contentGroup";

        private final List<SubScene> subScenes = new ArrayList<>();
        private final List<Group> contentGroups = new ArrayList<>();

        public void traverse(Node from, Consumer<Group> onGroupDiscover) {
            if (from instanceof SubScene subScene) {
                if (Helpers.is(CONTENT_SUBSCENE_ID, subScene) || subScene.getRoot() instanceof Group) {
                    if (!subScenes.contains(subScene)) {
                        subScenes.add(subScene);
                    }
                    if (subScene.getRoot() != null) {
                        traverse(subScene.getRoot(), onGroupDiscover);
                    }
                }
                return;
            }

            if (from instanceof Parent parent) {
                if (Helpers.is(CONTENT_GROUP_ID, parent) && parent instanceof Group group) {
                    if (!contentGroups.contains(group)) {
                        contentGroups.add(group);
                        onGroupDiscover.accept(group);
                    }
                    return; // don't go deeper
                }

                for (Node child : parent.getChildrenUnmodifiable()) {
                    traverse(child, onGroupDiscover);
                }
            }
        }

        public List<SubScene> getSubScenes() {
            return List.copyOf(subScenes);
        }

        public List<Group> getContentGroups() {
            return List.copyOf(contentGroups);
        }

        public void clear() {
            subScenes.clear();
            contentGroups.clear();
        }
    }
}