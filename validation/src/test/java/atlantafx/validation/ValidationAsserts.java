/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import javafx.beans.property.ReadOnlyObjectProperty;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("UnusedReturnValue")
public final class ValidationAsserts {

    private ValidationAsserts() { }

    public static ResultAssert assertThat(Result actual) {
        return new ResultAssert(actual);
    }

    public static ResultAssert assertThat(ReadOnlyObjectProperty<Result> actual) {
        return new ResultAssert(actual.get());
    }

    public static FailureAssert assertThat(Failure actual) {
        return new FailureAssert(actual);
    }

    public static SingleFailureAssert assertThat(Failure.Single<?> actual) {
        return new SingleFailureAssert(actual);
    }

    public static CompositeFailureAssert assertThat(Failure.Composite actual) {
        return new CompositeFailureAssert(actual);
    }

    public abstract static class AbstractFailureAssert<SELF
        extends AbstractFailureAssert<SELF, ACTUAL>, ACTUAL extends Failure>
        extends AbstractAssert<SELF, ACTUAL> {

        protected AbstractFailureAssert(ACTUAL actual, Class<?> selfType) {
            super(actual, selfType);
        }

        public SELF hasFailCount(int expected) {
            isNotNull();
            Assertions.assertThat(actual.violations())
                .as("violations of <%s>", actual)
                .hasSize(expected);
            return myself;
        }

        public SELF hasViolation(int code, String message) {
            return hasViolationAt(0, code, message);
        }

        public SELF hasViolationAt(int index, int code, String message) {
            isNotNull();
            Violation violation = violationAt(index);
            Assertions.assertThat(violation.code()).as("violations[%d].code", index).isEqualTo(code);
            Assertions.assertThat(violation.message()).as("violations[%d].message", index).isEqualTo(message);
            return myself;
        }

        public SELF hasFailCodeAt(int index, int expectedCode) {
            isNotNull();
            Assertions.assertThat(violationAt(index).code())
                .as("violations[%d].code", index)
                .isEqualTo(expectedCode);
            return myself;
        }

        public SELF hasFailCode(int expectedCode) {
            return hasFailCodeAt(0, expectedCode);
        }

        public SELF hasFailCodes(int... codes) {
            isNotNull();
            Assertions.assertThat(actual.violations())
                .extracting(Violation::code)
                .containsExactly(java.util.stream.IntStream.of(codes).boxed().toArray(Integer[]::new));
            return myself;
        }

        public SELF hasFailMessageAt(int index, @Nullable String expectedMessage) {
            isNotNull();
            Assertions.assertThat(violationAt(index).message())
                .as("violations[%d].message", index)
                .isEqualTo(expectedMessage);
            return myself;
        }

        public SELF hasFailMessage(@Nullable String expectedMessage) {
            return hasFailMessageAt(0, expectedMessage);
        }

        public ListAssert<Violation> violations() {
            isNotNull();
            return Assertions.assertThat(actual.violations());
        }

        private Violation violationAt(int index) {
            return Assertions.assertThat(actual.violations())
                .as("violations of <%s>", actual)
                .element(index, org.assertj.core.api.InstanceOfAssertFactories.type(Violation.class))
                .actual();
        }
    }

    public static class CompositeFailureAssert extends AbstractFailureAssert<CompositeFailureAssert, Failure.Composite> {

        protected CompositeFailureAssert(Failure.Composite actual) {
            super(actual, CompositeFailureAssert.class);
        }

        public CompositeFailureAssert hasFailuresSize(int expected) {
            isNotNull();
            Assertions.assertThat(actual.failures())
                .as("failures of <%s>", actual)
                .hasSize(expected);
            return myself;
        }

