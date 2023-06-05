/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.RingProgressIndicator;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

// #javafx-bug Indeterminate (animated) progress bar and also progress indicator
// are very resource expensive. It consumes a single CPU core and a lot of memory.
public final class ProgressIndicatorPage extends OutlinePage {

    public static final String NAME = "ProgressIndicator";

    @Override
    public String getName() {
        return NAME;
    }

    public ProgressIndicatorPage() {
        super();

        addPageHeader();
        addFormattedText("""
            JavaFX provides the two types of progress indicators: the [i]ProgressIndicator[/i] and \
            the [i]ProgressBar[/i]. In addition to them AtlantaFX also provides the \
            [i]RingProgressIndicator[/i]."""
        );
        addSection("Progress Bar", basicBarExample());
        addSection("Progress Indicator", basicIndicatorExample());
        addSection("Size", barSizeSample());
        addSection("Ring Indicator", ringIndicatorExample());
        addSection("Indeterminate", indeterminateExample());
        addSection("Dynamic Color Change", colorChangeExample());
    }

    private ExampleBox basicBarExample() {
        //snippet_1:start
        var bar1 = new ProgressBar(0);
        bar1.setPrefWidth(250);

        var bar2 = new ProgressBar(0.5);
        bar2.setPrefWidth(250);

        var bar3 = new ProgressBar(1);
        bar3.setPrefWidth(250);
        //snippet_1:end

        var box = new VBox(VGAP_20, bar1, bar2, bar3);
        box.setMinHeight(100);

        var description = BBCodeParser.createFormattedText("""
            [i]ProgressBar[/i] is a specialization of the [i]ProgressIndicator[/i] \
            which is represented as a horizontal bar."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox basicIndicatorExample() {
        //snippet_2:start
        var ind1 = new ProgressIndicator(0);
        ind1.setMinSize(50, 50);

        var ind2 = new ProgressIndicator(0.5);
        ind2.setMinSize(50, 50);

        var ind3 = new ProgressIndicator(1);
        ind3.setMinSize(50, 50);
        //snippet_2:end

        var box = new HBox(VGAP_20, ind1, ind2, ind3);
        var description = BBCodeParser.createFormattedText("""
            [i]ProgressIndicator[/i] is a circular control which is used for indicating progress, \
            either infinite (aka indeterminate) or finite."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox barSizeSample() {
        //snippet_3:start
        var smallBar = new ProgressBar(0.5);
        smallBar.setPrefWidth(250);
        smallBar.getStyleClass().add(Styles.SMALL);

        var mediumBar = new ProgressBar(0.5);
        mediumBar.setPrefWidth(250);
        mediumBar.getStyleClass().add(Styles.MEDIUM);

        var largeBar = new ProgressBar(0.5);
        largeBar.setPrefWidth(250);
        largeBar.getStyleClass().add(Styles.LARGE);
        //snippet_3:end

        var box = new VBox(
            VGAP_20,
            new HBox(HGAP_30, smallBar, new Text("small")),
            new HBox(HGAP_30, mediumBar, new Text("medium")),
            new HBox(HGAP_30, largeBar, new Text("large"))
        );
        box.setAlignment(Pos.TOP_LEFT);
        box.getChildren().forEach(c -> ((HBox) c).setAlignment(Pos.CENTER_LEFT));

        var description = BBCodeParser.createFormattedText("""
            To change the height of the [i]ProgressBar[/i], you can apply the \
            [code]Styles.SMALL[/code] or [code]Styles.LARGE[/code] style classes, respectively. \
            Use the large variant if you want to display a text on top of the [i]ProgressBar[/i]."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox ringIndicatorExample() {
        //snippet_4:start
        var basicInd = new RingProgressIndicator(0, false);

        var customTextInd = new RingProgressIndicator(0.5, false);
        customTextInd.setMinSize(75, 75);
        customTextInd.setStringConverter(new StringConverter<>() {
            @Override
            public String toString(Double progress) {
                return (int) Math.ceil(progress * 100) + "°";
            }

            @Override
            public Double fromString(String progress) {
                return 0d;
            }
        });

        var reverseInd = new RingProgressIndicator(0.25, true);
        reverseInd.setMinSize(150, 150);

        var reverseLabel = new Label("25%");
        reverseLabel.getStyleClass().add(Styles.TITLE_4);

        var reverseBtn = new Button(null, new FontIcon(Feather.PLAY));
        reverseBtn.getStyleClass().addAll(
            Styles.BUTTON_CIRCLE, Styles.FLAT
        );
        reverseBtn.disableProperty().bind(
            reverseInd.progressProperty().greaterThan(0.25)
        );
        reverseBtn.setOnAction(evt1 -> {
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
            task.setOnSucceeded(evt2 -> {
                reverseInd.progressProperty().unbind();
                reverseLabel.textProperty().unbind();
                reverseInd.setProgress(0.25);
                reverseLabel.setText("25%");
            });

            reverseInd.progressProperty().bind(task.progressProperty());
            reverseLabel.textProperty().bind(task.messageProperty());

            new Thread(task).start();
        });

        var reverseGraphic = new VBox(10, reverseLabel, reverseBtn);
        reverseGraphic.setAlignment(Pos.CENTER);
        reverseInd.setGraphic(reverseGraphic);
        //snippet_4:end

        var box = new HBox(HGAP_20, basicInd, customTextInd, reverseInd);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            The [i]RingProgressIndicator[/i] is a type of progress indicator \
            that displays progress as a ring that gradually empties out as a task is completed."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }

    private ExampleBox indeterminateExample() {
        //snippet_5:start
        var barToggle = new ToggleButton("Start");
        barToggle.textProperty().bind(Bindings.createStringBinding(
            () -> barToggle.isSelected() ? "Stop" : "Start",
            barToggle.selectedProperty()
        ));
        var bar = new ProgressBar(0);
        bar.progressProperty().bind(Bindings.createDoubleBinding(
            () -> barToggle.isSelected() ? -1d : 0d,
            barToggle.selectedProperty()
        ));

        var indicatorToggle = new ToggleButton("Start");
        indicatorToggle.textProperty().bind(Bindings.createStringBinding(
            () -> indicatorToggle.isSelected() ? "Stop" : "Start",
            indicatorToggle.selectedProperty()
        ));
        var indicator = new ProgressIndicator(0);
        indicator.setMinSize(60, 60);
        indicator.progressProperty().bind(Bindings.createDoubleBinding(
            () -> indicatorToggle.isSelected() ? -1d : 0d,
            indicatorToggle.selectedProperty()
        ));

        var ringToggle = new ToggleButton("Start");
        ringToggle.textProperty().bind(Bindings.createStringBinding(
            () -> ringToggle.isSelected() ? "Stop" : "Start",
            ringToggle.selectedProperty()
        ));
        var ring = new RingProgressIndicator(0, false);
        ring.setMinSize(75, 75);
        ring.progressProperty().bind(Bindings.createDoubleBinding(
            () -> ringToggle.isSelected() ? -1d : 0d,
            ringToggle.selectedProperty()
        ));
        //snippet_5:end

        var grid = new GridPane();
        grid.setHgap(60);
        grid.setVgap(10);
        grid.getColumnConstraints().setAll(
            new ColumnConstraints(-1, -1, -1, Priority.NEVER, HPos.CENTER, true),
            new ColumnConstraints(-1, -1, -1, Priority.NEVER, HPos.CENTER, true),
            new ColumnConstraints(-1, -1, -1, Priority.NEVER, HPos.CENTER, true)
        );
        grid.getRowConstraints().setAll(
            new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, true),
            new RowConstraints(-1, -1, -1, Priority.NEVER, VPos.CENTER, true)
        );
        grid.addColumn(0, bar, barToggle);
        grid.addColumn(1, indicator, indicatorToggle);
        grid.addColumn(2, ring, ringToggle);

        var description = BBCodeParser.createFormattedText("""
            Animated JavaFX progress indicators aren't cheap. They can consume quite a lot of CPU time."""
        );

        return new ExampleBox(grid, new Snippet(getClass(), 5), description);
    }

    private ExampleBox colorChangeExample() {
        var dataClass = """
            .example:success .progress-bar {
                 -color-progress-bar-fill: -color-success-emphasis;
             }
             .example:danger  .progress-bar {
                 -color-progress-bar-fill: -color-danger-emphasis;
             }
             .example:success .label,
             .example:danger  .label {
                 -fx-text-fill: -color-fg-emphasis;
             }""";

        //snippet_6:start
        var bar = new ProgressBar(0);
        bar.getStyleClass().add(Styles.LARGE);
        bar.setPrefWidth(300);
        bar.setMaxWidth(300);

        var barText = new Label();

        var barStack = new StackPane(bar, barText);
        barStack.getStyleClass().add("example");
        barStack.setPrefWidth(300);
        barStack.setMaxWidth(300);

        var runBtn = new Button("Start");
        runBtn.disableProperty().bind(
            bar.progressProperty().greaterThan(0)
        );

        var content = new VBox(VGAP_10, barStack, runBtn);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPrefHeight(200);
        // .example:success .progress-bar {
        //     -color-progress-bar-fill: -color-success-emphasis;
        // }
        // .example:danger  .progress-bar {
        //     -color-progress-bar-fill: -color-danger-emphasis;
        // }
        // .example:success .label,
        // .example:danger  .label {
        //     -fx-text-fill: -color-fg-emphasis;
        // }
        content.getStylesheets().add(Styles.toDataURI(dataClass));

        bar.progressProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }

            if (val.floatValue() > 0.80) {
                barStack.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            } else if (val.floatValue() > 0.47) {
                barStack.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
            }
        });

        runBtn.setOnAction(evt1 -> {
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
            task.setOnSucceeded(evt2 -> {
                bar.progressProperty().unbind();
                barText.textProperty().unbind();

                bar.setProgress(0);
                barText.setText(null);

                barStack.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                barStack.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            });

            bar.progressProperty().bind(task.progressProperty());
            barText.textProperty().bind(task.messageProperty());

            new Thread(task).start();
        });
        //snippet_6:end

        var description = BBCodeParser.createFormattedText("""
            This example demonstrates how looked-up color variables can be used to \
            change the progress bar color while the task is in progress."""
        );

        var example = new ExampleBox(content, new Snippet(getClass(), 6), description);
        example.setAllowDisable(false);

        return example;
    }
}
