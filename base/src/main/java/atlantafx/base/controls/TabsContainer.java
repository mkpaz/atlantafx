/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.controls.Tab.ResizePolicy;
import atlantafx.base.controls.TabsDragHandler.DragState;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

public class TabsContainer extends StackPane {

    protected static final PseudoClass PSEUDO_CLASS_FIRST = PseudoClass.getPseudoClass("first");
    protected static final PseudoClass PSEUDO_CLASS_LAST = PseudoClass.getPseudoClass("last");

    protected final TabLine control;
    protected final TabLineBehavior behavior;

    protected final Rectangle headerClip;
    protected final ListChangeListener<Tab> tabsListener;
    protected final TabsDragHandler tabsDragHandler;
    protected final ChangeListener<Tab.@Nullable ClosingPolicy> closingPolicyListener;
    protected final ChangeListener<Tab.@Nullable ResizePolicy> resizePolicyListener;

    public TabsContainer(TabLine control, TabLineBehavior behavior) {
        this.control = control;
        this.behavior = behavior;

        headerClip = new Rectangle();
        setClip(headerClip);

        tabsListener = this::onTabListChange;
        control.getTabs().addListener(tabsListener);
        getStyleClass().setAll("tabs", control.getTabClosingPolicy().getStyleClass());

        tabsDragHandler = new TabsDragHandler(control, this, behavior);
        tabsDragHandler.init();

        addEventHandler(ScrollEvent.SCROLL, (ScrollEvent e) -> {
            double delta = Math.abs(e.getDeltaY()) > Math.abs(e.getDeltaX()) ? e.getDeltaY() : e.getDeltaX();
            setScrollOffset(scrollOffset + delta);
        });

        closingPolicyListener = (obs, old, val) -> {
            //noinspection ConstantValue, Intellij is being stupid
            if (old != null) {
                getStyleClass().remove(old.getStyleClass());
            }
            //noinspection ConstantValue, Intellij is being stupid
            if (val != null) {
                getStyleClass().add(val.getStyleClass());
            }
            requestLayout();
        };

        resizePolicyListener = (obs, old, val) -> requestLayout();

        control.tabClosingPolicyProperty().addListener(closingPolicyListener);
        control.tabResizePolicyProperty().addListener(resizePolicyListener);
    }

    protected void dispose() {
        control.getTabs().removeListener(tabsListener);
        control.tabClosingPolicyProperty().removeListener(closingPolicyListener);
        control.tabResizePolicyProperty().removeListener(resizePolicyListener);

        // weak listeners are not reliable, dispose each tab skin manually
        new ArrayList<>(getChildren()).forEach(node -> ((TabSkin) node).dispose());

        tabsDragHandler.dispose();
    }

    //=========================================================================
    // Layout
    //=========================================================================

    protected boolean measuringTabs = false;

    @Override
    protected double computePrefWidth(double height) {
        return DoubleStream.of(computeTabsWidths()).sum()
            + snappedLeftInset()
            + snappedRightInset();
    }

    @Override
    protected double computePrefHeight(double width) {
        double height = 0;
        for (Node node : getChildren()) {
            TabSkin tabSkin = (TabSkin) node;
            height = Math.max(height, tabSkin.prefHeight(width));
        }

        return snapSizeY(height)
            + snappedTopInset()
            + snappedBottomInset();
    }

