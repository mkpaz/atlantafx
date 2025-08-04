/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2013, 2022 ControlsFX
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package atlantafx.base.controls;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

import java.util.Objects;

import atlantafx.base.util.NullSafetyHelper;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

/**
 * A control that is intended to provide detailed information about
 * an owning node in a popup window. The popup window has a lightweight
 * appearance (no default window decorations) and an arrow pointing at the owner.
 * Due to the nature of popup windows the Popover will move around with the parent
 * window when the user drags it.
 *
 * <p>The Popover can be detached from the owning node by dragging it away from the
 * owner. It stops displaying an arrow and starts displaying a title and a close
 * icon.
 *
 * <p>Example
 *
 * <pre>{@code
 * var textFlow = new TextFlow(new Text("Some content"));
 * textFlow.setPrefWidth(300);
 *
 * var popover = new Popover(textFlow);
 * popover.setTitle("Title");
 *
 * var ownerLink = new Hyperlink("Show popover");
 * ownerLink.setOnAction(e -> popover.show(ownerLink));
 * }</pre>
 */
public class Popover extends PopupControl {

    private static final String DEFAULT_STYLE_CLASS = "popover";
    private static final Duration DEFAULT_FADE_DURATION = Duration.seconds(.2);

    private final StackPane root = new StackPane();
    private double targetX;
    private double targetY;

    /**
     * Creates a popover with a label as the content node.
     */
    public Popover() {
        super();

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
        setOnHiding(evt -> setDetached(false));

        // create some initial content
        Label label = new Label("No Content");
        label.setPrefSize(200, 200);
        label.setPadding(new Insets(4));
        setContentNode(label);

        InvalidationListener repositionListener = observable -> {
            if (isShowing() && !isDetached()) {
                show(getOwnerNode(), targetX, targetY);
                adjustWindowLocation();
            }
        };

        arrowSize.addListener(repositionListener);
        cornerRadius.addListener(repositionListener);
        arrowLocation.addListener(repositionListener);
        arrowIndent.addListener(repositionListener);
        headerAlwaysVisible.addListener(repositionListener);

        // a detached popover should of course not automatically hide itself
        detached.addListener(it -> setAutoHide(!isDetached()));

        setAutoHide(true);
    }

