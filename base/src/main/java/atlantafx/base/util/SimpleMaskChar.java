/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * The default {@link MaskChar} implementation that should be suitable
 * for anything except heavily custom logic.
 */
public final class SimpleMaskChar implements MaskChar {

    private final Predicate<Character> matchExpr;
    private final UnaryOperator<Character> transform;
    private final char placeholder;
    private final boolean fixed;

    /**
     * Creates a SimpleMaskChar.
     *
     * @param matchExpr The matching predicate that determines which characters are masked.
     * @see #SimpleMaskChar(Predicate, UnaryOperator, char, boolean)
     */
    public SimpleMaskChar(Predicate<Character> matchExpr) {
        this(matchExpr, UnaryOperator.identity(), UNDERSCORE, false);
    }

    /**
     * Creates a SimpleMaskChar.
     *
     * @param matchExpr The matching predicate that determines which characters are masked.
     * @param transform The transformation function that is applied to input characters.
     * @see #SimpleMaskChar(Predicate, UnaryOperator, char, boolean)
     */
    public SimpleMaskChar(Predicate<Character> matchExpr,
                          UnaryOperator<Character> transform) {
        this(matchExpr, transform, UNDERSCORE, false);
    }

    /**
     * Creates a SimpleMaskChar.
     *
     * @param matchExpr   The matching predicate that determines which characters are masked.
     * @param transform   The transformation function that is applied to input characters.
     * @param placeholder The placeholder character to use for masking.
     * @see #SimpleMaskChar(Predicate, UnaryOperator, char, boolean)
     */
    public SimpleMaskChar(Predicate<Character> matchExpr,
                          UnaryOperator<Character> transform,
                          char placeholder) {
        this(matchExpr, transform, placeholder, false);
    }

    /**
     * Creates a SimpleMaskChar.
     *
     * @param matchExpr   The matching predicate that determines which characters are masked.
     * @param transform   The transformation function that is applied to input characters.
     *                    No transformation is applied by default.
     * @param placeholder The placeholder character to use for masking.
     *                    The default replacement is underscore character.
     * @param fixed       Boolean value indicating if the character is fixed or not.
     *                    Default is false.
     */
    public SimpleMaskChar(Predicate<Character> matchExpr,
                          UnaryOperator<Character> transform,
                          char placeholder,
                          boolean fixed) {
        this.matchExpr = Objects.requireNonNull(matchExpr);
        this.transform = Objects.requireNonNull(transform);
        this.placeholder = placeholder;
        this.fixed = fixed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllowed(final char ch) {
        return matchExpr.test(ch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char transform(final char ch) {
        return transform.apply(ch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char getPlaceholder() {
        return placeholder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFixed() {
        return fixed;
    }

    /**
     * A utility method for creating a fixed character - that is, the character used to represent
     * the fixed part (a prefix or a suffix) of the input mask.
     */
    public static SimpleMaskChar fixed(char ch) {
        return new SimpleMaskChar(c -> c == ch, UnaryOperator.identity(), ch, true);
    }
}
