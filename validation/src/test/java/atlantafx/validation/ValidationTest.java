/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Subscription;
import atlantafx.validation.actions.Action;
import atlantafx.validation.l10n.format_messages;
import atlantafx.validation.l10n.test_messages;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static atlantafx.validation.Check.Strings;
import static atlantafx.validation.ValidationAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Nested
    class ResultTest {

        @Test
        @DisplayName("result states should be mutually exclusive: success")
        void testSuccessState() {
            var result = Result.Success.EMPTY;
            Assertions.assertThat(result.valid()).isTrue();
            Assertions.assertThat(result.invalid()).isFalse();
            Assertions.assertThat(result.aborted()).isFalse();
        }

        @Test
        @DisplayName("result states should be mutually exclusive: failure")
        void testFailureState() {
            var result = Failure.EMPTY;
            Assertions.assertThat(result.valid()).isFalse();
            Assertions.assertThat(result.invalid()).isTrue();
            Assertions.assertThat(result.aborted()).isFalse();
        }

        @Test
        @DisplayName("result states should be mutually exclusive: aborted")
        void testAbortState() {
            var result = new Result.Abort(Descriptor.EMPTY, new IllegalStateException("abort"), "");
            Assertions.assertThat(result.valid()).isFalse();
            Assertions.assertThat(result.invalid()).isFalse();
            Assertions.assertThat(result.aborted()).isTrue();
        }

        @Test
        @DisplayName("rule should provide correct descriptor")
        void testRuleDescriptor() {
            var prop = new SimpleStringProperty("valid");

            var rule = Rule.on(prop, "rule1")
                .must("valid"::equals)
                .attribute("foo", "bar")
                .onSuccess(_ -> { })
                .onFailure(_ -> { })
                .onException(_ -> { })
                .doFinally(_ -> { });

            assertThat(rule.revalidate())
                .hasName("rule1")
                .isValid()
                .hasAttribute("foo", "bar");

            prop.set("invalid");
            assertThat(rule.revalidate())
                .hasName("rule1")
                .isInvalid()
                .hasAttribute("foo", "bar");

            rule = Rule.on(prop, "rule2")
                .must(_ -> { throw new RuntimeException("Check failed"); })
                .attribute("bar", "baz")
                .onException(_ -> { });

            assertThat(rule.revalidate())
                .hasName("rule2")
                .hasAttribute("bar", "baz")
                .isAborted()
                .hasRuleName("rule2");
        }

        @Test
        @DisplayName("rule set should provide correct descriptor")
        void testRuleSetDescriptor() {
            var prop = new SimpleStringProperty("valid");

            var rule1 = Rule.on(prop, "rule1")
                .must("valid"::equals)
                .onSuccess(_ -> { })
                .onFailure(_ -> { })
                .onException(_ -> { })
                .doFinally(_ -> { });
            var ruleSet1 = RuleSet.of("RuleSet1", rule1).attribute("foo", "bar");

            assertThat(ruleSet1.revalidate())
                .hasName("RuleSet1")
                .isValid()
                .hasAttribute("foo", "bar");

            prop.set("invalid");
            assertThat(ruleSet1.revalidate())
                .hasName("RuleSet1")
                .isInvalid()
                .hasAttribute("foo", "bar");

            var rule2 = Rule.on(prop, "rule2")
                .must(_ -> { throw new RuntimeException("Check failed"); })
                .onException(_ -> { });
            var ruleSet2 = RuleSet.of("RuleSet2", rule2).attribute("bar", "baz");

            assertThat(ruleSet2.revalidate())
                .hasName("RuleSet2")
                .hasAttribute("bar", "baz")
                .isAborted()
                .hasRuleName("rule2");
        }
    }

    @Nested
    class RuleTest {

        @Test
        @DisplayName("empty rule should not fail")
        void testEmptyRule() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop);

            assertNotNull(rule.name(), "Default name should not be null");
            assertEquals(Cascade.CONTINUE, rule.cascade(), "Default cascade should be CONTINUE");
        }

        @Test
        @DisplayName("rule should use source property name")
        void testRuleUsesPropertyName() {
            var prop = new SimpleStringProperty(this, "propName");
            var rule = Rule.on(prop, null); // name is null

            assertEquals("propName", rule.name());
        }

        @Test
        @DisplayName("multiple checks should be allowed to chain")
        void testChainingMultipleChecks() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop)
                .must(Objects::isNull).failCode(1)
                .must(Strings.contains("X")).failCode(2)
                .must(Strings.startsWith("Y")).failCode(3)
                .must(Strings.endsWith("Z")).failCode(4)
                .cascade(Cascade.CONTINUE);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCount(4);
        }

        @Test
        @DisplayName("basic validation should succeed")
        void testBasicValidationSuccess() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop, "username")
                .must(Objects::nonNull)
                .failCode(100)
                .failMessage("Cannot be null");

            assertThat(rule.revalidate()).isValid();
            assertThat(rule.result()).isValid();
        }

        @Test
        @DisplayName("basic validation should fail")
        void testBasicValidationFailure() {
            var prop = new SimpleStringProperty(null);
            var rule = Rule.on(prop, "username")
                .must(Objects::nonNull)
                .failCode(100)
                .failMessage("Cannot be null");

            var result = rule.revalidate();
            assertThat(result)
                .hasName("username")
                .isSingleFailure()
                .hasNullAttemptedValue()
                .hasFailCount(1)
                .hasViolation(100, "Cannot be null");
        }

        @Test
        @DisplayName("Cascade.STOP should stop on first failure")
        void testCascadeStop() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop)
                .must(_ -> false).failCode(1)
                .must(_ -> false).failCode(2)
                .cascade(Cascade.STOP);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCount(1)
                .hasFailCode(1);
        }

        @Test
        @DisplayName("Cascade.CONTINUE should evaluate all checks")
        void testCascadeContinue() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop)
                .must(_ -> false).failCode(1)
                .must(_ -> false).failCode(2)
                .cascade(Cascade.CONTINUE);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCount(2)
                .hasFailCodes(1, 2);
        }

        @Test
        @DisplayName("success handler should trigger when validation passes")
        void testSuccessHandler() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop).must("valid"::equals);

            var succeeded = new AtomicBoolean(false);
            rule.onSuccess(_ -> succeeded.set(true));

            rule.revalidate(); // fails
            assertFalse(succeeded.get());

            prop.set("valid");
            rule.revalidate(); // passes
            assertTrue(succeeded.get());
        }

        @Test
        @DisplayName("failure handler should trigger when validation fails")
        void testFailureHandler() {
            var prop = new SimpleStringProperty("valid");
            var rule = Rule.on(prop).must("valid"::equals);

            var failed = new AtomicBoolean(false);
            rule.onFailure(_ -> failed.set(true));

            rule.revalidate(); // passes
            assertFalse(failed.get());

            prop.set("invalid");
            rule.revalidate(); // fails
            assertTrue(failed.get());
        }

        @Test
        @DisplayName("rule handlers should trigger accordingly")
        void testRuleHandlers() {
            var prop = new SimpleStringProperty("invalid");
            var failed = new AtomicBoolean(false);
            var succeeded = new AtomicBoolean(false);

            var rule = Rule.on(prop)
                .must(Check.Objects.isEqual("valid"))
                .onFailure(_ -> failed.set(true))
                .onSuccess(_ -> succeeded.set(true));

            rule.revalidate();
            assertTrue(failed.get(), "onFailure() should be triggered on failure");
            assertFalse(succeeded.get(), "onSuccess() should not be triggered on failure");

            failed.set(false);
            prop.set("valid");
            rule.revalidate();

            assertFalse(failed.get(), "onFailure() should not be triggered on success");
            assertTrue(succeeded.get(), "onSuccess() should be triggered on success");
        }

        @Test
        @DisplayName("rule should throw exception when no exception handler")
        void testThrowsExceptionWithoutHandler() {
            var prop = new SimpleStringProperty("test");
            var expectedException = new RuntimeException("Uncaught error");
            var rule = Rule.on(prop).must(_ -> { throw expectedException; });

            assertThatThrownBy(rule::revalidate).isSameAs(expectedException);
        }

        @Test
        @DisplayName("exception handler should trigger when exception thrown")
        void testExceptionHandler() {
            var prop = new SimpleStringProperty("test");
            var expectedException = new RuntimeException("Check failed");
            var caughtException = new AtomicReference<Exception>();
            var rule = Rule.on(prop)
                .must(_ -> { throw expectedException; })
                .onException(r -> caughtException.set(r.exception()));

            assertThat(rule.revalidate()).isAborted();
            assertThat(rule.result()).isAborted();
            Assertions.assertThat(caughtException.get()).isSameAs(expectedException);
        }

        @Test
        @DisplayName("finally handler should trigger when validation passes")
        void testFinallyHandlerOnSuccess() {
            var prop = new SimpleStringProperty("valid");
            var finalized = new AtomicBoolean(false);
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .doFinally(_ -> finalized.set(true));

            assertThat(rule.revalidate()).isValid();
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger when validation fails")
        void testFinallyHandlerOnFailure() {
            var prop = new SimpleStringProperty("invalid");
            var finallyExecuted = new AtomicBoolean(false);
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .doFinally(_ -> finallyExecuted.set(true));

            assertThat(rule.revalidate()).isInvalid();
            Assertions.assertThat(finallyExecuted.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger on caught exception")
        void testFinallyHandlerOnException() {
            var prop = new SimpleStringProperty("test");
            var finalized = new AtomicBoolean(false);
            var rule = Rule.on(prop)
                .must(_ -> { throw new RuntimeException("Check error"); })
                .onException(_ -> { })
                .doFinally(_ -> finalized.set(true));

            rule.revalidate();
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger on unhandled exception")
        void testFinallyHandlerOnUnhandledException() {
            var prop = new SimpleStringProperty("test");
            var finalized = new AtomicBoolean(false);
            var rule = Rule.on(prop)
                .must(_ -> { throw new RuntimeException("Unhandled error"); })
                .doFinally(_ -> finalized.set(true));

            assertThatThrownBy(rule::revalidate).isInstanceOf(RuntimeException.class);
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("calling 'failCode' prior to 'must' should throw")
        void testSetCodeBeforeMustThrows() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop);

            assertThrows(
                IllegalStateException.class,
                () -> rule.failCode(100),
                "Should throw IllegalStateException because must() was not called first"
            );
        }

        @Test
        @DisplayName("calling 'failMessage' prior to 'must' should throw")
        void testSetMessageBeforeMustThrows() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop);

            assertThrows(
                IllegalStateException.class,
                () -> rule.failMessage("Error"),
                "Should throw IllegalStateException because must() was not called first"
            );
        }

        @Test
        @DisplayName("deferred validation should not run until revalidation")
        void testDeferredValidation() {
            var prop = new SimpleStringProperty("valid");
            var rule = Rule.on(prop).must("valid"::equals);

            prop.set("invalid");
            assertThat(rule.result())
                .as("Result should stay at initial success before manual revalidation")
                .isValid();

            rule.revalidate();
            assertThat(rule.result())
                .as("Result should populate failure after revalidate() is called")
                .isInvalid();
        }

        @Test
        @DisplayName("immediate validation should run automatically")
        void testImmediateValidation() {
            var prop = new SimpleStringProperty("valid");
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .immediate();

            assertThat(rule.result())
                .as("Initial state should be set to success")
                .isValid();

            prop.set("invalid");
            assertThat(rule.result())
                .as("Immediate rule should automatically revalidate on property change")
                .isSingleFailure()
                .hasAttemptedValue("invalid");
        }


        @Test
        @DisplayName("exception handler should trigger on immediate property change")
        void testOnExceptionWithImmediate() {
            var prop = new SimpleStringProperty("initial");
            var caughtException = new AtomicReference<Exception>();
            var rule = Rule.on(prop)
                .must(v -> {
                    if ("throw".equals(v)) {
                        throw new RuntimeException("Immediate error");
                    }
                    return true;
                })
                .onException(r -> caughtException.set(r.exception()))
                .immediate();

            prop.set("throw");

            assertThat(rule.result()).isAborted();
            Assertions.assertThat(caughtException.get())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Immediate error");
        }

        @Test
        @DisplayName("rule should stop remaining checks on exception regardless of cascade mode")
        void testExceptionHandlerCascade() {
            var prop = new SimpleStringProperty("test");
            var callCount = new AtomicInteger(0);
            var caughtException = new AtomicReference<Exception>();
            var rule = Rule.on(prop)
                .must(_ -> { throw new RuntimeException("First check crashed"); })
                .must(_ -> {
                    callCount.incrementAndGet();
                    return true;
                })
                .cascade(Cascade.CONTINUE)
                .onException(r -> caughtException.set(r.exception()));

            assertThat(rule.revalidate()).isAborted();
            Assertions.assertThat(callCount.get()).isEqualTo(0);
            Assertions.assertThat(caughtException.get()).hasMessage("First check crashed");
        }

        @Test
        @DisplayName("finally handler should trigger under Cascade.STOP when early check fails")
        void testFinallyHandlerCascadeStop() {
            var prop = new SimpleStringProperty("invalid");
            var finalized = new AtomicBoolean(false);
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .must(_ -> true)
                .cascade(Cascade.STOP)
                .doFinally(_ -> finalized.set(true));

            assertThat(rule.revalidate()).isInvalid();
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger under Cascade.CONTINUE after all checks")
        void testFinallyHandlerCascadeContinue() {
            var prop = new SimpleStringProperty("invalid");
            var checkCount = new AtomicInteger(0);
            var finalized = new AtomicBoolean(false);
            var rule = Rule.on(prop)
                .must(_ -> {
                    checkCount.incrementAndGet();
                    return false;
                })
                .must(_ -> {
                    checkCount.incrementAndGet();
                    return false;
                })
                .cascade(Cascade.CONTINUE)
                .doFinally(_ -> finalized.set(true));

            assertThat(rule.revalidate()).isInvalid();
            Assertions.assertThat(checkCount.get()).isEqualTo(2);
            Assertions.assertThat(finalized.get()).isTrue();
        }
    }

    @Nested
    class RuleConditionsTest {

        @Test
        @DisplayName("precondition 'when' = true should evaluate check")
        void testWhenTrueEvaluatesCheck() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .when(_ -> true)
                .failCode(100);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCode(100);
        }

        @Test
        @DisplayName("precondition 'when' = false should skip check")
        void testWhenFalseSkipsCheck() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .when(_ -> false)
                .failCode(100);

            assertThat(rule.revalidate()).isValid();
        }

        @Test
        @DisplayName("precondition 'unless' = true should skip check")
        void testUnlessTrueSkipsCheck() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .unless(_ -> true)
                .failCode(100);

            assertThat(rule.revalidate()).isValid();
        }

        @Test
        @DisplayName("precondition 'unless' = false should evaluate check")
        void testUnlessFalseEvaluatesCheck() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .unless(_ -> false)
                .failCode(100);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCode(100);
        }

        @Test
        @DisplayName("'when' after 'unless' should override condition")
        void testWhenOverridesUnless() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .unless(_ -> true)
                .when(_ -> true)
                .failCode(100);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCode(100);
        }

        @Test
        @DisplayName("'unless' after 'when' should override condition")
        void testUnlessOverridesWhen() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .when(_ -> true)
                .unless(_ -> true)
                .failCode(100);

            assertThat(rule.revalidate()).isValid();
        }

        @Test
        @DisplayName("multiple checks should apply conditions independently")
        void testMultipleChecksIndependentConditions() {
            var prop = new SimpleStringProperty("abc");
            var rule = Rule.on(prop)
                .must(Strings.lengthGreaterThan(5)).when(_ -> true).failCode(1) // fails
                .must(Objects::isNull).when(_ -> false).failCode(2)                // skipped
                .must(Strings.contains("z")).when(_ -> true).failCode(3);          // fails

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCount(2)
                .hasFailCodeAt(0, 1)
                .hasFailCodeAt(1, 3);
        }

        @Test
        @DisplayName("Cascade.STOP should ignore subsequent checks if previous condition met")
        void testCascadeStopWithConditions() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop)
                .must(_ -> false).when(_ -> true).failCode(1) // fails & stops stream
                .must(_ -> false).when(_ -> true).failCode(2) // should not run
                .cascade(Cascade.STOP);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCount(1)
                .hasFailCode(1);
        }

        @Test
        @DisplayName("skipped check should not trigger cascade STOP")
        void testSkippedCheckDoesNotTriggerCascadeStop() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop)
                .must(_ -> false).when(_ -> false).failCode(1) // skipped, does not stop
                .must(_ -> false).when(_ -> true).failCode(2)  // fails
                .cascade(Cascade.STOP);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCount(1)
                .hasFailCode(2);
        }

        @Test
        @DisplayName("calling 'when' before 'must' should throw exception")
        void testWhenBeforeMustThrowsException() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop);

            assertThrows(IllegalStateException.class, () -> rule.when(_ -> true));
        }

        @Test
        @DisplayName("precondition should receive attempted value")
        void testWhenConditionReceivesAttemptedValue() {
            var prop = new SimpleStringProperty("expected_value");
            var rule = Rule.on(prop)
                .must(_ -> false)
                .when("expected_value"::equals)
                .failCode(100);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailCode(100);
        }
    }

    @Nested
    class PreconditionsTest {

        @Test
        @DisplayName("'given' should control validation")
        void testGiven() {
            var prop = new SimpleStringProperty("test");
            var enableValidation = new SimpleBooleanProperty(false);

            var rule = Rule.on(prop)
                .must(_ -> false)
                .given(enableValidation, v -> v != null && v);

            assertThat(rule.revalidate())
                .as("Should skip validation because given() precondition is not met")
                .isValid();

            enableValidation.set(true);
            assertThat(rule.revalidate()).isInvalid();
        }

        @Test
        @DisplayName("'givenNot' should control validation")
        void testGivenNot() {
            var prop = new SimpleStringProperty("test");
            var skipValidation = new SimpleBooleanProperty(true);

            var rule = Rule.on(prop)
                .must(_ -> false)
                .givenNot(skipValidation, v -> v != null && v);

            assertThat(rule.revalidate())
                .as("Should skip validation because givenNot() precondition is met")
                .isValid();

            skipValidation.set(false);
            assertThat(rule.revalidate()).isInvalid();
        }

        @Test
        @DisplayName("success handler should be called when precondition fails")
        void testOnSuccessCalledOnPreconditionFailure() {
            var prop = new SimpleStringProperty("invalid");
            var condition = new SimpleBooleanProperty(false);
            var succeeded = new AtomicBoolean(false);

            var rule = Rule.on(prop)
                .must("valid"::equals)
                .given(condition, c -> c != null && c)
                .onSuccess(_ -> succeeded.set(true));

            assertThat(rule.revalidate())
                .as("Validation should be skipped due to given() condition")
                .isValid();
            assertTrue(succeeded.get(), "onSuccess() should be executed when precondition fails");
        }

        @Test
        @DisplayName("multiple preconditions should short-circuit evaluation")
        void testMultiplePreconditionsShortCircuitEvaluation() {
            var prop = new SimpleStringProperty("admin");

            var rule = Rule.on(prop)
                .must(_ -> false) // would fail if executed
                .given(Objects::nonNull)
                .givenNot(val -> val != null && val.startsWith("admin"));

            assertThat(rule.revalidate())
                .as("Validation should be skipped due to givenNot() condition")
                .isValid();

            prop.set("user"); // updating value so that all preconditions pass
            assertThat(rule.revalidate())
                .as("Validation should fail due to givenNot() condition becomes false")
                .isInvalid();
        }

        @Test
        @DisplayName("rule should reject duplicate 'given'")
        void testRejectsDuplicateGiven() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop);
            Predicate<String> condition = String::isEmpty;

            rule.given(condition);
            assertThrows(IllegalArgumentException.class, () -> rule.given(condition));
        }

        @Test
        @DisplayName("rule should reject duplicate 'givenNot'")
        void testRejectsDuplicateGivenNot() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop);
            Predicate<String> condition = String::isEmpty;

            rule.given(condition);
            assertThrows(IllegalArgumentException.class, () -> rule.givenNot(condition));
        }

        @Test
        @DisplayName("rule should reject duplicate across 'given'/'givenNot'")
        void testRejectsDuplicateDependencyAcrossGivenNot() {
            var prop = new SimpleStringProperty("test");
            Predicate<String> isNull = Objects::isNull;

            var rule = Rule.on(prop).given(isNull);
            assertThrows(IllegalArgumentException.class, () -> rule.givenNot(isNull));
        }

        @Test
        @DisplayName("given/givenNot duplicate check misses distinct instances")
        void testDuplicateConditionsCheckMissesDistinctInstances() {
            var prop = new SimpleStringProperty("test");

            // the check relies on object equality and cannot detect logically identical
            // conditions if they are passed as distinct instances.
            var rule = Rule.on(prop)
                .given(Objects::isNull)
                .givenNot(Objects::isNull);

            assertNotNull(rule);
        }

        @Test
        @DisplayName("exception handler should trigger in preconditions")
        void testExceptionHandler() {
            var prop = new SimpleStringProperty("test");
            var expectedException = new RuntimeException("Precondition failed");
            var caughtException = new AtomicReference<Exception>();

            var rule = Rule.on(prop)
                .must("test"::equals)
                .given(_ -> { throw expectedException; })
                .onException(r -> caughtException.set(r.exception()));

            Result result = rule.revalidate();

            assertThat(result).isAborted();
            assertThat(rule.result()).isAborted();
            Assertions.assertThat(caughtException.get()).isSameAs(expectedException);
        }

        @Test
        @DisplayName("finally handler should trigger in preconditions")
        void testFinallyHandler() {
            var prop = new SimpleStringProperty("test");
            var finalized = new AtomicBoolean(false);
            var rule = Rule.on(prop)
                .must("test"::equals)
                .given(_ -> { throw new RuntimeException("Precondition error"); })
                .onException(_ -> { })
                .doFinally(_ -> finalized.set(true));

            rule.revalidate();
            Assertions.assertThat(finalized.get()).isTrue();
        }
    }

    @Nested
    class DependenciesTest {

        @Test
        @DisplayName("'given' should automatically trigger revalidation")
        void testGivenTriggersRevalidation() {
            var prop = new SimpleStringProperty("invalid");
            var enableValidation = new SimpleBooleanProperty(false);

            var rule = Rule.on(prop)
                .must("valid"::equals)
                .given(enableValidation, Boolean.TRUE::equals);

            assertThat(rule.revalidate()) // manual validation when enableValidation is false should skip checks
                .as("Should skip validation because given() precondition is not met")
                .isValid();

            enableValidation.set(true); // resetting flag should automatically restart validation
            assertThat(rule.result())
                .as("Should automatically revalidate and fail")
                .isInvalid();

            enableValidation.set(false); // reverting dependency to false should automatically clear the result
            assertThat(rule.result())
                .as("Should automatically revalidate and clear result")
                .isValid();
        }

        @Test
        @DisplayName("'givenNot' should automatically trigger revalidation")
        void testGivenNotTriggersRevalidation() {
            var prop = new SimpleStringProperty("invalid");
            var skipValidation = new SimpleBooleanProperty(true);

            var rule = Rule.on(prop)
                .must("valid"::equals)
                .givenNot(skipValidation, Boolean.TRUE::equals);

            assertThat(rule.revalidate()) // manual validation when skipValidation is true should skip checks
                .as("Should ignore validation because givenNot() condition is met")
                .isValid();

            skipValidation.set(false); // resetting flag should automatically restart validation
            assertThat(rule.result())
                .as("Should automatically revalidate and fail")
                .isInvalid();

            skipValidation.set(true); // reverting the flag should automatically clear errors again
            assertThat(rule.result())
                .as("Should automatically revalidate and clear result")
                .isValid();
        }

        @Test
        @DisplayName("rule should reject dependency on source property")
        void testRuleRejectsDependencyOnSourceProperty() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop).must("test"::equals);

            assertThrows(IllegalArgumentException.class, () -> rule.given(prop, _ -> true));
            assertThrows(IllegalArgumentException.class, () -> rule.givenNot(prop, _ -> true));
        }

        @Test
        @DisplayName("rule should reject duplicate 'given' dependency")
        void testRejectsDuplicateGivenDependency() {
            var prop = new SimpleStringProperty("source");
            var dep = new SimpleStringProperty("dep");
            var rule = Rule.on(prop).given(dep, Objects::isNull);

            assertThrows(IllegalArgumentException.class, () -> rule.given(dep, Objects::isNull));
        }

        @Test
        @DisplayName("rule should reject duplicate 'givenNot' dependency")
        void testRejectsDuplicateGivenNotDependency() {
            var prop = new SimpleStringProperty("source");
            var dep = new SimpleStringProperty("dep");
            var rule = Rule.on(prop).givenNot(dep, Objects::isNull);

            assertThrows(IllegalArgumentException.class, () -> rule.givenNot(dep, Objects::isNull));
        }

        @Test
        @DisplayName("rule should reject duplicate dependency across 'given'/'givenNot'")
        void testRejectsDuplicateDependencyAcrossGivenNot() {
            var prop = new SimpleStringProperty("test");
            var dep = new SimpleStringProperty("dep");
            var rule = Rule.on(prop).given(dep, Objects::isNull); // given dep

            assertThrows(IllegalArgumentException.class, () -> rule.givenNot(dep, Objects::isNull)); // given not dep
        }

        @Test
        @DisplayName("exception handler should trigger in dependencies")
        void testExceptionHandler() {
            var prop = new SimpleStringProperty("test");
            var dep = new SimpleStringProperty("dep");
            var expectedException = new RuntimeException("Dependency failed");
            var caughtException = new AtomicReference<Exception>();
            var rule = Rule.on(prop)
                .must("test"::equals)
                .given(dep, _ -> { throw expectedException; })
                .onException(r -> caughtException.set(r.exception()));

            assertThat(rule.revalidate()).isAborted();
            assertThat(rule.result()).isAborted();
            Assertions.assertThat(caughtException.get()).isSameAs(expectedException);
        }

        @Test
        @DisplayName("finally handler should trigger in dependencies")
        void testFinallyHandler() {
            var prop = new SimpleStringProperty("test");
            var dep = new SimpleStringProperty("dep");
            var finalized = new AtomicBoolean(false);
            var rule = Rule.on(prop)
                .must("test"::equals)
                .given(dep, _ -> { throw new RuntimeException("Dependency error"); })
                .onException(_ -> { })
                .doFinally(_ -> finalized.set(true));

            rule.revalidate();
            Assertions.assertThat(finalized.get()).isTrue();
        }
    }

    @Nested
    class ChildRulesTest {

        @Test
        @DisplayName("child rules should revalidate automatically")
        void testChildRulesRevalidation() {
            var parentProp = new SimpleStringProperty("parent");
            var childProp = new SimpleStringProperty("child_invalid");

            var childRule = Rule.on(childProp).must("child_valid"::equals);
            var parentRule = Rule.on(parentProp)
                .must("parent"::equals)
                .childRules(childRule);

            assertThat(childRule.result())
                .as("Child rule result should initially be set to success")
                .isValid();

            parentRule.revalidate(); // should cascade revalidate() to childRule
            assertThat(childRule.result())
                .as("Child rule should be revalidated by the parent rule")
                .isInvalid();
        }

        @Test
        @DisplayName("parent should trigger revalidation on all child rules")
        void testAllChildRulesRevalidation() {
            var parentProp = new SimpleStringProperty("parent");
            var childProp1 = new SimpleStringProperty("child1_bad");
            var childProp2 = new SimpleStringProperty("child2_bad");
            var childProp3 = new SimpleStringProperty("child3_bad");

            var childRule1 = Rule.on(childProp1).must("child1_ok"::equals);
            var childRule2 = Rule.on(childProp2).must("child2_ok"::equals);
            var childRule3 = Rule.on(childProp3).must("child3_ok"::equals);

            var parentRule = Rule.on(parentProp)
                .must("parent"::equals)
                .childRules(childRule1, childRule2, childRule3);

            parentRule.revalidate();
            assertThat(childRule1.result()).as("1st dependent rule should be revalidated").isInvalid();
            assertThat(childRule2.result()).as("2nd dependent rule should be revalidated").isInvalid();
            assertThat(childRule3.result()).as("3rd dependent rule should be revalidated").isInvalid();
        }

        @Test
        @DisplayName("rule should prevent adding self to child rules")
        void testAddingSelfToChildRules() {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop).must("parent"::equals);

            assertThrows(IllegalArgumentException.class, () -> rule.childRules(rule));
        }

        @Test
        @DisplayName("rule should reject child rule for the same property")
        void testRejectsChildRuleForSameProperty() {
            var prop = new SimpleStringProperty("test");
            var rule1 = Rule.on(prop).must("test"::equals);
            var rule2 = Rule.on(prop).must("test"::equals);

            assertThrows(IllegalArgumentException.class, () -> rule1.childRules(rule2));
            assertThrows(IllegalArgumentException.class, () -> rule2.childRules(rule1));
        }

        @Test
        @DisplayName("rule should reject duplicate child rules")
        void testRejectsDuplicateChildRules() {
            var prop1 = new SimpleStringProperty("test1");
            var prop2 = new SimpleStringProperty("test2");
            var parent = Rule.on(prop1).must("test"::equals);
            var child = Rule.on(prop2).must("test"::equals);

            assertThrows(IllegalArgumentException.class, () -> parent.childRules(child, child));
        }

        @Test
        @DisplayName("rule should reject child rules targeting the same property")
        void testRejectsChildRulesSameSourceProperty() {
            var prop1 = new SimpleStringProperty("parent");
            var prop2 = new SimpleStringProperty("child");

            var parent = Rule.on(prop1).must("test"::equals);
            var child1 = Rule.on(prop2).must("test"::equals);
            var child2 = Rule.on(prop2).must("other"::equals);

            assertThrows(IllegalArgumentException.class, () -> parent.childRules(child1, child2));
        }
    }

    @Nested
    class RuleSetTest {

        @Test
        @DisplayName("empty rule set should not fail")
        void testEmptyRuleSet() {
            var rule1 = Rule.on(new SimpleStringProperty("a"));
            var rule2 = Rule.on(new SimpleStringProperty("b"));
            var ruleSet = RuleSet.of(rule1, rule2);

            assertNotNull(ruleSet.name(), "Default name should not be null");
            assertEquals(Cascade.CONTINUE, ruleSet.cascade(), "Default cascade should be CONTINUE");
            assertEquals(2, ruleSet.rules().size());
            assertThat(ruleSet.result()).isInitial();
        }

        @Test
        @DisplayName("rule set should aggregate results from multiple rules")
        void testRuleSetAggregatesResults() {
            var prop1 = new SimpleStringProperty("valid1");
            var prop2 = new SimpleStringProperty("invalid2");

            var rule1 = Rule.on(prop1).must(Strings.startsWith("valid"));
            var rule2 = Rule.on(prop2).must(Strings.startsWith("valid"));
            var ruleSet = RuleSet.of("RuleSet", rule1, rule2);

            assertThat(ruleSet.result()).isInitial();
            ruleSet.revalidate();

            assertThat(rule1.result()).isValid();
            assertThat(rule2.result()).isInvalid();

            ruleSet.revalidate();
            assertThat(ruleSet.result()).isInvalid();

            prop2.set("valid2");
            ruleSet.revalidate();
            assertThat(ruleSet.result()).isValid();
        }

        @Test
        @DisplayName("rule set Cascade.STOP should stop on first failure")
        void testRuleSetCascadeStop() {
            var prop1 = new SimpleStringProperty("invalid1");
            var prop2 = new SimpleStringProperty("invalid2");

            var rule1 = Rule.on(prop1, "rule1").must(_ -> false);
            var rule2 = Rule.on(prop2, "rule2").must(_ -> false);

            var ruleSet = RuleSet.of(rule1, rule2).cascade(Cascade.STOP);
            assertThat(ruleSet.result()).isInitial();
            ruleSet.revalidate();

            assertThat(rule1.result()).isInvalid();
            assertThat(rule2.result()).isInitial();
        }

        @Test
        @DisplayName("rule set Cascade.CONTINUE should evaluate all rules")
        void testRuleSetCascadeContinue() {
            var prop1 = new SimpleStringProperty("invalid1");
            var prop2 = new SimpleStringProperty("invalid2");

            var rule1 = Rule.on(prop1).must("valid1"::equals);
            var rule2 = Rule.on(prop2).must("valid2"::equals);

            var ruleSet = RuleSet.of(rule1, rule2).cascade(Cascade.CONTINUE);
            ruleSet.revalidate();
            assertThat(ruleSet.result()).isInvalid();

            assertThat(ruleSet.result()).isCompositeFailure();
            assertThat(rule1.result())
                .as("Rule#1 should be evaluated and fail")
                .isInvalid();
            assertThat(rule2.result())
                .as("Rule#2 should be evaluated despite Rule#1 failure")
                .isInvalid();
        }

        @Test
        @DisplayName("rule set should be updated immediately")
        void testRuleSetSubscription() {
            var prop = new SimpleStringProperty("invalid-email");
            var rule = Rule.on(prop)
                .must(Objects::nonNull)
                .must(Strings.contains("@"));

            var ruleSet = RuleSet.of(rule).immediate();
            assertThat(ruleSet.result()).isInvalid();

            prop.set("test@example.org"); // immediate revalidation
            assertThat(ruleSet.result())
                .as("RuleSet should automatically react to rule changes via subscription")
                .isValid();
        }

        @Test
        @DisplayName("rule set 'onSuccess' should trigger when all rules pass")
        void testRuleSetSuccessHandler() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop).must("valid"::equals);
            var ruleSet = RuleSet.of(rule);

            var succeeded = new AtomicBoolean(false);
            ruleSet.onSuccess(_ -> succeeded.set(true));

            ruleSet.revalidate();
            assertFalse(succeeded.get());

            prop.set("valid");
            ruleSet.revalidate();
            assertTrue(succeeded.get());
        }

        @Test
        @DisplayName("rule set 'onFailure' should trigger when any rule fails")
        void testRuleSetFailureHandler() {
            var prop = new SimpleStringProperty("valid");
            var rule = Rule.on(prop).must("valid"::equals);
            var ruleSet = RuleSet.of(rule);

            var failed = new AtomicBoolean(false);
            ruleSet.onFailure(_ -> failed.set(true));

            ruleSet.revalidate();
            assertFalse(failed.get());

            prop.set("invalid");
            ruleSet.revalidate();
            assertTrue(failed.get());
        }

        @Test
        @DisplayName("rule set handlers should be invoked")
        void testRuleSetHandlersFlow() {
            var prop = new SimpleStringProperty("");
            var rule = Rule.on(prop).must(Strings.isNotEmpty());

            var failed = new AtomicBoolean(false);
            var succeeded = new AtomicBoolean(false);

            var ruleSet = RuleSet.of(rule)
                .onFailure(_ -> failed.set(true))
                .onSuccess(_ -> succeeded.set(true));

            ruleSet.revalidate();
            assertTrue(failed.get());
            assertFalse(succeeded.get());

            prop.set("ok");
            ruleSet.revalidate();
            assertTrue(succeeded.get());
        }

        @Test
        @DisplayName("rule set should reject duplicate rule instances")
        void testRejectsDuplicateRuleInstances() {
            var prop1 = new SimpleStringProperty("test1");
            var prop2 = new SimpleStringProperty("test2");

            var rule1 = Rule.on(prop1).must("val1"::equals);
            var rule2 = Rule.on(prop2).must("val2"::equals);

            assertThrows(
                IllegalArgumentException.class,
                () -> RuleSet.of(rule1, rule2, rule1)
            );
        }

        @Test
        @DisplayName("rule set should reject rules targeting the same property")
        void testRejectsRulesWithSameProperty() {
            var prop1 = new SimpleStringProperty("prop1");
            var prop2 = new SimpleStringProperty("prop2");

            var rule1 = Rule.on(prop1).must("val1"::equals);
            var rule2 = Rule.on(prop1).must("val2"::equals);
            var rule3 = Rule.on(prop2).must("val3"::equals);

            assertThrows(
                IllegalArgumentException.class,
                () -> RuleSet.of("MyRuleSet", rule1, rule3, rule2)
            );
        }

        @Test
        @DisplayName("rule set should catch exception thrown by child rule")
        void testExceptionHandler() {
            var prop = new SimpleStringProperty("val");
            var expectedException = new RuntimeException("Child rule exception");
            var caughtException = new AtomicReference<Exception>();

            var rule = Rule.on(prop).must(_ -> { throw expectedException; });
            var ruleSet = RuleSet.of(rule).onException(r -> caughtException.set(r.exception()));

            assertThat(ruleSet.revalidate()).isAborted();
            assertThat(ruleSet.result()).isAborted();
            Assertions.assertThat(caughtException.get()).isSameAs(expectedException);
        }

        @Test
        @DisplayName("rule set should throw exception when no exception handler")
        void testThrowsExceptionWithoutHandler() {
            var prop = new SimpleStringProperty("val");
            var expectedException = new RuntimeException("Uncaught error");

            var rule = Rule.on(prop).must(_ -> { throw expectedException; });
            var ruleSet = RuleSet.of(rule);

            assertThatThrownBy(ruleSet::revalidate).isSameAs(expectedException);
        }

        @Test
        @DisplayName("exception handler should trigger via immediate property change")
        void testExceptionHandlerImmediate() {
            var prop = new SimpleStringProperty("val");
            var expectedException = new RuntimeException("Immediate exception");
            var caughtException = new AtomicReference<Exception>();

            var rule = Rule.on(prop).must(_ -> { throw expectedException; });
            var ruleSet = RuleSet.of(rule)
                .onException(r -> caughtException.set(r.exception()))
                .immediate();

            prop.set("trigger_change");

            assertThat(ruleSet.result()).isAborted();
            Assertions.assertThat(caughtException.get()).isSameAs(expectedException);
        }

        @Test
        @DisplayName("rule set should stop on exception under Cascade.STOP")
        void testExceptionHandlerCascadeStop() {
            var prop1 = new SimpleStringProperty("val1");
            var prop2 = new SimpleStringProperty("val2");
            var callCount = new AtomicInteger(0);
            var caughtException = new AtomicReference<Exception>();

            var rule1 = Rule.on(prop1).must(_ -> { throw new RuntimeException("Rule 1 exception"); });
            var rule2 = Rule.on(prop2).must(_ -> {
                callCount.incrementAndGet();
                return true;
            });
            var ruleSet = RuleSet.of(rule1, rule2)
                .cascade(Cascade.STOP)
                .onException(r -> caughtException.set(r.exception()));

            assertThat(ruleSet.revalidate()).isAborted();
            Assertions.assertThat(callCount.get()).isEqualTo(0);
            Assertions.assertThat(caughtException.get()).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("rule set should stop on exception under Cascade.CONTINUE")
        void testExceptionHandlerCascadeContinue() {
            var prop1 = new SimpleStringProperty("val1");
            var prop2 = new SimpleStringProperty("val2");
            var callCount = new AtomicInteger(0);
            var caughtException = new AtomicReference<Exception>();

            var rule1 = Rule.on(prop1).must(_ -> { throw new RuntimeException("Rule 1 exception"); });
            var rule2 = Rule.on(prop2).must(_ -> {
                callCount.incrementAndGet();
                return true;
            });
            var ruleSet = RuleSet.of(rule1, rule2)
                .cascade(Cascade.CONTINUE)
                .onException(r -> caughtException.set(r.exception()));

            assertThat(ruleSet.revalidate()).isAborted();
            Assertions.assertThat(callCount.get()).isEqualTo(0);
            Assertions.assertThat(caughtException.get()).isNotNull();
        }

        @Test
        @DisplayName("finally handler should trigger on success")
        void testFinallyHandlerOnSuccess() {
            var prop = new SimpleStringProperty("valid");
            var finalized = new AtomicBoolean(false);

            var rule = Rule.on(prop).must("valid"::equals);
            var ruleSet = RuleSet.of(rule).doFinally(_ -> finalized.set(true));

            assertThat(ruleSet.revalidate()).isValid();
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger on validation failure")
        void testFinallyHandlerOnFailure() {
            var prop = new SimpleStringProperty("invalid");
            var finalized = new AtomicBoolean(false);

            var rule = Rule.on(prop).must("valid"::equals);
            var ruleSet = RuleSet.of(rule).doFinally(_ -> finalized.set(true));

            assertThat(ruleSet.revalidate()).isInvalid();
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger when on caught exception")
        void testFinallyHandlerOnException() {
            var prop = new SimpleStringProperty("val");
            var finalized = new AtomicBoolean(false);

            var rule = Rule.on(prop).must(_ -> { throw new RuntimeException("Error"); });
            var ruleSet = RuleSet.of(rule)
                .onException(_ -> { })
                .doFinally(_ -> finalized.set(true));

            ruleSet.revalidate();
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger on unhandled exception")
        void testFinallyHandlerOnUnhandledException() {
            var prop = new SimpleStringProperty("val");
            var finalized = new AtomicBoolean(false);

            var rule = Rule.on(prop).must(_ -> { throw new RuntimeException("Unhandled"); });
            var ruleSet = RuleSet.of(rule).doFinally(_ -> finalized.set(true));

            assertThatThrownBy(ruleSet::revalidate).isInstanceOf(RuntimeException.class);
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger via immediate property change")
        void testFinallyHandlerImmediate() {
            var prop = new SimpleStringProperty("valid");
            var finalized = new AtomicBoolean(false);

            var rule = Rule.on(prop).must("valid"::equals);
            RuleSet.of(rule).doFinally(_ -> finalized.set(true)).immediate();

            prop.set("new_valid");
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger under Cascade.STOP when first rule fails")
        void testFinallyHandlerCascadeStop() {
            var prop1 = new SimpleStringProperty("invalid1");
            var prop2 = new SimpleStringProperty("valid2");
            var finalized = new AtomicBoolean(false);

            var rule1 = Rule.on(prop1).must("valid1"::equals);
            var rule2 = Rule.on(prop2).must("valid2"::equals);
            var ruleSet = RuleSet.of(rule1, rule2)
                .cascade(Cascade.STOP)
                .doFinally(_ -> finalized.set(true));

            assertThat(ruleSet.revalidate()).isInvalid();
            Assertions.assertThat(finalized.get()).isTrue();
        }

        @Test
        @DisplayName("finally handler should trigger under Cascade.CONTINUE after all rules")
        void testFinallyHandlerCascadeContinue() {
            var prop1 = new SimpleStringProperty("invalid1");
            var prop2 = new SimpleStringProperty("invalid2");
            var finalized = new AtomicBoolean(false);

            var rule1 = Rule.on(prop1).must("valid1"::equals);
            var rule2 = Rule.on(prop2).must("valid2"::equals);
            var ruleSet = RuleSet.of(rule1, rule2)
                .cascade(Cascade.CONTINUE)
                .doFinally(_ -> finalized.set(true));

            assertThat(ruleSet.revalidate()).isInvalid();
            Assertions.assertThat(finalized.get()).isTrue();
        }
    }

    @Nested
    class TypeInferenceTest {

        static class Animal { }

        static class Dog extends Animal { }

        @Test
        @DisplayName("compile time type inference should work")
        void testCompileTimeTypeInference() {
            var stringProp1 = new SimpleStringProperty("Test");
            var stringProp2 = new SimpleStringProperty("X");
            var numberProp = new SimpleObjectProperty<>(10);
            var booleanProp = new SimpleBooleanProperty(true);

            // verifies type inference exclusively at compile-time.
            var stringRule = Rule.on(stringProp1)
                .must(s -> s != null && s.length() > 3)
                .must(s -> s != null && s.contains("Test"))
                .failCode(1)
                .failMessage("Should contain Test")
                .childRules(
                    Rule.on(stringProp2).must(s -> s != null && s.startsWith("X"))
                )
                .given(booleanProp, b -> b != null && b);

            var numberRule = Rule.on(numberProp)
                .must(i -> i != null && i > 5)
                .given(i -> i != null && i % 2 == 0);

            assertThat(stringRule.revalidate()).as("String rule should pass validation").isValid();
            assertThat(numberRule.revalidate()).as("Number rule should pass validation").isValid();
        }

        @Test
        @DisplayName("supertype predicate should be applied to subtype property")
        void testSuperTypePredicate() {
            ObjectProperty<Dog> dogProperty = new SimpleObjectProperty<>(new Dog());
            Predicate<Animal> isNotNullAnimal = Objects::nonNull;

            Validator<Dog> dogRule = Rule.on(dogProperty)
                .must(isNotNullAnimal)
                .failMessage("Dog is null");

            assertThat(dogRule.revalidate()).as("Validation should pass for non-null Dog").isValid();
        }

        @Test
        @DisplayName("dependency should accept polymorphic types")
        void testDependencyTypeInference() {
            ObjectProperty<String> textProperty = new SimpleObjectProperty<>("Hello");
            ObjectProperty<Integer> countProperty = new SimpleObjectProperty<>(10);

            Predicate<Number> isPositiveNumber = num -> num != null && num.doubleValue() > 0;

            Validator<String> rule = Rule.on(textProperty)
                .must(Check.Objects.isNotNull())
                .given(countProperty, isPositiveNumber);

            assertThat(rule.revalidate())
                .as("Rule should execute successfully when count is positive")
                .isValid();

            countProperty.set(-5);
            assertThat(rule.revalidate())
                .as("Rule should not trigger a failure when precondition is violated")
                .isValid();
        }
    }

    @Nested
    class SubscriptionTest {

        @Test
        @DisplayName("rule subscription should trigger on change and stop after unsubscribe")
        void testRuleSubscribeUnsubscribe() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop).must("valid"::equals);

            var callCount = new AtomicInteger(0);

            // immediately calls consumer with current value (success)
            Subscription sub = rule.subscribe(_ -> callCount.incrementAndGet());
            assertEquals(1, callCount.get());
            assertThat(rule.result()).isValid();

            rule.revalidate();
            assertEquals(2, callCount.get());
            assertThat(rule.result()).isInvalid();

            prop.set("valid");
            rule.revalidate();
            assertEquals(3, callCount.get());
            assertThat(rule.result()).isValid();

            sub.unsubscribe();

            prop.set("invalid");
            rule.revalidate(); // failure, but listener unsubscribed

            assertThat(rule.result()).isInvalid();
            assertEquals(3, callCount.get(), "No trigger after unsubscribe");
        }

        @Test
        @DisplayName("rule set subscription should trigger on change and stop after unsubscribe")
        void testRuleSetSubscribeUnsubscribe() {
            var prop = new SimpleStringProperty("invalid");
            var rule = Rule.on(prop).must("valid"::equals);
            var ruleSet = RuleSet.of(rule);

            var callCount = new AtomicInteger(0);

            // immediately calls consumer with current value (success)
            Subscription sub = ruleSet.subscribe(_ -> callCount.incrementAndGet());
            assertEquals(1, callCount.get());
            assertThat(ruleSet.result()).isValid();

            ruleSet.revalidate();
            assertEquals(2, callCount.get());
            assertThat(ruleSet.result()).isInvalid();

            prop.set("valid");
            ruleSet.revalidate();
            assertEquals(3, callCount.get());
            assertThat(ruleSet.result()).isValid();

            sub.unsubscribe();

            // failure, but listener is unsubscribed
            prop.set("invalid");
            ruleSet.revalidate();

            assertThat(ruleSet.result()).isInvalid();
            assertEquals(3, callCount.get(), "No trigger after unsubscribe");
        }
    }

    @Nested
    class RuleMemoryLeakTest {

        @Test
        @DisplayName("source property listener should allow rule GC")
        void testRuleGarbageCollected() {
            var longLivedProp = new SimpleStringProperty("initial");
            // rule is created and bound to a long-lived property, but no external strong reference exists
            WeakReference<Rule<String>> ruleRef = createRuleWeakRef(longLivedProp, null);

            awaitGC(ruleRef);
            assertNull(ruleRef.get(), "Rule should be garbage collected despite long-lived property");
        }

        @Test
        @DisplayName("garbage collected rule should ignore property changes")
        void testSourcePropertyListenerStopsTriggeringAfterRuleGC() {
            var longLivedProp = new SimpleStringProperty("initial");
            WeakReference<Rule<String>> ruleRef = createRuleWeakRef(longLivedProp, null);

            awaitGC(ruleRef);
            assertNull(ruleRef.get(), "Rule must be garbage collected");

            // changing the long-lived property after Rule GC should not throw exceptions
            assertDoesNotThrow(
                () -> longLivedProp.set("updated"),
                "Changing long-lived property should ignore garbage collected listener"
            );
        }

        @Test
        @DisplayName("source property listeners should allow rule set GC")
        void testRuleSetGarbageCollected() {
            var longLivedProp1 = new SimpleStringProperty("field1");
            var longLivedProp2 = new SimpleStringProperty("field2");
            WeakReference<RuleSet> ruleSetRef = createRuleSetWeakRef(longLivedProp1, longLivedProp2);

            awaitGC(ruleSetRef);
            assertNull(ruleSetRef.get(), "RuleSet should be garbage collected despite long-lived properties");
        }

        @Test
        @DisplayName("rule with subscription should allow GC")
        void testRuleWithSubscriptionGarbageCollected() {
            var longLivedProp = new SimpleStringProperty("initial");
            WeakReference<Rule<String>> ruleRef = createRuleWeakRef(longLivedProp, () -> { });

            awaitGC(ruleRef);
            assertNull(ruleRef.get(), "Rule with subscription should be garbage collected");
        }

        private WeakReference<Rule<String>> createRuleWeakRef(ObservableValue<String> sourceProperty,
                                                              @Nullable Runnable r) {
            var rule = Rule.on(sourceProperty).must("valid"::equals).immediate();
            // anonymous subscriber that does not retain a strong reference to the rule itself
            if (r != null) {
                rule.subscribe(_ -> { });
            }

            return new WeakReference<>((Rule<String>) rule);
        }

        private WeakReference<RuleSet> createRuleSetWeakRef(ObservableValue<String> source1,
                                                            ObservableValue<String> source2) {
            return new WeakReference<>(RuleSet.of(
                Rule.on(source1).must("val1"::equals).immediate(),
                Rule.on(source2).must("val2"::equals).immediate()
            ));
        }
    }

    @Nested
    class DependencyMemoryLeakTest {

        @Test
        @DisplayName("dependency listener should allow rule GC")
        void testDependencyListenerGarbageCollected() {
            var longLivedDependency = new SimpleBooleanProperty(false);
            WeakReference<Rule<String>> ruleRef = createRuleWeakRef(longLivedDependency);

            awaitGC(ruleRef);
            assertNull(ruleRef.get(), "Rule should be garbage collected despite dependency registration");
        }

        @Test
        @DisplayName("garbage collected rule should not trigger dependency changes")
        void testDependencyListenerStopTriggeringAfterRuleGC() {
            var longLivedDependency = new SimpleBooleanProperty(false);
            WeakReference<Rule<String>> ruleRef = createRuleWeakRef(longLivedDependency);

            awaitGC(ruleRef);
            assertNull(ruleRef.get(), "Rule must be garbage collected"); // precondition

            assertDoesNotThrow(
                () -> longLivedDependency.set(true),
                "Changing long-lived dependency should ignore garbage collected listener"
            );
        }

        private WeakReference<Rule<String>> createRuleWeakRef(ObservableValue<Boolean> dependency) {
            var prop = new SimpleStringProperty("test");
            var rule = Rule.on(prop)
                .must("valid"::equals)
                .given(dependency, Boolean.TRUE::equals);

            // creates a rule within an isolated method scope to prevent strong references
            // from lingering on the test method stack frame
            return new WeakReference<>((Rule<String>) rule);
        }
    }

    @Nested
    class DisposeTest {

        @Test
        @DisplayName("dispose should unregister source property listener")
        void testDisposeUnregistersSourcePropertyListener() {
            var prop = new SimpleStringProperty("valid");
            var rule = Rule.on(prop).must("valid"::equals).immediate();

            var callCount = new AtomicInteger(0);
            rule.subscribe(_ -> callCount.incrementAndGet());
            assertEquals(1, callCount.get());

            prop.set("invalid");
            assertEquals(2, callCount.get());

            rule.asRule().dispose();

            prop.set("valid");
            assertEquals(2, callCount.get(), "Source property changes should be ignored after dispose");
        }

        @Test
        @DisplayName("dispose should unregister dependency listeners")
        void testDisposeUnregistersDependencyListeners() {
            var prop = new SimpleStringProperty("test");
            var dep = new SimpleStringProperty("dep");
            var rule = Rule.on(prop)
                .must("test"::equals)
                .given(dep, "dep"::equals)
                .asRule();

            var callCount = new AtomicInteger(0);
            rule.subscribe(_ -> callCount.incrementAndGet());
            assertEquals(1, callCount.get());

            rule.dispose();

            dep.set("changed");
            assertEquals(1, callCount.get(), "Dependency property changes should be ignored after dispose");
            assertNull(rule.dependencies, "Dependencies list should be cleared after dispose");
        }

        @Test
        @DisplayName("dispose should clear internal states and callbacks")
        void testDisposeClearsStateAndCallbacks() {
            var prop = new SimpleStringProperty("invalid");
            var successCount = new AtomicInteger(0);
            var failureCount = new AtomicInteger(0);

            var rule = Rule.on(prop)
                .must("valid"::equals)
                .onSuccess(_ -> successCount.incrementAndGet())
                .onFailure(_ -> failureCount.incrementAndGet())
                .asRule();

            rule.revalidate();
            assertEquals(1, failureCount.get());

            rule.dispose();

            assertTrue(rule.checks.isEmpty());
            assertNull(rule.preconditions);
            assertNull(rule.childRules);
            assertNull(rule.callbacks.success);
            assertNull(rule.callbacks.failure);
            assertNull(rule.callbacks.exception);
            assertNull(rule.callbacks.finale);
        }

        @Test
        @DisplayName("rule set dispose should unsubscribe from all rules and dispose them")
        void testRuleSetDisposeUnsubscribesChildren() {
            var prop1 = new SimpleStringProperty("val1");
            var prop2 = new SimpleStringProperty("val2");
            var rule1 = Rule.on(prop1).must("val1"::equals);
            var rule2 = Rule.on(prop2).must("val2"::equals);
            var ruleSet = RuleSet.of(rule1, rule2);

            var callCount = new AtomicInteger(0);
            ruleSet.subscribe(_ -> callCount.incrementAndGet());
            assertEquals(1, callCount.get()); // eager subscribe

            ruleSet.revalidate();
            assertEquals(2, callCount.get());
            prop1.set("invalid");

            ruleSet.revalidate();
            assertEquals(3, callCount.get());

            ruleSet.dispose();

            prop1.set("val1");
            prop2.set("invalid"); // nothing changed
            assertEquals(3, callCount.get(), "RuleSet should not react to child rule updates after dispose");
        }

        @Test
        @DisplayName("dispose should clear rule set callbacks and state")
        void testRuleSetDisposeClearsCallbacks() {
            var prop = new SimpleStringProperty("val");
            var rule = Rule.on(prop).must("val"::equals);

            var successCount = new AtomicInteger(0);
            var ruleSet = RuleSet.of(rule).onSuccess(_ -> successCount.incrementAndGet());

            ruleSet.revalidate();
            assertEquals(1, successCount.get());

            ruleSet.dispose();

            assertTrue(ruleSet.rules.isEmpty());
            assertNull(ruleSet.callbacks.success);
            assertNull(ruleSet.callbacks.failure);
            assertNull(ruleSet.callbacks.exception);
            assertNull(ruleSet.callbacks.finale);
        }
    }

    @Nested
    class MessageTest {

        @Test
        @DisplayName("MessageFormat should evaluate default args")
        void testMessageFormatDefaultArgs() {
            var format = "val={0}, name={1}";
            var rule1 = Rule.on(new SimpleStringProperty(), "foo")
                .must(_ -> false)
                .failMessageFormat(format);
            var rule2 = Rule.on(new SimpleStringProperty("test"), "rule2")
                .must(_ -> false)
                .failMessageFormat(format);

            assertThat(rule1.revalidate())
                .isSingleFailure()
                .hasFailMessage("val=null, name=foo");
            assertThat(rule2.revalidate())
                .isSingleFailure()
                .hasFailMessage("val=test, name=rule2");
        }

        @Test
        @DisplayName("MessageFormat should evaluate args at validation time")
        void testMessageFormatArgsEvaluationTime() {
            var prop = new SimpleStringProperty("test");
            var external = new SimpleStringProperty("initial");
            var rule = Rule.on(prop)
                .must(_ -> false)
                .failMessageFormat("{2} {3}", Args.of(10, Args.cast(external::get)));

            external.set("updated");
            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailMessage("10 updated");
        }

        @Test
        @DisplayName("failMessageKey should resolve plain bundle message")
        void testFailMessageKeyDirectAndSupplier() {
            var bundle = new test_messages();

            var ruleDirect = Rule.on(new SimpleStringProperty("value"))
                .must(_ -> false)
                .failMessageKey(bundle, "err.plain");

            var ruleSupplier = Rule.on(new SimpleStringProperty("value"))
                .must(_ -> false)
                .failMessageKey(() -> bundle, "err.plain");

            assertThat(ruleDirect.revalidate())
                .isSingleFailure()
                .hasFailMessage("Static error message");

            assertThat(ruleSupplier.revalidate())
                .isSingleFailure()
                .hasFailMessage("Static error message");
        }

        @Test
        @DisplayName("failMessageKey should resolve bundle message using locale supplier")
        void testFailMessageKeyLocaleSupplier() {
            var currentLocale = new AtomicReference<>(Locale.ENGLISH);

            var rule = Rule.on(new SimpleStringProperty("test"))
                .must(_ -> false)
                .failMessageKey("atlantafx.validation.l10n.app_messages", "err.invalid", currentLocale::get);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailMessage("Invalid value");

            currentLocale.set(Locale.FRENCH);

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailMessage("Valeur invalide");
        }

        @Test
        @DisplayName("failMessageFormatKey should format bundle message with args")
        void testFailMessageFormatKeyWithArgs() {
            var bundle = new format_messages();

            var minProp = new SimpleStringProperty("10");
            var maxProp = new SimpleStringProperty("100");

            var rule = Rule.on(new SimpleStringProperty("5"), "age")
                .must(_ -> false)
                .failMessageFormatKey(() -> bundle, "err.range", () -> new Object[] {minProp.get(), maxProp.get()});

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailMessage("Field age with value 5 must be between 10 and 100");

            minProp.set("20");
            maxProp.set("200");

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailMessage("Field age with value 5 must be between 20 and 200");
        }

        @Test
        @DisplayName("failMessageFormatKey should support explicit locale and bundle name")
        void testFailMessageFormatKeyExplicitLocale() {
            var rule = Rule.on(new SimpleStringProperty("3"), "limit")
                .must(_ -> false)
                .failMessageFormatKey(
                    "atlantafx.validation.l10n.validation_messages",
                    "err.min",
                    Locale.GERMAN, () -> new Object[] {18}
                );

            assertThat(rule.revalidate())
                .isSingleFailure()
                .hasFailMessage("Feld limit (3) muss mindestens 18 sein");
        }
    }

    @Nested
    class ActionTest {

        @Test
        @DisplayName("combine() should return EMPTY when no actions provided")
        void testCombineReturnsEmpty() {
            var result = Action.combine();
            assertSame(Action.EMPTY, result);
        }

        @Test
        @DisplayName("combine() should return single action when only one provided")
        void testCombineReturnsSingleAction() {
            var action = new TestAction();
            var result = Action.combine(action);
            assertSame(action, result);
        }

        @Test
        @DisplayName("combine() should combine multiple actions into a composite")
        void testCombinesMultipleIntoComposite() {
            var action1 = new TestAction();
            var action2 = new TestAction();
            var action3 = new TestAction();
            var result = Action.combine(action1, action2, action3);

            assertNotNull(result);
            assertNotSame(action1, result);
            assertNotSame(action2, result);
            assertNotSame(action3, result);

            Failure failure = Failure.EMPTY;
            result.apply(failure);

            assertTrue(action1.isApplied());
            assertTrue(action2.isApplied());
            assertTrue(action3.isApplied());
        }

        @Test
        @DisplayName("combine() should filter out EMPTY actions")
        void testCombineFiltersOutEmpty() {
            var action1 = new TestAction();
            var action2 = new TestAction();
            var result = Action.combine(Action.EMPTY, action1, Action.EMPTY, action2, Action.EMPTY);

            Failure failure = Failure.EMPTY;
            result.apply(failure);

            assertTrue(action1.isApplied());
            assertTrue(action2.isApplied());
        }

        @Test
        @DisplayName("combine() should return EMPTY when all actions are EMPTY")
        void testCombineReturnsEmptyWhenAllEmpty() {
            var result = Action.combine(Action.EMPTY, Action.EMPTY, Action.EMPTY);
            assertSame(Action.EMPTY, result);
        }

        @Test
        @DisplayName("combine() should flatten nested composite actions")
        void testCombineFlattensNestedCompositeActions() {
            var leaf1 = new TestAction();
            var leaf2 = new TestAction();
            var leaf3 = new TestAction();
            var leaf4 = new TestAction();

            var composite1 = Action.combine(leaf1, leaf2);
            var composite2 = Action.combine(leaf3, leaf4);
            var nestedComposite = Action.combine(composite1, composite2);

            var leaf5 = new TestAction();
            var result = Action.combine(nestedComposite, leaf5);

            Failure failure = Failure.EMPTY;
            result.apply(failure);

            // all leaf actions should be applied exactly once
            assertTrue(leaf1.isApplied());
            assertTrue(leaf2.isApplied());
            assertTrue(leaf3.isApplied());
            assertTrue(leaf4.isApplied());
            assertTrue(leaf5.isApplied());

            // verify apply count is exactly 1 for each
            assertEquals(1, leaf1.getApplyCount());
            assertEquals(1, leaf2.getApplyCount());
            assertEquals(1, leaf3.getApplyCount());
            assertEquals(1, leaf4.getApplyCount());
            assertEquals(1, leaf5.getApplyCount());
        }

        @Test
        @DisplayName("combine() should maintain order of actions")
        void testCombineMaintainsOrder() {
            List<Integer> executionOrder = new ArrayList<>();

            var action1 = new OrderedAction(1, executionOrder);
            var action2 = new OrderedAction(2, executionOrder);
            var action3 = new OrderedAction(3, executionOrder);

            var result = Action.combine(action1, action2, action3);

            Failure failure = Failure.EMPTY;
            result.apply(failure);

            assertEquals(List.of(1, 2, 3), executionOrder);
        }

        @Test
        @DisplayName("combine() should apply clear() to all actions")
        void testCombineAppliesClearToAll() {
            var action1 = new TestAction();
            var action2 = new TestAction();
            var action3 = new TestAction();

            var result = Action.combine(action1, action2, action3);

            Descriptor descriptor = Descriptor.EMPTY;
            result.clear(descriptor);

            assertTrue(action1.isCleared());
            assertTrue(action2.isCleared());
            assertTrue(action3.isCleared());
        }

        @Test
        @DisplayName("combine() should flatten nested composites and filter EMPTY")
        void testCombineFlattensNestedCompositesAndFiltersEmpty() {
            var leaf1 = new TestAction();
            var leaf2 = new TestAction();

            var compositeWithEmpty = Action.combine(leaf1, Action.EMPTY);
            var result = Action.combine(compositeWithEmpty, Action.EMPTY, leaf2);

            Failure failure = Failure.EMPTY;
            result.apply(failure);

            assertTrue(leaf1.isApplied());
            assertTrue(leaf2.isApplied());
        }

        @Test
        @DisplayName("combine() should handle deeply nested composites")
        void testCombineHandlesDeeplyNestedComposites() {
            var leaf = new TestAction();

            // create deeply nested structure
            var nested1 = Action.combine(leaf);
            var nested2 = Action.combine(nested1);
            var nested3 = Action.combine(nested2);
            var nested4 = Action.combine(nested3);

            var result = Action.combine(nested4);

            // should be flattened to just the leaf action
            Failure failure = Failure.EMPTY;
            result.apply(failure);

            assertTrue(leaf.isApplied());
            assertEquals(1, leaf.getApplyCount());
        }

        @Test
        @DisplayName("combine() should return single action if after only one remains")
        void testCombineReturnsSingleActionIfOnlyOneRemains() {
            var action1 = new TestAction();
            var result = Action.combine(Action.EMPTY, action1, Action.EMPTY);

            // should return the single action, not a composite
            assertSame(action1, result);
        }

        @NullMarked
        static class TestAction implements Action {
            private boolean applied = false;
            private boolean cleared = false;
            private int applyCount = 0;

            @Override
            public void apply(Failure failure) {
                applied = true;
                applyCount++;
            }

            @Override
            public void clear(Descriptor descriptor) {
                cleared = true;
            }

            boolean isApplied() { return applied; }

            boolean isCleared() { return cleared; }

            int getApplyCount() { return applyCount; }
        }

        @NullMarked
        record OrderedAction(int order, List<Integer> executionOrder) implements Action {

            @Override
            public void apply(Failure failure) {
                executionOrder.add(order);
            }

            @Override
            public void clear(Descriptor descriptor) {
            }
        }
    }

    @Nested
    class CompositionTest {

        @Test
        @DisplayName("manual revalidation should work across 3-level rule hierarchy")
        void testManualComposition() {
            var prop1 = new SimpleStringProperty("valid1");
            var dep1 = new SimpleBooleanProperty(true);

            var prop2 = new SimpleStringProperty("valid2");
            var dep2 = new SimpleBooleanProperty(true);

            var prop3 = new SimpleStringProperty("valid3");
            var dep3 = new SimpleBooleanProperty(true);

            // level 3 (leaf)
            var rule3 = Rule.on(prop3, "Rule3")
                .must("valid3"::equals)
                .given(dep3, Boolean.TRUE::equals);
            var ruleSet3 = RuleSet.of("RuleSet3", rule3);

            // level 2 (middle node triggers level 3 via childRules)
            var rule2 = Rule.on(prop2, "Rule2")
                .must("valid2"::equals)
                .given(dep2, Boolean.TRUE::equals)
                .childRules(rule3);
            var ruleSet2 = RuleSet.of("RuleSet2", rule2);

            // level 1 (root node triggers level 2 via childRules)
            var rule1 = Rule.on(prop1, "Rule1")
                .must("valid1"::equals)
                .given(dep1, Boolean.TRUE::equals)
                .childRules(rule2);
            var ruleSet1 = RuleSet.of("RuleSet1", rule1);

            // verify all RuleSets start in unvalidated Initial state
            assertThat(ruleSet1.result()).isInitial();
            assertThat(ruleSet2.result()).isInitial();
            assertThat(ruleSet3.result()).isInitial();

            // revalidating from root should cascade down: rule1 -> rule2 -> rule3
            ruleSet1.revalidate();

            assertThat(rule1.result()).isValid();
            assertThat(rule2.result()).isValid();
            assertThat(rule3.result()).isValid();

            // sync independent RuleSets with their newly calculated rules
            ruleSet2.revalidate();
            ruleSet3.revalidate();

            assertThat(ruleSet1.result()).isValid();
            assertThat(ruleSet2.result()).isValid();
            assertThat(ruleSet3.result()).isValid();

            // mutating Level 3 property should mark rule3 invalid when revalidated from root
            prop3.set("invalid3");
            ruleSet1.revalidate();

            assertThat(rule1.result()).as("L1 rule should remain valid").isValid();
            assertThat(rule2.result()).as("L2 rule should remain valid").isValid();
            assertThat(rule3.result()).as("L3 rule should become invalid via cascade").isInvalid();

            ruleSet3.revalidate();
            assertThat(ruleSet1.result()).as("RuleSet1 remains valid as rule1 passed").isValid();
            assertThat(ruleSet2.result()).as("RuleSet2 remains valid as rule2 passed").isValid();
            assertThat(ruleSet3.result()).as("RuleSet3 must reflect L3 failure").isInvalid();

            // restoring Level 3 property should propagate validity back down the chain
            prop3.set("valid3");
            ruleSet1.revalidate();

            assertThat(rule1.result()).as("L1 rule should be valid").isValid();
            assertThat(rule2.result()).as("L2 rule should be valid").isValid();
            assertThat(rule3.result()).as("L3 rule should recover via cascade").isValid();

            ruleSet3.revalidate();
            assertThat(ruleSet3.result()).as("RuleSet3 should recover to valid").isValid();

            // disabling root precondition should bypass rule1 check
            dep1.set(false);
            ruleSet1.revalidate();

            assertThat(rule1.result()).as("L1 rule should skip checks when dep1=false").isValid();

            // disabling leaf precondition should bypass rule3 check even with invalid property
            dep1.set(true);
            prop3.set("invalid3");
            dep3.set(false);

            ruleSet1.revalidate();

            assertThat(rule3.result()).as("L3 rule should pass because dep3 precondition is false").isValid();

            ruleSet3.revalidate();
            assertThat(ruleSet3.result()).as("RuleSet3 should be valid").isValid();
        }

        @Test
        @DisplayName("immediate revalidation should work across 3-level rule hierarchy")
        void testImmediateComposition() {
            var prop1 = new SimpleStringProperty("valid1");
            var dep1 = new SimpleBooleanProperty(true);

            var prop2 = new SimpleStringProperty("valid2");
            var dep2 = new SimpleBooleanProperty(true);

            var prop3 = new SimpleStringProperty("valid3");
            var dep3 = new SimpleBooleanProperty(true);

            // level 3 (leaf)
            var rule3 = Rule.on(prop3, "Rule3")
                .must("valid3"::equals)
                .given(dep3, Boolean.TRUE::equals);
            var ruleSet3 = RuleSet.of("RuleSet3", rule3);

            // level 2 (middle)
            var rule2 = Rule.on(prop2, "Rule2")
                .must("valid2"::equals)
                .given(dep2, Boolean.TRUE::equals)
                .childRules(rule3);
            var ruleSet2 = RuleSet.of("RuleSet2", rule2);

            // level 1 (root)
            var rule1 = Rule.on(prop1, "Rule1")
                .must("valid1"::equals)
                .given(dep1, Boolean.TRUE::equals)
                .childRules(rule2);
            var ruleSet1 = RuleSet.of("RuleSet1", rule1);

            // enable reactive mode on all levels
            ruleSet3.immediate();
            ruleSet2.immediate();
            ruleSet1.immediate();

            assertThat(ruleSet1.result()).isValid();
            assertThat(ruleSet2.result()).isValid();
            assertThat(ruleSet3.result()).isValid();

            // changing property at L3 must instantly invalidate rule3 and RuleSet3
            prop3.set("invalid3");

            assertThat(rule3.result()).as("L3 rule should reactively become invalid").isInvalid();
            assertThat(ruleSet3.result()).as("RuleSet3 should reactively become invalid").isInvalid();
            assertThat(ruleSet1.result()).as("RuleSet1 depends only on rule1, remains valid").isValid();

            // disabling dependency at L3 must instantly clear error at L3
            dep3.set(false);

            assertThat(rule3.result()).as("L3 rule should skip checks when dep3=false").isValid();
            assertThat(ruleSet3.result()).as("RuleSet3 should reactively become valid").isValid();

            // reactive dependency change at root level
            dep1.set(false);
            assertThat(rule1.result()).as("L1 rule should react to dep1 change").isValid();
            assertThat(ruleSet1.result()).as("RuleSet1 should remain valid").isValid();

            // mutate l2 property -> fails, toggle l2 dependency -> recovers
            prop2.set("invalid2");
            assertThat(rule2.result()).as("L2 rule should become invalid").isInvalid();
            assertThat(ruleSet2.result()).as("RuleSet2 should become invalid").isInvalid();

            dep2.set(false);
            assertThat(rule2.result()).as("L2 rule should become valid when dep2=false").isValid();
            assertThat(ruleSet2.result()).as("RuleSet2 should recover to valid").isValid();
        }
    }

    //*************************************************************************

    private static void awaitGC(WeakReference<?> ref) {
        for (int i = 0; i < 10; i++) {
            if (ref.get() == null) {
                return;
            }

            System.gc();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}