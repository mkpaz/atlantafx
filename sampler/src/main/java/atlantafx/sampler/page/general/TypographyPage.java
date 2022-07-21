/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.general;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static atlantafx.base.theme.Styles.*;

public class TypographyPage extends AbstractPage {

    public static final String NAME = "Typography";

    @Override
    public String getName() { return NAME; }

    private GridPane fontSizeBox;

    public TypographyPage() {
        super();
        createView();
    }

    private void createView() {
        var fontSizeSample = fontSizeSample();
        fontSizeBox = (GridPane) fontSizeSample.getContent();

        userContent.getChildren().setAll(
                fontSizeSample.getRoot(),
                fontWeightSample().getRoot(),
                fontStyleSample().getRoot(),
                hyperlinkSample().getRoot(),
                textColorSample().getRoot(),
                textFlowSample().getRoot()
        );
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
        var box = new HBox(10,
                           text("Bold", TEXT_BOLD),
                           text("Bolder", TEXT_BOLDER),
                           text("Normal", TEXT_NORMAL),
                           text("Lighter", TEXT_LIGHTER)
        );
        box.setAlignment(Pos.BASELINE_LEFT);

        return new SampleBlock("Font weight", box);
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

    // font metrics can only be obtained by requesting from a rendered node
    protected void onRendered() {
        for (Node node : fontSizeBox.getChildren()) {
            if (node instanceof Text textNode) {
                var font = textNode.getFont();
                textNode.setText(String.format("%s = %.1fpx",
                                               textNode.getText(),
                                               Math.ceil(font.getSize())
                ));
            }
        }
    }
}
