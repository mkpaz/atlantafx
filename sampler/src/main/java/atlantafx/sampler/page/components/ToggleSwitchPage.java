/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public final class ToggleSwitchPage extends OutlinePage {

    public static final String NAME = "ToggleSwitch";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public ToggleSwitchPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]ToggleSwitch[/i] is a control used to activate or deactivate a feature. \
            It consists of a horizontal bar with a small knob that can be moved to turn on \
            or off a function."""
        );
        addSection("Usage", usageExample());
        addSection("Color", colorExample());
        addSection("Toggle Group", toggleGroupExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var toggle1 = new ToggleSwitch("Enabled");
        toggle1.selectedProperty().addListener(
            (obs, old, val) -> toggle1.setText(val ? "Enabled" : "Disabled")
        );
        toggle1.setSelected(true);

        var toggle2 = new ToggleSwitch("Disabled");
        toggle2.selectedProperty().addListener(
            (obs, old, val) -> toggle2.setText(val ? "Enabled" : "Disabled")
        );
        toggle2.setLabelPosition(HorizontalDirection.RIGHT);
        toggle2.setSelected(false);
        //snippet_1:end

        var box = new HBox(HGAP_30, toggle1, toggle2);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            The text position can be changed by setting the [code]labelPosition[/code] \
            property value."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox colorExample() {
        //snippet_2:start
        var successToggle = new ToggleSwitch("Enabled");
        successToggle.selectedProperty().addListener((obs, old, val) -> {
                successToggle.setText(val ? "Enabled" : "Disabled");
                successToggle.pseudoClassStateChanged(Styles.STATE_SUCCESS, val);
            }
        );
        successToggle.setSelected(true);
        successToggle.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);

        var dangerToggle = new ToggleSwitch("Disabled");
        dangerToggle.selectedProperty().addListener((obs, old, val) -> {
                dangerToggle.setText(val ? "Enabled" : "Disabled");
                dangerToggle.pseudoClassStateChanged(Styles.STATE_DANGER, val);
            }
        );
        dangerToggle.setLabelPosition(HorizontalDirection.RIGHT);
        dangerToggle.setSelected(false);
        //snippet_2:end

        var box = new HBox(HGAP_30, successToggle, dangerToggle);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            Use [code]Styles.STATE_SUCCESS[/code] or [code]Styles.STATE_DANGER[/code] \
            pseudo-classes to change the [i]ToggleSwitch[/i] color.""");

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox toggleGroupExample() {
        //snippet_3:start
        var group = new ToggleGroup();

        var toggle1 = new ToggleSwitch();
        toggle1.setToggleGroup(group);
        toggle1.setSelected(true);

        var toggle2 = new ToggleSwitch();
        toggle2.setToggleGroup(group);

        var toggle3 = new ToggleSwitch();
        toggle3.setToggleGroup(group);
        //snippet_3:end

        var grid = new GridPane();
        grid.setHgap(HGAP_20);
        grid.setVgap(VGAP_10);
        grid.addRow(0, new Label("Option 1"), toggle1);
        grid.addRow(1, new Label("Option 2"), toggle2);
        grid.addRow(2, new Label("Option 3"), toggle3);

        var description = BBCodeParser.createFormattedText("""
            Toggles can optionally be combined into a group where only one switch \
            at a time can be selected."""
        );

        return new ExampleBox(grid, new Snippet(getClass(), 3), description);
    }
}
