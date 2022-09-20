/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.sampler.Resources;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AccordionPage extends AbstractPage {

    public static final String NAME = "Accordion";

    @Override
    public String getName() { return NAME; }

    private final BooleanProperty expandedProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty animatedProperty = new SimpleBooleanProperty(true);

    public AccordionPage() {
        super();

        var sample = new SampleBlock(
                "Playground",
                new VBox(SampleBlock.BLOCK_VGAP, createControls(), createPlayground())
        );
        sample.setFillHeight(true);
        setUserContent(sample);
    }

    private HBox createControls() {
        var animatedToggle = new ToggleSwitch("Animated");
        animatedProperty.bind(animatedToggle.selectedProperty());
        animatedToggle.setSelected(true);

        var expandedToggle = new ToggleSwitch("Always expanded");
        expandedProperty.bind(expandedToggle.selectedProperty());
        expandedToggle.setSelected(true);

        var controls = new HBox(SampleBlock.BLOCK_HGAP, animatedToggle, expandedToggle);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(0, 0, 0, 2));

        return controls;
    }

    private Accordion createPlayground() {
        var textBlockContent = new Label(FAKER.chuckNorris().fact());
        var textBlock = new TitledPane("_Quote", textBlockContent);
        textBlock.setMnemonicParsing(true);
        textBlock.animatedProperty().bind(animatedProperty);

        var textFlow = new TextFlow(new Text(String.join("\n\n", FAKER.lorem().paragraphs(10))));
        textFlow.setPadding(new Insets(0, 10, 0, 0));
        var scrollTextBlockContent = new ScrollPane(textFlow);
        scrollTextBlockContent.setMinHeight(200);
        scrollTextBlockContent.setFitToWidth(true);
        var scrollableTextBlock = new TitledPane("_Scrollable Text", scrollTextBlockContent);
        scrollableTextBlock.setMnemonicParsing(true);
        scrollableTextBlock.animatedProperty().bind(animatedProperty);

        var disabledBlock = new TitledPane("Disabled Block", null);
        disabledBlock.setDisable(true);

        var imageBlock = new TitledPane("_Image", new VBox(10,
                new ImageView(new Image(Resources.getResourceAsStream("images/20_min_adventure.jpg"))),
                new TextFlow(new Text(FAKER.rickAndMorty().quote()))
        ));
        imageBlock.animatedProperty().bind(animatedProperty);
        imageBlock.setMnemonicParsing(true);

        // ~

        var accordion = new Accordion(
                textBlock,
                scrollableTextBlock,
                disabledBlock,
                imageBlock
        );

        // prevents accordion from being completely collapsed
        accordion.expandedPaneProperty().addListener((obs, old, val) -> {
            boolean hasExpanded = accordion.getPanes().stream().anyMatch(TitledPane::isExpanded);
            if (expandedProperty.get() && !hasExpanded && old != null) {
                Platform.runLater(() -> accordion.setExpandedPane(old));
            }
        });
        accordion.setExpandedPane(accordion.getPanes().get(1));

        return accordion;
    }
}
