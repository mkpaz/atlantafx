/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class ToggleButtonPage extends OutlinePage {

    public static final String NAME = "ToggleButton";

    @Override
    public String getName() {
        return NAME;
    }

    public ToggleButtonPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A [i]ToggleButton[/i] is a special control having the ability to be selected."""
        );
        addSection("Usage", usageExample());
        addSection("Icon Only", iconOnlyExample());
        addSection("Segmented Group", segmentedGroupExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var singleBtn = new ToggleButton("Toggle");
        singleBtn.setSelected(true);

        // ~

        var leftBtn1 = new ToggleButton("._left-pill");
        leftBtn1.getStyleClass().add(Styles.LEFT_PILL);
        leftBtn1.setSelected(true);

        var rightBtn1 = new ToggleButton("._right-pill");
        rightBtn1.getStyleClass().add(Styles.RIGHT_PILL);

        var twoBtnGroup = new ToggleGroup();
        twoBtnGroup.getToggles().addAll(leftBtn1, rightBtn1);

        // ~

        var leftBtn2 = new ToggleButton("._left-pill");
        leftBtn2.getStyleClass().add(Styles.LEFT_PILL);
        leftBtn2.setMnemonicParsing(true);
        leftBtn2.setSelected(true);

        var centerBtn2 = new ToggleButton("._center-pill");
        centerBtn2.getStyleClass().add(Styles.CENTER_PILL);
        centerBtn2.setMnemonicParsing(true);

        var rightBtn2 = new ToggleButton("._right-pill");
        rightBtn2.getStyleClass().add(Styles.RIGHT_PILL);
        rightBtn2.setMnemonicParsing(true);

        var threeBtnGroup = new ToggleGroup();
        threeBtnGroup.getToggles().addAll(leftBtn2, centerBtn2, rightBtn2);
        //snippet_1:end

        var twoBtnBox = new HBox(leftBtn1, rightBtn1);
        twoBtnBox.setAlignment(Pos.CENTER_LEFT);

        var threeBtnBox = new HBox(leftBtn2, centerBtn2, rightBtn2);
        threeBtnBox.setAlignment(Pos.CENTER_LEFT);

        var box = new HBox(HGAP_30, singleBtn, twoBtnBox, threeBtnBox);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            The [i]ToggleButtons[/i] can also be placed in groups. When in groups, only one button \
            at a time within that group can be selected. Use [code]Styles.*_PILL[/code] style \
            classes if you want to toggle group to look like a single "segmented" button."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox iconOnlyExample() {
        //snippet_2:start
        var leftBtn = new ToggleButton(null, new FontIcon(Feather.BOLD));
        leftBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.LEFT_PILL
        );
        leftBtn.setSelected(true);

        var centerBtn = new ToggleButton(null, new FontIcon(Feather.ITALIC));
        centerBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.CENTER_PILL
        );

        var rightBtn = new ToggleButton(null, new FontIcon(Feather.UNDERLINE));
        rightBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.RIGHT_PILL
        );
        //snippet_2:end

        var box = new HBox(leftBtn, centerBtn, rightBtn);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            Sometimes it's desirable to create a button group that consists from icon \
            buttons only. This is also supported."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox segmentedGroupExample() {
        //snippet_3:start
        var musicBtn = new ToggleButton(
            "Music", new FontIcon(Feather.MUSIC)
        );
        musicBtn.getStyleClass().add(Styles.LEFT_PILL);
        musicBtn.setSelected(true);

        var imagesBtn = new ToggleButton(
            "Images", new FontIcon(Feather.IMAGE)
        );
        imagesBtn.getStyleClass().add(Styles.CENTER_PILL);

        var videosBtn = new ToggleButton(
            "Videos", new FontIcon(Feather.VIDEO)
        );
        videosBtn.getStyleClass().add(Styles.RIGHT_PILL);

        var group = new ToggleGroup();
        group.getToggles().addAll(musicBtn, imagesBtn, videosBtn);
        group.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val == null) {
                old.setSelected(true);
            }
        });

        var prevBtn = new Button(null, new FontIcon(Feather.CHEVRON_LEFT));
        prevBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.LEFT_PILL, "toggle-button"
        );
        prevBtn.setOnAction(evt -> {
            int sel = group.getToggles().indexOf(group.getSelectedToggle());
            if (sel > 0) {
                group.selectToggle(group.getToggles().get(sel - 1));
            }
        });

        var nextBtn = new Button(null, new FontIcon(Feather.CHEVRON_RIGHT));
        nextBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.RIGHT_PILL, "toggle-button"
        );
        nextBtn.setContentDisplay(ContentDisplay.RIGHT);
        nextBtn.setOnAction(evt -> {
            int sel = group.getToggles().indexOf(group.getSelectedToggle());
            if (sel < group.getToggles().size() - 1) {
                group.selectToggle(group.getToggles().get(sel + 1));
            }
        });

        var groupBox = new HBox(
            prevBtn, musicBtn, imagesBtn, videosBtn, nextBtn
        );
        groupBox.setAlignment(Pos.CENTER_LEFT);
        //snippet_3:end

        var description = BBCodeParser.createFormattedText("""
            This example demonstrates how to create a complex segmented button group."""
        );

        return new ExampleBox(groupBox, new Snippet(getClass(), 3), description);
    }
}
