package atlantafx.base.util;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animations {

    public static final Interpolator EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);

    ///////////////////////////////////////////////////////////////////////////
    //  FADE                                                                 //
    ///////////////////////////////////////////////////////////////////////////

    public static Timeline fadeIn(Node node, Duration duration) {
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

    public static Timeline fadeOut(Node node, Duration duration) {
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

    ///////////////////////////////////////////////////////////////////////////
    //  ZOOM                                                                 //
    ///////////////////////////////////////////////////////////////////////////

    public static Timeline zoomIn(Node node, Duration duration) {
        return zoomIn(node, duration, 0.3);
    }

    public static Timeline zoomIn(Node node, Duration duration, double startValue) {
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

    public static Timeline zoomOut(Node node, Duration duration) {
        return zoomOut(node, duration, 0.3);
    }

    public static Timeline zoomOut(Node node, Duration duration, double endValue) {
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

    ///////////////////////////////////////////////////////////////////////////
    //  SLIDE                                                                //
    ///////////////////////////////////////////////////////////////////////////

    public static Timeline slideInDown(Node node, Duration duration) {
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

    public static Timeline slideOutDown(Node node, Duration duration) {
        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getWidth(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateY(0);
            }
        });

        return t;
    }

    public static Timeline slideInLeft(Node node, Duration duration) {
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

    public static Timeline slideOutLeft(Node node, Duration duration) {
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

    public static Timeline slideInRight(Node node, Duration duration) {
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

    public static Timeline slideOutRight(Node node, Duration duration) {
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

    public static Timeline slideInUp(Node node, Duration duration) {
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

    public static Timeline slideOutUp(Node node, Duration duration) {
        var t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateYProperty(), 0, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.translateYProperty(), node.getBoundsInParent().getWidth(), EASE)
            )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateY(0);
            }
        });

        return t;
    }
}
