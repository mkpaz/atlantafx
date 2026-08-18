package atlantafx.base.util;

import javafx.application.Platform;
import javafx.scene.Node;
import org.jspecify.annotations.Nullable;

/**
 * Allows a component to designate a primary node that should receive keyboard focus.
 */
public interface Focusable {

    /**
     * Returns the node that should receive focus.
     *
     * @return the focus target, or {@code null} if this component currently has none
     */
    @Nullable
    Node getFocusTarget();

    /**
     * Attempts to move keyboard focus onto {@link #getFocusTarget()}.
     *
     * @see #focus(Node, int)
     */
    default void focus(int attempts) {
        focus(getFocusTarget(), attempts);
    }

    /**
     * Attempts to move keyboard focus onto the given node.
     *
     * <p>Each attempt is deferred to the next JavaFX pulse via {@link Platform#runLater},
     * so this method is safe to call from any thread. Retries stop as soon as the target
     * node reports {@link Node#isFocused()}, or once {@code attempts} is exhausted,
     * whichever comes first.
     *
     * @param node     the focus target
     * @param attempts the maximum number of focus requests to issue
     */
    static void focus(@Nullable Node node, int attempts) {
        if (node == null || attempts <= 0) {
            return;
        }

        Platform.runLater(() -> {
            if (!node.isFocused()) {
                node.requestFocus();
                focus(node, attempts - 1);
            }
        });
    }
}