/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.BottomSheet;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class BottomSheetPage extends OutlinePage {

    public static final String NAME = "BottomSheet";

    private final BottomSheet bottomSheet = new BottomSheet();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public BottomSheetPage() {
        super();

        getChildren().add(bottomSheet);

        // reset state when sheet closes to avoid conflicts between examples
        bottomSheet.displayProperty().addListener((obs, old, val) -> {
            if (!val) {
                bottomSheet.setHeader(null);
                bottomSheet.setPersistent(false);
            }
        });

        addPageHeader();
        addFormattedText("""
            A bottom sheet is a UI component that slides up from the bottom of the screen, \
            commonly used in mobile and modern web applications. It can display arbitrary content \
            and supports drag-to-dismiss gesture, ESC key dismiss, and click-outside dismiss."""
        );
        addSection("Usage", usageExample());
        addSection("With Header", headerExample());
        addSection("Scrollable Content", scrollableExample());
        addSection("Drag to Dismiss", dragDismissExample());
        addSection("Persistent", persistentExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var content = new VBox(10, new Label("Hello from BottomSheet!"));
        content.setPadding(new Insets(16));

        var showBtn = new Button("Show");
        showBtn.setOnAction(evt -> bottomSheet.show(content));

        var hideBtn = new Button("Hide");
        hideBtn.setOnAction(evt -> bottomSheet.hide());
        //snippet_1:end

        var box = new HBox(HGAP_20, showBtn, hideBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            The simplest way to use [i]BottomSheet[/i] is to call the [code]show(Node)[/code] \
            method which sets the content and triggers the display. You can dismiss it by \
            calling [code]hide()[/code], pressing ESC, or clicking outside the sheet."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 1), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox headerExample() {
        //snippet_2:start
        var header = new BorderPane();
        header.setLeft(new Label("Choose an Option"));
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 1.2em;");
        header.setPadding(new Insets(8, 16, 4, 16));

        var content = new VBox(10);
        content.setPadding(new Insets(0, 16, 16, 16));
        for (String item : new String[]{"Take Photo", "Choose from Gallery", "Share", "Delete"}) {
            var btn = new Button(item);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(evt -> bottomSheet.hide());
            content.getChildren().add(btn);
        }

        var showBtn = new Button("Show Action Sheet");
        showBtn.setOnAction(evt -> {
            bottomSheet.setHeader(header);
            bottomSheet.show(content);
        });
        //snippet_2:end

        var box = new HBox(showBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            You can optionally set a header node that appears above the content area \
            and below the drag handle. This is useful for showing titles or action buttons."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 2), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox scrollableExample() {
        //snippet_3:start
        var content = new VBox(8);
        content.setPadding(new Insets(0, 16, 16, 16));
        for (int i = 0; i < 20; i++) {
            content.getChildren().add(new Label(FAKER.lorem().sentence()));
        }

        var scrollContent = new ScrollPane(content);
        scrollContent.setFitToWidth(true);
        scrollContent.setPrefHeight(300);
        scrollContent.setMaxHeight(400);

        var showBtn = new Button("Show Long Content");
        showBtn.setOnAction(evt -> bottomSheet.show(scrollContent));
        //snippet_3:end

        var box = new HBox(showBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            For long content, wrap the content in a [i]ScrollPane[/i] to make it scrollable \
            inside the sheet. The sheet will display at a comfortable height."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 3), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox dragDismissExample() {
        //snippet_4:start
        var content = new VBox(10);
        content.setPadding(new Insets(16));
        content.getChildren().setAll(
            new Label("Drag this sheet downward to dismiss it."),
            new Separator(),
            new Label("The dismiss threshold controls how far"),
            new Label("you need to drag before the sheet closes.")
        );

        var sliderLabel = new Label("Dismiss Threshold: 100");
        var slider = new Slider(50, 300, 100);
        slider.setShowTickLabels(true);
        slider.setBlockIncrement(10);
        slider.valueProperty().addListener((obs, old, val) -> {
            int threshold = val.intValue();
            bottomSheet.setDismissThreshold(threshold);
            sliderLabel.setText("Dismiss Threshold: " + threshold);
        });

        var showBtn = new Button("Show");
        showBtn.setOnAction(evt -> bottomSheet.show(content));
        //snippet_4:end

        var box = new HBox(HGAP_20, showBtn, sliderLabel, slider);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            The [i]BottomSheet[/i] supports drag-to-dismiss gesture. Drag the sheet \
            downward to dismiss it. The [code]dismissThreshold[/code] property controls \
            how far the user must drag before the sheet is dismissed."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 4), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox persistentExample() {
        //snippet_5:start
        var content = new VBox(10);
        content.setPadding(new Insets(16));
        content.getChildren().setAll(
            new Label("This sheet cannot be dismissed by ESC or clicking outside."),
            new Label("Press ESC or click outside to see the bounce animation."),
            new Separator(),
            new Label("Use the Hide button below to close it.")
        );

        var showBtn = new Button("Show Persistent");
        showBtn.setOnAction(evt -> {
            bottomSheet.setPersistent(true);
            bottomSheet.show(content);
        });

        var hideBtn = new Button("Hide");
        hideBtn.setOnAction(evt -> {
            bottomSheet.setPersistent(false);
            bottomSheet.hide();
        });
        //snippet_5:end

        var box = new HBox(HGAP_20, showBtn, hideBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            Setting [code]persistent[/code] to [code]true[/code] prevents the sheet from being \
            dismissed by pressing ESC or clicking outside. Instead, a bounce animation is played \
            to indicate that the sheet cannot be closed this way."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 5), description);
        example.setAllowDisable(false);

        return example;
    }
}
