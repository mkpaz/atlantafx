/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Represents a single validation check.
 *
 * @param <T>       the type of the object to be validated
 * @param must      the condition that must be met for this check to succeed
 * @param code      the optional code to return if {@code must} condition is not met
 * @param message   the optional message to return if {@code must} condition is not met
 * @param condition the optional predicate that determines whether this check should be evaluated
 */
public record Check<T extends @Nullable Object>(Predicate<? super @Nullable T> must,
                                                int code,
                                                @Nullable MessageProvider<T> message,
                                                @Nullable Predicate<? super @Nullable T> condition) {

    /**
     * Tests the given object against the check condition.
     *
     * @param t the object to be tested, which may be null
     * @return true if the object satisfies the check, false otherwise
     */
    public boolean test(@Nullable T t) {
        if (condition != null && !condition.test(t)) {
            return true;
        }
        return must().test(t);
    }

    //*************************************************************************

    /**
     * Checks if a value matches all the given predicates.
     */
    @SafeVarargs
    public static <T> Predicate<@Nullable T> allOf(Predicate<? super T>... predicates) {
        return v -> {
            for (Predicate<? super T> p : predicates) {
                if (!p.test(v)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Checks if a value matches at least one of the given predicates.
     */
    @SafeVarargs
    public static <T> Predicate<@Nullable T> anyOf(Predicate<? super T>... predicates) {
        return v -> {
            for (Predicate<? super T> p : predicates) {
                if (p.test(v)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Checks if a value matches none of the given predicates.
     */
    @SafeVarargs
    public static <T> Predicate<@Nullable T> noneOf(Predicate<? super T>... predicates) {
        return v -> {
            for (Predicate<? super T> p : predicates) {
                if (p.test(v)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Wraps a predicate to evaluate if the input is non-null, yielding {@code true}
     * for {@code null} values.
     *
     * @param condition the predicate to evaluate when the input is present
     * @return {@code true} if the input is {@code null} or satisfies the condition
     */
    public static <V> Predicate<@Nullable V> nullable(Predicate<V> condition) {
        return v -> v == null || condition.test(v);
    }

    /**
     * Wraps a predicate to require non-null input, yielding {@code false} for {@code null} values.
     *
     * @param condition the predicate to evaluate when the input is present
     * @return {@code true} if the input is non-null and satisfies the condition
     */
    public static <V> Predicate<@Nullable V> required(Predicate<V> condition) {
        return v -> v != null && condition.test(v);
    }

    //region OBJECTS
    //*************************************************************************
    public static final class Objects {

        private Objects() {
            // utility
        }

        /**
         * Checks if an object is {@code null}.
         */
        public static <T> Predicate<@Nullable T> isNull() {
            return java.util.Objects::isNull;
        }

        /**
         * Checks if an object is not {@code null}.
         */
        public static <T> Predicate<@Nullable T> isNotNull() {
            return java.util.Objects::nonNull;
        }

        /**
         * Checks if an object is equal to the target value.
         */
        public static <T> Predicate<@Nullable T> isEqual(@Nullable T target) {
            return o -> java.util.Objects.equals(target, o);
        }

        /**
         * Checks if an object is not equal to the target value.
         */
        public static <T> Predicate<@Nullable T> isNotEqual(@Nullable T target) {
            return o -> !java.util.Objects.equals(target, o);
        }

        /**
         * Checks if a value is the same reference as the given object.
         */
        public static <T> Predicate<@Nullable T> isSame(@Nullable T other) {
            return o -> o == other;
        }

        /**
         * Checks if a value is not the same reference as the given object.
         */
        public static <T> Predicate<@Nullable T> isNotSame(@Nullable T other) {
            return o -> o != other;
        }

        /**
         * Checks if a value is equal to one of the given values.
         */
        @SafeVarargs
        public static <T> Predicate<@Nullable T> oneOf(@Nullable T @Nullable... values) {
            return o -> {
                if (o == null || values == null) {
                    return false;
                }
                for (T v : values) {
                    if (java.util.Objects.equals(o, v)) {
                        return true;
                    }
                }
                return false;
            };
        }

        /**
         * Checks if a value is equal to one of the given values.
         */
        public static <T> Predicate<@Nullable T> oneOf(@Nullable Collection<@Nullable T> values) {
            return o -> o != null && values != null && values.contains(o);
        }

        /**
         * Checks if a value is none of the given values.
         */
        @SafeVarargs
        public static <T> Predicate<@Nullable T> noneOf(@Nullable T @Nullable... values) {
            return o -> {
                if (values == null) {
                    return true;
                }
                if (o == null) {
                    return false;
                }
                for (T v : values) {
                    if (java.util.Objects.equals(o, v)) {
                        return false;
                    }
                }
                return true;
            };
        }

        /**
         * Checks if a value is none of the given values.
         */
        public static <T> Predicate<@Nullable T> noneOf(@Nullable Collection<@Nullable T> values) {
            return o -> {
                if (values == null) {
                    return true;
                }
                if (o == null) {
                    return false;
                }
                return !values.contains(o);
            };
        }

        /**
         * Checks if a value is an instance of the given type.
         */
        public static Predicate<@Nullable Object> isInstanceOf(@Nullable Class<?> type) {
            //noinspection PointlessNullCheck
            return o -> o != null && type != null && type.isInstance(o);
        }

        /**
         * Checks if a value is not an instance of the given type.
         */
        public static Predicate<@Nullable Object> isNotInstanceOf(@Nullable Class<?> type) {
            //noinspection PointlessNullCheck
            return o -> o == null || type == null || !type.isInstance(o);
        }
    }
    // endregion

    //region BOOLEANS
    //*************************************************************************

    public static final class Booleans {

        private Booleans() {
            // utility
        }

        /**
         * Checks if a value is {@code true}.
         */
        public static Predicate<@Nullable Boolean> isTrue() {
            return b -> b != null && b;
        }

        /**
         * Checks if a value is {@code false}.
         */
        public static Predicate<@Nullable Boolean> isFalse() {
            return b -> b != null && !b;
        }
    }
    //endregion

    //region COLLECTIONS
    //*************************************************************************

    public static final class Collections {

        private Collections() {
            // utility
        }

        /**
         * Checks if a collection is non-null and contains no elements.
         */
        public static <C extends Collection<?>> Predicate<@Nullable C> isEmpty() {
            return c -> c != null && c.isEmpty();
        }

        /**
         * Checks if a collection is non-null and contains elements.
         */
        public static <C extends Collection<?>> Predicate<@Nullable C> isNotEmpty() {
            return c -> c != null && !c.isEmpty();
        }

        /**
         * Checks if a collection has exactly the given size.
         */
        public static <T> Predicate<@Nullable Collection<T>> hasSize(int size) {
            return c -> c != null && c.size() == size;
        }

        /**
         * Checks if a collection's size is strictly greater than the given size.
         */
        public static <T> Predicate<@Nullable Collection<T>> sizeGreaterThan(int size) {
            return c -> c != null && c.size() > size;
        }

        /**
         * Checks if a collection's size is strictly greater than or equal to the given size.
         */
        public static <T> Predicate<@Nullable Collection<T>> sizeGreaterOrEqual(int size) {
            return c -> c != null && c.size() >= size;
        }

        /**
         * Checks if a collection's size is strictly less than the given size.
         */
        public static <T> Predicate<@Nullable Collection<T>> sizeLessThan(int size) {
            return c -> c != null && c.size() < size;
        }

        /**
         * Checks if a collection's size is strictly less than or equal to the given size.
         */
        public static <T> Predicate<@Nullable Collection<T>> sizeLessOrEqual(int size) {
            return c -> c != null && c.size() <= size;
        }

        /**
         * Checks if a collection's element count is within a specified range.
         *
         * @param min the lower bound (inclusive)
         * @param max the upper bound (exclusive)
         * @return {@code true} if the collection size is within [min, max]
         */
        public static <C extends Collection<?>> Predicate<@Nullable C> sizeInside(int min, int max) {
            return c -> c != null && c.size() >= min && c.size() < max;
        }

        /**
         * Checks if a collection's element count is within a specified range (inclusive).
         *
         * @param min the lower bound (inclusive)
         * @param max the upper bound (inclusive)
         * @return {@code true} if the collection size is within [min, max]
         */
        public static <C extends Collection<?>> Predicate<@Nullable C> sizeBetween(int min, int max) {
            return c -> c != null && c.size() >= min && c.size() <= max;
        }

        /**
         * Checks if all elements in a collection satisfy a condition.
         *
         * @param condition the condition evaluated for each element
         * @return {@code true} if the collection is non-null and every element matches
         */
        public static <E, C extends Collection<E>> Predicate<@Nullable C> allMatch(
            @Nullable Predicate<@Nullable E> condition) {

            return c -> {
                if (c == null || condition == null) return false;
                for (E e : c) {
                    if (!condition.test(e)) {
                        return false;
                    }
                }
                return true;
            };
        }

        /**
         * Checks if at least one element in a collection satisfies a condition.
         *
         * @param condition the condition evaluated against elements
         * @return {@code true} if the collection is non-null and at least one element matches
         */
        public static <E, C extends Collection<E>> Predicate<@Nullable C> anyMatch(
            @Nullable Predicate<@Nullable E> condition) {

            return c -> {
                if (c == null || condition == null) return false;
                for (E e : c) {
                    if (condition.test(e)) {
                        return true;
                    }
                }
                return false;
            };
        }

        /**
         * Checks if no element of a collection matches the given predicate.
         *
         * @param predicate the condition that must not be matched
         * @return {@code true} if no element matches the predicate
         */
        public static <T> Predicate<@Nullable Collection<T>> noneMatch(
            @Nullable Predicate<? super @Nullable T> predicate) {
            return c -> c != null && predicate != null && c.stream().noneMatch(predicate);
        }

        /**
         * Checks if a collection contains the given value.
         *
         * @param value the value that must all be present
         * @return {@code true} if the collection contains value
         */
        public static <T> Predicate<@Nullable Collection<T>> contains(@Nullable T value) {
            return c -> c != null && value != null && c.contains(value);
        }

        /**
         * Checks if a collection contains all the given values.
         *
         * @param values the values that must all be present
         * @return {@code true} if the collection contains every value in values
         */
        public static <T> Predicate<@Nullable Collection<T>> containsAll(@Nullable Collection<T> values) {
            return c -> c != null && values != null && c.containsAll(values);
        }

        /**
         * Checks if a collection contains none of the given values.
         *
         * @param values the values that must not be present
         * @return {@code true} if the collection contains none of the values
         */
        public static <T> Predicate<@Nullable Collection<T>> containsNone(@Nullable Collection<T> values) {
            return c -> c != null && values != null && java.util.Collections.disjoint(c, values);
        }

        /**
         * Checks if all elements of a collection are distinct, by {@link Object#equals(Object)}.
         *
         * @return {@code true} if the collection contains no duplicate elements
         */
        public static <T> Predicate<@Nullable Collection<T>> distinct() {
            return c -> c != null && c.size() == new HashSet<>(c).size();
        }

        /**
         * Checks if all elements of a collection are distinct by the given key,
         * as extracted by {@code extractor}.
         *
         * @param extractor the function used to derive the comparison key for each element
         * @return {@code true} if no two elements produce the same key
         */
        public static <T, K> Predicate<@Nullable Collection<T>> distinct(
            @Nullable Function<? super T, ? extends K> extractor) {
            return c -> {
                if (c == null || extractor == null) {
                    return false;
                }
                Set<K> seen = new HashSet<>();
                for (T t : c) {
                    if (!seen.add(extractor.apply(t))) {
                        return false;
                    }
                }
                return true;
            };
        }
    }
    //endregion

    //region COMPARABLES
    //*************************************************************************

    public static final class Comparables {

        private Comparables() {
            // utility
        }

        /**
         * Checks if a value is equal to another value, by {@link Comparable#compareTo},
         * not by {@link Object#equals(Object)}.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> isEqual(@Nullable T other) {
            return v -> v != null && other != null && v.compareTo(other) == 0;
        }

        /**
         * Checks if a value is not equal to another value, by {@link Comparable#compareTo},
         * not by {@link Object#equals(Object)}.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> isNotEqual(@Nullable T other) {
            return v -> v != null && other != null && v.compareTo(other) != 0;
        }

        /**
         * Checks if a value is strictly between a lower and an upper bound.
         *
         * @param min the lower bound (inclusive)
         * @param max the upper bound (exclusive)
         * @return {@code true} if the value is greater than min and less than max
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> inside(@Nullable T min,
                                                                                      @Nullable T max) {
            return v -> v != null && min != null && max != null && v.compareTo(min) >= 0 && v.compareTo(max) < 0;
        }

        /**
         * Checks if a value is within a lower and an upper bound (inclusive).
         *
         * @param min the lower bound (inclusive)
         * @param max the upper bound (inclusive)
         * @return {@code true} if the value is greater than or equal to min and less than or equal to max
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> between(@Nullable T min,
                                                                                       @Nullable T max) {
            return v -> v != null && min != null && max != null && v.compareTo(min) >= 0 && v.compareTo(max) <= 0;
        }

        /**
         * Checks if a value is strictly greater than a minimum boundary.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> greaterThan(@Nullable T min) {
            return v -> v != null && min != null && v.compareTo(min) > 0;
        }

        /**
         * Checks if a value is greater than or equal to a minimum boundary.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> greaterOrEqual(@Nullable T min) {
            return v -> v != null && min != null && v.compareTo(min) >= 0;
        }

        /**
         * Checks if a value is strictly less than a maximum boundary.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> lessThan(@Nullable T max) {
            return v -> v != null && max != null && v.compareTo(max) < 0;
        }

        /**
         * Checks if a value is less than or equal to a maximum boundary.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> lessOrEqual(@Nullable T max) {
            return v -> v != null && max != null && v.compareTo(max) <= 0;
        }
    }
    // endregion

    //region ENUMS
    //*************************************************************************

    public static final class Enums {

        private Enums() {
            // utility
        }

        /**
         * Checks if a value is equal to one of the given enum constants.
         */
        @SafeVarargs
        public static <T extends Enum<T>> Predicate<@Nullable T> oneOf(T @Nullable... values) {
            return e -> {
                if (e == null || values == null) {
                    return false;
                }
                for (T val : values) {
                    if (e == val) {
                        return true;
                    }
                }
                return false;
            };
        }

        /**
         * Checks if a value is equal to one of the given enum constants.
         */
        public static <T extends Enum<T>> Predicate<@Nullable T> oneOf(@Nullable Collection<T> values) {
            return e -> e != null && values != null && values.contains(e);
        }

        /**
         * Checks if a value's {@link Enum#name()} matches one of the given names.
         */
        public static <T extends Enum<T>> Predicate<@Nullable T> oneOf(String @Nullable... names) {
            return e -> {
                if (e == null || names == null) {
                    return false;
                }
                String s = e.name();
                for (String name : names) {
                    if (java.util.Objects.equals(s, name)) {
                        return true;
                    }
                }
                return false;
            };
        }

        /**
         * Checks if a value is none of the given enum constants.
         */
        @SafeVarargs
        public static <T extends Enum<T>> Predicate<@Nullable T> noneOf(T @Nullable... values) {
            return e -> {
                if (e == null) {
                    return false;
                }
                if (values == null) {
                    return true;
                }
                for (T val : values) {
                    if (e == val) {
                        return false;
                    }
                }
                return true;
            };
        }

        /**
         * Checks if a value is none of the given enum constants.
         */
        public static <T extends Enum<T>> Predicate<@Nullable T> noneOf(@Nullable Collection<T> values) {
            return e -> e != null && (values == null || !values.contains(e));
        }

        /**
         * Checks if a value's {@link Enum#name()} is none of the given names.
         */
        public static <T extends Enum<T>> Predicate<@Nullable T> noneOf(String @Nullable... names) {
            return e -> {
                if (e == null) {
                    return false;
                }
                if (names == null) {
                    return true;
                }
                String eName = e.name();
                for (String name : names) {
                    if (java.util.Objects.equals(eName, name)) {
                        return false;
                    }
                }
                return true;
            };
        }
    }
    //endregion

    //region MAPS
    //*************************************************************************

    public static final class Maps {

        private Maps() {
            // utility
        }

        /**
         * Checks if a map contains the given key.
         */
        public static <K, V> Predicate<@Nullable Map<K, V>> hasKey(@Nullable K key) {
            return map -> map != null && map.containsKey(key);
        }

        /**
         * Checks if a map contains the given value.
         */
        public static <K, V> Predicate<@Nullable Map<K, V>> hasValue(@Nullable V value) {
            return map -> map != null && map.containsValue(value);
        }
    }
    //endregion

    //region NUMBERS
    //*************************************************************************

    public static final class Numbers {

        private Numbers() {
            // utility
        }

        /**
         * Checks if a number is strictly greater than zero.
         */
        public static <T extends Number> Predicate<@Nullable T> isPositive() {
            return n -> n != null && n.doubleValue() > 0;
        }

        /**
         * Checks if a number is strictly less than zero.
         */
        public static <T extends Number> Predicate<@Nullable T> isNegative() {
            return n -> n != null && n.doubleValue() < 0;
        }

        /**
         * Checks if a number is equal to zero.
         */
        public static <T extends Number> Predicate<@Nullable T> isZero() {
            return n -> n != null && n.doubleValue() == 0;
        }

        /**
         * Checks if a number is odd.
         *
         * <p>The value is evaluated via {@link Number#longValue()}, so non-integral types are
         * truncated before the check.
         */
        public static <T extends Number> Predicate<@Nullable T> isOdd() {
            return n -> n != null && n.longValue() % 2 != 0;
        }

        /**
         * Checks if a number is even.
         *
         * <p>The value is evaluated via {@link Number#longValue()}, so non-integral types are
         * truncated before the check.
         */
        public static <T extends Number> Predicate<@Nullable T> isEven() {
            return n -> n != null && n.longValue() % 2 == 0;
        }
    }
    //endregion

    //region OPTIONALS
    //*************************************************************************

    public static final class Optionals {

        private Optionals() {
            // utility
        }

        /**
         * Checks if the optional contains a value.
         */
        public static <T> Predicate<@Nullable Optional<T>> isPresent() {
            return opt -> opt != null && opt.isPresent();
        }

        /**
         * Checks if the optional contains no value.
         */
        @SuppressWarnings("all")
        public static <T> Predicate<@Nullable Optional<@Nullable T>> isEmpty() {
            return val -> val != null && val.isEmpty();
        }

        /**
         * Checks if the optional is present and equal to the given value.
         */
        public static <T> Predicate<@Nullable Optional<@Nullable T>> isEqual(@Nullable T expected) {
            return opt -> opt != null && opt.isPresent() && java.util.Objects.equals(opt.get(), expected);
        }

        /**
         * Checks if the optional is present and not equal to the given value.
         */
        public static <T> Predicate<@Nullable Optional<@Nullable T>> isNotEqual(@Nullable T expected) {
            return opt -> opt != null && opt.isPresent() && !java.util.Objects.equals(opt.get(), expected);
        }

        /**
         * Checks if the optional is present and its value matches the given predicate.
         */
        public static <T> Predicate<@Nullable Optional<@Nullable T>> matches(@Nullable Predicate<? super T> predicate) {
            return opt -> opt != null && opt.isPresent() && predicate != null && predicate.test(opt.get());
        }
    }
    //endregion

    //region STREAMS
    //*************************************************************************

    /**
     * A {@link Stream} can only be consumed once. These predicates are intended
     * for validation calls that produce a fresh stream per check, not for reusing a stream
     * instance across multiple predicates.
     */
    public static final class Streams {

        private Streams() {
            // utility
        }

        /**
         * Checks if the stream has no elements.
         */
        public static <T> Predicate<@Nullable Stream<T>> isEmpty() {
            return s -> s != null && s.findAny().isEmpty();
        }

        /**
         * Checks if the stream has at least one element.
         */
        public static <T> Predicate<@Nullable Stream<T>> isNotEmpty() {
            return s -> s != null && s.findAny().isPresent();
        }

        /**
         * Checks if at least one element of the stream matches the given predicate.
         */
        public static <T> Predicate<@Nullable Stream<T>> anyMatch(
            @Nullable Predicate<? super @Nullable T> predicate) {

            return s -> s != null && predicate != null && s.anyMatch(predicate);
        }

        /**
         * Checks if every element of the stream matches the given predicate.
         */
        public static <T> Predicate<@Nullable Stream<T>> allMatch(
            @Nullable Predicate<? super @Nullable T> predicate) {

            return s -> s != null && predicate != null && s.allMatch(predicate);
        }

        /**
         * Checks if no element of the stream matches the given predicate.
         */
        public static <T> Predicate<@Nullable Stream<T>> noneMatch(
            @Nullable Predicate<? super @Nullable T> predicate) {

            return s -> s != null && predicate != null && s.noneMatch(predicate);
        }

        /**
         * Checks if all elements of the stream are distinct, by {@link Object#equals(Object)}.
         *
         * @return {@code true} if the stream contains no duplicate elements
         */
        public static <T> Predicate<@Nullable Stream<T>> distinct() {
            return s -> {
                if (s == null) {
                    return false;
                }
                Set<T> seen = new HashSet<>();
                return s.allMatch(seen::add);
            };
        }
    }
    //endregion

    //region STRINGS
    //*************************************************************************

    public static final class Strings {

        private Strings() {
            // utility
        }

        /**
         * Checks if a string is either null or empty.
         */
        public static Predicate<@Nullable String> isEmpty() {
            return str -> str == null || str.isEmpty();
        }

        /**
         * Checks if a string is not null and not empty.
         */
        public static Predicate<@Nullable String> isNotEmpty() {
            return str -> str != null && !str.isEmpty();
        }

        /**
         * Checks if a string is empty or contains only whitespace.
         */
        public static Predicate<@Nullable String> isBlank() {
            return str -> str == null || str.isBlank();
        }

        /**
         * Checks if a string is not null, not empty, and contains non-whitespace characters.
         */
        public static Predicate<@Nullable String> isNotBlank() {
            return str -> str != null && !str.isBlank();
        }

        /**
         * Checks equality with a target string ignoring character case.
         */
        public static Predicate<@Nullable String> isEqualAnyCase(@Nullable String target) {
            return str -> str != null && str.equalsIgnoreCase(target);
        }

        /**
         * Checks non-equality with a target string ignoring character case.
         */
        public static Predicate<@Nullable String> isNotEqualAnyCase(@Nullable String target) {
            return str -> str != null && !str.equalsIgnoreCase(target);
        }

        /**
         * Checks if a string contains a specified substring.
         */
        public static Predicate<@Nullable String> contains(@Nullable String substring) {
            return str -> str != null && substring != null && str.contains(substring);
        }

        /**
         * Checks if a string contains the given substring, ignoring case.
         */
        public static Predicate<@Nullable String> containsAnyCase(@Nullable String other) {
            return val -> val != null
                && other != null
                && val.toLowerCase(Locale.ROOT).contains(other.toLowerCase(Locale.ROOT));
        }

        /**
         * Checks if a string starts with a specified prefix.
         */
        public static Predicate<@Nullable String> startsWith(@Nullable String prefix) {
            return str -> str != null && prefix != null && str.startsWith(prefix);
        }

        /**
         * Checks if a string starts with the given prefix, ignoring case.
         */
        public static Predicate<@Nullable String> startsWithAnyCase(@Nullable String prefix) {
            return str -> str != null
                && prefix != null
                && str.regionMatches(true, 0, prefix, 0, prefix.length());
        }

        /**
         * Checks if a string ends with a specified suffix.
         */
        public static Predicate<@Nullable String> endsWith(@Nullable String suffix) {
            return str -> str != null && suffix != null && str.endsWith(suffix);
        }

        /**
         * Checks if a string ends with the given suffix, ignoring case.
         */
        public static Predicate<@Nullable String> endsWithAnyCase(@Nullable String suffix) {
            return str -> str != null
                && suffix != null
                && str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length());
        }

        /**
         * Checks if a string contains no lowercase characters.
         */
        public static Predicate<@Nullable String> isUpperCase() {
            return str -> str != null && str.equals(str.toUpperCase(Locale.ROOT));
        }

        /**
         * Checks if a string contains no uppercase characters.
         */
        public static Predicate<@Nullable String> isLowerCase() {
            return str -> str != null && str.equals(str.toLowerCase(Locale.ROOT));
        }

        /**
         * Checks if a string contains only alphabetic characters.
         */
        public static Predicate<@Nullable String> isAlphabetic() {
            return str -> {
                if (str == null || str.isEmpty()) {
                    return false;
                }
                for (int i = 0; i < str.length(); i++) {
                    if (!Character.isAlphabetic(str.charAt(i))) {
                        return false;
                    }
                }
                return true;
            };
        }

        /**
         * Checks if a string contains only numeric digits (0-9).
         */
        public static Predicate<@Nullable String> isDigits() {
            return str -> {
                if (str == null || str.isEmpty()) {
                    return false;
                }
                for (int i = 0; i < str.length(); i++) {
                    if (!Character.isDigit(str.charAt(i))) {
                        return false;
                    }
                }
                return true;
            };
        }

        /**
         * Checks if a string contains only alphanumeric characters (letters or digits).
         */
        public static Predicate<@Nullable String> isAlphaNumeric() {
            return str -> {
                if (str == null || str.isEmpty()) {
                    return false;
                }
                for (int i = 0; i < str.length(); i++) {
                    if (!Character.isLetterOrDigit(str.charAt(i))) {
                        return false;
                    }
                }
                return true;
            };
        }

        /**
         * Checks if a string can be parsed as a floating-point number.
         */
        public static Predicate<@Nullable String> isNumeric() {
            return str -> {
                if (str == null || str.isEmpty()) {
                    return false;
                }
                try {
                    Double.parseDouble(str);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            };
        }

        /**
         * Checks if a string matches a regular expression.
         */
        public static Predicate<@Nullable String> matches(@Nullable String regex) {
            if (regex == null) {
                return _ -> false;
            }
            Pattern pattern = Pattern.compile(regex);
            return str -> str != null && pattern.matcher(str).matches();
        }

        /**
         * Checks if a string fully matches at least one of the given patterns.
         */
        public static Predicate<@Nullable String> matchesAny(@Nullable Pattern @Nullable... patterns) {
            return str -> str != null
                && patterns != null
                && Arrays.stream(patterns).anyMatch(p -> p != null && p.matcher(str).matches());
        }

        /**
         * Checks if a string matches none of the given patterns.
         */
        public static Predicate<@Nullable String> matchesNone(@Nullable Pattern @Nullable... patterns) {
            return str -> str != null
                && (patterns == null || Arrays.stream(patterns).noneMatch(p -> p != null && p.matcher(str).matches()));
        }

        /**
         * Checks if a string length matches an exact size.
         */
        public static Predicate<@Nullable String> hasLength(int size) {
            return str -> str != null && str.length() == size;
        }

        /**
         * Checks if a string length falls within a specific range.
         *
         * @param min the minimum allowed length (inclusive)
         * @param max the maximum allowed length (exclusive)
         * @return {@code true} if the string length is within [min, max]
         */
        public static Predicate<@Nullable String> lengthInside(int min, int max) {
            return str -> str != null && str.length() >= min && str.length() < max;
        }

        /**
         * Checks if a string length falls within a specific range (inclusive).
         *
         * @param min the minimum allowed length (inclusive)
         * @param max the maximum allowed length (inclusive)
         * @return {@code true} if the string length is within [min, max]
         */
        public static Predicate<@Nullable String> lengthBetween(int min, int max) {
            return str -> str != null && str.length() >= min && str.length() <= max;
        }

        /**
         * Checks if a string length is strictly greater than a minimum boundary.
         */
        public static Predicate<@Nullable String> lengthGreaterThan(int min) {
            return str -> str != null && str.length() > min;
        }

        /**
         * Checks if a string length is greater than or equal to a minimum boundary.
         */
        public static Predicate<@Nullable String> lengthGreaterOrEqual(int min) {
            return str -> str != null && str.length() >= min;
        }

        /**
         * Checks if a string length is strictly less than a maximum boundary.
         */
        public static Predicate<@Nullable String> lengthLessThan(int max) {
            return str -> str != null && str.length() < max;
        }

        /**
         * Checks if a string length is less than or equal to a maximum boundary.
         */
        public static Predicate<@Nullable String> lengthLessOrEqual(int max) {
            return str -> str != null && str.length() <= max;
        }
    }
    // endregion

    //region TEMPORALS
    //*************************************************************************

    /**
     * Predicates for temporal types, all of which implement {@link Comparable}.
     *
     * <p>Semantically equivalent to {@link Comparables#lessThan}, {@link Comparables#greaterThan},
     * named for readability.
     */
    public static final class Temporals {

        private Temporals() {
            // utility
        }

        /**
         * Checks if a value is strictly before another value.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> isBefore(@Nullable T other) {
            return v -> v != null && other != null && v.compareTo(other) < 0;
        }

        /**
         * Checks if a value is strictly after another value.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> isAfter(@Nullable T other) {
            return v -> v != null && other != null && v.compareTo(other) > 0;
        }

        /**
         * Checks if a value is before or equal to another value.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> isBeforeOrEqual(@Nullable T other) {
            return v -> v != null && other != null && v.compareTo(other) <= 0;
        }

        /**
         * Checks if a value is after or equal to another value.
         */
        public static <T extends Comparable<? super T>> Predicate<@Nullable T> isAfterOrEqual(@Nullable T other) {
            return v -> v != null && other != null && v.compareTo(other) >= 0;
        }
    }
    // endregion
}