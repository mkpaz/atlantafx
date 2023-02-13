/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2014, 2020, ControlsFX
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package atlantafx.base.controls;

import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Region;
import javafx.util.Callback;

/**
 * Represents a bread crumb bar. This control is useful to visualize and navigate
 * a hierarchical path structure, such as file systems.
 *
 * <p>A breadcrumbs consist of two types of elements: a button (default is a hyperlink)
 * and a divider (default is for Label). You can customize both by providing the
 * corresponding control factory.
 */
@SuppressWarnings("unused")
public class Breadcrumbs<T> extends Control {

    protected static final String DEFAULT_STYLE_CLASS = "breadcrumbs";

    protected final Callback<BreadCrumbItem<T>, ButtonBase> defaultCrumbNodeFactory =
        item -> new Hyperlink(item.getStringValue());
    protected final Callback<BreadCrumbItem<T>, ? extends Node> defaultDividerFactory =
        item -> item != null && !item.isLast() ? new Label("/") : null;

    /**
     * Creates an empty bread crumb bar.
     */
    public Breadcrumbs() {
        this(null);
    }

    /**
     * Creates a bread crumb bar with the given BreadCrumbItem as the currently
     * selected crumb.
     */
    public Breadcrumbs(BreadCrumbItem<T> selectedCrumb) {
        getStyleClass().add(DEFAULT_STYLE_CLASS);

        // breadcrumbs should be the size of its content
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        setMaxWidth(Region.USE_PREF_SIZE);
        setPrefHeight(Region.USE_COMPUTED_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);

        setSelectedCrumb(selectedCrumb);
        setCrumbFactory(defaultCrumbNodeFactory);
        setDividerFactory(defaultDividerFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new BreadcrumbsSkin<>(this);
    }

    /**
     * Construct a tree model from the flat list which then can be set
     * as selectedCrumb node to be shown.
     */
    @SafeVarargs
    public static <T> BreadCrumbItem<T> buildTreeModel(T... crumbs) {
        BreadCrumbItem<T> subRoot = null;
        for (T crumb : crumbs) {
            BreadCrumbItem<T> currentNode = new BreadCrumbItem<>(crumb);
            if (subRoot != null) {
                subRoot.getChildren().add(currentNode);
            }
            subRoot = currentNode;
        }
        return subRoot;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents the bottom-most path node (the node on the most-right side in
     * terms of the bread crumb bar). The full path is then being constructed
     * using getParent() of the tree-items.
     *
     * <p>Consider the following hierarchy:
     * [Root] &gt; [Folder] &gt; [SubFolder] &gt; [file.txt]
     *
     * <p>To show the above bread crumb bar, you have to set the [file.txt] tree-node as selected crumb.
     */
    public final ObjectProperty<BreadCrumbItem<T>> selectedCrumbProperty() {
        return selectedCrumb;
    }

    protected final ObjectProperty<BreadCrumbItem<T>> selectedCrumb =
        new SimpleObjectProperty<>(this, "selectedCrumb");

    public final BreadCrumbItem<T> getSelectedCrumb() {
        return selectedCrumb.get();
    }

    public final void setSelectedCrumb(BreadCrumbItem<T> selectedCrumb) {
        this.selectedCrumb.set(selectedCrumb);
    }

    /**
     * Enables or disables auto navigation (default is enabled).
     * If auto navigation is enabled, it will automatically navigate to the crumb which was
     * clicked by the user.
     *
     * @return a {@link BooleanProperty}
     */
    public final BooleanProperty autoNavigationEnabledProperty() {
        return autoNavigation;
    }

    protected final BooleanProperty autoNavigation =
        new SimpleBooleanProperty(this, "autoNavigationEnabled", true);

    public final boolean isAutoNavigationEnabled() {
        return autoNavigation.get();
    }

    public final void setAutoNavigationEnabled(boolean enabled) {
        autoNavigation.set(enabled);
    }

    /**
     * Crumb factory is used to create custom bread crumb instances.
     * <code>null</code> is not allowed and will result in a fallback to the default factory.
     *
     * <p><code>BreadCrumbItem&lt;T&gt;</code> specifies the tree item for creating bread crumb. Use
     * {@link BreadCrumbItem#isFirst()} and {@link BreadCrumbItem#isLast()} to create bread crumb
     * depending on item position.
     *
     * <p><code>ButtonBase</code> stands for resulting bread crumb node. It CAN NOT be <code>null</code>.
     */
    public final ObjectProperty<Callback<BreadCrumbItem<T>, ButtonBase>> crumbFactoryProperty() {
        return crumbFactory;
    }

    protected final ObjectProperty<Callback<BreadCrumbItem<T>, ButtonBase>> crumbFactory =
        new SimpleObjectProperty<>(this, "crumbFactory");

    public final void setCrumbFactory(Callback<BreadCrumbItem<T>, ButtonBase> value) {
        if (value == null) {
            value = defaultCrumbNodeFactory;
        }
        crumbFactoryProperty().set(value);
    }

    public final Callback<BreadCrumbItem<T>, ButtonBase> getCrumbFactory() {
        return crumbFactory.get();
    }

    /**
     * Divider factory is used to create custom divider instances.
     * <code>null</code> is not allowed and will result in a fallback to the default factory.
     *
     * <p><code>BreadCrumbItem&lt;T&gt;</code> specifies the preceding tree item. It can be null, because this way
     * you can insert divider before the first bread crumb, which can be used e.g. for creating a Unix path.
     * Use {@link BreadCrumbItem#isFirst()} and {@link BreadCrumbItem#isLast()} to create divider
     * depending on item position.
     *
     * <p><code>? extends Node</code> stands for resulting divider node. It CAN be <code>null</code>, which
     * means there will be no divider inserted after the specified bread crumb.
     */
    public final ObjectProperty<Callback<BreadCrumbItem<T>, ? extends Node>> dividerFactoryProperty() {
        return dividerFactory;
    }

    protected final ObjectProperty<Callback<BreadCrumbItem<T>, ? extends Node>> dividerFactory =
        new SimpleObjectProperty<>(this, "dividerFactory");

    public final void setDividerFactory(Callback<BreadCrumbItem<T>, ? extends Node> value) {
        if (value == null) {
            value = defaultDividerFactory;
        }
        dividerFactoryProperty().set(value);
    }

    public final Callback<BreadCrumbItem<T>, ? extends Node> getDividerFactory() {
        return dividerFactory.get();
    }

    /**
     * The EventHandler is called when a user selects a bread crumb.
     */
    public final ObjectProperty<EventHandler<BreadCrumbActionEvent<T>>> onCrumbActionProperty() {
        return onCrumbAction;
    }

    /**
     * Set a new EventHandler for when a user selects a crumb.
     */
    public final void setOnCrumbAction(EventHandler<BreadCrumbActionEvent<T>> value) {
        onCrumbActionProperty().set(value);
    }

    public final EventHandler<BreadCrumbActionEvent<T>> getOnCrumbAction() {
        return onCrumbActionProperty().get();
    }

    protected final ObjectProperty<EventHandler<BreadCrumbActionEvent<T>>> onCrumbAction = new ObjectPropertyBase<>() {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        protected void invalidated() {
            setEventHandler(BreadCrumbActionEvent.CRUMB_ACTION, (EventHandler<BreadCrumbActionEvent>) (Object) get());
        }

        @Override
        public Object getBean() {
            return Breadcrumbs.this;
        }

        @Override
        public String getName() {
            return "onCrumbAction";
        }
    };

    ///////////////////////////////////////////////////////////////////////////

    public static class BreadCrumbItem<T> extends TreeItem<T> {

        // setters must not be exposed to user
        private boolean first;
        private boolean last;

        public BreadCrumbItem(T value) {
            super(value);
        }

        public boolean isFirst() {
            return first;
        }

        void setFirst(boolean first) {
            this.first = first;
        }

        public boolean isLast() {
            return last;
        }

        void setLast(boolean last) {
            this.last = last;
        }

        String getStringValue() {
            return getValue() != null ? getValue().toString() : "";
        }
    }

    /**
     * Represents an Event which is fired when a bread crumb was activated.
     */
    public static class BreadCrumbActionEvent<T> extends Event {

        /**
         * The event type that should be listened to by people interested in
         * knowing when the {@link Breadcrumbs#selectedCrumbProperty() selected crumb}
         * has changed.
         */
        public static final EventType<BreadCrumbActionEvent<?>> CRUMB_ACTION
            = new EventType<>("CRUMB_ACTION" + UUID.randomUUID());

        private final BreadCrumbItem<T> selectedCrumb;

        /**
         * Creates a new event that can subsequently be fired.
         */
        public BreadCrumbActionEvent(BreadCrumbItem<T> selectedCrumb) {
            super(CRUMB_ACTION);
            this.selectedCrumb = selectedCrumb;
        }

        public BreadCrumbItem<T> getSelectedCrumb() {
            return selectedCrumb;
        }
    }
}
