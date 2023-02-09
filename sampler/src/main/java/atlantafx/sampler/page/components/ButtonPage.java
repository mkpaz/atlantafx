/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.BUTTON_CIRCLE;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.BUTTON_OUTLINED;
import static atlantafx.base.theme.Styles.DANGER;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.base.theme.Styles.LARGE;
import static atlantafx.base.theme.Styles.ROUNDED;
import static atlantafx.base.theme.Styles.SMALL;
import static atlantafx.base.theme.Styles.SUCCESS;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.CSSFragment;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

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
        grid.setHgap(Page.PAGE_HGAP);
        grid.setVgap(Page.PAGE_VGAP);

        grid.add(basicSample(), 0, 0);
        grid.add(iconButtonSample(), 1, 0);

        grid.add(coloredSample(), 0, 1);
        grid.add(circularSample(), 1, 1);

        grid.add(outlinedSample(), 0, 2);
        grid.add(sizeSample(), 1, 2);

        grid.add(roundedSample(), 0, 3);
        grid.add(customColorSample(), 1, 3);

        grid.add(disabledSample(), 0, 4);

        setUserContent(grid);
    }

    private SampleBlock basicSample() {
        var basicBtn = new Button("_Basic");
        basicBtn.setMnemonicParsing(true);

        var defaultBtn = new Button("_Default");
        defaultBtn.setDefaultButton(true);
        defaultBtn.setMnemonicParsing(true);

        var flatBtn = new Button("_Flat");
        flatBtn.getStyleClass().add(FLAT);

        var content = new HBox(BLOCK_HGAP, basicBtn, defaultBtn, flatBtn);
        return new SampleBlock("Basic", content);
    }

    private SampleBlock coloredSample() {
        var accentBtn = new Button("_Accent");
        accentBtn.getStyleClass().add(ACCENT);
        accentBtn.setMnemonicParsing(true);

        var successBtn = new Button("_Success", new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().add(SUCCESS);
        successBtn.setMnemonicParsing(true);

        var dangerBtn = new Button("Da_nger", new FontIcon(Feather.TRASH));
        dangerBtn.getStyleClass().add(DANGER);
        dangerBtn.setContentDisplay(ContentDisplay.RIGHT);
        dangerBtn.setMnemonicParsing(true);

        var content = new HBox(BLOCK_HGAP, accentBtn, successBtn, dangerBtn);
        return new SampleBlock("Colored", content);
    }

    private SampleBlock iconButtonSample() {
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

        var content = new HBox(BLOCK_HGAP,
            basicBtn, accentBtn, successBtn, dangerBtn,
            flatAccentBtn, flatSuccessBtn, flatDangerBtn
        );
        return new SampleBlock("Icon", content);
    }

    private SampleBlock circularSample() {
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

        var content = new HBox(BLOCK_HGAP,
            basicBtn, accentBtn, successBtn, dangerBtn,
            flatAccentBtn, flatSuccessBtn, flatDangerBtn
        );
        return new SampleBlock("Circular", content);
    }

    private SampleBlock outlinedSample() {
        var accentBtn = new Button("Accen_t");
        accentBtn.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
        accentBtn.setMnemonicParsing(true);

        var successBtn = new Button("S_uccess", new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
        successBtn.setMnemonicParsing(true);

        var dangerBtn = new Button("Dan_ger", new FontIcon(Feather.TRASH));
        dangerBtn.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
        dangerBtn.setContentDisplay(ContentDisplay.RIGHT);
        dangerBtn.setMnemonicParsing(true);

        var content = new HBox(BLOCK_HGAP, accentBtn, successBtn, dangerBtn);

        return new SampleBlock("Outlined", content);
    }

    private SampleBlock roundedSample() {
        var basicBtn = new Button("Basic");
        basicBtn.getStyleClass().addAll(SMALL, ROUNDED);

        var accentBtn = new Button("Accent");
        accentBtn.getStyleClass().addAll(ROUNDED, ACCENT);

        var successBtn = new Button("Success", new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().addAll(LARGE, ROUNDED, BUTTON_OUTLINED, SUCCESS);

        var content = new HBox(BLOCK_HGAP, basicBtn, accentBtn, successBtn);
        content.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Rounded", content);
    }

    private SampleBlock sizeSample() {
        var smallBtn = new Button("Small");
        smallBtn.getStyleClass().addAll(SMALL);

        var normalBtn = new Button("Normal");

        var largeBtn = new Button("Large");
        largeBtn.getStyleClass().addAll(LARGE);

        var content = new HBox(BLOCK_HGAP, smallBtn, normalBtn, largeBtn);
        content.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Size", content);
    }

    private SampleBlock customColorSample() {
        var btn = new Button("DO SOMETHING!");
        btn.getStyleClass().addAll(SUCCESS, LARGE);
        btn.setStyle("""
              -color-button-bg:       linear-gradient(to bottom right, -color-success-emphasis, darkblue);
              -color-button-bg-hover:   -color-button-bg;
              -color-button-bg-focused: -color-button-bg;
              -color-button-bg-pressed: -color-button-bg;
            """);

        var iconBtn = new Button("", new FontIcon(Material2AL.FAVORITE));
        iconBtn.getStyleClass().addAll("favorite-button", BUTTON_CIRCLE, FLAT, DANGER);
        new CSSFragment("""
            .favorite-button.button >.ikonli-font-icon {
                -fx-fill:       linear-gradient(to bottom right, pink, -color-danger-emphasis);
                -fx-icon-color: linear-gradient(to bottom right, pink, -color-danger-emphasis);
                -fx-font-size:  32px;
                -fx-icon-size:  32px;
            }
            """).addTo(iconBtn);

        var content = new HBox(BLOCK_HGAP, btn, iconBtn);
        content.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Custom Color", content);
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

        var content = new HBox(BLOCK_HGAP, basicBtn, defaultBtn, flatBtn, iconBtn);
        return new SampleBlock("Disabled", content);
    }
}
