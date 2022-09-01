/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.general;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.ThemeEvent.EventType;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.util.NodeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static atlantafx.base.theme.Styles.*;

public class TypographyPage extends AbstractPage {

    private static final double CONTROL_WIDTH = 200;
    private static final String DEFAULT_FONT_ID = "Default";

    public static final String NAME = "Typography";

    @Override
    public String getName() { return NAME; }

    private Pane fontSizeSampleContent;

    public TypographyPage() {
        super();
        createView();
        ThemeManager.getInstance().addEventListener(e -> {
            if (e.eventType() == EventType.FONT_FAMILY_CHANGE || e.eventType() == EventType.FONT_SIZE_CHANGE) {
                // only works for managed nodes
                updateFontInfo(Duration.ofMillis(1000));
            }
        });
    }

    private void createView() {
        var controlsGrid = new GridPane();
        controlsGrid.setVgap(10);
        controlsGrid.setHgap(20);

        controlsGrid.add(new Label("Font family"), 0, 0);
        controlsGrid.add(fontFamilyChooser(), 1, 0);
        controlsGrid.add(new Label("Font size"), 0, 1);
        controlsGrid.add(fontSizeSpinner(), 1, 1);

        var fontSizeSample = fontSizeSample();
        fontSizeSampleContent = (Pane) fontSizeSample.getContent();

        userContent.getChildren().setAll(
                controlsGrid,
                fontSizeSample.getRoot(),
                fontWeightSample().getRoot(),
                fontStyleSample().getRoot(),
                hyperlinkSample().getRoot(),
                textColorSample().getRoot(),
                textFlowSample().getRoot()
        );
        // if you want to enable quick menu don't forget that
        // font size spinner value have to be updated accordingly
        NodeUtils.toggleVisibility(quickConfigBtn, false);
        NodeUtils.toggleVisibility(sourceCodeToggleBtn, false);
    }

