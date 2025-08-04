/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import static javafx.animation.Animation.Status;

public class TabSkin extends StackPane {

    public enum TransitionState { NONE, SHOWING, HIDING }

    protected static final PseudoClass SELECTED_PSEUDOCLASS = PseudoClass.getPseudoClass("selected");
    protected static final PseudoClass PINNED_PSEUDOCLASS = PseudoClass.getPseudoClass("pinned");

    /**
     * Set when current tab width less than min tab width.
     */
    protected static final PseudoClass MIN_WIDTH_PSEUDOCLASS = PseudoClass.getPseudoClass("min-width-exceeded");

    protected final Tab tab;
    protected final TabLine control;
    protected final TabLineBehavior behavior;

    protected final HBox tabBox;
    protected final Label label;
    protected final CloseButton closeBtn;
    protected final Rectangle clip;
    protected final Subscription propertiesSubscription = Subscription.EMPTY;
    protected final ListChangeListener<String> styleClassListener = new ListChangeListener<>() {
        @Override
        public void onChanged(Change<? extends String> change) {
            getStyleClass().setAll(tab.getStyleClass());
        }
    };

    protected @Nullable Tooltip currentTooltip;
    protected boolean closing = false;
    protected TransitionState transitionState = TransitionState.NONE;
    protected @Nullable Timeline currentTransition;
    protected final DoubleProperty transitionProgress = new SimpleDoubleProperty(1.0) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    public TabSkin(Tab tab, TabLine control, TabLineBehavior behavior) {
        this.tab = tab;
        this.control = control;
        this.behavior = behavior;

        clip = new Rectangle();
        setClip(clip);

        label = new Label(tab.getText(), tab.getGraphic());
        label.getStyleClass().setAll("label");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setEllipsisString(control.getEllipsisString());
        HBox.setHgrow(label, Priority.ALWAYS);

        closeBtn = new CloseButton();
        closeBtn.setOnMousePressed(e -> {
            if (MouseButton.PRIMARY.equals(e.getButton())) {
                if (tab.isPinned()) {
                    tab.setPinned(false);
                    control.pinOrUnpin(tab);
                } else {
                    closeTab();
                }
                e.consume();
            }
        });
        closeBtn.getStyleClass().setAll("close-button");
        HBox.setHgrow(closeBtn, Priority.NEVER);

        tabBox = new HBox();
        tabBox.getStyleClass().add("tab-box");
        tabBox.setAlignment(Pos.CENTER_LEFT);
        tabBox.getChildren().addAll(label, closeBtn);

        // init listeners
        propertiesSubscription.and(tab.selectedProperty().subscribe(o -> {
            // need to request a layout pass for tab box because if the width or height
            // didn't change the label or close button may have changed
            requestLayout();
            pseudoClassStateChanged(SELECTED_PSEUDOCLASS, tab.isSelected());
            if (getParent() != null && getParent() instanceof TabsContainer container) {
                container.scrollToSelectedTab();
            }
        }));
        propertiesSubscription.and(tab.pinnedProperty().subscribe((old, val) -> {
            // refactoring note: we always need the old value, because it changes how ChangeListener works
            control.pinOrUnpin(tab);
            requestLayout();
            pseudoClassStateChanged(PINNED_PSEUDOCLASS, val);
        }));

        propertiesSubscription.and(tab.textProperty().subscribe(
            o -> label.setText(tab.getText())
        ));
        propertiesSubscription.and(tab.graphicProperty().subscribe(
            o -> label.setGraphic(tab.getGraphic())
        ));
        propertiesSubscription.and(tab.tooltipProperty().subscribe(
            o -> updateTooltip(tab.getTooltip())
        ));
        propertiesSubscription.and(tab.styleProperty().subscribe(
            o -> setStyle(tab.getStyle())
        ));
        propertiesSubscription.and(control.tabFixedWidthProperty().subscribe(o -> {
            requestLayout();
            control.requestLayout();
        }));

        tab.getStyleClass().addListener(styleClassListener);

        setOnContextMenuRequested((ContextMenuEvent e) -> {
            var cm = tab.getContextMenu();
            if (cm != null) {
                if (cm instanceof TabContextMenu tcm) {
                    tcm.updateOwnerTab(tab);
                }
                cm.show(tabBox, e.getScreenX(), e.getScreenY());
                e.consume();
            }
        });

        setOnMousePressed(e -> {
            if (MouseButton.MIDDLE.equals(e.getButton()) || MouseButton.PRIMARY.equals(e.getButton())) {
                var cm = tab.getContextMenu();
                if (cm != null && cm.isShowing()) {
                    cm.hide();
                }
            }
            if (MouseButton.MIDDLE.equals(e.getButton()) && isShowCloseButton()) {
                closeTab();
            }
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                behavior.selectTab(tab);
            }
        });

        // set initial properties
        updateTooltip(tab.getTooltip());
        pseudoClassStateChanged(SELECTED_PSEUDOCLASS, tab.isSelected());
        pseudoClassStateChanged(PINNED_PSEUDOCLASS, tab.isPinned());
        setViewOrder(1); // see TabsContainer drag&drop

