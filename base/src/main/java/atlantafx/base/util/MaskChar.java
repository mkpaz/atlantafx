/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

/**
 * Provides basic API for defining an input mask.
 */
public interface MaskChar {

    char UNDERSCORE = '_';

    char INPUT_MASK_LETTER = 'A';
    char INPUT_MASK_DIGIT_OR_LETTER = 'N';
    char INPUT_MASK_ANY_NON_SPACE = 'X';
    char INPUT_MASK_HEX = 'H';
    char INPUT_MASK_DIGIT_NON_ZERO = 'D';
    char INPUT_MASK_DIGIT = '9';
    char INPUT_MASK_DIGIT_0_TO_8 = '8';
    char INPUT_MASK_DIGIT_0_TO_7 = '7';
    char INPUT_MASK_DIGIT_0_TO_6 = '6';
    char INPUT_MASK_DIGIT_0_TO_5 = '5';
    char INPUT_MASK_DIGIT_0_TO_4 = '4';
    char INPUT_MASK_DIGIT_0_TO_3 = '3';
    char INPUT_MASK_DIGIT_0_TO_2 = '2';
    char INPUT_MASK_DIGIT_0_TO_1 = '1';
    char INPUT_MASK_DIGIT_ZERO = '0';

    /**
     * Returns "true" if the character is allowed, "false" otherwise.
     */
    boolean isAllowed(char ch);

    /**
     * Transforms user input character before setting.
     */
    char transform(char ch);

    /**
     * Returns the placeholder for the mask character.
     */
    char getPlaceholder();

    /**
     * Returns whether character is fixed (prefix, suffix or separator).
     */
    boolean isFixed();
}
