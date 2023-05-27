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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public final class ButtonPage extends OutlinePage {

    public static final String NAME = "Button";

    @Override
    public String getName() {
        return NAME;
    }

    public ButtonPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A simple button control. The button control can contain text and/or a graphic.
            A button control has three different modes:

            [ul]
            [li]Normal: A normal push button.[/li]
            [li]Default: A default button is the button that receives a keyboard [code]VK_ENTER[/code] press,
            if no other node in the scene consumes it.[/li]
            [li]Cancel: A cancel button is the button that receives a keyboard [code]VK_ESC[/code] press,
            if no other node in the scene consumes it.[/li][/ul]"""
        );
        addSection("Usage", usageExample());
        addSection("Colored", coloredButtonExample());
        addSection("Icon Button", iconButtonExample());
        addSection("Circular", circularButtonExample());
        addSection("Outlined", outlinedButtonExample());
        addSection("Rounded", roundedButtonExample());
        addSection("Button Size", buttonSizeExample());
        addSection("Custom Styles", customColorExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var normalBtn = new Button("_Normal");
        normalBtn.setMnemonicParsing(true);

        var defaultBtn = new Button("_Default");
        defaultBtn.setDefaultButton(true);
        defaultBtn.setMnemonicParsing(true);

        var outlinedBtn = new Button("Out_lined");
        outlinedBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        outlinedBtn.setMnemonicParsing(true);

        var flatBtn = new Button("_Flat");
        flatBtn.getStyleClass().add(Styles.FLAT);
        //snippet_1:end

        var box = new HBox(HGAP_20, normalBtn, defaultBtn, outlinedBtn, flatBtn);
        var description = BBCodeParser.createFormattedText("""
            The [i]Button[/i] comes with four CSS variants: normal (default), colored, \
            outlined, and flat (or text). To change the appearance of the [i]Button[/i], \
            you set the corresponding style classes that work as modifiers."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox coloredButtonExample() {
        //snippet_2:start
        var accentBtn = new Button("_Accent");
        accentBtn.getStyleClass().add(Styles.ACCENT);
        accentBtn.setMnemonicParsing(true);

        var successBtn = new Button(
            "_Success", new FontIcon(Feather.CHECK)
        );
        successBtn.getStyleClass().add(Styles.SUCCESS);
        successBtn.setMnemonicParsing(true);

        var dangerBtn = new Button(
            "Da_nger", new FontIcon(Feather.TRASH)
        );
        dangerBtn.getStyleClass().add(Styles.DANGER);
        dangerBtn.setContentDisplay(ContentDisplay.RIGHT);
        dangerBtn.setMnemonicParsing(true);

        // ~
        var accentOutBtn = new Button("Accen_t");
        accentOutBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.ACCENT
        );
        accentOutBtn.setMnemonicParsing(true);

        var successOutBtn = new Button(
            "S_uccess", new FontIcon(Feather.CHECK)
        );
        successOutBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.SUCCESS
        );
        successOutBtn.setMnemonicParsing(true);

        var dangerOutBtn = new Button(
            "Dan_ger", new FontIcon(Feather.TRASH)
        );
        dangerOutBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.DANGER
        );
        dangerOutBtn.setContentDisplay(ContentDisplay.RIGHT);
        dangerOutBtn.setMnemonicParsing(true);

        // ~
        var accentFlatBtn = new Button("Accen_t");
        accentFlatBtn.getStyleClass().addAll(
            Styles.FLAT, Styles.ACCENT
        );
        accentFlatBtn.setMnemonicParsing(true);

        var successFlatBtn = new Button(
            "S_uccess", new FontIcon(Feather.CHECK)
        );
        successFlatBtn.getStyleClass().addAll(
            Styles.FLAT, Styles.SUCCESS
        );
        successFlatBtn.setMnemonicParsing(true);

        var dangerFlatBtn = new Button(
            "Dan_ger", new FontIcon(Feather.TRASH)
        );
        dangerFlatBtn.getStyleClass().addAll(
            Styles.FLAT, Styles.DANGER
        );
        dangerFlatBtn.setContentDisplay(ContentDisplay.RIGHT);
        dangerFlatBtn.setMnemonicParsing(true);
        //snippet_2:end

        var box = new VBox(
            VGAP_20,
            new HBox(HGAP_20, accentBtn, successBtn, dangerBtn),
            new HBox(HGAP_20, accentOutBtn, successOutBtn, dangerOutBtn),
            new HBox(HGAP_20, accentFlatBtn, successFlatBtn, dangerFlatBtn)
        );
        var description = BBCodeParser.createFormattedText("""
            You can change the [i]Button[/i] color simply by using predefined style class modifiers."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox iconButtonExample() {
        //snippet_3:start
        var normalBtn = new Button(null, new FontIcon(Feather.MORE_HORIZONTAL));
        normalBtn.getStyleClass().addAll(Styles.BUTTON_ICON);

        var accentBtn = new Button(null, new FontIcon(Feather.MENU));
        accentBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.ACCENT
        );

        var successBtn = new Button(null, new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.SUCCESS
        );

        var dangerBtn = new Button(null, new FontIcon(Feather.TRASH));
        dangerBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.BUTTON_OUTLINED, Styles.DANGER
        );

        var flatAccentBtn = new Button(null, new FontIcon(Feather.MIC));
        flatAccentBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.FLAT, Styles.ACCENT
        );

        var flatSuccessBtn = new Button(null, new FontIcon(Feather.USER));
        flatSuccessBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.FLAT, Styles.SUCCESS
        );

        var flatDangerBtn = new Button(null, new FontIcon(Feather.CROSSHAIR));
        flatDangerBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.FLAT, Styles.DANGER
        );
        //snippet_3:end

        var box = new HBox(HGAP_20,
            normalBtn, accentBtn, successBtn, dangerBtn,
            flatAccentBtn, flatSuccessBtn, flatDangerBtn
        );
        var description = BBCodeParser.createFormattedText("""
            Icon buttons are present in two variants. The first one is just a \
            normal [i]Button[/i] but with no text and the second one is a flat button - \
            suitable for toolbars and similar controls."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox circularButtonExample() {
        //snippet_4:start
        var normalBtn = new Button(null, new FontIcon(Feather.MORE_HORIZONTAL));
        normalBtn.getStyleClass().addAll(Styles.BUTTON_CIRCLE);

        var accentBtn = new Button(null, new FontIcon(Feather.MENU));
        accentBtn.getStyleClass().addAll(
            Styles.BUTTON_CIRCLE, Styles.ACCENT
        );

        var successBtn = new Button(null, new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().addAll(
            Styles.BUTTON_CIRCLE, Styles.SUCCESS
        );

        var dangerBtn = new Button(null, new FontIcon(Feather.TRASH));
        dangerBtn.getStyleClass().addAll(
            Styles.BUTTON_CIRCLE, Styles.BUTTON_OUTLINED, Styles.DANGER
        );

        var flatAccentBtn = new Button(null, new FontIcon(Feather.MIC));
        flatAccentBtn.getStyleClass().addAll(
            Styles.BUTTON_CIRCLE, Styles.FLAT, Styles.ACCENT
        );

        var flatSuccessBtn = new Button(null, new FontIcon(Feather.USER));
        flatSuccessBtn.getStyleClass().addAll(
            Styles.BUTTON_CIRCLE, Styles.FLAT, Styles.SUCCESS
        );

        var flatDangerBtn = new Button(null, new FontIcon(Feather.CROSSHAIR));
        flatDangerBtn.getStyleClass().addAll(
            Styles.BUTTON_CIRCLE, Styles.FLAT, Styles.DANGER
        );
        //snippet_4:end

        var box = new HBox(HGAP_20,
            normalBtn, accentBtn, successBtn, dangerBtn,
            flatAccentBtn, flatSuccessBtn, flatDangerBtn
        );

        var description = BBCodeParser.createFormattedText("""
            You can also apply the [code]setShape()[/code] method to make the \
            [i]Button[/i] look circular.""");

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }

    private ExampleBox outlinedButtonExample() {
        //snippet_5:start
        var accentOutBtn = new Button("Accen_t");
        accentOutBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.ACCENT
        );
        accentOutBtn.setMnemonicParsing(true);

        var successOutBtn = new Button("S_uccess", new FontIcon(Feather.CHECK));
        successOutBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.SUCCESS
        );
        successOutBtn.setMnemonicParsing(true);

        var dangerOutBtn = new Button("Dan_ger", new FontIcon(Feather.TRASH));
        dangerOutBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.DANGER
        );
        dangerOutBtn.setContentDisplay(ContentDisplay.RIGHT);
        dangerOutBtn.setMnemonicParsing(true);
        //snippet_5:end

        var box = new HBox(HGAP_20, accentOutBtn, successOutBtn, dangerOutBtn);
        var description = BBCodeParser.createFormattedText("""
            Outlined buttons are medium-emphasis buttons. They contain actions that are \
            important but aren't the primary action in an app."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 5), description);
    }

    private ExampleBox roundedButtonExample() {
        //snippet_6:start
        var normalBtn = new Button("Normal");
        normalBtn.getStyleClass().addAll(
            Styles.SMALL, Styles.ROUNDED
        );

        var accentBtn = new Button("Accent");
        accentBtn.getStyleClass().addAll(
            Styles.ROUNDED, Styles.ACCENT
        );

        var successBtn = new Button("Success", new FontIcon(Feather.CHECK));
        successBtn.getStyleClass().addAll(
            Styles.LARGE, Styles.ROUNDED, Styles.BUTTON_OUTLINED, Styles.SUCCESS
        );
        //snippet_6:end

        var box = new HBox(HGAP_20, normalBtn, accentBtn, successBtn);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            [i]Button[/i] corners can be rounded with the [code]Styles.ROUNDED[/code] \
            style class modifier."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 6), description);
    }

    private ExampleBox buttonSizeExample() {
        //snippet_7:start
        var smallBtn = new Button("Small");
        smallBtn.getStyleClass().addAll(Styles.SMALL);

        var normalBtn = new Button("Normal");

        var largeBtn = new Button("Large");
        largeBtn.getStyleClass().addAll(Styles.LARGE);
        //snippet_7:end

        var box = new HBox(HGAP_20, smallBtn, normalBtn, largeBtn);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            For larger or smaller buttons, use the [code]Styles.SMALL[/code] or \
            [code]Styles.LARGE[/code] style classes, respectively."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 7), description);
    }

    private ExampleBox customColorExample() {
        var customStyle = """
            -color-button-bg: linear-gradient(
                to bottom right, -color-success-emphasis, darkblue
            );
            -color-button-bg-hover:   -color-button-bg;
            -color-button-bg-focused: -color-button-bg;
            -color-button-bg-pressed: -color-button-bg;""";

        String dataClass = """
            .favorite-button.button >.ikonli-font-icon {
                -fx-fill: linear-gradient(
                    to bottom right, pink, -color-danger-emphasis
                );
                -fx-icon-color: linear-gradient(
                    to bottom right, pink, -color-danger-emphasis
                );
                -fx-font-size:  32px;
                -fx-icon-size:  32px;
            }""";

        //snippet_8:start
        var btn = new Button("DO SOMETHING!");
        btn.getStyleClass().addAll(Styles.SUCCESS, Styles.LARGE);
        // -color-button-bg: linear-gradient(
        //     to bottom right, -color-success-emphasis, darkblue
        // );
        // -color-button-bg-hover:   -color-button-bg;
        // -color-button-bg-focused: -color-button-bg;
        // -color-button-bg-pressed: -color-button-bg;
        btn.setStyle(customStyle);

        var iconBtn = new Button(null, new FontIcon(Material2AL.FAVORITE));
        iconBtn.getStyleClass().addAll(
            "favorite-button",
            Styles.BUTTON_CIRCLE, Styles.FLAT, Styles.DANGER
        );
        // .favorite-button.button >.ikonli-font-icon {
        //     -fx-fill: linear-gradient(
        //         to bottom right, pink, -color-danger-emphasis
        //     );
        //     -fx-icon-color: linear-gradient(
        //         to bottom right, pink, -color-danger-emphasis
        //     );
        //     -fx-font-size:  32px;
        //     -fx-icon-size:  32px;
        // }
        iconBtn.getStylesheets().add(Styles.toDataURI(dataClass));
        //snippet_8:end

        var box = new HBox(HGAP_20, btn, iconBtn);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            In addition to using the predefined [i]Button[/i] colors, you can add custom ones \
            by manipulating the looked-up color variables."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 8), description);
    }
}
