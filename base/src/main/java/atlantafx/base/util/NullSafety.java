/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Utility class providing helper methods for null-safety handling and bridging gaps
 * in static nullability analysis tools.
 */
public final class NullSafety {

    private NullSafety() {
        // utility class
    }

    /**
     * Accepts a {@code @Nullable T} and returns a plain {@code T}, suppressing
     * nullability warnings without performing any runtime safety checks.
     *
     * <p>See <a href="https://github.com/jspecify/jspecify/issues/300">jspecify/issues/300</a>.
     *
     * @param <T> the type of the object
     * @param t   the potentially null value
     * @return the same value, unsafely asserted as non-null to the compiler
     */
    @SuppressWarnings("ALL")
    public static <T extends @Nullable Object> T permitNullUnsafe(@Nullable T t) {
        return t;
    }

    /**
     * Allows initializing a {@code @NonNull} field with {@code null} when actual
     * initialization happens later in the lifecycle (e.g., during dependency injection
     * or setup methods).
     *
     * @param <T> the expected non-null type
     * @return {@code null} cast to a non-null type to satisfy static analysis
     */
    @SuppressWarnings("ALL")
    public static <T> @NonNull T lateNonNull() {
        return (T) permitNullUnsafe(null);
    }

    /**
     * Asserts that the specified object reference is not {@code null}.
     *
     * @param <T> the type of the reference
     * @param t   the object reference to check for nullity
     * @throws NullPointerException if {@code t} is {@code null}
     * @see #assertNonNull(Object, String)
     */
    public static <T> void assertNonNull(@Nullable T t) {
        assertNonNull(t, null);
    }

    /**
     * Asserts that the specified object reference is not {@code null}, throwing
     * a {@link NullPointerException} if it is.
     *
     * <p>This is used to bypass overly aggressive IDE warnings for non-null values
     * where an explicit runtime null-check is still required.
     *
     * @param <T>      the type of the reference
     * @param t        the object reference to check for nullity
     * @param property the optional property or argument name used to construct
     *                 the exception message (e.g., {@code "username cannot be null!"})
     * @throws NullPointerException if {@code t} is {@code null}
     */
    public static <T> void assertNonNull(@Nullable T t, @Nullable String property) {
        if (t == null) {
            String name = (property != null) ? property : "Object";
            throw new NullPointerException(name + " cannot be null!");
        }
    }
}