        public CompositeFailureAssert isEmpty() {
            isNotNull();
            if (!actual.isEmpty()) {
                failWithMessage("Expecting composite failure to be empty but contained <%d> failure(s):%n <%s>",
                    actual.failures().size(), actual.failures());
            }
            return myself;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        public SingleFailureAssert single(String name) {
            isNotNull();
            var found = actual.get(name);
            if (found.isEmpty()) {
                failWithMessage("Expecting composite failure to contain a Failure.Single with name:%n <%s>%nbut found failures were:%n <%s>",
                    name, actual.failures());
            }
            return new SingleFailureAssert(found.get());
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        public SingleFailureAssert single(int index) {
            isNotNull();
            var found = actual.get(index);
            if (found.isEmpty()) {
                failWithMessage("Expecting composite failure to contain a Failure.Single at index <%d> but found failures were:%n <%s>",
                    index, actual.failures());
            }
            return new SingleFailureAssert(found.get());
        }
    }

    public static class FailureAssert extends AbstractFailureAssert<FailureAssert, Failure> {

        protected FailureAssert(Failure actual) {
            super(actual, FailureAssert.class);
        }

        @SuppressWarnings("all")
        public SingleFailureAssert asSingle() {
            isNotNull();
            if (!(actual instanceof Failure.Single<?> single)) {
                failWithMessage("Expecting actual to be an instance of:%n <%s>%nbut was:%n <%s>",
                    Failure.Single.class.getName(), actual.getClass().getName());
            }

            return new SingleFailureAssert((Failure.Single<?>) actual);
        }

        @SuppressWarnings("all")
        public CompositeFailureAssert asComposite() {
            isNotNull();
            if (!(actual instanceof Failure.Composite composite)) {
                failWithMessage("Expecting actual to be an instance of:%n <%s>%nbut was:%n <%s>",
                    Failure.Composite.class.getName(), actual.getClass().getName());
            }
            return new CompositeFailureAssert((Failure.Composite) actual);
        }
    }

    public static class ResultAssert extends AbstractAssert<ResultAssert, Result> {

        protected ResultAssert(Result actual) {
            super(actual, ResultAssert.class);
        }

        public ResultAssert isInitial() {
            isNotNull();
            if (!actual.initial()) {
                failWithMessage("Expecting result to be initial but it was:%n <%s>", actual);
            }
            return this;
        }

        public ResultAssert isValid() {
            isNotNull();
            if (!actual.valid()) {
                failWithMessage("Expecting result to be valid but it was:%n <%s>", actual);
            }
            return this;
        }

        public ResultAssert isInvalid() {
            isNotNull();
            if (actual.valid()) {
                failWithMessage("Expecting result to be invalid but it was:%n <%s", actual);
            }
            return this;
        }

        public ResultAssert hasName(String expected) {
            isNotNull();
            if (!Objects.equals(actual.name(), expected)) {
                failWithMessage("Expecting name of:%n <%s>%nto be:%n <%s>%nbut was:%n <%s>",
                    actual, expected, actual.name());
            }
            return this;
        }

        public ResultAssert hasAttribute(String key, @Nullable String value) {
            isNotNull();
            Assertions.assertThat(actual.descriptor().attributes()).containsEntry(key, value);
            return this;
        }

        public AbortAssert isAborted() {
            isNotNull();
            if (!actual.aborted()) {
                failWithMessage("Expecting result to be aborted but was:%n <%s>", actual);
            }
            return new AbortAssert((Result.Abort) actual);
        }

        public FailureAssert isFailure() {
            isInvalid();
            return new FailureAssert((Failure) actual);
        }

        @SuppressWarnings("all")
        public SingleFailureAssert isSingleFailure() {
            isInvalid();
            if (!(actual instanceof Failure.Single<?> single)) {
                failWithMessage("Expecting result to be Failure.Single but was:%n <%s>", actual.getClass().getName());
            }
            return new SingleFailureAssert((Failure.Single<?>) actual);
        }

        @SuppressWarnings("all")
        public CompositeFailureAssert isCompositeFailure() {
            isInvalid();
            if (!(actual instanceof Failure.Composite composite)) {
                failWithMessage("Expecting result to be Failure.Composite but was:%n <%s>", actual.getClass().getName());
            }
            return new CompositeFailureAssert((Failure.Composite) actual);
        }
    }

    public static class SingleFailureAssert extends AbstractFailureAssert<SingleFailureAssert, Failure.Single<?>> {

        protected SingleFailureAssert(Failure.Single<?> actual) {
            super(actual, SingleFailureAssert.class);
        }

        public SingleFailureAssert hasAttemptedValue(Object expected) {
            isNotNull();
            if (!Objects.equals(actual.attemptedValue(), expected)) {
                failWithMessage("Expecting attemptedValue of:%n <%s>%nto be:%n <%s>%nbut was:%n <%s>",
                    actual, expected, actual.attemptedValue());
            }
            return myself;
        }

        public SingleFailureAssert hasNullAttemptedValue() {
            return hasAttemptedValue(null);
        }
    }

    public static class AbortAssert extends AbstractAssert<AbortAssert, Result.Abort> {

        protected AbortAssert(Result.Abort actual) {
            super(actual, AbortAssert.class);
        }

        public AbortAssert hasRuleName(String expected) {
            isNotNull();
            if (!Objects.equals(actual.ruleName(), expected)) {
                failWithMessage("Expecting ruleName of:%n <%s>%nto be:%n <%s>%nbut was:%n <%s>",
                    actual, expected, actual.ruleName());
            }
            return myself;
        }
    }
}