/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import java.util.Objects;
import java.util.function.BiConsumer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.jspecify.annotations.Nullable;

/**
 * An internal convenience class for implementing slot-based approach.
 *
 * <p>It is intended to be used for controls that allow custom user nodes
 * to be placed inside their skins. his class automatically adds or removes
 * an updated <code>ObservableValue<? extends Node></code> value to/from the
 * given container and also maintains the <code>:filled</code> pseudo-class
 * state to indicate whether the corresponding slot is empty or not.
 */
final class SlotListener implements ChangeListener<Node> {

    private static final PseudoClass FILLED = PseudoClass.getPseudoClass("filled");

    private final Pane slot;
    private final @Nullable BiConsumer<@Nullable Node, Boolean> onContentUpdate;

    /**
     * Creates a new listener and binds it to the specified container.
     *
     * @param slot The container for user-specified node.
     */
    public SlotListener(Pane slot) {
        this(slot, null);
    }

    /**
     * Creates a new listener and binds it to the specified container.
     * Also, it registers the custom callback handler that will be notified
     * upon the container content changed.
     *
     * @param slot            The container for user-specified node.
     * @param onContentUpdate The callback handler to be notified upon
     *                        the container content changing.
     */
    public SlotListener(Node slot, @Nullable BiConsumer<Node, Boolean> onContentUpdate) {
        Objects.requireNonNull(slot, "Slot cannot be null.");

        this.onContentUpdate = onContentUpdate;

        if (slot instanceof Pane pane) {
            this.slot = pane;
        } else {
            throw new IllegalArgumentException("Invalid slot type. Pane is required.");
        }
    }

    @Override
    public void changed(ObservableValue<? extends Node> obs, @Nullable Node old, @Nullable Node val) {
        if (val != null) {
            slot.getChildren().setAll(val);
        } else {
            slot.getChildren().clear();
        }
        slot.setVisible(val != null);
        slot.setManaged(val != null);
        slot.pseudoClassStateChanged(FILLED, val != null);

        if (onContentUpdate != null) {
            onContentUpdate.accept(val, val != null);
        }
    }
}
