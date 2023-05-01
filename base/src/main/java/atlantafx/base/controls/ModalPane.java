/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.util.Animations;
import java.util.Objects;
import java.util.function.Function;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Duration;
import org.jetbrains.annotations.Nullable;

/**
 * A container for displaying application dialogs ot top of the current scene
 * without opening a modal {@link javafx.stage.Stage}. It's a translucent (glass) pane
 * that can hold arbitrary content as well as animate its appearance.<br/><br/>
 *
 * <p>When {@link #displayProperty()} value is changed the modal pane modifies own
 * {@link #viewOrderProperty()} value accordingly, thus moving itself on top of the
 * parent container or vise versa. You can change the target view order value via the
 * constructor param. This also means that one must not change modal pane {@link #viewOrderProperty()}
 * property manually.
 */
public class ModalPane extends Control {

    protected static final int Z_FRONT = -10;
    protected static final int Z_BACK = 10;

    public static final Duration DEFAULT_DURATION_IN = Duration.millis(200);
    public static final Duration DEFAULT_DURATION_OUT = Duration.millis(100);

    private final int topViewOrder;

    /**
     * See {@link #ModalPane(int)}.
     */
    public ModalPane() {
        this(Z_FRONT);
    }

    /**
     * Creates a new modal pane.
     *
     * @param topViewOrder the {@link #viewOrderProperty()} value to be set
     *                     to display the modal pane on top of the parent container.
     */
    public ModalPane(int topViewOrder) {
        super();
        this.topViewOrder = topViewOrder;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ModalPaneSkin(this);
    }

    public int getTopViewOrder() {
        return topViewOrder;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    protected ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content", null);

    public Node getContent() {
        return content.get();
    }

    public void setContent(Node node) {
        this.content.set(node);
    }

    /**
     * The content node to display inside the modal pane.
     */
    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    // ~

    protected BooleanProperty display = new SimpleBooleanProperty(this, "display", false);

    public boolean isDisplay() {
        return display.get();
    }

    public void setDisplay(boolean display) {
        this.display.set(display);
    }

    /**
     * Whether the modal pane is set to top or not.
     * When changed the pane {@link #viewOrderProperty()} value will be modified accordingly.
     */
    public BooleanProperty displayProperty() {
        return display;
    }

    // ~

    protected ObjectProperty<Pos> alignment = new SimpleObjectProperty<>(this, "alignment", Pos.CENTER);

    public Pos getAlignment() {
        return alignment.get();
    }

    public void setAlignment(Pos alignment) {
        this.alignment.set(alignment);
    }

    /**
     * Content alignment.
     */
    public ObjectProperty<Pos> alignmentProperty() {
        return alignment;
    }

    // ~

    protected ObjectProperty<Function<Node, Animation>> inTransitionFactory = new SimpleObjectProperty<>(
        this, "inTransitionFactory", node -> Animations.zoomIn(node, DEFAULT_DURATION_IN)
    );

    public Function<Node, Animation> getInTransitionFactory() {
        return inTransitionFactory.get();
    }

    public void setInTransitionFactory(Function<Node, Animation> inTransitionFactory) {
        this.inTransitionFactory.set(inTransitionFactory);
    }

    /**
     * The factory that provides a transition to be played on content appearance,
     * i.e. when {@link #displayProperty()} is set to 'true'.
     */
    public ObjectProperty<Function<Node, Animation>> inTransitionFactoryProperty() {
        return inTransitionFactory;
    }

    // ~

    protected ObjectProperty<Function<Node, Animation>> outTransitionFactory = new SimpleObjectProperty<>(
        this, "outTransitionFactory", node -> Animations.zoomOut(node, DEFAULT_DURATION_OUT)
    );

    public Function<Node, Animation> getOutTransitionFactory() {
        return outTransitionFactory.get();
    }

    public void setOutTransitionFactory(Function<Node, Animation> outTransitionFactory) {
        this.outTransitionFactory.set(outTransitionFactory);
    }

    /**
     * The factory that provides a transition to be played on content disappearance,
     * i.e. when {@link #displayProperty()} is set to 'false'.
     */
    public ObjectProperty<Function<Node, Animation>> outTransitionFactoryProperty() {
        return outTransitionFactory;
    }

    // ~

    protected BooleanProperty persistent = new SimpleBooleanProperty(this, "persistent", false);

    public boolean getPersistent() {
        return persistent.get();
    }

    public void setPersistent(boolean persistent) {
        this.persistent.set(persistent);
    }

    /**
     * Specifies whether content should be treated as persistent or not.
     * By default, modal pane exits when on ESC button or mouse click outside the contenbt are.
     * This property prevents this behavior and plays bouncing animation instead.
     */
    public BooleanProperty persistentProperty() {
        return persistent;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public API                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * A convenience method for setting the content of the modal pane content
     * and triggering display state at the same time.
     */
    public void show(Node node) {
        // calling show method with no content specified doesn't make any sense
        Objects.requireNonNull(content, "Content cannot be null.");
        setContent(node);
        setDisplay(true);
    }

    /**
     * See {@link #hide(boolean)}.
     */
    public void hide() {
        hide(false);
    }

    /**
     * A convenience method for clearing the content of the modal pane content
     * and triggering display state at the same time.
     */
    public void hide(boolean clear) {
        setDisplay(false);
        if (clear) {
            setContent(null);
        }
    }

    /**
     * Sets the predefined factory for both {@link #inTransitionFactoryProperty()} and
     * {@link #outTransitionFactoryProperty()} based on content position.
     */
    public void usePredefinedTransitionFactories(@Nullable Side side) {
        usePredefinedTransitionFactories(side, DEFAULT_DURATION_IN, DEFAULT_DURATION_OUT);
    }

    public void usePredefinedTransitionFactories(@Nullable Side side,
                                                 @Nullable Duration inDuration,
                                                 @Nullable Duration outDuration) {
        Duration durIn = Objects.requireNonNullElse(inDuration, DEFAULT_DURATION_IN);
        Duration durOut = Objects.requireNonNullElse(outDuration, DEFAULT_DURATION_OUT);

        if (side == null) {
            setInTransitionFactory(node -> Animations.zoomIn(node, durIn));
            setOutTransitionFactory(node -> Animations.fadeOut(node, durOut));
        } else {
            switch (side) {
                case TOP -> {
                    setInTransitionFactory(node -> Animations.slideInDown(node, durIn));
                    setOutTransitionFactory(node -> Animations.slideOutDown(node, durOut));
                }
                case RIGHT -> {
                    setInTransitionFactory(node -> Animations.slideInRight(node, durIn));
                    setOutTransitionFactory(node -> Animations.slideOutRight(node, durOut));
                }
                case BOTTOM -> {
                    setInTransitionFactory(node -> Animations.slideInUp(node, durIn));
                    setOutTransitionFactory(node -> Animations.slideOutUp(node, durOut));
                }
                case LEFT -> {
                    setInTransitionFactory(node -> Animations.slideInLeft(node, durIn));
                    setOutTransitionFactory(node -> Animations.slideOutLeft(node, durOut));
                }
            }
        }
    }
}
