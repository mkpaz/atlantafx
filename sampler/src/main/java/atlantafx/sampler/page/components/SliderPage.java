/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

public final class SliderPage extends OutlinePage {

    public static final String NAME = "Slider";

    @Override
    public String getName() {
        return NAME;
    }

    public SliderPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]Slider[/i] control is used to display a continuous or discrete range of valid numeric \
            choices. It is typically represented visually as having a [i]track[/i] and a [i]knob[/i] \
            or [i]thumb[/i] which is dragged within the track. The [i]Slider[/i] can optionally show tick \
            marks and labels indicating the different slider position values.
                        
            AtlantaFX also provides the [b]ProgressSliderSkin[/b], which implements color support for \
            [i]Slider[/i] progress indication. Additionally, it adds the [code]Styles.SMALL[/code] and \
            [code]Styles.LARGE[/code] style class modifiers to change the [i]Slider[/i] size."""
        );
        addSection("Usage", usageExample());
        addSection("Small", smallExample());
        addSection("Large", largeExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var hSlider = new Slider(1, 5, 3);

        var hTickSlider = createTickSlider();
        hTickSlider.setSkin(new ProgressSliderSkin(hTickSlider));

        var vSlider = new Slider(1, 5, 3);
        vSlider.setOrientation(Orientation.VERTICAL);

        var vTickSlider = createTickSlider();
        vTickSlider.setOrientation(Orientation.VERTICAL);
        vTickSlider.setSkin(new ProgressSliderSkin(vTickSlider));
        //snippet_1:end

        var box = createGrid(hSlider, hTickSlider, vSlider, vTickSlider);

        return new ExampleBox(box, new Snippet(getClass(), 1));
    }

    private ExampleBox smallExample() {
        //snippet_2:start
        var hSlider = new Slider(1, 5, 3);
        hSlider.getStyleClass().add(Styles.SMALL);

        var hTickSlider = createTickSlider();
        hTickSlider.getStyleClass().add(Styles.SMALL);
        hTickSlider.setSkin(new ProgressSliderSkin(hTickSlider));

        var vSlider = new Slider(1, 5, 3);
        vSlider.setOrientation(Orientation.VERTICAL);
        vSlider.getStyleClass().add(Styles.SMALL);

        var vTickSlider = createTickSlider();
        vTickSlider.setOrientation(Orientation.VERTICAL);
        vTickSlider.getStyleClass().add(Styles.SMALL);
        vTickSlider.setSkin(new ProgressSliderSkin(vTickSlider));
        //snippet_2:end

        var box = createGrid(hSlider, hTickSlider, vSlider, vTickSlider);

        return new ExampleBox(box, new Snippet(getClass(), 2));
    }

    private ExampleBox largeExample() {
        //snippet_3:start
        var hSlider = new Slider(1, 5, 3);
        hSlider.getStyleClass().add(Styles.LARGE);

        var hTickSlider = createTickSlider();
        hTickSlider.getStyleClass().add(Styles.LARGE);
        hTickSlider.setSkin(new ProgressSliderSkin(hTickSlider));

        var vSlider = new Slider(1, 5, 3);
        vSlider.setOrientation(Orientation.VERTICAL);
        vSlider.getStyleClass().add(Styles.LARGE);

        var vTickSlider = createTickSlider();
        vTickSlider.setOrientation(Orientation.VERTICAL);
        vTickSlider.getStyleClass().add(Styles.LARGE);
        vTickSlider.setSkin(new ProgressSliderSkin(vTickSlider));
        //snippet_3:end

        var box = createGrid(hSlider, hTickSlider, vSlider, vTickSlider);

        return new ExampleBox(box, new Snippet(getClass(), 3));
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

    private GridPane createGrid(Slider hs1, Slider hs2, Slider vs1, Slider vs2) {
        var grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(20);

        hs1.setMinWidth(200);
        hs2.setMinWidth(200);
        vs1.setMinHeight(200);
        vs2.setMinHeight(200);

        grid.add(hs1, 0, 0);
        grid.add(hs2, 0, 1);
        grid.add(vs1, 1, 0, 1, GridPane.REMAINING);
        grid.add(vs2, 2, 0, 1, GridPane.REMAINING);

        return grid;
    }
}
