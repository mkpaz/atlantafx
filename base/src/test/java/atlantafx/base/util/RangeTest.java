package atlantafx.base.util;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@NullMarked
class RangeTest {

    @Test
    @DisplayName("should create range when start is less than end")
    void testCreationValid() {
        var range = Range.of(1, 10);

        assertThat(range.start()).isEqualTo(1);
        assertThat(range.end()).isEqualTo(10);
    }

    @Test
    @DisplayName("should create empty range when start equals end")
    void testCreationEmpty() {
        var range = Range.of(5, 5);

        assertThat(range.start()).isEqualTo(5);
        assertThat(range.end()).isEqualTo(5);
    }

    @Test
    @DisplayName("should throw exception when start is greater than end")
    void testCreationInvalid() {
        assertThatThrownBy(() -> new Range(10, 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Start (10) cannot be greater than end (1).");
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6, 9})
    @DisplayName("should return true when value is inside range")
    void testInsideTrue(int value) {
        var range = Range.of(5, 10);

        assertThat(range.inside(value)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 10, 11})
    @DisplayName("should return false when value is outside range")
    void testInsideFalse(int value) {
        var range = Range.of(5, 10);

        assertThat(range.inside(value)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6, 10})
    @DisplayName("should return true when value is between bounds")
    void testBetweenTrue(int value) {
        var range = Range.of(5, 10);

        assertThat(range.between(value)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 11})
    @DisplayName("should return false when value is not between bounds")
    void testBetweenFalse(int value) {
        var range = Range.of(5, 10);

        assertThat(range.between(value)).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
        "3, 7, true",   // partial overlap left
        "7, 12, true",  // partial overlap right
        "6, 8, true",   // inner range
        "1, 15, true",  // enclosing range
        "1, 5, false",  // adjacent left
        "10, 15, false",// adjacent right
        "1, 4, false",  // completely left
        "11, 15, false" // completely right
    })
    @DisplayName("should correctly check range intersection")
    void testIntersects(int start, int end, boolean expected) {
        var range = Range.of(5, 10);
        var other = Range.of(start, end);

        assertThat(range.intersects(other)).isEqualTo(expected);
    }

    @Test
    @DisplayName("should throw exception when intersects parameter is null")
    @SuppressWarnings("all")
    void testIntersectsNull() {
        var range = Range.of(5, 10);

        assertThatThrownBy(() -> range.intersects(null))
            .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "5, 10, true",  // exact match
        "6, 9, true",   // strict inner
        "5, 8, true",   // same start
        "7, 10, true",  // same end
        "4, 10, false", // wider start
        "5, 11, false", // wider end
        "1, 15, false"  // wider overall
    })
    @DisplayName("should correctly check range containment")
    void testContains(int start, int end, boolean expected) {
        var range = Range.of(5, 10);
        var other = Range.of(start, end);

        assertThat(range.contains(other)).isEqualTo(expected);
    }

    @Test
    @DisplayName("should throw exception when contains parameter is null")
    @SuppressWarnings("all")
    void testContainsNull() {
        var range = Range.of(5, 10);

        assertThatThrownBy(() -> range.contains(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should calculate range length")
    void testLength() {
        var range = Range.of(5, 10);

        assertThat(range.length()).isEqualTo(5);
    }

    @Test
    @DisplayName("should return true for empty range")
    void testIsEmptyTrue() {
        var range = Range.of(5, 5);

        assertThat(range.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should return false for non empty range")
    void testIsEmptyFalse() {
        var range = Range.of(5, 6);

        assertThat(range.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should return intersection when ranges overlap")
    void testIntersectionValid() {
        var range = Range.of(5, 10);
        var other = Range.of(7, 12);

        var result = range.intersection(other);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(7);
        assertThat(result.end()).isEqualTo(10);
    }

    @Test
    @DisplayName("should return null when ranges do not overlap")
    void testIntersectionNull() {
        var range = Range.of(5, 10);
        var other = Range.of(10, 15);

        var result = range.intersection(other);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should return union when ranges overlap")
    void testUnionOverlapping() {
        var range = Range.of(5, 10);
        var other = Range.of(7, 12);

        var result = range.union(other);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(5);
        assertThat(result.end()).isEqualTo(12);
    }

    @Test
    @DisplayName("should return union when ranges touch")
    void testUnionTouching() {
        var range = Range.of(5, 10);
        var other = Range.of(10, 15);

        var result = range.union(other);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(5);
        assertThat(result.end()).isEqualTo(15);
    }

    @Test
    @DisplayName("should return null when ranges have gap")
    void testUnionWithGap() {
        var range = Range.of(5, 10);
        var other = Range.of(12, 15);

        var result = range.union(other);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should throw exception when union parameter is null")
    @SuppressWarnings("all")
    void testUnionNull() {
        var range = Range.of(5, 10);

        assertThatThrownBy(() -> range.union(null))
            .isInstanceOf(NullPointerException.class);
    }
}