/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public final class Snippet {

    private final Class<?> sourceClass;
    private final int id;
    private HBox container = null;

    public Snippet(Class<?> sourceClass, int id) {
        this.sourceClass = Objects.requireNonNull(sourceClass, "sourceClass");
        this.id = id;
    }

    public Node render() {
        var snippet = getSourceCode();
        if (container == null && !snippet.isBlank()) {
            var textFlow = new TextFlow(BBSyntaxHighlighter.highlight(snippet));
            HBox.setHgrow(textFlow, Priority.ALWAYS);
            container = new HBox(textFlow);
            container.getStyleClass().add("snippet");
        }

        return Objects.requireNonNullElse(container, new TextFlow(new Text("Code snippet not found.")));
    }

    public String getSourceCode() {
        var sourceFileName = sourceClass.getSimpleName() + ".java";
        try (var stream = sourceClass.getResourceAsStream(sourceFileName)) {
            Objects.requireNonNull(stream, "Missing source file '" + sourceFileName + "';");

            var sourceCode = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            var startTag = "//snippet_" + id + ":start";
            var endTag = "//snippet_" + id + ":end";
            var start = sourceCode.indexOf(startTag);
            var end = sourceCode.indexOf(endTag);

            var snippet = "";
            if (start >= 0 && end >= 0) {
                snippet = sourceCode.substring(start + startTag.length(), end)
                    .stripIndent()
                    .trim();
            }

            return snippet;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
