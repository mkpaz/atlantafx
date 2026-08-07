/* SPDX-License-Identifier: MIT */

package atlantafx.validation.actions;

import java.util.List;

/**
 * Represents an action that groups multiple actions into a single unit.
 *
 * <p>Executing this action delegates the call to each underlying action in sequence.
 */
public interface CompositeAction extends Action {

    /**
     * Returns the list of underlying actions contained in this composite action.
     */
    List<Action> unwrap();
}