/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import java.util.List;
import java.util.function.Supplier;

/**
 * A lazy arguments holder for parameterized message formatting.
 *
 * <p>Acts as an argument adapter, allowing static values, dynamic suppliers, or a mixture of both
 * to be passed for message formatting. Any elements implementing {@link Supplier} are executed
 * dynamically (at validation time), while static elements are evaluated as-is (at instantiation time).
 *
 * <p><b>Usage Examples:</b>
 *
 * <pre>{@code
 * // Static arguments
 * rule.failMessageFormat("Range must be between {2} and {3}", Args.of(50, 100));
 *
 * // Dynamic arguments
 * rule.failMessageFormat("Range must be between {2} and {3}", Args.of(min::get, max::get));
 *
 * // Mixed
 * rule.failMessageFormat("Value must be > {2} at {3}", Args.of(10, Args.cast(context::get)));
 * }</pre>
 *
 * @param args the raw array of arguments, which may contain static objects or {@link Supplier} instances
 */
public record Args(List<Object> args) implements Supplier<Object[]> {

    public Args {
        args = List.copyOf(args);
    }

    /**
     * Constructs a {@code Args} for the specified array of arguments.
     *
     * @param args the raw array of arguments
     */
    public Args(Object... args) {
        this(List.of(args));
    }

    /**
     * Collects all arguments into a target object array.
     *
     * @return a new {@code Object[]} array containing all evaluated arguments
     */
    @Override
    public Object[] get() {
        var result = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i) instanceof Supplier<?> supplier) {
                result[i] = supplier.get();
            } else {
                result[i] = args.get(i);
            }
        }
        return result;
    }

    /**
     * Creates an {@code Args} instance for static or mixed argument values.
     *
     * @param args the static arguments or suppliers to include
     */
    public static Args of(Object... args) {
        return new Args(args);
    }

    /**
     * Creates an {@code Args} instance for dynamic argument values.
     *
     * @param suppliers the suppliers to evaluate when formatting the message
     */
    public static Args of(Supplier<?>... suppliers) {
        return new Args((Object[]) suppliers);
    }

    /**
     * Type-helper method used to wrap a single {@link Supplier} when mixing static values
     * and lambdas in the {@link #of(Object...)} overload.
     *
     * @param supplier the supplier instance to pass through
     * @return the same supplier instance, with target type inferred
     */
    public static <T> Supplier<T> cast(Supplier<T> supplier) {
        return supplier;
    }
}
