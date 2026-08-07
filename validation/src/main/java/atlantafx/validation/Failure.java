/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents an unsuccessful validation result that contains one or more violations.
 *
 * <p>A failure can represent a single validation result ({@link Single}) or a collection
 * of validation results ({@link Composite}).
 */
public sealed interface Failure extends Result permits Failure.Single, Failure.Composite {

    /** A blank failure result with an empty name and no attributes. */
    Single<?> EMPTY = new Single<>(Descriptor.EMPTY, null, List.of());

    /**
     * Joins all non-null violation messages ({@link Violation#message()}) into a single string
     * separated by line breaks.
     */
    Function<Failure, String> ALL_VIOLATIONS = failure -> failure.violations().stream()
        .map(Violation::message)
        .filter(Objects::nonNull)
        .collect(Collectors.joining("\n"));

    /**
     * Returns all violations contained in this failure.
     *
     * @return a list of violations
     */
    List<Violation> violations();

    @Override
    default boolean valid() {
        return false;
    }

    /**
     * Extracts the first non-null {@link Violation#message()}, or returns an empty string
     * if there are no violations or valid messages.
     */
    default String message() {
        return violations().stream()
            .map(Violation::message)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");
    }

    /**
     * Represents a validation failure.
     *
     * @param <T>            the type of the value that was validated
     * @param descriptor     the descriptor identifying the rule or rule set that produced this result
     * @param attemptedValue the value that failed validation
     * @param violations     a list of violations associated with the failure
     */
    record Single<T extends @Nullable Object>(Descriptor descriptor,
                                              @Nullable T attemptedValue,
                                              List<Violation> violations) implements Failure {
    }

    /**
     * Represents a group of validation failures.
     *
     * @param descriptor the descriptor identifying the rule or rule set that produced a validation result
     * @param failures   the list of individual failures in this group
     */
    record Composite(Descriptor descriptor, List<Failure> failures) implements Failure {

        @Override
        public List<Violation> violations() {
            return failures.stream()
                .flatMap(f -> f.violations().stream())
                .toList();
        }

        /**
         * Checks whether this composite failure contains any child failures.
         */
        public boolean isEmpty() {
            return failures.isEmpty();
        }

        /**
         * Returns a single failure at the specified index.
         *
         * @param <T>   the expected type of the attempted value
         * @param index the index of the failure to retrieve, or an empty optional if not found
         */
        @SuppressWarnings("unchecked")
        public <T extends @Nullable Object> Optional<Single<T>> get(int index) {
            return (index >= 0 && index < failures.size() && failures.get(index) instanceof Single<?> single)
                ? Optional.of((Single<T>) single)
                : Optional.empty();
        }

        /**
         * Finds a single failure matching the specified descriptor name.
         *
         * @param <T>  the expected type of the attempted value
         * @param name the name of the descriptor to search for
         */
        @SuppressWarnings("unchecked")
        public <T extends @Nullable Object> Optional<Single<T>> get(String name) {
            return failures.stream()
                .filter(f -> f instanceof Single<?> && Objects.equals(f.name(), name))
                .map(f -> (Single<T>) f)
                .findFirst();
        }
    }
}