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

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Callback;

import java.util.UUID;

/**
 * Represents a bread crumb bar. This control is useful to visualize and navigate
 * a hierarchical path structure, such as file systems.
 */
@SuppressWarnings("unused")
public class Breadcrumbs<T> extends Control {

    private static final String STYLE_CLASS_FIRST = "first";

    /** Represents an Event which is fired when a bread crumb was activated. */
    public static class BreadCrumbActionEvent<TE> extends Event {

        /**
         * The event type that should be listened to by people interested in
         * knowing when the {@link Breadcrumbs#selectedCrumbProperty() selected crumb}
         * has changed.
         */
        public static final EventType<BreadCrumbActionEvent<?>> CRUMB_ACTION
                = new EventType<>("CRUMB_ACTION" + UUID.randomUUID());

        private final TreeItem<TE> selectedCrumb;

        /** Creates a new event that can subsequently be fired. */
        public BreadCrumbActionEvent(TreeItem<TE> selectedCrumb) {
            super(CRUMB_ACTION);
            this.selectedCrumb = selectedCrumb;
        }

        /** Returns the crumb which was the action target. */
        public TreeItem<TE> getSelectedCrumb() {
            return selectedCrumb;
        }
    }

    /**
     * Construct a tree model from the flat list which then can be set
     * as selectedCrumb node to be shown
     */
    @SafeVarargs
    public static <T> TreeItem<T> buildTreeModel(T... crumbs) {
        TreeItem<T> subRoot = null;
        for (T crumb : crumbs) {
            TreeItem<T> currentNode = new TreeItem<>(crumb);
            if (subRoot != null) {
                subRoot.getChildren().add(currentNode);
            }
            subRoot = currentNode;
        }
        return subRoot;
    }

    /** Default crumb node factory. This factory is used when no custom factory is specified by the user. */
    private final Callback<TreeItem<T>, Button> defaultCrumbNodeFactory =
            crumb -> new BreadCrumbButton(crumb.getValue() != null ? crumb.getValue().toString() : "");

    /** Creates an empty bread crumb bar. */
    public Breadcrumbs() {
        this(null);
    }

    /**
     * Creates a bread crumb bar with the given TreeItem as the currently
     * selected crumb.
     */
    public Breadcrumbs(TreeItem<T> selectedCrumb) {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        setSelectedCrumb(selectedCrumb);
        setCrumbFactory(defaultCrumbNodeFactory);
    }

    /**
     * Represents the bottom-most path node (the node on the most-right side in
     * terms of the bread crumb bar). The full path is then being constructed
     * using getParent() of the tree-items.
     *
     * <p>
     * Consider the following hierarchy:
     * [Root] &gt; [Folder] &gt; [SubFolder] &gt; [file.txt]
     * <p>
     * To show the above bread crumb bar, you have to set the [file.txt] tree-node as selected crumb.
     */
    public final ObjectProperty<TreeItem<T>> selectedCrumbProperty() {
        return selectedCrumb;
    }

    private final ObjectProperty<TreeItem<T>> selectedCrumb =
            new SimpleObjectProperty<>(this, "selectedCrumb");

    /** Get the current target path. */
    public final TreeItem<T> getSelectedCrumb() {
        return selectedCrumb.get();
    }

    /** Select one node in the BreadCrumbBar for being the bottom-most path node. */
    public final void setSelectedCrumb(TreeItem<T> selectedCrumb) {
        this.selectedCrumb.set(selectedCrumb);
    }

    /**
     * Enable or disable auto navigation (default is enabled).
     * If auto navigation is enabled, it will automatically navigate to the crumb which was clicked by the user.
     *
     * @return a {@link BooleanProperty}
     */
    public final BooleanProperty autoNavigationEnabledProperty() {
        return autoNavigation;
    }

    private final BooleanProperty autoNavigation =
            new SimpleBooleanProperty(this, "autoNavigationEnabled", true);

    /**
     * Return whether auto-navigation is enabled.
     *
     * @return whether auto-navigation is enabled.
     */
    public final boolean isAutoNavigationEnabled() {
        return autoNavigation.get();
    }

    /**
     * Enable or disable auto navigation (default is enabled).
     * If auto navigation is enabled, it will automatically navigate to the crumb which was clicked by the user.
     */
    public final void setAutoNavigationEnabled(boolean enabled) {
        autoNavigation.set(enabled);
    }

    /**
     * Return an ObjectProperty of the CrumbFactory.
     *
     * @return an ObjectProperty of the CrumbFactory.
     */
    public final ObjectProperty<Callback<TreeItem<T>, Button>> crumbFactoryProperty() {
        return crumbFactory;
    }

    private final ObjectProperty<Callback<TreeItem<T>, Button>> crumbFactory =
            new SimpleObjectProperty<>(this, "crumbFactory");

