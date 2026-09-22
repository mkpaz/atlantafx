/**
 * SPDX-License-Identifier: MIT
 *
 * <p>This class is based on AnimateFX library, but reduces it to a single
 * factory class and unlike AnimateFX, it allows for configuration of the
 * most important transition parameters.
 *
 * <p>All credits to Loïc Sculier aka typhon0
 * https://github.com/Typhon0/AnimateFX
 */

package atlantafx.base.util;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A utility class that provides factory methods to create a predefined
 * animations for various effects, such as fade, slide, rotate, scale etc.
 */
public final class Animations {

    /** The default interpolator value that is used across all animations. */
    public static final Interpolator EASE = Interpolator.ofSpline(0.25, 0.1, 0.25, 1);

    //*************************************************************************
    //  SPECIALS                                                             //
    //*************************************************************************

    /**
     * Changes the node opacity to full transparency and then back to its
     * original opacity in quick succession, creating a flashing effect.
     *
     * @param node The node to be animated.
     */
    public static Timeline flash(Node node) {
        Objects.requireNonNull(node, "Node cannot be null!");

        getOrCreateState(node).storeOpacity(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(Duration.millis(250),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(Duration.millis(750),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );
    }

    /**
     * See {@link #pulse(Node, double)}.
     */
    public static Timeline pulse(Node node) {
        return pulse(node, 1.05);
    }

    /**
     * Repeatedly increases and decreases the scale of the node,
     * giving it a pulsating effect that draws attention to it.
     *
     * @param node  The node to be animated.
     * @param scale The scale factor.
     */
    public static Timeline pulse(Node node, double scale) {
        Objects.requireNonNull(node, "Node cannot be null!");

        getOrCreateState(node).storeScaleX(node).storeScaleY(node).storeScaleZ(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.scaleXProperty(), 1, EASE),
                new KeyValue(node.scaleYProperty(), 1, EASE),
                new KeyValue(node.scaleZProperty(), 1, EASE)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(node.scaleXProperty(), scale, EASE),
                new KeyValue(node.scaleYProperty(), scale, EASE),
                new KeyValue(node.scaleZProperty(), scale, EASE)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(node.scaleXProperty(), 1, EASE),
                new KeyValue(node.scaleYProperty(), 1, EASE),
                new KeyValue(node.scaleZProperty(), 1, EASE)
            )
        );
    }

    /**
     * See {@link #shakeX(Node, double)}.
     */
    public static Timeline shakeX(Node node) {
        return shakeX(node, 10);
    }

