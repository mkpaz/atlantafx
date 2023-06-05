/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static javafx.geometry.HPos.RIGHT;
import static javafx.scene.layout.Priority.NEVER;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public final class TitledPanePage extends OutlinePage {

    public static final String NAME = "TitledPane";

    @Override
    public String getName() {
        return NAME;
    }

    public TitledPanePage() {
        super();

        addPageHeader();
        addFormattedText("""
            [i]TitledPane[/i] is a panel with a title that can be opened and closed. \
            It holds one or more user interface elements and you can expand and collapse it. \
            Some of the [i]TitledPane[/i] can be applied to the [i]Accordion[/i] as well."""
        );
        addSection("Usage", usageExample());
        addSection("Elevation", elevationExample());
        addSection("Dense", denseExample());
        addSection("Alternative Icon", altIconExample());
        addSection("Playground", playground());

        var dummyBox = new HBox();
        dummyBox.setMinHeight(10);
        addNode(dummyBox);
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var tp1 = new TitledPane(
            "Header",
            new Label("Content")
        );

        var tp2 = new TitledPane("Header", new Label("Content"));
        tp2.setCollapsible(false);
        //snippet_1:end

        tp1.setPrefWidth(200);
        tp2.setPrefWidth(200);

        var box = new HBox(HGAP_20, tp1, tp2);
        var description = BBCodeParser.createFormattedText("""
            The panel in a [i]TitledPane[/i] can be any node such as controls or groups of nodes \
            added to a layout container. Note that whilst [i]TitledPane[/i] extends from [i]Labeled[/i], \
            the inherited properties are used to manipulate the [i]TitledPane[/i] header, not the \
            content area itself."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox elevationExample() {
        //snippet_2:start
        var tp0 = new TitledPane(
            "Default",
            new Label("Content")
        );

        var tp1 = new TitledPane(
            "Styles.ELEVATED_1",
            new Label("Content")
        );
        tp1.getStyleClass().add(Styles.ELEVATED_1);

        var tp2 = new TitledPane(
            "Styles.ELEVATED_2",
            new Label("Content")
        );
        tp2.getStyleClass().add(Styles.ELEVATED_2);

        var tp3 = new TitledPane(
            "Styles.ELEVATED_3",
            new Label("Content")
        );
        tp3.getStyleClass().add(Styles.ELEVATED_3);

        var tp4 = new TitledPane(
            "Styles.ELEVATED_4",
            new Label("Content")
        );
        tp4.getStyleClass().add(Styles.ELEVATED_4);

        var tp5 = new TitledPane(
            "Styles.INTERACTIVE",
            new Label("Hover here")
        );
        tp5.getStyleClass().add(Styles.INTERACTIVE);
        //snippet_2:end

        tp0.setPrefWidth(300);
        tp1.setPrefWidth(300);
        tp2.setPrefWidth(300);
        tp3.setPrefWidth(300);
        tp4.setPrefWidth(300);
        tp5.setPrefWidth(300);

        var box = new FlowPane(HGAP_30, VGAP_20, tp0, tp1, tp2, tp3, tp4, tp5);
        var description = BBCodeParser.createFormattedText("""
            With [code]Styles.ELEVATED_N[/code] or [code]Styles.INTERACTIVE[/code] styles classes \
            you can add raised shadow effect to the [i]TitledPane[/i]."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox denseExample() {
        //snippet_3:start
        var tp = new TitledPane(
            "Header",
            new Label("Content")
        );
        tp.getStyleClass().add(Styles.DENSE);
        //snippet_3:end

        tp.setPrefWidth(200);

        var box = new HBox(tp);
        var description = BBCodeParser.createFormattedText("""
            If you need more compact view there's [code]Styles.DENSE[/code] for that."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox altIconExample() {
        //snippet_3:start
        var tp = new TitledPane(
            "Header",
            new Label("Content")
        );
        tp.getStyleClass().add(Tweaks.ALT_ICON);
        //snippet_3:end

        tp.setPrefWidth(200);

        var box = new HBox(tp);
        var description = BBCodeParser.createFormattedText("""
            There's additional tweak [code]Tweaks.ALT_ICON[/code] to change header \
            arrow icon to the classic style."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playground                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private VBox playground() {
        var titledPane = new TitledPane();
        titledPane.setText("_Header");
        titledPane.setMnemonicParsing(true);
        titledPane.getStyleClass().add(Styles.ELEVATED_2);

        var textFlow = new TextFlow(new Text(
            FAKER.lorem().paragraph(10))
        );
        textFlow.setMinHeight(Region.USE_PREF_SIZE);
        textFlow.setMaxHeight(Region.USE_PREF_SIZE);
        VBox.setVgrow(textFlow, Priority.ALWAYS);
        textFlow.setLineSpacing(5);

        String elevationPrefix = "elevated-";
        var elevationSlider = new Slider(0, 4, 2);
        elevationSlider.setShowTickLabels(true);
        elevationSlider.setShowTickMarks(true);
        elevationSlider.setMajorTickUnit(1);
        elevationSlider.setBlockIncrement(1);
        elevationSlider.setMinorTickCount(0);
        elevationSlider.setSnapToTicks(true);
        elevationSlider.setMinWidth(150);
        elevationSlider.setMaxWidth(150);
        elevationSlider.valueProperty().addListener((obs, old, val) -> {
            titledPane.getStyleClass().removeAll(
                titledPane.getStyleClass().stream()
                    .filter(c -> c.startsWith(elevationPrefix))
                    .toList()
            );
            if (val == null) {
                return;
            }
            int level = val.intValue();
            if (level > 0) {
                titledPane.getStyleClass().add(elevationPrefix + level);
            }
        });

        // NOTE:
        // Disabling 'collapsible' property leads to incorrect title layout,
        // for some reason it still preserves arrow button gap. #javafx-bug
        var collapseToggle = new ToggleSwitch("Collapsible");
        collapseToggle.setSelected(true);
        titledPane.collapsibleProperty().bind(collapseToggle.selectedProperty());

        var animateToggle = new ToggleSwitch("Animated");
        animateToggle.setSelected(true);
        titledPane.animatedProperty().bind(animateToggle.selectedProperty());

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty()
            .addListener((obs, old, val) -> Styles.toggleStyleClass(titledPane, Styles.DENSE));

        var altIconToggle = new ToggleSwitch("Alt icon");
        altIconToggle.selectedProperty()
            .addListener((obs, old, val) -> Styles.toggleStyleClass(titledPane, Tweaks.ALT_ICON));

        var toggles = new GridPane();
        toggles.setHgap(HGAP_20);
        toggles.setVgap(VGAP_10);
        toggles.getColumnConstraints().setAll(
            new ColumnConstraints(-1, -1, -1, NEVER, RIGHT, false),
            new ColumnConstraints(-1, -1, -1, NEVER, RIGHT, false)
        );
        toggles.addRow(0, collapseToggle, animateToggle);
        toggles.addRow(1, denseToggle, altIconToggle);

        var elevationBox = new HBox(10, new Label("Elevation"), elevationSlider);

        var controls = new HBox(VGAP_10);
        controls.setMinHeight(100);
        controls.setFillHeight(false);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().setAll(elevationBox, new Spacer(), toggles);

        var content = new VBox(VGAP_10, textFlow, controls);
        titledPane.setContent(content);

        var description = BBCodeParser.createFormattedText("""
            The playground demonstrates the most important [i]TitledPane[/i] features \
            and also serves as an object for monkey testing."""
        );

        return new VBox(VGAP_10, description, titledPane);
    }
}
