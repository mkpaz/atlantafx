/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * The default {@link MaskChar} implementation that should be suitable
 * for anything except heavily custom logic.
 */
final class SimpleMaskChar implements MaskChar {

    private final Predicate<Character> matchExpr;
    private final UnaryOperator<Character> transform;
    private final char placeholder;
    private final boolean fixed;

    public SimpleMaskChar(Predicate<Character> matchExpr) {
        this(matchExpr, UnaryOperator.identity(), UNDERSCORE, false);
    }

    public SimpleMaskChar(Predicate<Character> matchExpr,
                          UnaryOperator<Character> transform) {
        this(matchExpr, transform, UNDERSCORE, false);
    }

    public SimpleMaskChar(Predicate<Character> matchExpr,
                          UnaryOperator<Character> transform,
                          char placeholder) {
        this(matchExpr, transform, placeholder, false);
    }

    public SimpleMaskChar(Predicate<Character> matchExpr,
                          UnaryOperator<Character> transform,
                          char placeholder,
                          boolean fixed) {
        this.matchExpr = Objects.requireNonNull(matchExpr);
        this.transform = Objects.requireNonNull(transform);
        this.placeholder = placeholder;
        this.fixed = fixed;
    }

    @Override
    public boolean isAllowed(final char ch) {
        return matchExpr.test(ch);
    }

    @Override
    public char transform(final char ch) {
        return transform.apply(ch);
    }

    @Override
    public char getPlaceholder() {
        return placeholder;
    }

    @Override
    public boolean isFixed() {
        return fixed;
    }

    public static SimpleMaskChar fixed(char ch) {
        return new SimpleMaskChar(c -> c == ch, UnaryOperator.identity(), ch, true);
    }
}