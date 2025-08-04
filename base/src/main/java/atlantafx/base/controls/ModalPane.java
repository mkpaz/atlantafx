/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.util.Animations;
import java.util.Objects;
import java.util.function.Function;
import javafx.animation.Animation;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

/**
 * A container for displaying application dialogs ot top of the current scene
 * without opening a modal {@link Stage}. It's a translucent (glass) pane
 * that can hold arbitrary content as well as animate its appearance.
 *
 * <p>When {@link #displayProperty()} value is changed the modal pane modifies own
 * {@link #viewOrderProperty()} value accordingly, thus moving itself on top of the
 * parent container or vise versa. You can change the target view order value via the
 * constructor param. This also means that one <b>must not</b> change the modal pane
 * {@link #viewOrderProperty()} property manually.
 *
 * <p>Example:
 *
 * <pre>{@code
 * public class ModalPaneExample extends Application {
 *
 *     @Override
 *     public void start(Stage primaryStage) {
 *         setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
 *
 *         class Dialog extends VBox {
 *
 *             public Dialog(int width, int height, Runnable closeHandler) {
 *                 super();
 *
 *                 setMinSize(width, height);
 *                 setMaxSize(width, height);
 *                 setSpacing(10);
 *                 setStyle("-fx-background-color: -color-bg-default;");
 *
 *                 var closeBtn = new Button("Close");
 *                 closeBtn.setOnAction(e -> closeHandler.run());
 *                 getChildren().setAll(closeBtn);
 *             }
 *         }
 *
 *         var modalPane = new ModalPane();
 *         modalPane.setAlignment(Pos.CENTER);
 *
 *         var dialogOpenBtn = new Button("Open Dialog");
 *         dialogOpenBtn.setOnAction(e ->
 *             modalPane.show(new Dialog(300, 300, () -> modalPane.hide(true)))
 *         );
 *
 *         var root = new StackPane();
 *         root.getChildren().addAll(new VBox(dialogOpenBtn), modalPane);
 *
 *         primaryStage.setScene(new Scene(root, 600, 600));
 *         primaryStage.show();
 *     }
 * }
 * }</pre>
 */
public class ModalPane extends Control {

    /**
     * The default value that is set to the modal pane
     * when it must be on top of other nodes.
     */
    public static final int Z_FRONT = -10;

    /**
     * The default value that is set to the modal pane
     * when it must be below of other nodes.
     */
    public static final int Z_BACK = 10;

    /**
     * The default animation duration that is applied to the modal content
     * when it enters the user view.
     */
    public static final Duration DEFAULT_DURATION_IN = Duration.millis(200);

    /**
     * The default animation duration that is applied to the modal content
     * when it leaves the user view.
     */
    public static final Duration DEFAULT_DURATION_OUT = Duration.millis(100);

    private final int topViewOrder;

    /**
     * Creates a new modal pane with the default {@code topViewOrder}
     * property value.
     */
    public ModalPane() {
        this(Z_FRONT);
    }

    /**
     * Creates a new modal pane with the specified {@code topViewOrder} property.
     *
     * @param topViewOrder The {@link #viewOrderProperty()} value to be set in order
     *                     to display the ModalPane on top of the parent container.
     */
    public ModalPane(@NamedArg("topViewOrder") int topViewOrder) {
        super();
        this.topViewOrder = topViewOrder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new ModalPaneSkin(this);
    }

