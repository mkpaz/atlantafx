/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.BUTTON_OUTLINED;
import static atlantafx.base.theme.Styles.DANGER;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.base.theme.Styles.SUCCESS;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import java.util.stream.IntStream;
import javafx.geometry.Side;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuButtonPage extends AbstractPage {

    public static final String NAME = "MenuButton";
    private static final int PREF_WIDTH = 150;

    @Override
    public String getName() {
        return NAME;
    }

    public MenuButtonPage() {
        super();
        setUserContent(new VBox(
            Page.PAGE_VGAP,
            expandingHBox(basicSample(), iconOnlySample()),
            expandingHBox(coloredSample(), outlinedSample()),
            expandingHBox(popupSideSample(), noArrowSample()),
            disabledSample()

        ));
    }

    private SampleBlock basicSample() {
        var basicMenuBtn = new MenuButton("_Menu Button");
        basicMenuBtn.getItems().setAll(createItems(5));
        basicMenuBtn.setMnemonicParsing(true);

        var basicSplitBtn = new SplitMenuButton(createItems(5));
        basicSplitBtn.setText("_Split Menu Button");
        basicSplitBtn.setMnemonicParsing(true);

        var flatMenuBtn = new MenuButton("Flat");
        flatMenuBtn.getItems().setAll(createItems(5));
        flatMenuBtn.getStyleClass().add(FLAT);

        var flatSplitBtn = new SplitMenuButton(createItems(5));
        flatSplitBtn.setText("Flat");
        flatSplitBtn.getStyleClass().add(FLAT);

        var content = new FlowPane(BLOCK_HGAP, BLOCK_VGAP);
        content.getChildren().addAll(basicMenuBtn, basicSplitBtn, flatMenuBtn, flatSplitBtn);

        return new SampleBlock("Basic", content);
    }

    private SampleBlock coloredSample() {
        var accentMenuBtn = new MenuButton("_Accent");
        accentMenuBtn.getItems().setAll(createItems(5));
        accentMenuBtn.getStyleClass().add(ACCENT);
        accentMenuBtn.setMnemonicParsing(true);
        accentMenuBtn.setPrefWidth(PREF_WIDTH);

        var accentSplitBtn = new SplitMenuButton(createItems(5));
        accentSplitBtn.setText("Accent");
        accentSplitBtn.getStyleClass().add(ACCENT);
        accentSplitBtn.setPrefWidth(PREF_WIDTH);

        var successMenuBtn = new MenuButton("Success");
        successMenuBtn.getItems().setAll(createItems(5));
        successMenuBtn.setGraphic(new FontIcon(Feather.CHECK));
        successMenuBtn.getStyleClass().add(SUCCESS);
        successMenuBtn.setPrefWidth(PREF_WIDTH);

        var successSplitBtn = new SplitMenuButton(createItems(5));
        successSplitBtn.setGraphic(new FontIcon(Feather.CHECK));
        successSplitBtn.setText("_Success");
        successSplitBtn.getStyleClass().add(SUCCESS);
        successSplitBtn.setMnemonicParsing(true);
        successSplitBtn.setPrefWidth(PREF_WIDTH);

        var dangerMenuBtn = new MenuButton("Danger");
        dangerMenuBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerMenuBtn.getItems().setAll(createItems(5));
        dangerMenuBtn.getStyleClass().add(DANGER);
        dangerMenuBtn.setPrefWidth(PREF_WIDTH);

        var dangerSplitBtn = new SplitMenuButton(createItems(5));
        dangerSplitBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerSplitBtn.setText("_Danger");
        dangerSplitBtn.getStyleClass().add(DANGER);
        dangerSplitBtn.setMnemonicParsing(true);
        dangerSplitBtn.setPrefWidth(PREF_WIDTH);

        var content = new GridPane();
        content.setHgap(BLOCK_HGAP);
        content.setVgap(BLOCK_VGAP);
        content.add(accentMenuBtn, 0, 0);
        content.add(accentSplitBtn, 1, 0);
        content.add(successMenuBtn, 0, 1);
        content.add(successSplitBtn, 1, 1);
        content.add(dangerMenuBtn, 0, 2);
        content.add(dangerSplitBtn, 1, 2);

        return new SampleBlock("Colored", content);
    }

    private SampleBlock popupSideSample() {
        var topMenuBtn = new MenuButton("Top");
        topMenuBtn.getItems().setAll(createItems(5));
        topMenuBtn.setPopupSide(Side.TOP);

        var rightMenuBtn = new MenuButton("Right");
        rightMenuBtn.getItems().setAll(createItems(5));
        rightMenuBtn.setPopupSide(Side.RIGHT);
        rightMenuBtn.getStyleClass().add(ACCENT);

        var bottomMenuBtn = new MenuButton("Bottom");
        bottomMenuBtn.getItems().setAll(createItems(5));
        bottomMenuBtn.setPopupSide(Side.BOTTOM);
        bottomMenuBtn.getStyleClass().add(SUCCESS);

        var leftMenuBtn = new MenuButton("Left");
        leftMenuBtn.getItems().setAll(createItems(5));
        leftMenuBtn.setPopupSide(Side.LEFT);
        leftMenuBtn.getStyleClass().add(DANGER);

        var content = new FlowPane(BLOCK_HGAP, BLOCK_VGAP);
        content.getChildren().addAll(topMenuBtn, rightMenuBtn, bottomMenuBtn, leftMenuBtn);

        return new SampleBlock("Popup Side", content);
    }

    private SampleBlock iconOnlySample() {
        var basicMenuBtn = new MenuButton();
        basicMenuBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        basicMenuBtn.getItems().setAll(createItems(5));
        basicMenuBtn.getStyleClass().addAll(BUTTON_ICON);

        var basicSplitBtn = new SplitMenuButton(createItems(5));
        basicSplitBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        basicSplitBtn.getStyleClass().addAll(BUTTON_ICON);

        var accentMenuBtn = new MenuButton();
        accentMenuBtn.setGraphic(new FontIcon(Feather.MENU));
        accentMenuBtn.getItems().setAll(createItems(5));
        accentMenuBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);

        var accentSplitBtn = new SplitMenuButton(createItems(5));
        accentSplitBtn.setGraphic(new FontIcon(Feather.MENU));
        accentSplitBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);

        var content = new FlowPane(BLOCK_HGAP, BLOCK_VGAP);
        content.getChildren().addAll(basicMenuBtn, basicSplitBtn, accentMenuBtn, accentSplitBtn);

        return new SampleBlock("Icons", content);
    }

    private SampleBlock outlinedSample() {
        var accentMenuBtn = new MenuButton("Accen_t");
        accentMenuBtn.getItems().setAll(createItems(5));
        accentMenuBtn.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
        accentMenuBtn.setMnemonicParsing(true);
        accentMenuBtn.setPrefWidth(PREF_WIDTH);

        var accentSplitBtn = new SplitMenuButton(createItems(5));
        accentSplitBtn.setText("Accent");
        accentSplitBtn.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
        accentSplitBtn.setPrefWidth(PREF_WIDTH);

        var successMenuBtn = new MenuButton("S_uccess");
        successMenuBtn.getItems().setAll(createItems(5));
        successMenuBtn.setGraphic(new FontIcon(Feather.CHECK));
        successMenuBtn.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
        successMenuBtn.setMnemonicParsing(true);
        successMenuBtn.setPrefWidth(PREF_WIDTH);

        var successSplitBtn = new SplitMenuButton(createItems(5));
        successSplitBtn.setGraphic(new FontIcon(Feather.CHECK));
        successSplitBtn.setText("Success");
        successSplitBtn.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
        successSplitBtn.setPrefWidth(PREF_WIDTH);

        var dangerMenuBtn = new MenuButton("Danger");
        dangerMenuBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerMenuBtn.getItems().setAll(createItems(5));
        dangerMenuBtn.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
        dangerMenuBtn.setPrefWidth(PREF_WIDTH);

        var dangerSplitBtn = new SplitMenuButton(createItems(5));
        dangerSplitBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerSplitBtn.setText("Dan_ger");
        dangerSplitBtn.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
        dangerSplitBtn.setMnemonicParsing(true);
        dangerSplitBtn.setPrefWidth(PREF_WIDTH);

        var content = new GridPane();
        content.setHgap(BLOCK_HGAP);
        content.setVgap(BLOCK_VGAP);
        content.add(accentMenuBtn, 0, 0);
        content.add(accentSplitBtn, 1, 0);
        content.add(successMenuBtn, 0, 1);
        content.add(successSplitBtn, 1, 1);
        content.add(dangerMenuBtn, 0, 2);
        content.add(dangerSplitBtn, 1, 2);

        return new SampleBlock("Outlined", content);
    }

    private SampleBlock disabledSample() {
        var basicMenuBtn = new MenuButton("Menu Button");
        basicMenuBtn.getItems().setAll(createItems(5));
        basicMenuBtn.setDisable(true);

        var accentSplitBtn = new SplitMenuButton();
        accentSplitBtn.setText("Accent");
        accentSplitBtn.getItems().setAll(createItems(5));
        accentSplitBtn.getStyleClass().addAll(ACCENT);
        accentSplitBtn.setDisable(true);

        var flatMenuBtn = new MenuButton("Flat");
        flatMenuBtn.getItems().setAll(createItems(5));
        flatMenuBtn.getStyleClass().addAll(FLAT);
        flatMenuBtn.setDisable(true);

        var iconMenuBtn = new MenuButton();
        iconMenuBtn.getItems().setAll(createItems(5));
        iconMenuBtn.getStyleClass().addAll(BUTTON_ICON);
        iconMenuBtn.setDisable(true);

        var sample = new HBox(BLOCK_HGAP);
        sample.getChildren().addAll(basicMenuBtn, accentSplitBtn, flatMenuBtn, iconMenuBtn);

        return new SampleBlock("Disabled", sample);
    }

    private SampleBlock noArrowSample() {
        var basicMenuBtn = new MenuButton("_Menu Button");
        basicMenuBtn.getItems().setAll(createItems(5));
        basicMenuBtn.getStyleClass().addAll(Tweaks.NO_ARROW);

        var flatMenuBtn = new MenuButton("Flat");
        flatMenuBtn.getItems().setAll(createItems(5));
        flatMenuBtn.getStyleClass().addAll(FLAT, Tweaks.NO_ARROW);

        var iconMenuBtn = new MenuButton();
        iconMenuBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        iconMenuBtn.getItems().setAll(createItems(5));
        iconMenuBtn.getStyleClass().addAll(BUTTON_ICON, Tweaks.NO_ARROW);

        var content = new HBox(BLOCK_HGAP);
        content.getChildren().addAll(basicMenuBtn, flatMenuBtn, iconMenuBtn);

        return new SampleBlock("No Arrow", content);
    }

    @SuppressWarnings("SameParameterValue")
    private MenuItem[] createItems(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new MenuItem(FAKER.babylon5().character()))
            .toArray(MenuItem[]::new);
    }
}
