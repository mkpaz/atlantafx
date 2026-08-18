/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A wrapper around a {@link Supplier} that defers and caches a single computation.
 *
 * <p>The wrapped value is computed on the first call to {@link #get()} and
 * reused on every subsequent call.
 *
 * <p>If the supplier produces {@code null}, the result is indistinguishable
 * from "not yet computed" — {@link #empty()} will keep returning {@code true},
 * and {@link #get()} will invoke the supplier again on every call.
 *
 * <p>Instances are compared by identity. Do not override {@code hashCode()}
 * or {@code equals()}, as two instances wrapping equivalent suppliers are
 * not necessarily interchangeable, particularly before initialization.
 */
public class Lazy<T> implements Supplier<T> {

    protected final Supplier<T> supplier;
    protected @Nullable T value = null;

    public Lazy(Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    /**
     * Returns the wrapped value, computing it via the supplier on first access.
     *
     * @return the (possibly newly computed) value
     */
    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }

    /**
     * Checks whether the wrapped value is absent.
     *
     * <p>Returns {@code true} both when {@link #get()} has never been called
     * and when the supplier has returned {@code null} — the two cases cannot
     * be distinguished.
     *
     * @return {@code true} if there is currently no cached value, {@code false} otherwise
     */
    public boolean empty() {
        return value == null;
    }

    /**
     * Returns the string representation of the currently cached value,
     * without triggering initialization.
     *
     * @return {@code "null"} if the value is currently absent (see {@link #empty()}),
     *         otherwise {@code String.valueOf} of the cached value
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}