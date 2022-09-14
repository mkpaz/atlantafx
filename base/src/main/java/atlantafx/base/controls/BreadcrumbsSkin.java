/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2014, 2021, ControlsFX
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

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BreadcrumbsSkin<T> extends SkinBase<Breadcrumbs<T>> {

    private static final String STYLE_CLASS_FIRST = "first";
    private static final String STYLE_CLASS_LAST = "last";

    public BreadcrumbsSkin(final Breadcrumbs<T> control) {
        super(control);
        control.selectedCrumbProperty().addListener(selectedPathChangeListener);
        updateSelectedPath(getSkinnable().selectedCrumbProperty().get(), null);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final ChangeListener<TreeItem<T>> selectedPathChangeListener =
            (obs, oldItem, newItem) -> updateSelectedPath(newItem, oldItem);

    private void updateSelectedPath(TreeItem<T> newTarget, TreeItem<T> oldTarget) {
        if (oldTarget != null) {
            // remove old listener
            oldTarget.removeEventHandler(TreeItem.childrenModificationEvent(), treeChildrenModifiedHandler);
        }
        if (newTarget != null) {
            // add new listener
            newTarget.addEventHandler(TreeItem.childrenModificationEvent(), treeChildrenModifiedHandler);
        }
        updateBreadCrumbs();
    }

    private final EventHandler<TreeModificationEvent<Object>> treeChildrenModifiedHandler =
            args -> updateBreadCrumbs();

    private void updateBreadCrumbs() {
        final Breadcrumbs<T> buttonBar = getSkinnable();
        final TreeItem<T> pathTarget = buttonBar.getSelectedCrumb();
        final Callback<TreeItem<T>, Button> factory = buttonBar.getCrumbFactory();

        getChildren().clear();

        if (pathTarget != null) {
            List<TreeItem<T>> crumbs = constructFlatPath(pathTarget);

            for (int i = 0; i < crumbs.size(); i++) {
                Button crumb = createCrumb(factory, crumbs.get(i));
                crumb.setMnemonicParsing(false);

                boolean first = crumbs.size() > 1 && i == 0;
                boolean last = crumbs.size() > 1 && i == crumbs.size() - 1;

                if (first) {
                    crumb.getStyleClass().remove(STYLE_CLASS_LAST);
                    addStyleClass(crumb, STYLE_CLASS_FIRST);
                } else if (last) {
                    crumb.getStyleClass().remove(STYLE_CLASS_FIRST);
                    addStyleClass(crumb, STYLE_CLASS_LAST);
                } else {
                    crumb.getStyleClass().removeAll(STYLE_CLASS_FIRST, STYLE_CLASS_LAST);
                }

                getChildren().add(crumb);
            }
        }
    }

    private void addStyleClass(Node node, String styleClass) {
        if (!node.getStyleClass().contains(styleClass)) {
            node.getStyleClass().add(styleClass);
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        for (int i = 0; i < getChildren().size(); i++) {
            Node n = getChildren().get(i);

            double nw = snapSizeX(n.prefWidth(h));
            double nh = snapSizeY(n.prefHeight(-1));

            if (i > 0) {
                // we have to position the bread crumbs slightly overlapping
                double ins = n instanceof Breadcrumbs.BreadCrumbButton ? ((Breadcrumbs.BreadCrumbButton) n).getArrowWidth() : 0;
                x = snapPositionX(x - ins);
            }

            n.resize(nw, nh);
            n.relocate(x, y);
            x += nw;
        }
    }

    /**
     * Construct a flat list for the crumbs
     *
     * @param bottomMost The crumb node at the end of the path
     */
    private List<TreeItem<T>> constructFlatPath(TreeItem<T> bottomMost) {
        List<TreeItem<T>> path = new ArrayList<>();

        TreeItem<T> current = bottomMost;
        do {
            path.add(current);
            current = current.getParent();
        } while (current != null);

        Collections.reverse(path);
        return path;
    }

    private Button createCrumb(
            final Callback<TreeItem<T>, Button> factory,
            final TreeItem<T> selectedCrumb) {

        Button crumb = factory.call(selectedCrumb);

        crumb.getStyleClass().add("crumb"); //$NON-NLS-1$

        // listen to the action event of each bread crumb
        crumb.setOnAction(ae -> onBreadCrumbAction(selectedCrumb));

        return crumb;
    }

    /**
     * Occurs when a bread crumb gets the action event
     *
     * @param crumbModel The crumb which received the action event
     */
    protected void onBreadCrumbAction(final TreeItem<T> crumbModel) {
        final Breadcrumbs<T> breadCrumbBar = getSkinnable();

        // fire the composite event in the breadCrumbBar
        Event.fireEvent(breadCrumbBar, new Breadcrumbs.BreadCrumbActionEvent<>(crumbModel));

        // navigate to the clicked crumb
        if (breadCrumbBar.isAutoNavigationEnabled()) {
            breadCrumbBar.setSelectedCrumb(crumbModel);
        }
    }
}
