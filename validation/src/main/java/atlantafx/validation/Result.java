/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

/**
 * Represents the result of validation.
 *
 * <p>A result can be a {@link Success}, a {@link Failure}, or an {@link Abort}
 * if an exception occurred during evaluation.
 */
public sealed interface Result permits
    Result.Success, Failure, Result.Abort, Result.Initial {

    /**
     * Returns the descriptor identifying the rule or rule set that produced this result.
     *
     * @return the result descriptor
     */
    Descriptor descriptor();

    /**
     * Returns the name of the rule or rule set from the underlying descriptor.
     *
     * @return the rule or rule set name
     */
    default String name() {
        return descriptor().name();
    }

    /**
     * Returns whether the validation was successful.
     *
     * @return {@code true} if validation passed, {@code false} otherwise
     */
    boolean valid();

    /**
     * Returns whether the validation failed without being aborted.
     *
     * @return {@code true} if validation failed normally, {@code false} if it succeeded or aborted
     */
    default boolean invalid() {
        return !aborted() && !valid();
    }

    /**
     * Returns whether the validation result is initial.
     *
     * @return {@code true} if validation was performed, {@code false} otherwise
     */
    default boolean initial() {
        return this instanceof Initial;
    }

    /**
     * Returns whether the validation was aborted due to an exception.
     *
     * @return {@code true} if validation was aborted, {@code false} otherwise
     */
    default boolean aborted() {
        return this instanceof Abort;
    }

    //*************************************************************************

    /**
     * Represents an initial validator state.
     *
     * @param descriptor the descriptor identifying the rule or rule set that produced this result
     * @param valid      the initial validation result
     */
    record Initial(Descriptor descriptor, boolean valid) implements Result { }

    /**
     * Represents a validation success.
     *
     * @param descriptor the descriptor identifying the rule or rule set that produced this result
     */
    record Success(Descriptor descriptor) implements Result {

        /** A success result with an empty name and no attributes. */
        public static final Success EMPTY = new Success(Descriptor.EMPTY);

        @Override
        public boolean valid() {
            return true;
        }
    }

    /**
     * Represents an aborted validation result, produced when an exception
     * is thrown during rule evaluation instead of a normal success or failure.
     *
     * @param descriptor the descriptor identifying the rule or rule set that produced this result;
     *                   when the exception occurred within a rule that is part of a {@code RuleSet},
     *                   this identifies the enclosing rule set, not the individual rule
     * @param ruleName   the name of the specific rule in which the exception occurred
     * @param exception  the exception that caused validation to abort
     */
    record Abort(Descriptor descriptor,
                 Exception exception,
                 String ruleName) implements Result {

        @Override
        public boolean valid() {
            return false;
        }
    }
}