    @Override
    protected void layoutChildren() {
        double availableWidth = getWidth();
        boolean isTabsFit = computeTabsPrefWidth() < availableWidth;

        if (isTabsFit) {
            setScrollOffset(0);
        } else {
            if (scrollOffsetDirty) {
                // probably not needed
                // scrollToSelectedTab();
                scrollOffsetDirty = false;
            }
            // ensure there's no gap between last visible tab and trailing edge
            setScrollOffset(scrollOffset);
        }

        updateHeaderClip();

        double prefHeight = snapSizeY(prefHeight(-1));
        double[] widths = computeTabsWidths();
        List<Node> children = getChildrenUnmodifiable();

        // build from the left
        double tabX = scrollOffset;

        for (int i = 0; i < children.size(); i++) {
            TabSkin tabSkin = (TabSkin) children.get(i);
            double tabWidth = widths[i];
            double tabHeight = snapSizeY(tabSkin.prefHeight(-1));

            tabSkin.resize(tabWidth, tabHeight);

            // ensure the tabs are located in the correct position when they are of differing heights
            double tabY = prefHeight - tabHeight - snappedBottomInset();

            // only layout tab if we are not in progress of dragging or processed tab isn't the dragged tab
            if (tabsDragHandler.getDragState() != DragState.REORDER || tabsDragHandler.isNotDragged(tabSkin)) {
                tabSkin.relocate(tabX, tabY);
            }

            tabSkin.pseudoClassStateChanged(TabSkin.MIN_WIDTH_PSEUDOCLASS, tabWidth < control.getTabMinWidth());

            tabX += tabWidth;
        }
    }

    protected void updateHeaderClip() {
        measuringTabs = true;
        double prefWidth = snapSizeX(prefWidth(-1));
        measuringTabs = false;

        double prefHeight = snapSizeY(prefHeight(-1));
        double clipWidth = Math.min(prefWidth, snapSizeX(getWidth()));

        headerClip.setX(0);
        headerClip.setY(0);
        headerClip.setWidth(clipWidth);
        headerClip.setHeight(prefHeight);
    }

    protected double computeTabsPrefWidth() {
        ResizePolicy resizePolicy = control.getTabResizePolicy();
        int tabCount = getChildrenUnmodifiable().size();
        double availableWidth = getWidth();

        double prefWidth = 0;
        for (Node node : getChildrenUnmodifiable()) {
            // compute pref width with isTabsFit=true, actual value doesn't matter
            // this gives us minimal pref tab size
            double tabWidth = normalizeWidth(
                resizePolicy.computePrefWidth(availableWidth, tabCount, true),
                node.prefWidth(-1)
            );

            prefWidth += tabWidth;
        }
        return snapSizeX(prefWidth);
    }

    /**
     * Compute tabs widths according to the {@link ResizePolicy}.
     */
    protected double[] computeTabsWidths() {
        ResizePolicy resizePolicy = control.getTabResizePolicy();
        int tabCount = getChildrenUnmodifiable().size();
        double availableWidth = getWidth();
        boolean isTabsFit = computeTabsPrefWidth() < availableWidth;
        control.updateTabsFit(isTabsFit);

        double[] widths = new double[tabCount];
        double constrainedWidth = 0;
        int i = 0;

        for (Node node : getChildrenUnmodifiable()) {
            TabSkin tabSkin = (TabSkin) node;

            double tabWidth = normalizeWidth(
                resizePolicy.computePrefWidth(availableWidth, tabCount, isTabsFit),
                tabSkin.prefWidth(-1)
            );

            tabWidth = snapSizeX(tabWidth);

            widths[i] = tabWidth;
            constrainedWidth += tabWidth;

            i++;
        }

        // Suppose we have 100 pixels available and need to distribute the width among 6 tabs.
        // 100 / 6 = 16.66 from the resize policy will be rounded up to 17 by the snapSizeX().
        // Consequently, 17 * 6 = 102, resulting in 2 extra pixels needed to lay out the tabs,
        // though the resize policy also states that scrolling must not be used. The code above
        // removes this delta by subtracting 1 pixel from each tab, starting from the last one.
        if (!resizePolicy.isScrollable() && constrainedWidth > availableWidth) {
            double delta = constrainedWidth - availableWidth;
            for (int k = widths.length - 1; k >= 0; k--) {
                widths[k] = widths[k] - 1;
                constrainedWidth--;
                delta--;

                if (delta <= 0) {
                    break;
                }
            }
        }

        return widths;
    }

    protected double normalizeWidth(double constrainedWidth, double contentWidth) {
        if (constrainedWidth == ResizePolicy.USE_FIXED_SIZE) {
            return control.getTabFixedWidth();
        }
        if (constrainedWidth == ResizePolicy.USE_COMPUTED_SIZE) {
            return contentWidth;
        }

        return constrainedWidth;
    }

