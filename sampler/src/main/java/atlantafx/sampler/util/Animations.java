/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.util;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public final class Animations {

    public static final Interpolator EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);

    public static Timeline fadeIn(Node node, Duration duration) {
        return new Timeline(
            new KeyFrame(Duration.millis(0), new KeyValue(node.opacityProperty(), 0, EASE)),
            new KeyFrame(duration, new KeyValue(node.opacityProperty(), 1, EASE))
        );
    }

    public static Timeline fadeOut(Node node, Duration duration) {
        return new Timeline(
            new KeyFrame(Duration.millis(0), new KeyValue(node.opacityProperty(), 1, EASE)),
            new KeyFrame(duration, new KeyValue(node.opacityProperty(), 0, EASE))
        );
    }

    public static Timeline zoomIn(Node node, Duration duration) {
        return new Timeline(
            new KeyFrame(Duration.millis(0),
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.scaleXProperty(), 0.3, EASE),
                new KeyValue(node.scaleYProperty(), 0.3, EASE),
                new KeyValue(node.scaleZProperty(), 0.3, EASE)
            ),
            new KeyFrame(duration,
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.scaleXProperty(), 1, EASE),
                new KeyValue(node.scaleYProperty(), 1, EASE),
                new KeyValue(node.scaleZProperty(), 1, EASE)
            )
        );
    }

    public static Timeline zoomOut(Node node, Duration duration) {
        return new Timeline(
            new KeyFrame(Duration.millis(0),
                new KeyValue(node.opacityProperty(), 1, EASE),
                new KeyValue(node.scaleXProperty(), 1, EASE),
                new KeyValue(node.scaleYProperty(), 1, EASE),
                new KeyValue(node.scaleZProperty(), 0.3, EASE)
            ),
            new KeyFrame(duration.divide(2),
                new KeyValue(node.opacityProperty(), 0, EASE),
                new KeyValue(node.scaleXProperty(), 0.3, EASE),
                new KeyValue(node.scaleYProperty(), 0.3, EASE),
                new KeyValue(node.scaleZProperty(), 0.3, EASE)
            ),
            new KeyFrame(duration, new KeyValue(node.opacityProperty(), 0, EASE))
        );
    }
}