    /**
     * Sets the crumb factory to create (custom) {@link BreadCrumbButton} instances.
     * <code>null</code> is not allowed and will result in a fallback to the default factory.
     */
    public final void setCrumbFactory(Callback<TreeItem<T>, Button> value) {
        if (value == null) {
            value = defaultCrumbNodeFactory;
        }
        crumbFactoryProperty().set(value);
    }

    /**
     * Returns the cell factory that will be used to create {@link BreadCrumbButton}
     * instances
     */
    public final Callback<TreeItem<T>, Button> getCrumbFactory() {
        return crumbFactory.get();
    }

    /**
     * @return an ObjectProperty representing the crumbAction EventHandler being used.
     */
    public final ObjectProperty<EventHandler<BreadCrumbActionEvent<T>>> onCrumbActionProperty() {
        return onCrumbAction;
    }

    /** Set a new EventHandler for when a user selects a crumb. */
    public final void setOnCrumbAction(EventHandler<BreadCrumbActionEvent<T>> value) {
        onCrumbActionProperty().set(value);
    }

    /**
     * Return the EventHandler currently used when a user selects a crumb.
     *
     * @return the EventHandler currently used when a user selects a crumb.
     */
    public final EventHandler<BreadCrumbActionEvent<T>> getOnCrumbAction() {
        return onCrumbActionProperty().get();
    }

    private final ObjectProperty<EventHandler<BreadCrumbActionEvent<T>>> onCrumbAction = new ObjectPropertyBase<>() {

        @SuppressWarnings({ "unchecked", "rawtypes" })
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

    private static final String DEFAULT_STYLE_CLASS = "bread-crumb-bar";

    /** {@inheritDoc} */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new BreadcrumbsSkin<>(this);
    }

    @SuppressWarnings("FieldCanBeLocal")
    public static class BreadCrumbButton extends Button {

        private final ObjectProperty<Boolean> first = new SimpleObjectProperty<>(this, STYLE_CLASS_FIRST);

        private final double arrowWidth = 5;
        private final double arrowHeight = 20;

        /**
         * Create a BreadCrumbButton
         *
         * @param text Buttons text
         */
        public BreadCrumbButton(String text) {
            this(text, null);
        }

        /**
         * Create a BreadCrumbButton
         *
         * @param text Buttons text
         * @param gfx  Gfx of the Button
         */
        public BreadCrumbButton(String text, Node gfx) {
            super(text, gfx);
            first.set(false);

            getStyleClass().addListener((InvalidationListener) obs -> updateShape());

            updateShape();
        }

        private void updateShape() {
            this.setShape(createButtonShape());
        }

        /** Returns the crumb arrow width. */
        public double getArrowWidth() {
            return arrowWidth;
        }

        /** Creates an arrow path. */
        private Path createButtonShape() {
            // build the following shape (or home without left arrow)

            //   --------
            //  \         \
            //  /         /
            //   --------
            Path path = new Path();

            // begin in the upper left corner
            MoveTo e1 = new MoveTo(0, 0);
            path.getElements().add(e1);

            // draw a horizontal line that defines the width of the shape
            HLineTo e2 = new HLineTo();
            // bind the width of the shape to the width of the button
            e2.xProperty().bind(this.widthProperty().subtract(arrowWidth));
            path.getElements().add(e2);

            // draw upper part of right arrow
            LineTo e3 = new LineTo();
            // the x endpoint of this line depends on the x property of line e2
            e3.xProperty().bind(e2.xProperty().add(arrowWidth));
            e3.setY(arrowHeight / 2.0);
            path.getElements().add(e3);

            // draw lower part of right arrow
            LineTo e4 = new LineTo();
            // the x endpoint of this line depends on the x property of line e2
            e4.xProperty().bind(e2.xProperty());
            e4.setY(arrowHeight);
            path.getElements().add(e4);

            // draw lower horizontal line
            HLineTo e5 = new HLineTo(0);
            path.getElements().add(e5);

            if (!getStyleClass().contains(STYLE_CLASS_FIRST)) {
                // draw lower part of left arrow
                // we simply can omit it for the first Button
                LineTo e6 = new LineTo(arrowWidth, arrowHeight / 2.0);
                path.getElements().add(e6);
            } else {
                // draw an arc for the first bread crumb
                ArcTo arcTo = new ArcTo();
                arcTo.setSweepFlag(true);
                arcTo.setX(0);
                arcTo.setY(0);
                arcTo.setRadiusX(15.0f);
                arcTo.setRadiusY(15.0f);
                path.getElements().add(arcTo);
            }

            // close path
            ClosePath e7 = new ClosePath();
            path.getElements().add(e7);

            // this is a dummy color to fill the shape, it won't be visible
            path.setFill(Color.BLACK);

            return path;
        }
    }
}
