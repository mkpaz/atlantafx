/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.animation.Animation;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

/**
 * The default skin for the {@link BottomSheet} control.
 */
public class BottomSheetSkin extends SkinBase<BottomSheet> {

    protected @Nullable BottomSheet control;

    protected final StackPane root;
    protected final VBox sheetContainer;
    protected final StackPane dragHandleArea;
    protected final Region dragHandle;
    protected final StackPane headerArea;
    protected final StackPane contentArea;

    protected final EventHandler<KeyEvent> keyHandler = createKeyHandler();
    protected final EventHandler<MouseEvent> mouseHandler = createMouseHandler();
    protected final ChangeListener<Animation.Status> animationInListener = createAnimationInListener();
    protected final ChangeListener<Animation.Status> animationOutListener = createAnimationOutListener();

    protected @Nullable Animation inTransition;
    protected @Nullable Animation outTransition;

    // Drag gesture state
    protected double dragStartY = 0;
    protected double dragTranslateY = 0;
    protected boolean isDragging = false;

    // Scene listener for cleanup
    protected final ChangeListener<Scene> sceneListener = (obs, old, val) -> {
        if (old != null) {
            old.removeEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        }
        if (val != null && getSkinnable().isDisplay()) {
            val.addEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        }
    };

    protected BottomSheetSkin(BottomSheet control) {
        super(control);

        // Drag handle (visual indicator)
        dragHandle = new Region();
        dragHandle.getStyleClass().add("drag-handle");

        dragHandleArea = new StackPane(dragHandle);
        dragHandleArea.getStyleClass().add("drag-handle-area");

        // Header area (optional, set by user)
        headerArea = new StackPane();
        headerArea.getStyleClass().add("header");

        // Content area
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content");

        // Ensure header keeps its preferred height, content area stretches/fills remaining space
        VBox.setVgrow(headerArea, Priority.NEVER);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Sheet container
        sheetContainer = new VBox();
        sheetContainer.getStyleClass().add("sheet");
        sheetContainer.getChildren().setAll(dragHandleArea, headerArea, contentArea);

        // Root pane (covers full parent area)
        root = new StackPane();
        root.getStyleClass().add("container");
        root.getChildren().setAll(sheetContainer);

        getChildren().add(root);
        doHide();

        registerListeners();
    }

    protected void registerListeners() {
        registerChangeListener(getSkinnable().contentProperty(), obs -> {
            Node content = getSkinnable().getContent();
            if (content != null) {
                contentArea.getChildren().setAll(content);
            } else {
                contentArea.getChildren().clear();
            }
            contentArea.layout();
            invalidateTransitions();
        });

        registerChangeListener(getSkinnable().headerProperty(), obs -> {
            Node header = getSkinnable().getHeader();
            if (header != null) {
                headerArea.getChildren().setAll(header);
            } else {
                headerArea.getChildren().clear();
            }
        });

        registerChangeListener(getSkinnable().displayProperty(), obs -> {
            if (getSkinnable().isDisplay()) {
                show();
            } else {
                hide();
            }
        });

        registerChangeListener(getSkinnable().inTransitionFactoryProperty(), obs -> invalidateInTransition());
        registerChangeListener(getSkinnable().outTransitionFactoryProperty(), obs -> invalidateOutTransition());

        // Click outside sheet to dismiss
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);

        // Track scene changes to manage the key handler lifecycle
        getSkinnable().sceneProperty().addListener(sceneListener);

        // Drag gesture on drag handle area
        dragHandleArea.setOnMousePressed(this::onDragStart);
        dragHandleArea.setOnMouseDragged(this::onDragMove);
        dragHandleArea.setOnMouseReleased(this::onDragEnd);

