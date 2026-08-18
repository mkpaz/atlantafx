/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents an integer range defined by a start and an end point.
 *
 * @param start the start of the range
 * @param end   the end of the range
 */
public record Range(int start, int end) {

    /**
     * Validates the range boundaries during instance creation.
     *
     * @throws IllegalArgumentException if start is greater than end
     */
    public Range {
        if (start > end) {
            throw new IllegalArgumentException("Start (" + start + ") cannot be greater than end (" + end + ").");
        }
    }

    /**
     * Creates a new range from the given start and end values.
     *
     * @param start the start value
     * @param end   the end value
     */
    public static Range of(int start, int end) {
        return new Range(start, end);
    }

    /**
     * Checks if the value is inside the range.
     * Includes the start bound and excludes the end bound.
     *
     * @param value the value to check
     * @return true if the value is inside the range, false otherwise
     */
    public boolean inside(int value) {
        return value >= start && value < end;
    }

    /**
     * Checks if the value is between the start and end bounds.
     * Includes both start and end bounds.
     *
     * @param value the value to check
     * @return true if the value is between start and end, false otherwise
     */
    public boolean between(int value) {
        return value >= start && value <= end;
    }

    /**
     * Checks if this range overlaps with another range.
     *
     * @param other the other range to check
     * @return true if ranges intersect, false otherwise
     */
    public boolean intersects(Range other) {
        Objects.requireNonNull(other, "Other range must not be null.");
        return this.start < other.end && other.start < this.end;
    }

    /**
     * Checks if this range completely contains another range.
     *
     * @param other the range to check
     * @return true if this range contains the other range, false otherwise
     */
    public boolean contains(Range other) {
        Objects.requireNonNull(other, "Other range must not be null.");
        return this.start <= other.start && this.end >= other.end;
    }

    /**
     * Calculates the length of the range.
     *
     * @return the difference between end and start
     */
    public int length() {
        return end - start;
    }

    /**
     * Checks if the range is empty.
     * A range is empty when start equals end.
     *
     * @return true if length is zero, false otherwise
     */
    public boolean isEmpty() {
        return start == end;
    }

    /**
     * Finds the overlapping area between this range and another range.
     *
     * @param other the other range
     * @return the intersection range, or null if ranges do not overlap
     */
    public @Nullable Range intersection(Range other) {
        Objects.requireNonNull(other, "Other range must not be null.");

        if (!intersects(other)) {
            return null;
        }

        int newStart = Math.max(this.start, other.start);
        int newEnd = Math.min(this.end, other.end);

        return new Range(newStart, newEnd);
    }

    /**
     * Combines this range with another range if they touch or overlap.
     *
     * @param other the range to combine with
     * @return the combined range, or null if the ranges do not touch
     */
    public @Nullable Range union(Range other) {
        Objects.requireNonNull(other, "Other range must not be null.");

        if (this.start > other.end || other.start > this.end) {
            return null;
        }

        int newStart = Math.min(this.start, other.start);
        int newEnd = Math.max(this.end, other.end);

        return new Range(newStart, newEnd);
    }
}