    private ComboBox<String> fontFamilyChooser() {
        final var tm = ThemeManager.getInstance();

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().add(tm.isDefaultFontFamily() ? DEFAULT_FONT_ID : tm.getFontFamily());
        comboBox.getItems().addAll(FXCollections.observableArrayList(Font.getFamilies()));
        comboBox.setPrefWidth(CONTROL_WIDTH);
        comboBox.getSelectionModel().select(tm.getFontFamily());

        comboBox.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                tm.setFontFamily(DEFAULT_FONT_ID.equals(val) ? ThemeManager.DEFAULT_FONT_FAMILY_NAME : val);
                tm.reloadCustomCSS();
            }
        });

        return comboBox;
    }

    private Spinner<Integer> fontSizeSpinner() {
        final var tm = ThemeManager.getInstance();

        var spinner = new Spinner<Integer>(10, 24, tm.getFontSize());
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner.setPrefWidth(CONTROL_WIDTH);

        // Instead of this we should obtain font size from a rendered node.
        // But since it's not trivial (thanks to JavaFX doesn't expose relevant API)
        // we just keep current font size inside ThemeManager singleton.
        // It works fine if ThemeManager default font size value matches
        // default theme font size value.
        spinner.getValueFactory().setValue(tm.getFontSize());

        spinner.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                tm.setFontSize(val);
                tm.reloadCustomCSS();
                updateFontInfo(Duration.ofMillis(1000));
            }
        });

        return spinner;
    }

    // font metrics can only be obtained by requesting from a rendered node
    protected void onRendered() {
        super.onRendered();
        updateFontInfo(Duration.ZERO);
    }

    private void updateFontInfo(Duration delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    for (Node node : fontSizeSampleContent.getChildren()) {
                        if (node instanceof Text textNode) {
                            var font = textNode.getFont();
                            textNode.setText(
                                    String.format("%s = %.1fpx", textNode.getUserData(), Math.ceil(font.getSize()))
                            );
                        }
                    }
                });
            }
        }, delay.toMillis());
    }

    private SampleBlock fontSizeSample() {
        var grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(10);

        grid.add(text("Title 1", TITLE_1), 0, 0);
        grid.add(text("Title 2", TITLE_2), 0, 1);
        grid.add(text("Title 3", TITLE_3), 0, 2);
        grid.add(text("Title 4", TITLE_4), 0, 3);

        grid.add(text("Caption", TEXT_CAPTION), 1, 0);
        grid.add(text("Default"), 1, 1);
        grid.add(text("Small", TEXT_SMALL), 1, 2);

        grid.setAlignment(Pos.BASELINE_LEFT);

        return new SampleBlock("Font size", grid);
    }

    private SampleBlock fontWeightSample() {
        var sample1 = new HBox(10,
                text("Bold", TEXT_BOLD),
                text("Bolder", TEXT_BOLDER),
                text("Normal", TEXT_NORMAL),
                text("Lighter", TEXT_LIGHTER)
        );
        sample1.setAlignment(Pos.BASELINE_LEFT);

        var sample2 = new HBox(10,
                textInlineStyle("900", "-fx-font-weight:900;"),
                textInlineStyle("800", "-fx-font-weight:800;"),
                textInlineStyle("700", "-fx-font-weight:700;"),
                textInlineStyle("600", "-fx-font-weight:600;"),
                textInlineStyle("500", "-fx-font-weight:500;"),
                textInlineStyle("400", "-fx-font-weight:400;"),
                textInlineStyle("300", "-fx-font-weight:300;"),
                textInlineStyle("200", "-fx-font-weight:200;"),
                textInlineStyle("100", "-fx-font-weight:100;")
        );
        sample2.setAlignment(Pos.BASELINE_LEFT);

        var sample3 = new HBox(10,
                textInlineStyle("900", "-fx-font-family:'Inter Black';"),
                textInlineStyle("800", "-fx-font-family:'Inter Extra Bold';"),
                textInlineStyle("700", "-fx-font-family:'Inter Bold';"),
                textInlineStyle("600", "-fx-font-family:'Inter Semi Bold';"),
                textInlineStyle("500", "-fx-font-family:'Inter Medium';"),
                textInlineStyle("400", "-fx-font-family:'Inter Regular';"),
                textInlineStyle("300", "-fx-font-family:'Inter Light';"),
                textInlineStyle("200", "-fx-font-family:'Inter Extra Light';"),
                textInlineStyle("100", "-fx-font-family:'Inter Thin';")
        );
        sample3.setAlignment(Pos.BASELINE_LEFT);

        // JDK-8090423: https://bugs.openjdk.org/browse/JDK-8090423
        // Workaround:  https://edencoding.com/resources/css_properties/fx-font-weight/
        return new SampleBlock("Font weight", new VBox(10,
                sample1,
                sample2,
                sample3,
                text("JavaFX only supports Bold or Regular font weight. See the source code for workaround.",
                        TEXT, TEXT_SMALL, DANGER
                )
        ));
    }

    private SampleBlock fontStyleSample() {
        var box = new HBox(10,
                text("Italic", TEXT_ITALIC),
                text("Oblique", TEXT_OBLIQUE),
                text("Underlined", TEXT_UNDERLINED),
                text("Strikethrough", TEXT_STRIKETHROUGH)
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        return new SampleBlock("Font style", box);
    }

    private SampleBlock textColorSample() {
        var box = new HBox(10,
                text("Accent", TEXT, ACCENT),
                text("Success", TEXT, SUCCESS),
                text("Warning", TEXT, WARNING),
                text("Danger", TEXT, DANGER)
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        return new SampleBlock("Text color", box);
    }

    private SampleBlock hyperlinkSample() {
        var linkNormal = hyperlink("_Normal", false, false);
        linkNormal.setMnemonicParsing(true);

        var linkVisited = hyperlink("_Visited", true, false);
        linkVisited.setMnemonicParsing(true);

        var box = new HBox(10,
                linkNormal,
                linkVisited,
                hyperlink("Disabled", false, true)
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        return new SampleBlock("Hyperlink", box);
    }

    private Text text(String text, String... styleClasses) {
        var t = new Text(text);
        t.getStyleClass().addAll(styleClasses);
        t.setUserData(text);
        return t;
    }

    private Text textInlineStyle(String text, String style) {
        var t = new Text(text);
        t.setStyle(style);
        return t;
    }

    private Hyperlink hyperlink(String text, boolean visited, boolean disabled) {
        var h = new Hyperlink(text);
        h.setVisited(visited);
        h.setDisable(disabled);
        return h;
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
                new Text(", eget facilisis enim. Suspendisse potenti. Nulla euismod, nisl sed dapibus pretium, augue ligula finibus arcu, in iaculis nulla neque a est. Sed in rutrum diam. Donec quis arcu molestie, facilisis ex fringilla, "),
                new Hyperlink("volutpat velit"),
                new Text(".")
        );

        return new SampleBlock("Text flow", textFlow);
    }
}
