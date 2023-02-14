/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.CENTER_PILL;
import static atlantafx.base.theme.Styles.LEFT_PILL;
import static atlantafx.base.theme.Styles.RIGHT_PILL;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.util.Controls.toggleButton;
import static javafx.scene.layout.GridPane.REMAINING;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class ToggleButtonPage extends AbstractPage {

    public static final String NAME = "ToggleButton";

    @Override
    public String getName() {
        return NAME;
    }

    public ToggleButtonPage() {
        super();
        createView();
    }

    private void createView() {
        var grid = new GridPane();
        grid.setHgap(Page.PAGE_HGAP);
        grid.setVgap(Page.PAGE_VGAP);

        grid.add(basicSample(), 0, 0, REMAINING, 1);
        grid.add(wizardSample(), 0, 1);
        grid.add(iconOnlySample(), 1, 1);
        grid.add(disabledSample(), 0, 2);

        setUserContent(grid);
    }

    private SampleBlock basicSample() {
        var threeButtonGroup = new ToggleGroup();

        var leftPill = toggleButton("._left-pill", null, threeButtonGroup, true, LEFT_PILL);
        leftPill.setMnemonicParsing(true);

        var centerPill = toggleButton("._center-pill", null, threeButtonGroup, false, CENTER_PILL);
        centerPill.setMnemonicParsing(true);

        var rightPill = toggleButton("._right-pill", null, threeButtonGroup, false, RIGHT_PILL);
        rightPill.setMnemonicParsing(true);

        var threeButtonBox = new HBox(leftPill, centerPill, rightPill);

        var twoButtonGroup = new ToggleGroup();
        var twoButtonBox = new HBox(
            toggleButton(".left-pill", null, twoButtonGroup, true, LEFT_PILL),
            toggleButton(".right-pill", null, twoButtonGroup, false, RIGHT_PILL)
        );

        return new SampleBlock("Basic", new HBox(BLOCK_HGAP, threeButtonBox, twoButtonBox));
    }

    private SampleBlock wizardSample() {
        var group = new ToggleGroup();

        var prevBtn = new Button("\f", new FontIcon(Feather.CHEVRON_LEFT));
        prevBtn.getStyleClass().addAll(BUTTON_ICON, LEFT_PILL, "toggle-button");
        prevBtn.setOnAction(e -> {
            int selected = group.getToggles().indexOf(group.getSelectedToggle());
            if (selected > 0) {
                group.selectToggle(group.getToggles().get(selected - 1));
            }
        });

        var nextBtn = new Button("\f", new FontIcon(Feather.CHEVRON_RIGHT));
        nextBtn.getStyleClass().addAll(BUTTON_ICON, RIGHT_PILL, "toggle-button");
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
            if (val == null) {
                old.setSelected(true);
            }
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
