/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.CSSFragment;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

// #javafx-bug Indeterminate (animated) progress bar and also progress indicator
// are VERY resource expensive. It consumes a single CPU core and a lot of memory.
public class ProgressPage extends AbstractPage {

    public static final String NAME = "Progress";

    @Override
    public String getName() { return NAME; }

    public ProgressPage() {
        super();
        setUserContent(new VBox(Page.PAGE_VGAP,
                expandingHBox(basicBarSample(), basicIndicatorSample()),
                expandingHBox(barSizeSample(), colorChangeSample())
        ));
    }

    private SampleBlock basicBarSample() {
        var flowPane = new FlowPane(
                BLOCK_HGAP, BLOCK_VGAP,
                createBar(0, false),
                createBar(0.5, false),
                createBar(1, false),
                createBar(0.5, true)
        );
        flowPane.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Progress Bar", flowPane);
    }

    private SampleBlock basicIndicatorSample() {
        var flowPane = new FlowPane(
                BLOCK_HGAP, BLOCK_VGAP,
                createIndicator(0, false),
                createIndicator(0.5, false),
                createIndicator(1, false),
                createIndicator(0.5, true)
        );
        flowPane.setAlignment(Pos.TOP_LEFT);

        return new SampleBlock("Progress Indicator", flowPane);
    }

    private SampleBlock barSizeSample() {
        var container = new VBox(
                BLOCK_VGAP,
                new HBox(20, createBar(0.5, false, SMALL), new Text("small")),
                new HBox(20, createBar(0.5, false, MEDIUM), new Text("medium")),
                new HBox(20, createBar(0.5, false, LARGE), new Text("large"))
        );
        container.getChildren().forEach(c -> ((HBox) c).setAlignment(Pos.CENTER_LEFT));

        return new SampleBlock("Size", container);
    }

    private SampleBlock colorChangeSample() {
        var stateSuccess = PseudoClass.getPseudoClass("state-success");
        var stateDanger = PseudoClass.getPseudoClass("state-danger");
        var width = 300;

        var bar = new ProgressBar(0);
        bar.getStyleClass().add(LARGE);
        bar.setPrefWidth(width);
        bar.setMaxWidth(width);

        var barText = new Label();

        var barStack = new StackPane(bar, barText);
        barStack.getStyleClass().add("example");
        barStack.setPrefWidth(width);
        barStack.setMaxWidth(width);

        var runBtn = new Button("Start");
        runBtn.disableProperty().bind(bar.progressProperty().greaterThan(0));

        // ~

        var content = new VBox(BLOCK_VGAP);
        content.getChildren().setAll(barStack, runBtn);
        content.setAlignment(Pos.CENTER_LEFT);

        bar.progressProperty().addListener((obs, old, val) -> {
            if (val == null) { return; }

            if (val.floatValue() > 0.80) {
                barStack.pseudoClassStateChanged(stateDanger, true);
            } else if (val.floatValue() > 0.47) {
                barStack.pseudoClassStateChanged(stateSuccess, true);
            }
        });

        new CSSFragment("""
                .example:state-success .progress-bar {
                    -color-progress-bar-fill: -color-success-emphasis;
                }
                .example:state-danger  .progress-bar {
                    -color-progress-bar-fill: -color-danger-emphasis;
                }
                .example:state-success .label,
                .example:state-danger  .label {
                    -fx-text-fill: -color-fg-emphasis;
                }
                """
        ).addTo(content);

        runBtn.setOnAction(e1 -> {
            var task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int steps = 1_000;

                    for (int i = 0; i < steps; i++) {
                        Thread.sleep(10);
                        updateProgress(i, steps);
                        updateMessage(String.valueOf(i));
                    }
                    return null;
                }
            };

            // reset properties, so we can start a new task
            task.setOnSucceeded(e2 -> {
                bar.progressProperty().unbind();
                barText.textProperty().unbind();

                bar.setProgress(0);
                barText.setText(null);

                barStack.pseudoClassStateChanged(stateSuccess, false);
                barStack.pseudoClassStateChanged(stateDanger, false);
            });

            bar.progressProperty().bind(task.progressProperty());
            barText.textProperty().bind(task.messageProperty());

            new Thread(task).start();
        });

        return new SampleBlock("Dynamic Color Change", content);
    }

    private ProgressIndicator createBar(double progress, boolean disabled, String... styleClasses) {
        var bar = new ProgressBar(progress);
        bar.getStyleClass().addAll(styleClasses);
        bar.setDisable(disabled);
        return bar;
    }

    private ProgressIndicator createIndicator(double progress, boolean disabled) {
        var indicator = new ProgressIndicator(progress);
        indicator.setMinSize(50, 50);
        indicator.setMaxSize(50, 50);
        indicator.setDisable(disabled);
        return indicator;
    }
}
