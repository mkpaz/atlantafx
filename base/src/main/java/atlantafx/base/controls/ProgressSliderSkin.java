/* SPDX-License-Identifier: MIT */
package atlantafx.base.controls;

import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.control.skin.SliderSkin;
import javafx.scene.layout.StackPane;

/** {@link Slider} skin that supports progress color. */
public class ProgressSliderSkin extends SliderSkin {

    protected final StackPane thumb;
    protected final StackPane track;
    protected final StackPane progressTrack;

    public ProgressSliderSkin(Slider slider) {
        super(slider);

        track = (StackPane) getSkinnable().lookup(".track");
        thumb = (StackPane) getSkinnable().lookup(".thumb");

        progressTrack = new StackPane();
        progressTrack.getStyleClass().add("progress");
        progressTrack.setMouseTransparent(true);

        getSkinnable().getStyleClass().add("progress-slider");
        getChildren().add(getChildren().indexOf(thumb), progressTrack);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        double progressX, progressY, progressWidth, progressHeight;

        // intentionally ignore background radius in calculation,
        // because slider looks better this way
        if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
            progressX = track.getLayoutX();
            progressY = track.getLayoutY();
            progressWidth = thumb.getLayoutX() - snappedLeftInset();
            progressHeight = track.getHeight();
        } else {
            progressX = track.getLayoutX();
            progressY = thumb.getLayoutY();
            progressWidth = track.getWidth();
            progressHeight = track.getLayoutBounds().getMaxY() + track.getLayoutY() - thumb.getLayoutY() - snappedBottomInset();
        }

        progressTrack.resizeRelocate(progressX, progressY, progressWidth, progressHeight);
    }
}
