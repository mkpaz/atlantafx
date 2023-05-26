/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;

public final class TooltipPage extends OutlinePage {

    public static final String NAME = "Tooltip";

    @Override
    public String getName() {
        return NAME;
    }

    public TooltipPage() {
        super();

        addPageHeader();
        addFormattedText("""
            Tooltips are used for showing additional information when the node is hovered over by the mouse.
                        
            [ul]
            [li]Any node can show a tooltip.[/li]
            [li]A Tooltip is able to show within it an arbitrary scenegraph of nodes.[/li]
            [li]A single tooltip can be installed on multiple target nodes or multiple controls.[/li][/ul]"""
        );
        addSection("Usage", usageExample());
        addSection("Position", positionExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var basicTtp = new Tooltip(FAKER.harryPotter().spell());
        basicTtp.setHideDelay(Duration.seconds(3));

        var basicLbl = createLabel("Basic");
        basicLbl.setTooltip(basicTtp);

        var longTtp = new Tooltip(FAKER.lorem().paragraph(5));
        longTtp.setHideDelay(Duration.seconds(3));
        longTtp.setPrefWidth(200);
        longTtp.setWrapText(true);

        var longLbl = createLabel("Long Text");
        longLbl.setTooltip(longTtp);
        //snippet_1:end

        var box = new HBox(HGAP_20, basicLbl, longLbl);

        var description = BBCodeParser.createFormattedText("""
            In most cases a [i]Tooltip[/i] is created and its text property is \
            modified to show plain text to the user."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 1), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox positionExample() {
        //snippet_2:start
        // if the tooltip disappears almost immediately,
        // it's a #javafx-bug (you'll never walk alone)
        var topLeftTtp = new Tooltip(FAKER.chuckNorris().fact());
        topLeftTtp.setAnchorLocation(AnchorLocation.WINDOW_BOTTOM_RIGHT);
        var topLeftLbl = createLabel("Top Left");
        topLeftLbl.setTooltip(topLeftTtp);

        var topRightTtp = new Tooltip(FAKER.chuckNorris().fact());
        topRightTtp.setAnchorLocation(AnchorLocation.WINDOW_BOTTOM_LEFT);
        var topRightLbl = createLabel("Top Right");
        topRightLbl.setTooltip(topRightTtp);

        var bottomLeftTtp = new Tooltip(FAKER.chuckNorris().fact());
        bottomLeftTtp.setAnchorLocation(AnchorLocation.WINDOW_TOP_RIGHT);
        var bottomLeftLbl = createLabel("Bottom Left");
        bottomLeftLbl.setTooltip(bottomLeftTtp);

        var bottomRightTtp = new Tooltip(FAKER.chuckNorris().fact());
        bottomRightTtp.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
        var bottomRightLbl = createLabel("Bottom Right");
        bottomRightLbl.setTooltip(bottomRightTtp);
        //snippet_2:end

        var box = new HBox(HGAP_20, topLeftLbl, topRightLbl, bottomLeftLbl, bottomRightLbl);

        var description = BBCodeParser.createFormattedText("""
            You can specify the popup anchor point which is used in [i]Tooltip[/i] positioning. \
            The point can be set to a corner of the popup window or a corner of its content."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 2), description);
        example.setAllowDisable(false);

        return example;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setMinWidth(100);
        label.setMinHeight(50);
        label.setPadding(new Insets(10));
        label.setStyle("-fx-background-color:-color-accent-subtle;");
        label.setAlignment(Pos.CENTER);
        return label;
    }
}
