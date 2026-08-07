/* SPDX-License-Identifier: MIT */

package atlantafx.validation.actions;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import atlantafx.validation.Descriptor;
import atlantafx.validation.Failure;

import java.util.Arrays;
import java.util.List;

/**
 * An action that manages the disabled state of properties or nodes.
 *
 * <p>This action sets the target boolean properties to {@code true} when validation fails
 * and restores them to {@code false} when cleared. If {@code inverted} flag is set
 * to {@code true}, the behavior is reversed.
 *
 * @param properties the boolean properties to manage
 * @param inverted   whether to invert the disable behavior
 */
public record DisableAction(List<BooleanProperty> properties, boolean inverted) implements Action {

    public DisableAction {
        properties = List.copyOf(properties);
    }

    /**
     * Constructs a {@code DisableAction} for the specified properties with default
     * (non-inverted) behavior.
     *
     * @param properties the boolean properties to manage
     */
    public DisableAction(List<BooleanProperty> properties) {
        this(properties, false);
    }

    /**
     * Constructs a {@code DisableAction} for the specified properties with default
     * (non-inverted) behavior.
     *
     * @param properties the boolean properties to manage
     */
    public DisableAction(BooleanProperty... properties) {
        this(List.of(properties), false);
    }

    /**
     * Constructs a {@code DisableAction} for the specified properties with explicit
     * inversion settings.
     *
     * @param properties the boolean properties to manage
     * @param inverted   whether to invert the disable behavior
     */
    public DisableAction(boolean inverted, BooleanProperty... properties) {
        this(List.of(properties), inverted);
    }

    /**
     * Constructs a {@code DisableAction} targeting the {@link Node#disableProperty()}
     * of the given nodes.
     *
     * @param nodes the target nodes to manage
     */
    public DisableAction(Node... nodes) {
        this(Arrays.stream(nodes).map(Node::disableProperty).toList(), false);
    }

    @Override
    public void apply(Failure failure) {
        for (var property : properties) {
            property.set(!inverted());
        }
    }

    @Override
    public void clear(Descriptor descriptor) {
        for (var property : properties) {
            property.set(inverted());
        }
    }
}