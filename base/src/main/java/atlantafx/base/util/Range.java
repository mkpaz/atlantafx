/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

/**
 * Represents a range with a specified start and end value.
 * The range is inclusive of the start value, while the end value can be
 * either inclusive or exclusive, depending on the user's preference.
 *
 * @param start the starting value of the range (inclusive)
 * @param end   the ending value of the range (inclusive or exclusive)
 */
public record Range(int start, int end) {
}