        // Also allow drag on the sheet container itself
        sheetContainer.setOnMousePressed(this::onDragStart);
        sheetContainer.setOnMouseDragged(this::onDragMove);
        sheetContainer.setOnMouseReleased(this::onDragEnd);
    }

    @Override
    public void dispose() {
        super.dispose();

        // Remove scene-level key handler
        Scene scene = getSkinnable().getScene();
        if (scene != null) {
            scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        }
        getSkinnable().sceneProperty().removeListener(sceneListener);

        unregisterChangeListeners(getSkinnable().contentProperty());
        unregisterChangeListeners(getSkinnable().headerProperty());
        unregisterChangeListeners(getSkinnable().displayProperty());
        unregisterChangeListeners(getSkinnable().inTransitionFactoryProperty());
        unregisterChangeListeners(getSkinnable().outTransitionFactoryProperty());

        root.removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
    }

    protected void invalidateTransitions() {
        invalidateInTransition();
        invalidateOutTransition();
    }

    protected void invalidateInTransition() {
        if (inTransition != null) {
            inTransition.statusProperty().removeListener(animationInListener);
        }
        inTransition = null;
    }

    protected void invalidateOutTransition() {
        if (outTransition != null) {
            outTransition.statusProperty().removeListener(animationOutListener);
        }
        outTransition = null;
    }

    protected EventHandler<KeyEvent> createKeyHandler() {
        return event -> {
            if (event.getCode() == KeyCode.ESCAPE && getSkinnable().isDisplay()) {
                if (getSkinnable().getPersistent()) {
                    javafx.animation.Timeline bounce = atlantafx.base.util.Animations.zoomOut(
                        sheetContainer, javafx.util.Duration.millis(100), 0.98
                    );
                    bounce.playFromStart();
                } else {
                    getSkinnable().hide();
                    event.consume();
                }
            }
        };
    }

    protected EventHandler<MouseEvent> createMouseHandler() {
        return event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            Bounds sheetBounds = sheetContainer.localToScene(sheetContainer.getBoundsInLocal());

            double clickSceneY = event.getSceneY();

            // If click is outside the sheet (above it), dismiss
            if (clickSceneY < sheetBounds.getMinY()) {
                if (getSkinnable().getPersistent()) {
                    javafx.animation.Timeline bounce = atlantafx.base.util.Animations.zoomOut(
                        sheetContainer, javafx.util.Duration.millis(100), 0.98
                    );
                    bounce.playFromStart();
                } else {
                    getSkinnable().hide();
                    event.consume();
                }
            }
        };
    }

    protected ChangeListener<Animation.Status> createAnimationInListener() {
        return (obs, old, val) -> {
            if (val == Animation.Status.RUNNING) {
                doShow();
            }
        };
    }

    protected ChangeListener<Animation.Status> createAnimationOutListener() {
        return (obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                doHide();
            }
        };
    }

    protected void onDragStart(MouseEvent e) {
        if (e.getButton() != MouseButton.PRIMARY) {
            return;
        }
        isDragging = true;
        dragStartY = e.getSceneY();
        dragTranslateY = sheetContainer.getTranslateY();
    }

    protected void onDragMove(MouseEvent e) {
        if (!isDragging) {
            return;
        }
        double deltaY = e.getSceneY() - dragStartY;
        if (deltaY > 0) {
            sheetContainer.setTranslateY(dragTranslateY + deltaY);
        }
    }

    protected void onDragEnd(MouseEvent e) {
        if (!isDragging) {
            return;
        }
        isDragging = false;

        double totalDrag = sheetContainer.getTranslateY() - dragTranslateY;
        if (totalDrag > getSkinnable().getDismissThreshold()) {
            getSkinnable().hide();
        } else {
            sheetContainer.setTranslateY(dragTranslateY);
        }
    }

    protected void show() {
        if (getSkinnable().getViewOrder() <= getSkinnable().getTopViewOrder()) {
            return;
        }

        // Reset drag state
        sheetContainer.setTranslateY(0);

        Node content = getSkinnable().getContent();
        if (content == null) {
            doShow();
            return;
        }

        if (inTransition == null && getSkinnable().getInTransitionFactory() != null) {
            inTransition = getSkinnable().getInTransitionFactory().apply(sheetContainer);
            inTransition.statusProperty().addListener(animationInListener);
        }

        if (inTransition != null) {
            inTransition.playFromStart();
        } else {
            doShow();
        }
    }

    protected void hide() {
        if (getSkinnable().getViewOrder() >= BottomSheet.Z_BACK) {
            return;
        }

        if (outTransition == null && getSkinnable().getOutTransitionFactory() != null) {
            outTransition = getSkinnable().getOutTransitionFactory().apply(sheetContainer);
            outTransition.statusProperty().addListener(animationOutListener);
        }

        if (outTransition != null) {
            outTransition.playFromStart();
        } else {
            doHide();
        }
    }

    protected void doShow() {
        getSkinnable().setDisplay(true);
        getSkinnable().setOpacity(1);
        getSkinnable().setViewOrder(getSkinnable().getTopViewOrder());

        // Register key handler on scene so ESC works regardless of focus
        Scene scene = getSkinnable().getScene();
        if (scene != null) {
            scene.addEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        }
    }

    protected void doHide() {
        getSkinnable().setOpacity(0);
        getSkinnable().setViewOrder(BottomSheet.Z_BACK);
        getSkinnable().setDisplay(false);
        sheetContainer.setTranslateY(0);

        // Remove key handler from scene
        Scene scene = getSkinnable().getScene();
        if (scene != null) {
            scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        }
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
        root.resize(contentWidth, contentHeight);
        sheetContainer.setMaxWidth(contentWidth);
        sheetContainer.setMaxHeight(contentHeight * 0.7);
    }
}
