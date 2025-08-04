/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.shim.event.EventHandlerManager;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.event.*;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Tab implements EventTarget, Styleable {

    protected static final String DEFAULT_STYLE_CLASS = "tab";

    /**
     * Called when the tab becomes selected or unselected.
     */
    public static final EventType<Event> SELECTION_CHANGED_EVENT = new EventType<>(
        // this and the following event names MUST be different from used by the TabPane
        // otherwise these two controls can't co-exist in the same scene-graph
        Event.ANY, "TAB_LINE$SELECTION_CHANGED_EVENT"
    );

    /**
     * Called when a user closes this tab.
     */
    public static final EventType<Event> CLOSED_EVENT = new EventType<>(
        Event.ANY, "TAB_LINE$TAB_CLOSED"
    );

    /**
     * Called when there is an external request to close this {@code Tab}.
     * The installed event handler thus can prevent tab closing by consuming the received event.
     */
    public static final EventType<Event> TAB_CLOSE_REQUEST_EVENT = new EventType<>(
        Event.ANY, "TAB_LINE$TAB_CLOSE_REQUEST_EVENT"
    );

    /**
     * Creates an empty tab.
     */
    public Tab() {
        this(null, null, null);
    }

    /**
     * See {@link #Tab(String, String, Node)}.
     */
    public Tab(@Nullable String text) {
        this(null, text, null);
    }

    /**
     * See {@link #Tab(String, String, Node)}.
     */
    public Tab(@Nullable String id, @Nullable String text) {
        this(id, text, null);
    }

    /**
     * Creates a tab.
     *
     * @param id      the id of the tab
     * @param text    the title of the tab
     * @param graphic the graphic for the tab
     */
    public Tab(@Nullable String id, @Nullable String text, @Nullable Node graphic) {
        setId(id);
        setText(text);
        setGraphic(graphic);
        styleClass.addAll(DEFAULT_STYLE_CLASS);
    }

    //=========================================================================
    // Properties
    //=========================================================================

    /**
     * The simple string identifier for finding a specific tab.
     * The default value is {@code null}.
     */
    public final StringProperty idProperty() {
        if (id == null) {
            id = new SimpleStringProperty(this, "id");
        }
        return id;
    }

    protected @Nullable StringProperty id;

    @Override
    public @Nullable String getId() {
        return id != null ? id.get() : null;
    }

    public void setId(@Nullable String value) {
        idProperty().set(value);
    }

    /**
     * The CSS style string associated to this tab.
     */
    public final StringProperty styleProperty() {
        if (style == null) {
            style = new SimpleStringProperty(this, "style");
        }
        return style;
    }

    protected @Nullable StringProperty style;

    @Override
    public @Nullable String getStyle() {
        return style != null ? style.get() : null;
    }

    public void setStyle(@Nullable String value) {
        styleProperty().set(value);
    }

    /**
     * The currently selected tab.
     */
    public final ReadOnlyBooleanProperty selectedProperty() {
        return selectedPropertyImpl().getReadOnlyProperty();
    }

    protected @Nullable ReadOnlyBooleanWrapper selected;

    protected ReadOnlyBooleanWrapper selectedPropertyImpl() {
        if (selected == null) {
            selected = new ReadOnlyBooleanWrapper() {
                @Override
                protected void invalidated() {
                    if (getOnSelectionChanged() != null) {
                        Event.fireEvent(Tab.this, new Event(SELECTION_CHANGED_EVENT));
                    }
                }

                @Override
                public Object getBean() {
                    return Tab.this;
                }

                @Override
                public String getName() {
                    return "selected";
                }
            };
        }
        return selected;
    }

    public boolean isSelected() {
        return selected != null && selected.get();
    }

    // Tabs can only be selected using the SelectionModel.
    // Use a different name to avoid confusion with the typically public setter.
    protected void updateSelected(boolean value) {
        selectedPropertyImpl().set(value);
    }

    /**
     * The text to show in the tab to allow the user to differentiate between
     * the function of each tab. The text is always visible.
     */
    public final StringProperty textProperty() {
        if (text == null) {
            text = new SimpleStringProperty(this, "text");
        }
        return text;
    }

    protected @Nullable StringProperty text;

    public @Nullable String getText() {
        return text != null ? text.get() : null;
    }

    public void setText(@Nullable String value) {
        textProperty().set(value);
    }

    /**
     * The graphic to show in the tab to allow the user to differentiate
     * between the function of each tab.
     */
    public final ObjectProperty<@Nullable Node> graphicProperty() {
        if (graphic == null) {
            graphic = new SimpleObjectProperty<>(this, "graphic");
        }
        return graphic;
    }

    protected @Nullable ObjectProperty<@Nullable Node> graphic;

    public @Nullable Node getGraphic() {
        return graphic != null ? graphic.get() : null;
    }

    public void setGraphic(@Nullable Node value) {
        graphicProperty().set(value);
    }

    /**
     * The context menu associated with the tab.
     */
    public final ObjectProperty<@Nullable ContextMenu> contextMenuProperty() {
        if (contextMenu == null) {
            contextMenu = new SimpleObjectProperty<>(this, "contextMenu");
        }
        return contextMenu;
    }

    protected @Nullable ObjectProperty<@Nullable ContextMenu> contextMenu;

    public @Nullable ContextMenu getContextMenu() {
        return contextMenu != null ? contextMenu.get() : null;
    }

    public void setContextMenu(@Nullable ContextMenu value) {
        contextMenuProperty().set(value);
    }

    /**
     * The pinned state for this tab.
     * The default is {@code false}.
     */
    public final BooleanProperty pinnedProperty() {
        if (pinned == null) {
            pinned = new SimpleBooleanProperty(this, "pinned", false) {
                @Override
                protected void invalidated() {
                    if (getTabLine() != null && !getTabLine().getTabClosingPolicy().canPin(Tab.this)) {
                        setPinned(false);
                    }
                    super.invalidated();
                }
            };
        }
        return pinned;
    }

    protected @Nullable BooleanProperty pinned;

    public boolean isPinned() {
        return pinned != null && pinned.get();
    }

    public void setPinned(boolean value) {
        pinnedProperty().set(value);
    }

    /**
     * The event handler that is associated with a selection on the tab.
     */
    public final ObjectProperty<@Nullable EventHandler<Event>> onSelectionChangedProperty() {
        if (onSelectionChanged == null) {
            onSelectionChanged = new ObjectPropertyBase<>() {
                @Override
                protected void invalidated() {
                    setEventHandler(SELECTION_CHANGED_EVENT, get());
                }

                @Override
                public Object getBean() {
                    return Tab.this;
                }

                @Override
                public String getName() {
                    return "onSelectionChanged";
                }
            };
        }
        return onSelectionChanged;
    }

    protected @Nullable ObjectProperty<@Nullable EventHandler<Event>> onSelectionChanged;

    public @Nullable EventHandler<Event> getOnSelectionChanged() {
        return onSelectionChanged != null ? onSelectionChanged.get() : null;
    }

    public void setOnSelectionChanged(@Nullable EventHandler<Event> value) {
        onSelectionChangedProperty().set(value);
    }

    /**
     * The event handler that is associated with the tab when the tab is closed.
     */
    public final ObjectProperty<@Nullable EventHandler<Event>> onClosedProperty() {
        if (onClosed == null) {
            onClosed = new ObjectPropertyBase<>() {
                @Override
                protected void invalidated() {
                    setEventHandler(CLOSED_EVENT, get());
                }

                @Override
                public Object getBean() {
                    return Tab.this;
                }

                @Override
                public String getName() {
                    return "onClosed";
                }
            };
        }
        return onClosed;
    }

    protected @Nullable ObjectProperty<@Nullable EventHandler<Event>> onClosed;

    public @Nullable EventHandler<Event> getOnClosed() {
        return onClosed != null ? onClosed.get() : null;
    }

    public void setOnClosed(@Nullable EventHandler<Event> value) {
        onClosedProperty().set(value);
    }

    /**
     * The tooltip associated with this tab.
     */
    public final ObjectProperty<@Nullable Tooltip> tooltipProperty() {
        if (tooltip == null) {
            tooltip = new SimpleObjectProperty<>(this, "tooltip");
        }
        return tooltip;
    }

    protected @Nullable ObjectProperty<@Nullable Tooltip> tooltip;

    public @Nullable Tooltip getTooltip() {
        return tooltip != null ? tooltip.getValue() : null;
    }

    public void setTooltip(@Nullable Tooltip value) {
        tooltipProperty().setValue(value);
    }

    /**
     * The event handler that is called when there is an external request to close this tab.
     * The installed event handler can prevent tab closing by consuming the received event.
     */
    public final ObjectProperty<@Nullable EventHandler<Event>> onCloseRequestProperty() {
        if (onCloseRequest == null) {
            onCloseRequest = new ObjectPropertyBase<>() {
                @Override
                protected void invalidated() {
                    setEventHandler(TAB_CLOSE_REQUEST_EVENT, get());
                }

                @Override
                public Object getBean() {
                    return Tab.this;
                }

                @Override
                public String getName() {
                    return "onCloseRequest";
                }
            };
        }
        return onCloseRequest;
    }

    protected @Nullable ObjectProperty<@Nullable EventHandler<Event>> onCloseRequest;

    public @Nullable EventHandler<Event> getOnCloseRequest() {
        if (onCloseRequest == null) {
            return null;
        }
        return onCloseRequest.get();
    }

    public void setOnCloseRequest(@Nullable EventHandler<Event> value) {
        onCloseRequestProperty().set(value);
    }

    /**
     * The TabLine that contains this tab.
     */
    public final ReadOnlyObjectProperty<@Nullable TabLine> tabLineProperty() {
        return tabLinePropertyImpl().getReadOnlyProperty();
    }

    private @Nullable ReadOnlyObjectWrapper<@Nullable TabLine> tabLine;

    private ReadOnlyObjectWrapper<@Nullable TabLine> tabLinePropertyImpl() {
        if (tabLine == null) {
            tabLine = new ReadOnlyObjectWrapper<>(this, "tabLine");
        }
        return tabLine;
    }

    public @Nullable TabLine getTabLine() {
        return tabLine != null ? tabLine.get() : null;
    }

    protected void updateTabLine(@Nullable TabLine value) {
        tabLinePropertyImpl().set(value);
    }

    //=========================================================================
    // User Data
    //=========================================================================

    protected static final Object USER_DATA_KEY = new Object();

    // a map containing a set of properties for this Tab
    protected @Nullable ObservableMap<Object, Object> properties;

    /**
     * Returns an observable map of properties on this Tab for use primarily
     * by application developers.
     */
    public final ObservableMap<Object, Object> getProperties() {
        if (properties == null) {
            properties = FXCollections.observableMap(new HashMap<>());
        }
        return properties;
    }

    /**
     * Tests if this Tab has properties.
     */
    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }

    /**
     * Returns a previously set Object property, or null if no such property
     * has been set using the {@link Tab#setUserData(Object)} method.
     */
    public Object getUserData() {
        return getProperties().get(USER_DATA_KEY);
    }

    /**
     * Convenience method for setting a single Object property that can be
     * retrieved at a later date. This is functionally equivalent to calling
     * the getProperties().put(Object key, Object value) method. This can later
     * be retrieved by calling {@link Tab#getUserData()}.
     */
    public void setUserData(Object value) {
        getProperties().put(USER_DATA_KEY, value);
    }

    //=========================================================================
    // Events
    //=========================================================================

    protected final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain chain) {
        return chain.prepend(eventHandlerManager);
    }

    @Override
    public final <E extends Event> void addEventHandler(EventType<E> eventType,
                                                        EventHandler<? super E> eventHandler) {
        eventHandlerManager.addEventHandler(eventType, eventHandler);
    }

    @Override
    public final <E extends Event> void removeEventHandler(EventType<E> eventType,
                                                           EventHandler<? super E> eventHandler) {
        eventHandlerManager.removeEventHandler(eventType, eventHandler);
    }

    @Override
    public final <E extends Event> void addEventFilter(EventType<E> eventType,
                                                       EventHandler<? super E> eventFilter) {
        eventHandlerManager.addEventFilter(eventType, eventFilter);
    }

    @Override
    public final <E extends Event> void removeEventFilter(EventType<E> eventType,
                                                          EventHandler<? super E> eventFilter) {
        eventHandlerManager.removeEventFilter(eventType, eventFilter);
    }

    protected <E extends Event> void setEventHandler(EventType<E> eventType,
                                                     @Nullable EventHandler<E> eventHandler) {
        eventHandlerManager.setEventHandler(eventType, eventHandler);
    }

    //=========================================================================
    // Stylesheet Handling
    //=========================================================================

    protected final ObservableList<String> styleClass = FXCollections.observableArrayList();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeSelector() {
        return "Tab";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getStyleClass() {
        return styleClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ObservableSet<PseudoClass> getPseudoClassStates() {
        return FXCollections.emptyObservableSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Styleable getStyleableParent() {
        return getTabLine();
    }

    /**
     * Gets the {@code CssMetaData} associated with this class,
     * which may include the {@code CssMetaData} of its superclasses.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return Collections.emptyList();
    }

    //=========================================================================
    // Support classes
    //=========================================================================

    /**
     * Describes the drag policies for tabs.
     */
    public enum DragPolicy {

        /**
         * The tabs remain fixed in their positions and cannot be dragged.
         */
        FIXED,

        /**
         * The tabs can be dragged to reorder them.
         */
        REORDER
    }

    /**
     * Describes the closing policies for tabs.
     */
    public interface ClosingPolicy {

        /**
         * All tabs can be closed.
         */
        ClosingPolicy ALL_TABS = new AllTabsClosingPolicy();

        /**
         * Only the selected tab can be closed.
         */
        ClosingPolicy SELECTED_TAB = new SelectedTabClosingPolicy();

        /**
         * Tabs cannot be closed.
         */
        ClosingPolicy NO_TABS = new NoTabsClosingPolicy();

        /**
         * Specifies whether a tab can be closed.
         *
         * @param tab the tab to check
         * @return true if the tab can be closed; false otherwise
         */
        boolean canClose(Tab tab);

        /**
         * Specifies whether a tab can be pinned.
         *
         * @param tab the tab to check
         * @return true if the tab can be pinned; false otherwise
         */
        boolean canPin(Tab tab);

        /**
         * Returns the style class to be set to the {@link TabsContainer} for this closing policy.
         *
         * @return the style class as a String
         */
        String getStyleClass();
    }

    public static class AllTabsClosingPolicy implements ClosingPolicy {

        @Override
        public boolean canClose(Tab tab) {
            return true;
        }

        @Override
        public boolean canPin(Tab tab) {
            return true;
        }

        @Override
        public String getStyleClass() {
            return "all-tabs-closable";
        }
    }

    public static class SelectedTabClosingPolicy implements ClosingPolicy {

        @Override
        public boolean canClose(Tab tab) {
            return tab.isSelected();
        }

        @Override
        public boolean canPin(Tab tab) {
            return true;
        }

        @Override
        public String getStyleClass() {
            return "selected-tab-closable";
        }
    }

    public static class NoTabsClosingPolicy implements ClosingPolicy {

        @Override
        public boolean canClose(Tab tab) {
            return false;
        }

        @Override
        public boolean canPin(Tab tab) {
            return false;
        }

        @Override
        public String getStyleClass() {
            return "no-tabs-closable";
        }
    }

    /**
     * The {@link ResizePolicy} interface defines the strategy for calculating the width of tabs
     * in the {@link TabLine} control. Implementations of this interface are responsible for
     * determining the width of each tab based on the available space.
     */
    public interface ResizePolicy {

        /**
         * Specifies that the tab width should be determined by the
         * {@code -fx-fixed-tab-width} CSS property.
         */
        double USE_FIXED_SIZE = -1;

        /**
         * Specifies that the tab width should be based on the computed content width.
         */
        double USE_COMPUTED_SIZE = Double.NEGATIVE_INFINITY;

        /**
         * See {@link FixedWidthResizePolicy}.
         */
        ResizePolicy FIXED_WIDTH = new FixedWidthResizePolicy();

        /**
         * See {@link ComputedWidthResizePolicy}.
         */
        ResizePolicy COMPUTED_WIDTH = new ComputedWidthResizePolicy();

        /**
         * See {@link AdaptiveResizePolicy}.
         */
        ResizePolicy ADAPTIVE = new AdaptiveResizePolicy();

        /**
         * See {@link StretchingResizePolicy}.
         */
        ResizePolicy STRETCH = new StretchingResizePolicy();

        /**
         * Computes the tab width.
         *
         * @param availableWidth the visible width of the {@link TabLine}
         * @param tabCount       the numbers of tabs in the {@link TabLine}
         * @param isTabsFit      whether the summary tabs width fits the visible width of the {@link TabLine}
         */
        double computePrefWidth(double availableWidth, int tabCount, boolean isTabsFit);

        boolean isScrollable();
    }

    /**
     * Resizes the tabs to a fixed width, requiring the user to scroll horizontally
     * if the total width exceeds the visible width of the tab line.
     * <ul>
     * <li>Each tab has a fixed width.</li>
     * <li>If the content of a tab exceeds the fixed width, it will be clipped.</li>
     * <li>If the total width of the tabs exceeds the visible width of the tab line,
     * the width of each individual tab will not be reduced.</li>
     * <li>The user can scroll horizontally to view specific tabs, with the tab line
     * scrolling accordingly.</li>
     * </ul>
     */
    public static class FixedWidthResizePolicy implements ResizePolicy {

        @Override
        public double computePrefWidth(double availableWidth, int tabCount, boolean isTabsFit) {
            return ResizePolicy.USE_FIXED_SIZE;
        }

        @Override
        public boolean isScrollable() {
            return true;
        }
    }

    /**
     * Resizes the tabs to a computed width, requiring the user to scroll horizontally
     * if the total width exceeds the visible width of the tab line.
     *
     * <ul>
     * <li>Each tab has a computed width equal to the preferred width of its content.</li>
     * <li>The content of each tab is not clipped.</li>
     * <li>If the total width of the tabs exceeds the visible width of the tab line, the width
     * of each individual tab will remain unchanged.</li>
     * <li>The user can scroll horizontally to view specific tabs, with the tab line scrolling accordingly.</li>
     * </ul>
     */
    public static class ComputedWidthResizePolicy implements ResizePolicy {

        @Override
        public double computePrefWidth(double availableWidth, int tabCount, boolean isTabsFit) {
            return ResizePolicy.USE_COMPUTED_SIZE;
        }

        @Override
        public boolean isScrollable() {
            return true;
        }
    }

    /**
     * Resizes the tabs to a fixed width when there is available space; otherwise, it
     * proportionally shrinks the tab width.
     *
     * <ul>
     * <li>Each tab has a fixed width when there is available space.</li>
     * <li>If no space is available, the width of each individual tab will be reduced
     * proportionally based on the number of tabs.</li>
     * </ul>
     */
    public static class AdaptiveResizePolicy implements ResizePolicy {

        @Override
        public double computePrefWidth(double availableWidth, int tabCount, boolean isTabsFit) {
            return isTabsFit
                ? ResizePolicy.USE_FIXED_SIZE
                : availableWidth / tabCount;
        }

        @Override
        public boolean isScrollable() {
            return false;
        }
    }

    /**
     * Resizes the tabs to fill all available space proportionally.
     *
     * <ul>
     * <li>Each tab's width is proportional to the available width and the number of tabs.</li>
     * <li>If the total preferred width of the tabs exceeds the visible width of the tab line,
     * the width of each individual tab will be reduced proportionally based on the number of tabs.</li>
     * </ul>
     */
    public static class StretchingResizePolicy implements ResizePolicy {

        @Override
        public double computePrefWidth(double availableWidth, int tabCount, boolean isTabsFit) {
            return availableWidth / tabCount;
        }

        @Override
        public boolean isScrollable() {
            return false;
        }
    }
}
