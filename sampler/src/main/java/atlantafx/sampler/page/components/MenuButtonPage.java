/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Side;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.stream.IntStream;

import static atlantafx.base.theme.Styles.*;

public class MenuButtonPage extends AbstractPage {

    public static final String NAME = "MenuButton";

    @Override
    public String getName() { return NAME; }

    public MenuButtonPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(
                basicSample().getRoot(),
                coloredSample().getRoot(),
                popupSideSample().getRoot(),
                iconOnlySample().getRoot(),
                outlinedSample().getRoot(),
                disabledSample().getRoot(),
                noArrowSample().getRoot()
        );
    }

    private SampleBlock basicSample() {
        var basicMenuBtn = new MenuButton("_Menu Button");
        basicMenuBtn.getItems().setAll(menuItems(5));
        basicMenuBtn.setMnemonicParsing(true);
        basicMenuBtn.setOnAction(PRINT_SOURCE);

        var basicSplitBtn = new SplitMenuButton(menuItems(5));
        basicSplitBtn.setText("_Split Menu Button");
        basicSplitBtn.setMnemonicParsing(true);
        basicSplitBtn.setOnAction(PRINT_SOURCE);

        var flatMenuBtn = new MenuButton("Flat");
        flatMenuBtn.getItems().setAll(menuItems(5));
        flatMenuBtn.getStyleClass().add(FLAT);

        var flatSplitBtn = new SplitMenuButton(menuItems(5));
        flatSplitBtn.setText("Flat");
        flatSplitBtn.getStyleClass().add(FLAT);

        var content = new HBox(10);
        content.getChildren().addAll(basicMenuBtn, basicSplitBtn, flatMenuBtn, flatSplitBtn);

        return new SampleBlock("Basic", content);
    }

    private SampleBlock coloredSample() {
        var accentMenuBtn = new MenuButton("_Accent");
        accentMenuBtn.getItems().setAll(menuItems(5));
        accentMenuBtn.getStyleClass().add(ACCENT);
        accentMenuBtn.setMnemonicParsing(true);
        accentMenuBtn.setOnAction(PRINT_SOURCE);

        var accentSplitBtn = new SplitMenuButton(menuItems(5));
        accentSplitBtn.setText("Accent");
        accentSplitBtn.getStyleClass().add(ACCENT);

        var successMenuBtn = new MenuButton("Success");
        successMenuBtn.getItems().setAll(menuItems(5));
        successMenuBtn.setGraphic(new FontIcon(Feather.CHECK));
        successMenuBtn.getStyleClass().add(SUCCESS);

        var successSplitBtn = new SplitMenuButton(menuItems(5));
        successMenuBtn.setGraphic(new FontIcon(Feather.CHECK));
        successSplitBtn.setText("_Success");
        successSplitBtn.getStyleClass().add(SUCCESS);
        successSplitBtn.setMnemonicParsing(true);
        successSplitBtn.setOnAction(PRINT_SOURCE);

        var dangerMenuBtn = new MenuButton("Danger");
        dangerMenuBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerMenuBtn.getItems().setAll(menuItems(5));
        dangerMenuBtn.getStyleClass().add(DANGER);

        var dangerSplitBtn = new SplitMenuButton(menuItems(5));
        dangerSplitBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerSplitBtn.setText("_Danger");
        dangerSplitBtn.getStyleClass().add(DANGER);
        dangerSplitBtn.setMnemonicParsing(true);
        dangerSplitBtn.setOnAction(PRINT_SOURCE);

        var content = new HBox(10);
        content.getChildren().addAll(
                accentMenuBtn, accentSplitBtn,
                successMenuBtn, successSplitBtn,
                dangerMenuBtn, dangerSplitBtn
        );

        return new SampleBlock("Colored", content);
    }

    private SampleBlock popupSideSample() {
        var topMenuBtn = new MenuButton("Top");
        topMenuBtn.getItems().setAll(menuItems(5));
        topMenuBtn.setPopupSide(Side.TOP);

        var rightMenuBtn = new MenuButton("Right");
        rightMenuBtn.getItems().setAll(menuItems(5));
        rightMenuBtn.setPopupSide(Side.RIGHT);
        rightMenuBtn.getStyleClass().add(ACCENT);

        var bottomMenuBtn = new MenuButton("Bottom");
        bottomMenuBtn.setGraphic(new FontIcon(Feather.CHECK));
        bottomMenuBtn.getItems().setAll(menuItems(5));
        bottomMenuBtn.setPopupSide(Side.BOTTOM);
        bottomMenuBtn.getStyleClass().add(SUCCESS);

        var leftMenuBtn = new MenuButton("Left");
        leftMenuBtn.setGraphic(new FontIcon(Feather.TRASH));
        leftMenuBtn.getItems().setAll(menuItems(5));
        leftMenuBtn.setPopupSide(Side.LEFT);
        leftMenuBtn.getStyleClass().add(DANGER);

        var content = new FlowPane(10, 10);
        content.getChildren().addAll(topMenuBtn, rightMenuBtn, bottomMenuBtn, leftMenuBtn);

        return new SampleBlock("Popup Side", content);
    }

    private SampleBlock iconOnlySample() {
        var basicMenuBtn = new MenuButton();
        basicMenuBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        basicMenuBtn.getItems().setAll(menuItems(5));
        basicMenuBtn.getStyleClass().addAll(BUTTON_ICON);

        var basicSplitBtn = new SplitMenuButton(menuItems(5));
        basicSplitBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        basicSplitBtn.getStyleClass().addAll(BUTTON_ICON);

        var accentMenuBtn = new MenuButton();
        accentMenuBtn.setGraphic(new FontIcon(Feather.MENU));
        accentMenuBtn.getItems().setAll(menuItems(5));
        accentMenuBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);

        var accentSplitBtn = new SplitMenuButton(menuItems(5));
        accentSplitBtn.setGraphic(new FontIcon(Feather.MENU));
        accentSplitBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);

        var content = new FlowPane(10, 10);
        content.getChildren().addAll(basicMenuBtn, basicSplitBtn, accentMenuBtn, accentSplitBtn);

        return new SampleBlock("Icons", content);
    }

    private SampleBlock outlinedSample() {
        var accentMenuBtn = new MenuButton("Accen_t");
        accentMenuBtn.getItems().setAll(menuItems(5));
        accentMenuBtn.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
        accentMenuBtn.setMnemonicParsing(true);
        accentMenuBtn.setOnAction(PRINT_SOURCE);

        var accentSplitBtn = new SplitMenuButton(menuItems(5));
        accentSplitBtn.setText("Accent");
        accentSplitBtn.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);

        var successMenuBtn = new MenuButton("S_uccess");
        successMenuBtn.getItems().setAll(menuItems(5));
        successMenuBtn.setGraphic(new FontIcon(Feather.CHECK));
        successMenuBtn.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
        successMenuBtn.setMnemonicParsing(true);
        successMenuBtn.setOnAction(PRINT_SOURCE);

        var successSplitBtn = new SplitMenuButton(menuItems(5));
        successMenuBtn.setGraphic(new FontIcon(Feather.CHECK));
        successSplitBtn.setText("Success");
        successSplitBtn.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);

        var dangerMenuBtn = new MenuButton("Danger");
        dangerMenuBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerMenuBtn.getItems().setAll(menuItems(5));
        dangerMenuBtn.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);

        var dangerSplitBtn = new SplitMenuButton(menuItems(5));
        dangerSplitBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerSplitBtn.setText("Dan_ger");
        dangerSplitBtn.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
        dangerSplitBtn.setMnemonicParsing(true);
        dangerSplitBtn.setOnAction(PRINT_SOURCE);

        var content = new HBox(10);
        content.getChildren().addAll(
                accentMenuBtn, accentSplitBtn,
                successMenuBtn, successSplitBtn,
                dangerMenuBtn, dangerSplitBtn
        );

        return new SampleBlock("Outlined", content);
    }

    private SampleBlock disabledSample() {
        var basicMenuBtn = new MenuButton("Menu Button");
        basicMenuBtn.getItems().setAll(menuItems(5));
        basicMenuBtn.setDisable(true);

        var accentSplitBtn = new SplitMenuButton();
        accentSplitBtn.setText("Accent");
        accentSplitBtn.getItems().setAll(menuItems(5));
        accentSplitBtn.getStyleClass().addAll(ACCENT);
        accentSplitBtn.setDisable(true);

        var flatMenuBtn = new MenuButton("Flat");
        flatMenuBtn.getItems().setAll(menuItems(5));
        flatMenuBtn.getStyleClass().addAll(FLAT);
        flatMenuBtn.setDisable(true);

        var iconMenuBtn = new MenuButton();
        iconMenuBtn.getItems().setAll(menuItems(5));
        iconMenuBtn.getStyleClass().addAll(BUTTON_ICON);
        iconMenuBtn.setDisable(true);

        var sample = new HBox(10);
        sample.getChildren().addAll(basicMenuBtn, accentSplitBtn, flatMenuBtn, iconMenuBtn);

        return new SampleBlock("Disabled", sample);
    }

    private SampleBlock noArrowSample() {
        var basicMenuBtn = new MenuButton("_Menu Button");
        basicMenuBtn.getItems().setAll(menuItems(5));
        basicMenuBtn.getStyleClass().addAll(Tweaks.NO_ARROW);

        var flatMenuBtn = new MenuButton("Flat");
        flatMenuBtn.getItems().setAll(menuItems(5));
        flatMenuBtn.getStyleClass().addAll(FLAT, Tweaks.NO_ARROW);

        var iconMenuBtn = new MenuButton();
        iconMenuBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        iconMenuBtn.getItems().setAll(menuItems(5));
        iconMenuBtn.getStyleClass().addAll(BUTTON_ICON, Tweaks.NO_ARROW);

        var content = new HBox(10);
        content.getChildren().addAll(basicMenuBtn, flatMenuBtn, iconMenuBtn);

        return new SampleBlock("No Arrow", content);
    }

    @SuppressWarnings("SameParameterValue")
    private MenuItem[] menuItems(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new MenuItem(FAKER.babylon5().character()))
                .toArray(MenuItem[]::new);
    }
}
