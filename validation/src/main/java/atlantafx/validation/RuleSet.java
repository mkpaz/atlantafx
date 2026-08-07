/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.lang.System.Logger.Level;
import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a collection of validation rules that can be validated as a group.
 */
public final class RuleSet implements RuleCallback<RuleSet, Failure.Composite> {

    private static final System.Logger LOG = System.getLogger(RuleSet.class.getName());
    private static @Nullable Boolean SUPPRESS_INIT_CHECK = null;

    private final String name;
    final List<Validator<?>> rules;
    private Cascade cascade = Cascade.CONTINUE;
    final Rule.Callbacks<Failure.Composite> callbacks = new Rule.Callbacks<>();

    private Subscription ruleSubscriptions = Subscription.EMPTY;
    private final ReadOnlyObjectWrapper<Result> resultProperty;
    private @Nullable Map<String, @Nullable Object> attributes = null;

    /**
     * Constructs a named {@link RuleSet} from the given validation rules.
     *
     * @param name  the optional name for the rule set
     * @param rules the validators to include in the set
     */
    private RuleSet(@Nullable String name, Validator<?>... rules) {
        if (Rule.hasDuplicates(rules)) {
            throw new IllegalArgumentException("Duplicate rules are not allowed.");
        }

        if (name == null || name.isEmpty()) {
            if (rules.length > 0) {
                var first = rules[0].asRule().name();
                int extra = rules.length - 1;
                name = extra > 0
                    ? String.format("RuleSet[%s +%d]", first, extra)
                    : String.format("RuleSet[%s]", first);
            } else {
                name = "RuleSet@" + Integer.toHexString(System.identityHashCode(this));
            }
        }
        this.name = name;

        this.rules = new ArrayList<>(Arrays.asList(rules));

        this.resultProperty = new ReadOnlyObjectWrapper<>(
            new Result.Initial(Descriptor.of(name, attributes), true)
        );
    }

    /**
     * Creates a new {@link RuleSet} from the given validation rules.
     *
     * @param rules the validators to include in the set
     */
    public static RuleSet of(Validator<?>... rules) {
        return new RuleSet(null, rules);
    }

    /**
     * Creates a new named {@link RuleSet} from the given validation rules.
     *
     * @param name  the optional name for the rule set
     * @param rules the validators to include in the set
     */
    public static RuleSet of(@Nullable String name, Validator<?>... rules) {
        return new RuleSet(name, rules);
    }

    /**
     * The name of the rule set.
     *
     * <p>If a name was not specified during construction, an automatically
     * generated name is returned.
     */
    public String name() {
        return name;
    }

    /**
     * Configures this set to update its result immediately whenever any individual
     * rule within it changes its validation result.
     *
     * <p>This method performs the following actions:
     * <ul>
     * <li>Makes all rules in this set reactive ({@link Rule#immediate()}).</li>
     * <li>Subscribes to all validation result changes.</li>
     * <li>Revalidates all rules in this set and updates the set's own validation result accordingly.</li>
     * </ul>
     */
    public RuleSet immediate() {
        initCheck("immediate");

        for (var rule : rules) {
            if (rule instanceof Rule<?> r) {
                r.immediate();
            }
        }

        if (result().initial()) {
            initRuleSetSubscription();
            revalidate();
        }

        return this;
    }

    /**
     * Sets the cascade behavior for the rules within this set.
     * The default state is {@link Cascade#CONTINUE}.
     *
     * @param cascade the cascade behavior
     */
    public RuleSet cascade(@Nullable Cascade cascade) {
        initCheck("cascade");

        this.cascade = Objects.requireNonNullElse(cascade, Cascade.CONTINUE);
        return this;
    }

    /**
     * Returns the cascade behavior for rules within this set.
     */
    public Cascade cascade() {
        return cascade;
    }

