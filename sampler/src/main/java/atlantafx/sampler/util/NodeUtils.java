/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.util;

import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public final class NodeUtils {

    public static void toggleVisibility(Node node, boolean on) {
        node.setVisible(on);
        node.setManaged(on);
    }

    public static void setAnchors(Node node, Insets insets) {
        if (insets.getTop() >= 0) {
            AnchorPane.setTopAnchor(node, insets.getTop());
        }
        if (insets.getRight() >= 0) {
            AnchorPane.setRightAnchor(node, insets.getRight());
        }
        if (insets.getBottom() >= 0) {
            AnchorPane.setBottomAnchor(node, insets.getBottom());
        }
        if (insets.getLeft() >= 0) {
            AnchorPane.setLeftAnchor(node, insets.getLeft());
        }
    }

    public static void setScrollConstraints(ScrollPane scrollPane,
                                            ScrollPane.ScrollBarPolicy vbarPolicy, boolean fitHeight,
                                            ScrollPane.ScrollBarPolicy hbarPolicy, boolean fitWidth) {
        scrollPane.setVbarPolicy(vbarPolicy);
        scrollPane.setFitToHeight(fitHeight);
        scrollPane.setHbarPolicy(hbarPolicy);
        scrollPane.setFitToWidth(fitWidth);
    }

    public static boolean isDoubleClick(MouseEvent e) {
        return e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2;
    }

    public static <T> T getChildByIndex(Parent parent, int index, Class<T> contentType) {
        List<Node> children = parent.getChildrenUnmodifiable();
        if (index < 0 || index >= children.size()) {
            return null;
        }
        Node node = children.get(index);
        return contentType.isInstance(node) ? contentType.cast(node) : null;
    }

    public static boolean isDescendant(Node ancestor, Node descendant) {
        if (ancestor == null) {
            return true;
        }

        while (descendant != null) {
            if (descendant == ancestor) {
                return true;
            }
            descendant = descendant.getParent();
        }
        return false;
    }
}
