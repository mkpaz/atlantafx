/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.util.Animations;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

/**
 * The default skin for the {@link ModalPane} control.
 */
public class ModalPaneSkin extends SkinBase<ModalPane> {

    protected @Nullable ModalPane control;

    protected final StackPane root;
    protected final ScrollPane scrollPane;
    protected final StackPane contentWrapper;

    protected final EventHandler<KeyEvent> keyHandler = createKeyHandler();
    protected final EventHandler<MouseEvent> mouseHandler = createMouseHandler();
    protected final ChangeListener<Animation.Status> animationInListener = createAnimationInListener();
    protected final ChangeListener<Animation.Status> animationOutListener = createAnimationOutListener();

    protected @Nullable List<ScrollBar> scrollbars;
    protected @Nullable Animation inTransition;
    protected @Nullable Animation outTransition;

    protected ModalPaneSkin(ModalPane control) {
        super(control);

        root = new StackPane();

        contentWrapper = new StackPane();
        contentWrapper.getStyleClass().add("scrollable-content");
        contentWrapper.setAlignment(Pos.CENTER);

        scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(20_000); // scroll pane won't work without height specified
        scrollPane.setContent(contentWrapper);

        getChildren().add(scrollPane);
        control.getStyleClass().add("modal-pane");
        doHide();

        registerListeners();
    }

    protected void registerListeners() {
        registerChangeListener(getSkinnable().contentProperty(), obs -> {
            Node content = getSkinnable().getContent();

            // the transition is node-based
            if (inTransition != null && content != null) {
                inTransition.statusProperty().removeListener(animationInListener);
            }
            inTransition = null;

            if (outTransition != null && content != null) {
                outTransition.statusProperty().removeListener(animationOutListener);
            }
            outTransition = null;

            if (content != null) {
                contentWrapper.getChildren().setAll(content);
            } else {
                contentWrapper.getChildren().clear();
            }

            // JavaFX defers initial layout until node is first _shown_ on the scene,
            // which means that animations that use node bounds won't work.
            // So, we have to call it manually to init boundsInParent beforehand.
            contentWrapper.layout();
        });

        registerChangeListener(getSkinnable().displayProperty(), obs -> {
            boolean display = getSkinnable().isDisplay();
            if (display) {
                show();
            } else {
                hide();
            }
        });

        registerChangeListener(getSkinnable().inTransitionFactoryProperty(), obs -> {
            // invalidate cached value
            if (inTransition != null) {
                inTransition.statusProperty().removeListener(animationInListener);
            }
            inTransition = null;
        });

        registerChangeListener(getSkinnable().outTransitionFactoryProperty(), obs -> {
            // invalidate cached value
            if (outTransition != null) {
                outTransition.statusProperty().removeListener(animationOutListener);
            }
            outTransition = null;
        });

        contentWrapper.paddingProperty().bind(getSkinnable().paddingProperty());
        contentWrapper.alignmentProperty().bind(getSkinnable().alignmentProperty());

        // Hide overlay by pressing ESC.
        // It only works when modal pane or one of its children has focus.
        scrollPane.addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);

        // Hide overlay by clicking outside the content area. Don't use MOUSE_CLICKED,
        // because it's the same as MOUSE_RELEASED event, thus it doesn't prevent case
        // when user pressed mouse button inside the content and released outside of it.
        scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
    }

    @Override
    public void dispose() {
        super.dispose();

        unregisterChangeListeners(getSkinnable().contentProperty());
        unregisterChangeListeners(getSkinnable().displayProperty());
        unregisterChangeListeners(getSkinnable().inTransitionFactoryProperty());
        unregisterChangeListeners(getSkinnable().outTransitionFactoryProperty());

        contentWrapper.paddingProperty().unbind();
        contentWrapper.alignmentProperty().unbind();

        scrollPane.removeEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        scrollPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
    }

    @SuppressWarnings("ShortCircuitBoolean")
    protected boolean isClickInArea(MouseEvent e, Node area) {
        return (e.getX() >= area.getLayoutX() & e.getX() <= area.getLayoutX() + area.getLayoutBounds().getWidth())
            && (e.getY() >= area.getLayoutY() & e.getY() <= area.getLayoutY() + area.getLayoutBounds().getHeight());
    }

    protected EventHandler<KeyEvent> createKeyHandler() {
        return event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Node content  = getSkinnable().getContent();
                if (getSkinnable().getPersistent() && content != null) {
                    createCloseBlockedAnimation(content).playFromStart();
                } else {
                    hideAndConsume(event);
                }
            }
        };
    }

    protected EventHandler<MouseEvent> createMouseHandler() {
        return event -> {
            Node content = getSkinnable().getContent();
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            if (content == null) {
                hideAndConsume(event);
                return;
            }

            if (isClickInArea(event, content)) {
                return;
            }

            if (scrollbars == null || scrollbars.isEmpty()) {
                scrollbars = scrollPane.lookupAll(".scroll-bar").stream()
                    .filter(node -> node instanceof ScrollBar)
                    .map(node -> (ScrollBar) node)
                    .toList();
            }

            var scrollBarClick = scrollbars.stream().anyMatch(scrollBar -> isClickInArea(event, scrollBar));
            if (!scrollBarClick) {
                if (getSkinnable().getPersistent()) {
                    createCloseBlockedAnimation(content).playFromStart();
                } else {
                    hideAndConsume(event);
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

    protected Timeline createCloseBlockedAnimation(Node content) {
        return Animations.zoomOut(content, Duration.millis(100), 0.98);
    }

    protected void show() {
        if (getSkinnable().getViewOrder() <= getSkinnable().getTopViewOrder()) {
            return;
        }

        Node content = getSkinnable().getContent();
        if (content == null) {
            doShow();
            return;
        }

        if (inTransition == null && getSkinnable().getInTransitionFactory() != null) {
            inTransition = getSkinnable().getInTransitionFactory().apply(content);
            inTransition.statusProperty().addListener(animationInListener);
        }

        if (inTransition != null) {
            inTransition.playFromStart();
        } else {
            doShow();
        }
    }

    protected void hide() {
        if (getSkinnable().getViewOrder() >= ModalPane.Z_BACK) {
            return;
        }

        Node content = getSkinnable().getContent();
        if (content == null) {
            doHide();
            return;
        }

        if (outTransition == null && getSkinnable().getOutTransitionFactory() != null) {
            outTransition = getSkinnable().getOutTransitionFactory().apply(content);
            outTransition.statusProperty().addListener(animationOutListener);
        }

        if (outTransition != null) {
            outTransition.playFromStart();
        } else {
            doHide();
        }
    }

    protected void hideAndConsume(Event e) {
        hide();
        e.consume();
    }

    protected void doShow() {
        getSkinnable().setDisplay(true);
        getSkinnable().setOpacity(1);
        getSkinnable().setViewOrder(getSkinnable().getTopViewOrder());
    }

    protected void doHide() {
        getSkinnable().setOpacity(0);
        getSkinnable().setViewOrder(ModalPane.Z_BACK);
        getSkinnable().setDisplay(false);
    }
}
