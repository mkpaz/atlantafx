/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;

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
        var editor = new HTMLEditor();
        editor.setPrefHeight(400);
        editor.setHtmlText(String.join("<br/><br/>", FAKER.lorem().paragraphs(10)));
        return new SampleBlock("Playground", editor);
    }
}
