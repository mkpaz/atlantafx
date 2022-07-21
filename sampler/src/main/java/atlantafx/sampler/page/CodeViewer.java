/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page;

import atlantafx.sampler.Resources;
import atlantafx.sampler.theme.HighlightJSTheme;
import atlantafx.sampler.util.Containers;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CodeViewer extends AnchorPane {

    private static final String HLJS_LIB = "assets/highlightjs/highlight.min.js";
    private static final String HLJS_SCRIPT = "hljs.highlightAll();";

    private WebView webView;

    public CodeViewer() {
        getStyleClass().add("code-viewer");
    }

    private void lazyLoadWebView() {
        if (webView == null) {
            webView = new WebView();
            Containers.setAnchors(webView, Insets.EMPTY);
            getChildren().setAll(webView);
        }
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public void setContent(InputStream source, HighlightJSTheme theme) {
        lazyLoadWebView();

        try (var hljs = Resources.getResourceAsStream(HLJS_LIB)) {

            // NOTE:
            // Line numbers aren't here because Highlight JS itself doesn't support it
            // and highlighjs-line-numbers plugin break both indentation and colors.
            webView.getEngine().loadContent(
                    new StringBuilder()
                            .append("<html>")
                            .append("<head>")
                            .append("<style>").append(theme.getCss()).append("</style>")
                            .append("<script>").append(new String(hljs.readAllBytes(), UTF_8)).append("</script>")
                            .append("<script>" + HLJS_SCRIPT + "</script>")
                            .append("</head>")
                            // Transparent background is allowed starting from OpenJFX 18.
                            // https://bugs.openjdk.org/browse/JDK-8090547
                            // Until that it should match Highlight JS background.
                            .append(String.format("<body style=\"background-color:%s;\">", theme.getBackground()))
                            .append("<pre>")
                            .append("<code class=\"language-java\">")
                            .append(new String(source.readAllBytes(), UTF_8))
                            .append("</code>")
                            .append("</pre>")
                            .append("</body>")
                            .append("</html>")
                            .toString()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