    /**
     * Returns the immutable list of validation rules in this set.
     */
    public List<Validator<?>> rules() {
        return List.copyOf(rules);
    }

    /**
     * Returns the rule in this set that performs validation against the given property.
     *
     * @param <T>      the type of value wrapped by the property
     * @param property the property instance to look up
     */
    @SuppressWarnings("unchecked")
    public @Nullable <T> Rule<T> get(ObservableValue<T> property) {
        return (Rule<T>) rules.stream()
            .filter(r -> r.asRule().sourceProperty() == property)
            .findFirst()
            .orElse(null);
    }

    /**
     * Returns the rule in this set associated with the given field name.
     *
     * @param name the name of the validated field
     */
    public @Nullable Rule<?> get(String name) {
        //noinspection ConstantValue
        if (name == null) {
            return null;
        }
        return (Rule<?>) rules.stream()
            .filter(r -> Objects.equals(r.asRule().name(), name))
            .findFirst()
            .orElse(null);
    }

    //region CALLBACKS
    //*************************************************************************

    @Override
    public RuleSet onSuccess(@Nullable Consumer<Result.Success> handler) {
        initCheck("onSuccess");
        callbacks.success = handler;
        return this;
    }

    @Override
    public RuleSet onFailure(@Nullable Consumer<Failure.Composite> handler) {
        initCheck("onFailure");
        callbacks.failure = handler;
        return this;
    }

    @Override
    public RuleSet onException(Consumer<Result.Abort> handler) {
        initCheck("onException");
        callbacks.exception = handler;
        return this;
    }

    @Override
    public RuleSet doFinally(Consumer<Descriptor> handler) {
        initCheck("doFinally");
        callbacks.finale = handler;
        return this;
    }
    //endregion

    //region VALIDATOR
    //*************************************************************************

    /**
     * Returns the observable validation result.
     */
    public ReadOnlyObjectWrapper<Result> resultProperty() {
        return resultProperty;
    }

    /**
     * Returns the validation result.
     */
    public Result result() {
        return resultProperty.get();
    }

    /**
     * Creates an observable boolean binding that is {@code true} when the validation
     * result is valid.
     */
    public ObservableValue<Boolean> observeValid() {
        return Bindings.createBooleanBinding(() -> result().valid(), resultProperty());
    }

    /**
     * Creates an observable boolean binding that is {@code true} when the validation
     * result is invalid.
     */
    public ObservableValue<Boolean> observeInvalid() {
        return Bindings.createBooleanBinding(() -> result().invalid(), resultProperty());
    }

    /**
     * Revalidates all rules in the set.
     */
    public Result revalidate() {
        try {
            // boolean stop = false;
            for (var rule : rules) {
                try {
                    // if (rule.result().initial()) {
                    //     rule.revalidate();
                    // } else if (!stop) {
                    //     var result = rule.revalidate();
                    //     if (result.invalid() && cascade == Cascade.STOP) {
                    //         stop = true;
                    //     }
                    // }

                    // deliberately not revalidate rules in initial state (even once) at Cascade.STOP
                    var result = rule.revalidate();
                    if (result.invalid() && cascade == Cascade.STOP) {
                        break;
                    }
                } catch (Exception e) {
                    var abortResult = new Result.Abort(
                        Descriptor.of(name, attributes), e, rule.asRule().name()
                    );
                    resultProperty.set(abortResult);

                    if (callbacks.exception != null) {
                        callbacks.exception.accept(abortResult);
                        return abortResult;
                    } else {
                        throw e;
                    }
                }
            }
            return updateResult();
        } finally {
            if (callbacks.finale != null) {
                callbacks.finale.accept(Descriptor.of(name, attributes));
            }
        }
    }

    /**
     * Registers a callback to be executed whenever the validation result changes.
     *
     * @param consumer the action to run on result change
     * @return a subscription that can be used to unsubscribe
     */
    public Subscription subscribe(Consumer<Result> consumer) {
        return resultProperty.subscribe(consumer);
    }
    //endregion

