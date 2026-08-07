/* SPDX-License-Identifier: MIT */

package atlantafx.validation.actions;

import javafx.scene.Node;
import atlantafx.validation.Descriptor;
import atlantafx.validation.Failure;

import java.util.List;

/**
 * An action that manages the visibility of target nodes.
 *
 * <p>This action hides the nodes and removes them from layout calculations
 * ({@code visible = false}, {@code managed = false}) when validation fails,
 * and restores them when cleared. If {@code inverted} is set to {@code true},
 * the behavior is reversed.
 *
 * @param nodes    the target nodes to manage
 * @param inverted whether to invert the visibility behavior
 */
public record VisibleAction(List<Node> nodes, boolean inverted) implements Action {

    public VisibleAction {
        nodes = List.copyOf(nodes);
    }

    /**
     * Constructs a {@code VisibleAction} for the specified nodes with default
     * (non-inverted) behavior.
     *
     * @param nodes the target nodes to manage
     */
    public VisibleAction(List<Node> nodes) {
        this(nodes, false);
    }

    /**
     * Constructs a {@code VisibleAction} for the specified nodes with default
     * (non-inverted) behavior.
     *
     * @param nodes the target nodes to manage
     */
    public VisibleAction(Node... nodes) {
        this(List.of(nodes), false);
    }

    /**
     * Constructs a {@code VisibleAction} for the specified nodes with explicit inversion settings.
     *
     * @param nodes    the target nodes to manage
     * @param inverted whether to invert the visibility behavior
     */
    public VisibleAction(boolean inverted, Node... nodes) {
        this(List.of(nodes), inverted);
    }

    @Override
    public void apply(Failure failure) {
        setNodesState(inverted());
    }

    @Override
    public void clear(Descriptor descriptor) {
        setNodesState(!inverted());
    }

    private void setNodesState(boolean visible) {
        for (var node : nodes) {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }
}