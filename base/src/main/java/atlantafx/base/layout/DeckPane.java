/* SPDX-License-Identifier: MIT */

package atlantafx.base.layout;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jetbrains.annotations.Nullable;

/**
 * DeckPane represents a pane that displays all of its child nodes in a deck,
 * where only one node can be visible at a time. It does not maintain any sequence
 * (model), but only cares about the top node, which can be changed by various
 * transition effects.<p/>
 *
 * <h3>View Order</h3>
 *
 * <p>DeckPane manages {@link Node#viewOrderProperty()} of its children. Topmost
 * visible node always has a higher view order value, while the rest of the nodes
 * have the default value, which is zero. Following that logic, one must not set
 * child nodes view order manually, because it will break the contract.
 *
 * <p>If all child nodes have the same view order value (default state after creating
 * a new DeckPane), they are displayed in order specified by the root container, which
 * is {@link AnchorPane}. When a node is removed from the pane, its view order is
 * restored automatically.
 */
public class DeckPane extends AnchorPane {

    protected static final Comparator<Node> Z_COMPARATOR = Comparator.comparingDouble(Node::getViewOrder);

    // while transition lasts, animated nodes must be on the top
    protected static final int Z_ANIMATED_IN = -20;
    protected static final int Z_ANIMATED_OUT = -15;

    // topmost deck node
    protected static final int Z_DECK_TOP = -10;

    // the rest of the nodes
    protected static final int Z_DEFAULT = 0;