        setId(tab.getId());
        setStyle(tab.getStyle());
        setAccessibleRole(AccessibleRole.TAB_ITEM);
        getStyleClass().setAll(tab.getStyleClass());
        getChildren().addAll(tabBox);
    }

    public Tab getTab() {
        return tab;
    }

    public TransitionState getTransitionState() {
        return transitionState;
    }

    public double getTransitionProgress() {
        return transitionProgress.get();
    }

    public boolean isClosing() {
        return closing;
    }

    public void setClosing(boolean value) {
        closing = value;
    }

    public void dispose() {
        tab.getStyleClass().removeListener(styleClassListener);
        tab.updateTabLine(null);

        propertiesSubscription.unsubscribe();

        setOnContextMenuRequested(null);
        setOnMousePressed(null);

        closeBtn.setOnMousePressed(null);

        currentTooltip = null;
        currentTransition = null;
    }

    public void animateShow() {
        transitionState = TransitionState.SHOWING;
        transitionProgress.setValue(0.0);
        setVisible(true);
        currentTransition = createTimeline(this, 1.0, () -> {
            transitionState = TransitionState.NONE;
            tabBox.requestLayout();
        });
        currentTransition.play();
    }

    public void animateHide(Runnable callback) {
        transitionState = TransitionState.HIDING;
        currentTransition = createTimeline(this, 0.0F, callback);
        currentTransition.play();
    }

    public void stopAnimation() {
        Timeline timeline = currentTransition;
        if (timeline != null && timeline.getStatus() == Status.RUNNING) {
            timeline.getOnFinished().handle(null);
            timeline.stop();
            currentTransition = null;
        }
    }

    @Override
    protected double computeMinWidth(double height) {
        return computePrefWidth(height);
    }

    @Override
    protected double computePrefWidth(double height) {
        Tab.ClosingPolicy closingPolicy = control.getTabClosingPolicy();

        double width = snapSizeX(label.prefWidth(-1));
        if (closingPolicy.canClose(getTab())) {
            width += snapSizeX(closeBtn.prefWidth(-1))
                     + closeBtn.snappedLeftInset()
                     + closeBtn.snappedRightInset();
        }

        return snapSizeX(width)
            + snappedLeftInset()
            + snappedRightInset();
    }

    @Override
    protected double computePrefHeight(double width) {
        return snapSizeY(tabBox.prefHeight(width))
            + snappedTopInset()
            + snappedBottomInset();
    }

    @Override
    protected void layoutChildren() {
        double width = snapSizeX(getWidth()) - snappedRightInset() - snappedLeftInset();
        double height = snapSizeY(getHeight()) - snappedTopInset() - snappedBottomInset();
        tabBox.resize(width * transitionProgress.getValue(), height);
        tabBox.relocate(snappedLeftInset(), snappedTopInset());
    }

    @Override
    protected void setWidth(double value) {
        super.setWidth(value);
        clip.setWidth(value);
    }

    @Override
    protected void setHeight(double value) {
        super.setHeight(value);
        clip.setHeight(value);
    }

    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        return switch (attribute) {
            case TEXT -> tab.getText() != null ? tab.getText() : "";
            case SELECTED -> control.getSelectionModel().getSelectedItem() == tab;
            default -> super.queryAccessibleAttribute(attribute, parameters);
        };
    }

    @Override
    public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
        if (action == AccessibleAction.REQUEST_FOCUS) {
            control.getSelectionModel().select(tab);
        } else {
            super.executeAccessibleAction(action, parameters);
        }
    }

    //=========================================================================

    protected void updateTooltip(@Nullable Tooltip tooltip) {
        if (currentTooltip != null) {
            Tooltip.uninstall(this, currentTooltip);
        }

        // install new tooltip and save as old tooltip
        if (tooltip != null) {
            Tooltip.install(this, tooltip);
            currentTooltip = tooltip;
        }
    }

    protected void updateEllipsisString() {
        label.setEllipsisString(control.getEllipsisString());
    }

    protected boolean isShowCloseButton() {
        return control.getTabClosingPolicy().canClose(getTab());
    }

    protected void closeTab() {
        if (behavior.canCloseTab(tab)) {
            // prevents multi-click, suppose it's not really needed
            setOnMousePressed(null);
            closeBtn.setOnMousePressed(null);

            behavior.closeTab(tab);
        }
    }

    protected Timeline createTimeline(TabSkin tabSkin, double endValue, Runnable onFinished) {
        var timeline = new Timeline();
        timeline.setCycleCount(1);

        var keyValue = new KeyValue(tabSkin.transitionProgress, endValue, Interpolator.LINEAR);
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(new KeyFrame(TabLine.ANIMATION_SPEED, keyValue));

        timeline.setOnFinished(e -> onFinished.run());

        return timeline;
    }

    //=========================================================================
    // Inner Classes
    //=========================================================================

    protected class CloseButton extends StackPane {

        protected final StackPane graphic = new StackPane();

        protected CloseButton() {
            graphic.getStyleClass().setAll("graphic");

            getChildren().setAll(graphic);
            getStyleClass().setAll("close-button");
            setAccessibleRole(AccessibleRole.BUTTON);

            // JavaFX i18n API is not public
            // closeBtn.setAccessibleText(ControlResources.getString("Accessibility.title.TabPane.CloseButton"));
        }

        @Override
        public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
            if (action == AccessibleAction.FIRE) {
                closeTab();
            } else {
                super.executeAccessibleAction(action, parameters);
            }
        }
    }
}
