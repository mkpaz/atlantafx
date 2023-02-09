/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.util.IntegerStringConverter;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.Spinner;
import javafx.scene.layout.FlowPane;

public final class SpinnerPage extends AbstractPage {

    public static final String NAME = "Spinner";
    private static final int PREF_WIDTH = 120;

    @Override
    public String getName() {
        return NAME;
    }

    public SpinnerPage() {
        super();
        setUserContent(new FlowPane(
            Page.PAGE_HGAP, Page.PAGE_VGAP,
            basicSample(),
            arrowsLeftVerticalSample(),
            arrowsLeftHorizontalSample(),
            arrowsRightHorizontalSample(),
            arrowsSplitHorizontalSample(),
            arrowsSplitVerticalSample(),
            disabledSample()
        ));
    }

    private SampleBlock basicSample() {
        var spinner = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner);
        spinner.setPrefWidth(PREF_WIDTH);
        spinner.setEditable(true);
        return new SampleBlock("Basic", spinner);
    }

    private SampleBlock disabledSample() {
        var spinner = new Spinner<Integer>(1, 10, 1);
        spinner.setPrefWidth(PREF_WIDTH);
        spinner.setDisable(true);
        return new SampleBlock("Disabled", spinner);
    }

    private SampleBlock arrowsLeftVerticalSample() {
        var spinner = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner);
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);
        spinner.setPrefWidth(PREF_WIDTH);
        spinner.setEditable(true);
        return new SampleBlock("Left & Vertical", spinner);
    }

    private SampleBlock arrowsLeftHorizontalSample() {
        var spinner = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner);
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_HORIZONTAL);
        spinner.setPrefWidth(PREF_WIDTH);
        spinner.setEditable(true);
        return new SampleBlock("Left & Horizontal", spinner);
    }

    private SampleBlock arrowsRightHorizontalSample() {
        var spinner = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner);
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        spinner.setPrefWidth(PREF_WIDTH);
        spinner.setEditable(true);
        return new SampleBlock("Right & Horizontal", spinner);
    }

    private SampleBlock arrowsSplitHorizontalSample() {
        var spinner = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner);
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner.setPrefWidth(PREF_WIDTH);
        spinner.setEditable(true);
        return new SampleBlock("Split & Horizontal", spinner);
    }

    private SampleBlock arrowsSplitVerticalSample() {
        var spinner = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner);
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
        spinner.setEditable(true);
        spinner.setPrefWidth(40);
        return new SampleBlock("Split & Vertical", spinner);
    }
}
