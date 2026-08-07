/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import org.jspecify.annotations.Nullable;

import java.lang.System.Logger.Level;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Defines validation logic for a specific observable property.
 *
 * @param <T> the type of the value to be validated
 */
public final class Rule<T extends @Nullable Object>
    implements RuleCheckBuilder<T>, RuleOptionsBuilder<T>, Validator<T> {

    private static final StackWalker WALKER = StackWalker.getInstance();
    private static final System.Logger LOG = System.getLogger(Rule.class.getName());
    private static @Nullable Boolean SUPPRESS_INIT_CHECK = null;

    private static final int DEFAULT_CAPACITY = 2;

    private final String name;
    private final ObservableValue<@Nullable T> sourceProperty;
    private final BooleanProperty immediate = new SimpleBooleanProperty(false);
    private final ChangeListener<T> sourceListener = (_, _, _) -> {
        if (immediate.get()) {
            revalidate();
        }
    };
    private final ChangeListener<T> sourceWeakListener = new WeakChangeListener<>(sourceListener);

    final List<Check<T>> checks = new ArrayList<>(DEFAULT_CAPACITY);
    private Cascade cascade = Cascade.CONTINUE;
    @Nullable List<Validator<?>> childRules = null;
    @Nullable List<Condition<? super @Nullable T>> preconditions = null;
    @Nullable List<Dependency<?>> dependencies = null;
    private final ChangeListener<? super Object> dependencyListener = (_, _, _) -> revalidate();
    private final ChangeListener<? super Object> dependencyWeakListener = new WeakChangeListener<>(dependencyListener);
    final Callbacks<Failure.Single<T>> callbacks = new Callbacks<>();

    private @Nullable Map<String, @Nullable Object> attributes = null;
    private final ReadOnlyObjectWrapper<Result> resultProperty;

    /**
     * Constructs a new validation rule for the given observable property.
     *
     * <p>If an explicit name was not provided during creation, the rule falls back
     * to the name of the validated property. If no property name is available,
     * an automatically generated name is used.
     *
     * @param obs     the observable value to be validated
     * @param name    an optional name for the field being validated
     * @param initial the initial validation state, default is {@code valid}
     */
    private Rule(ObservableValue<T> obs, @Nullable String name, boolean initial) {
        this.sourceProperty = obs;

        if ((name == null || name.isEmpty()) && obs instanceof ReadOnlyProperty<?> p) {
            name = p.getName();
        }
        if (name == null || name.isEmpty()) {
            name = "Rule@" + Integer.toHexString(System.identityHashCode(obs));
        }
        this.name = name;

        this.resultProperty = new ReadOnlyObjectWrapper<>(
            new Result.Initial(Descriptor.of(name, attributes), initial)
        );

        this.sourceProperty.addListener(sourceWeakListener);
    }

    /**
     * Creates a new {@link Rule} for the given {@link ObservableValue}.
     *
     * @param obs the observable value to validate
     * @param <T> the type of the value
     */
    public static <T> Rule<T> on(ObservableValue<@Nullable T> obs) {
        return new Rule<>(obs, null, true);
    }

    /**
     * Creates a new {@link Rule} for the given {@link ObservableValue} and field name.
     *
     * @param obs  the observable value to validate
     * @param name the optional name of the field being validated
     * @param <T>  the type of the value
     */
    public static <T> Rule<T> on(ObservableValue<@Nullable T> obs, @Nullable String name) {
        return new Rule<>(obs, name, true);
    }

    /**
     * Creates a new {@link Rule} for the given {@link ObservableValue}, field name and initial result.
     *
     * @param obs     the observable value to validate
     * @param name    the optional name of the field being validated
     * @param initial the initial validation state, default is {@code valid}
     * @param <T>     the type of the value
     */
    public static <T> Rule<T> on(ObservableValue<@Nullable T> obs, @Nullable String name, boolean initial) {
        return new Rule<>(obs, name, initial);
    }

    /**
     * Returns the name of the field or property being validated.
     *
     * <p>If an explicit name was not provided during creation, the rule falls back
     * to the name of the validated property. If no property name is available,
     * an automatically generated name is used.
     */
    public String name() {
        return name;
    }

    /**
     * Returns the source property this rule validated against.
     */
    public ObservableValue<@Nullable T> sourceProperty() {
        return sourceProperty;
    }

    /**
     * Returns the cascade behavior for checks within this rule.
     */
    public Cascade cascade() {
        return cascade;
    }

    //region CHECK
    //*************************************************************************

    @Override
    public RuleCheckBuilder<T> must(Predicate<? super @Nullable T> predicate) {
        initCheck("must");
        checks.add(new Check<>(predicate, Integer.MIN_VALUE, null, null));
        return this;
    }

    @Override
    public RuleCheckBuilder<T> failCode(int code) {
        if (checks.isEmpty()) {
            throw new IllegalStateException("You have to specify 'must' condition before setting optional params.");
        }

        initCheck("failCode");

        int lastIdx = checks.size() - 1;
        Check<T> old = checks.get(lastIdx);
        checks.set(lastIdx, new Check<>(old.must(), code, old.message(), old.condition()));

        return this;
    }

    @Override
    public RuleCheckBuilder<T> failMessage(@Nullable MessageProvider<T> provider,
                                           @Nullable Supplier<Object[]> args) {

        // The 'args' param is not used in the implementation. It just allows passing optional
        // MessageFormat args to the violation message at validation time

        if (checks.isEmpty()) {
            throw new IllegalStateException("You have to specify 'must' condition before setting optional params.");
        }

        initCheck("failMessage");

        int lastIdx = checks.size() - 1;
        Check<T> old = checks.get(lastIdx);
        checks.set(lastIdx, new Check<>(old.must(), old.code(), provider, old.condition()));

        return this;
    }

    @Override
    public RuleCheckBuilder<T> when(Predicate<? super @Nullable T> condition) {
        if (checks.isEmpty()) {
            throw new IllegalStateException("You have to specify 'must' condition before setting optional params.");
        }

        initCheck("when");

        int lastIdx = checks.size() - 1;
        Check<T> old = checks.get(lastIdx);
        checks.set(lastIdx, new Check<>(old.must(), old.code(), old.message(), condition));

        return this;
    }
    //endregion

    //region OPTIONS
    //*************************************************************************

    @Override
    public RuleOptionsBuilder<T> immediate() {
        initCheck("immediate");
        immediate.set(true);
        return this;
    }

    @Override
    public RuleOptionsBuilder<T> cascade(@Nullable Cascade cascade) {
        initCheck("cascade");
        this.cascade = Objects.requireNonNullElse(cascade, Cascade.CONTINUE);
        return this;
    }

    @Override
    public RuleOptionsBuilder<T> childRules(Validator<?>... rules) {
        for (var rule : rules) {
            if (Objects.equals(sourceProperty, keyOf(rule))) {
                throw new IllegalArgumentException(
                    "Child rule cannot target parent's source property or be the parent itself."
                );
            }
        }

        if (hasDuplicates(rules)) {
            throw new IllegalArgumentException("Duplicate child rules are not allowed.");
        }

        initCheck("childRules");

        this.childRules = Arrays.asList(rules);
        return this;
    }

    @Override
    public RuleOptionsBuilder<T> given(Condition<T> condition) {
        if (preconditions == null) {
            preconditions = new ArrayList<>(DEFAULT_CAPACITY);
        } else if (preconditions.contains(condition)) {
            throw new IllegalArgumentException("Duplicate precondition is not allowed.");
        }

        initCheck("given");

        preconditions.add(condition);
        return this;
    }

    @Override
    public <D> RuleOptionsBuilder<T> given(ObservableValue<@Nullable D> obs,
                                           Predicate<? super @Nullable D> condition) {
        if (obs == sourceProperty) {
            throw new IllegalArgumentException("A rule cannot depend on its own source property.");
        }

        if (dependencies == null) {
            dependencies = new ArrayList<>(DEFAULT_CAPACITY);
        } else {
            for (var dep : dependencies) {
                if (dep.property() == obs) {
                    throw new IllegalArgumentException("Duplicate dependency on the same property is not allowed.");
                }
            }
        }

        initCheck("given");

        dependencies.add(new Dependency<>(obs, condition));
        obs.addListener(dependencyWeakListener);

        return this;
    }

    @Override
    public RuleOptionsBuilder<T> attribute(String key, @Nullable Object value) {
        if (attributes == null) {
            attributes = new TreeMap<>();
        }

        initCheck("attribute");
        attributes.put(key, value);

        return this;
    }
    //endregion

    //region CALLBACKS
    //*************************************************************************

    @Override
    public RuleOptionsBuilder<T> onSuccess(@Nullable Consumer<Result.Success> handler) {
        initCheck("onSuccess");
        callbacks.success = handler;
        return this;
    }

    @Override
    public RuleOptionsBuilder<T> onFailure(@Nullable Consumer<Failure.Single<T>> handler) {
        initCheck("onFailure");
        callbacks.failure = handler;
        return this;
    }

    @Override
    public RuleOptionsBuilder<T> onException(Consumer<Result.Abort> handler) {
        initCheck("onException");
        callbacks.exception = handler;
        return this;
    }

    @Override
    public RuleOptionsBuilder<T> doFinally(Consumer<Descriptor> handler) {
        initCheck("doFinally");
        callbacks.finale = handler;
        return this;
    }
    //endregion

    //region VALIDATOR
    //*************************************************************************

    @Override
    public ReadOnlyObjectProperty<Result> resultProperty() {
        return resultProperty.getReadOnlyProperty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result revalidate() {
        try {
            // test preconditions and dependencies before processing rules
            if (!testPreconditions() || !testDependencies()) {
                var successResult = new Result.Success(Descriptor.of(name, attributes));
                resultProperty.set(successResult);
                if (callbacks.success != null) {
                    callbacks.success.accept(successResult);
                }
                return successResult;
            }

            boolean valid = true;
            List<Violation> violations = null;

            var descriptor = Descriptor.of(name, attributes);
            for (var check : checks) {
                boolean test = check.test(attemptedValue());
                if (!test) {
                    valid = false;
                    if (violations == null) {
                        violations = new ArrayList<>();
                    }
                    var message = check.message() != null
                        ? check.message().apply(attemptedValue(), descriptor)
                        : null;
                    violations.add(new Violation(check.code(), message));

                    if (cascade == Cascade.STOP) {
                        break;
                    }
                }
            }

            Result result = valid
                ? new Result.Success(Descriptor.of(name, attributes))
                : new Failure.Single<>(Descriptor.of(name, attributes), attemptedValue(), violations);

            resultProperty.set(result);

            if (result.valid() && callbacks.success != null && result instanceof Result.Success s) {
                callbacks.success.accept(s);
            }
            if (result.invalid() && callbacks.failure != null && result instanceof Failure.Single<?> f) {
                callbacks.failure.accept((Failure.Single<T>) f);
            }

            // revalidate child rules
            if (childRules != null) {
                for (var rule : childRules) {
                    rule.revalidate();
                }
            }

            return result;
        } catch (Exception e) {
            var abortResult = new Result.Abort(Descriptor.of(name, attributes), e, name);
            resultProperty.set(abortResult);

            if (callbacks.exception != null) {
                callbacks.exception.accept(abortResult);
                return abortResult;
            } else {
                throw e;
            }
        } finally {
            if (callbacks.finale != null) {
                callbacks.finale.accept(Descriptor.of(name, attributes));
            }
        }
    }
    //endregion

    //region AUXILIARY
    //*************************************************************************

    /**
     * Disposes this rule and unregisters all property listeners.
     *
     * <p>This method is optional and only needed if the lifetime of this rule
     * is shorter than the lifetime of the validated property.
     */
    public void dispose() {
        sourceProperty.removeListener(sourceWeakListener);

        if (dependencies != null) {
            for (var dependency : dependencies) {
                dependency.property().removeListener(dependencyWeakListener);
            }
            dependencies.clear();
            dependencies = null;
        }

        checks.clear();
        preconditions = null;
        childRules = null;
        attributes = null;
        callbacks.dispose();
    }

    // A wrapper that keeps all callbacks under one roof.
    static class Callbacks<F extends Failure> {

        public @Nullable Consumer<Result.Success> success;
        public @Nullable Consumer<? super F> failure;
        public @Nullable Consumer<Result.Abort> exception;
        public @Nullable Consumer<Descriptor> finale;

        public void dispose() {
            success = null;
            failure = null;
            exception = null;
            finale = null;
        }
    }

    // Checks whether the given set of rules contains duplicates.
    static boolean hasDuplicates(Validator<?>[] rules) {
        if (rules.length < 2) {
            return false;
        }

        // O(N^2) no allocations (small arrays)
        if (rules.length < 10) {
            for (int i = 0; i < rules.length; i++) {
                Object o1 = keyOf(rules[i]);
                for (int j = i + 1; j < rules.length; j++) {
                    Object o2 = keyOf(rules[j]);
                    if (Objects.equals(o1, o2)) {
                        return true;
                    }
                }
            }
            return false;
        }

        // O(N)
        var seen = new HashSet<>(Math.min((int) (rules.length / 0.75f) + 1, rules.length));
        for (Validator<?> r : rules) {
            Object o = keyOf(r);
            if (!seen.add(o)) {
                return true;
            }
        }

        return false;
    }

    // Returns the validators' unique key for equality check.
    static Object keyOf(Validator<?> validator) {
        return (validator instanceof Rule<?> r) ? r.sourceProperty : validator;
    }

    // Returns method call chain.
    static List<String> getCallChain(int max, int skip) {
        return WALKER.walk(frames ->
            frames.skip(skip)
                .limit(max)
                .map(frame -> frame.getClassName() + "." + frame.getMethodName() + ":" + frame.getLineNumber())
                .collect(Collectors.toList())
        );
    }

    private boolean testPreconditions() {
        if (preconditions == null) {
            return true;
        }

        for (var predicate : preconditions) {
            var val = predicate.test(attemptedValue());
            if (!val) {
                return false;
            }
        }
        return true;
    }

    private boolean testDependencies() {
        if (dependencies == null) {
            return true;
        }

        for (var dependency : dependencies) {
            var val = dependency.test();
            if (!val) {
                return false;
            }
        }
        return true;
    }

    private @Nullable T attemptedValue() {
        return sourceProperty.getValue();
    }

    private void initCheck(String caller) {
        if (SUPPRESS_INIT_CHECK == null) {
            SUPPRESS_INIT_CHECK = Boolean.getBoolean(SUPPRESS_INIT_CHECK_PROPERTY);
        }
        if (SUPPRESS_INIT_CHECK) {
            return;
        }

        if (!result().initial() && LOG.isLoggable(Level.WARNING)) {
            var message = "`%s`.%s() | Rule properties should not be updated after initialization:\n"
                .formatted(name, caller);
            message += String.join("\n\t-> ", Rule.getCallChain(5, 1));
            LOG.log(Level.WARNING, message);
        }
    }
    //endregion
}