/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static atlantafx.base.theme.Styles.*;

public class ButtonPage extends AbstractPage {

    public static final String NAME = "Button";

    @Override
    public String getName() {
        return NAME;
    }

    public ButtonPage() {
        super();
        createView();
    }

    private void createView() {
        var grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(40);
        grid.add(basicSamples().getRoot(), 0, 0);
        grid.add(iconOnlySamples().getRoot(), 1, 0);
        grid.add(coloredSamples().getRoot(), 0, 1);
        grid.add(circularButtons().getRoot(), 1, 1);
        grid.add(outlinedSamples().getRoot(), 0, 2);
        grid.add(roundedSamples().getRoot(), 1, 2);
        grid.add(disabledSample().getRoot(), 0, 3);

        userContent.getChildren().addAll(grid);
    }

    private SampleBlock basicSamples() {
        var basicBtn = new Button("_Basic");
        basicBtn.setMnemonicParsing(true);
        basicBtn.setOnAction(PRINT_SOURCE);

        var defaultBtn = new Button("_Default");
        defaultBtn.setDefaultButton(true);
        defaultBtn.setMnemonicParsing(true);
        defaultBtn.setOnAction(PRINT_SOURCE);

        var flatBtn = new Button("_Flat");
        flatBtn.getStyleClass().add(FLAT);
        flatBtn.setOnAction(PRINT_SOURCE);

        var content = new HBox(10);
        content.getChildren().addAll(basicBtn, defaultBtn, flatBtn);

        return new SampleBlock("Basic", content);
    }

