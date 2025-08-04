/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TabsDragHandler {

    public enum DragState { NONE, START, REORDER }

    protected static final Duration DRAG_DURATION = Duration.millis(120);
    protected static final int DRAG_FORWARDS = 1;
    protected static final int DRAG_BACKWARDS = -1;
    protected static final double DRAG_DISTANCE_THRESHOLD = 0.75;

    protected final EventHandler<MouseEvent> tabDraggedHandler = this::handleTabDragged;
    protected final EventHandler<MouseEvent> tabMousePressedHandler = this::handleTabMousePressed;
    protected final EventHandler<MouseEvent> tabMouseReleasedHandler = this::handleTabMouseReleased;
    protected final DragTransition dragTransition = createDragTransition();
    protected final DragTransition dropTransition = createDropTransition();
    protected final ListChangeListener<Node> tabsDragListener = change -> {
        while (change.next()) {
            if (change.wasAdded()) {
                for (var node : change.getAddedSubList()) {
                    addReorderEventHandlers(node);
                }
            }
            if (change.wasRemoved()) {
                for (var node : change.getRemoved()) {
                    removeReorderEventHandlers(node);
                }
            }
        }
    };

    protected final TabLine control;
    protected final TabsContainer tabsContainer;
    protected final TabLineBehavior behavior;
    protected final ChangeListener<Tab.DragPolicy> dragPolicyListener;

    protected @Nullable TabSkin dragTabSkin;
    protected @Nullable TabSkin dropTabSkin;
    protected @Nullable TabSkin transitionTabSkin;
    protected DragState dragState = DragState.NONE;
    protected double dragEventLocalX;
    protected int dragTabStartIndex;
    protected int dragTabCurrentIndex;
    protected int dragDirection = DRAG_FORWARDS;
    protected double dragTabSourceX;
    protected double dragTabTransitionX;
    protected double dragTabDestX;
    protected double dropTabSourceX;
    protected double dropTabTransitionX;

    public TabsDragHandler(TabLine control,
                           TabsContainer tabsContainer,
                           TabLineBehavior behavior) {
        this.control = control;
        this.tabsContainer = tabsContainer;
        this.behavior = behavior;

        dragPolicyListener = (obs, old, val) -> updateDragListeners();
        control.tabDragPolicyProperty().addListener(dragPolicyListener);
    }

    public void init() {
        updateDragListeners();
    }

    public DragState getDragState() {
        return dragState;
    }

    public boolean isNotDragged(TabSkin tabSkin) {
        return tabSkin != dragTabSkin && tabSkin != transitionTabSkin;
    }

    public void dispose() {
        control.tabDragPolicyProperty().removeListener(dragPolicyListener);

        tabsContainer.getChildren().removeListener(tabsDragListener);
        new ArrayList<>(tabsContainer.getChildren()).forEach(this::removeReorderEventHandlers);

        dragTabSkin = null;
        dropTabSkin = null;
        transitionTabSkin = null;
    }

    //=========================================================================

    protected DragTransition createDragTransition() {
        var transition = new DragTransition(frac -> {
            if (dragTabSkin != null) {
                dragTabSkin.setLayoutX(dragTabSourceX + dragTabTransitionX * frac);
            }
        });
        transition.setInterpolator(Interpolator.EASE_OUT);
        transition.setOnFinished(e -> {
            if (dragTabCurrentIndex != dragTabStartIndex) {
                reorderTabs(dragTabStartIndex, dragTabCurrentIndex);
            }
            resetDrag();
        });

        return transition;
    }

    protected DragTransition createDropTransition() {
        var transition = new DragTransition(frac -> {
            if (transitionTabSkin != null) {
                transitionTabSkin.setLayoutX(dropTabSourceX + dropTabTransitionX * frac);
            }
        }
        );
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.setOnFinished(e -> completeHeaderReordering());

        return transition;
    }

    protected void addReorderEventHandlers(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, tabDraggedHandler);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, tabMousePressedHandler);
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, tabMouseReleasedHandler);
    }

    protected void removeReorderEventHandlers(Node node) {
        node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, tabDraggedHandler);
        node.removeEventHandler(MouseEvent.MOUSE_PRESSED, tabMousePressedHandler);
        node.removeEventHandler(MouseEvent.MOUSE_RELEASED, tabMouseReleasedHandler);
    }

    protected void updateDragListeners() {
        switch (control.getTabDragPolicy()) {
            case FIXED -> {
                for (Node node : tabsContainer.getChildren()) {
                    removeReorderEventHandlers(node);
                }
                tabsContainer.getChildren().removeListener(tabsDragListener);
            }
            case REORDER -> {
                for (Node node : tabsContainer.getChildren()) {
                    addReorderEventHandlers(node);
                }
                tabsContainer.getChildren().addListener(tabsDragListener);
            }
        }
    }

    protected void handleTabDragged(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            doDrag(event);
        }
    }

    protected void handleTabMousePressed(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            startDrag(event);
        }
    }

    protected void handleTabMouseReleased(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            stopDrag();
            event.consume();
        }
    }

    protected void startDrag(MouseEvent event) {
        // stop the animations if any are running from the previous reorder
        stopTransition(dragTransition);
        stopTransition(dropTransition);

        dragTabSkin = (TabSkin) event.getSource();
        if (dragTabSkin != null) {
            dragState = DragState.START;
            dragTabCurrentIndex = tabsContainer.getChildren().indexOf(dragTabSkin);
            dragTabStartIndex = dragTabCurrentIndex;
            dragEventLocalX = getEventLocalX(event);
            dragTabDestX = dragTabSkin.getLayoutX();
            dragTabSkin.setViewOrder(0);
        }
    }

    protected void doDrag(MouseEvent event) {
        if (dragState == DragState.NONE || dragTabSkin == null) {
            return;
        }

        double eventLocalX = getEventLocalX(event);
        double dragDelta = eventLocalX - dragEventLocalX;

        int direction = 0;
        if (dragDelta > 0) {
            // dragging the tab towards higher indexed tabs in container
            direction = DRAG_FORWARDS;
        } else if (dragDelta < 0) {
            // dragging the tab towards higher indexed tabs in container
            direction = DRAG_BACKWARDS;
        }

        // stop drop animation if drag direction is changed
        if (direction != 0 && dragDirection != direction) {
            stopTransition(dropTransition);
            dragDirection = direction;
        }

        double newLayoutX = dragTabSkin.getLayoutX() + dragDelta;

        if (newLayoutX >= 0 && newLayoutX + dragTabSkin.getWidth() <= tabsContainer.getWidth()) {
            dragState = DragState.REORDER;
            dragTabSkin.setLayoutX(newLayoutX);
            Bounds dragTabBounds = dragTabSkin.getBoundsInParent();

            // dragging the tab towards lower/higher indexed tabs in the container,
            // tab also must not be dragged outside the container
            if (direction == DRAG_FORWARDS) {
                dragForwards(dragTabBounds);
            } else {
                dragBackwards(dragTabBounds);
            }
        }

        dragEventLocalX = eventLocalX;
        event.consume();
    }

    protected void dragForwards(Bounds dragTabBounds) {
        // when the mouse is moved too fast, sufficient number of events are not generated,
        // hence it's required to check all possible tabs to be reordered
        for (int i = dragTabCurrentIndex + 1; i < tabsContainer.getChildren().size(); i++) {
            dropTabSkin = (TabSkin) tabsContainer.getChildren().get(i);

            // dropTabSkin should not be already reordering
            if (transitionTabSkin != dropTabSkin) {
                Bounds dropTabBounds = dropTabSkin.getBoundsInParent();
                double draggedDistance = dragTabBounds.getMaxX() - dropTabBounds.getMinX();

                // a tab is reordered when dragged tab crosses DRAG_DIST_THRESHOLD of the next tab's width
                if (draggedDistance > dropTabBounds.getWidth() * DRAG_DISTANCE_THRESHOLD) {
                    stopTransition(dropTransition);
                    // distance by which tab should be animated
                    dropTabTransitionX = -dragTabBounds.getWidth();
                    dragTabDestX = dropTabBounds.getMaxX() - dragTabBounds.getWidth();
                    startReorderingTransition();
                } else {
                    break;
                }
            }
        }
    }

    protected void dragBackwards(Bounds dragTabBounds) {
        // when the mouse is moved too fast, sufficient number of events are not generated,
        // hence it's required to check all possible tabs to be reordered
        for (int i = dragTabCurrentIndex - 1; i >= 0; i--) {
            dropTabSkin = (TabSkin) tabsContainer.getChildren().get(i);

            // dropTabSkin should not be already reordering
            if (transitionTabSkin != dropTabSkin) {
                Bounds dropTabBounds = dropTabSkin.getBoundsInParent();
                double draggedDist = dropTabBounds.getMaxX() - dragTabBounds.getMinX();

                // a tab is reordered when dragged tab crosses DRAG_DIST_THRESHOLD of the next tab's width
                if (draggedDist > dropTabBounds.getWidth() * DRAG_DISTANCE_THRESHOLD) {
                    stopTransition(dropTransition);
                    // distance by which tab should be animated
                    dropTabTransitionX = dragTabBounds.getWidth();
                    dragTabDestX = dropTabBounds.getMinX();

                    startReorderingTransition();
                } else {
                    break;
                }
            }
        }
    }

    protected void stopDrag() {
        if (dragState == DragState.START) {
            // no drag was performed
            resetDrag();
        } else if (dragState == DragState.REORDER && dragTabSkin != null) {
            // animate tab being dragged to its final position
            dragTabSourceX = dragTabSkin.getLayoutX();
            dragTabTransitionX = dragTabDestX - dragTabSourceX;
            dragTransition.playFromStart();
        }
        tabsContainer.invalidateScrollOffset();
    }

    protected void resetDrag() {
        dragState = DragState.NONE;

        if (dragTabSkin != null) {
            dragTabSkin.setViewOrder(1);
            dragTabSkin = null;
        }

        dropTabSkin = null;
        tabsContainer.requestLayout();
    }

    protected void startReorderingTransition() {
        // animates tab being dropped-on to its new position
        transitionTabSkin = dropTabSkin;
        if (transitionTabSkin != null) {
            dropTabSourceX = transitionTabSkin.getLayoutX();
        }
        dropTransition.playFromStart();
    }

    protected void completeHeaderReordering() {
        // remove transitionTabSkin and add it at the index position of dragTabHeader
        if (transitionTabSkin != null) {
            tabsContainer.getChildren().remove(transitionTabSkin);
            tabsContainer.getChildren().add(dragTabCurrentIndex, transitionTabSkin);

            transitionTabSkin = null;
            tabsContainer.requestLayout();
            dragTabCurrentIndex = tabsContainer.getChildren().indexOf(dragTabSkin);
        }
    }

    protected void stopTransition(Animation animation) {
        if (animation.getStatus() == Animation.Status.RUNNING) {
            animation.getOnFinished().handle(null);
            animation.stop();
        }
    }

    protected double getEventLocalX(MouseEvent e) {
        // the event is converted to the tab container local space
        // and returns a value of X coordinate with all transformations
        Point2D sceneToLocalHR = tabsContainer.sceneToLocal(e.getSceneX(), e.getSceneY());
        return sceneToLocalHR.getX();
    }

    protected void reorderTabs(int from, int to) {
        var fromTab = control.getTabs().get(from);
        control.reorderTabs(from, to);
        control.getSelectionModel().select(fromTab);
    }

    //=========================================================================

    protected static class DragTransition extends Transition {

        private final Consumer<Double> interpolate;

        public DragTransition(Consumer<Double> interpolate) {
            super();
            this.interpolate = interpolate;
            setCycleDuration(DRAG_DURATION);
        }

        @Override
        protected void interpolate(double frac) {
            interpolate.accept(frac);
        }
    }
}