    //=========================================================================
    // Scroll
    //=========================================================================

    protected double scrollOffset;
    protected boolean scrollOffsetDirty = true;

    protected void setScrollOffset(double scrollOffset) {
        double availableWidth = snapSizeX(getWidth());
        double tabsWidth = DoubleStream.of(computeTabsWidths()).sum();
        double prevScrollOffset = this.scrollOffset;

        double newScrollOffset = scrollOffset;
        if ((availableWidth - scrollOffset) > tabsWidth && scrollOffset < 0) {
            // stick to the rightmost side
            newScrollOffset = availableWidth - tabsWidth;
        } else if (scrollOffset > 0) {
            // stick to the leftmost side
            newScrollOffset = 0;
        }

        if (Math.abs(newScrollOffset - prevScrollOffset) > 0.001) {
            this.scrollOffset = newScrollOffset;
            requestLayout();
        }
    }

    protected void scrollToSelectedTab() {
        var selectedTabIndex = control.getSelectionModel().getSelectedIndex();
        double[] widths = computeTabsWidths();
        if (selectedTabIndex >= widths.length) {
            return;
        }

        double availableWidth = snapSizeX(getWidth());

        double selectedTabStartX = 0;
        for (int i = 0; i < selectedTabIndex; i++) {
            selectedTabStartX += widths[i];
        }

        double selectedTabEndX = selectedTabStartX + widths[selectedTabIndex];
        double newScrollOffset = scrollOffset;

        if (selectedTabStartX < -scrollOffset) {
            newScrollOffset = -selectedTabStartX;
        } else if (selectedTabEndX > (availableWidth - scrollOffset)) {
            newScrollOffset = availableWidth - selectedTabEndX;
        }

        setScrollOffset(newScrollOffset);
    }

    protected void invalidateScrollOffset() {
        scrollOffsetDirty = true;
    }

    //=========================================================================
    // Manipulate Tabs
    //=========================================================================

    protected void onTabListChange(ListChangeListener.Change<? extends Tab> change) {
        var tabsToRemove = new ArrayList<Tab>();
        var tabsToAdd = new ArrayList<Tab>();

        while (change.next()) {
            if (change.wasPermutated()) {
                if (tabsDragHandler.getDragState() != DragState.REORDER) {
                    // create the list of permutated tabs
                    List<Tab> tabs = control.getTabs();
                    List<Tab> permutatedTabs = new ArrayList<>(change.getTo() - change.getFrom());
                    for (int i = change.getFrom(); i < change.getTo(); i++) {
                        permutatedTabs.add(tabs.get(i));
                    }

                    // clear selection
                    Tab prevSelectedTab = control.getSelectionModel().getSelectedItem();
                    control.getSelectionModel().clearSelection();

                    // remove permutated tabs, add them back in correct order
                    removeTabs(permutatedTabs);
                    addTabs(permutatedTabs, change.getFrom());

                    // restore old selection
                    control.getSelectionModel().select(prevSelectedTab);
                }
            }

            if (change.wasRemoved()) {
                tabsToRemove.addAll(change.getRemoved());
            }
            if (change.wasAdded()) {
                tabsToAdd.addAll(change.getAddedSubList());
            }
        }

        // now only remove the tabs that are not in the tabsToAdd list
        tabsToRemove.removeAll(tabsToAdd);
        removeTabs(tabsToRemove);

        // and add any new tabs that we don't already have showing
        var tabsToMove = new ArrayList<Pair<Integer, TabSkin>>();
        if (!tabsToAdd.isEmpty()) {
            for (var node : new ArrayList<>(getChildren())) {
                var tabSkin = (TabSkin) node;
                if (!tabSkin.isClosing() && tabsToAdd.contains(tabSkin.getTab())) {
                    tabsToAdd.remove(tabSkin.getTab());

                    // if a tab was removed and added back at the same time, we must ensure
                    // that the skin index in tab container is same as tab index in getTabs()
                    int tabIndex = control.getTabs().indexOf(tabSkin.getTab());
                    int skinIndex = getChildren().indexOf(tabSkin);
                    if (tabIndex != skinIndex) {
                        tabsToMove.add(new Pair<>(tabIndex, tabSkin));
                    }
                }
            }

            if (!tabsToAdd.isEmpty()) {
                addTabs(tabsToAdd, control.getTabs().indexOf(tabsToAdd.getFirst()));
            }

            for (Pair<Integer, TabSkin> move : tabsToMove) {
                doMoveTab(move.getKey(), move.getValue());
            }
        }

        updatePseudoClasses();

        control.requestLayout();
    }

