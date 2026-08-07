/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static atlantafx.validation.Check.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckTest {

    @Test
    void testAllOf() {
        assertTrue(allOf(_ -> true, _ -> true).test("value"));
        assertFalse(allOf(_ -> true, _ -> false).test("value"));
        assertFalse(allOf(_ -> false, _ -> true).test("value"));
    }

    @Test
    void testAnyOf() {
        assertTrue(anyOf(_ -> true, _ -> false).test("value"));
        assertFalse(anyOf(_ -> false, _ -> false).test("value"));
        assertTrue(anyOf(_ -> false, _ -> true).test("value"));
    }

    @Test
    void testNoneOf() {
        assertTrue(noneOf(_ -> false, _ -> false).test("value"));
        assertFalse(noneOf(_ -> true, _ -> false).test("value"));
        assertFalse(noneOf(_ -> false, _ -> true).test("value"));
    }

    @Test
    void testNullable() {
        assertTrue(nullable(_ -> false).test(null));
        assertFalse(nullable(_ -> false).test("value"));
    }

    @Test
    void testRequired() {
        assertFalse(required(_ -> true).test(null));
        assertTrue(required(_ -> true).test("value"));
    }

    @Test
    void testObjects() {
        assertTrue(Objects.isNull().test(null));
        assertFalse(Objects.isNull().test("value"));

        assertFalse(Objects.isNotNull().test(null));
        assertTrue(Objects.isNotNull().test("value"));

        assertFalse(Objects.isEqual("target").test(null));
        assertFalse(Objects.isEqual(null).test("target"));
        assertTrue(Objects.isEqual("target").test("target"));
        assertFalse(Objects.isEqual("target").test("other"));

        assertTrue(Objects.isNotEqual("target").test(null));
        assertTrue(Objects.isNotEqual(null).test("target"));
        assertTrue(Objects.isNotEqual("target").test("other"));
        assertFalse(Objects.isNotEqual("target").test("target"));

        assertTrue(Objects.isSame(null).test(null));
        String val = "value";
        assertTrue(Objects.isSame(val).test(val));
        //noinspection StringOperationCanBeSimplified
        assertFalse(Objects.isSame(val).test(new String("value")));

        assertFalse(Objects.isNotSame(null).test(null));
        assertTrue(Objects.isNotSame(new Object()).test(new Object()));
        assertFalse(Objects.isNotSame(val).test(val));

        assertFalse(Objects.oneOf((Object[]) null).test("value"));
        assertFalse(Objects.oneOf((Object[]) null).test(null));
        assertTrue(Objects.oneOf("a", "b").test("a"));
        assertFalse(Objects.oneOf("a", "b").test("c"));

        assertFalse(Objects.oneOf((Collection<String>) null).test("value"));
        assertFalse(Objects.oneOf((Collection<String>) null).test(null));
        assertTrue(Objects.oneOf(List.of("a", "b")).test("a"));
        assertFalse(Objects.oneOf(List.of("a", "b")).test("c"));

        assertTrue(Objects.noneOf((Object[]) null).test("value"));
        assertTrue(Objects.noneOf((Object[]) null).test(null));
        assertTrue(Objects.noneOf("a", "b").test("c"));
        assertFalse(Objects.noneOf("a", "b").test("a"));

        assertTrue(Objects.noneOf((Collection<String>) null).test("value"));
        assertTrue(Objects.noneOf((Collection<String>) null).test(null));
        assertTrue(Objects.noneOf(List.of("a", "b")).test("c"));
        assertFalse(Objects.noneOf(List.of("a", "b")).test("a"));

        assertFalse(Objects.isInstanceOf(null).test("value"));
        assertFalse(Objects.isInstanceOf(null).test(null));
        assertTrue(Objects.isInstanceOf(String.class).test("value"));
        assertFalse(Objects.isInstanceOf(Integer.class).test("value"));

        assertTrue(Objects.isNotInstanceOf(null).test("value"));
        assertTrue(Objects.isNotInstanceOf(null).test(null));
        assertTrue(Objects.isNotInstanceOf(Integer.class).test("value"));
        assertFalse(Objects.isNotInstanceOf(String.class).test("value"));
    }

    @Test
    void testBooleans() {
        assertFalse(Booleans.isTrue().test(null));
        assertTrue(Booleans.isTrue().test(true));
        assertFalse(Booleans.isTrue().test(false));

        assertFalse(Booleans.isFalse().test(null));
        assertTrue(Booleans.isFalse().test(false));
        assertFalse(Booleans.isFalse().test(true));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testCollections() {
        assertFalse(Collections.isEmpty().test(null));
        assertTrue(Collections.isEmpty().test(List.of()));
        assertFalse(Collections.isEmpty().test(List.of(1)));

        assertFalse(Collections.isNotEmpty().test(null));
        assertFalse(Collections.isNotEmpty().test(List.of()));
        assertTrue(Collections.isNotEmpty().test(List.of(1)));

        assertFalse(Collections.hasSize(1).test(null));
        assertTrue(Collections.hasSize(1).test(List.of(1)));
        assertFalse(Collections.hasSize(1).test(List.of(1, 2)));

        assertFalse(Collections.sizeGreaterThan(1).test(null));
        assertTrue(Collections.sizeGreaterThan(1).test(List.of(1, 2)));
        assertFalse(Collections.sizeGreaterThan(1).test(List.of(1)));

        assertFalse(Collections.sizeGreaterOrEqual(1).test(null));
        assertTrue(Collections.sizeGreaterOrEqual(1).test(List.of(1)));
        assertFalse(Collections.sizeGreaterOrEqual(1).test(List.of()));

        assertFalse(Collections.sizeLessThan(2).test(null));
        assertTrue(Collections.sizeLessThan(2).test(List.of(1)));
        assertFalse(Collections.sizeLessThan(2).test(List.of(1, 2)));

        assertFalse(Collections.sizeLessOrEqual(2).test(null));
        assertTrue(Collections.sizeLessOrEqual(2).test(List.of(1, 2)));
        assertFalse(Collections.sizeLessOrEqual(2).test(List.of(1, 2, 3)));

        assertFalse(Collections.sizeInside(1, 3).test(null));
        assertTrue(Collections.sizeInside(1, 3).test(List.of(1, 2)));
        assertFalse(Collections.sizeInside(1, 3).test(List.of(1, 2, 3)));

        assertFalse(Collections.sizeBetween(1, 3).test(null));
        assertTrue(Collections.sizeBetween(1, 3).test(List.of(1, 2, 3)));
        assertFalse(Collections.sizeBetween(1, 3).test(List.of()));

        assertFalse(Collections.allMatch(null).test(List.of(1)));
        assertFalse(Collections.allMatch(_ -> true).test(null));
        assertTrue(Collections.<Integer, List<Integer>>allMatch(v -> v > 0).test(List.of(1, 2)));
        assertFalse(Collections.<Integer, List<Integer>>allMatch(v -> v > 1).test(List.of(1, 2)));

        assertFalse(Collections.anyMatch(null).test(List.of(1)));
        assertFalse(Collections.anyMatch(_ -> true).test(null));
        assertTrue(Collections.<Integer, List<Integer>>anyMatch(v -> v > 1).test(List.of(1, 2)));
        assertFalse(Collections.<Integer, List<Integer>>anyMatch(v -> v > 2).test(List.of(1, 2)));

        assertFalse(Collections.noneMatch(null).test(List.of(1)));
        assertFalse(Collections.noneMatch(_ -> true).test(null));
        assertTrue(Collections.<Integer>noneMatch(v -> v > 2).test(List.of(1, 2)));
        assertFalse(Collections.<Integer>noneMatch(v -> v > 0).test(List.of(1, 2)));

        assertFalse(Collections.contains(null).test(List.of(1)));
        assertTrue(Collections.contains(1).test(List.of(1, 2)));
        assertFalse(Collections.contains(3).test(List.of(1, 2)));

        assertFalse(Collections.containsAll(null).test(List.of(1)));
        assertFalse(Collections.containsAll(List.of(1)).test(null));
        assertTrue(Collections.containsAll(List.of(1, 2)).test(List.of(1, 2, 3)));
        assertFalse(Collections.containsAll(List.of(1, 4)).test(List.of(1, 2, 3)));

        assertFalse(Collections.containsNone(null).test(List.of(1)));
        assertFalse(Collections.containsNone(List.of(1)).test(null));
        assertTrue(Collections.containsNone(List.of(4, 5)).test(List.of(1, 2, 3)));
        assertFalse(Collections.containsNone(List.of(1, 4)).test(List.of(1, 2, 3)));

        assertFalse(Collections.distinct().test(null));
        assertTrue(Collections.distinct().test(List.of(1, 2, 3)));
        assertFalse(Collections.distinct().test(List.of(1, 2, 1)));

        assertFalse(Collections.distinct(null).test(List.of(1)));
        assertFalse(Collections.distinct(String::length).test(null));
        assertTrue(Collections.distinct(String::length).test(List.of("a", "bb")));
        assertFalse(Collections.distinct(String::length).test(List.of("a", "b")));
    }

    @Test
    void testComparables() {
        assertFalse(Comparables.isEqual(5).test(null));
        assertFalse(Comparables.isEqual((Integer) null).test(5));
        assertTrue(Comparables.isEqual(5).test(5));
        assertFalse(Comparables.isEqual(5).test(6));

        assertFalse(Comparables.isNotEqual(5).test(null));
        assertFalse(Comparables.isNotEqual((Integer) null).test(5));
        assertTrue(Comparables.isNotEqual(5).test(6));
        assertFalse(Comparables.isNotEqual(5).test(5));

        assertFalse(Comparables.inside(1, 5).test(null));
        assertTrue(Comparables.inside(1, 5).test(1));
        assertTrue(Comparables.inside(1, 5).test(3));
        assertTrue(Comparables.inside(1, 5).test(4));

        assertFalse(Comparables.between(1, 5).test(null));
        assertTrue(Comparables.between(1, 5).test(1));
        assertTrue(Comparables.between(1, 5).test(3));
        assertTrue(Comparables.between(1, 5).test(5));
        assertFalse(Comparables.between(1, 5).test(6));

        assertFalse(Comparables.greaterThan(5).test(null));
        assertTrue(Comparables.greaterThan(5).test(6));
        assertFalse(Comparables.greaterThan(5).test(5));

        assertFalse(Comparables.greaterOrEqual(5).test(null));
        assertTrue(Comparables.greaterOrEqual(5).test(5));
        assertFalse(Comparables.greaterOrEqual(5).test(4));

        assertFalse(Comparables.lessThan(5).test(null));
        assertTrue(Comparables.lessThan(5).test(4));
        assertFalse(Comparables.lessThan(5).test(5));

        assertFalse(Comparables.lessOrEqual(5).test(null));
        assertTrue(Comparables.lessOrEqual(5).test(5));
        assertFalse(Comparables.lessOrEqual(5).test(6));
    }

    enum TestEnum { A, B, C }

    @Test
    void testEnums() {
        assertFalse(Enums.oneOf((TestEnum[]) null).test(TestEnum.A));
        assertTrue(Enums.oneOf(TestEnum.A, TestEnum.B).test(TestEnum.A));
        assertFalse(Enums.oneOf(TestEnum.A, TestEnum.B).test(TestEnum.C));

        assertFalse(Enums.oneOf((Collection<TestEnum>) null).test(TestEnum.A));
        assertTrue(Enums.oneOf(List.of(TestEnum.A, TestEnum.B)).test(TestEnum.A));
        assertFalse(Enums.oneOf(List.of(TestEnum.A, TestEnum.B)).test(TestEnum.C));

        assertFalse(Enums.<TestEnum>oneOf((String[]) null).test(TestEnum.A));
        assertTrue(Enums.<TestEnum>oneOf("A", "B").test(TestEnum.A));
        assertFalse(Enums.<TestEnum>oneOf("A", "B").test(TestEnum.C));

        assertTrue(Enums.noneOf((TestEnum[]) null).test(TestEnum.A));
        assertTrue(Enums.noneOf(TestEnum.A, TestEnum.B).test(TestEnum.C));
        assertFalse(Enums.noneOf(TestEnum.A, TestEnum.B).test(TestEnum.A));

        assertTrue(Enums.noneOf((Collection<TestEnum>) null).test(TestEnum.A));
        assertTrue(Enums.noneOf(List.of(TestEnum.A, TestEnum.B)).test(TestEnum.C));
        assertFalse(Enums.noneOf(List.of(TestEnum.A, TestEnum.B)).test(TestEnum.A));

        assertTrue(Enums.<TestEnum>noneOf((String[]) null).test(TestEnum.A));
        assertTrue(Enums.<TestEnum>noneOf("A", "B").test(TestEnum.C));
        assertFalse(Enums.<TestEnum>noneOf("A", "B").test(TestEnum.A));
    }

    @Test
    void testMaps() {
        assertFalse(Maps.hasKey("key").test(null));
        assertTrue(Maps.hasKey("key").test(Map.of("key", "value")));
        assertFalse(Maps.hasKey("key").test(Map.of("other", "value")));

        assertFalse(Maps.hasValue("value").test(null));
        assertTrue(Maps.hasValue("value").test(Map.of("key", "value")));
        assertFalse(Maps.hasValue("value").test(Map.of("key", "other")));
    }

    @Test
    void testNumbers() {
        assertFalse(Numbers.isPositive().test(null));
        assertTrue(Numbers.isPositive().test(1));
        assertFalse(Numbers.isPositive().test(-1));

        assertFalse(Numbers.isNegative().test(null));
        assertTrue(Numbers.isNegative().test(-1));
        assertFalse(Numbers.isNegative().test(1));

        assertFalse(Numbers.isZero().test(null));
        assertTrue(Numbers.isZero().test(0));
        assertFalse(Numbers.isZero().test(1));

        assertFalse(Numbers.isOdd().test(null));
        assertTrue(Numbers.isOdd().test(1));
        assertFalse(Numbers.isOdd().test(2));

        assertFalse(Numbers.isEven().test(null));
        assertTrue(Numbers.isEven().test(2));
        assertFalse(Numbers.isEven().test(1));
    }

    @Test
    void testOptionals() {
        assertFalse(Optionals.isPresent().test(null));
        assertTrue(Optionals.isPresent().test(Optional.of("value")));
        assertFalse(Optionals.isPresent().test(Optional.empty()));

        assertFalse(Optionals.isEmpty().test(null));
        assertTrue(Optionals.isEmpty().test(Optional.empty()));
        assertFalse(Optionals.isEmpty().test(Optional.of("value")));

        assertFalse(Optionals.isEqual("target").test(null));
        assertFalse(Optionals.isEqual(null).test(null)); // is not present
        assertTrue(Optionals.isEqual("target").test(Optional.of("target")));
        assertFalse(Optionals.isEqual("target").test(Optional.of("other")));

        assertFalse(Optionals.isNotEqual("target").test(null));
        assertFalse(Optionals.isNotEqual(null).test(null)); // is not present
        assertTrue(Optionals.isNotEqual("target").test(Optional.of("other")));
        assertFalse(Optionals.isNotEqual("target").test(Optional.of("target")));

        assertFalse(Optionals.matches(null).test(Optional.of("value")));
        assertTrue(Optionals.matches(_ -> true).test(Optional.of("value")));
        assertFalse(Optionals.matches(_ -> false).test(Optional.of("value")));
    }

    @Test
    void testStreams() {
        assertFalse(Streams.isEmpty().test(null));
        assertTrue(Streams.isEmpty().test(Stream.empty()));
        assertFalse(Streams.isEmpty().test(Stream.of("value")));

        assertFalse(Streams.isNotEmpty().test(null));
        assertTrue(Streams.isNotEmpty().test(Stream.of("value")));
        assertFalse(Streams.isNotEmpty().test(Stream.empty()));

        assertFalse(Streams.anyMatch(null).test(Stream.of("value")));
        assertTrue(Streams.anyMatch(_ -> true).test(Stream.of("value")));
        assertFalse(Streams.anyMatch(_ -> false).test(Stream.of("value")));

        assertFalse(Streams.allMatch(null).test(Stream.of("value")));
        assertTrue(Streams.allMatch(_ -> true).test(Stream.of("value")));
        assertFalse(Streams.allMatch(_ -> false).test(Stream.of("value")));

        assertFalse(Streams.noneMatch(null).test(Stream.of("value")));
        assertTrue(Streams.noneMatch(_ -> false).test(Stream.of("value")));
        assertFalse(Streams.noneMatch(_ -> true).test(Stream.of("value")));

        assertFalse(Streams.distinct().test(null));
        assertTrue(Streams.distinct().test(Stream.of("a", "b")));
        assertFalse(Streams.distinct().test(Stream.of("a", "a")));
    }

    @Test
    void testStrings() {
        assertTrue(Strings.isEmpty().test(null));
        assertTrue(Strings.isEmpty().test(""));
        assertFalse(Strings.isEmpty().test("value"));

        assertFalse(Strings.isNotEmpty().test(null));
        assertTrue(Strings.isNotEmpty().test("value"));
        assertFalse(Strings.isNotEmpty().test(""));

        assertTrue(Strings.isBlank().test(null));
        assertTrue(Strings.isBlank().test("   "));
        assertFalse(Strings.isBlank().test("value"));

        assertFalse(Strings.isNotBlank().test(null));
        assertTrue(Strings.isNotBlank().test("value"));
        assertFalse(Strings.isNotBlank().test("   "));

        assertFalse(Strings.isEqualAnyCase("Target").test(null));
        assertFalse(Strings.isEqualAnyCase(null).test(null));
        assertTrue(Strings.isEqualAnyCase("Target").test("target"));
        assertFalse(Strings.isEqualAnyCase("Target").test("other"));

        assertFalse(Strings.isNotEqualAnyCase("Target").test(null));
        assertFalse(Strings.isNotEqualAnyCase(null).test(null));
        assertTrue(Strings.isNotEqualAnyCase("Target").test("other"));
        assertFalse(Strings.isNotEqualAnyCase("Target").test("target"));

        assertFalse(Strings.contains(null).test("value"));
        assertFalse(Strings.contains(null).test(null));
        assertTrue(Strings.contains("alu").test("value"));
        assertFalse(Strings.contains("xyz").test("value"));

        assertFalse(Strings.containsAnyCase(null).test("Value"));
        assertFalse(Strings.containsAnyCase(null).test(null));
        assertTrue(Strings.containsAnyCase("alu").test("VALUE"));
        assertFalse(Strings.containsAnyCase("xyz").test("Value"));

        assertFalse(Strings.startsWith(null).test("value"));
        assertFalse(Strings.startsWith(null).test(null));
        assertTrue(Strings.startsWith("val").test("value"));
        assertFalse(Strings.startsWith("xyz").test("value"));

        assertFalse(Strings.startsWithAnyCase(null).test("Value"));
        assertFalse(Strings.startsWithAnyCase(null).test(null));
        assertTrue(Strings.startsWithAnyCase("val").test("VALUE"));
        assertFalse(Strings.startsWithAnyCase("xyz").test("Value"));

        assertFalse(Strings.endsWith(null).test("value"));
        assertFalse(Strings.endsWith(null).test(null));
        assertTrue(Strings.endsWith("lue").test("value"));
        assertFalse(Strings.endsWith("xyz").test("value"));

        assertFalse(Strings.endsWithAnyCase(null).test("Value"));
        assertFalse(Strings.endsWithAnyCase(null).test(null));
        assertTrue(Strings.endsWithAnyCase("LUE").test("value"));
        assertFalse(Strings.endsWithAnyCase("xyz").test("Value"));

        assertFalse(Strings.isUpperCase().test(null));
        assertTrue(Strings.isUpperCase().test("VALUE"));
        assertFalse(Strings.isUpperCase().test("Value"));

        assertFalse(Strings.isLowerCase().test(null));
        assertTrue(Strings.isLowerCase().test("value"));
        assertFalse(Strings.isLowerCase().test("Value"));

        assertFalse(Strings.isAlphabetic().test(null));
        assertTrue(Strings.isAlphabetic().test("Value"));
        assertFalse(Strings.isAlphabetic().test("Value123"));

        assertFalse(Strings.isDigits().test(null));
        assertTrue(Strings.isDigits().test("123"));
        assertFalse(Strings.isDigits().test("123a"));

        assertFalse(Strings.isAlphaNumeric().test(null));
        assertTrue(Strings.isAlphaNumeric().test("Value123"));
        assertFalse(Strings.isAlphaNumeric().test("Value 123"));

        assertFalse(Strings.isNumeric().test(null));
        assertTrue(Strings.isNumeric().test("123.45"));
        assertFalse(Strings.isNumeric().test("abc"));

        assertFalse(Strings.matches(null).test("value"));
        assertFalse(Strings.matches(null).test(null));
        assertTrue(Strings.matches("\\w+").test("value"));
        assertFalse(Strings.matches("\\d+").test("value"));

        assertFalse(Strings.matchesAny((Pattern[]) null).test("value"));
        assertFalse(Strings.matchesAny((Pattern[]) null).test(null));
        assertTrue(Strings.matchesAny(Pattern.compile("\\d+"), Pattern.compile("\\w+")).test("value"));
        assertFalse(Strings.matchesAny(Pattern.compile("\\d+")).test("value"));

        assertTrue(Strings.matchesNone((Pattern[]) null).test("value"));
        assertFalse(Strings.matchesNone((Pattern[]) null).test(null));
        assertTrue(Strings.matchesNone(Pattern.compile("\\d+")).test("value"));
        assertFalse(Strings.matchesNone(Pattern.compile("\\w+")).test("value"));

        assertFalse(Strings.hasLength(5).test(null));
        assertTrue(Strings.hasLength(5).test("value"));
        assertFalse(Strings.hasLength(5).test("values"));

        assertFalse(Strings.lengthInside(1, 5).test(null));
        assertTrue(Strings.lengthInside(1, 5).test("v"));
        assertTrue(Strings.lengthInside(1, 5).test("val"));
        assertTrue(Strings.lengthInside(1, 5).test("valu"));
        assertFalse(Strings.lengthInside(1, 5).test("value"));

        assertFalse(Strings.lengthBetween(1, 5).test(null));
        assertTrue(Strings.lengthBetween(1, 5).test("v"));
        assertTrue(Strings.lengthBetween(1, 5).test("val"));
        assertTrue(Strings.lengthBetween(1, 5).test("value"));
        assertFalse(Strings.lengthBetween(1, 5).test("values"));

        assertFalse(Strings.lengthGreaterThan(3).test(null));
        assertTrue(Strings.lengthGreaterThan(3).test("value"));
        assertFalse(Strings.lengthGreaterThan(3).test("val"));

        assertFalse(Strings.lengthGreaterOrEqual(3).test(null));
        assertTrue(Strings.lengthGreaterOrEqual(3).test("val"));
        assertFalse(Strings.lengthGreaterOrEqual(3).test("va"));

        assertFalse(Strings.lengthLessThan(5).test(null));
        assertTrue(Strings.lengthLessThan(5).test("val"));
        assertFalse(Strings.lengthLessThan(5).test("value"));

        assertFalse(Strings.lengthLessOrEqual(5).test(null));
        assertTrue(Strings.lengthLessOrEqual(5).test("value"));
        assertFalse(Strings.lengthLessOrEqual(5).test("values"));
    }

    @Test
    void testTemporals() {
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);
        LocalDate tomorrow = now.plusDays(1);

        assertFalse(Check.Temporals.isBefore(now).test(null));
        assertTrue(Check.Temporals.isBefore(now).test(yesterday));
        assertFalse(Check.Temporals.isBefore(now).test(tomorrow));

        assertFalse(Check.Temporals.isAfter(now).test(null));
        assertTrue(Check.Temporals.isAfter(now).test(tomorrow));
        assertFalse(Check.Temporals.isAfter(now).test(yesterday));

        assertFalse(Check.Temporals.isBeforeOrEqual(now).test(null));
        assertTrue(Check.Temporals.isBeforeOrEqual(now).test(yesterday));
        assertTrue(Check.Temporals.isBeforeOrEqual(now).test(now));
        assertFalse(Check.Temporals.isBeforeOrEqual(now).test(tomorrow));

        assertFalse(Check.Temporals.isAfterOrEqual(now).test(null));
        assertTrue(Check.Temporals.isAfterOrEqual(now).test(tomorrow));
        assertTrue(Check.Temporals.isAfterOrEqual(now).test(now));
        assertFalse(Check.Temporals.isAfterOrEqual(now).test(yesterday));
    }
}