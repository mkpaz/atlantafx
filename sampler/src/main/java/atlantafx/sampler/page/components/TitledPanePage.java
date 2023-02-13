/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.DENSE;
import static atlantafx.base.theme.Styles.ELEVATED_2;
import static atlantafx.base.theme.Styles.INTERACTIVE;
import static atlantafx.base.theme.Styles.toggleStyleClass;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;
import static javafx.geometry.HPos.RIGHT;
import static javafx.scene.layout.Priority.NEVER;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TitledPanePage extends AbstractPage {

    public static final String NAME = "TitledPane";
    private static final String ELEVATED_PREFIX = "elevated-";

    @Override
    public String getName() {
        return NAME;
    }

    public TitledPanePage() {
        super();
        createView();
    }

    private void createView() {
        var samples = new HBox(
            PAGE_HGAP,
            interactivePane(),
            disabledPane(),
            untitledPane()
        );
        samples.getChildren().forEach(c -> ((TitledPane) c).setPrefSize(500, 120));

        setUserContent(new VBox(
            Page.PAGE_VGAP,
            createPlayground(),
            samples
        ));
    }

    private TitledPane createPlayground() {
        var playground = new TitledPane();
        playground.setText("_Playground");
        playground.setMnemonicParsing(true);
        playground.getStyleClass().add(ELEVATED_2);

        var textFlow = new TextFlow(new Text(FAKER.lorem().paragraph(10)));
        textFlow.setMinHeight(Region.USE_PREF_SIZE);
        textFlow.setMaxHeight(Region.USE_PREF_SIZE);
        textFlow.setLineSpacing(5);

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
            playground.getStyleClass().removeAll(
                playground.getStyleClass().stream().filter(c -> c.startsWith(ELEVATED_PREFIX)).toList()
            );
            if (val == null) {
                return;
            }
            int level = val.intValue();
            if (level > 0) {
                playground.getStyleClass().add(ELEVATED_PREFIX + level);
            }
        });

        // NOTE:
        // Disabling 'collapsible' property leads to incorrect title layout,
        // for some reason it still preserves arrow button gap. #javafx-bug
        var collapseToggle = new ToggleSwitch("Collapsible");
        collapseToggle.setSelected(true);
        playground.collapsibleProperty().bind(collapseToggle.selectedProperty());

        var animateToggle = new ToggleSwitch("Animated");
        animateToggle.setSelected(true);
        playground.animatedProperty().bind(animateToggle.selectedProperty());

        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(playground, DENSE));

        var altIconToggle = new ToggleSwitch("Alt icon");
        altIconToggle.selectedProperty().addListener((obs, old, val) -> toggleStyleClass(playground, Tweaks.ALT_ICON));

        var toggles = new GridPane();
        toggles.setVgap(10);
        toggles.setHgap(BLOCK_HGAP);
        toggles.getColumnConstraints().setAll(
            new ColumnConstraints(-1, -1, -1, NEVER, RIGHT, false),
            new ColumnConstraints(-1, -1, -1, NEVER, RIGHT, false)
        );
        toggles.add(collapseToggle, 0, 0);
        toggles.add(animateToggle, 1, 0);
        toggles.add(denseToggle, 0, 1);
        toggles.add(altIconToggle, 1, 1);

        var controls = new HBox(BLOCK_HGAP);
        controls.setMinHeight(80);
        controls.setFillHeight(false);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().setAll(
            new Label("Elevation"),
            elevationSlider,
            new Spacer(),
            toggles
        );

        var content = new VBox(BLOCK_VGAP, textFlow, controls);
        VBox.setVgrow(textFlow, Priority.ALWAYS);
        playground.setContent(content);

        return playground;
    }

    private TitledPane interactivePane() {
        var titledPane = new TitledPane("Interactive", new Text("Hover here."));
        titledPane.setCollapsible(false);
        titledPane.getStyleClass().add(INTERACTIVE);
        return titledPane;
    }

    private TitledPane disabledPane() {
        var titledPane = new TitledPane("Disabled", new CheckBox("This checkbox is disabled."));
        titledPane.setCollapsible(false);
        titledPane.setDisable(true);
        return titledPane;
    }

    private TitledPane untitledPane() {
        var titledPane = new TitledPane("This pane has no title.", new Text());
        titledPane.setCollapsible(false);
        return titledPane;
    }
}
