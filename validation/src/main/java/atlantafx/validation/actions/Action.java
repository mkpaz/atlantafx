/* SPDX-License-Identifier: MIT */

package atlantafx.validation.actions;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import atlantafx.validation.Descriptor;
import atlantafx.validation.Failure;

import java.util.ArrayList;
import java.util.List;

/**
 * Responds to changes in validation state.
 *
 * <p>An action handles a {@link Failure} when validation fails and cleans up when the failure
 * is cleared. Implementations can update UI components or trigger background logic like logging.
 */
public interface Action {

    /**
     * An action that does nothing.
     */
    Action EMPTY = new Action() {
        @Override
        public void apply(Failure failure) { }

        @Override
        public void clear(Descriptor descriptor) { }
    };

    /**
     * Applies a validation failure.
     *
     * @param failure the details of the failure to handle
     */
    void apply(Failure failure);

    /**
     * Clears a previously applied failure.
     *
     * @param descriptor the descriptor identifying the rule or rule set that produced a validation result
     */
    void clear(Descriptor descriptor);

    //*************************************************************************

    /**
     * Combines multiple actions into a single action.
     *
     * <p>The returned action executes all provided actions sequentially.
     * Nested composite actions are flattened.
     *
     * @param actions the actions to combine
     * @return a single action that executes all given actions, or {@link #EMPTY} if no active actions remain
     */
    static Action combine(Action... actions) {
        if (actions.length == 0) {
            return EMPTY;
        }
        if (actions.length == 1) {
            return actions[0];
        }

        var result = new ArrayList<Action>(actions.length);
        for (var action : actions) {
            if (action == EMPTY) {
                continue;
            }
            if (action instanceof CompositeAction composite) {
                result.addAll(composite.unwrap());
            } else {
                result.add(action);
            }
        }

        if (result.isEmpty()) {
            return EMPTY;
        }
        if (result.size() == 1) {
            return result.getFirst();
        }

        record CompositeActionImpl(List<Action> unwrap) implements CompositeAction {

            @Override
            public void apply(Failure failure) {
                for (var action : unwrap()) {
                    action.apply(failure);
                }
            }

            @Override
            public void clear(Descriptor descriptor) {
                for (var action : unwrap()) {
                    action.clear(descriptor);
                }
            }
        }

        return new CompositeActionImpl(List.copyOf(result));
    }

    /**
     * Adds a style class to the target nodes.
     *
     * @param styleClass the name of the style class
     * @param nodes      the nodes to update
     */
    static void addStylesClass(String styleClass, Node... nodes) {
        for (var node : nodes) {
            if (node.getStyleClass().contains(styleClass)) {
                continue;
            }
            node.getStyleClass().add(styleClass);
        }
    }

    /**
     * Adds a style class to the target nodes.
     *
     * @param styleClass the name of the style class
     * @param nodes      the nodes to update
     */
    static void addStylesClass(String styleClass, List<Node> nodes) {
        for (var node : nodes) {
            if (node.getStyleClass().contains(styleClass)) {
                continue;
            }
            node.getStyleClass().add(styleClass);
        }
    }

    /**
     * Removes a style class from the target nodes.
     *
     * @param styleClass the name of the style class
     * @param nodes      the nodes to update
     */
    static void removeStylesClass(String styleClass, Node... nodes) {
        for (var node : nodes) {
            node.getStyleClass().remove(styleClass);
        }
    }

    /**
     * Removes a style class from the target nodes.
     *
     * @param styleClass the name of the style class
     * @param nodes      the nodes to update
     */
    static void removeStylesClass(String styleClass, List<Node> nodes) {
        for (var node : nodes) {
            node.getStyleClass().remove(styleClass);
        }
    }

    /**
     * Enables a pseudo-class for the target nodes.
     *
     * @param pseudoClass the pseudo-class to turn on
     * @param nodes       the nodes to update
     */
    static void enablePseudoClass(PseudoClass pseudoClass, Node... nodes) {
        for (var node : nodes) {
            node.pseudoClassStateChanged(pseudoClass, true);
        }
    }

    /**
     * Enables a pseudo-class for the target nodes.
     *
     * @param pseudoClass the pseudo-class to turn on
     * @param nodes       the nodes to update
     */
    static void enablePseudoClass(PseudoClass pseudoClass, List<Node> nodes) {
        for (var node : nodes) {
            node.pseudoClassStateChanged(pseudoClass, true);
        }
    }

    /**
     * Disables a pseudo-class for the target nodes.
     *
     * @param pseudoClass the pseudo-class to turn off
     * @param nodes       the nodes to update
     */
    static void disablePseudoClass(PseudoClass pseudoClass, Node... nodes) {
        for (var node : nodes) {
            node.pseudoClassStateChanged(pseudoClass, false);
        }
    }

    /**
     * Disables a pseudo-class for the target nodes.
     *
     * @param pseudoClass the pseudo-class to turn off
     * @param nodes       the nodes to update
     */
    static void disablePseudoClass(PseudoClass pseudoClass, List<Node> nodes) {
        for (var node : nodes) {
            node.pseudoClassStateChanged(pseudoClass, false);
        }
    }
}