    //region AUXILIARY
    //*************************************************************************

    /**
     * Associates a custom key-value attribute with this rule set.
     *
     * @param key   the attribute key
     * @param value the attribute value
     * @see RuleOptionsBuilder#attribute(String, Object)
     */
    public RuleSet attribute(String key, @Nullable Object value) {
        if (attributes == null) {
            attributes = new TreeMap<>();
        }
        attributes.put(key, value);

        return this;
    }

    /**
     * Associates multiple custom key-value attributes with this rule set at once.
     *
     * @param attributes a map of attributes
     * @see RuleOptionsBuilder#attributes(Map) .
     */
    public RuleSet attributes(Map<String, Object> attributes) {
        attributes.forEach(this::attribute);
        return this;
    }

    /**
     * Disposes this rule set and unregisters all property listeners.
     *
     * <p>This method is optional and only needed if the lifetime of this rule set
     * is shorter than the lifetime of the validated object.
     */
    public void dispose() {
        ruleSubscriptions.unsubscribe();
        ruleSubscriptions = Subscription.EMPTY;

        for (var rule : rules) {
            if (rule instanceof Rule<?> r) {
                r.dispose();
            }
        }

        rules.clear();
        attributes = null;
        callbacks.dispose();
    }

    private Result updateResult() {
        try {
            var failures = new ArrayList<Failure>();
            Result.Abort ruleAbort = null;

            for (var rule : rules) {
                var ruleResult = rule.resultProperty().get();

                if (ruleResult instanceof Result.Abort r) {
                    ruleAbort = r;
                    break;
                }
                if (ruleResult.invalid() && ruleResult instanceof Failure f) {
                    failures.add(f);
                    if (cascade == Cascade.STOP) {
                        break;
                    }
                }
            }

            if (ruleAbort != null) {
                var abortResult = new Result.Abort(
                    Descriptor.of(name, attributes),
                    ruleAbort.exception(),
                    ruleAbort.ruleName()
                );
                resultProperty.set(abortResult);

                if (callbacks.exception != null) {
                    callbacks.exception.accept(abortResult);
                }
                return abortResult;
            }

            Result result = failures.isEmpty()
                ? new Result.Success(Descriptor.of(name, attributes))
                : new Failure.Composite(Descriptor.of(name, attributes), failures);

            resultProperty.set(result);

            if (result.valid() && callbacks.success != null && result instanceof Result.Success s) {
                callbacks.success.accept(s);
            }
            if (result.invalid() && callbacks.failure != null && result instanceof Failure.Composite f) {
                callbacks.failure.accept(f);
            }

            return result;
        } finally {
            if (callbacks.finale != null) {
                callbacks.finale.accept(Descriptor.of(name, attributes));
            }
        }
    }

    private void initRuleSetSubscription() {
        var subscriptions = new Subscription[rules.size()];
        for (int i = 0; i < rules.size(); i++) {
            subscriptions[i] = rules.get(i).resultProperty().subscribe(this::updateResult); // lazy listener
        }
        ruleSubscriptions = () -> {
            for (var subscription : subscriptions) {
                subscription.unsubscribe();
            }
        };
    }

    private void initCheck(String caller) {
        if (SUPPRESS_INIT_CHECK == null) {
            SUPPRESS_INIT_CHECK = Boolean.getBoolean(Validator.SUPPRESS_INIT_CHECK_PROPERTY);
        }
        if (SUPPRESS_INIT_CHECK) {
            return;
        }

        if (!result().initial() && LOG.isLoggable(Level.WARNING)) {
            var message = "%s.%s() | Rule set properties should not be updated after initialization:\n"
                .formatted(name, caller);
            message += String.join("\n\t-> ", Rule.getCallChain(5, 1));
            LOG.log(Level.WARNING, message);
        }
    }
    //endregion
}
