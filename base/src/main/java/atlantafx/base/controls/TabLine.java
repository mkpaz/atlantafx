/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.shim.collections.ReorderableList;
import atlantafx.base.util.NullSafetyHelper;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The {@code TabLine} is a more customizable alternative to the JavaFX {@link TabPane} component.
 *
 * <p><h3>Removed features:</h3>
 * <li>Orientation: {@code TabLine} can only be placed horizontally.</li>
 * <li>Content view: You no longer use the {@link javafx.scene.control.Tab#setContent(Node)} method.
 * Instead, you can subscribe to {@link SelectionModel} changes and modify the application view as needed,
 * such as switching the {@link StackPane} layer.</li>
 * <li>Automatic dropdown menu display when the tabs overflow the viewport size. Instead, use
 * {@link TabMenuButton} (or any control of your choice) and place it in one of the two custom slots on
 * the right or left side of the tabs.</li>
 *
 * <p><h3>Added features:</h3>
 * <li>Allows for pinning tabs via {@link Tab#pinnedProperty()}.</li>
 * <li>Enables the placement of custom controls through the {@link TabLine#leftNodeProperty()} and
 * {@link TabLine#rightNodeProperty()}.</li>
 * <li>Closing policy is now an interface rather than an enum, allowing you to implement your own policy
 * to determine whether a tab can be closed or pinned. See {@link Tab.ClosingPolicy}.</li>
 * <li>{@link Tab.ResizePolicy} to define the strategy for calculating the width of tabs, including viewport
 * overflow behavior.</li>
 * <li>Reusable {@link TabContextMenu}, allowing you to attach a single context menu instance to any tab.</li>
 * <li>Simplified and improved control layout to support additional styling features available, such as a hover
 * effect for the close button.</li>
 *
 * <p><h3>Example:</h3>
 * <pre>
 * var tabLine = new TabLine();
 * tabLine.getTabs().setAll(
 *     new Tab("#first", "First"),
 *     new Tab("#second", "Second")
 * );
 * tabLine.setTabDragPolicy(Tab.DragPolicy.REORDER);
 * tabLine.setTabResizePolicy(Tab.ResizePolicy.FIXED_WIDTH);
 * tabLine.setTabClosingPolicy(Tab.ClosingPolicy.SELECTED_TAB);
 *
 * var tabContent1 = new Label("First Tab");
 * var tabContent2 = new Label("Second Tab");
 *
 * var contentArea = new BorderPane(label);
 * tabLine.getSelectionModel().selectedItemProperty().subscribe(tab -> {
 *     var content = switch (tab.getId()) {
 *         case "#first" -> tabContent1;
 *         case "#second" -> tabContent2;
 *         default -> null;
 *     };
 *     contentArea.setCenter(content);
 * });
 * </pre>
 */
public class TabLine extends Control {

    protected static final Duration ANIMATION_SPEED = Duration.millis(150);
    protected static final double DEFAULT_TAB_FIXED_WIDTH = 120;
    protected static final double DEFAULT_TAB_MIN_WIDTH = 40;

    // fo the reference: https://bugs.openjdk.org/browse/JDK-8350921
    protected final ObservableList<Tab> tabs = new ReorderableList<>(new ArrayList<>());

    /**
     * Creates a new TabLine with no tabs.
     */
    public TabLine() {
        this((Tab[]) null);
    }

    /**
     * Creates a new TabLine with the given tabs set to show.
     *
     * @param tabs the {@link Tab tabs} to display inside the TabLine.
     */
    public TabLine(Tab @Nullable ... tabs) {
        setAccessibleRole(AccessibleRole.TAB_PANE);
        getStyleClass().setAll("tab-line");

        if (tabs != null) {
            getTabs().addAll(tabs);
        }
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new TabLineSkin(this);
    }

    /**
     * The tabs to display in this TabLine. Changing this {@code ObservableList}
     * will immediately result in the TabLine updating to display the new contents.
     *
     * <p>If the tabs {@code ObservableList} changes, the selected tab will remain the
     * previously selected tab, if it remains within this {@code ObservableList}.
     * If the previously selected tab is no longer in the tabs {@code ObservableList},
     * the selected tab will become the first tab in the {@code ObservableList}.
     */
    public final ObservableList<Tab> getTabs() {
        return tabs;
    }

    //=========================================================================
    // Properties
    //=========================================================================

    /**
     * The selection model used for selecting tabs. Changing the model alters
     * how the tabs are selected and which tabs are first or last.
     */
    public final ObjectProperty<SingleSelectionModel<Tab>> selectionModelProperty() {
        return selectionModel;
    }

    protected ObjectProperty<SingleSelectionModel<Tab>> selectionModel = new SimpleObjectProperty<>(
        this, "selectionModel", new TabLineSelectionModel(this)
    );

    public SingleSelectionModel<Tab> getSelectionModel() {
        return selectionModel.get();
    }

    public void setSelectionModel(@Nullable SingleSelectionModel<Tab> value) {
        selectionModel.set(Objects.requireNonNullElse(value, new TabLineSelectionModel(this)));
    }

    /**
     * Specifies how the {@code TabLine} handles tab closing from an end-user's perspective.
     * The default value is {@code Tab.ClosingPolicy.ALL_TABS}
     *
     * <p>Refer to the {@link Tab.ClosingPolicy} enumeration for further details.
     */
    public final ObjectProperty<Tab.ClosingPolicy> tabClosingPolicyProperty() {
        if (tabClosingPolicy == null) {
            tabClosingPolicy = new SimpleObjectProperty<>(
                this, "tabClosingPolicy", Tab.ClosingPolicy.ALL_TABS
            );
        }
        return tabClosingPolicy;
    }

    protected @Nullable ObjectProperty<Tab.ClosingPolicy> tabClosingPolicy;

    public Tab.ClosingPolicy getTabClosingPolicy() {
        return tabClosingPolicyProperty().get();
    }

    public void setTabClosingPolicy(Tab.@Nullable ClosingPolicy value) {
        tabClosingPolicyProperty().set(Objects.requireNonNullElse(value, Tab.ClosingPolicy.ALL_TABS));
    }

    /**
     * The drag policy for the tabs specifies if tabs can be reordered or not.
     */
    public final ObjectProperty<Tab.DragPolicy> tabDragPolicyProperty() {
        if (tabDragPolicy == null) {
            tabDragPolicy = new SimpleObjectProperty<>(this, "tabDragPolicy", Tab.DragPolicy.FIXED);
        }
        return tabDragPolicy;
    }

    protected @Nullable ObjectProperty<Tab.DragPolicy> tabDragPolicy;

    public Tab.DragPolicy getTabDragPolicy() {
        return tabDragPolicyProperty().get();
    }

    public void setTabDragPolicy(Tab.@Nullable DragPolicy value) {
        tabDragPolicyProperty().set(Objects.requireNonNullElse(value, Tab.DragPolicy.FIXED));
    }

    /**
     * The resize policy for the tabs specifies how to resize the tabs when there is no available
     * space left in the {@link TabLine}.
     */
    public final ObjectProperty<Tab.ResizePolicy> tabResizePolicyProperty() {
        if (tabResizePolicy == null) {
            tabResizePolicy = new SimpleObjectProperty<>(this, "tabResizePolicy", Tab.ResizePolicy.COMPUTED_WIDTH);
        }
        return tabResizePolicy;
    }

    protected @Nullable ObjectProperty<Tab.ResizePolicy> tabResizePolicy;

    public Tab.ResizePolicy getTabResizePolicy() {
        return tabResizePolicyProperty().get();
    }

    public void setTabResizePolicy(Tab.@Nullable ResizePolicy value) {
        tabResizePolicyProperty().set(Objects.requireNonNullElse(value, Tab.ResizePolicy.COMPUTED_WIDTH));
    }

    /**
     * Specifies whether the {@code TabLine} animates adding and closing tabs or not.
     * The default value is {@code true}.
     */
    public final BooleanProperty animatedProperty() {
        if (animated == null) {
            animated = new SimpleBooleanProperty(this, "animated", true);
        }
        return animated;
    }

    protected @Nullable BooleanProperty animated;

    public boolean getAnimated() {
        return animated == null || animatedProperty().get();
    }

    public void setAnimated(boolean value) {
        animatedProperty().set(value);
    }

    /**
     * Specifies the fixed width of a {@code Tab} in the {@code TabLine} that can be used
     * by the {@link #tabResizePolicy}. See {@link Tab.FixedWidthResizePolicy}.
     *
     * <p>This value can also be set via CSS using {@code -fx-tab-fixed-width}.
     */
    public final DoubleProperty tabFixedWidthProperty() {
        if (tabFixedWidth == null) {
            tabFixedWidth = new StyleableDoubleProperty(DEFAULT_TAB_FIXED_WIDTH) {

                @Override
                public CssMetaData<TabLine, Number> getCssMetaData() {
                    return StyleableProperties.TAB_FIXED_WIDTH;
                }

                @Override
                public Object getBean() {
                    return TabLine.this;
                }

                @Override
                public String getName() {
                    return "tabFixedWidth";
                }
            };
        }

        return tabFixedWidth;
    }

    protected @Nullable DoubleProperty tabFixedWidth;

    public double getTabFixedWidth() {
        return tabFixedWidth != null ? tabFixedWidth.getValue() : DEFAULT_TAB_FIXED_WIDTH;
    }

    public void setTabFixedWidth(double value) {
        tabFixedWidthProperty().setValue(value);
    }

    /**
     * Specifies the min width of a {@code Tab} in the {@code TabLine}.
     *
     * <p>This value can also be set via CSS using {@code -fx-tab-min-width}.
     */
    public final DoubleProperty tabMinWidthProperty() {
        if (tabMinWidth == null) {
            tabMinWidth = new StyleableDoubleProperty(DEFAULT_TAB_MIN_WIDTH) {

                @Override
                public CssMetaData<TabLine, Number> getCssMetaData() {
                    return StyleableProperties.TAB_MIN_WIDTH;
                }

                @Override
                public Object getBean() {
                    return TabLine.this;
                }

                @Override
                public String getName() {
                    return "tabMinWidth";
                }
            };
        }

        return tabMinWidth;
    }

    protected @Nullable DoubleProperty tabMinWidth;

    public double getTabMinWidth() {
        return tabMinWidth != null ? tabMinWidth.getValue() : DEFAULT_TAB_MIN_WIDTH;
    }

    public void setTabMinWidth(double value) {
        tabMinWidthProperty().setValue(value);
    }

    /**
     * Indicates whether the total width of the tabs fits within the available space.
     */
    public final ReadOnlyBooleanProperty tabsFitProperty() {
        return tabsFitPropertyImpl().getReadOnlyProperty();
    }

    protected @Nullable ReadOnlyBooleanWrapper tabsFit;

    protected ReadOnlyBooleanWrapper tabsFitPropertyImpl() {
        if (tabsFit == null) {
            tabsFit = new ReadOnlyBooleanWrapper(this, "tabsFit", true);
        }
        return tabsFit;
    }

    public boolean isTabsFit() {
        return tabsFit != null && tabsFit.get();
    }

    protected void updateTabsFit(boolean value) {
        tabsFitPropertyImpl().set(value);
    }

    /**
     * Specifies the string to display for the ellipsis when tab text is truncated.
     * The default value is empty string.
     */
    public final StringProperty ellipsisStringProperty() {
        if (ellipsisString == null) {
            ellipsisString = new SimpleStringProperty(this, "ellipsisString", "");
        }
        return ellipsisString;
    }

    protected @Nullable StringProperty ellipsisString;

    public String getEllipsisString() {
        return ellipsisString != null ? ellipsisString.get() : "";
    }

    public void setEllipsisString(@Nullable String value) {
        ellipsisStringProperty().set(Objects.requireNonNullElse(value, ""));
    }

    /**
     * Represents a custom node that is placed at the top of the TabLine, before all tabs.
     */
    public final ObjectProperty<@Nullable Node> leftNodeProperty() {
        return leftNode;
    }

    private final ObjectProperty<@Nullable Node> leftNode = new SimpleObjectProperty<>(this, "leftNode", null);

    public @Nullable Node getLeftNode() {
        return leftNode.getValue();
    }

    public void setLeftNode(@Nullable Node value) {
        leftNode.setValue(value);
    }

    /**
     * Represents a custom node that is placed to the right of the TabLine, after all tabs.
     */
    public final ObjectProperty<@Nullable Node> rightNodeProperty() {
        return rightNode;
    }

    private final ObjectProperty<@Nullable Node> rightNode = new SimpleObjectProperty<>(this, "rightNode", null);

    public void setRightNode(@Nullable Node value) {
        rightNode.setValue(value);
    }

    public @Nullable Node getRightNode() {
        return rightNode.getValue();
    }

    //=========================================================================
    // Auxiliary Methods
    //=========================================================================

    @SuppressWarnings("ConstantValue")
    protected void pinOrUnpin(Tab tab) {
        var selectedTab = getSelectionModel().getSelectedItem();
        int to = getIndexToMovePinedTab(tab);

        if (to >= 0) {
            getTabs().remove(tab);
            getTabs().add(to, tab);

            if (selectedTab != null) {
                getSelectionModel().select(selectedTab);
            }
        }
    }

    protected int getIndexToMovePinedTab(Tab tabToPin) {
        var tabs = getTabs();
        if (tabs.isEmpty()) {
            return -1;
        } else if (tabs.size() == 1) {
            return 0;
        }

        int lastPinnedTabIndex = -1;
        int lastTabIndex = tabs.size() - 1;
        int index = -1;

        for (var tab : tabs) {
            if (tab == tabToPin) {
                continue;
            }

            // find first not pinned tab
            if (!tab.isPinned()) {
                lastPinnedTabIndex = index;
                break;
            }

            index++;
        }

        if (lastPinnedTabIndex == -1) {
            return index == -1
                ? 0 // no pinned tabs, make first
                : lastTabIndex; // all tabs pinned, make last
        }

        return Math.min(lastPinnedTabIndex + 1, lastTabIndex);
    }

    @SuppressWarnings("ConstantValue")
    protected int getNextSelectedTabIndex(int start) {
        int min = 0;
        int max = getTabs().size();

        int index = start % max;
        if (index > min && max < min) {
            index = index + max - min;
        } else if (index < min && max > min) {
            index = index + max - min;
        }

        return index;
    }

    protected void reorderTabs(int fromIndex, int toIndex) {
        var fromTab = getTabs().get(fromIndex);
        var toTab = getTabs().get(toIndex);
        var tabs = getTabs();

        if (!tabs.contains(fromTab) || !tabs.contains(toTab) || fromTab == toTab) {
            return;
        }

        if (tabs instanceof ReorderableList<Tab> list) {
            list.reorder(fromIndex, toIndex);
        } else {
            tabs.remove(fromTab);
            tabs.add(toIndex, fromTab);
        }
    }

    //=========================================================================
    // Stylesheet Handling
    //=========================================================================

    private static class StyleableProperties {

        private static final CssMetaData<TabLine, Number> TAB_FIXED_WIDTH =
            new CssMetaData<>("-fx-tab-fixed-width", SizeConverter.getInstance(), DEFAULT_TAB_FIXED_WIDTH) {

                @Override
                public boolean isSettable(TabLine tabLine) {
                    return tabLine.tabFixedWidth == null || !tabLine.tabFixedWidth.isBound();
                }

                @Override
                @SuppressWarnings("unchecked")
                public StyleableProperty<Number> getStyleableProperty(TabLine tabLine) {
                    return (StyleableProperty<Number>) tabLine.tabFixedWidthProperty();
                }
            };

        private static final CssMetaData<TabLine, Number> TAB_MIN_WIDTH =
            new CssMetaData<>("-fx-tab-min-width", SizeConverter.getInstance(), DEFAULT_TAB_MIN_WIDTH) {

                @Override
                public boolean isSettable(TabLine tabLine) {
                    return tabLine.tabMinWidth == null || !tabLine.tabMinWidth.isBound();
                }

                @Override
                @SuppressWarnings("unchecked")
                public StyleableProperty<Number> getStyleableProperty(TabLine tabLine) {
                    return (StyleableProperty<Number>) tabLine.tabMinWidthProperty();
                }
            };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
                Control.getClassCssMetaData()
            );
            styleables.add(TAB_FIXED_WIDTH);
            styleables.add(TAB_MIN_WIDTH);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * Gets the {@code CssMetaData} associated with this class,
     * which may include the {@code CssMetaData} of its superclasses.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    //=========================================================================
    // Support classes
    //=========================================================================

    public static class TabLineSelectionModel extends SingleSelectionModel<Tab> {

        protected TabLine tabLine;
        protected final ListChangeListener<Tab> tabsListener = this::onTabsChange;

        public TabLineSelectionModel(TabLine tabLine) {
            this.tabLine = tabLine;
            tabLine.getTabs().addListener(tabsListener);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void select(int index) {
            var tab = getModelItem(index);
            int size = getItemCount();
            int prevIndex = getSelectedIndex();

            if (index < 0 || (size > 0 && index >= size) || (index == prevIndex && tab != null && tab.isSelected())) {
                return;
            }

            // unselect the old tab
            if (prevIndex >= 0 && prevIndex < size) {
                tabLine.getTabs().get(prevIndex).updateSelected(false);
            }

            setSelectedIndex(index);

            if (tab != null) {
                setSelectedItem(tab);
            }

            // select the new tab
            if (getSelectedIndex() >= 0 && getSelectedIndex() < getItemCount()) {
                tabLine.getTabs().get(getSelectedIndex()).updateSelected(true);
            }

            tabLine.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void select(Tab tab) {
            int size = getItemCount();

            for (int index = 0; index < size; index++) {
                var item = getModelItem(index);
                if (item != null && item.equals(tab)) {
                    select(index);
                    return;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected @Nullable Tab getModelItem(int index) {
            ObservableList<Tab> items = tabLine.getTabs();
            if (index < 0 || index >= items.size()) {
                return null;
            }
            return items.get(index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected int getItemCount() {
            return tabLine.getTabs().size();
        }

        @SuppressWarnings("ConstantValue")
        protected void onTabsChange(ListChangeListener.Change<? extends Tab> change) {
            while (change.next()) {
                for (Tab tab : change.getRemoved()) {
                    // unselect removed tab
                    if (tab.isSelected()) {
                        tab.updateSelected(false);
                        int index = change.getFrom();

                        // try to select the nearest tab from the position of the closed tab
                        selectNearestAvailableTab(index);
                    }
                }

                if (change.wasAdded() || change.wasRemoved()) {
                    // the selected tab index can be out of sync with the list of tab
                    // if we add or remove tabs before the selected tab
                    if (getSelectedIndex() != tabLine.getTabs().indexOf(getSelectedItem())) {
                        clearAndSelect(tabLine.getTabs().indexOf(getSelectedItem()));
                    }
                }
            }

            if (getSelectedIndex() == -1 && getSelectedItem() == null && !tabLine.getTabs().isEmpty()) {
                selectNearestAvailableTab(0);
            } else if (tabLine.getTabs().isEmpty()) {
                clearSelection();
            }
        }

        protected void selectNearestAvailableTab(int tabIndex) {
            int size = getItemCount();
            int delta = 1;
            Tab bestTab = null;

            while (true) {
                // look leftwards
                int downPos = tabIndex - delta;
                if (downPos >= 0) {
                    Tab tmp = getModelItem(downPos);
                    if (tmp != null) {
                        bestTab = tmp;
                        break;
                    }
                }

                // Look rightwards. We subtract 1 from delta as we need to take into account
                // that a tab has been removed and if we don't do this we'll miss the tab
                // to the right of the tab (as it has moved into the removed tabs position).
                int upPos = tabIndex + delta - 1;
                if (upPos < size) {
                    Tab tmp = getModelItem(upPos);
                    if (tmp != null) {
                        bestTab = tmp;
                        break;
                    }
                }

                if (downPos < 0 && upPos >= size) {
                    break;
                }

                delta++;
            }

            if (bestTab != null) {
                select(bestTab);
            }
        }

        @SuppressWarnings("all")
        protected void dispose() {
            tabLine.getTabs().removeListener(tabsListener);
            tabLine = null;
        }
    }
}
