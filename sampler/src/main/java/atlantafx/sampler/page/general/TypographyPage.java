/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.DANGER;
import static atlantafx.base.theme.Styles.SUCCESS;
import static atlantafx.base.theme.Styles.TEXT;
import static atlantafx.base.theme.Styles.TEXT_BOLD;
import static atlantafx.base.theme.Styles.TEXT_BOLDER;
import static atlantafx.base.theme.Styles.TEXT_CAPTION;
import static atlantafx.base.theme.Styles.TEXT_ITALIC;
import static atlantafx.base.theme.Styles.TEXT_LIGHTER;
import static atlantafx.base.theme.Styles.TEXT_MUTED;
import static atlantafx.base.theme.Styles.TEXT_NORMAL;
import static atlantafx.base.theme.Styles.TEXT_OBLIQUE;
import static atlantafx.base.theme.Styles.TEXT_SMALL;
import static atlantafx.base.theme.Styles.TEXT_STRIKETHROUGH;
import static atlantafx.base.theme.Styles.TEXT_SUBTLE;
import static atlantafx.base.theme.Styles.TEXT_UNDERLINED;
import static atlantafx.base.theme.Styles.TITLE_1;
import static atlantafx.base.theme.Styles.TITLE_2;
import static atlantafx.base.theme.Styles.TITLE_3;
import static atlantafx.base.theme.Styles.TITLE_4;
import static atlantafx.base.theme.Styles.WARNING;
import static atlantafx.sampler.event.ThemeEvent.EventType.FONT_CHANGE;
import static atlantafx.sampler.event.ThemeEvent.EventType.THEME_CHANGE;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;
import static atlantafx.sampler.theme.ThemeManager.DEFAULT_FONT_FAMILY_NAME;
import static atlantafx.sampler.theme.ThemeManager.SUPPORTED_FONT_SIZE;