    /**
     * Rapidly moves the node from side-to-side horizontally,
     * creating a shaking or vibrating effect.
     *
     * @param node   The node to be animated.
     * @param offset The shake offset.
     */
    public static Timeline shakeX(Node node, double offset) {
        Objects.requireNonNull(node, "Node cannot be null!");

        getOrCreateState(node).storeTranslateX(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), 0, EASE)
            ),
            new KeyFrame(Duration.millis(100),
                new KeyValue(node.translateXProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(200),
                new KeyValue(node.translateXProperty(), offset, EASE)
            ),
            new KeyFrame(Duration.millis(300),
                new KeyValue(node.translateXProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(400),
                new KeyValue(node.translateXProperty(), offset, EASE)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(node.translateXProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(600),
                new KeyValue(node.translateXProperty(), offset, EASE)
            ),
            new KeyFrame(Duration.millis(700),
                new KeyValue(node.translateXProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(800),
                new KeyValue(node.translateXProperty(), offset, EASE)
            ),
            new KeyFrame(Duration.millis(900),
                new KeyValue(node.translateXProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(node.translateXProperty(), 0, EASE)
            )
        );
    }

    public static Timeline shakeY(Node node) {
        return shakeY(node, 10);
    }

    /**
     * Rapidly moves the node up and down vertically, creating
     * a shaking or bouncing effect.
     *
     * @param node   The node to be animated.
     * @param offset The shake offset.
     */
    public static Timeline shakeY(Node node, double offset) {
        Objects.requireNonNull(node, "Node cannot be null!");

        getOrCreateState(node).storeTranslateY(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(Duration.millis(100),
                new KeyValue(node.translateYProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(200),
                new KeyValue(node.translateYProperty(), offset, EASE)
            ),
            new KeyFrame(Duration.millis(300),
                new KeyValue(node.translateYProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(400),
                new KeyValue(node.translateYProperty(), offset, EASE)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(node.translateYProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(600),
                new KeyValue(node.translateYProperty(), offset, EASE)
            ),
            new KeyFrame(Duration.millis(700),
                new KeyValue(node.translateYProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(800),
                new KeyValue(node.translateYProperty(), offset, EASE)
            ),
            new KeyFrame(Duration.millis(900),
                new KeyValue(node.translateYProperty(), -offset, EASE)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(node.translateYProperty(), 0, EASE)
            )
        );
    }

    /**
     * Causes the node to rapidly wobble back and forth,
     * creating a visually engaging effect.
     *
     * @param node The node to be animated.
     */
    public static Timeline wobble(Node node) {
        Objects.requireNonNull(node, "Node cannot be null!");

        getOrCreateState(node).storeTranslateX(node).storeRotate(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), 0, EASE),
                new KeyValue(node.rotateProperty(), -0, EASE)
            ),
            new KeyFrame(Duration.millis(150),
                new KeyValue(node.translateXProperty(), -0.25 * node.getBoundsInParent().getWidth(), EASE),
                new KeyValue(node.rotateProperty(), -5, EASE)
            ),
            new KeyFrame(Duration.millis(300),
                new KeyValue(node.translateXProperty(), 0.2 * node.getBoundsInParent().getWidth(), EASE),
                new KeyValue(node.rotateProperty(), 3, EASE)
            ),
            new KeyFrame(Duration.millis(450),
                new KeyValue(node.translateXProperty(), -0.15 * node.getBoundsInParent().getWidth(), EASE),
                new KeyValue(node.rotateProperty(), -3, EASE)
            ),
            new KeyFrame(Duration.millis(600),
                new KeyValue(node.translateXProperty(), 0.1 * node.getBoundsInParent().getWidth(), EASE),
                new KeyValue(node.rotateProperty(), 2, EASE)
            ),
            new KeyFrame(Duration.millis(750),
                new KeyValue(node.translateXProperty(), -0.05 * node.getBoundsInParent().getWidth(), EASE),
                new KeyValue(node.rotateProperty(), -1, EASE)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(node.translateXProperty(), 0, EASE),
                new KeyValue(node.rotateProperty(), 0, EASE)
            )
        );
    }

    //*************************************************************************
    //  FADE                                                                 //
    //*************************************************************************

    /**
     * Gradually increases the opacity of the node from 0 to 1,
     * making it appear on the scene with a fading-in effect.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeIn(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );
    }

    /**
     * Gradually decreases the opacity of the node from 1 to 0,
     * making it disappear from the scene with a fading-out effect.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeOut(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );
    }

    /**
     * Combines the {@link #fadeIn(Node, Duration)} effect with the node’s downward
     * movement, creating an animated entrance of the node from the top.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeInDown(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateY(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight(), EASE)
            ),

            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateYProperty(), 0, EASE)
            )
        );
    }

    /**
     * Combines the {@link #fadeOut(Node, Duration)} effect with the node’s downward
     * movement, creating an animated exit of the node to the bottom.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeOutDown(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateY(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight(), EASE)
            )
        );
    }

    /**
     * Combines the {@link #fadeIn(Node, Duration)} effect with the node’s leftward
     * movement, creating an animated entrance of the node from the left.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeInLeft(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateX(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE)
            )
        );
    }

    /**
     * Combines the {@link #fadeOut(Node, Duration)} effect with the node’s leftward
     * movement, creating an animated exit of the node to the left.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeOutLeft(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateX(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth(), EASE)
            )
        );
    }

    /**
     * Combines the {@link #fadeIn(Node, Duration)} effect with the node’s rightward
     * movement, creating an animated entrance of the node from the right.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeInRight(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateX(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE)
            )
        );
    }

    /**
     * Combines the {@link #fadeOut(Node, Duration)} effect with the node’s rightward
     * movement, creating an animated exit of the node to the right.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeOutRight(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateX(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth(), EASE)
            )
        );
    }

    /**
     * Combines the {@link #fadeIn(Node, Duration)} effect with the node’s upward
     * movement, creating an animated entrance of the node from the bottom.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeInUp(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateY(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateYProperty(), 0, EASE)
            )
        );
    }

    /**
     * Combines the {@link #fadeOut(Node, Duration)} effect with the node’s upward
     * movement, creating an animated exit of the node to the top.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline fadeOutUp(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateY(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight(), EASE)
            )
        );
    }

    //*************************************************************************
    //  ROLL                                                                 //
    //*************************************************************************

    /**
     * Applies an animated effect to the node causing it to roll into
     * the scene from the left side at an angle.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rollIn(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateX(node).storeRotate(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), -node.getBoundsInLocal().getWidth(), EASE),
                new KeyValue(node.rotateProperty(), -120, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE),
                new KeyValue(node.rotateProperty(), 0, EASE)
            )
        );
    }

    /**
     * Applies an animated effect to the node causing it to roll out
     * from the scene to the right side at an angle.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rollOut(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeTranslateX(node).storeRotate(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE),
                new KeyValue(node.rotateProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), node.getBoundsInLocal().getWidth(), EASE),
                new KeyValue(node.rotateProperty(), 120, EASE)
            )
        );
    }

    //*************************************************************************
    //  ROTATE                                                               //
    //*************************************************************************

    /**
     * Rotates the node and gradually increases its opacity,
     * giving it an animated entrance effect.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateIn(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeRotate(node).storeRotationAxis(node);

        node.setRotationAxis(Rotate.Z_AXIS);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.rotateProperty(), -200, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.rotateProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );
    }

    /**
     * Rotates the node and gradually decreases its opacity,
     * giving it an animated exit effect.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateOut(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeOpacity(node).storeRotate(node).storeRotationAxis(node);

        node.setRotationAxis(Rotate.Z_AXIS);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.rotateProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.rotateProperty(), 200, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );
    }

    /**
     * Combines the {@link #rotateIn(Node, Duration)} effect with the node’s downward
     * movement from the left, creating an animated entrance of the node from the top
     * left corner.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateInDownLeft(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        final var rotate = new Rotate(0, 0, node.getBoundsInLocal().getHeight());

        getOrCreateState(node)
            .storeOpacity(node)
            .storeRotate(node)
            .storeRotationAxis(node)
            .storeCustomTransform(node, rotate);

        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), -45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );
    }

    /**
     * Combines the {@link #rotateOut(Node, Duration)} effect with the node’s downward
     * movement to the left, creating an animated exit of the node towards the bottom
     * left corner.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateOutDownLeft(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        final var rotate = new Rotate(0, 0, node.getBoundsInLocal().getHeight());

        getOrCreateState(node)
            .storeOpacity(node)
            .storeRotate(node)
            .storeRotationAxis(node)
            .storeCustomTransform(node, rotate);

        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );
    }

    /**
     * Combines the {@link #rotateIn(Node, Duration)} effect with the node’s downward
     * movement from the right, creating an animated entrance of the node from the top
     * right corner.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateInDownRight(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        final var rotate = new Rotate(0,
            node.getBoundsInLocal().getWidth(),
            node.getBoundsInLocal().getHeight()
        );

        getOrCreateState(node)
            .storeOpacity(node)
            .storeRotate(node)
            .storeRotationAxis(node)
            .storeCustomTransform(node, rotate);

        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );
    }

    /**
     * Combines the {@link #rotateOut(Node, Duration)} effect with the node’s downward
     * movement to the right, creating an animated exit of the node towards the bottom
     * right corner.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateOutDownRight(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        final var rotate = new Rotate(0,
            node.getBoundsInLocal().getWidth(),
            node.getBoundsInLocal().getHeight()
        );

        getOrCreateState(node)
            .storeOpacity(node)
            .storeRotate(node)
            .storeRotationAxis(node)
            .storeCustomTransform(node, rotate);

        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), -45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );
    }

    /**
     * Combines the {@link #rotateIn(Node, Duration)} effect with the node’s upward
     * movement from the left, creating an animated entrance of the node from the
     * bottom left corner.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateInUpLeft(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        final var rotate = new Rotate(0, 0, node.getBoundsInLocal().getHeight());

        getOrCreateState(node)
            .storeOpacity(node)
            .storeRotate(node)
            .storeRotationAxis(node)
            .storeCustomTransform(node, rotate);

        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );
    }

    /**
     * Combines the {@link #rotateOut(Node, Duration)} effect with the node’s upward
     * movement to the left, creating an animated exit of the node towards the top
     * left corner.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateOutUpLeft(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        final var rotate = new Rotate(0, 0, node.getBoundsInLocal().getHeight());

        getOrCreateState(node)
            .storeOpacity(node)
            .storeRotate(node)
            .storeRotationAxis(node)
            .storeCustomTransform(node, rotate);

        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), -45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );
    }

    /**
     * Combines the {@link #rotateIn(Node, Duration)} effect with the node’s upward
     * movement from the right, creating an animated entrance of the node from the
     * bottom right corner.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateInUpRight(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        final var rotate = new Rotate(0,
            node.getBoundsInLocal().getWidth(),
            node.getBoundsInLocal().getHeight()
        );

        getOrCreateState(node)
            .storeOpacity(node)
            .storeRotate(node)
            .storeRotationAxis(node)
            .storeCustomTransform(node, rotate);

        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), -45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );
    }

    /**
     * Combines the {@link #rotateOut(Node, Duration)} effect with the node’s upward
     * movement to the right, creating an animated exit of the node towards the top
     * right corner.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline rotateOutUpRight(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        final var rotate = new Rotate(0,
            node.getBoundsInLocal().getWidth(),
            node.getBoundsInLocal().getHeight()
        );

        getOrCreateState(node)
            .storeOpacity(node)
            .storeRotate(node)
            .storeRotationAxis(node)
            .storeCustomTransform(node, rotate);

        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );
    }

    //*************************************************************************
    //  SLIDE                                                                //
    //*************************************************************************

    /**
     * Applies an animated effect to the node, causing it to slide into view
     * from the top side.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline slideInDown(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeTranslateY(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), 0, EASE)
            )
        );
    }

    /**
     * Applies an animated effect to the node, causing it to slide out of view
     * through the bottom side.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline slideOutDown(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeTranslateY(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight(), EASE)
            )
        );
    }

    /**
     * Applies an animated effect to the node, causing it to slide into view
     * from the left side.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline slideInLeft(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeTranslateX(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateXProperty(), 0, EASE)
            )
        );
    }

    /**
     * Applies an animated effect to the node, causing it to slide out of view
     * through the left side.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline slideOutLeft(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeTranslateX(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth(), EASE)
            )
        );
    }

    /**
     * Applies an animated effect to the node, causing it to slide into view
     * from the right side.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline slideInRight(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeTranslateX(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateXProperty(), 0, EASE)
            )
        );
    }

    /**
     * Applies an animated effect to the node, causing it to slide out of view
     * through the right side.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline slideOutRight(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeTranslateX(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth(), EASE)
            )
        );
    }

    /**
     * Applies an animated effect to the node, causing it to slide into view
     * from the bottom side.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline slideInUp(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeTranslateY(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), 0, EASE)
            )
        );
    }

    /**
     * Applies an animated effect to the node, causing it to slide out of view
     * through the top side.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     */
    public static Timeline slideOutUp(Node node, Duration duration) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeTranslateY(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight(), EASE)
            )
        );
    }

    //*************************************************************************
    //  ZOOM                                                                 //
    //*************************************************************************

    /**
     * See {@link #zoomIn(Node, Duration, double)}.
     */
    public static Timeline zoomIn(Node node, Duration duration) {
        return zoomIn(node, duration, 0.3);
    }

    /**
     * Increases the scale of the node, starting from a smaller size and gradually
     * zooming it to the regular size, emphasizing the node’s entrance.
     *
     * @param node       The node to be animated.
     * @param duration   The animation duration.
     * @param startValue The initial zoom value.
     */
    public static Timeline zoomIn(Node node, Duration duration, double startValue) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeScaleX(node).storeScaleY(node).storeScaleZ(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.scaleXProperty(), startValue, EASE),
                new KeyValue(node.scaleYProperty(), startValue, EASE),
                new KeyValue(node.scaleZProperty(), startValue, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.scaleXProperty(), 1, EASE),
                new KeyValue(node.scaleYProperty(), 1, EASE),
                new KeyValue(node.scaleZProperty(), 1, EASE)
            )
        );
    }

    /**
     * See {@link #zoomOut(Node, Duration, double)}.
     */
    public static Timeline zoomOut(Node node, Duration duration) {
        return zoomOut(node, duration, 0.3);
    }

    /**
     * Reduces the scale of the node, creating a shrinking effect that starts from
     * its original size and gradually zooms out to a smaller size, emphasizing
     * the node’s exit.
     *
     * @param node     The node to be animated.
     * @param duration The animation duration.
     * @param endValue The target zoom value.
     */
    public static Timeline zoomOut(Node node, Duration duration, double endValue) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Objects.requireNonNull(duration, "Duration cannot be null!");

        getOrCreateState(node).storeScaleX(node).storeScaleY(node).storeScaleZ(node);

        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.scaleXProperty(), 1, EASE),
                new KeyValue(node.scaleYProperty(), 1, EASE),
                new KeyValue(node.scaleZProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.scaleXProperty(), endValue, EASE),
                new KeyValue(node.scaleYProperty(), endValue, EASE),
                new KeyValue(node.scaleZProperty(), endValue, EASE)
            )
        );
    }

    //region RESET

    /**
     * Key used to store the {@link ResetState} instance in the node's properties map.
     */
    public static final String RESET_KEY = "ANIMATION_RESET_STATE";

    /**
     * A container storing the original state of a {@link Node} prior to animation.
     */
    public static final class ResetState {

        // @formatter:off

        // bitmask flags
        private static final short MASK_OPACITY     = 1;
        private static final short MASK_TRANSLATE_X = 1 << 1;
        private static final short MASK_TRANSLATE_Y = 1 << 2;
        private static final short MASK_TRANSLATE_Z = 1 << 3;
        private static final short MASK_SCALE_X     = 1 << 4;
        private static final short MASK_SCALE_Y     = 1 << 5;
        private static final short MASK_SCALE_Z     = 1 << 6;
        private static final short MASK_ROTATE      = 1 << 7;
        private static final short MASK_ROT_AXIS    = 1 << 8;
        private static final short MASK_TRANSFORM   = 1 << 9;

        // default node values
        private static final double DEFAULT_OPACITY = 1.0;
        private static final double DEFAULT_ZERO    = 0.0;
        private static final double DEFAULT_SCALE   = 1.0;

        private short mask = 0;
        private double opacity    = DEFAULT_OPACITY;
        private double translateX = DEFAULT_ZERO;
        private double translateY = DEFAULT_ZERO;
        private double translateZ = DEFAULT_ZERO;
        private double scaleX     = DEFAULT_SCALE;
        private double scaleY     = DEFAULT_SCALE;
        private double scaleZ     = DEFAULT_SCALE;
        private double rotate     = DEFAULT_ZERO;

        // @formatter:on

        private Point3D rotationAxis = Rotate.Z_AXIS;
        private @Nullable Transform customTransform = null;

        /**
         * Stores the current opacity of the specified node.
         *
         * @param node the target node
         */
        public ResetState storeOpacity(Node node) {
            double val = node.getOpacity();
            if (Double.compare(val, DEFAULT_OPACITY) != 0) {
                this.opacity = val;
                this.mask |= MASK_OPACITY;
            } else {
                this.mask &= ~MASK_OPACITY;
            }
            return this;
        }

        /**
         * Stores the current X translation of the specified node.
         *
         * @param node the target node
         */
        public ResetState storeTranslateX(Node node) {
            double val = node.getTranslateX();
            if (Double.compare(val, DEFAULT_ZERO) != 0) {
                this.translateX = val;
                this.mask |= MASK_TRANSLATE_X;
            } else {
                this.mask &= ~MASK_TRANSLATE_X;
            }
            return this;
        }

        /**
         * Stores the current Y translation of the specified node.
         *
         * @param node the target node
         */
        public ResetState storeTranslateY(Node node) {
            double val = node.getTranslateY();
            if (Double.compare(val, DEFAULT_ZERO) != 0) {
                this.translateY = val;
                this.mask |= MASK_TRANSLATE_Y;
            } else {
                this.mask &= ~MASK_TRANSLATE_Y;
            }
            return this;
        }

        /**
         * Stores the current Z translation of the specified node.
         *
         * @param node the target node
         */
        public ResetState storeTranslateZ(Node node) {
            double val = node.getTranslateZ();
            if (Double.compare(val, DEFAULT_ZERO) != 0) {
                this.translateZ = val;
                this.mask |= MASK_TRANSLATE_Z;
            } else {
                this.mask &= ~MASK_TRANSLATE_Z;
            }
            return this;
        }

        /**
         * Stores the current X scale factor of the specified node.
         *
         * @param node the target node
         */
        public ResetState storeScaleX(Node node) {
            double val = node.getScaleX();
            if (Double.compare(val, DEFAULT_SCALE) != 0) {
                this.scaleX = val;
                this.mask |= MASK_SCALE_X;
            } else {
                this.mask &= ~MASK_SCALE_X;
            }
            return this;
        }

        /**
         * Stores the current Y scale factor of the specified node.
         *
         * @param node the target node
         */
        public ResetState storeScaleY(Node node) {
            double val = node.getScaleY();
            if (Double.compare(val, DEFAULT_SCALE) != 0) {
                this.scaleY = val;
                this.mask |= MASK_SCALE_Y;
            } else {
                this.mask &= ~MASK_SCALE_Y;
            }
            return this;
        }

        /**
         * Stores the current Z scale factor of the specified node.
         *
         * @param node the target node
         */
        public ResetState storeScaleZ(Node node) {
            double val = node.getScaleZ();
            if (Double.compare(val, DEFAULT_SCALE) != 0) {
                this.scaleZ = val;
                this.mask |= MASK_SCALE_Z;
            } else {
                this.mask &= ~MASK_SCALE_Z;
            }
            return this;
        }

        /**
         * Stores the current rotation angle of the specified node.
         *
         * @param node the target node
         */
        public ResetState storeRotate(Node node) {
            double val = node.getRotate();
            if (Double.compare(val, DEFAULT_ZERO) != 0) {
                this.rotate = val;
                this.mask |= MASK_ROTATE;
            } else {
                this.mask &= ~MASK_ROTATE;
            }
            return this;
        }

        /**
         * Stores the current rotation axis of the specified node.
         *
         * @param node the target node
         */
        public ResetState storeRotationAxis(Node node) {
            this.rotationAxis = node.getRotationAxis();
            this.mask |= MASK_ROT_AXIS;
            return this;
        }

        /**
         * Registers a custom transform and removes any previously registered custom transform.
         *
         * @param node      the target node
         * @param transform the custom transform to register
         */
        public ResetState storeCustomTransform(Node node, Transform transform) {
            // remove previous custom transform to prevent accumulation in node.getTransforms()
            if (this.customTransform != null) {
                node.getTransforms().remove(this.customTransform);
            }
            this.customTransform = transform;
            this.mask |= MASK_TRANSFORM;
            return this;
        }

        /**
         * Restores all stored properties of the specified node to their saved states or default values.
         *
         * @param node the target node to restore
         */
        public void restore(Node node) {
            // remove dynamically added custom transforms
            if ((mask & MASK_TRANSFORM) != 0 && customTransform != null) {
                node.getTransforms().remove(customTransform);
                customTransform = null;
            }

            // restore node properties
            node.setOpacity((mask & MASK_OPACITY) != 0 ? opacity : DEFAULT_OPACITY);
            node.setTranslateX((mask & MASK_TRANSLATE_X) != 0 ? translateX : DEFAULT_ZERO);
            node.setTranslateY((mask & MASK_TRANSLATE_Y) != 0 ? translateY : DEFAULT_ZERO);
            node.setTranslateZ((mask & MASK_TRANSLATE_Z) != 0 ? translateZ : DEFAULT_ZERO);
            node.setScaleX((mask & MASK_SCALE_X) != 0 ? scaleX : DEFAULT_SCALE);
            node.setScaleY((mask & MASK_SCALE_Y) != 0 ? scaleY : DEFAULT_SCALE);
            node.setScaleZ((mask & MASK_SCALE_Z) != 0 ? scaleZ : DEFAULT_SCALE);
            node.setRotate((mask & MASK_ROTATE) != 0 ? rotate : DEFAULT_ZERO);

            if ((mask & MASK_ROT_AXIS) != 0) {
                node.setRotationAxis(rotationAxis);
            } else {
                node.setRotationAxis(Rotate.Z_AXIS);
            }

            // clear flags after restoration
            this.mask = 0;
        }
    }

    /**
     * Retrieves the existing {@link ResetState} associated with the node,
     * or creates and attaches a new one if it does not already exist.
     *
     * @param node the target node
     * @return the {@link ResetState} instance for the node
     */
    public static ResetState getOrCreateState(Node node) {
        var properties = node.getProperties();

        ResetState state = (ResetState) properties.get(RESET_KEY);
        if (state == null) {
            state = new ResetState();
            properties.put(RESET_KEY, state);
        }

        return state;
    }

    /**
     * Resets the target node back to its original state prior to any animation calls.
     *
     * @param node the node to reset
     */
    public static void reset(Node node) {
        Objects.requireNonNull(node, "Node cannot be null!");
        Object o = node.getProperties().remove(RESET_KEY);
        if (o instanceof ResetState state) {
            state.restore(node);
        }
    }
    //endregion
}
