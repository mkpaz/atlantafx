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

import atlantafx.base.controls.Breadcrumbs.BreadCrumbItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import org.jspecify.annotations.Nullable;

/**
 * The default skin for the {@link Breadcrumbs} control.
 */
public class BreadcrumbsSkin<T> extends SkinBase<Breadcrumbs<T>> {

    protected static final PseudoClass FIRST = PseudoClass.getPseudoClass("first");
    protected static final PseudoClass LAST = PseudoClass.getPseudoClass("last");

    protected final EventHandler<TreeModificationEvent<Object>> treeChildrenModifiedHandler =
        e -> updateBreadCrumbs();

    public BreadcrumbsSkin(final Breadcrumbs<T> control) {
        super(control);

        control.selectedCrumbProperty().addListener(
            (obs, old, val) -> updateSelectedPath(old, val)
        );
        updateSelectedPath(getSkinnable().selectedCrumbProperty().get(), null);
    }

    @Override
    protected void layoutChildren(double x, double y, double width, double height) {
        double controlHeight = getSkinnable().getHeight();
        double nodeX = x, nodeY;

        for (int i = 0; i < getChildren().size(); i++) {
            Node node = getChildren().get(i);

            double nodeWidth = snapSizeX(node.prefWidth(height));
            double nodeHeight = snapSizeY(node.prefHeight(-1));

            // center node within the breadcrumbs
            nodeY = nodeHeight < controlHeight ? (controlHeight - nodeHeight) / 2 : y;

            node.resizeRelocate(nodeX, nodeY, nodeWidth, nodeHeight);
            nodeX += nodeWidth;
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset,
                                     double bottomInset, double leftInset) {
        double width = 0;
        for (Node node : getChildren()) {
            if (!node.isManaged()) {
                continue;
            }
            width += snapSizeX(node.prefWidth(height));
        }

        return width + rightInset + leftInset;
    }

    protected void updateSelectedPath(@Nullable BreadCrumbItem<T> old, @Nullable BreadCrumbItem<T> val) {
        if (old != null) {
            old.removeEventHandler(BreadCrumbItem.childrenModificationEvent(), treeChildrenModifiedHandler);
        }
        if (val != null) {
            val.addEventHandler(BreadCrumbItem.childrenModificationEvent(), treeChildrenModifiedHandler);
        }
        updateBreadCrumbs();
    }

    protected void updateBreadCrumbs() {
        getChildren().clear();
        BreadCrumbItem<T> selectedTreeItem = getSkinnable().getSelectedCrumb();
        Node divider;
        if (selectedTreeItem != null) {
            // optionally insert divider before the first node
            divider = createDivider(null);
            if (divider != null) {
                divider.pseudoClassStateChanged(FIRST, true);
                divider.pseudoClassStateChanged(LAST, false);
                getChildren().add(divider);
            }

            List<BreadCrumbItem<T>> crumbs = constructFlatPath(selectedTreeItem);
            for (BreadCrumbItem<T> treeItem : crumbs) {
                ButtonBase crumb = createCrumb(treeItem);
                crumb.pseudoClassStateChanged(FIRST, treeItem.isFirst());
                crumb.pseudoClassStateChanged(LAST, treeItem.isLast());
                getChildren().add(crumb);

                // for the sake of flexibility, it's user responsibility to decide
                // whether insert divider after the last node or not
                divider = createDivider(treeItem);
                if (divider != null) {
                    if (treeItem.isLast()) {
                        divider.pseudoClassStateChanged(FIRST, false);
                        divider.pseudoClassStateChanged(LAST, true);
                    }
                    getChildren().add(divider);
                }
            }
        }
    }

    /**
     * Construct a flat list for the crumbs.
     *
     * @param bottomMost The crumb node at the end of the path
     */
    protected List<BreadCrumbItem<T>> constructFlatPath(BreadCrumbItem<T> bottomMost) {
        List<BreadCrumbItem<T>> path = new ArrayList<>();

        BreadCrumbItem<T> current = bottomMost;
        do {
            path.add(current);
            current.setFirst(false);
            current.setLast(false);
            current = (BreadCrumbItem<T>) current.getParent();
        } while (current != null);

        Collections.reverse(path);

        // if the path consists of a single item it considered as first, but not last
        if (path.size() > 0) {
            path.get(0).setFirst(true);
        }
        if (path.size() > 1) {
            path.get(path.size() - 1).setLast(true);
        }

        return path;
    }

    protected ButtonBase createCrumb(BreadCrumbItem<T> treeItem) {
        ButtonBase crumb = getSkinnable().getCrumbFactory().call(treeItem);
        crumb.setMnemonicParsing(false);

        if (!crumb.getStyleClass().contains("crumb")) {
            crumb.getStyleClass().add("crumb");
        }

        // listen to the action event of each bread crumb
        crumb.setOnAction(e -> onBreadCrumbAction(treeItem));

        return crumb;
    }

    protected @Nullable Node createDivider(@Nullable BreadCrumbItem<T> treeItem) {
        Node divider = getSkinnable().getDividerFactory().call(treeItem);
        if (divider == null) {
            return null;
        }

        if (!divider.getStyleClass().contains("divider")) {
            divider.getStyleClass().add("divider");
        }

        return divider;
    }

    /**
     * Occurs when a bread crumb gets the action event.
     *
     * @param crumbModel The crumb which received the action event
     */
    protected void onBreadCrumbAction(BreadCrumbItem<T> crumbModel) {
        final Breadcrumbs<T> breadCrumbBar = getSkinnable();

        // fire the composite event in the breadCrumbBar
        Event.fireEvent(breadCrumbBar, new Breadcrumbs.BreadCrumbActionEvent<>(crumbModel));

        // navigate to the clicked crumb
        if (breadCrumbBar.isAutoNavigationEnabled()) {
            breadCrumbBar.setSelectedCrumb(crumbModel);
        }
    }
}
