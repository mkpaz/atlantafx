/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

// Indeterminate (animated) progress bar and also progress indicator are very expensive.
// It consumes single CPU core and a lot of memory.
// #javafx-bug
public class ProgressPage extends AbstractPage {

    public static final String NAME = "Progress";

    @Override
    public String getName() { return NAME; }

    public ProgressPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(
                basicBarSamples().getRoot(),
                basicIndicatorSamples().getRoot(),
                barSizeSamples().getRoot()
        );
    }

    private SampleBlock basicBarSamples() {
        var flowPane = new FlowPane(20, 20);
        flowPane.setAlignment(Pos.CENTER_LEFT);
        flowPane.getChildren().addAll(
                progressBar(0, false),
                progressBar(0.5, false),
                progressBar(1, false),
                progressBar(0.5, true)
        );

        return new SampleBlock("Progress Bar", flowPane);
    }

    private SampleBlock basicIndicatorSamples() {
        var flowPane = new FlowPane(20, 20);
        flowPane.getChildren().addAll(
                progressIndicator(0, false),
                progressIndicator(0.5, false),
                progressIndicator(1, false),
                progressIndicator(0.5, true)
        );
        flowPane.setAlignment(Pos.TOP_LEFT);

        return new SampleBlock("Progress Indicator", flowPane);
    }

    private SampleBlock barSizeSamples() {
        var container = new VBox(
                10,
                new HBox(20, progressBar(0.5, false, Styles.SMALL), new Text("small")),
                new HBox(20, progressBar(0.5, false, Styles.MEDIUM), new Text("medium")),
                new HBox(20, progressBar(0.5, false, Styles.LARGE), new Text("large"))
        );
        container.getChildren().forEach(c -> ((HBox) c).setAlignment(Pos.CENTER_LEFT));

        return new SampleBlock("Size", container);
    }

    private ProgressIndicator progressBar(double progress, boolean disabled, String... styleClasses) {
        var bar = new ProgressBar(progress);
        bar.getStyleClass().addAll(styleClasses);
        bar.setDisable(disabled);
        return bar;
    }

    private ProgressIndicator progressIndicator(double progress, boolean disabled) {
        var indicator = new ProgressIndicator(progress);
        indicator.setMinSize(50, 50);
        indicator.setMaxSize(50, 50);
        indicator.setDisable(disabled);
        return indicator;
    }
}
