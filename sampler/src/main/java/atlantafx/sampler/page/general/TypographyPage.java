/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.sampler.event.ThemeEvent.EventType;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.ThemeEvent;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public final class TypographyPage extends OutlinePage {

    public static final String NAME = "Typography";

    private GridPane fontSizeGridPane;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public @Nullable URI getJavadocUri() {
        return null;
    }

    public TypographyPage() {
        super();

        addPageHeader();
        addFormattedText("""
                Because AtlantaFX is also distributed as a single CSS file, it does not come \
                with any fonts. However, it does support several utility classes demonstrated \
                below that can be used to manipulate font properties. If you need a formatted \
                text support have a look at [url=local://general.BBCodePage]BBCodeParser[/url].""",
            true
        );
        addSection("Font Size", fontSizeExample());
        addSection("Font Weight", fontWeightExample());
        addSection("Font Style", fontStyleExample());
        addSection("Text Color", textColorExample());
        addSection("Label", labelExample());
        addSection("Hyperlink", hyperlinkExample());

        DefaultEventBus.getInstance().subscribe(ThemeEvent.class, e -> {
            var eventType = e.getEventType();
            if (eventType == EventType.THEME_CHANGE || eventType == EventType.FONT_CHANGE) {
                updateFontInfo();
            }
        });
    }

    @Override
    protected void onRendered() {
        super.onRendered();
        // font metrics can only be obtained by requesting from a rendered node
        updateFontInfo();
    }

    private void updateFontInfo() {
        if (fontSizeGridPane == null) {
            return;
        }

        Map<String, Node> map = fontSizeGridPane.getChildren().stream()
            .collect(Collectors.toMap(
                n -> GridPane.getColumnIndex(n).toString() + GridPane.getRowIndex(n).toString(),
                n -> n
            ));
        ((Label) map.get("10")).setText(String.format("=%.0fpx", getFontSize(map.get("00"))));
        ((Label) map.get("11")).setText(String.format("=%.0fpx", getFontSize(map.get("01"))));
        ((Label) map.get("12")).setText(String.format("=%.0fpx", getFontSize(map.get("02"))));
        ((Label) map.get("13")).setText(String.format("=%.0fpx", getFontSize(map.get("03"))));
        ((Label) map.get("30")).setText(String.format("=%.0fpx", getFontSize(map.get("20"))));
        ((Label) map.get("31")).setText(String.format("=%.0fpx", getFontSize(map.get("21"))));
        ((Label) map.get("32")).setText(String.format("=%.0fpx", getFontSize(map.get("22"))));
    }

    private double getFontSize(Node node) {
        return (node instanceof Text text) ? Math.ceil(text.getFont().getSize()) : 0;
    }

    ///////////////////////////////////////////////////////////////////////////

    private ExampleBox fontSizeExample() {
        //snippet_1:start
        var title1Text = new Text("Title 1");
        title1Text.getStyleClass().addAll(Styles.TITLE_1);

        var title2Text = new Text("Title 2");
        title2Text.getStyleClass().addAll(Styles.TITLE_2);

        var title3Text = new Text("Title 3");
        title3Text.getStyleClass().addAll(Styles.TITLE_3);

        var title4Text = new Text("Title 4");
        title4Text.getStyleClass().addAll(Styles.TITLE_4);

        var captionText = new Text("Caption");
        captionText.getStyleClass().addAll(Styles.TEXT_CAPTION);

        var defaultText = new Text("Default");

        var smallText = new Text("Small");
        smallText.getStyleClass().addAll(Styles.TEXT_SMALL);
        //snippet_1:end

        var grid = new GridPane();
        grid.setHgap(HGAP_20);
        grid.setVgap(VGAP_20);
        grid.addRow(0,
            title1Text, createFontSizeLabel(),
            captionText, createFontSizeLabel()
        );
        grid.addRow(1,
            title2Text, createFontSizeLabel(),
            defaultText, createFontSizeLabel()
        );
        grid.addRow(2,
            title3Text, createFontSizeLabel(),
            smallText, createFontSizeLabel()
        );
        grid.addRow(3, title4Text, createFontSizeLabel());
        grid.setAlignment(Pos.BASELINE_LEFT);

        fontSizeGridPane = grid;

        var example = new ExampleBox(grid, new Snippet(getClass(), 1));
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox fontWeightExample() {
        //snippet_2:start
        class StyledText extends Text {

            public StyledText(String text, String style) {
                super();
                setText(text);
                setStyle(style + "-fx-fill:-color-fg-default;");
            }
        }

        var boldText = new Text("Bold");
        boldText.getStyleClass().addAll(Styles.TEXT_BOLD);

        var bolderText = new Text("Bolder");
        bolderText.getStyleClass().addAll(Styles.TEXT_BOLDER);

        var normalText = new Text("Normal");
        normalText.getStyleClass().addAll(Styles.TEXT_NORMAL);

        var lighterText = new Text("Lighter");
        lighterText.getStyleClass().addAll(Styles.TEXT_LIGHTER);

        var sample1 = new HBox(
            HGAP_20, boldText, bolderText, normalText, lighterText
        );
        sample1.setAlignment(Pos.BASELINE_LEFT);

        // ~
        var sample2 = new HBox(
            HGAP_20,
            new StyledText("900", "-fx-font-weight:900;"),
            new StyledText("800", "-fx-font-weight:800;"),
            new StyledText("700", "-fx-font-weight:700;"),
            new StyledText("600", "-fx-font-weight:600;"),
            new StyledText("500", "-fx-font-weight:500;"),
            new StyledText("400", "-fx-font-weight:400;"),
            new StyledText("300", "-fx-font-weight:300;"),
            new StyledText("200", "-fx-font-weight:200;"),
            new StyledText("100", "-fx-font-weight:100;"),
            new Text("🡠 no difference")
        );
        sample2.setAlignment(Pos.BASELINE_LEFT);

        // JDK-8090423:
        // https://bugs.openjdk.org/browse/JDK-8090423
        // Workaround:
        // https://edencoding.com/resources/css_properties/fx-font-weight/
        var sample3 = new HBox(
            HGAP_20,
            new StyledText("900", "-fx-font-family:'Inter Black';"),
            new StyledText("800", "-fx-font-family:'Inter Extra Bold';"),
            new StyledText("700", "-fx-font-family:'Inter Bold';"),
            new StyledText("600", "-fx-font-family:'Inter Semi Bold';"),
            new StyledText("500", "-fx-font-family:'Inter Medium';"),
            new StyledText("400", "-fx-font-family:'Inter Regular';"),
            new StyledText("300", "-fx-font-family:'Inter Light';"),
            new StyledText("200", "-fx-font-family:'Inter Extra Light';"),
            new StyledText("100", "-fx-font-family:'Inter Thin';"),
            new Text("🡠 workaround")
        );
        sample3.setAlignment(Pos.BASELINE_LEFT);
        //snippet_2:end

        var box = new VBox(VGAP_20, sample1, sample2, sample3);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            JavaFX [color="-color-danger-fg"]only supports Bold or Regular[/color] font weight. \
            See the source code for workaround."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 2), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox fontStyleExample() {
        //snippet_3:start
        var italicText = new Text("Italic");
        italicText.getStyleClass().addAll(
            Styles.TEXT, Styles.TEXT_ITALIC
        );

        var obliqueText = new Text("Oblique");
        obliqueText.getStyleClass().addAll(
            Styles.TEXT, Styles.TEXT_OBLIQUE
        );

        var underlinedText = new Text("Underlined");
        underlinedText.getStyleClass().addAll(
            Styles.TEXT, Styles.TEXT_UNDERLINED
        );

        var strikethroughText = new Text("Strikethrough");
        strikethroughText.getStyleClass().addAll(
            Styles.TEXT, Styles.TEXT_STRIKETHROUGH
        );
        //snippet_3:end

        var box = new FlowPane(
            HGAP_20, VGAP_20,
            italicText, obliqueText, underlinedText, strikethroughText
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        var example = new ExampleBox(box, new Snippet(getClass(), 3));
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox textColorExample() {
        //snippet_4:start
        var accentText = new Text("Accent");
        accentText.getStyleClass().addAll(Styles.TEXT, Styles.ACCENT);

        var successText = new Text("Success");
        successText.getStyleClass().addAll(Styles.TEXT, Styles.SUCCESS);

        var warningText = new Text("Warning");
        warningText.getStyleClass().addAll(Styles.TEXT, Styles.WARNING);

        var dangerText = new Text("Danger");
        dangerText.getStyleClass().addAll(Styles.TEXT, Styles.DANGER);

        var mutedText = new Text("Muted");
        mutedText.getStyleClass().addAll(Styles.TEXT, Styles.TEXT_MUTED);

        var subtleText = new Text("Subtle");
        subtleText.getStyleClass().addAll(Styles.TEXT, Styles.TEXT_SUBTLE);
        //snippet_4:end

        var box = new FlowPane(
            HGAP_20, VGAP_20,
            accentText, successText, warningText, dangerText, mutedText, subtleText
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        var example = new ExampleBox(box, new Snippet(getClass(), 4));
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox hyperlinkExample() {
        //snippet_5:start
        var linkNormal = new Hyperlink("_Normal");
        linkNormal.setMnemonicParsing(true);

        var linkVisited = new Hyperlink("_Visited");
        linkVisited.setVisited(true);
        linkVisited.setMnemonicParsing(true);

        var linkBroken = new Hyperlink("_Broken");
        linkBroken.setStyle("-color-link-fg-visited:-color-danger-fg;");
        linkBroken.setVisited(true);
        linkBroken.setMnemonicParsing(true);

        var linkDisabled = new Hyperlink("Disabled");
        linkDisabled.setDisable(true);
        //snippet_5:end

        var box = new FlowPane(
            HGAP_20, VGAP_20,
            linkNormal, linkVisited, linkBroken, linkDisabled
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        var description = BBCodeParser.createFormattedText("""
            An HTML like label which can be a graphic and/or text which responds to \
            rollovers and clicks. When a hyperlink is clicked/pressed [code]#isVisited[/code] \
            becomes "true".  A Hyperlink behaves just like a [i]Button[/i]."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 5), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox labelExample() {
        //snippet_6:start
        var defaultLabel = new Label("default", createFontIcon());

        var accentLabel = new Label("accent", createFontIcon());
        accentLabel.getStyleClass().add(Styles.ACCENT);

        var successLabel = new Label("success", createFontIcon());
        successLabel.getStyleClass().add(Styles.SUCCESS);

        var warningLabel = new Label("warning", createFontIcon());
        warningLabel.getStyleClass().add(Styles.WARNING);

        var dangerLabel = new Label("danger", createFontIcon());
        dangerLabel.getStyleClass().add(Styles.DANGER);

        var mutedLabel = new Label("muted", createFontIcon());
        mutedLabel.getStyleClass().add(Styles.TEXT_MUTED);

        var subtleLabel = new Label("subtle", createFontIcon());
        subtleLabel.getStyleClass().add(Styles.TEXT_SUBTLE);
        //snippet_6:end

        var box = new FlowPane(
            20, 20,
            defaultLabel,
            accentLabel,
            successLabel,
            warningLabel,
            dangerLabel,
            mutedLabel,
            subtleLabel
        );

        var description = BBCodeParser.createFormattedText("""
            Label is a non-editable text control. A [i]Label[/i] is useful for displaying text \
            that is required to fit within a specific space, and thus may need to use an ellipsis \
            or truncation to size the string to fit.
                        
            You can use pseudo-classes to set the [i]Label[/i] color. Note that icon \
            inherits label color by default."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 6), description);
        example.setAllowDisable(false);

        return example;
    }

    private Label createFontSizeLabel() {
        var label = new Label();
        label.setPadding(new Insets(5, 40, 5, 10));
        label.setStyle("-fx-font-family:monospace;");
        return label;
    }

    private FontIcon createFontIcon(String... stylesClass) {
        var icon = new FontIcon(Material2AL.LABEL);
        icon.getStyleClass().addAll(stylesClass);
        return icon;
    }
}
