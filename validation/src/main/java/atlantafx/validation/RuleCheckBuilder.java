/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import org.jspecify.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A staging builder interface for defining the mandatory checks of a validation rule.
 *
 * @param <T> The type of the value being validated.
 */
public sealed interface RuleCheckBuilder<T extends @Nullable Object>
    extends RuleOptionsBuilder<T>, Validator<T>
    permits Rule {

    /**
     * Adds a predicate that the value must satisfy.
     *
     * @param predicate the validation logic
     */
    RuleCheckBuilder<T> must(Predicate<? super @Nullable T> predicate);

    /**
     * Sets a condition that must be met for a single rule check to be evaluated.
     *
     * @param condition the execution predicate
     */
    RuleCheckBuilder<T> when(Predicate<? super @Nullable T> condition);

    /**
     * Sets a condition that prevents a single rule check from being evaluated.
     *
     * @param condition the predicate that skips evaluation when {@code true}
     */
    default RuleCheckBuilder<T> unless(Predicate<? super @Nullable T> condition) {
        return when(condition.negate());
    }

    /**
     * Sets a violations code.
     *
     * @param code the violations code
     */
    RuleCheckBuilder<T> failCode(int code);

    /**
     * Sets an violation message provider.
     *
     * @param provider the violation message converter
     * @param args     the optional args supplier
     */
    RuleCheckBuilder<T> failMessage(@Nullable MessageProvider<T> provider,
                                    @Nullable Supplier<Object[]> args);

    /**
     * Sets an violation message provider.
     *
     * @param provider the violation message converter
     */
    default RuleCheckBuilder<T> failMessage(@Nullable MessageProvider<T> provider) {
        return failMessage(provider, null);
    }

    /**
     * Sets a plain violation message.
     *
     * @param message the violation message
     */
    default RuleCheckBuilder<T> failMessage(String message) {
        return failMessage((_, _) -> message);
    }

    /**
     * Sets a formatted violation message with positional placeholders {@code {0}} — attempted value
     * and {@code {1}} — rule name.
     *
     * @param message the violation message pattern
     */
    default RuleCheckBuilder<T> failMessageFormat(String message) {
        return failMessage((val, d) ->
            MessageFormat.format(message, String.valueOf(val), d.name())
        );
    }

    /**
     * Sets a formatted violation message with positional placeholders {@code {0}} — attempted value,
     * {@code {1}} — rule name, and additional arguments starting at {@code {2}}.
     *
     * @param message the message pattern
     * @param args    the optional supplier of additional formatting arguments starting at index {@code {2}}
     */
    default RuleCheckBuilder<T> failMessageFormat(String message, @Nullable Supplier<Object[]> args) {
        return failMessage((v, d) -> MessageProvider.formatMessage(message, v, d.name(), args), args);
    }

    /**
     * Sets a plain violation message fetched from the given {@link ResourceBundle} by key.
     *
     * @param bundle the resource bundle containing the message
     * @param key    the message key in the resource bundle
     */
    default RuleCheckBuilder<T> failMessageKey(ResourceBundle bundle, String key) {
        return failMessage((_, _) -> bundle.getString(key));
    }

    /**
     * Sets a plain violation message fetched lazily from a {@link ResourceBundle} supplier by key.
     *
     * @param bundleSupplier the supplier of the resource bundle
     * @param key            the message key in the resource bundle
     */
    default RuleCheckBuilder<T> failMessageKey(Supplier<ResourceBundle> bundleSupplier, String key) {
        return failMessage((_, _) -> bundleSupplier.get().getString(key));
    }

    /**
     * Sets a plain violation message fetched from a resource bundle by name using {@link Locale#getDefault()}.
     *
     * @param bundleName the base name of the resource bundle
     * @param key        the message key in the resource bundle
     */
    default RuleCheckBuilder<T> failMessageKey(String bundleName, String key) {
        return failMessageKey(() -> ResourceBundle.getBundle(bundleName, Locale.getDefault()), key);
    }

    /**
     * Sets a plain violation message fetched from a resource bundle by name and explicit locale.
     *
     * @param bundleName the base name of the resource bundle
     * @param key        the message key in the resource bundle
     * @param locale     the locale to load the resource bundle
     */
    default RuleCheckBuilder<T> failMessageKey(String bundleName, String key, Locale locale) {
        return failMessageKey(() -> ResourceBundle.getBundle(bundleName, locale), key);
    }

    /**
     * Sets a plain violation message fetched from a resource bundle by name using a locale supplier.
     *
     * @param bundleName the base name of the resource bundle
     * @param key        the message key in the resource bundle
     * @param locale     the supplier resolving current locale
     */
    default RuleCheckBuilder<T> failMessageKey(String bundleName, String key, Supplier<Locale> locale) {
        return failMessageKey(() -> ResourceBundle.getBundle(bundleName, locale.get()), key);
    }

    /**
     * Sets a formatted violation message fetched from the given {@link ResourceBundle} by key
     * with positional placeholders {@code {0}} — attempted value and {@code {1}} — rule name.
     *
     * @param bundle the resource bundle containing the message pattern
     * @param key    the message key in the resource bundle
     */
    default RuleCheckBuilder<T> failMessageFormatKey(ResourceBundle bundle, String key) {
        return failMessageFormatKey(() -> bundle, key);
    }

    /**
     * Sets a formatted violation message fetched from the given {@link ResourceBundle} by key
     * with positional placeholders {@code {0}} — attempted value, {@code {1}} — rule name,
     * and additional arguments starting at {@code {2}}.
     *
     * @param bundle the resource bundle containing the message pattern
     * @param key    the message key in the resource bundle
     * @param args   the optional supplier of additional formatting arguments starting at index {@code {2}}
     */
    default RuleCheckBuilder<T> failMessageFormatKey(ResourceBundle bundle, String key,
                                                     @Nullable Supplier<Object[]> args) {
        return failMessageFormatKey(() -> bundle, key, args);
    }

    /**
     * Sets a formatted violation message fetched lazily from a {@link ResourceBundle} supplier by key
     * with positional placeholders {@code {0}} — attempted value and {@code {1}} — rule name.
     *
     * @param bundleSupplier the supplier of the resource bundle
     * @param key            the message key in the resource bundle
     */
    default RuleCheckBuilder<T> failMessageFormatKey(Supplier<ResourceBundle> bundleSupplier, String key) {
        return failMessageFormatKey(bundleSupplier, key, null);
    }

    /**
     * Sets a formatted violation message fetched lazily from a {@link ResourceBundle} supplier by key
     * with positional placeholders {@code {0}} — attempted value, {@code {1}} — rule name,
     * and additional arguments starting at {@code {2}}.
     *
     * @param bundleSupplier the supplier of the resource bundle
     * @param key            the message key in the resource bundle
     * @param args           the optional supplier of additional formatting arguments starting at index {@code {2}}
     */
    default RuleCheckBuilder<T> failMessageFormatKey(Supplier<ResourceBundle> bundleSupplier, String key,
                                                     @Nullable Supplier<Object[]> args) {
        return failMessage((v, d) -> {
            String pattern = bundleSupplier.get().getString(key);
            return MessageProvider.formatMessage(pattern, v, d.name(), args);
        }, args);
    }

    /**
     * Sets a formatted violation message fetched from a resource bundle by name using {@link Locale#getDefault()}
     * with positional placeholders {@code {0}} — attempted value and {@code {1}} — rule name.
     *
     * @param bundleName the base name of the resource bundle
     * @param key        the message key in the resource bundle
     */
    default RuleCheckBuilder<T> failMessageFormatKey(String bundleName, String key) {
        return failMessageFormatKey(() -> ResourceBundle.getBundle(bundleName, Locale.getDefault()), key);
    }

    /**
     * Sets a formatted violation message fetched from a resource bundle by name using {@link Locale#getDefault()}
     * with positional placeholders {@code {0}} — attempted value, {@code {1}} — rule name,
     * and additional arguments starting at {@code {2}}.
     *
     * @param bundleName the base name of the resource bundle
     * @param key        the message key in the resource bundle
     * @param args       the optional supplier of additional formatting arguments starting at index {@code {2}}
     */
    default RuleCheckBuilder<T> failMessageFormatKey(String bundleName, String key, @Nullable Supplier<Object[]> args) {
        return failMessageFormatKey(() -> ResourceBundle.getBundle(bundleName, Locale.getDefault()), key, args);
    }

    /**
     * Sets a formatted violation message fetched from a resource bundle by name and explicit locale
     * with positional placeholders {@code {0}} — attempted value, {@code {1}} — rule name,
     * and additional arguments starting at {@code {2}}.
     *
     * @param bundleName the base name of the resource bundle
     * @param key        the message key in the resource bundle
     * @param locale     the locale for which to load the resource bundle
     * @param args       the optional supplier of additional formatting arguments starting at index {@code {2}}
     */
    default RuleCheckBuilder<T> failMessageFormatKey(String bundleName, String key, Locale locale,
                                                     @Nullable Supplier<Object[]> args) {
        return failMessageFormatKey(() -> ResourceBundle.getBundle(bundleName, locale), key, args);
    }

    /**
     * Sets a formatted violation message fetched from a resource bundle by name using a locale supplier
     * with positional placeholders {@code {0}} — attempted value, {@code {1}} — rule name,
     * and additional arguments starting at {@code {2}}.
     *
     * @param bundleName the base name of the resource bundle
     * @param key        the message key in the resource bundle
     * @param locale     the supplier resolving current locale
     * @param args       the optional supplier of additional formatting arguments starting at index {@code {2}}
     */
    default RuleCheckBuilder<T> failMessageFormatKey(String bundleName, String key, Supplier<Locale> locale,
                                                     @Nullable Supplier<Object[]> args) {
        return failMessageFormatKey(() -> ResourceBundle.getBundle(bundleName, locale.get()), key, args);
    }
    //endregion
}