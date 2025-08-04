/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class NullSafetyHelper {

    private NullSafetyHelper() {
        // utility method
    }

    /**
     * Accepts a {@code @Nullable T} and returns a plain {@code T}, without
     * performing any check that this conversion is safe.
     *
     * <p>See <a href="https://github.com/jspecify/jspecify/issues/300">jspecify/issues/300</a>.
     */
    @SuppressWarnings("ALL")
    public static <T extends @Nullable Object> T castNonNullUnsafe(@Nullable T t) {
        return t;
    }

    /**
     * Allows to initializes a @NonNull field with {@code null} that is initialized later.
     */
    @SuppressWarnings("ALL")
    public static <T> @NonNull T lateNonNull() {
        return (T) castNonNullUnsafe(null);
    }

    /**
     * See {@link #assertNonNull(Object, String)}.
     */
    public <T> void assertNonNull(@Nullable T t) {
        assertNonNull(t, null);
    }

    /**
     * Throws {@code NullPointerException} if {@code T} is null.
     *
     * <p>This is used to avoid overly smart IDE warnings for non-values where we still
     * want to perform a null check.
     *
     * @param t        the value to check for null
     * @param property the property name for error message (optional)
     */
    public static <T> void assertNonNull(@Nullable T t, @Nullable String property) {
        if (t == null) {
            throw new NullPointerException(property != null ? property : "Object" + " cannot be null!");
        }
    }
}
