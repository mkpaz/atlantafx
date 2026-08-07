/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import javafx.beans.value.ObservableValue;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Predicate;

/**
 * A staging builder interface for defining optional configurations for a validation rule.
 *
 * @param <T> the type of the value being validated
 */
public sealed interface RuleOptionsBuilder<T extends @Nullable Object>
    extends Validator<T>, RuleCallback<RuleOptionsBuilder<T>, Failure.Single<T>>
    permits Rule, RuleCheckBuilder {

    /**
     * Configures the rule to revalidate immediately whenever its source property changes.
     */
    RuleOptionsBuilder<T> immediate();

    /**
     * Sets the cascade behavior for checks within the rule.
     *
     * @param cascade the cascade behavior
     */
    RuleOptionsBuilder<T> cascade(Cascade cascade);

    /**
     * Specifies other rules that should be revalidated after this one.
     *
     * @param rules the child rules
     */
    RuleOptionsBuilder<T> childRules(Validator<?>... rules);

    /**
     * Adds a precondition that must be met for this rule to be evaluated.
     *
     * @param condition the precondition
     */
    RuleOptionsBuilder<T> given(Condition<T> condition);

    /**
     * Associates a custom key-value attribute with this rule.
     *
     * <p>Attributes are carried alongside the validation result and are exposed
     * to {@link RuleCallback} handlers (e.g. {@code onSuccess}, {@code onFailure})
     * via the metadata object passed to them. They do not affect validation
     * logic and can be used for diagnostics, logging, correlation with external
     * systems, or any other cross-cutting concern.
     *
     * @param key   the attribute key
     * @param value the attribute value
     */
    RuleOptionsBuilder<T> attribute(String key, @Nullable Object value);

    /**
     * Associates multiple custom key-value attributes with this rule at once.
     *
     * <p>Equivalent to calling {@link #attribute(String, Object)} for each entry
     * in the given map.
     *
     * @param attributes a map of attributes
     */
    default RuleOptionsBuilder<T> attributes(Map<String, Object> attributes) {
        attributes.forEach(this::attribute);
        return this;
    }

    /**
     * Adds a precondition that must be met for this rule to be evaluated.
     *
     * @param condition the precondition
     */
    default RuleOptionsBuilder<T> given(Predicate<? super @Nullable T> condition) {
        return given(new Condition<>(condition));
    }

    /**
     * Adds a precondition that must NOT be met for this rule to be evaluated.
     *
     * @param condition the negated precondition
     */
    default RuleOptionsBuilder<T> givenNot(Predicate<? super @Nullable T> condition) {
        return given(new Condition<>(condition, true));
    }

    /**
     * Adds a dependency on another observable value. The rule will only be evaluated
     * if the condition on the other value is met.
     *
     * @param obs       the external observable value
     * @param condition the condition to test on the external value
     * @param <D>       the type of the external value
     */
    <D> RuleOptionsBuilder<T> given(ObservableValue<@Nullable D> obs,
                                    Predicate<? super @Nullable D> condition);

    /**
     * Adds a dependency on another observable value. The rule will only be evaluated
     * if the condition on the other value is NOT met.
     *
     * @param obs       the external observable value
     * @param condition the negated condition to test on the external value
     * @param <D>       the type of the external value
     */
    default <D> RuleOptionsBuilder<T> givenNot(ObservableValue<@Nullable D> obs,
                                               Predicate<? super @Nullable D> condition) {
        return given(obs, condition.negate());
    }
}