import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.ThemeEvent;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.ThemeManager;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class TypographyPage extends AbstractPage {

    public static final String NAME = "Typography";
    private static final int CONTROL_WIDTH = 200;
    private static final String DEFAULT_FONT_ID = "Default";
    private static final ThemeManager TM = ThemeManager.getInstance();

    private Pane fontSizeSampleContent;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return false;
    }

    @Override
    public boolean canChangeThemeSettings() {
        return false;
    }

    public TypographyPage() {
        super();

        createView();
        DefaultEventBus.getInstance().subscribe(ThemeEvent.class, e -> {
            if (e.getEventType() == THEME_CHANGE || e.getEventType() == FONT_CHANGE) {
                updateFontInfo(Duration.seconds(1));
            }
        });
    }

    private void createView() {
        var controlsGrid = new GridPane();
        controlsGrid.setHgap(BLOCK_HGAP);
        controlsGrid.setVgap(BLOCK_VGAP);
        controlsGrid.add(new Label("Font family"), 0, 0);
        controlsGrid.add(createFontFamilyChooser(), 1, 0);
        controlsGrid.add(new Label("Font size"), 0, 1);
        controlsGrid.add(crateFontSizeSpinner(), 1, 1);

        var fontSizeSample = fontSizeSample();
        fontSizeSampleContent = (Pane) fontSizeSample.getContent();

        setUserContent(new VBox(
            PAGE_VGAP,
            controlsGrid,
            fontSizeSample,
            fontWeightSample(),
            expandingHBox(fontStyleSample(), textColorSample(), hyperlinkSample()),
            textFlowSample()
        ));
    }

    @Override
    protected void onRendered() {
        super.onRendered();
        // font metrics can only be obtained by requesting from a rendered node
        updateFontInfo(Duration.ZERO);
    }

    private ComboBox<String> createFontFamilyChooser() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().add(DEFAULT_FONT_ID); // keyword to reset font family to its default value
        comboBox.getItems().addAll(FXCollections.observableArrayList(Font.getFamilies()));
        comboBox.setPrefWidth(CONTROL_WIDTH);
        comboBox.getSelectionModel().select(TM.getFontFamily()); // select active font family value on page load
        comboBox.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                TM.setFontFamily(DEFAULT_FONT_ID.equals(val) ? DEFAULT_FONT_FAMILY_NAME : val);
            }
        });

        return comboBox;
    }

    private Spinner<Integer> crateFontSizeSpinner() {
        var spinner = new Spinner<Integer>(
            SUPPORTED_FONT_SIZE.get(0),
            SUPPORTED_FONT_SIZE.get(SUPPORTED_FONT_SIZE.size() - 1),
            TM.getFontSize()
        );
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner.setPrefWidth(CONTROL_WIDTH);

        // Instead of this we should obtain font size from a rendered node.
        // But since it's not trivial (thanks to JavaFX doesn't expose relevant API)
        // we just keep current font size inside ThemeManager singleton.
        // It works fine if ThemeManager default font size value matches
        // default theme font size value.
        spinner.getValueFactory().setValue(TM.getFontSize());

        spinner.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                TM.setFontSize(val);
                updateFontInfo(Duration.seconds(1));
            }
        });

        return spinner;
    }

    private void updateFontInfo(Duration delay) {
        var t = new Timeline(new KeyFrame(delay));
        t.setOnFinished(e -> {
            Map<String, Node> map = fontSizeSampleContent.getChildren().stream()
                .collect(Collectors.toMap(
                    n -> GridPane.getColumnIndex(n).toString() + GridPane.getRowIndex(n).toString(),
                    n -> n
                ));
            ((Label) map.get("10")).setText(String.format("%.0fpx", getFontSize(map.get("00"))));
            ((Label) map.get("11")).setText(String.format("%.0fpx", getFontSize(map.get("01"))));
            ((Label) map.get("12")).setText(String.format("%.0fpx", getFontSize(map.get("02"))));
            ((Label) map.get("13")).setText(String.format("%.0fpx", getFontSize(map.get("03"))));
            ((Label) map.get("30")).setText(String.format("%.0fpx", getFontSize(map.get("20"))));
            ((Label) map.get("31")).setText(String.format("%.0fpx", getFontSize(map.get("21"))));
            ((Label) map.get("32")).setText(String.format("%.0fpx", getFontSize(map.get("22"))));
        });
        t.play();
    }

    private double getFontSize(Node node) {
        return (node instanceof Text text) ? Math.ceil(text.getFont().getSize()) : 0;
    }

    private SampleBlock fontSizeSample() {
        var grid = new GridPane();
        grid.setHgap(BLOCK_HGAP);
        grid.setVgap(BLOCK_VGAP);

        grid.add(createText("Title 1", TITLE_1), 0, 0);
        grid.add(createFontSizeLabel(), 1, 0);
        grid.add(createText("Title 2", TITLE_2), 0, 1);
        grid.add(createFontSizeLabel(), 1, 1);
        grid.add(createText("Title 3", TITLE_3), 0, 2);
        grid.add(createFontSizeLabel(), 1, 2);
        grid.add(createText("Title 4", TITLE_4), 0, 3);
        grid.add(createFontSizeLabel(), 1, 3);

        grid.add(createText("Caption", TEXT_CAPTION), 2, 0);
        grid.add(createFontSizeLabel(), 3, 0);
        grid.add(createText("Default"), 2, 1);
        grid.add(createFontSizeLabel(), 3, 1);
        grid.add(createText("Small", TEXT_SMALL), 2, 2);
        grid.add(createFontSizeLabel(), 3, 2);

        grid.setAlignment(Pos.BASELINE_LEFT);

        return new SampleBlock("Font Size", grid);
    }

    private SampleBlock fontWeightSample() {
        var sample1 = new HBox(
            BLOCK_HGAP,
            createText("Bold", TEXT_BOLD),
            createText("Bolder", TEXT_BOLDER),
            createText("Normal", TEXT_NORMAL),
            createText("Lighter", TEXT_LIGHTER)
        );
        sample1.setAlignment(Pos.BASELINE_LEFT);

        var sample2 = new HBox(
            BLOCK_HGAP,
            createStyledText("900", "-fx-font-weight:900;"),
            createStyledText("800", "-fx-font-weight:800;"),
            createStyledText("700", "-fx-font-weight:700;"),
            createStyledText("600", "-fx-font-weight:600;"),
            createStyledText("500", "-fx-font-weight:500;"),
            createStyledText("400", "-fx-font-weight:400;"),
            createStyledText("300", "-fx-font-weight:300;"),
            createStyledText("200", "-fx-font-weight:200;"),
            createStyledText("100", "-fx-font-weight:100;")
        );
        sample2.setAlignment(Pos.BASELINE_LEFT);

        var sample3 = new HBox(
            BLOCK_HGAP,
            createStyledText("900", "-fx-font-family:'Inter Black';"),
            createStyledText("800", "-fx-font-family:'Inter Extra Bold';"),
            createStyledText("700", "-fx-font-family:'Inter Bold';"),
            createStyledText("600", "-fx-font-family:'Inter Semi Bold';"),
            createStyledText("500", "-fx-font-family:'Inter Medium';"),
            createStyledText("400", "-fx-font-family:'Inter Regular';"),
            createStyledText("300", "-fx-font-family:'Inter Light';"),
            createStyledText("200", "-fx-font-family:'Inter Extra Light';"),
            createStyledText("100", "-fx-font-family:'Inter Thin';")
        );
        sample3.setAlignment(Pos.BASELINE_LEFT);

        // JDK-8090423: https://bugs.openjdk.org/browse/JDK-8090423
        // Workaround:  https://edencoding.com/resources/css_properties/fx-font-weight/
        return new SampleBlock("Font Weight", new VBox(
            BLOCK_VGAP,
            sample1,
            sample2,
            sample3,
            createText("JavaFX only supports Bold or Regular font weight. See the source code for workaround.", TEXT,
                WARNING)
        ));
    }

    private SampleBlock fontStyleSample() {
        var box = new FlowPane(
            BLOCK_HGAP, BLOCK_VGAP,
            createText("Italic", TEXT_ITALIC),
            createText("Oblique", TEXT_OBLIQUE),
            createText("Underlined", TEXT_UNDERLINED),
            createText("Strikethrough", TEXT_STRIKETHROUGH)
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        return new SampleBlock("Font Style", box);
    }

    private SampleBlock textColorSample() {
        var box = new FlowPane(
            BLOCK_HGAP, BLOCK_VGAP,
            createText("Accent", TEXT, ACCENT),
            createText("Success", TEXT, SUCCESS),
            createText("Warning", TEXT, WARNING),
            createText("Danger", TEXT, DANGER),
            createText("Muted", TEXT, TEXT_MUTED),
            createText("Subtle", TEXT, TEXT_SUBTLE)
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        return new SampleBlock("Text Color", box);
    }

    private SampleBlock hyperlinkSample() {
        var linkNormal = createHyperlink("_Normal", false, false);
        linkNormal.setMnemonicParsing(true);

        var linkVisited = createHyperlink("_Visited", true, false);
        linkVisited.setMnemonicParsing(true);

        var linkBroken = createHyperlink("_Broken", true, false);
        linkBroken.setStyle("-color-link-fg-visited:-color-danger-fg;");
        linkBroken.setMnemonicParsing(true);

        var box = new FlowPane(
            BLOCK_HGAP, BLOCK_VGAP,
            linkNormal,
            linkVisited,
            linkBroken,
            createHyperlink("Disabled", false, true)
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        return new SampleBlock("Hyperlink", box);
    }

    private SampleBlock textFlowSample() {
        var textFlow = new TextFlow(
            new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. "),
            new Hyperlink("Vivamus at lorem"),
            new Text(" in urna facilisis aliquam. Morbi ut "),
            new Hyperlink("velit"),
            new Text(" iaculis erat cursus molestie eget laoreet quam. "),
            new Text(" Vivamus eu nulla sapien. Sed et malesuada augue. Nullam nec "),
            new Hyperlink("consectetur"),
            new Text(" "),
            new Hyperlink("ipsum"),
            new Text(", eget facilisis enim. Suspendisse potenti. Nulla euismod, nisl sed dapibus pretium,"
                + " augue ligula finibus arcu, in iaculis nulla neque a est. Sed in rutrum diam."
                + " Donec quis arcu molestie, facilisis ex fringilla, "),
            new Hyperlink("volutpat velit"),
            new Text(".")
        );

        return new SampleBlock("Text Flow", textFlow);
    }

    private Text createText(String text, String... styleClasses) {
        var t = new Text(text);
        t.getStyleClass().addAll(styleClasses);
        t.setUserData(text);
        return t;
    }

    private Text createStyledText(String text, String style) {
        var t = new Text(text);
        t.setStyle(style);
        return t;
    }

    private Label createFontSizeLabel() {
        var label = new Label();
        label.setPadding(new Insets(5, 40, 5, 10));
        return label;
    }

    private Hyperlink createHyperlink(String text, boolean visited, boolean disabled) {
        var h = new Hyperlink(text);
        h.setVisited(visited);
        h.setDisable(disabled);
        return h;
    }
}
