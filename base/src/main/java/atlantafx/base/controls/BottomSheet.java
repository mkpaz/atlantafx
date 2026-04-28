/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.util.Animations;
import java.util.Objects;
import java.util.function.Function;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

/**
 * A container that slides up from the bottom of its parent, overlaying the
 * existing content. It follows the same {@code display} + {@code transitionFactory}
 * pattern as {@link ModalPane}.
 *
 * <p>The sheet is typically placed inside a {@link javafx.scene.layout.StackPane}
 * alongside the main content. When displayed, it animates upward from the bottom.
 * It supports a drag-to-dismiss gesture and can display an optional header with
 * a drag handle.
 *
 * <p>Example:
 *
 * <pre>{@code
 * var bottomSheet = new BottomSheet();
 *
 * var content = new VBox(10, new Label("Sheet content"));
 * bottomSheet.setContent(content);
 *
 * var openBtn = new Button("Open Sheet");
 * openBtn.setOnAction(e -> bottomSheet.show());
 *
 * var root = new StackPane();
 * root.getChildren().addAll(new VBox(openBtn), bottomSheet);
 * }</pre>
 */
public class BottomSheet extends Control {

    /**
     * The default value that is set to the bottom sheet
     * when it must be on top of other nodes.
     */
    public static final int Z_FRONT = -10;

    /**
     * The default value that is set to the bottom sheet
     * when it must be below of other nodes.
     */
    public static final int Z_BACK = 10;

    /**
     * The default animation duration for slide-in.
     */
    public static final Duration DEFAULT_DURATION_IN = Duration.millis(250);

    /**
     * The default animation duration for slide-out.
     */
    public static final Duration DEFAULT_DURATION_OUT = Duration.millis(200);

    /**
     * The default drag threshold (in pixels) before the sheet dismisses.
     */
    public static final double DEFAULT_DISMISS_THRESHOLD = 100;

    private final int topViewOrder;

    public BottomSheet() {
        this(Z_FRONT);
    }

    public BottomSheet(int topViewOrder) {
        super();
        this.topViewOrder = topViewOrder;
        getStyleClass().add("bottom-sheet");
        usePredefinedTransitionFactories(DEFAULT_DURATION_IN, DEFAULT_DURATION_OUT);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BottomSheetSkin(this);
    }

    public int getTopViewOrder() {
        return topViewOrder;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Specifies the content node to display inside the bottom sheet body.
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
     * Specifies an optional header node to display at the top of the sheet.
     * A drag handle will be displayed above the header.
     */
    public ObjectProperty<@Nullable Node> headerProperty() {
        return header;
    }

    protected final ObjectProperty<@Nullable Node> header = new SimpleObjectProperty<>(this, "header", null);

    public @Nullable Node getHeader() {
        return header.get();
    }

    public void setHeader(@Nullable Node node) {
        this.header.set(node);
    }

    /**
     * Indicates whether the bottom sheet is set to be on top or not.
     * When changed, the {@link #viewOrderProperty()} value will be modified accordingly.
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
     * The factory that provides a transition to be played when the sheet appears.
     */
    public ObjectProperty<@Nullable Function<Node, Animation>> inTransitionFactoryProperty() {
        return inTransitionFactory;
    }

    protected final ObjectProperty<@Nullable Function<Node, Animation>> inTransitionFactory = new SimpleObjectProperty<>(
        this, "inTransitionFactory", null
    );

    public @Nullable Function<Node, Animation> getInTransitionFactory() {
        return inTransitionFactory.get();
    }

    public void setInTransitionFactory(@Nullable Function<Node, Animation> factory) {
        this.inTransitionFactory.set(factory);
    }

    /**
     * The factory that provides a transition to be played when the sheet disappears.
     */
    public ObjectProperty<@Nullable Function<Node, Animation>> outTransitionFactoryProperty() {
        return outTransitionFactory;
    }

    protected final ObjectProperty<@Nullable Function<Node, Animation>> outTransitionFactory = new SimpleObjectProperty<>(
        this, "outTransitionFactory", null
    );

    public @Nullable Function<Node, Animation> getOutTransitionFactory() {
        return outTransitionFactory.get();
    }

    public void setOutTransitionFactory(@Nullable Function<Node, Animation> factory) {
        this.outTransitionFactory.set(factory);
    }

    /**
     * Specifies whether the content should be treated as persistent or not.
     * When persistent, ESC key and clicking outside won't dismiss the sheet.
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

    /**
     * The drag distance threshold (in pixels) after which the sheet dismisses.
     */
    public DoubleProperty dismissThresholdProperty() {
        return dismissThreshold;
    }

    protected final DoubleProperty dismissThreshold = new SimpleDoubleProperty(this, "dismissThreshold", DEFAULT_DISMISS_THRESHOLD);

    public double getDismissThreshold() {
        return dismissThreshold.get();
    }

    public void setDismissThreshold(double threshold) {
        this.dismissThreshold.set(threshold);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public API                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sets the content and triggers display.
     */
    public void show(Node node) {
        Objects.requireNonNull(node, "Content cannot be null.");
        setContent(node);
        setDisplay(true);
    }

    /**
     * Triggers display with the current content.
     */
    public void show() {
        setDisplay(true);
    }

    /**
     * Hides the bottom sheet.
     */
    public void hide() {
        setDisplay(false);
    }

    /**
     * Sets predefined slide-in/slide-out transition factories.
     */
    public void usePredefinedTransitionFactories(@Nullable Duration inDuration, @Nullable Duration outDuration) {
        Duration durIn = Objects.requireNonNullElse(inDuration, DEFAULT_DURATION_IN);
        Duration durOut = Objects.requireNonNullElse(outDuration, DEFAULT_DURATION_OUT);
        setInTransitionFactory(node -> Animations.slideInUp(node, durIn));
        setOutTransitionFactory(node -> Animations.slideOutDown(node, durOut));
    }
}
