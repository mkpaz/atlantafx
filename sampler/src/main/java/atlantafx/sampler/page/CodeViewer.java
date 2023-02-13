/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import static java.nio.charset.StandardCharsets.UTF_8;

import atlantafx.sampler.Resources;
import atlantafx.sampler.theme.HighlightJSTheme;
import atlantafx.sampler.util.Containers;
import java.io.IOException;
import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

public class CodeViewer extends AnchorPane {

    private static final String HLJS_LIB = "assets/highlightjs/highlight.min.js";
    private static final String HLJS_SCRIPT = "hljs.highlightAll();hljs.initLineNumbersOnLoad();";

    private static final String HLJS_LN_LIB = "assets/highlightjs/highlightjs-line-numbers.min.js";
    private static final String HLJS_LN_CSS = ".hljs-ln-numbers { padding-right: 20px !important;;}";

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

        try (var hljs = Resources.getResourceAsStream(HLJS_LIB);
             var hljsLineNum = Resources.getResourceAsStream(HLJS_LN_LIB)) {

            var content = new StringBuilder()
                .append("<!DOCTYPE html>") // mandatory for line numbers plugin
                .append("<html>")
                .append("<head>")
                .append("<style>").append(theme.getCss()).append("</style>")
                .append("<style>").append(HLJS_LN_CSS).append("</style>")
                .append("<script>").append(new String(hljs.readAllBytes(), UTF_8)).append("</script>")
                .append("<script>").append(new String(hljsLineNum.readAllBytes(), UTF_8)).append("</script>")
                .append("<script>" + HLJS_SCRIPT + "</script>")
                .append("</head>")
                .append("<body>")
                .append("<pre>")
                .append("<code class=\"language-java\">")
                .append(new String(source.readAllBytes(), UTF_8))
                .append("</code>")
                .append("</pre>")
                .append("</body>")
                .append("</html>")
                .toString();

            webView.setPageFill(Color.TRANSPARENT);
            webView.getEngine().loadContent(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
