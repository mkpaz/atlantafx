/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.util.stream.IntStream;
import javafx.geometry.Side;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class MenuButtonPage extends OutlinePage {

    public static final String NAME = "MenuButton";

    @Override
    public String getName() {
        return NAME;
    }

    public MenuButtonPage() {
        super();

        addPageHeader();
        addFormattedText("""
            [i]MenuButton[/i] is a button which, when clicked or pressed, will \
            show a context (dropdown) menu.
                        
            [i]SplitMenuButton[/i] is a variation of menu button. It is broken into two \
            pieces, the [i]action[/i] area and the [i]menu open[/i] area. If the user clicks \
            in the action area, the [i]SplitMenuButton[/i] will act similarly to [i]Button[/i], \
            while the menu open area of the control will show a context menu if clicked."""
        );
        addSection("Usage", usageExample());
        addSection("Colored", coloredExample());
        addSection("Popup Side", popupSideExample());
        addSection("Icon Button", iconButtonExample());
        addSection("Outlined", outlinedExample());
        addSection("No Arrow", noArrowExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var normalMenuBtn = new MenuButton("_Menu Button");
        normalMenuBtn.getItems().setAll(createItems(5));
        normalMenuBtn.setMnemonicParsing(true);

        var normalSplitBtn = new SplitMenuButton(createItems(5));
        normalSplitBtn.setText("_Split Menu Button");
        normalSplitBtn.setMnemonicParsing(true);

        var outlinedMenuBtn = new MenuButton("Outlined");
        outlinedMenuBtn.getItems().setAll(createItems(5));
        outlinedMenuBtn.getStyleClass().add(Styles.BUTTON_OUTLINED);

        var outlinedSplitBtn = new SplitMenuButton(createItems(5));
        outlinedSplitBtn.setText("Outlined");
        outlinedSplitBtn.getStyleClass().add(Styles.BUTTON_OUTLINED);

        var flatMenuBtn = new MenuButton("Flat");
        flatMenuBtn.getItems().setAll(createItems(5));
        flatMenuBtn.getStyleClass().add(Styles.FLAT);

        var flatSplitBtn = new SplitMenuButton(createItems(5));
        flatSplitBtn.setText("Flat");
        flatSplitBtn.getStyleClass().add(Styles.FLAT);
        //snippet_1:end

        var box = new FlowPane(
            HGAP_20, VGAP_10,
            normalMenuBtn, normalSplitBtn,
            outlinedMenuBtn, outlinedSplitBtn,
            flatMenuBtn, flatSplitBtn
        );

        var description = BBCodeParser.createFormattedText("""
            Just like the [i]Button[/i], [i]MenuButton[/i] comes with four \
            CSS variants: normal (default), colored, outlined, and flat (or text). \
            To change the appearance of the [i]Button[/i], you set the corresponding \
            style classes that work as modifiers."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox coloredExample() {
        //snippet_2:start
        var accentMenuBtn = new MenuButton("_Accent");
        accentMenuBtn.getItems().setAll(createItems(5));
        accentMenuBtn.getStyleClass().add(Styles.ACCENT);
        accentMenuBtn.setMnemonicParsing(true);
        accentMenuBtn.setPrefWidth(150);

        var accentSplitBtn = new SplitMenuButton(createItems(5));
        accentSplitBtn.setText("Accent");
        accentSplitBtn.getStyleClass().add(Styles.ACCENT);
        accentSplitBtn.setPrefWidth(150);

        var successMenuBtn = new MenuButton("Success");
        successMenuBtn.getItems().setAll(createItems(5));
        successMenuBtn.setGraphic(new FontIcon(Feather.CHECK));
        successMenuBtn.getStyleClass().add(Styles.SUCCESS);
        successMenuBtn.setPrefWidth(150);

        var successSplitBtn = new SplitMenuButton(createItems(5));
        successSplitBtn.setGraphic(new FontIcon(Feather.CHECK));
        successSplitBtn.setText("_Success");
        successSplitBtn.getStyleClass().add(Styles.SUCCESS);
        successSplitBtn.setMnemonicParsing(true);
        successSplitBtn.setPrefWidth(150);

        var dangerMenuBtn = new MenuButton("Danger");
        dangerMenuBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerMenuBtn.getItems().setAll(createItems(5));
        dangerMenuBtn.getStyleClass().add(Styles.DANGER);
        dangerMenuBtn.setPrefWidth(150);

        var dangerSplitBtn = new SplitMenuButton(createItems(5));
        dangerSplitBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerSplitBtn.setText("_Danger");
        dangerSplitBtn.getStyleClass().add(Styles.DANGER);
        dangerSplitBtn.setMnemonicParsing(true);
        dangerSplitBtn.setPrefWidth(150);
        //snippet_2:end

        var grid = new GridPane();
        grid.setHgap(HGAP_20);
        grid.setVgap(VGAP_10);
        grid.addColumn(0, accentMenuBtn, successMenuBtn, dangerMenuBtn);
        grid.addColumn(1, accentSplitBtn, successSplitBtn, dangerSplitBtn);

        var description = BBCodeParser.createFormattedText("""
            You can change the [i]MenuButton[/i] color simply by using predefined \
            style class modifiers."""
        );

        return new ExampleBox(grid, new Snippet(getClass(), 2), description);
    }

    private ExampleBox popupSideExample() {
        //snippet_3:start
        var topMenuBtn = new MenuButton("Top");
        topMenuBtn.getItems().setAll(createItems(5));
        topMenuBtn.setPopupSide(Side.TOP);

        var rightMenuBtn = new MenuButton("Right");
        rightMenuBtn.getItems().setAll(createItems(5));
        rightMenuBtn.setPopupSide(Side.RIGHT);
        rightMenuBtn.getStyleClass().add(Styles.ACCENT);

        var bottomMenuBtn = new MenuButton("Bottom");
        bottomMenuBtn.getItems().setAll(createItems(5));
        bottomMenuBtn.setPopupSide(Side.BOTTOM);
        bottomMenuBtn.getStyleClass().add(Styles.SUCCESS);

        var leftMenuBtn = new MenuButton("Left");
        leftMenuBtn.getItems().setAll(createItems(5));
        leftMenuBtn.setPopupSide(Side.LEFT);
        leftMenuBtn.getStyleClass().add(Styles.DANGER);
        //snippet_3:end

        var box = new FlowPane(
            HGAP_20, VGAP_10,
            topMenuBtn, rightMenuBtn, bottomMenuBtn, leftMenuBtn
        );
        var description = BBCodeParser.createFormattedText("""
            A [i]MenuButton[/i] can be set to show its menu on any side of the button. \
            This is specified using the [code]popupSide[/code] property. By default the \
            menu appears below the button. However, regardless of the popupSide specified, \
            if there is not enough room, the [i]ContextMenu[/i] will be smartly repositioned, \
            most probably to be on the opposite side of the [i]MenuButton[/i].""");

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox iconButtonExample() {
        //snippet_4:start
        var normalMenuBtn = new MenuButton();
        normalMenuBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        normalMenuBtn.getItems().setAll(createItems(5));
        normalMenuBtn.getStyleClass().addAll(Styles.BUTTON_ICON);

        var normalSplitBtn = new SplitMenuButton(createItems(5));
        normalSplitBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        normalSplitBtn.getStyleClass().addAll(Styles.BUTTON_ICON);

        var outlinedMenuBtn = new MenuButton();
        outlinedMenuBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        outlinedMenuBtn.getItems().setAll(createItems(5));
        outlinedMenuBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.BUTTON_OUTLINED
        );

        var outlinedSplitBtn = new SplitMenuButton(createItems(5));
        outlinedSplitBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        outlinedSplitBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.BUTTON_OUTLINED
        );

        var accentMenuBtn = new MenuButton();
        accentMenuBtn.setGraphic(new FontIcon(Feather.MENU));
        accentMenuBtn.getItems().setAll(createItems(5));
        accentMenuBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.ACCENT
        );

        var accentSplitBtn = new SplitMenuButton(createItems(5));
        accentSplitBtn.setGraphic(new FontIcon(Feather.MENU));
        accentSplitBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Styles.ACCENT
        );
        //snippet_4:end

        var box = new FlowPane(
            HGAP_20, VGAP_10,
            normalMenuBtn, normalSplitBtn,
            outlinedMenuBtn, outlinedSplitBtn,
            accentMenuBtn, accentSplitBtn
        );
        var description = BBCodeParser.createFormattedText("""
            You can hide the [i]MenuButton[/i] text by applying [code]Styles.BUTTON_ICON[/code] \
            style class modifier."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }

    private ExampleBox outlinedExample() {
        //snippet_5:start
        var accentMenuBtn = new MenuButton("Accen_t");
        accentMenuBtn.getItems().setAll(createItems(5));
        accentMenuBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.ACCENT
        );
        accentMenuBtn.setMnemonicParsing(true);
        accentMenuBtn.setPrefWidth(150);

        var accentSplitBtn = new SplitMenuButton(createItems(5));
        accentSplitBtn.setText("Accent");
        accentSplitBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.ACCENT
        );
        accentSplitBtn.setPrefWidth(150);

        var successMenuBtn = new MenuButton("S_uccess");
        successMenuBtn.getItems().setAll(createItems(5));
        successMenuBtn.setGraphic(new FontIcon(Feather.CHECK));
        successMenuBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.SUCCESS
        );
        successMenuBtn.setMnemonicParsing(true);
        successMenuBtn.setPrefWidth(150);

        var successSplitBtn = new SplitMenuButton(createItems(5));
        successSplitBtn.setGraphic(new FontIcon(Feather.CHECK));
        successSplitBtn.setText("Success");
        successSplitBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.SUCCESS
        );
        successSplitBtn.setPrefWidth(150);

        var dangerMenuBtn = new MenuButton("Danger");
        dangerMenuBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerMenuBtn.getItems().setAll(createItems(5));
        dangerMenuBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.DANGER
        );
        dangerMenuBtn.setPrefWidth(150);

        var dangerSplitBtn = new SplitMenuButton(createItems(5));
        dangerSplitBtn.setGraphic(new FontIcon(Feather.TRASH));
        dangerSplitBtn.setText("Dan_ger");
        dangerSplitBtn.getStyleClass().addAll(
            Styles.BUTTON_OUTLINED, Styles.DANGER
        );
        dangerSplitBtn.setMnemonicParsing(true);
        dangerSplitBtn.setPrefWidth(150);
        //snippet_5:end

        var grid = new GridPane();
        grid.setHgap(HGAP_20);
        grid.setVgap(VGAP_10);
        grid.addColumn(0, accentMenuBtn, successMenuBtn, dangerMenuBtn);
        grid.addColumn(1, accentSplitBtn, successSplitBtn, dangerSplitBtn);

        var description = BBCodeParser.createFormattedText("""
            Outlined buttons are medium-emphasis buttons. They contain actions that are \
            important but aren't the primary action in an app."""
        );

        return new ExampleBox(grid, new Snippet(getClass(), 5), description);
    }

    private ExampleBox noArrowExample() {
        //snippet_6:start
        var normalMenuBtn = new MenuButton("_Menu Button");
        normalMenuBtn.getItems().setAll(createItems(5));
        normalMenuBtn.getStyleClass().addAll(Tweaks.NO_ARROW);

        var flatMenuBtn = new MenuButton("Flat");
        flatMenuBtn.getItems().setAll(createItems(5));
        flatMenuBtn.getStyleClass().addAll(
            Styles.FLAT, Tweaks.NO_ARROW
        );

        var iconMenuBtn = new MenuButton();
        iconMenuBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        iconMenuBtn.getItems().setAll(createItems(5));
        iconMenuBtn.getStyleClass().addAll(
            Styles.BUTTON_ICON, Tweaks.NO_ARROW
        );
        //snippet_6:end

        var box = new HBox(HGAP_20);
        box.getChildren().addAll(normalMenuBtn, flatMenuBtn, iconMenuBtn);

        var description = BBCodeParser.createFormattedText("""
            To hide the [i]MenuButton[/i] arrow, use the [code]Tweak.NO_ARROW[/code] \
            style class modifier."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 6), description);
    }

    @SuppressWarnings("SameParameterValue")
    private MenuItem[] createItems(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new MenuItem(FAKER.babylon5().character()))
            .toArray(MenuItem[]::new);
    }
}
