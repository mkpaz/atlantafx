/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.RingProgressIndicator;
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
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

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

        var grid = new GridPane();
        grid.setHgap(Page.PAGE_HGAP);
        grid.setVgap(Page.PAGE_VGAP);

        grid.add(basicBarSample(), 0, 0);
        grid.add(basicIndicatorSample(), 1, 0);

        grid.add(ringIndicatorSample(), 0, 1);
        grid.add(barSizeSample(), 1, 1);

        grid.add(colorChangeSample(), 0, 2);

        setUserContent(grid);
    }

    private SampleBlock basicBarSample() {
        var flowPane = new FlowPane(BLOCK_HGAP, BLOCK_VGAP, createBar(0, false), createBar(0.5, false), createBar(1, false), createBar(0.5, true));
        flowPane.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Progress Bar", flowPane);
    }

    private SampleBlock basicIndicatorSample() {
        var flowPane = new FlowPane(BLOCK_HGAP, BLOCK_VGAP, createIndicator(0, false), createIndicator(0.5, false), createIndicator(1, false), createIndicator(0.5, true));
        flowPane.setAlignment(Pos.TOP_LEFT);

        return new SampleBlock("Progress Indicator", flowPane);
    }

    private SampleBlock barSizeSample() {
        var container = new VBox(BLOCK_VGAP, new HBox(20, createBar(0.5, false, SMALL), new Text("small")), new HBox(20, createBar(0.5, false, MEDIUM), new Text("medium")), new HBox(20, createBar(0.5, false, LARGE), new Text("large")));
        container.getChildren().forEach(c -> ((HBox) c).setAlignment(Pos.CENTER_LEFT));

        return new SampleBlock("Size", container);
    }

    private SampleBlock ringIndicatorSample() {
        var basicIndicator = new RingProgressIndicator(0, false);

        var customTextIndicator = new RingProgressIndicator(0.5, false);
        customTextIndicator.setPrefSize(75, 75);
        customTextIndicator.setStringConverter(new StringConverter<>() {
            @Override
            public String toString(Double progress) {
                return (int) Math.ceil(progress * 100) + "°";
            }

            @Override
            public Double fromString(String progress) {
                return 0d;
            }
        });

        var reverseIndicator = new RingProgressIndicator(0.25, true);
        reverseIndicator.setPrefSize(150, 150);

        var reverseIndicatorLabel = new Label("25%");
        reverseIndicatorLabel.getStyleClass().add(TITLE_4);

        var reversePlayButton = new Button("", new FontIcon(Feather.PLAY));
        reversePlayButton.getStyleClass().addAll(BUTTON_CIRCLE, FLAT);
        reversePlayButton.disableProperty().bind(reverseIndicator.progressProperty().greaterThan(0.25));
        reversePlayButton.setOnAction(e1 -> {
            var task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int steps = 100;

                    for (int i = 25; i <= steps; i++) {
                        Thread.sleep(100);
                        updateProgress(i, steps);
                        updateMessage(i + "%");
                    }
                    return null;
                }
            };

            // reset properties, so we can start a new task
            task.setOnSucceeded(e2 -> {
                reverseIndicator.progressProperty().unbind();
                reverseIndicatorLabel.textProperty().unbind();

                reverseIndicator.setProgress(0.25);
                reverseIndicatorLabel.setText("25%");
            });

            reverseIndicator.progressProperty().bind(task.progressProperty());
            reverseIndicatorLabel.textProperty().bind(task.messageProperty());

            new Thread(task).start();
        });

        var reverseBox = new VBox(10, reverseIndicatorLabel, reversePlayButton);
        reverseBox.setAlignment(Pos.CENTER);
        reverseIndicator.setGraphic(reverseBox);

        // ~

        var box = new HBox(BLOCK_HGAP, basicIndicator, customTextIndicator, reverseIndicator);
        box.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Ring Indicator", box);
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
                """).addTo(content);

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
