/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.sampler.util.Controls.toggleButton;
import static javafx.scene.layout.GridPane.REMAINING;

public class ToggleButtonPage extends AbstractPage {

    public static final String NAME = "ToggleButton";

    @Override
    public String getName() { return NAME; }

    public ToggleButtonPage() {
        super();
        createView();
    }

    private void createView() {
        var grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(40);

        grid.add(basicSample().getRoot(), 0, 0, REMAINING, 1);
        grid.add(wizardSample().getRoot(), 0, 1);
        grid.add(iconOnlySample().getRoot(), 1, 1);
        grid.add(disabledSample().getRoot(), 0, 2);

        userContent.getChildren().addAll(grid);
    }

    private SampleBlock basicSample() {
        var threeButtonGroup = new ToggleGroup();

        var leftPill = toggleButton("._left-pill", null, threeButtonGroup, true, LEFT_PILL);
        leftPill.setMnemonicParsing(true);
        leftPill.setOnAction(PRINT_SOURCE);

        var centerPill = toggleButton("._center-pill", null, threeButtonGroup, false, CENTER_PILL);
        centerPill.setMnemonicParsing(true);
        centerPill.setOnAction(PRINT_SOURCE);

        var rightPill = toggleButton("._right-pill", null, threeButtonGroup, false, RIGHT_PILL);
        rightPill.setMnemonicParsing(true);
        rightPill.setOnAction(PRINT_SOURCE);

        var threeButtonBox = new HBox(leftPill, centerPill, rightPill);

        var twoButtonGroup = new ToggleGroup();
        var twoButtonBox = new HBox(
                toggleButton(".left-pill", null, twoButtonGroup, true, LEFT_PILL),
                toggleButton(".right-pill", null, twoButtonGroup, false, RIGHT_PILL)
        );

        var content = new HBox(10);
        content.getChildren().setAll(threeButtonBox, twoButtonBox);

        return new SampleBlock("Basic", content);
    }

    private SampleBlock wizardSample() {
        var group = new ToggleGroup();

        var prevBtn = new Button("\f");
        prevBtn.getStyleClass().addAll(BUTTON_ICON, LEFT_PILL);
        prevBtn.setGraphic(new FontIcon(Feather.CHEVRON_LEFT));
        prevBtn.setOnAction(e -> {
            int selected = group.getToggles().indexOf(group.getSelectedToggle());
            if (selected > 0) {
                group.selectToggle(group.getToggles().get(selected - 1));
            }
        });

        var nextBtn = new Button("\f");
        nextBtn.getStyleClass().addAll(BUTTON_ICON, RIGHT_PILL);
        nextBtn.setGraphic(new FontIcon(Feather.CHEVRON_RIGHT));
        nextBtn.setContentDisplay(ContentDisplay.RIGHT);
        nextBtn.setOnAction(e -> {
            int selected = group.getToggles().indexOf(group.getSelectedToggle());
            if (selected < group.getToggles().size() - 1) {
                group.selectToggle(group.getToggles().get(selected + 1));
            }
        });

        var wizard = new HBox(
                prevBtn,
                toggleButton("Music", Feather.MUSIC, group, true, CENTER_PILL),
                toggleButton("Images", Feather.IMAGE, group, false, CENTER_PILL),
                toggleButton("Videos", Feather.VIDEO, group, false, CENTER_PILL),
                nextBtn
        );
        group.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val == null) { old.setSelected(true); }
        });

        return new SampleBlock("Wizard", wizard);
    }

    private SampleBlock iconOnlySample() {
        var icons = new HBox(
                toggleButton("", Feather.BOLD, null, true, BUTTON_ICON, LEFT_PILL),
                toggleButton("", Feather.ITALIC, null, false, BUTTON_ICON, CENTER_PILL),
                toggleButton("", Feather.UNDERLINE, null, false, BUTTON_ICON, RIGHT_PILL)
        );

        return new SampleBlock("Icon only", icons);
    }

    private SampleBlock disabledSample() {
        var group = new ToggleGroup();
        var content = new HBox(
                toggleButton(".left-pill", null, group, false, LEFT_PILL),
                toggleButton(".center-pill", null, group, false, CENTER_PILL),
                toggleButton(".right-pill", null, group, true, RIGHT_PILL)
        );
        content.getChildren().get(0).setDisable(true);
        content.getChildren().get(1).setDisable(true);

        return new SampleBlock("Disabled", content);
    }
}
