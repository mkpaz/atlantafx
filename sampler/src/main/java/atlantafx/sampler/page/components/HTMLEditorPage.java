/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;

import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

public class HTMLEditorPage extends AbstractPage {

    public static final String NAME = "HTMLEditor";

    @Override
    public String getName() { return NAME; }

    public HTMLEditorPage() {
        super();
        setUserContent(new VBox(
                editorSample()
        ));
    }

    private SampleBlock editorSample() {
        var description = new Text(
                "HTMLEditor toolbar buttons use images from 'com/sun/javafx/scene/control/skin/modena'. " +
                "In opposite, since AtlantaFX themes are also distributed as single CSS files, it contains no images. " +
                "Unfortunately reusing Modena resources isn't possible, because the package isn't opened in OpenJFX 'module-info'."
        );
        description.getStyleClass().addAll(Styles.TEXT, Styles.WARNING);

        var editor = new HTMLEditor();
        editor.setPrefHeight(400);
        editor.setHtmlText(String.join("<br/><br/>", FAKER.lorem().paragraphs(10)));
        return new SampleBlock("Playground", new VBox(BLOCK_VGAP, new TextFlow(description), editor));
    }
}
