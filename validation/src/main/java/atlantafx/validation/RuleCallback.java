/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import atlantafx.validation.actions.Action;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A staging builder interface for defining optional callbacks of a validation rule.
 *
 * @param <T> The type of the value being validated.
 */
public sealed interface RuleCallback<T, F extends Failure> permits RuleOptionsBuilder, RuleSet {

    /**
     * Sets a handler to be run when this rule passes validation or is skipped.
     *
     * @param handler the handler to run
     */
    T onSuccess(Consumer<Result.Success> handler);

    /**
     * Sets a handler to be run when an exception occurs during validation.
     *
     * @param handler the handler to run
     */
    T onFailure(@Nullable Consumer<F> handler);

    /**
     * Sets a handler to be run after validation completes, regardless of the validation result.
     *
     * @param handler the handler to run
     */
    T onException(Consumer<Result.Abort> handler);

    /**
     * Sets a handler to be run after validation completes, regardless of the validation result.
     *
     * @param handler the handler to run
     */
    T doFinally(Consumer<Descriptor> handler);

    /**
     * Sets an action to be executed when validation completes with either success or failure.
     *
     * @param action the action to perform
     */
    @SuppressWarnings("unchecked")
    default T onValidated(Action action) {
        this.onSuccess(r -> action.clear(r.descriptor()));
        this.onFailure(action::apply);
        return (T) this;
    }

    /**
     * Sets a composite action to be executed when validation completes with either success or failure.
     *
     * @param actions the composite action to perform
     */
    default T onValidated(Action... actions) {
        return onValidated(Action.combine(actions));
    }
}