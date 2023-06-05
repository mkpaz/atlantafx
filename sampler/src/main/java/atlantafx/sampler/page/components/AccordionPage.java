/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.Resources;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public final class AccordionPage extends OutlinePage {

    public static final String NAME = "Accordion";

    @Override
    public String getName() {
        return NAME;
    }

    public AccordionPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A user interface component that allows you to display a list of expandable \
            items and only one item can be open at a time. Each item in the [i]Accordion[/i] \
            is made up of two parts, the header, and the content. The header is typically \
            a text or graphic and the content can be any valid JavaFX node. When a user \
            clicks on a header of an item, it will expand or collapse its content."""
        );
        addSection("Usage", usageExample());
        addSection("Dense", denseExample());
        addSection("Alternative Icon", altIconExample());
        addSection("Playground", playground());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        Supplier<Node> gen = () -> {
            var text = FAKER.lorem().paragraph();
            var textFlow = new TextFlow(new Text(text));
            textFlow.setMinHeight(100);
            VBox.setVgrow(textFlow, Priority.ALWAYS);
            return new VBox(textFlow);
        };

        var tp1 = new TitledPane("Item 1", gen.get());
        var tp2 = new TitledPane("Item 2", gen.get());
        var tp3 = new TitledPane("Item 3", gen.get());
        var accordion = new Accordion(tp1, tp2, tp3);
        //snippet_1:end

        var description = BBCodeParser.createFormattedText("""
            An [i]Accordion[/i] consists of a group of [i]TitlePanes[/i], \
            each of which can have its content expanded or collapsed."""
        );

        return new ExampleBox(accordion, new Snippet(getClass(), 1), description);
    }

    private ExampleBox denseExample() {
        //snippet_2:start
        Supplier<Node> gen = () -> {
            var text = FAKER.lorem().paragraph();
            var textFlow = new TextFlow(new Text(text));
            textFlow.setMinHeight(100);
            VBox.setVgrow(textFlow, Priority.ALWAYS);
            return new VBox(textFlow);
        };

        var tp1 = new TitledPane("Item 1", gen.get());
        tp1.getStyleClass().add(Styles.DENSE);

        var tp2 = new TitledPane("Item 2", gen.get());
        tp2.getStyleClass().add(Styles.DENSE);

        var tp3 = new TitledPane("Item 3", gen.get());
        tp3.getStyleClass().add(Styles.DENSE);

        var accordion = new Accordion(tp1, tp2, tp3);
        //snippet_2:end

        var description = BBCodeParser.createFormattedText("""
            If you need more compact view there's [code]Styles.DENSE[/code] for that."""
        );

        return new ExampleBox(accordion, new Snippet(getClass(), 2), description);
    }

    private ExampleBox altIconExample() {
        //snippet_3:start
        Supplier<Node> gen = () -> {
            var text = FAKER.lorem().paragraph();
            var textFlow = new TextFlow(new Text(text));
            textFlow.setMinHeight(100);
            VBox.setVgrow(textFlow, Priority.ALWAYS);
            return new VBox(textFlow);
        };

        var tp1 = new TitledPane("Item 1", gen.get());
        tp1.getStyleClass().add(Tweaks.ALT_ICON);

        var tp2 = new TitledPane("Item 2", gen.get());
        tp2.getStyleClass().add(Tweaks.ALT_ICON);

        var tp3 = new TitledPane("Item 3", gen.get());
        tp3.getStyleClass().add(Tweaks.ALT_ICON);

        var accordion = new Accordion(tp1, tp2, tp3);
        //snippet_3:end

        var description = BBCodeParser.createFormattedText("""
            There's also additional tweak [code]Tweaks.ALT_ICON[/code] to change header \
            arrow icon to the classic style."""
        );

        return new ExampleBox(accordion, new Snippet(getClass(), 3), description);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playground                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private VBox playground() {
        final var expandedProperty = new SimpleBooleanProperty(true);
        final var animatedProperty = new SimpleBooleanProperty(true);

        // == ACCORDION ==

        var labelBlock = new TitledPane("_Quote", new Label(FAKER.chuckNorris().fact()));
        labelBlock.setMnemonicParsing(true);
        labelBlock.animatedProperty().bind(animatedProperty);

        var textFlow = new TextFlow(new Text(
            String.join("\n\n", FAKER.lorem().paragraphs(10)))
        );
        textFlow.setPadding(new Insets(0, 10, 0, 0));
        var textScroll = new ScrollPane(textFlow);
        textScroll.setMinHeight(200);
        textScroll.setFitToWidth(true);
        var textBlock = new TitledPane("_Scrollable Text", textScroll);
        textBlock.setMnemonicParsing(true);
        textBlock.animatedProperty().bind(animatedProperty);

        var disabledBlock = new TitledPane("Disabled Block", null);
        disabledBlock.setDisable(true);

        var imageBlock = new TitledPane("_Image", new VBox(10,
            new ImageView(new Image(Resources.getResourceAsStream("images/fun/20_min_adventure.jpg"))),
            new TextFlow(new Text(FAKER.rickAndMorty().quote()))
        ));
        imageBlock.animatedProperty().bind(animatedProperty);
        imageBlock.setMnemonicParsing(true);

        var accordion = new Accordion(labelBlock, textBlock, disabledBlock, imageBlock);

        // prevents accordion from being completely collapsed
        accordion.expandedPaneProperty().addListener((obs, old, val) -> {
            boolean hasExpanded = accordion.getPanes().stream().anyMatch(TitledPane::isExpanded);
            if (expandedProperty.get() && !hasExpanded && old != null) {
                Platform.runLater(() -> accordion.setExpandedPane(old));
            }
        });
        accordion.setExpandedPane(accordion.getPanes().get(1));

        // == TOGGLES ==

        var animatedToggle = new ToggleSwitch("Animated");
        animatedProperty.bind(animatedToggle.selectedProperty());
        animatedToggle.setSelected(true);

        var expandedToggle = new ToggleSwitch("Always expanded");
        expandedProperty.bind(expandedToggle.selectedProperty());
        expandedToggle.setSelected(true);

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener(
            (obs, old, val) -> accordion.getPanes().forEach(p -> Styles.toggleStyleClass(p, Styles.DENSE))
        );

        var altIconToggle = new ToggleSwitch("Alt icon");
        altIconToggle.selectedProperty().addListener(
            (obs, old, val) -> accordion.getPanes().forEach(p -> Styles.toggleStyleClass(p, Tweaks.ALT_ICON))
        );

        var controls = new HBox(HGAP_20, animatedToggle, expandedToggle, denseToggle, altIconToggle);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(0, 0, 0, 2));

        var description = BBCodeParser.createFormattedText("""
            The playground demonstrates the most important [i]Accordion[/i] features \
            and also serves as an object for monkey testing."""
        );

        return new VBox(VGAP_10, description, accordion, controls);
    }
}
