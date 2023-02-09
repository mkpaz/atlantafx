/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;
import static javafx.geometry.Orientation.VERTICAL;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;

public class TooltipPage extends AbstractPage {

    public static final String NAME = "Tooltip";

    @Override
    public String getName() {
        return NAME;
    }

    public TooltipPage() {
        super();
        setUserContent(new VBox(Page.PAGE_VGAP,
            expandingHBox(
                basicSample(),
                textWrapSample(),
                indefiniteSample()
            ),
            positionSample()
        ));
    }

    private SampleBlock basicSample() {
        var tooltip = new Tooltip(FAKER.harryPotter().spell());
        tooltip.setHideDelay(Duration.seconds(3));

        var label = createLabel("Hover me");
        label.setTooltip(tooltip);

        return new SampleBlock("Basic", label);
    }

    private SampleBlock textWrapSample() {
        var tooltip = new Tooltip(FAKER.lorem().paragraph(5));
        tooltip.setHideDelay(Duration.seconds(3));
        tooltip.setPrefWidth(200);
        tooltip.setWrapText(true);

        var label = createLabel("Hover me");
        label.setTooltip(tooltip);

        return new SampleBlock("Text Wrap", label);
    }

    private SampleBlock indefiniteSample() {
        var tooltip = new Tooltip(FAKER.harryPotter().spell());
        tooltip.setHideDelay(Duration.INDEFINITE);

        var label = createLabel("Hover me");
        label.setTooltip(tooltip);

        return new SampleBlock("Indefinite", label);
    }

    private SampleBlock positionSample() {
        var topLeftLabel = createLabel("Top Left");
        topLeftLabel.setTooltip(createTooltip("Top Left", AnchorLocation.WINDOW_BOTTOM_RIGHT));

        var topRightLabel = createLabel("Top Right");
        topRightLabel.setTooltip(createTooltip("Top Right", AnchorLocation.WINDOW_BOTTOM_LEFT));

        var bottomLeftLabel = createLabel("Bottom Left");
        bottomLeftLabel.setTooltip(createTooltip("Bottom Left", AnchorLocation.WINDOW_TOP_RIGHT));

        var bottomRightLabel = createLabel("Bottom Right");
        bottomRightLabel.setTooltip(createTooltip("Bottom Right", AnchorLocation.WINDOW_TOP_LEFT));

        var flowPane = new FlowPane(
            BLOCK_HGAP, BLOCK_VGAP,
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

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setMinWidth(50);
        label.setMinHeight(50);
        label.setPadding(new Insets(10));
        label.setStyle("-fx-background-color:-color-accent-subtle;");
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

    private Tooltip createTooltip(String text, AnchorLocation arrowPos) {
        var tooltip = new Tooltip(text);
        tooltip.setAnchorLocation(arrowPos);
        return tooltip;
    }
}
