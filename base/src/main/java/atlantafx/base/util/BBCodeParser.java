/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import org.jspecify.annotations.Nullable;

/**
 * A simple push parser for the <a href="https://www.bbcode.org/">BBCode</a> markup.
 * As the content is parsed, methods of {@link BBCodeHandler} are called.<br/><br/>
 *
 * <p>The parser doesn't impose restrictions on tag names or tag params.
 * It's a handler implementation responsibility to differentiate supported
 * tags from unsupported and so to for the tag params. This allows user to utilize
 * arbitrary tags or params without changing the parser behaviour. The parser,
 * however, verifies that each opening tag has the matching closing tag.
 *
 * <p>If parsing is failed due to invalid input an {@link IllegalStateException}
 * will be thrown.
 */
public class BBCodeParser {

    /**
     * Reserved tag names. Note, this isn't the list of supported codes
     * as the {@link BBCodeHandler} is responsible for implementation.
     */
    public static final Set<String> RESERVED_TAGS = Set.of(
        // supported by the default handler
        "abbr", "align", "b", "caption", "center", "code", "color", "email", "heading",
        "font", "hr", "i", "indent", "label", "left", "li", "ol",
        "right", "s", "size", "small", "span", "style", "sub", "sup", "u", "ul", "url",
        // reserved
        "alert", "em", "fieldset", "h1", "h2", "h3", "h4", "icon", "img", "info", "kbd",
        "list", "media", "plain", "pre", "quote", "spoiler", "stop", "table", "tooltip",
        "td", "th", "tr", "warning"
    );

    private final String input;
    private final BBCodeHandler handler;
    private final Set<String> processedTags;
    private final Deque<String> openTags = new ArrayDeque<>();
    private int offset = 0;
    private int lastClosingPos = 0;

    /**
     * Creates a new parser.
     *
     * @see #BBCodeParser(String, BBCodeHandler, Set).
     */
    public BBCodeParser(String input, BBCodeHandler handler) {
        this(input, handler, RESERVED_TAGS);
    }

    /**
     * Creates a new parser.
     *
     * @param input   An input non-null string.
     * @param handler A {@link BBCodeHandler} implementation.
     * @param tags    The list of processed tags, i.e. the tags that parser won't ignore.
     */
    public BBCodeParser(String input, BBCodeHandler handler, @Nullable Set<String> tags) {
        this.input = Objects.requireNonNull(input, "Input can't be null.");
        this.handler = Objects.requireNonNull(handler, "Handler can't be null.");
        this.processedTags = Objects.requireNonNullElse(tags, RESERVED_TAGS);
    }

    /**
     * Starts input parsing.
     * There's no way to stop the process until parsing is finished.
     */
    public void parse() {
        handler.startDocument(input.toCharArray());

        while (offset < input.length()) {
            if (input.charAt(offset) == '[') {
                int openBracketPos = offset;
                int closeBracketPos = input.indexOf(']', offset);

                // a single square bracket, isn't a part of the markup
                if (closeBracketPos == -1) {
                    offset++;
                    continue;
                }

                int tagLength = closeBracketPos - openBracketPos + 1;

                // empty brackets, isn't a part of the markup
                if (tagLength == 2) {
                    offset++;
                    continue;
                }

                if (input.charAt(openBracketPos + 1) != '/') {
                    // push leading and intermediate characters
                    if (openTags.isEmpty()) {
                        handleCharacters(
                            lastClosingPos > 0 ? lastClosingPos + 1 : 0,
                            lastClosingPos > 0 ? offset - lastClosingPos - 1 : offset
                        );
                    }

                    boolean selfClose = input.charAt(closeBracketPos - 1) == '/';
                    var isKnownTag = handleStartTag(openBracketPos, tagLength, selfClose);

                    // an unknown "opened tag", and we are not inside opened known tag
                    if (!isKnownTag && openTags.isEmpty()) {
                        handleCharacters(openBracketPos, tagLength);
                        offset += tagLength;
                        lastClosingPos = closeBracketPos + 1;
                        continue;
                    }

                    // move input cursor immediately, because self-close tags can't contain text
                    if (selfClose) {
                        lastClosingPos = closeBracketPos;
                    }
                } else {
                    var isKnownTag = handleEndTag(openBracketPos, tagLength);

                    // an unknown "closing tag", and we are not inside opened known tag
                    if (!isKnownTag && openTags.isEmpty()) {
                        handleCharacters(lastClosingPos, closeBracketPos - lastClosingPos + 1);
                    }

                    lastClosingPos = closeBracketPos;
                }

                offset = closeBracketPos + 1;
            } else {
                // increment offset for any other character
                offset++;
            }
        }

        if (!openTags.isEmpty()) {
            throw new IllegalStateException("Invalid BBCode: Opening tags without closing tags: " + openTags);
        }

        // push trailing characters
        if (lastClosingPos < input.length()) {
            handleCharacters(
                lastClosingPos > 0 ? lastClosingPos + 1 : lastClosingPos,
                lastClosingPos > 0 ? input.length() - lastClosingPos - 1 : input.length()
            );
        }

        handler.endDocument();
    }

