/* SPDX-License-Identifier: MIT */

package atlantafx.scenebuilder.plugin;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public final class Helpers {

    private static final StackWalker WALKER = StackWalker.getInstance();

    public Helpers() {
        // utility
    }

    public static boolean is(@Nullable String id, @Nullable Node node) {
        return id != null && node != null && id.equals(node.getId());
    }

    public static List<String> getCallChain(int max, int skip) {
        return WALKER.walk(frames ->
            frames.skip(skip)
                .limit(max)
                .map(frame -> frame.getClassName() + "." + frame.getMethodName() + ":" + frame.getLineNumber())
                .collect(Collectors.toList())
        );
    }

    //*************************************************************************

    public static void registerDebugger(Scene scene, KeyCode keyCode) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == keyCode) {
                System.out.println("\n=================== START DIAGNOSTIC DUMP ===================");
                dumpSceneStyles(scene);
                System.out.println("==================== END DIAGNOSTIC DUMP ====================\n");
            }
        });
    }

    private static void dumpSceneStyles(Scene scene) {
        Window window = scene.getWindow();
        System.out.println(">>> [WINDOW]");

        if (window != null) {
            System.out.println("    Window Class: " + window.getClass().getName());
            System.out.println("    Window Superclass: " + window.getClass().getSuperclass().getName());
            System.out.println("    Showing: " + window.isShowing());

            if (window instanceof Stage stage) {
                var owner = stage.getOwner() != null
                    ? stage.getOwner().getClass().getName() + " (" + stage.getOwner() + ")"
                    : "null";

                System.out.println("    Stage Title: " + stage.getTitle());
                System.out.println("    Stage Style: " + stage.getStyle());
                System.out.println("    Stage Owner: " + owner);
                System.out.println("    Stage UserData: " + stage.getUserData());
                System.out.println("    Stage Properties: " + stage.getProperties());
            }
        }

        System.out.println("\n>>> [SCENE]");
        System.out.println("    Scene Class: " + scene.getClass().getName());
        System.out.println("    Scene UA Stylesheet: " + scene.getUserAgentStylesheet());
        System.out.println("    Scene Properties: " + scene.getProperties());
        System.out.println("    Scene Stylesheets Count: " + scene.getStylesheets().size());
        for (int i = 0; i < scene.getStylesheets().size(); i++) {
            System.out.println("      [" + i + "] " + scene.getStylesheets().get(i));
        }

        Parent root = scene.getRoot();
        if (root != null) {
            System.out.println("\n>>> [SCENE GRAPH]");
            traverseSceneGraph(root, 0);
        } else {
            System.out.println("\n>>> [GRAPH GRAPH]: Root is null");
        }
    }

    private static void traverseSceneGraph(Node node, int depth) {
        String indent = "  ".repeat(depth + 1);

        var sb = new StringBuilder();
        sb.append(indent).append("[").append(node.getClass().getSimpleName()).append("]");
        sb.append(" Class=").append(node.getClass().getName());
        if (node.getId() != null && !node.getId().isEmpty()) {
            sb.append(" id='#").append(node.getId()).append("'");
        }

        if (!node.getStyleClass().isEmpty()) {
            sb.append(" class='").append(String.join(".", node.getStyleClass())).append("'");
        }

        System.out.println(sb);

        if (!node.getProperties().isEmpty()) {
            System.out.println(indent + "  └─ Properties: " + node.getProperties());
        }

        if (node instanceof SubScene subScene) {
            System.out.println(indent + "  └─ [SUBSCENE UA]: " + subScene.getUserAgentStylesheet());
            if (subScene.getRoot() != null) {
                traverseSceneGraph(subScene.getRoot(), depth + 1);
            }
            return;
        }

        if (node instanceof Parent parent) {
            if (!parent.getStylesheets().isEmpty()) {
                System.out.println(indent + "  └─ [PARENT STYLESHEETS]: " + parent.getStylesheets());
            }

            for (Node child : parent.getChildrenUnmodifiable()) {
                traverseSceneGraph(child, depth + 1);
            }
        }
    }
}
