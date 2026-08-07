/* SPDX-License-Identifier: MIT */

package atlantafx.validation.actions;

import javafx.scene.Node;
import atlantafx.validation.Descriptor;
import atlantafx.validation.Failure;

import java.util.List;

/**
 * An action that manages a style class on target nodes.
 *
 * <p>This action adds the style class when validation fails and removes it when cleared.
 * If {@code inverted} is set to {@code true}, the behavior is reversed.
 *
 * @param styleClass the name of the style class
 * @param nodes      the target nodes to manage
 * @param inverted   whether to invert the style class management behavior
 */
public record StyleClassAction(String styleClass, List<Node> nodes, boolean inverted) implements Action {

    public StyleClassAction {
        nodes = List.copyOf(nodes);
    }

    /**
     * Constructs a {@code StyleClassAction} for the specified nodes with default
     * (non-inverted) behavior.
     *
     * @param styleClass the name of the style class
     * @param nodes      the target nodes to manage
     */
    public StyleClassAction(String styleClass, List<Node> nodes) {
        this(styleClass, nodes, false);
    }

    /**
     * Constructs a {@code StyleClassAction} for the specified nodes with default
     * (non-inverted) behavior.
     *
     * @param styleClass the name of the style class
     * @param nodes      the target nodes to manage
     */
    public StyleClassAction(String styleClass, Node... nodes) {
        this(styleClass, List.of(nodes), false);
    }

    /**
     * Constructs a {@code StyleClassAction} for the specified nodes with explicit
     * inversion settings.
     *
     * @param styleClass the name of the style class
     * @param nodes      the target nodes to manage
     * @param inverted   whether to invert the style class management behavior
     */
    public StyleClassAction(String styleClass, boolean inverted, Node... nodes) {
        this(styleClass, List.of(nodes), inverted);
    }

    @Override
    public void apply(Failure failure) {
        if (inverted()) {
            Action.removeStylesClass(styleClass(), nodes());
        } else {
            Action.addStylesClass(styleClass(), nodes());
        }
    }

    @Override
    public void clear(Descriptor descriptor) {
        if (inverted()) {
            Action.addStylesClass(styleClass(), nodes());
        } else {
            Action.removeStylesClass(styleClass(), nodes());
        }
    }
}