    private SampleBlock coloredSamples() {
        var accentBtn = new Button("_Accent");
        accentBtn.getStyleClass().add(ACCENT);
        accentBtn.setMnemonicParsing(true);
        accentBtn.setOnAction(PRINT_SOURCE);

        var successBtn = new Button("_Success", new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().add(SUCCESS);
        successBtn.setMnemonicParsing(true);
        successBtn.setOnAction(PRINT_SOURCE);

        var dangerBtn = new Button("Da_nger", new FontIcon(Feather.TRASH));
        dangerBtn.getStyleClass().add(DANGER);
        dangerBtn.setContentDisplay(ContentDisplay.RIGHT);
        dangerBtn.setMnemonicParsing(true);
        dangerBtn.setOnAction(PRINT_SOURCE);

        var content = new HBox(10);
        content.getChildren().addAll(accentBtn, successBtn, dangerBtn);

        return new SampleBlock("Colored", content);
    }

    private SampleBlock iconOnlySamples() {
        var basicBtn = new Button("", new FontIcon(Feather.MORE_HORIZONTAL));
        basicBtn.getStyleClass().addAll(BUTTON_ICON);

        var accentBtn = new Button("", new FontIcon(Feather.MENU));
        accentBtn.getStyleClass().addAll(BUTTON_ICON, ACCENT);

        var successBtn = new Button("", new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().addAll(BUTTON_ICON, SUCCESS);

        var dangerBtn = new Button("", new FontIcon(Feather.TRASH));
        dangerBtn.getStyleClass().addAll(BUTTON_ICON, BUTTON_OUTLINED, DANGER);

        var flatAccentBtn = new Button("", new FontIcon(Feather.MIC));
        flatAccentBtn.getStyleClass().addAll(BUTTON_ICON, FLAT, ACCENT);

        var flatSuccessBtn = new Button("", new FontIcon(Feather.USER));
        flatSuccessBtn.getStyleClass().addAll(BUTTON_ICON, FLAT, SUCCESS);

        var flatDangerBtn = new Button("", new FontIcon(Feather.CROSSHAIR));
        flatDangerBtn.getStyleClass().addAll(BUTTON_ICON, FLAT, DANGER);

        var content = new HBox(10);
        content.getChildren().addAll(basicBtn, accentBtn, successBtn, dangerBtn,
                flatAccentBtn, flatSuccessBtn, flatDangerBtn
        );

        return new SampleBlock("Icon only", content);
    }

    private SampleBlock circularButtons() {
        var basicBtn = new Button("", new FontIcon(Feather.MORE_HORIZONTAL));
        basicBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        basicBtn.setShape(new Circle(50));

        var accentBtn = new Button("", new FontIcon(Feather.MENU));
        accentBtn.getStyleClass().addAll(BUTTON_CIRCLE, ACCENT);
        accentBtn.setShape(new Circle(50));

        var successBtn = new Button("", new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().addAll(BUTTON_CIRCLE, SUCCESS);
        successBtn.setShape(new Circle(50));

        var dangerBtn = new Button("", new FontIcon(Feather.TRASH));
        dangerBtn.getStyleClass().addAll(BUTTON_CIRCLE, BUTTON_OUTLINED, DANGER);
        dangerBtn.setShape(new Circle(50));

        var flatAccentBtn = new Button("", new FontIcon(Feather.MIC));
        flatAccentBtn.getStyleClass().addAll(BUTTON_CIRCLE, FLAT, ACCENT);
        flatAccentBtn.setShape(new Circle(50));

        var flatSuccessBtn = new Button("", new FontIcon(Feather.USER));
        flatSuccessBtn.getStyleClass().addAll(BUTTON_CIRCLE, FLAT, SUCCESS);
        flatSuccessBtn.setShape(new Circle(50));

        var flatDangerBtn = new Button("", new FontIcon(Feather.CROSSHAIR));
        flatDangerBtn.getStyleClass().addAll(BUTTON_CIRCLE, FLAT, DANGER);
        flatDangerBtn.setShape(new Circle(50));

        var content = new HBox(10);
        content.getChildren().addAll(basicBtn, accentBtn, successBtn, dangerBtn,
                flatAccentBtn, flatSuccessBtn, flatDangerBtn
        );

        return new SampleBlock("Circular", content);
    }

    private SampleBlock outlinedSamples() {
        var accentBtn = new Button("Accen_t");
        accentBtn.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
        accentBtn.setMnemonicParsing(true);
        accentBtn.setOnAction(PRINT_SOURCE);

        var successBtn = new Button("S_uccess", new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
        successBtn.setMnemonicParsing(true);
        successBtn.setOnAction(PRINT_SOURCE);

        var dangerBtn = new Button("Dan_ger", new FontIcon(Feather.TRASH));
        dangerBtn.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
        dangerBtn.setContentDisplay(ContentDisplay.RIGHT);
        dangerBtn.setMnemonicParsing(true);
        dangerBtn.setOnAction(PRINT_SOURCE);

        var content = new HBox(10);
        content.getChildren().addAll(accentBtn, successBtn, dangerBtn);

        return new SampleBlock("Outlined", content);
    }

    private SampleBlock roundedSamples() {
        var basicBtn = new Button("Basic");
        basicBtn.getStyleClass().add(ROUNDED);
        basicBtn.setOnAction(PRINT_SOURCE);

        var accentBtn = new Button("Accent");
        accentBtn.getStyleClass().addAll(ROUNDED, ACCENT);
        accentBtn.setOnAction(PRINT_SOURCE);

        var successBtn = new Button("Success", new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().addAll(ROUNDED, BUTTON_OUTLINED, SUCCESS);
        successBtn.setOnAction(PRINT_SOURCE);

        var content = new HBox(10);
        content.getChildren().addAll(basicBtn, accentBtn, successBtn);

        return new SampleBlock("Rounded", content);
    }

    private SampleBlock disabledSample() {
        var basicBtn = new Button("Basic");
        basicBtn.setDisable(true);

        var defaultBtn = new Button("Default");
        defaultBtn.setDefaultButton(true);
        defaultBtn.setDisable(true);

        var flatBtn = new Button("Flat");
        flatBtn.getStyleClass().addAll(FLAT);
        flatBtn.setDisable(true);

        var iconBtn = new Button("", new FontIcon(Feather.TAG));
        iconBtn.getStyleClass().addAll(BUTTON_ICON);
        iconBtn.setDisable(true);

        var content = new HBox(10);
        content.getChildren().addAll(basicBtn, defaultBtn, flatBtn, iconBtn);

        return new SampleBlock("Disabled", content);
    }
}
