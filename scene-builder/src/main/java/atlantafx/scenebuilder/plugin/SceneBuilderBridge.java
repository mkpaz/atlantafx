/* SPDX-License-Identifier: MIT */

package atlantafx.scenebuilder.plugin;

import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

@NullMarked
public final class SceneBuilderBridge {

    private static boolean initialized = false;

    private static @Nullable Method getSingletonMethod;
    private static @Nullable Method getControllersMethod;
    private static @Nullable Method getStageMethod;
    private static @Nullable Method getSceneMethod;
    private static @Nullable Method getEditorControllerMethod;
    private static @Nullable Method getPreviewWindowControllerMethod;
    private static @Nullable Method getPreviewStageMethod;

    private SceneBuilderBridge() {
        // default constructor
    }

    private static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        try {
            Class<?> appClass = Class.forName("com.oracle.javafx.scenebuilder.app.SceneBuilderApp");
            getSingletonMethod = appClass.getMethod("getSingleton");
            getControllersMethod = appClass.getMethod("getDocumentWindowControllers");

            Class<?> dwcClass = Class.forName("com.oracle.javafx.scenebuilder.app.DocumentWindowController");
            getStageMethod = dwcClass.getMethod("getStage");
            getSceneMethod = dwcClass.getMethod("getScene");
            getEditorControllerMethod = dwcClass.getMethod("getEditorController");
            getPreviewWindowControllerMethod = dwcClass.getMethod("getPreviewWindowController");

            Class<?> pwcClass = Class.forName("com.oracle.javafx.scenebuilder.kit.preview.PreviewWindowController");
            getPreviewStageMethod = pwcClass.getMethod("getStage");
        } catch (Exception e) {
            System.err.println(
                "[AtlantaFX-Reflection] Failed to initialize SceneBuilder App reflection handles: " + e.getMessage()
            );
        }
    }

    public static @Nullable EditorController findEditorController(@Nullable Stage stage) {
        if (stage == null) {
            return null;
        }

        try {
            List<?> controllers = getDocumentWindowControllers();
            if (controllers != null && getStageMethod != null && getEditorControllerMethod != null) {
                for (var c : controllers) {
                    if (stage == getStageMethod.invoke(c)) {
                        return (EditorController) getEditorControllerMethod.invoke(c);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[AtlantaFX-Plugin] Error finding EditorController for stage: " + e.getMessage());
        }

        return findEditorControllerForPreview(stage);
    }

    public static @Nullable EditorController findEditorController(@Nullable Scene scene) {
        if (scene == null) {
            return null;
        }

        if (scene.getWindow() instanceof Stage stage) {
            EditorController controller = findEditorController(stage);
            if (controller != null) {
                return controller;
            }
        }

        try {
            List<?> controllers = getDocumentWindowControllers();
            if (controllers != null && getSceneMethod != null && getEditorControllerMethod != null) {
                for (var c : controllers) {
                    if (scene == getSceneMethod.invoke(c)) {
                        return (EditorController) getEditorControllerMethod.invoke(c);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[AtlantaFX-Plugin] Error finding EditorController for scene: " + e.getMessage());
        }
        return null;
    }

    public static @Nullable EditorController findEditorControllerForPreview(@Nullable Stage stage) {
        if (stage == null) {
            return null;
        }

        try {
            List<?> controllers = getDocumentWindowControllers();
            if (controllers != null && getEditorControllerMethod != null
                && getPreviewWindowControllerMethod != null && getPreviewStageMethod != null) {

                for (var documentController : controllers) {
                    var previewController = getPreviewWindowControllerMethod.invoke(documentController);
                    if (previewController != null && stage == getPreviewStageMethod.invoke(previewController)) {
                        // a preview is owned by a specific DocumentWindowController,
                        // so we take the EditorController from the parent document
                        return (EditorController) getEditorControllerMethod.invoke(documentController);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[AtlantaFX-Plugin] Error finding EditorController for preview: " + e.getMessage());
        }
        return null;
    }

    public static EditorPlatform.@Nullable Theme findTheme(@Nullable Stage stage) {
        if (stage == null) {
            return null;
        }

        if (stage.getScene() != null) {
            var theme = findTheme(stage.getScene());
            if (theme != null) {
                return theme;
            }
        }

        EditorController controller = findEditorController(stage);
        return controller != null ? controller.getTheme() : null;
    }

    public static EditorPlatform.@Nullable Theme findTheme(@Nullable Scene scene) {
        if (scene == null) {
            return null;
        }

        // for the preview, PreviewController has to ue used resolve a theme
        if (scene.getRoot() != null && Helpers.is(Agent.PREVIEW_ROOT_ID, scene.getRoot())) {
            if (scene.getWindow() instanceof Stage stage) {
                return findThemeForPreview(stage);
            }
        }

        // for regular windows (DocumentWindowController)
        return findThemeForScene(scene);
    }

    //*************************************************************************

    private static @Nullable List<?> getDocumentWindowControllers() throws Exception {
        init();

        if (getSingletonMethod == null || getControllersMethod == null) {
            return null;
        }

        var app = getSingletonMethod.invoke(null);
        if (app == null) {
            return null;
        }

        return (List<?>) getControllersMethod.invoke(app);
    }

    private static EditorPlatform.@Nullable Theme findThemeForScene(@Nullable Scene scene) {
        EditorController controller = findEditorController(scene);
        return controller != null ? controller.getTheme() : null;
    }

    private static EditorPlatform.@Nullable Theme findThemeForPreview(@Nullable Stage stage) {
        EditorController controller = findEditorControllerForPreview(stage);
        return controller != null ? controller.getTheme() : null;
    }
}