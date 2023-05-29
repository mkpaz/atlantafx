/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import java.util.Objects;
import java.util.function.BiConsumer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.Nullable;

final class SlotListener implements ChangeListener<Node> {

    private static final PseudoClass FILLED = PseudoClass.getPseudoClass("filled");

    private final Pane slot;
    private final @Nullable BiConsumer<Node, Boolean> onContentUpdate;

    public SlotListener(Pane slot) {
        this(slot, null);
    }

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
    public void changed(ObservableValue<? extends Node> obs, Node old, Node val) {
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