    public DeckPane() {
        super();

        getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                // restore view order for removed nodes
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(node -> node.setViewOrder(Z_DEFAULT));
                }
            }
        });
    }

    public DeckPane(Node... children) {
        this();
        getChildren().addAll(children);
    }

    /**
     * Returns the node with the higher view order value or the last node
     * if all child nodes have the same view order value.
     */
    public @Nullable Node getTopNode() {
        var size = getChildren().size();

        if (size == 0) {
            return null;
        }

        return getChildren().stream()
            // if two elements have equal viewOrder, last wins
            // unlike the default min() implementation
            .reduce((o1, o2) -> Z_COMPARATOR.compare(o1, o2) >= 0 ? o2 : o1)
            // if all equal, return the last node
            .orElse(getChildren().get(size - 1));
    }

    /**
     * Sets given node on top without playing any transition.
     * Does nothing if that node isn't added to the pane.
     */
    public void setTopNode(Node target) {
        if (!getChildren().contains(target)) {
            return;
        }

        var topNode = getTopNode();

        if (topNode == null || topNode == target) {
            return;
        }

        setViewOrder(topNode, Z_DEFAULT);
        setViewOrder(target, Z_DECK_TOP);
    }

    /**
     * Sets current top node view order to the default value.
     */
    public void resetTopNode() {
        var topNode = getTopNode();
        if (topNode != null) {
            setViewOrder(topNode, Z_DEFAULT);
        }
    }

    /**
     * Adds given nodes to the pane and binds them to the pane edges
     * using the provided offsets. See {@link AnchorPane#setTopAnchor(Node, Double)}
     * for the reference.
     */
    public void addChildren(Insets offsets, Node... nodes) {
        for (var node : nodes) {
            AnchorPane.setTopAnchor(node, offsets.getTop());
            AnchorPane.setRightAnchor(node, offsets.getRight());
            AnchorPane.setBottomAnchor(node, offsets.getBottom());
            AnchorPane.setLeftAnchor(node, offsets.getLeft());
        }
        getChildren().addAll(nodes);
    }

    /**
     * Places target node on the top of the pane while playing the
     * swipe transition from bottom to top. If the pane doesn't contain
     * that node, it will be added to the end before playing transition.
     */
    public void swipeUp(Node target) {
        var topNode = getTopNode();

        if (!prepareAndCheck(topNode, target)) {
            return;
        }

        Objects.requireNonNull(topNode);

        setViewOrder(topNode, Z_ANIMATED_OUT);
        setViewOrder(target, Z_ANIMATED_IN);

        var transition = new ParallelTransition(
            moveYUpFromTopBorderToOffCanvas(topNode),  // out
            moveYUpFromBottomBorderToTopBorder(target) // in
        );
        transition.setOnFinished(e -> onTransitionFinished(topNode, target));
        setAnimationActive(true);
        transition.play();
    }

    /**
     * Places target node on the top of the pane while playing the
     * swipe transition from top to bottom. If the pane doesn't contain
     * that node, it will be added to the end before playing transition.
     */
    public void swipeDown(Node target) {
        var topNode = getTopNode();

        if (!prepareAndCheck(topNode, target)) {
            return;
        }

        Objects.requireNonNull(topNode);

        setViewOrder(topNode, Z_ANIMATED_OUT);
        setViewOrder(target, Z_ANIMATED_IN);

        var transition = new ParallelTransition(
            moveYDownFromTopBorderToBottomBorder(topNode), // out
            moveYDownFromOffCanvasToTopBorder(target)      // in
        );
        transition.setOnFinished(e -> onTransitionFinished(topNode, target));
        setAnimationActive(true);
        transition.play();
    }

    /**
     * Places target node on the top of the pane while playing the
     * swipe transition from right to left. If the pane doesn't contain
     * that node, it will be added to the end before playing transition.
     */
    public void swipeLeft(Node target) {
        var topNode = getTopNode();

        if (!prepareAndCheck(topNode, target)) {
            return;
        }

        Objects.requireNonNull(topNode);

        setViewOrder(topNode, Z_ANIMATED_OUT);
        setViewOrder(target, Z_ANIMATED_IN);

        var transition = new ParallelTransition(
            moveXLeftFromLeftBorderToOffCanvas(topNode), // out
            moveXLeftFromRightBorderToLeftBorder(target)  // in
        );
        transition.setOnFinished(e -> onTransitionFinished(topNode, target));
        setAnimationActive(true);
        transition.play();
    }

    /**
     * Places target node on the top of the pane while playing the
     * swipe transition from left to right. If the pane doesn't contain
     * that node, it will be added to the end before playing transition.
     */
    public void swipeRight(Node target) {
        var topNode = getTopNode();

        if (!prepareAndCheck(topNode, target)) {
            return;
        }

        Objects.requireNonNull(topNode);

        setViewOrder(topNode, Z_ANIMATED_OUT);
        setViewOrder(target, Z_ANIMATED_IN);

        var transition = new ParallelTransition(
            moveXRightFromLeftBorderToRightBorder(topNode), // out
            moveXRightFromOffCanvasToLeftBorder(target)     // in
        );
        transition.setOnFinished(e -> onTransitionFinished(topNode, target));
        setAnimationActive(true);
        transition.play();
    }

    /**
     * Places target node on the top of the pane while playing the
     * slide transition from bottom to top. If the pane doesn't contain
     * that node, it will be added to the end before playing transition.
     */
    public void slideUp(Node target) {
        var topNode = getTopNode();

        if (!prepareAndCheck(topNode, target)) {
            return;
        }

        Objects.requireNonNull(topNode);

        setViewOrder(topNode, Z_ANIMATED_OUT);
        setViewOrder(target, Z_ANIMATED_IN);

        var transition = moveYUpFromBottomBorderToTopBorder(target);
        transition.setOnFinished(e -> onTransitionFinished(topNode, target));
        setAnimationActive(true);
        transition.play();
    }

    /**
     * Places target node on the top of the pane while playing the
     * slide transition from top to bottom. If the pane doesn't contain
     * that node, it will be added to the end before playing transition.
     */
    public void slideDown(Node target) {
        var topNode = getTopNode();

        if (!prepareAndCheck(topNode, target)) {
            return;
        }

        Objects.requireNonNull(topNode);

        setViewOrder(topNode, Z_ANIMATED_OUT);
        setViewOrder(target, Z_ANIMATED_IN);

        var transition = moveYDownFromOffCanvasToTopBorder(target);
        transition.setOnFinished(e -> onTransitionFinished(topNode, target));
        setAnimationActive(true);
        transition.play();
    }

    /**
     * Places target node on the top of the pane while playing the
     * slide transition from right to left. If the pane doesn't contain
     * that node, it will be added to the end before playing transition.
     */
    public void slideLeft(Node target) {
        var topNode = getTopNode();

        if (!prepareAndCheck(topNode, target)) {
            return;
        }

        Objects.requireNonNull(topNode);

        setViewOrder(topNode, Z_ANIMATED_OUT);
        setViewOrder(target, Z_ANIMATED_IN);

        var transition = moveXLeftFromRightBorderToLeftBorder(target);
        transition.setOnFinished(e -> onTransitionFinished(topNode, target));
        setAnimationActive(true);
        transition.play();
    }

    /**
     * Places target node on the top of the pane while playing the
     * slide transition from left to right. If the pane doesn't contain
     * that node, it will be added to the end before playing transition.
     */
    public void slideRight(Node target) {
        var topNode = getTopNode();

        if (!prepareAndCheck(topNode, target)) {
            return;
        }

        Objects.requireNonNull(topNode);

        setViewOrder(topNode, Z_ANIMATED_OUT);
        setViewOrder(target, Z_ANIMATED_IN);

        var transition = moveXRightFromOffCanvasToLeftBorder(target);
        transition.setOnFinished(e -> onTransitionFinished(topNode, target));
        setAnimationActive(true);
        transition.play();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    protected final ObjectProperty<Duration> animationDuration =
        new SimpleObjectProperty<>(this, "animationDuration", Duration.seconds(1));

    public Duration getAnimationDuration() {
        return animationDuration.get();
    }

    /**
     * Duration of the transition effect which is played when changing the top node.
     */
    public ObjectProperty<Duration> animationDurationProperty() {
        return animationDuration;
    }

    public void setAnimationDuration(@Nullable Duration animationDuration) {
        this.animationDuration.set(Objects.requireNonNullElse(animationDuration, Duration.ZERO));
    }

    // ~

    protected final ReadOnlyBooleanWrapper animationActive =
        new ReadOnlyBooleanWrapper(this, "animationActive");

    public boolean isAnimationActive() {
        return animationActive.get();
    }

    /**
     * Returns whether transition is in progress. Subscribe to be notified
     * when animation started or finished.
     */
    public ReadOnlyBooleanProperty animationActiveProperty() {
        return animationActive.getReadOnlyProperty();
    }

    protected void setAnimationActive(boolean animationActive) {
        this.animationActive.set(animationActive);
    }

    // ~

    protected final ObjectProperty<Consumer<Node>> beforeShowCallback =
        new SimpleObjectProperty<>(this, "beforeShowCallback");

    public @Nullable Consumer<Node> getBeforeShowCallback() {
        return beforeShowCallback.get();
    }

    /**
     * Callback action to be called before setting a node at the top of the deck.
     */
    public ObjectProperty<Consumer<Node>> beforeShowCallbackProperty() {
        return beforeShowCallback;
    }

    public void setBeforeShowCallback(@Nullable Consumer<Node> callback) {
        this.beforeShowCallback.set(callback);
    }

    protected void runBeforeShowCallback(Node node) {
        if (getBeforeShowCallback() != null) {
            getBeforeShowCallback().accept(node);
        }
    }

    // ~

    protected final ObjectProperty<Consumer<Node>> afterHideCallback =
        new SimpleObjectProperty<>(this, "afterHideCallback");

    public @Nullable Consumer<Node> getAfterHideCallback() {
        return afterHideCallback.get();
    }

    /**
     * Callback action to be called after removing the top node from the top of the deck.
     */
    public ObjectProperty<Consumer<Node>> afterHideCallbackProperty() {
        return afterHideCallback;
    }

    public void setAfterHideCallback(@Nullable Consumer<Node> callback) {
        this.afterHideCallback.set(callback);
    }

    protected void runAfterHideCallback(Node node) {
        if (getAfterHideCallback() != null) {
            getAfterHideCallback().accept(node);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal API                                                          //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Verifies that transition is possible and prepares initial conditions.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean prepareAndCheck(@Nullable Node topNode, @Nullable Node target) {
        if (topNode == null || target == null || target == topNode || isAnimationActive()) {
            return false;
        }

        if (!getChildren().contains(target)) {
            getChildren().add(target);
        }

        runBeforeShowCallback(target);

        return true;
    }

    /**
     * Cleans-up properties after transition finished.
     */
    protected void onTransitionFinished(Node topNode, Node target) {
        resetNode(topNode);
        resetNode(target);

        setViewOrder(topNode, Z_DEFAULT);
        setViewOrder(target, Z_DECK_TOP);

        runAfterHideCallback(topNode);
        setAnimationActive(false);
    }

    protected Timeline moveYUpFromTopBorderToOffCanvas(Node node) {
        var clip = new Rectangle();
        clip.setWidth(getWidth());
        node.setClip(clip);

        // animated properties
        clip.setHeight(getHeight());
        clip.translateYProperty().set(0);
        node.translateYProperty().set(0);

        var timeline = new Timeline();
        timeline.getKeyFrames().add(
            new KeyFrame(
                animationDuration.getValue(),
                new KeyValue(clip.heightProperty(), 0),
                new KeyValue(clip.translateYProperty(), getHeight()),
                new KeyValue(node.translateYProperty(), -getHeight())
            ));

        return timeline;
    }

    protected Timeline moveYUpFromBottomBorderToTopBorder(Node node) {
        var clip = new Rectangle();
        clip.setWidth(getWidth());
        node.setClip(clip);

        // animated properties
        clip.setHeight(0);
        node.translateYProperty().set(getHeight());

        var timeline = new Timeline();

        timeline.getKeyFrames().add(
            new KeyFrame(
                animationDuration.getValue(),
                new KeyValue(clip.heightProperty(), getHeight()),
                new KeyValue(node.translateYProperty(), 0)
            ));

        return timeline;
    }

    protected Timeline moveYDownFromTopBorderToBottomBorder(Node node) {
        var clip = new Rectangle();
        clip.setWidth(getWidth());
        node.setClip(clip);

        // animated properties
        clip.setHeight(getHeight());
        node.translateYProperty().set(0);

        var timeline = new Timeline();
        timeline.getKeyFrames().add(
            new KeyFrame(
                animationDuration.getValue(),
                new KeyValue(clip.heightProperty(), 0),
                new KeyValue(node.translateYProperty(), getHeight())
            ));

        return timeline;
    }

    protected Timeline moveYDownFromOffCanvasToTopBorder(Node node) {
        var clip = new Rectangle();
        clip.setWidth(getWidth());
        node.setClip(clip);

        // animated properties
        clip.setHeight(0);
        clip.translateYProperty().set(getHeight());
        node.translateYProperty().set(-getHeight());

        var timeline = new Timeline();

        timeline.getKeyFrames().add(
            new KeyFrame(
                animationDuration.getValue(),
                new KeyValue(clip.heightProperty(), getHeight()),
                new KeyValue(clip.translateYProperty(), 0),
                new KeyValue(node.translateYProperty(), 0)
            ));

        return timeline;
    }

    protected Timeline moveXLeftFromLeftBorderToOffCanvas(Node node) {
        var clip = new Rectangle();
        clip.setHeight(getHeight());
        node.setClip(clip);

        // animated properties
        clip.setWidth(getWidth());
        clip.translateXProperty().set(0);
        node.translateXProperty().set(0);

        var timeline = new Timeline();
        timeline.getKeyFrames().add(
            new KeyFrame(
                animationDuration.getValue(),
                new KeyValue(clip.widthProperty(), 0),
                new KeyValue(clip.translateXProperty(), getWidth()),
                new KeyValue(node.translateXProperty(), -getWidth())
            ));

        return timeline;
    }

    protected Timeline moveXLeftFromRightBorderToLeftBorder(Node node) {
        var clip = new Rectangle();
        clip.setHeight(getHeight());
        node.setClip(clip);

        // animated properties
        clip.setWidth(0);
        node.translateXProperty().set(getWidth());

        var timeline = new Timeline();

        timeline.getKeyFrames().add(
            new KeyFrame(
                animationDuration.getValue(),
                new KeyValue(clip.widthProperty(), getWidth()),
                new KeyValue(node.translateXProperty(), 0)
            ));

        return timeline;
    }

    protected Timeline moveXRightFromLeftBorderToRightBorder(Node node) {
        var clip = new Rectangle();
        clip.setHeight(getHeight());
        node.setClip(clip);

        // animated properties
        clip.setWidth(getWidth());
        node.translateXProperty().set(0);

        var timeline = new Timeline();
        timeline.getKeyFrames().add(
            new KeyFrame(
                animationDuration.getValue(),
                new KeyValue(clip.widthProperty(), 0),
                new KeyValue(node.translateXProperty(), getWidth())
            ));

        return timeline;
    }

    protected Timeline moveXRightFromOffCanvasToLeftBorder(Node node) {
        var clip = new Rectangle();
        clip.setHeight(getHeight());
        node.setClip(clip);

        // animated properties
        clip.setWidth(0);
        clip.translateXProperty().set(getWidth());
        node.translateXProperty().set(-getWidth());

        var timeline = new Timeline();

        timeline.getKeyFrames().add(
            new KeyFrame(
                animationDuration.getValue(),
                new KeyValue(clip.widthProperty(), getWidth()),
                new KeyValue(clip.translateXProperty(), 0),
                new KeyValue(node.translateXProperty(), 0)
            ));

        return timeline;
    }

    /**
     * Resets given node to the default state.
     */
    protected void resetNode(Node node) {
        node.setClip(null);
        node.setTranslateX(0);
        node.setTranslateY(0);
    }

    /**
     * Sets the node view order. Only accept predefined values
     * to avoid messing up with magical numbers.
     */
    protected void setViewOrder(Node node, int viewOrder) {
        if (viewOrder == Z_DEFAULT
            || viewOrder == Z_DECK_TOP
            || viewOrder == Z_ANIMATED_IN
            || viewOrder == Z_ANIMATED_OUT) {
            node.setViewOrder(viewOrder);
        } else {
            throw new IllegalArgumentException("Unknown view order value: " + viewOrder);
        }
    }
}
