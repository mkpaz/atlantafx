/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

/**
 * A helper class for managing inline CSS styles on JavaFX {@link Node} instances.
 *
 * <p>An instance of {@code StyleMap} is attached to a target {@link Node}'s property map via
 * {@link Node#getProperties()}, ensuring that both share the same lifecycle. Repeated calls to
 * {@link #on(Node)} return the same instance for a given node.
 *
 * <p>Calling {@link #bind()} connects the internal style map to the node's {@link Node#styleProperty()}.
 * Once bound, any subsequent mutation (such as {@link #set(String, String)}) automatically triggers an
 * update to the node's style. Without binding, changes take effect only after an explicit call to
 * {@link #apply()}.
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // explicit
 * StyleMap.on(myButton)
 *         .set("-fx-background-color", "darkblue")
 *         .set("-fx-text-fill", "white")
 *         .apply();
 *
 * // bound
 * StyleMap.on(myButton)
 *         .bind()
 *         .set("-fx-font-size", "14px");
 * }</pre>
 */
public class StyleMap {

    protected static final String PROPERTY_KEY = StyleMap.class.getName() + ".instance";

    protected final Node node;
    protected final Map<String, String> attributes = new TreeMap<>();
    protected final BooleanProperty invalidationProperty = new SimpleBooleanProperty(false);
    protected @Nullable Binding<String> styleBinding;

    /**
     * Constructs a {@code StyleMap} associated with the specified {@link Node}.
     *
     * @param node the target node
     */
    protected StyleMap(Node node) {
        this.node = node;
    }

    /**
     * Obtains the {@code StyleMap} associated with the specified {@link Node}.
     *
     * <p>If a {@code StyleMap} already exists in the node's property map, that instance is returned.
     * Otherwise, a new {@code StyleMap} is created, stored in {@link Node#getProperties()}, and returned.
     *
     * @param node the target node to retrieve or attach a {@code StyleMap} for
     * @return the {@code StyleMap} instance attached to the given node
     */
    public static StyleMap on(Node node) {
        Object existing = node.getProperties().get(PROPERTY_KEY);
        if (existing instanceof StyleMap map) {
            return map;
        }

        var map = new StyleMap(node);
        node.getProperties().put(PROPERTY_KEY, map);
        return map;
    }

    /**
     * Returns the value of the specified CSS attribute.
     *
     * @param key the CSS attribute key
     */
    public @Nullable String get(String key) {
        return attributes.get(key);
    }

    /**
     * Sets or removes a CSS attribute.
     *
     * <p>If {@code value} is {@code null}, the property corresponding to {@code key} is removed.
     * If the {@code StyleMap} is bound via {@link #bind()}, the change takes effect immediately.
     *
     * @param key   the CSS attribute key
     * @param value the CSS attribute value, or {@code null} to remove the key
     */
    public StyleMap set(String key, @Nullable String value) {
        if (value == null) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
        fireChanged();
        return this;
    }

    /**
     * Sets all key-value pairs from the specified map into this {@code StyleMap}.
     *
     * <p>If any value in the map is {@code null}, the corresponding property key is removed.
     * Changes are applied together and {@link #fireChanged()} is triggered only once.
     *
     * @param styles a map of CSS attributes to set
     */
    public StyleMap set(Map<String, @Nullable String> styles) {
        if (styles.isEmpty()) {
            return this;
        }

        boolean modified = false;
        for (var entry : styles.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null) {
                if (attributes.remove(key) != null) {
                    modified = true;
                }
            } else {
                String previous = attributes.put(key, value);
                if (!value.equals(previous)) {
                    modified = true;
                }
            }
        }

        if (modified) {
            fireChanged();
        }

        return this;
    }

    /**
     * Removes multiple CSS attributes specified by the given keys.
     *
     * <p>Changes are applied together and {@link #fireChanged()} is triggered only once.
     *
     * @param keys the CSS attributes to remove
     */
    public StyleMap remove(Iterable<String> keys) {
        boolean modified = false;
        for (String key : keys) {
            if (attributes.remove(key) != null) {
                modified = true;
            }
        }

        if (modified) {
            fireChanged();
        }
        return this;
    }

    /**
     * Removes multiple CSS attributes specified by the given keys.
     *
     * <p>Changes are applied together and {@link #fireChanged()} is triggered only once.
     *
     * @param keys the CSS attributes to remove
     */
    public StyleMap remove(String... keys) {
        return remove(java.util.Arrays.asList(keys));
    }

    /**
     * Removes a CSS attribute.
     *
     * @param key the CSS attribute to remove
     */
    public StyleMap remove(String key) {
        if (attributes.containsKey(key)) {
            attributes.remove(key);
            fireChanged();
        }
        return this;
    }

    /**
     * Clears all CSS attributes.
     */
    public StyleMap clear() {
        attributes.clear();
        fireChanged();
        return this;
    }

    /**
     * Returns an unmodifiable copy of the CSS attributes contained in this {@code StyleMap}.
     */
    public Map<String, String> attributes() {
        return Map.copyOf(attributes);
    }

    /**
     * Formats the {@code StyleMap} into a valid CSS style string.
     *
     * @return a formatted CSS style string
     */
    @Override
    public String toString() {
        if (attributes.isEmpty()) {
            return "";
        }
        var sb = new StringBuilder(attributes.size() * 32);
        for (var entry : attributes.entrySet()) {
            sb.append(entry.getKey())
                .append(':')
                .append(entry.getValue())
                .append(';');
        }
        return sb.toString();
    }

    /**
     * Applies the accumulated CSS styles to the target node via {@link Node#setStyle(String)}.
     *
     * <p>This method has no effect if the {@code StyleMap} is currently bound via {@link #bind()}.
     */
    public void apply() {
        if (!isBound()) {
            node.setStyle(toString());
        }
    }

    /**
     * Binds the {@code StyleMap} to the target node's {@link Node#styleProperty()}.
     *
     * <p>Once bound, any modification made via {@link #set(String, String)}, {@link #remove(String)},
     * or {@link #clear()} instantly updates the node's inline styles.
     */
    public StyleMap bind() {
        if (!isBound()) {
            styleBinding = Bindings.createStringBinding(this::toString, invalidationProperty);
            node.styleProperty().bind(styleBinding);
        }
        return this;
    }

    /**
     * Unbinds the {@code StyleMap} from the target node's {@link Node#styleProperty()}
     * if it was previously bound.
     */
    public void unbind() {
        if (isBound()) {
            node.styleProperty().unbind();
            if (styleBinding != null) {
                styleBinding.dispose();
                styleBinding = null;
            }
        }
    }

    /**
     * Checks whether this {@code StyleMap} is currently bound to the node's style property.
     */
    public boolean isBound() {
        return styleBinding != null && node.styleProperty().isBound();
    }

    /**
     * Triggers a state change event to invalidate the style binding and update the target
     * node's styles.
     */
    public void fireChanged() {
        invalidationProperty.set(!invalidationProperty.get());
    }
}