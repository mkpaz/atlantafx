/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.util;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public final class NodeUtils {

    public static void toggleVisibility(Node node, boolean on) {
        node.setVisible(on);
        node.setManaged(on);
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
