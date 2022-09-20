/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;

public class SliderPage extends AbstractPage {

    public static final String NAME = "Slider";
    private static final int SLIDER_SIZE = 200;
    private static final int SPACING = 40;

    @Override
    public String getName() { return NAME; }

    public SliderPage() {
        super();
        setUserContent(new FlowPane(
                Page.PAGE_HGAP, Page.PAGE_VGAP,
                horizontalSample(),
                verticalSample(),
                disabledSample()
        ));
    }

    private SampleBlock horizontalSample() {
        var slider = new Slider(1, 5, 3);
        slider.setOrientation(HORIZONTAL);

        var tickSlider = createTickSlider();
        tickSlider.setMinWidth(SLIDER_SIZE);
        tickSlider.setMaxWidth(SLIDER_SIZE);

        return new SampleBlock("Horizontal", new VBox(SPACING, slider, tickSlider));
    }

    private Pane verticalSample() {
        var slider = new Slider(1, 5, 3);
        slider.setOrientation(VERTICAL);

        var tickSlider = createTickSlider();
        tickSlider.setOrientation(VERTICAL);
        tickSlider.setMinHeight(SLIDER_SIZE);
        tickSlider.setMaxHeight(SLIDER_SIZE);

        return new SampleBlock("Vertical", new HBox(SPACING, slider, tickSlider));
    }

    private Pane disabledSample() {
        var disabledSlider = createTickSlider();
        disabledSlider.setDisable(true);
        return new SampleBlock("Disabled", new HBox(disabledSlider));
    }

    private Slider createTickSlider() {
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
