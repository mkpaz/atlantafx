/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;

import static javafx.geometry.Orientation.VERTICAL;

public class TooltipPage extends AbstractPage {

    public static final String NAME = "Tooltip";

    @Override
    public String getName() { return NAME; }

    public TooltipPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(
                basicSamples(),
                positionSamples().getRoot()
        );
    }

    private FlowPane basicSamples() {
        var basicTooltip = new Tooltip(FAKER.harryPotter().spell());
        basicTooltip.setHideDelay(Duration.seconds(3));
        var basicLabel = label("Hover me");
        basicLabel.setTooltip(basicTooltip);
        var basicBlock = new SampleBlock("Basic", basicLabel);

        var textWrapTooltip = new Tooltip(FAKER.lorem().paragraph(5));
        textWrapTooltip.setHideDelay(Duration.seconds(3));
        textWrapTooltip.setPrefWidth(200);
        textWrapTooltip.setWrapText(true);
        var textWrapLabel = label("Hover me");
        textWrapLabel.setTooltip(textWrapTooltip);
        var textWrapBlock = new SampleBlock("Text wrapping", textWrapLabel);

        var indefiniteTooltip = new Tooltip(FAKER.harryPotter().spell());
        indefiniteTooltip.setHideDelay(Duration.INDEFINITE);
        var indefiniteLabel = label("Hover me");
        indefiniteLabel.setTooltip(basicTooltip);
        var indefiniteBlock = new SampleBlock("Indefinite", indefiniteLabel);

        return new FlowPane(20, 10,
                basicBlock.getRoot(),
                textWrapBlock.getRoot(),
                indefiniteBlock.getRoot()
        );
    }

    private SampleBlock positionSamples() {
        var topLeftLabel = label("Top Left");
        topLeftLabel.setTooltip(tooltip("Top Left", AnchorLocation.WINDOW_BOTTOM_RIGHT));

        var topRightLabel = label("Top Right");
        topRightLabel.setTooltip(tooltip("Top Right", AnchorLocation.WINDOW_BOTTOM_LEFT));

        var bottomLeftLabel = label("Bottom Left");
        bottomLeftLabel.setTooltip(tooltip("Bottom Left", AnchorLocation.WINDOW_TOP_RIGHT));

        var bottomRightLabel = label("Bottom Right");
        bottomRightLabel.setTooltip(tooltip("Bottom Right", AnchorLocation.WINDOW_TOP_LEFT));

        var flowPane = new FlowPane(20, 10);
        flowPane.getChildren().setAll(
                topLeftLabel,
                new Separator(VERTICAL),
                topRightLabel,
                new Separator(VERTICAL),
                bottomLeftLabel,
                new Separator(VERTICAL),
                bottomRightLabel
        );

        return new SampleBlock("Position", flowPane);
    }

    private Label label(String text) {
        Label label = new Label(text);
        label.setMinWidth(50);
        label.setMinHeight(50);
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

    private Tooltip tooltip(String text, AnchorLocation anchorLocation) {
        var tooltip = new Tooltip(text);
        tooltip.setAnchorLocation(anchorLocation);
        return tooltip;
    }
}
