/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import org.jspecify.annotations.Nullable;

import java.text.MessageFormat;
import java.util.function.Supplier;

/**
 * Provides a message describing a validation failure.
 *
 * @param <T> the type of the value being validated
 */
@FunctionalInterface
public interface MessageProvider<T extends @Nullable Object> {

    /**
     * Returns a message describing the validation failure for the given value and descriptor.
     *
     * @param value      the attempted value that failed validation
     * @param descriptor the descriptor identifying the rule or rule set that produced a validation result
     */
    String apply(T value, Descriptor descriptor);

    /**
     * Formats a pattern using {@link MessageFormat}, placing {@code val} at {0},
     * {@code name} at {1}, and additional arguments from {@code args} starting at {2}.
     *
     * @param pattern the {@link MessageFormat} pattern
     * @param val     the attempted value
     * @param name    the rule/property name
     * @param args    the optional lazy supplier for additional arguments
     * @return the formatted message
     */
    static <T extends @Nullable Object> String formatMessage(String pattern,
                                                             @Nullable T val,
                                                             String name,
                                                             @Nullable Supplier<Object[]> args) {
        Object[] extraArgs = args != null ? args.get() : null;
        if (extraArgs == null || extraArgs.length == 0) {
            return MessageFormat.format(pattern, String.valueOf(val), name);
        }

        Object[] messageArgs = new Object[2 + extraArgs.length];
        messageArgs[0] = String.valueOf(val);
        messageArgs[1] = name;
        System.arraycopy(extraArgs, 0, messageArgs, 2, extraArgs.length);
        return MessageFormat.format(pattern, messageArgs);
    }
}
