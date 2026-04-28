/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Chip;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChipPage extends OutlinePage {

    public static final String NAME = "Chip";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public ChipPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]Chip[/i] control represents a compact element that can be used \
            to display tags, filter options, or trigger actions. It supports an \
            optional close button and selection via [code]ToggleGroup[/code]."""
        );
        addSection("Basic", basicExample());
        addSection("Deletable", deletableExample());
        addSection("Selectable", selectableExample());
    }

    private Node basicExample() {
        //snippet_1:start
        var c1 = new Chip("Tag");
        var c2 = new Chip("Info");
        var c3 = new Chip("Warning");
        //snippet_3:end

        var box = new FlowPane(10, 10, c1, c2, c3);
        var description = BBCodeParser.createFormattedText("""
            A basic [i]Chip[/i] displays a text label. Place multiple chips \
            in a [code]FlowPane[/code] for automatic wrapping."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private Node deletableExample() {
        //snippet_2:start
        var chip = new Chip("Removable");
        chip.setOnClose(e -> chip.setVisible(false));
        //snippet_2:end

        var box = new FlowPane(10, 10, chip);
        var description = BBCodeParser.createFormattedText("""
            Set [code]onClose[/code] to display a close button. Clicking it \
            fires the event handler, where you can remove or hide the chip."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private Node selectableExample() {
        //snippet_3:start
        var group = new ToggleGroup();

        var c1 = new Chip("Option A");
        c1.setToggleGroup(group);

        var c2 = new Chip("Option B");
        c2.setToggleGroup(group);
        c2.setSelected(true);

        var c3 = new Chip("Option C");
        c3.setToggleGroup(group);
        //snippet_3:end

        var selectionLabel = new Label("Selected: Option B");
        selectionLabel.setStyle("-fx-text-fill: -color-fg-muted;");
        group.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val instanceof Chip c) {
                selectionLabel.setText("Selected: " + c.getText());
            } else {
                selectionLabel.setText("Selected: none");
            }
        });

        var box = new VBox(10, new FlowPane(10, 10, c1, c2, c3), selectionLabel);
        var description = BBCodeParser.createFormattedText("""
            Add chips to a [code]ToggleGroup[/code] to enable single-selection \
            behavior. The selected chip is visually highlighted."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }
}
