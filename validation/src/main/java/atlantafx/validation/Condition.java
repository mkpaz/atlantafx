/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents a predicate condition with optional boolean negation.
 *
 * <p>Note: Two {@code Condition} instances are considered equal if their underlying
 * predicates are equal, regardless of their {@code negated} flag state.
 *
 * @param <T>       the type of the value to test
 * @param predicate the predicate to evaluate
 * @param negated   whether to invert the evaluation result
 */
public record Condition<T extends @Nullable Object>(Predicate<? super @Nullable T> predicate,
                                                    boolean negated) {

    /**
     * Constructs a {@code Condition} with default (non-negated) behavior.
     *
     * @param predicate the predicate to evaluate
     */
    public Condition(Predicate<? super @Nullable T> predicate) {
        this(predicate, false);
    }

    /**
     * Evaluates this condition against the given value.
     *
     * @param value the value to test
     * @return {@code true} if the evaluation succeeds, taking negation into account
     */
    public boolean test(T value) {
        boolean result = predicate.test(value);
        return negated != result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Condition<?> that)) {
            return false;
        }
        return Objects.equals(predicate, that.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(predicate);
    }
}