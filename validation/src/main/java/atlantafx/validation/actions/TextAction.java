/* SPDX-License-Identifier: MIT */

package atlantafx.validation.actions;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import atlantafx.validation.Descriptor;
import atlantafx.validation.Failure;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * An action that manages the text content of properties or text-bearing nodes.
 *
 * <p>This action sets the target string properties to the formatted message extracted from a
 * {@link Failure} when validation fails and restores them to {@code null} when cleared.
 *
 * @param extractor  a function that extracts a text message from a {@link Failure}
 * @param properties the string properties to manage
 */
public record TextAction(Function<Failure, String> extractor, List<StringProperty> properties) implements Action {

    public TextAction {
        Objects.requireNonNull(extractor, "extractor must not be null");
        properties = List.copyOf(properties);
    }

    /**
     * Constructs a {@code TextAction} for the specified string properties using
     * {@link Failure#message()} as the default text extractor.
     *
     * @param properties the string properties to manage
     */
    public TextAction(List<StringProperty> properties) {
        this(Failure::message, properties);
    }

    /**
     * Constructs a {@code TextAction} for the specified string properties using
     * {@link Failure#message()} as the default text extractor.
     *
     * @param properties the string properties to manage
     */
    public TextAction(StringProperty... properties) {
        this(Failure::message, List.of(properties));
    }

    /**
     * Constructs a {@code TextAction} for the specified string properties with a custom message extractor.
     *
     * @param extractor  a function that extracts a text message from a {@link Failure}
     * @param properties the string properties to manage
     */
    public TextAction(Function<Failure, String> extractor, StringProperty... properties) {
        this(extractor, List.of(properties));
    }

    /**
     * Constructs a {@code TextAction} targeting the {@link Label#textProperty()} of the given labels
     * using {@link Failure#message()} as the default text extractor.
     *
     * @param nodes the target labels to manage
     */
    public TextAction(Label... nodes) {
        this(Failure::message, Arrays.stream(nodes).map(Label::textProperty).toList());
    }

    /**
     * Constructs a {@code TextAction} targeting the {@link Label#textProperty()} of the given labels
     * with a custom message extractor.
     *
     * @param extractor a function that extracts a text message from a {@link Failure}
     * @param nodes     the target labels to manage
     */
    public TextAction(Function<Failure, String> extractor, Label... nodes) {
        this(extractor, Arrays.stream(nodes).map(Label::textProperty).toList());
    }

    /**
     * Constructs a {@code TextAction} targeting the {@link Text#textProperty()} of the given text nodes
     * using {@link Failure#message()} as the default text extractor.
     *
     * @param nodes the target text nodes to manage
     */
    public TextAction(Text... nodes) {
        this(Failure::message, Arrays.stream(nodes).map(Text::textProperty).toList());
    }

    /**
     * Constructs a {@code TextAction} targeting the {@link Text#textProperty()} of the given text nodes
     * with a custom message extractor.
     *
     * @param extractor a function that extracts a text message from a {@link Failure}
     * @param nodes     the target text nodes to manage
     */
    public TextAction(Function<Failure, String> extractor, Text... nodes) {
        this(extractor, Arrays.stream(nodes).map(Text::textProperty).toList());
    }

    @Override
    public void apply(Failure failure) {
        String text = extractor.apply(failure);
        for (var property : properties) {
            property.set(text);
        }
    }

    @Override
    public void clear(Descriptor descriptor) {
        for (var property : properties) {
            property.set(null);
        }
    }
}