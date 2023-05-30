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

import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * A utility class that provides factory methods to create a predefined
 * animations for various effects, such as fade, slide, rotate, scale etc.
 */
public final class Animations {

    /** The default interpolator value that is used across all animations. */
    public static final Interpolator EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);

    ///////////////////////////////////////////////////////////////////////////
    //  SPECIALS                                                             //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Changes the node opacity to full transparency and then back to its
     * original opacity in quick succession, creating a flashing effect.
     *
     * @param node The node to be animated.
     */
    public static Timeline flash(Node node) {
        Objects.requireNonNull(node, "Node cannot be null!");

        var t = new Timeline(
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

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
            }
        });

        return t;
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

        var t = new Timeline(
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

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setScaleX(1);
                node.setScaleY(1);
                node.setScaleZ(1);
            }
        });

        return t;
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

        var t = new Timeline(
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

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateX(0);
            }
        });

        return t;
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

        var t = new Timeline(
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

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateY(0);
            }
        });

        return t;
    }

    /**
     * Causes the node to rapidly wobble back and forth,
     * creating a visually engaging effect.
     *
     * @param node The node to be animated.
     */
    public static Timeline wobble(Node node) {
        Objects.requireNonNull(node, "Node cannot be null!");

        var t = new Timeline(
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

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateX(0);
                node.setRotate(0);
            }
        });

        return t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //  FADE                                                                 //
    ///////////////////////////////////////////////////////////////////////////

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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight(), EASE)
            ),

            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateYProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateY(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateY(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateX(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateX(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateX(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateXProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateX(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateYProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateY(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateY(0);
            }
        });

        return t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //  ROLL                                                                 //
    ///////////////////////////////////////////////////////////////////////////

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

        var t = new Timeline(
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

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateX(0);
                node.setRotate(0);
            }
        });

        return t;
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

        var t = new Timeline(
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

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setTranslateX(0);
                node.setRotate(0);
            }
        });

        return t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //  ROTATE                                                               //
    ///////////////////////////////////////////////////////////////////////////

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

        node.setRotationAxis(Rotate.Z_AXIS);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.rotateProperty(), -200, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.rotateProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setRotate(0);
                node.setOpacity(1);
            }
        });

        return t;
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

        node.setRotationAxis(Rotate.Z_AXIS);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.rotateProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.rotateProperty(), 200, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                node.setRotate(0);
            }
        });

        return t;
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
        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), -45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                rotate.setAngle(0);
            }
        });

        return t;
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
        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                rotate.setAngle(0);
            }
        });

        return t;
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
        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                rotate.setAngle(0);
            }
        });

        return t;
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
        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), -45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                rotate.setAngle(0);
            }
        });

        return t;
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
        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                rotate.setAngle(0);
            }
        });

        return t;
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
        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), -45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                rotate.setAngle(0);
            }
        });

        return t;
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
        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), -45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                rotate.setAngle(0);
            }
        });

        return t;
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
        node.setRotationAxis(Rotate.Z_AXIS);
        node.getTransforms().add(rotate);

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rotate.angleProperty(), 0, EASE),
                new KeyValue(node.opacityProperty(), 1, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(rotate.angleProperty(), 45, EASE),
                new KeyValue(node.opacityProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setOpacity(1);
                rotate.setAngle(0);
            }
        });

        return t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //  SLIDE                                                                //
    ///////////////////////////////////////////////////////////////////////////

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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateY(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateY(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateXProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateX(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateX(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateXProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateX(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateX(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight(), EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), 0, EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateY(0);
            }
        });

        return t;
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

        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateY(0);
            }
        });

        return t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //  ZOOM                                                                 //
    ///////////////////////////////////////////////////////////////////////////

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

        var t = new Timeline(
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

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setScaleX(1);
                node.setScaleY(1);
                node.setScaleZ(1);
            }
        });

        return t;
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

        var t = new Timeline(
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

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setScaleX(1);
                node.setScaleY(1);
                node.setScaleZ(1);
            }
        });

        return t;
    }
}