    /**
     * Returns the value of {@link #viewOrderProperty()} to be set in order to display
     * the ModalPane on top of its parent container. This is a constructor parameter
     * that cannot be changed after the ModalPane has been instantiated.
     */
    public int getTopViewOrder() {
        return topViewOrder;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Specifies the content node to display inside the modal pane.
     */
    public ObjectProperty<@Nullable Node> contentProperty() {
        return content;
    }

    protected final ObjectProperty<@Nullable Node> content = new SimpleObjectProperty<>(this, "content", null);

    public @Nullable Node getContent() {
        return content.get();
    }

    public void setContent(@Nullable Node node) {
        this.content.set(node);
    }

    /**
     * Indicates whether the modal pane is set to be on top or not.
     * When changed, the {@link #viewOrderProperty()} value will be modified accordingly.
     * See the {@link #getTopViewOrder()}.
     */
    public BooleanProperty displayProperty() {
        return display;
    }

    protected final BooleanProperty display = new SimpleBooleanProperty(this, "display", false);

    public boolean isDisplay() {
        return display.get();
    }

    public void setDisplay(boolean display) {
        this.display.set(display);
    }

    /**
     * Specifies the alignment of the content node.
     */
    public ObjectProperty<Pos> alignmentProperty() {
        return alignment;
    }

    protected final ObjectProperty<Pos> alignment = new SimpleObjectProperty<>(this, "alignment", Pos.CENTER);

    public Pos getAlignment() {
        return alignment.get();
    }

    public void setAlignment(Pos alignment) {
        this.alignment.set(alignment);
    }

    /**
     * The factory that provides a transition to be played on content appearance,
     * i.e. when {@link #displayProperty()} is set to 'true'.
     */
    public ObjectProperty<@Nullable Function<Node, Animation>> inTransitionFactoryProperty() {
        return inTransitionFactory;
    }

    protected final ObjectProperty<@Nullable Function<Node, Animation>> inTransitionFactory = new SimpleObjectProperty<>(
        this, "inTransitionFactory", node -> Animations.zoomIn(node, DEFAULT_DURATION_IN)
    );

    public @Nullable Function<Node, Animation> getInTransitionFactory() {
        return inTransitionFactory.get();
    }

    public void setInTransitionFactory(@Nullable Function<Node, Animation> inTransitionFactory) {
        this.inTransitionFactory.set(inTransitionFactory);
    }

    /**
     * The factory that provides a transition to be played on content disappearance,
     * i.e. when {@link #displayProperty()} is set to 'false'.
     */
    public ObjectProperty<@Nullable Function<Node, Animation>> outTransitionFactoryProperty() {
        return outTransitionFactory;
    }

    protected final ObjectProperty<@Nullable Function<Node, Animation>> outTransitionFactory = new SimpleObjectProperty<>(
        this, "outTransitionFactory", node -> Animations.zoomOut(node, DEFAULT_DURATION_OUT)
    );

    public @Nullable Function<Node, Animation> getOutTransitionFactory() {
        return outTransitionFactory.get();
    }

    public void setOutTransitionFactory(@Nullable Function<Node, Animation> outTransitionFactory) {
        this.outTransitionFactory.set(outTransitionFactory);
    }

    /**
     * Specifies whether the content should be treated as persistent or not.
     *
     * <p>By default, the modal pane exits when the ESC button is pressed or when
     * the mouse is clicked outside the content area. This property prevents
     * this behavior and plays a bouncing animation instead.
     */
    public BooleanProperty persistentProperty() {
        return persistent;
    }

    protected final BooleanProperty persistent = new SimpleBooleanProperty(this, "persistent", false);

    public boolean getPersistent() {
        return persistent.get();
    }

    public void setPersistent(boolean persistent) {
        this.persistent.set(persistent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public API                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * A convenience method for setting the content of the modal pane content
     * and triggering its display state at the same time.
     */
    public void show(Node node) {
        // calling show method with no content specified doesn't make any sense
        Objects.requireNonNull(content, "Content cannot be null.");
        setContent(node);
        setDisplay(true);
    }

    /**
     * A convenience method for clearing the content of the modal pane content
     * and triggering its display state at the same time.
     */
    public void hide(boolean clear) {
        setDisplay(false);
        if (clear) {
            setContent(null);
        }
    }

    /**
     * See {@link #hide(boolean)}.
     */
    public void hide() {
        hide(false);
    }

    /**
     * See {@link #usePredefinedTransitionFactories(Side, Duration, Duration)}.
     */
    public void usePredefinedTransitionFactories(@Nullable Side side) {
        usePredefinedTransitionFactories(side, DEFAULT_DURATION_IN, DEFAULT_DURATION_OUT);
    }

    /**
     * Sets the predefined factory for both {@link #inTransitionFactoryProperty()} and
     * {@link #outTransitionFactoryProperty()} based on content position.
     */
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
                    setOutTransitionFactory(node -> Animations.slideOutUp(node, durOut));
                }
                case RIGHT -> {
                    setInTransitionFactory(node -> Animations.slideInRight(node, durIn));
                    setOutTransitionFactory(node -> Animations.slideOutRight(node, durOut));
                }
                case BOTTOM -> {
                    setInTransitionFactory(node -> Animations.slideInUp(node, durIn));
                    setOutTransitionFactory(node -> Animations.slideOutDown(node, durOut));
                }
                case LEFT -> {
                    setInTransitionFactory(node -> Animations.slideInLeft(node, durIn));
                    setOutTransitionFactory(node -> Animations.slideOutLeft(node, durOut));
                }
            }
        }
    }
}
