/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import static java.nio.charset.StandardCharsets.UTF_8;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.Resources;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.PageEvent;
import atlantafx.sampler.theme.HighlightJSTheme;
import atlantafx.sampler.util.NodeUtils;
import java.io.IOException;
import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

final class CodeViewer extends AnchorPane {

    private static final String HLJS_LIB = "assets/highlightjs/highlight.min.js";
    private static final String HLJS_SCRIPT = "hljs.highlightAll();hljs.initLineNumbersOnLoad();";

    private static final String HLJS_LN_LIB = "assets/highlightjs/highlightjs-line-numbers.min.js";
    private static final String HLJS_LN_CSS = ".hljs-ln-numbers { padding-right: 20px !important;;}";

    private WebView webView;

    public CodeViewer() {
        super();

        var closeBtn = new Button("Return", new FontIcon(Material2AL.ARROW_BACK));
        closeBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL, Styles.ACCENT);
        AnchorPane.setTopAnchor(closeBtn, 20d);
        AnchorPane.setRightAnchor(closeBtn, 20d);
        closeBtn.setOnAction(e ->
            DefaultEventBus.getInstance().publish(new PageEvent(PageEvent.Action.SOURCE_CODE_OFF))
        );

        getChildren().add(closeBtn);
        getStyleClass().add("code-viewer");
    }

    private void lazyLoadWebView() {
        if (webView == null) {
            webView = new WebView();
            NodeUtils.setAnchors(webView, Insets.EMPTY);
            getChildren().add(0, webView);
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
                .append(escapeHtml(new String(source.readAllBytes(), UTF_8)))
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

    private String escapeHtml(String s) {
        var out = new StringBuilder(Math.max(128, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