    protected void updatePseudoClasses() {
        var children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            var tabSkin = (TabSkin) children.get(i);
            tabSkin.pseudoClassStateChanged(PSEUDO_CLASS_FIRST, i == 0);
            tabSkin.pseudoClassStateChanged(PSEUDO_CLASS_LAST, i == children.size() - 1);
        }
    }

    protected void addTabs(List<? extends Tab> addedList, int from) {
        // check if any other tabs are animating - they must be completed first
        for (var node : new ArrayList<>(getChildren())) {
            var tabSkin = (TabSkin) node;
            if (tabSkin.getTransitionState() == TabSkin.TransitionState.HIDING) {
                stopCurrentAnimation(tabSkin.getTab());
            }
        }

        int index = 0;
        for (Tab tabToAdd : addedList) {
            // must happen before addTab() call below
            stopCurrentAnimation(tabToAdd);

            // a new tab was added - animate it out
            if (!isVisible()) {
                setVisible(true);
            }

            doAddTab(new TabSkin(tabToAdd, control, behavior), from + index);
            index++;

            tabToAdd.updateTabLine(control);

            var tabSkin = findTab(tabToAdd);
            if (tabSkin == null) {
                continue;
            }

            if (control.getAnimated()) {
                tabSkin.animateShow();
            } else {
                tabSkin.setVisible(true);
                tabSkin.tabBox.requestLayout();
            }
        }
    }

    protected void removeTabs(List<? extends Tab> removedList) {
        for (Tab tabToRemove : removedList) {
            stopCurrentAnimation(tabToRemove);

            final TabSkin tabSkin = findTab(tabToRemove);
            if (tabSkin == null) {
                continue;
            }

            tabSkin.setClosing(true);
            tabSkin.dispose();

            Runnable cleanup = () -> {
                tabSkin.transitionState = TabSkin.TransitionState.NONE;

                doRemoveTab(tabToRemove);
                requestLayout();
                if (control.getTabs().isEmpty()) {
                    setVisible(false);
                }
            };

            if (control.getAnimated()) {
                tabSkin.animateHide(cleanup);
            } else {
                cleanup.run();
            }
        }
    }

    protected void doAddTab(TabSkin tabSkin, int addToIndex) {
        getChildren().add(addToIndex, tabSkin);
        invalidateScrollOffset();
    }

    protected void doRemoveTab(Tab tab) {
        TabSkin tabHeaderSkin = findTab(tab);
        if (tabHeaderSkin != null) {
            getChildren().remove(tabHeaderSkin);
        }
        invalidateScrollOffset();
    }

    protected void doMoveTab(int moveToIndex, TabSkin tabSkin) {
        if (moveToIndex != getChildren().indexOf(tabSkin)) {
            getChildren().remove(tabSkin);
            getChildren().add(moveToIndex, tabSkin);
        }
        invalidateScrollOffset();
    }

    protected @Nullable TabSkin findTab(Tab tab) {
        for (var node : getChildren()) {
            var tabSkin = (TabSkin) node;
            if (tabSkin.getTab().equals(tab)) {
                return tabSkin;
            }
        }
        return null;
    }

    protected void stopCurrentAnimation(Tab tab) {
        var tabSkin = findTab(tab);
        if (tabSkin != null) {
            // execute the code immediately, don't wait for the animation to finish
            tabSkin.stopAnimation();
        }
    }
}
