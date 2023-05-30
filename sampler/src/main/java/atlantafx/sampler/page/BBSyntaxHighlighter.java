/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import atlantafx.base.util.BBCodeParser;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.text.TextFlow;

// This code is adapted from RichTextFX JavaKeywordsDemo:
// https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos
public final class BBSyntaxHighlighter {

    private static final String[] KEYWORDS = new String[] {
        "abstract", "assert", "boolean", "break", "byte",
        "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float",
        "for", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "super",
        "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while",
        "var", "record", "with", "yield", "sealed", "non-sealed"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"
        + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    // Enclose keywords to BBCode tags.
    public static String format(String text) {
        Matcher m = PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        int lastKwEnd = 0;

        while (m.find()) {
            String styleClass =
                m.group("KEYWORD") != null ? "keyword" :
                    m.group("PAREN") != null ? "paren" :
                        m.group("STRING") != null ? "string" :
                            m.group("COMMENT") != null ? "comment" :
                                null;

            sb.append(text, lastKwEnd, m.start())
                .append("[span='")
                .append(Objects.requireNonNullElse(styleClass, "absent"))
                .append("']")
                .append(text, m.start(), m.end())
                .append("[/span]");
            lastKwEnd = m.end();
        }
        sb.append(text, lastKwEnd, text.length());

        return sb.toString();
    }

    public static TextFlow highlight(String text) {
        return BBCodeParser.createFormattedText(format(text));
    }
}
