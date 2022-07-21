/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;

public class SliderPage extends AbstractPage {

    public static final String NAME = "Slider";

    @Override
    public String getName() { return NAME; }

    public SliderPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(row1(), row2());
    }

    private Pane row1() {
        var slider = new Slider(1, 5, 3);
        slider.setOrientation(HORIZONTAL);

        var tickSlider = tickSlider();
        tickSlider.setMinWidth(200);
        tickSlider.setMaxWidth(200);

        var hBlock = new SampleBlock("Horizontal", new HBox(20, slider, tickSlider));

        return new HBox(20, hBlock.getRoot());
    }

    private Pane row2() {
        var slider = new Slider(1, 5, 3);
        slider.setOrientation(VERTICAL);

        var tickSlider = tickSlider();
        tickSlider.setOrientation(VERTICAL);
        tickSlider.setMinHeight(200);
        tickSlider.setMaxHeight(200);

        var vBlock = new SampleBlock("Vertical", new HBox(20, slider, tickSlider));

        var disabledSlider = tickSlider();
        disabledSlider.setDisable(true);

        var disabledBlock = new SampleBlock("Disabled", new HBox(20, disabledSlider));

        return new HBox(40, vBlock.getRoot(), disabledBlock.getRoot());
    }

    private Slider tickSlider() {
        var slider = new Slider(0, 5, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setBlockIncrement(1);
        slider.setMinorTickCount(5);
        slider.setSnapToTicks(true);

        return slider;
    }
}
