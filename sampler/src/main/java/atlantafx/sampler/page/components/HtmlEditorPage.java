/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Theme;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.ThemeEvent;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.HighlightJSTheme;
import atlantafx.sampler.theme.ThemeManager;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;

public class HtmlEditorPage extends AbstractPage {

    private static final PseudoClass USE_LOCAL_URL = PseudoClass.getPseudoClass("use-local-url");

    public static final String NAME = "HTMLEditor";

    @Override
    public String getName() {
        return NAME;
    }

    private HTMLEditor editor = createEditor();

    public HtmlEditorPage() {
        super();

        setUserContent(new VBox(editorSample()));
        editor.requestFocus();

        // update editor colors on app theme change
        DefaultEventBus.getInstance().subscribe(ThemeEvent.class, e -> {
            if (ThemeManager.getInstance().getTheme() != null) {
                editor.setHtmlText(generateContent());
                editor.requestFocus();
            }
        });
    }

    private SampleBlock editorSample() {
        var description = new Text("""
            HTMLEditor toolbar buttons use images from 'com/sun/javafx/scene/control/skin/modena'.
            In opposite, since AtlantaFX themes are also distributed as single CSS files, it contains no images.
            Unfortunately reusing Modena resources isn't possible, because the package isn't opened in OpenJFX
            'module-info'.
            """
        );

        var fixToggle = new ToggleSwitch("Apply Fix");

        var content = new VBox(BLOCK_VGAP, editor, new TextFlow(description), fixToggle);
        content.setAlignment(Pos.CENTER);

        fixToggle.selectedProperty().addListener((obs, old, val) -> {
            // toolbar icons can't be changed back without creating new editor instance #javafx-bug
            try {
                editor = createEditor();
                editor.pseudoClassStateChanged(USE_LOCAL_URL, val);
                content.getChildren().set(0, editor);
                editor.requestFocus();
            } catch (Exception ignored) {
                // hush internal HTML editor errors, because everything
                // we do here is ugly hacks around legacy control anyway
            }
        });

        return new SampleBlock("Playground", content);
    }

    private HTMLEditor createEditor() {
        var editor = new HTMLEditor();
        editor.setPrefHeight(400);
        editor.setHtmlText(generateContent());
        return editor;
    }

    private String generateContent() {
        var tm = ThemeManager.getInstance();
        Theme samplerTheme = tm.getTheme();
        HighlightJSTheme hlTheme = tm.getMatchingSourceCodeHighlightTheme(samplerTheme);
        return "<!DOCTYPE html>"
            + "<html>"
            + "<body style=\""
            + "background-color:" + hlTheme.getBackground() + ";"
            + "color:" + hlTheme.getForeground() + ";"
            + "font-family:" + tm.getFontFamily() + ";"
            + "font-size:" + tm.getFontSize() + "px;"
            + "\">"
            + String.join("<br/><br/>", FAKER.lorem().paragraphs(10))
            + "</body>"
            + "</html>";
    }
}