    /**
     * Creates a popover with the given node as the content node.
     *
     * @param content The content shown by the popover.
     */
    public Popover(Node content) {
        this();
        setContentNode(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new PopoverSkin(this);
    }

    /**
     * The root pane stores the content node of the popover. It is accessible
     * via this method in order to support proper styling.
     *
     * <p>Example:
     *
     * <pre>{@code
     * Popover popOver = new Popover();
     * popOver.getRoot().getStylesheets().add(...);
     * }</pre>
     *
     * @return the root pane
     */
    public final StackPane getRoot() {
        return root;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listeners                                                             //
    /// ////////////////////////////////////////////////////////////////////////

    private final InvalidationListener hideListener = observable -> {
        if (!isDetached()) {
            hide(Duration.ZERO);
        }
    };

    private final WeakInvalidationListener weakHideListener = new WeakInvalidationListener(hideListener);

    private final ChangeListener<Number> xListener = (value, oldX, newX) -> {
        if (!isDetached()) {
            setAnchorX(getAnchorX() + (newX.doubleValue() - oldX.doubleValue()));
        }
    };

    private final WeakChangeListener<Number> weakXListener = new WeakChangeListener<>(xListener);

    private final ChangeListener<Number> yListener = (value, oldY, newY) -> {
        if (!isDetached()) {
            setAnchorY(getAnchorY() + (newY.doubleValue() - oldY.doubleValue()));
        }
    };

    private final WeakChangeListener<Number> weakYListener = new WeakChangeListener<>(yListener);

    private @Nullable Window ownerWindow;

    private final EventHandler<WindowEvent> closePopoverOnOwnerWindowCloseLambda
        = event -> ownerWindowHiding();

    private final WeakEventHandler<WindowEvent> closePopoverOnOwnerWindowClose =
        new WeakEventHandler<>(closePopoverOnOwnerWindowCloseLambda);

    ///////////////////////////////////////////////////////////////////////////
    // API                                                                   //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Shows the popover in a position relative to the edges of the given owner
     * node. The position is dependent on the arrow location. If the arrow is
     * pointing to the right then the popover will be placed to the left of the
     * given owner. If the arrow points up then the popover will be placed
     * below the given owner node. The arrow will slightly overlap with the
     * owner node.
     *
     * @param owner The owner of the popover.
     */
    public final void show(Node owner) {
        show(owner, 4);
    }

    /**
     * Shows the popover in a position relative to the edges of the given owner
     * node. The position is dependent on the arrow location. If the arrow is
     * pointing to the right then the popover will be placed to the left of the
     * given owner. If the arrow points up then the popover will be placed
     * below the given owner node.
     *
     * @param owner  The owner of the popover.
     * @param offset If negative specifies the distance to the owner node or when
     *               positive specifies the number of pixels that the arrow will
     *               overlap with the owner node (positive values are recommended).
     */
    public final void show(Node owner, double offset) {
        Objects.requireNonNull(owner, "Owner node cannot be null!");

        Bounds bounds = owner.localToScreen(owner.getBoundsInLocal());
        if (bounds == null) {
            throw new IllegalStateException(
                "The owner node is not added to the scene. It cannot be used as a popover anchor."
            );
        }

        switch (getArrowLocation()) {
            case BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT -> show(
                owner, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + offset
            );
            case LEFT_BOTTOM, LEFT_CENTER, LEFT_TOP -> show(
                owner, bounds.getMaxX() - offset, bounds.getMinY() + bounds.getHeight() / 2
            );
            case RIGHT_BOTTOM, RIGHT_CENTER, RIGHT_TOP -> show(
                owner, bounds.getMinX() + offset, bounds.getMinY() + bounds.getHeight() / 2
            );
            case TOP_CENTER, TOP_LEFT, TOP_RIGHT -> show(
                owner, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() - offset
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void show(Window owner) {
        super.show(owner);
        ownerWindow = owner;

        if (isAnimated()) {
            showFadeInAnimation(getFadeInDuration());
        }

        ownerWindow.addEventFilter(WindowEvent.WINDOW_HIDING, closePopoverOnOwnerWindowClose);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void show(Window ownerWindow, double anchorX, double anchorY) {
        super.show(ownerWindow, anchorX, anchorY);
        this.ownerWindow = ownerWindow;

        if (isAnimated()) {
            showFadeInAnimation(getFadeInDuration());
        }

        ownerWindow.addEventFilter(WindowEvent.WINDOW_HIDING, closePopoverOnOwnerWindowClose);
    }

    /**
     * Makes the popover visible at the give location and associates it with
     * the given owner node. The x and y coordinate will be the target location
     * of the arrow of the popover and not the location of the window.
     *
     * @param owner The owning node.
     * @param x     The x coordinate for the popover arrow tip.
     * @param y     The y coordinate for the popover arrow tip.
     */
    @Override
    public final void show(Node owner, double x, double y) {
        show(owner, x, y, getFadeInDuration());
    }

    /**
     * Makes the popover visible at the give location and associates it with
     * the given owner node. The x and y coordinate will be the target location
     * of the arrow of the popover and not the location of the window.
     *
     * @param owner          The owning node.
     * @param x              The x coordinate for the popover arrow tip.
     * @param y              The y coordinate for the popover arrow tip.
     * @param fadeInDuration The time it takes for the popover to be fully visible.
     *                       This duration takes precedence over the fade-in property without setting.
     */
    public final void show(Node owner, double x, double y, @Nullable Duration fadeInDuration) {

        // Calling show() a second time without first closing the popover
        // causes it to be placed at the wrong location.
        if (ownerWindow != null && isShowing()) {
            super.hide();
        }

        targetX = x;
        targetY = y;

        NullSafetyHelper.assertNonNull(owner, "Owner Node");

        // this is all needed because children windows do not get their x and y
        // coordinate updated when the owning window gets moved by the user
        if (ownerWindow != null) {
            ownerWindow.xProperty().removeListener(weakXListener);
            ownerWindow.yProperty().removeListener(weakYListener);
            ownerWindow.widthProperty().removeListener(weakHideListener);
            ownerWindow.heightProperty().removeListener(weakHideListener);
        }

        ownerWindow = owner.getScene().getWindow();
        ownerWindow.xProperty().addListener(weakXListener);
        ownerWindow.yProperty().addListener(weakYListener);
        ownerWindow.widthProperty().addListener(weakHideListener);
        ownerWindow.heightProperty().addListener(weakHideListener);

        setOnShown(evt -> {
            // the user clicked somewhere into the transparent background,
            // if this is the case then hide the window (when attached)
            getScene().addEventHandler(MOUSE_CLICKED, mouseEvent -> {
                if (mouseEvent.getTarget().equals(getScene().getRoot()) && !isDetached()) {
                    hide();
                }
            });

            // move the window so that the arrow will end up pointing at the target coordinates
            adjustWindowLocation();

            // FIXME: Popover flickering
            // The reason of flickering is that for calculating popup bounds show() method have to
            // be called PRIOR TO adjusting window position. So, in a very short period we see the
            // window in its initial position. Ideally, we have to call adjustWindowLocation() right
            // after window is added to the scene, but before it's rendered, which is not possible
            // due to JavaFX async nature. The only way seems to start popover as invisible (not opaque)
            // and then restore its visibility to hide window repositioning.
            // Still it's not a 100% guarantee, but better than nothing.
            int delay = Math.min(
                (int) Objects.requireNonNullElse(fadeInDuration, DEFAULT_FADE_DURATION).toMillis() / 2, 250
            );

            var timer = new Timeline(new KeyFrame(Duration.millis(delay)));
            timer.setOnFinished(e -> getSkin().getNode().setVisible(true));
            timer.play();
        });

        super.show(owner, x, y);

        if (isAnimated()) {
            showFadeInAnimation(Objects.requireNonNullElse(fadeInDuration, DEFAULT_FADE_DURATION));
        }

        ownerWindow.addEventFilter(WindowEvent.WINDOW_HIDING, closePopoverOnOwnerWindowClose);
    }

    private void showFadeInAnimation(Duration fadeInDuration) {
        // fade in
        Node skinNode = getSkin().getNode();
        skinNode.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(fadeInDuration, skinNode);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void ownerWindowHiding() {
        hide(Duration.ZERO);
        if (ownerWindow != null) {
            // remove EventFilter to prevent memory leak
            ownerWindow.removeEventFilter(WindowEvent.WINDOW_HIDING, closePopoverOnOwnerWindowClose);
        }
    }

    /**
     * Hides the popover by quickly changing its opacity to 0.
     *
     * @see #hide(Duration)
     */
    @Override
    public final void hide() {
        hide(getFadeOutDuration());
    }

    /**
     * Hides the popover by quickly changing its opacity to 0.
     *
     * @param fadeOutDuration The duration of the fade transition that is being used to
     *                        change the opacity of the popover.
     */
    public final void hide(@Nullable Duration fadeOutDuration) {
        if (fadeOutDuration == null) {
            fadeOutDuration = DEFAULT_FADE_DURATION;
        }

        if (isShowing()) {
            if (isAnimated()) {
                // fade out
                Node skinNode = getSkin().getNode();

                FadeTransition fadeOut = new FadeTransition(fadeOutDuration, skinNode);
                fadeOut.setFromValue(skinNode.getOpacity());
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(evt -> super.hide());
                fadeOut.play();
            } else {
                super.hide();
            }
            getSkin().getNode().setVisible(false);
        }
    }

    private void adjustWindowLocation() {
        Bounds bounds = getSkin().getNode().getBoundsInParent();
        switch (getArrowLocation()) {
            case TOP_CENTER, TOP_LEFT, TOP_RIGHT -> {
                setAnchorX(getAnchorX() + bounds.getMinX() - computeXOffset());
                setAnchorY(getAnchorY() + bounds.getMinY() + getArrowSize());
            }
            case LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM -> {
                setAnchorX(getAnchorX() + bounds.getMinX() + getArrowSize());
                setAnchorY(getAnchorY() + bounds.getMinY() - computeYOffset());
            }
            case BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT -> {
                setAnchorX(getAnchorX() + bounds.getMinX() - computeXOffset());
                setAnchorY(getAnchorY() - bounds.getMinY() - bounds.getMaxY() - 1);
            }
            case RIGHT_TOP, RIGHT_BOTTOM, RIGHT_CENTER -> {
                setAnchorX(getAnchorX() - bounds.getMinX() - bounds.getMaxX() - 1);
                setAnchorY(getAnchorY() + bounds.getMinY() - computeYOffset());
            }
        }
    }

    private double computeXOffset() {
        return switch (getArrowLocation()) {
            case TOP_LEFT, BOTTOM_LEFT -> (
                getCornerRadius() + getArrowIndent() + getArrowSize()
            );
            case TOP_CENTER, BOTTOM_CENTER -> (
                getContentNode().prefWidth(-1) / 2
            );
            case TOP_RIGHT, BOTTOM_RIGHT -> (
                getContentNode().prefWidth(-1) - getArrowIndent() - getCornerRadius() - getArrowSize()
            );
            default -> 0;
        };
    }

    private double computeYOffset() {
        double prefContentHeight = getContentNode().prefHeight(-1);

        return switch (getArrowLocation()) {
            case LEFT_TOP, RIGHT_TOP -> getCornerRadius() + getArrowIndent() + getArrowSize();
            case LEFT_CENTER, RIGHT_CENTER -> Math.max(
                prefContentHeight, 2 * (getCornerRadius() + getArrowIndent() + getArrowSize())
            ) / 2;
            case LEFT_BOTTOM, RIGHT_BOTTOM -> Math.max(
                prefContentHeight - getCornerRadius() - getArrowIndent() - getArrowSize(),
                getCornerRadius() + getArrowIndent() + getArrowSize()
            );
            default -> 0;
        };
    }

    /**
     * Detaches the popover from the owning node. The popover will no longer
     * display an arrow pointing at the owner node.
     */
    public final void detach() {
        if (isDetachable()) {
            setDetached(true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Specifies the content shown by the popover.
     */
    public final ObjectProperty<Node> contentNodeProperty() {
        return contentNode;
    }

    private final ObjectProperty<Node> contentNode = new SimpleObjectProperty<>(this, "contentNode") {
        @Override
        public void setValue(Node node) {
            NullSafetyHelper.assertNonNull(node, "Node Node");
            this.set(node);
        }
    };

    /**
     * Returns the value of the content property.
     *
     * @return the content node.
     * @see #contentNodeProperty()
     */
    public final Node getContentNode() {
        return contentNodeProperty().get();
    }

    /**
     * Sets the value of the content property.
     *
     * @param content The new content node value.
     * @see #contentNodeProperty()
     */
    public final void setContentNode(Node content) {
        contentNodeProperty().set(content);
    }

    /**
     * Determines whether the {@link Popover} header should remain visible  or not,
     * even while attached.
     */
    public final BooleanProperty headerAlwaysVisibleProperty() {
        return headerAlwaysVisible;
    }

    private final BooleanProperty headerAlwaysVisible = new SimpleBooleanProperty(this, "headerAlwaysVisible");

    /**
     * Sets the value of the headerAlwaysVisible property.
     *
     * @param visible If "true", then the header is visible even while attached.
     * @see #headerAlwaysVisibleProperty()
     */
    public final void setHeaderAlwaysVisible(boolean visible) {
        headerAlwaysVisible.setValue(visible);
    }

    /**
     * Returns the value of the detachable property.
     *
     * @return "true" if the header is visible even while attached
     * @see #headerAlwaysVisibleProperty()
     */
    public final boolean isHeaderAlwaysVisible() {
        return headerAlwaysVisible.getValue();
    }

    /**
     * Determines whether the header's close button should be available or not.
     */
    public final BooleanProperty closeButtonEnabledProperty() {
        return closeButtonEnabled;
    }

    private final BooleanProperty closeButtonEnabled = new SimpleBooleanProperty(this, "closeButtonEnabled", true);

    /**
     * Sets the value of the closeButtonEnabled property.
     *
     * @param enabled If "false", the popover will not be closeable by the header's close button.
     * @see #closeButtonEnabledProperty()
     */
    public final void setCloseButtonEnabled(boolean enabled) {
        closeButtonEnabled.setValue(enabled);
    }

    /**
     * Returns the value of the closeButtonEnabled property.
     *
     * @return "true" if the header's close button is enabled
     * @see #closeButtonEnabledProperty()
     */
    public final boolean isCloseButtonEnabled() {
        return closeButtonEnabled.getValue();
    }

    /**
     * Determines if the popover is detachable at all.
     */
    public final BooleanProperty detachableProperty() {
        return detachable;
    }

    private final BooleanProperty detachable = new SimpleBooleanProperty(this, "detachable", true);

    /**
     * Sets the value of the detachable property.
     *
     * @param detachable If "true" then the user can detach / tear off the popover.
     * @see #detachableProperty()
     */
    public final void setDetachable(boolean detachable) {
        detachableProperty().set(detachable);
    }

    /**
     * Returns the value of the detachable property.
     *
     * @return "true" if the user is allowed to detach / tear off the popover
     * @see #detachableProperty()
     */
    public final boolean isDetachable() {
        return detachableProperty().get();
    }

    /**
     * Determines whether the popover is detached from the owning node or not.
     * A detached popover no longer shows an arrow pointing at the owner and
     * features its own title bar.
     */
    public final BooleanProperty detachedProperty() {
        return detached;
    }

    private final BooleanProperty detached = new SimpleBooleanProperty(this, "detached", false);

    /**
     * Sets the value of the detached property.
     *
     * @param detached If "true" the popover will change its appearance to "detached" mode.
     * @see #detachedProperty()
     */
    public final void setDetached(boolean detached) {
        detachedProperty().set(detached);
    }

    /**
     * Returns the value of the detached property.
     *
     * @return "true" if the popover is currently detached
     * @see #detachedProperty()
     */
    public final boolean isDetached() {
        return detachedProperty().get();
    }

    /**
     * Controls the size of the arrow.
     * Default value is "12".
     */
    public final DoubleProperty arrowSizeProperty() {
        return arrowSize;
    }

    private final DoubleProperty arrowSize = new SimpleDoubleProperty(this, "arrowSize", 12);

    /**
     * Returns the value of the arrow size property.
     *
     * @return the arrow size property value
     * @see #arrowSizeProperty()
     */
    public final double getArrowSize() {
        return arrowSizeProperty().get();
    }

    /**
     * Sets the value of the arrow size property.
     *
     * @param size The new value of the arrow size property.
     * @see #arrowSizeProperty()
     */
    public final void setArrowSize(double size) {
        arrowSizeProperty().set(size);
    }

    /**
     * Controls the distance between the arrow and the corners of the popover.
     * Default value is "12".
     */
    public final DoubleProperty arrowIndentProperty() {
        return arrowIndent;
    }

    private final DoubleProperty arrowIndent = new SimpleDoubleProperty(this, "arrowIndent", 12);

    /**
     * Returns the value of the arrow indent property.
     *
     * @return the arrow indent value
     * @see #arrowIndentProperty()
     */
    public final double getArrowIndent() {
        return arrowIndentProperty().get();
    }

    /**
     * Sets the value of the arrow indent property.
     *
     * @param size The arrow indent value.
     * @see #arrowIndentProperty()
     */
    public final void setArrowIndent(double size) {
        arrowIndentProperty().set(size);
    }

    /**
     * Returns the corner radius property for the popover.
     * Default value is "6".
     */
    public final DoubleProperty cornerRadiusProperty() {
        return cornerRadius;
    }

    private final DoubleProperty cornerRadius = new SimpleDoubleProperty(this, "cornerRadius", 6);

    /**
     * Returns the value of the corner radius property.
     *
     * @return the corner radius
     * @see #cornerRadiusProperty()
     */
    public final double getCornerRadius() {
        return cornerRadiusProperty().get();
    }

    /**
     * Sets the value of the corner radius property.
     *
     * @param radius The corner radius.
     * @see #cornerRadiusProperty()
     */
    public final void setCornerRadius(double radius) {
        cornerRadiusProperty().set(radius);
    }

    /**
     * Stores the title to display in the Popover's header.
     */
    public final StringProperty titleProperty() {
        return title;
    }

    private final StringProperty title = new SimpleStringProperty(this, "title", "Info");

    /**
     * Returns the value of the title property.
     *
     * @return the detached title
     * @see #titleProperty()
     */
    public final String getTitle() {
        return titleProperty().get();
    }

    /**
     * Sets the value of the title property.
     *
     * @param title The title to use when detached.
     * @see #titleProperty()
     */
    public final void setTitle(String title) {
        NullSafetyHelper.assertNonNull(title, "Title");
        titleProperty().set(title);
    }

    /**
     * Stores the preferred arrow location. This might not be the actual
     * location of the arrow if auto fix is enabled.
     *
     * @see #setAutoFix(boolean)
     */
    public final ObjectProperty<ArrowLocation> arrowLocationProperty() {
        return arrowLocation;
    }

    private final ObjectProperty<ArrowLocation> arrowLocation =
        new SimpleObjectProperty<>(this, "arrowLocation", ArrowLocation.LEFT_TOP);

    /**
     * Sets the value of the arrow location property.
     *
     * @param location The requested location.
     * @see #arrowLocationProperty()
     */
    public final void setArrowLocation(ArrowLocation location) {
        arrowLocationProperty().set(location);
    }

    /**
     * Returns the value of the arrow location property.
     *
     * @return the preferred arrow location
     * @see #arrowLocationProperty()
     */
    public final ArrowLocation getArrowLocation() {
        return arrowLocationProperty().get();
    }

    /**
     * All possible arrow locations.
     */
    public enum ArrowLocation {
        LEFT_TOP,
        LEFT_CENTER,
        LEFT_BOTTOM,
        RIGHT_TOP,
        RIGHT_CENTER,
        RIGHT_BOTTOM,
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT
    }

    /**
     * Stores the fade-in duration. This should be set before calling <code>Popover.show(..)</code>.
     */
    public final ObjectProperty<Duration> fadeInDurationProperty() {
        return fadeInDuration;
    }

    private final ObjectProperty<Duration> fadeInDuration = new SimpleObjectProperty<>(DEFAULT_FADE_DURATION);

    /**
     * Returns the value of the fade-in duration property.
     *
     * @return the fade-in duration
     * @see #fadeInDurationProperty()
     */
    public final Duration getFadeInDuration() {
        return fadeInDurationProperty().get();
    }

    /**
     * Sets the value of the fade-in duration property. This should be set before calling
     * Popover.show(...).
     *
     * @param duration The requested fade-in duration.
     * @see #fadeInDurationProperty()
     */
    public final void setFadeInDuration(Duration duration) {
        fadeInDurationProperty().setValue(duration);
    }

    /**
     * Stores the fade-out duration.
     */
    public final ObjectProperty<Duration> fadeOutDurationProperty() {
        return fadeOutDuration;
    }

    private final ObjectProperty<Duration> fadeOutDuration = new SimpleObjectProperty<>(DEFAULT_FADE_DURATION);

    /**
     * Returns the value of the fade-out duration property.
     *
     * @return the fade-out duration
     * @see #fadeOutDurationProperty()
     */
    public final Duration getFadeOutDuration() {
        return fadeOutDurationProperty().get();
    }

    /**
     * Sets the value of the fade-out duration property.
     *
     * @param duration The requested fade-out duration.
     * @see #fadeOutDurationProperty()
     */
    public final void setFadeOutDuration(Duration duration) {
        fadeOutDurationProperty().setValue(duration);
    }

    /**
     * Stores the "animated" flag. If true then the Popover will be shown / hidden with a short
     * fade in / out animation.
     */
    public final BooleanProperty animatedProperty() {
        return animated;
    }

    private final SimpleBooleanProperty animated = new SimpleBooleanProperty(true);

    /**
     * Returns the value of the "animated" property.
     *
     * @return "true" if the Popover will be shown and hidden with a short fade animation
     * @see #animatedProperty()
     */
    public final boolean isAnimated() {
        return animatedProperty().get();
    }

    /**
     * Sets the value of the "animated" property.
     *
     * @param animated If "true" the Popover will be shown and hidden with a short fade animation.
     * @see #animatedProperty()
     */
    public final void setAnimated(boolean animated) {
        animatedProperty().set(animated);
    }
}
