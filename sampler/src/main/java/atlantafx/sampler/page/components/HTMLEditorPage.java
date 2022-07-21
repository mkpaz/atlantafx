/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import javafx.scene.web.HTMLEditor;

public class HTMLEditorPage extends AbstractPage {

    public static final String NAME = "HTMLEditor";

    public HTMLEditorPage() {
        super();
        createView();
    }

    private void createView() {
        var editor = new HTMLEditor();
        editor.setPrefHeight(400);
        editor.setHtmlText(String.join("<br/><br/>", FAKER.lorem().paragraphs(5)));

        userContent.getChildren().setAll(editor);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
