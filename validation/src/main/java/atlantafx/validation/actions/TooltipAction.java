/* SPDX-License-Identifier: MIT */

package atlantafx.validation.actions;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.stage.WindowEvent;
import atlantafx.validation.Descriptor;
import atlantafx.validation.Failure;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Manages a shared {@link Tooltip} across multiple JavaFX {@link Node} targets.
 *
 * <p>This action attaches or detaches a tooltip based on whether a node contains a style class
 * matching the name of a {@link Descriptor}. When the user hovers over an affected node, the text
 * of the tooltip is extracted from the validation {@link Failure} result.
 */
public class TooltipAction implements Action {

    private final List<Node> nodes;
    private final Tooltip tooltip = new Tooltip();
    private final Function<Failure, String> extractor;

    private final EventHandler<WindowEvent> showingHandler;

    private @Nullable Failure failure;

    /**
     * Constructs a {@code TooltipAction} with a custom failure message extractor.
     *
     * @param extractor a function used to extract a display string from a {@link Failure}
     * @param nodes     the target JavaFX nodes to manage tooltips for
     */
    public TooltipAction(Function<Failure, String> extractor, Node... nodes) {
        this.nodes = List.of(nodes);
        this.extractor = extractor;

        this.showingHandler = _ -> updateText();
        this.tooltip.addEventHandler(WindowEvent.WINDOW_SHOWING, showingHandler);
    }

    /**
     * Constructs a {@code TooltipAction} with a default message extractor ({@link Failure#ALL_VIOLATIONS}).
     *
     * @param nodes the target JavaFX nodes to manage tooltips for
     */
    public TooltipAction(Node... nodes) {
        this(Failure.ALL_VIOLATIONS, nodes);
    }

    @Override
    public void apply(Failure failure) {
        this.failure = failure;
        updateText();

        String styleClass = failure.descriptor().name();
        for (Node node : nodes) {
            boolean matchesStyle = node.getStyleClass().contains(styleClass);
            boolean linked = isLinkedTo(node);

            if (matchesStyle && !linked) {
                Tooltip.install(node, tooltip);
            }
        }
    }

    @Override
    public void clear(Descriptor descriptor) {
        String styleClass = descriptor.name();

        for (var node : nodes) {
            if (node.getStyleClass().contains(styleClass) && isLinkedTo(node)) {
                Tooltip.uninstall(node, tooltip);
            }
        }

        if (failure != null && Objects.equals(failure.descriptor().name(), styleClass)) {
            this.failure = null;
            updateText();
        }
    }

    /**
     * Returns the underlying managed {@link Tooltip} instance for user configuration.
     *
     * @return the {@link Tooltip} instance controlled by this action
     */
    public Tooltip getTooltip() {
        return tooltip;
    }

    /**
     * Checks whether the managed tooltip is linked to the given node.
     */
    public boolean isLinkedTo(Node node) {
        if (node instanceof Control c && c.getTooltip() == tooltip) {
            return true;
        }
        return node.getProperties().containsValue(tooltip);
    }

    /**
     * Disposes this action and unregisters all listeners.
     */
    public void dispose() {
        tooltip.removeEventHandler(WindowEvent.WINDOW_SHOWING, showingHandler);
        for (var node : nodes) {
            if (isLinkedTo(node)) {
                Tooltip.uninstall(node, tooltip);
            }
        }
    }

    protected void updateText() {
        if (failure == null) {
            tooltip.setText(null);
            return;
        }
        tooltip.setText(extractor.apply(failure));
    }
}