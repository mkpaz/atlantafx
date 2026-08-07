/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Defines an interface to trigger validation and observe its outcome.
 *
 * @param <T> the type of the value to be validated
 */
public sealed interface Validator<T extends @Nullable Object> permits
    RuleCheckBuilder, RuleOptionsBuilder, Rule {

    String SUPPRESS_INIT_CHECK_PROPERTY = "atlantafx.validation.suppressInitCheck";

    /**
     * Returns the observable validation result.
     */
    ReadOnlyObjectProperty<Result> resultProperty();

    /**
     * Triggers a manual re-validation.
     */
    Result revalidate();

    /**
     * Returns the validation result.
     */
    default Result result() {
        return resultProperty().get();
    }

    /**
     * Creates an observable boolean binding that is {@code true} when the validation
     * result is valid.
     */
    default ObservableValue<Boolean> observeValid() {
        return Bindings.createBooleanBinding(() -> result().valid(), resultProperty());
    }

    /**
     * Creates an observable boolean binding that is {@code true} when the validation
     * result is invalid.
     */
    default ObservableValue<Boolean> observeInvalid() {
        return Bindings.createBooleanBinding(() -> result().invalid(), resultProperty());
    }

    /**
     * Registers a callback to be executed whenever the validation result changes.
     *
     * @param consumer the action to run on result change
     * @return a subscription that can be used to unsubscribe
     */
    default Subscription subscribe(Consumer<Result> consumer) {
        return resultProperty().subscribe(consumer);
    }

    /**
     * Downcasts this validator to a concrete {@link Rule}.
     *
     * @return the rule instance
     */
    default Rule<T> asRule() {
        return switch (this) {
            case Rule<T> r -> r;
        };
    }
}
