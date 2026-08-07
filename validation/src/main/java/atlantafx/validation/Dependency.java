/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import javafx.beans.value.ObservableValue;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Represents a dependency on an external {@link ObservableValue} for a validation rule.
 *
 * @param <D>       the type of the dependency's value
 * @param property  the observable property this dependency tracks
 * @param condition the predicate that must be true for the dependency to be met
 */
public record Dependency<D>(ObservableValue<@Nullable D> property,
                            Predicate<? super @Nullable D> condition) {

    /**
     * Tests if the condition is met with the dependency's current value.
     *
     * @return true if the condition is met, false otherwise
     */
    public boolean test() {
        return condition().test(property.getValue());
    }
}
