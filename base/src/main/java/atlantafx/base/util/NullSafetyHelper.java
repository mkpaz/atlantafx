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
}
