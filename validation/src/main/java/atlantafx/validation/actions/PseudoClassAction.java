/* SPDX-License-Identifier: MIT */

package atlantafx.validation.actions;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import atlantafx.validation.Descriptor;
import atlantafx.validation.Failure;

import java.util.List;

/**
 * An action that toggles a pseudo-class on target nodes.
 *
 * <p>This action enables the pseudo-class when validation fails and disables it when
 * the failure is cleared. If {@code inverted} is set to {@code true}, the behavior is reversed.
 *
 * @param pseudoClass the pseudo-class to toggle
 * @param nodes       the target nodes to manage
 * @param inverted    whether to invert the pseudo-class behavior
 */
public record PseudoClassAction(PseudoClass pseudoClass, List<Node> nodes, boolean inverted) implements Action {

    public PseudoClassAction {
        nodes = List.copyOf(nodes);
    }

    /**
     * Constructs a {@code PseudoClassAction} for the specified nodes with default
     * (non-inverted) behavior.
     *
     * @param pseudoClass the pseudo-class to toggle
     * @param nodes       the target nodes to manage
     */
    public PseudoClassAction(PseudoClass pseudoClass, List<Node> nodes) {
        this(pseudoClass, nodes, false);
    }

    /**
     * Constructs a {@code PseudoClassAction} for the specified nodes with default
     * (non-inverted) behavior.
     *
     * @param pseudoClass the pseudo-class to toggle
     * @param nodes       the target nodes to manage
     */
    public PseudoClassAction(PseudoClass pseudoClass, Node... nodes) {
        this(pseudoClass, List.of(nodes), false);
    }

    /**
     * Constructs a {@code PseudoClassAction} for the specified nodes with explicit
     * inversion settings.
     *
     * @param pseudoClass the pseudo-class to toggle
     * @param nodes       the target nodes to manage
     * @param inverted    whether to invert the pseudo-class behavior
     */
    public PseudoClassAction(PseudoClass pseudoClass, boolean inverted, Node... nodes) {
        this(pseudoClass, List.of(nodes), inverted);
    }

    @Override
    public void apply(Failure failure) {
        if (inverted()) {
            Action.disablePseudoClass(pseudoClass(), nodes());
        } else {
            Action.enablePseudoClass(pseudoClass(), nodes());
        }
    }

    @Override
    public void clear(Descriptor descriptor) {
        if (inverted()) {
            Action.enablePseudoClass(pseudoClass(), nodes());
        } else {
            Action.disablePseudoClass(pseudoClass(), nodes());
        }
    }
}