    /**
     * See {@link #createLayout(String, Pane)}.
     */
    public static TextFlow createFormattedText(String input) {
        return createLayout(input, new TextFlow());
    }

    /**
     * See {@link #createLayout(String, Pane)}.
     */
    public static VBox createLayout(String input) {
        var b = new VBox(10);
        b.setAlignment(Pos.TOP_LEFT);
        return createLayout(input, b);
    }

    /**
     * Parses the given string using BBCode markup and returns corresponding layout.
     * This is a shorthand method for using the feature.
     *
     * @param input     The BBCode markup string.
     * @param container The root container.
     * @see BBCodeHandler
     */
    public static <T extends Pane> T createLayout(String input, T container) {
        var handler = new BBCodeHandler.Default<>(container);

        var parser = new BBCodeParser(input, handler);
        parser.parse();

        return container;
    }

    ///////////////////////////////////////////////////////////////////////////

    protected boolean handleStartTag(int start, int length, boolean selfClose) {
        List<String> tokens = splitBySpace(input, start + 1, !selfClose ? length - 1 : length - 2);

        // only the name and param names are case-insensitive, param values aren't
        String name = tokens.get(0).toLowerCase();

        Map<String, String> params = null;
        for (int i = 0; i < tokens.size(); i++) {
            var token = tokens.get(i);
            int separatorPos = token.indexOf("=");

            if (separatorPos >= 0) {
                String key = token.substring(0, separatorPos).toLowerCase();
                String value = token.substring(separatorPos + 1);

                if (params == null) {
                    params = new HashMap<>();
                }

                // some bb codes use the format "[name=value]text[/name]",
                // in that case params map should have just a single key
                // which is exactly the same as the tag name
                if (i == 0) {
                    name = key;
                }

                params.put(key, value);
            }
        }

        if (!processedTags.contains(name)) {
            return false;
        }

        handler.startTag(name, params, start, length);

        if (!selfClose) {
            openTags.push(name);
        }

        return true;
    }

    protected boolean handleEndTag(int start, int length) {
        // ignore case
        String name = input.substring(start + 2, start + length - 1).toLowerCase();

        if (!processedTags.contains(name)) {
            return false;
        }

        if (openTags.isEmpty()) {
            throw new IllegalStateException(
                "Invalid BBCode: Closing tag without corresponding opening tag: '" + name + "'"
            );
        }

        String lastTag = openTags.pop();
        if (!lastTag.equals(name)) {
            throw new IllegalStateException(
                "Invalid BBCode: Closing tag '" + name + "' does not match opening tag '" + lastTag + "'"
            );
        }

        handler.endTag(name, start, length);

        return true;
    }

    protected void handleCharacters(int start, int length) {
        handler.characters(start, length);
    }

    /**
     * Splits input string by whitespace ignoring quoted text. E.g.
     * <pre>
     * "foo bar" = ["foo", "bar"]
     * "foo 'bar baz'" = ["foo", "bar baz"]
     * </pre>
     */
    protected List<String> splitBySpace(String str, int start, int length) {
        var tokens = new ArrayList<String>();
        var sb = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = start; i < start + length - 1; i++) {
            char ch = str.charAt(i);

            if (ch == ' ' && !insideQuotes) {
                tokens.add(sb.toString());
                sb = new StringBuilder();
            } else {
                if (ch == '"' || ch == '\'') {
                    insideQuotes = !insideQuotes;
                } else {
                    // remove quotes from param value,
                    // works for [name="value"] format as well
                    sb.append(ch);
                }
            }
        }

        tokens.add(sb.toString());

        return tokens;
    }
}
