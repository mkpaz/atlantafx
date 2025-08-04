/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class BBCodeParserTest {

    @Test
    public void testEmptyString() {
        var handler = BBCodeMockHandler.testString("");
        assertThat(handler.tags()).isEmpty();
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testSingleBlankString() {
        var handler = BBCodeMockHandler.testString(" ");
        assertThat(handler.tags()).isEmpty();
        assertThat(handler.text()).containsExactlyInAnyOrder(" ");
    }

    @Test
    public void testNoMarkupString() {
        var handler = BBCodeMockHandler.testString("This_is_a_bold_text");
        assertThat(handler.tags()).isEmpty();
        assertThat(handler.text()).containsExactlyInAnyOrder("This_is_a_bold_text");
    }

    @Test
    public void testEmptyTag() {
        var handler = BBCodeMockHandler.testString("[b][/b]");
        assertThat(handler.tags()).containsExactly(new MockTag("b", null, ""));
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testUnknownTags() {
        var handler = BBCodeMockHandler.testString("This_is_[foo]a_bold[/bar]_text");
        assertThat(handler.tags()).isEmpty();
        assertThat(String.join("", handler.text())).isEqualTo("This_is_[foo]a_bold[/bar]_text");

        handler = BBCodeMockHandler.testString("[foo]This_is_a_bold_text[/bar]");
        assertThat(handler.tags()).isEmpty();
        assertThat(String.join("", handler.text())).isEqualTo("[foo]This_is_a_bold_text[/bar]");

        handler = BBCodeMockHandler.testString("[/foo]This_is_a_bold_text[bar]");
        assertThat(handler.tags()).isEmpty();
        assertThat(String.join("", handler.text())).isEqualTo("[/foo]This_is_a_bold_text[bar]");

        handler = BBCodeMockHandler.testString("[/foo]This_is_a_[b]bold[/b]_text[bar]");
        assertThat(handler.tags()).containsExactly(new MockTag("b", null, "bold"));
        assertThat(String.join("", handler.text())).isEqualTo("[/foo]This_is_a__text[bar]");

        handler = BBCodeMockHandler.testString("[b]This_is_a_[foo]bold[/bar]_text[/b]");
        assertThat(handler.tags()).containsExactly(
            new MockTag("b", null, "This_is_a_[foo]bold[/bar]_text")
        );
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testSingleTag() {
        var handler = BBCodeMockHandler.testString("[b]text[/b]");
        assertThat(handler.tags()).containsExactly(new MockTag("b", null, "text"));
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testLeadingTag() {
        var handler = BBCodeMockHandler.testString("[b]This[/b]_is_a_bold_text");
        assertThat(handler.tags()).containsExactly(new MockTag("b", null, "This"));
        assertThat(handler.text()).containsExactly("_is_a_bold_text");
    }

    @Test
    public void testIntermediateTag() {
        var handler = BBCodeMockHandler.testString("This_is_a_[b]bold[/b]_text");
        assertThat(handler.tags()).containsExactly(new MockTag("b", null, "bold"));
        assertThat(handler.text()).containsExactly("This_is_a_", "_text");
    }

    @Test
    public void testTrailingTag() {
        var handler = BBCodeMockHandler.testString("This_is_a_bold_[b]text[/b]");
        assertThat(handler.tags()).containsExactly(new MockTag("b", null, "text"));
        assertThat(handler.text()).containsExactly("This_is_a_bold_");
    }

    @Test
    public void testShorthandParamSyntax() {
        var handler = BBCodeMockHandler.testString("[b=foo]bold[/b]");
        assertThat(handler.tags()).containsExactly(new MockTag("b", Map.of("b", "foo"), "bold"));
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testShorthandParamSyntaxWithQuotes() {
        var handler = BBCodeMockHandler.testString("[b=\"foo bar\"]bold[/b]");
        assertThat(handler.tags()).containsExactly(new MockTag("b", Map.of("b", "foo bar"), "bold"));
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testSingleTagParam() {
        var handler = BBCodeMockHandler.testString("[b param=foo]bold[/b]");
        assertThat(handler.tags()).containsExactly(new MockTag("b", Map.of("param", "foo"), "bold"));
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testSingleTagParamWithQuotes() {
        var handler = BBCodeMockHandler.testString("[b param='foo bar']bold[/b]");
        assertThat(handler.tags()).containsExactly(new MockTag("b", Map.of("param", "foo bar"), "bold"));
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testMultipleTagParams() {
        var handler = BBCodeMockHandler.testString("[b param1=foo param2=bar param3=baz]bold[/b]");
        assertThat(handler.tags()).containsExactlyInAnyOrder(new MockTag(
            "b", Map.of("param1", "foo", "param2", "bar", "param3", "baz"), "bold")
        );
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testMultipleTagParamsWithQuotes() {
        var handler = BBCodeMockHandler.testString("[b param1=foo param2=\"a b\" param3='c d']bold[/b]");
        assertThat(handler.tags()).containsExactlyInAnyOrder(new MockTag(
            "b", Map.of("param1", "foo", "param2", "a b", "param3", "c d"), "bold")
        );
        assertThat(handler.text()).isEmpty();
    }

    @Test
    public void testSiblingTags() {
        var handler = BBCodeMockHandler.testString("This_[i]is[/i]_a_[b]bold[/b]_[s]text[/s]");
        assertThat(handler.tags()).containsExactly(
            new MockTag("i", null, "is"),
            new MockTag("b", null, "bold"),
            new MockTag("s", null, "text")
        );
        assertThat(handler.text()).containsExactly("This_", "_a_", "_");
    }

    @Test
    public void testNestedTags() {
        var handler = BBCodeMockHandler.testString("This_[i][s]is_[b]a[/b]_bold[/s]_text[/i]");
        assertThat(handler.text()).containsExactly("This_");
        assertThat(handler.tags()).containsExactly(
            new MockTag("b", null, "a"),
            new MockTag("s", null, "is_[b]a[/b]_bold"),
            new MockTag("i", null, "[s]is_[b]a[/b]_bold[/s]_text")
        );
    }

    @Test
    public void testEqualNestedTags() {
        var handler = BBCodeMockHandler.testString("This_[b]is_[b]a[/b]_bold[/b]_text");
        assertThat(handler.text()).containsExactly("This_", "_text");
        assertThat(handler.tags()).containsExactly(
            new MockTag("b", null, "a"),
            new MockTag("b", null, "is_[b]a[/b]_bold")
        );
    }

    @Test
    public void testNestedTagsWithParams() {
        var handler = BBCodeMockHandler.testString("This_[i=foo][s]is_[b bar=baz]a[/b]_bold[/s]_text[/i]");
        assertThat(handler.text()).containsExactly("This_");
        assertThat(handler.tags()).containsExactly(
            new MockTag("b", Map.of("bar", "baz"), "a"),
            new MockTag("s", null, "is_[b bar=baz]a[/b]_bold"),
            new MockTag("i", Map.of("i", "foo"), "[s]is_[b bar=baz]a[/b]_bold[/s]_text")
        );
    }

    @Test
    public void testUnclosedTagThrowsException() {
        assertThatThrownBy(() -> BBCodeMockHandler.testString("This_is_[b]a_bold_text"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testNestedUnclosedTagThrowsException() {
        assertThatThrownBy(() -> BBCodeMockHandler.testString("This_is_[b]a_[i]bold_[/b]text"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testUnopenedTagThrowsException() {
        assertThatThrownBy(() -> BBCodeMockHandler.testString("This_is_[/b]a_bold_text"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testNestedUnopenedTagThrowsException() {
        assertThatThrownBy(() -> BBCodeMockHandler.testString("This_is_[b]a_[/i]bold_[/b]text"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testNotMatchingClosingTagThrowsException() {
        assertThatThrownBy(() -> BBCodeMockHandler.testString("This_is_[b]a_bold_[/i]text"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testTagInsideDoubleBracesThrowsException() {
        // this is known and won't be fixed
        assertThatThrownBy(() -> BBCodeMockHandler.testString("This_is_[[url=link]a_bold[/url]]_text"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testSelfCloseTag() {
        var handler = BBCodeMockHandler.testString("This_[hr/]is_a_[hr=5/]bold_text");
        assertThat(handler.tags()).containsExactly(
            new MockTag("hr", null, null),
            new MockTag("hr", Map.of("hr", "5"), null)
        );
        assertThat(handler.text()).containsExactlyInAnyOrder("This_", "is_a_", "bold_text");
    }

    ///////////////////////////////////////////////////////////////////////////

    public record MockTag(String name,
                          @Nullable Map<String, String> params,
                          @Nullable String text) {
    }

    @NullMarked
    public static class BBCodeMockHandler implements BBCodeHandler {

        private static final Set<String> SELF_CLOSE_TAGS = Set.of("hr", "img");
        private static final boolean DEBUG = false;
        private static final Map<String, String> PARAMS_PLACEHOLDER = new HashMap<>();

        private final List<MockTag> tags = new ArrayList<>();
        private final List<String> text = new ArrayList<>();
        private final Deque<Integer> textStart = new ArrayDeque<>();
        private final Deque<Map<String, String>> tagParams = new ArrayDeque<>();
        private char @Nullable[] doc;

        @Override
        public void startDocument(char[] doc) {
            this.doc = doc;
        }

        @Override
        public void endDocument() {
            this.doc = null;
        }

        @Override
        public void startTag(String name, @Nullable Map<String, String> params, int start, int length) {
            assertThat(doc).isNotNull();
            debug("START:" + name + "|" + params + "|" + start + "|" + length + "|" + new String(doc, start, length));

            if (!SELF_CLOSE_TAGS.contains(name)) {
                tagParams.push(params != null ? params : PARAMS_PLACEHOLDER);
                textStart.push(start + length);
            } else {
                tags.add(new MockTag(name, params, null));
                textStart.push(start + length);
            }
        }

        @Override
        public void endTag(String name, int start, int length) {
            assertThat(doc).isNotNull();
            debug("END:" + name + "|" + start + "|" + length + "|" + new String(doc, start, length));

            var params = tagParams.pop();
            var openTagTextStart = textStart.pop();
            tags.add(new MockTag(
                name,
                params != PARAMS_PLACEHOLDER ? params : null,
                new String(doc, openTagTextStart, start - openTagTextStart))
            );
        }

        @Override
        public void characters(int start, int length) {
            assertThat(doc).isNotNull();
            debug("CHARACTERS:" + new String(doc, start, length));

            if (length > 0) {
                text.add(new String(doc, start, length));
            }
        }

        private void debug(String s) {
            if (DEBUG) {
                System.out.println(s);
            }
        }

        public List<MockTag> tags() {
            return tags;
        }

        public List<String> text() {
            return text;
        }

        public @Nullable MockTag get(String name) {
            return tags.stream()
                .filter(tag -> name.equals(tag.name()))
                .findFirst()
                .orElse(null);
        }

        public static BBCodeMockHandler testString(String input) {
            var handler = new BBCodeMockHandler();
            var parser = new BBCodeParser(input, handler);
            parser.parse();
            return handler;
        }
    }
}
