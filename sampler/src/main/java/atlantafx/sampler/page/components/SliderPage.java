/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static javafx.geometry.Orientation.VERTICAL;

import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class SliderPage extends AbstractPage {

    public static final String NAME = "Slider";
    private static final int SLIDER_SIZE = 180;
    private static final int SPACING = 20;

    @Override
    public String getName() {
        return NAME;
    }

    public SliderPage() {
        super();
        setUserContent(new FlowPane(
            Page.PAGE_HGAP, Page.PAGE_VGAP,
            basicSample(),
            smallSample(),
            largeSample(),
            disabledSample()
        ));
    }

    private SampleBlock basicSample() {
        var hSlider = new Slider(1, 5, 3);

        var hTickSlider = createTickSlider();
        hTickSlider.setSkin(new ProgressSliderSkin(hTickSlider));

        var vSlider = new Slider(1, 5, 3);
        vSlider.setOrientation(VERTICAL);

        var vTickSlider = createTickSlider();
        vTickSlider.setOrientation(VERTICAL);
        vTickSlider.setSkin(new ProgressSliderSkin(vTickSlider));

        return new SampleBlock("Basic", createContent(hSlider, hTickSlider, vSlider, vTickSlider));
    }

    private Pane smallSample() {
        var hSlider = new Slider(1, 5, 3);
        hSlider.getStyleClass().add(Styles.SMALL);

        var hTickSlider = createTickSlider();
        hTickSlider.getStyleClass().add(Styles.SMALL);
        hTickSlider.setSkin(new ProgressSliderSkin(hTickSlider));

        var vSlider = new Slider(1, 5, 3);
        vSlider.setOrientation(VERTICAL);
        vSlider.getStyleClass().add(Styles.SMALL);

        var vTickSlider = createTickSlider();
        vTickSlider.setOrientation(VERTICAL);
        vTickSlider.getStyleClass().add(Styles.SMALL);
        vTickSlider.setSkin(new ProgressSliderSkin(vTickSlider));

        return new SampleBlock("Small", createContent(hSlider, hTickSlider, vSlider, vTickSlider));
    }

    private Pane largeSample() {
        var hSlider = new Slider(1, 5, 3);
        hSlider.getStyleClass().add(Styles.LARGE);

        var hTickSlider = createTickSlider();
        hTickSlider.getStyleClass().add(Styles.LARGE);
        hTickSlider.setSkin(new ProgressSliderSkin(hTickSlider));

        var vSlider = new Slider(1, 5, 3);
        vSlider.setOrientation(VERTICAL);
        vSlider.getStyleClass().add(Styles.LARGE);

        var vTickSlider = createTickSlider();
        vTickSlider.setOrientation(VERTICAL);
        vTickSlider.getStyleClass().add(Styles.LARGE);
        vTickSlider.setSkin(new ProgressSliderSkin(vTickSlider));

        return new SampleBlock("Large", createContent(hSlider, hTickSlider, vSlider, vTickSlider));
    }

    private Pane disabledSample() {
        var hSlider = new Slider(1, 5, 3);
        hSlider.setDisable(true);

        var hTickSlider = createTickSlider();
        hTickSlider.setSkin(new ProgressSliderSkin(hTickSlider));
        hTickSlider.setDisable(true);

        var vSlider = new Slider(1, 5, 3);
        vSlider.setOrientation(VERTICAL);
        vSlider.setDisable(true);

        var vTickSlider = createTickSlider();
        vTickSlider.setOrientation(VERTICAL);
        vTickSlider.setSkin(new ProgressSliderSkin(vTickSlider));
        vTickSlider.setDisable(true);

        return new SampleBlock("Disabled", createContent(hSlider, hTickSlider, vSlider, vTickSlider));
    }

    private Slider createTickSlider() {
        var slider = new Slider(1, 5, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setBlockIncrement(1);
        slider.setMinorTickCount(5);
        slider.setSnapToTicks(true);
        return slider;
    }

    private GridPane createContent(Slider h1, Slider h2, Slider v1, Slider v2) {
        var grid = new GridPane();
        grid.setVgap(SPACING);
        grid.setHgap(SPACING);

        h1.setPrefWidth(SLIDER_SIZE);
        h2.setPrefWidth(SLIDER_SIZE);

        v1.setPrefHeight(SLIDER_SIZE);
        v2.setPrefHeight(SLIDER_SIZE);

        grid.add(h1, 0, 0);
        grid.add(h2, 0, 1);

        grid.add(v1, 1, 0, 1, GridPane.REMAINING);
        grid.add(v2, 2, 0, 1, GridPane.REMAINING);

        return grid;
    }
}
