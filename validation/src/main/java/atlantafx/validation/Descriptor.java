/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Identifies the rule or rule set that produced a validation result, together with
 * any custom attributes attached to it.
 *
 * @param name       the name of the rule or rule set this descriptor belongs to
 * @param attributes custom key-value attributes
 */
public record Descriptor(String name, Map<String, @Nullable Object> attributes) {

    /**
     * A descriptor with an empty name and no attributes.
     */
    public static final Descriptor EMPTY = new Descriptor("", Map.of());

    /**
     * Creates a descriptor with the given name and an immutable copy of the given attributes.
     */
    public static Descriptor of(String name, @Nullable Map<String, @Nullable Object> attributes) {
        return new Descriptor(name, attributes != null ? Map.copyOf(attributes) : Map.of());
    }
}
