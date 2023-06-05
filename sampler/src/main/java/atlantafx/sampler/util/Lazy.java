/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.util;

import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Auxiliary object wrapper to support lazy initialization.
 * DO NOT override {@code hashCode()} / {@code equals()}, because each instance
 * of this object must remain unique.
 */
public class Lazy<T> implements Supplier<T> {

    protected final Supplier<T> supplier;
    protected @Nullable T value = null;

    public Lazy(Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }

    public boolean initialized() {
        return value != null;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}