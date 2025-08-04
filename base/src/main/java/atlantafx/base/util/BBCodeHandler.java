/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import atlantafx.base.theme.Styles;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.jspecify.annotations.Nullable;

/**
 * The basic handler interface for the {@link BBCodeParser} that will
 * receive notifications while processing user input text.
 */
public interface BBCodeHandler {

    /**
     * Notifies that parsing has started.
     *
     * @param doc parser input string
     */
    void startDocument(char[] doc);

    /**
     * Notifies that parsing has finished.
     */
    void endDocument();

    /**
     * Notifies about the start of the tag.
     * In case of self-closing tag this also notifies about the end of the tag.
     *
     * @param name   The tag name.
     * @param params The tag params.
     * @param start  The tag start position, i.e. the position of open square bracket (not the tag name start).
     * @param length The tag length, including closing bracket.
     */
    void startTag(String name, @Nullable Map<String, String> params, int start, int length);

    /**
     * Notifies about the end of the tag.
     * In case of self-closing tag only {@link #startTag(String, Map, int, int)} method is called.
     *
     * @param name   The tag name.
     * @param start  The tag start position, i.e. the position of open square bracket (not the tag name start).
     * @param length The tag length, including closing bracket.
     */
    void endTag(String name, int start, int length);

    /**
     * Notifies about characters data that doesn't belong to any tag, i.e.
     * leading, intermediate or trailing text.
     *
     * @param start  The text start position.
     * @param length The text length.
     */
    void characters(int start, int length);

    ///////////////////////////////////////////////////////////////////////////

    /**
     * A basic {@link BBCodeHandler} implementation.<br/><br/>
     *
     * <p>While parsing all created nodes will be added to the given root container.
     * The container choice depends on the actual markup. Default constructor accepts any
     * {@link Pane} or its descendant. Using the{@link TextFlow} for text-only markup
     * (no block nodes) and {@link VBox} otherwise, is recommended.<br/><br/>
     *
     * <h3>Supported tags</h3><br/>
     * <pre>
     * Bold Text          : text  : [b]{text}[/b]
     * Italic Text        : text  : [i]{text}[/i]
     * Underlined Text    : text  : [u]{text}[/u]
     * Strikethrough Text : text  : [s]{text}[/s]
     * Font Color         : text  : [color={color}]{text}[/color]
     * Font Family        : text  : [font={monospace}]{text}[/font]
     * Font Size          : text  : [size={size}]{text}[/size]
     * Link               : text  : [url={url}]{text}[/url]
     *                              [url url={url} class={class}]{text}[/url]
     * Email              : text  : [email]{text}[/email]
     *                              [email email={url} class={class}]{text}[/email]
     * Style              : text  : [style={style}]{text}[/style]
     * Subscript          : text  : [sub]{text}[/sub]
     * Superscript        : text  : [sup]{text}[/sup]
     * Heading            : text  : [heading]{text}[/heading]
     *                              [heading={level}]{text}[/heading]
     * Code               : text  : [code]{text}[/code]
     *                              [code={class}]{text}[/code]
     * Span               : text  : [span]{text}[/span]
     *                              [span={class}]{text}[/span]
     *                              [span style={style} class={class}]{text}[/span]
     * Label              : text  : [label]{text}[/label]
     *                              [label={class}]{text}[/label]
     *                              [label style={style} class={class}]{text}[/label]
     * Caption Text       : text  : [caption]{text}[/caption]
     * Small Text         : text  : [small]{text}[/small]
     * Abbreviation       : text  : [abbr="tooltip text"]{text}[/abbr]
     * Unordered List     : block : [ul]
     *                              [li]Entry 1[/li]
     *                              [li]Entry 2[/li]
     *                              [/ul]
     *                              [ul={bullet character}]
     *                              [li]Entry 1[/li]
     *                              [li]Entry 2[/li]
     *                              [/ul]
     * Ordered List       : block : [ol]
     *                              [li]Entry 1[/li]
     *                              [li]Entry 2[/li]
     *                              [/ol]
     *                              [ol={start number or letter}]
     *                              [li]Entry 1[/li]
     *                              [li]Entry 2[/li]
     *                              [/ol]
     * Alignment          : block : [left]{content}[/left]
     *                              [center]{content}[/center]
     *                              [right]{content}[/right]
     *                              [align={javafx.geometry.Pos}]{content}[/align]
     * Indentation        : block : [indent]{content}[/indent]
     *                              [indent=level]{content}[/indent]
     * Horizontal Rule    : block : [hr/]
     *                              [hr=thickness/]
     * </pre>
     *
     * <ul>
     * <li>If a tag param contains whitespaces or trailing slash is must be
     * enclosed in double or single quotes.
     * <li>If a tag only has a single param, it can be shortened to the
     * {@code [name=value]{text}[/name]}. In this case the tag param name
     * considered to be equal to the tag name.
     * <li>Unknown tag params will be ignored.
     * </ul>
     *
     * <h3>Action Events</h3><br/>
     * Some nodes, e.g. {@link Hyperlink} require action handlers. To avoid traversing
     * the root container's node graph you can add an event filter.
     *
     * <pre>{@code
     * var input = "Visit the [url=https://example.com]website[/url].";
     * var textFlow = BBCodeParser.createLayout(input);
     * textFlow.addEventFilter(ActionEvent.ACTION, e-> {
     *     if (e.getTarget() instanceof Hyperlink link) {
     *         openURL(link.getUserData());
     *     }
     *     e.consume();
     * });}
     * </pre>
     */
    class Default<T extends Pane> implements BBCodeHandler {

        protected static final int OL_LETTER_OFFSET = 100_000;

        protected final Block root;
        protected final Deque<Tag> openTags = new ArrayDeque<>();
        protected final Deque<Block> openBlocks = new ArrayDeque<>();
        protected char @Nullable[] doc;
        protected int textCursor;

        /**
         * Creates a new handler instance.
         *
         * @param root root container
         */
        public Default(T root) {
            Objects.requireNonNull(root, "Root container cannot be null.");
            this.root = root instanceof TextFlow tf ? new Block(root, tf) : new Block(root, new TextFlow());
            this.root.node().getStyleClass().add("bb-code");
        }

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
            Tag tag = createTag(name, params);

            // ignore unknown tags
            if (tag == null) {
                return;
            }

            // append all text before the current cursor position
            if (!openTags.isEmpty()) {
                appendTextToCurrentBranch(openTags.getFirst(), textCursor, start - textCursor);
            }

            if (!tag.isSelfClose()) {
                // push newly opened tag on top
                openTags.addFirst(tag);

                // if tag is a block or an inline block, update branch reference
                if (tag.isBlock()) {
                    createBranch();
                }

            } else {
                appendSelfCloseTag(tag);
            }

            // move text cursor to the first char inside the new tag
            // (or to the first char after the self-close tag)
            textCursor = start + length;
        }

        @Override
        public void endTag(String name, int start, int length) {
            // closing the first tag node from stack
            Tag tag = openTags.getFirst();

            // append all text before the current cursor position
            // and move text cursor to the first char after the closing tag
            appendTextToCurrentBranch(tag, textCursor, start - textCursor);
            textCursor = start + length;

            openTags.removeFirst(); // close tag
            if (tag.isBlock()) {    // return to the parent node
                openBlocks.removeFirst();
            }
        }

        @Override
        public void characters(int start, int length) {
            if (length > 0 && doc != null) {
                var text = new Text(new String(doc, start, length));

                if (root.node() instanceof TextFlow) {
                    // support special use case for simple markup
                    root.children().add(text);
                } else {
                    // otherwise text is always appended to the root,
                    // this also creates a new TextFlow if necessary
                    appendTextToRoot(text);
                }
            }
        }

        protected @Nullable Tag createTag(String name, @Nullable Map<String, String> params) {
            Tag.Type tagType = null;

            // all styles added here will be inherited by nested tags
            Set<String> stylesClass = new HashSet<>();
            Set<String> style = new HashSet<>();

            // all supported tags must be listed here
            switch (name) {
                // == TEXT STYLE ==
                case "b" -> {
                    stylesClass.add(Styles.TEXT_BOLD);
                    tagType = Tag.Type.TEXT;
                }
                case "i" -> {
                    stylesClass.add(Styles.TEXT_ITALIC);
                    tagType = Tag.Type.TEXT;
                }
                case "u" -> {
                    stylesClass.add(Styles.TEXT_UNDERLINED);
                    tagType = Tag.Type.TEXT;
                }
                case "s" -> {
                    stylesClass.add(Styles.TEXT_STRIKETHROUGH);
                    tagType = Tag.Type.TEXT;
                }
                case "color" -> {
                    addStyleIfPresent(params, "-fx-fill", "color", style);
                    tagType = Tag.Type.TEXT;
                }
                case "font" -> {
                    addStyleIfPresent(params, "-fx-font-family", "font", style);
                    tagType = Tag.Type.TEXT;
                }
                case "style" -> {
                    addStyleIfPresent(params, "style", style, ";");
                    tagType = Tag.Type.TEXT;
                }
                case "sub" -> {
                    stylesClass.add("sub");
                    tagType = Tag.Type.TEXT;
                }
                case "sup" -> {
                    stylesClass.add("sup");
                    tagType = Tag.Type.TEXT;
                }

                // == TEXT SIZE ==
                case "heading" -> {
                    if (params != null && params.containsKey("heading")) {
                        stylesClass.add("title-" + getParamOrDefault(params, "heading", "3"));
                    }
                    stylesClass.add("heading");
                    tagType = Tag.Type.TEXT;
                }
                case "caption" -> {
                    stylesClass.add(Styles.TEXT_CAPTION);
                    tagType = Tag.Type.TEXT;
                }
                case "size" -> {
                    addStyleIfPresent(params, "-fx-font-size", "size", style);
                    tagType = Tag.Type.TEXT;
                }
                case "small" -> {
                    stylesClass.add(Styles.TEXT_SMALL);
                    tagType = Tag.Type.TEXT;
                }

                // LINKS
                case "url", "email" -> {
                    // support style class to differentiate controls in action handler
                    addStyleIfPresent(params, "class", stylesClass, " ");
                    tagType = Tag.Type.TEXT;
                }

                // == CUSTOM FORMATTING ==
                case "code" -> {
                    stylesClass.add("code");
                    tagType = Tag.Type.TEXT;
                }
                case "span" -> {
                    addStyleIfPresent(params, "span", stylesClass, " ");
                    addStyleIfPresent(params, "class", stylesClass, " ");
                    addStyleIfPresent(params, "style", style, ";");
                    tagType = Tag.Type.TEXT;
                }
                case "label" -> {
                    addStyleIfPresent(params, "label", stylesClass, " ");
                    addStyleIfPresent(params, "class", stylesClass, " ");
                    addStyleIfPresent(params, "style", style, ";");
                    tagType = Tag.Type.TEXT;
                }

                // INTERACTIVE
                case "abbr" -> {
                    stylesClass.add("abbr");
                    tagType = Tag.Type.TEXT;
                }

                // == BLOCKS ==
                case "ol", "ul" -> {
                    addStyleIfPresent(params, "class", stylesClass, " ");
                    tagType = Tag.Type.BLOCK;
                }
                case "align", "li", "left", "center", "right", "indent" -> tagType = Tag.Type.BLOCK;

                // == SELF-CLOSE ==
                case "hr" -> tagType = Tag.Type.SELF_CLOSE;
            }

            return tagType != null ? new Tag(name, tagType, params, stylesClass, style) : null;
        }

        protected void appendTextToRoot(Node node) {
            TextFlow parent;

            // append to the last TextFlow node, create one if not present
            if (root.isEmpty() || !(root.children().get(root.size() - 1) instanceof TextFlow)) {
                parent = new TextFlow();
                root.children().add(parent);
            } else {
                parent = (TextFlow) root.children().get(root.size() - 1);
            }

            parent.getChildren().add(node);
        }

        protected void appendTextToCurrentBranch(Tag tag, int textStart, int textLength) {
            if (textLength > 0 && doc != null) {
                Node node = createTextNode(tag, new String(doc, textStart, textLength));
                node.getStyleClass().addAll(getStyleClass()); // inherit all styles from stack
                node.setStyle(getStyle());

                // if branch is empty, start a new paragraph from root
                if (openBlocks.isEmpty()) {
                    appendTextToRoot(node);
                } else {
                    openBlocks.getFirst().addText(node);
                }
            }
        }

        protected Node createTextNode(Tag tag, String text) {
            return switch (tag.name()) {
                case "label", "code" -> new Label(text);
                case "url" -> {
                    var node = new Hyperlink(text);
                    node.setUserData(tag.getParam("url"));
                    node.getStyleClass().add("url");
                    yield node;
                }
                case "email" -> {
                    var node = new Hyperlink(text);
                    node.setUserData(tag.getParam("email"));
                    node.getStyleClass().add("email");
                    yield node;
                }
                case "abbr" -> {
                    var node = new Label(text);
                    String tooltip = tag.getParam("abbr");
                    if (tooltip != null) {
                        node.setTooltip(new Tooltip(tooltip));
                    }
                    yield node;
                }
                default -> {
                    var node = new Text(text);
                    node.getStyleClass().add(Styles.TEXT);
                    yield node;
                }
            };
        }

        protected void createBranch() {
            final var tag = openTags.getFirst();
            if (!tag.isBlock()) {
                return;
            }

            final var hgap = 10;
            final var vgap = 10;
            boolean isGrid = false;

            final Block parent = !openBlocks.isEmpty() ? openBlocks.getFirst() : root;
            final Block current = switch (tag.name()) {
                case "ul" -> {
                    var pane = new VBox(vgap);
                    pane.setUserData(tag.getParam("ul", "•"));
                    pane.getStyleClass().addAll("ul", "list");
                    yield new Block(pane, null);
                }
                case "ol" -> {
                    var grid = new GridPane();
                    grid.setVgap(vgap);
                    grid.setHgap(hgap);
                    grid.setUserData(getListStartNumber(tag.getParam("ol")));
                    grid.getStyleClass().addAll("ol", "list");
                    grid.getColumnConstraints().addAll(
                        new ColumnConstraints(
                            Region.USE_PREF_SIZE, -1, Region.USE_PREF_SIZE, Priority.NEVER, HPos.LEFT, false
                        ),
                        new ColumnConstraints(
                            -1, -1, -1, Priority.SOMETIMES, HPos.LEFT, false
                        )
                    );
                    yield new Block(grid, null);
                }
                case "li" -> {
                    var text = new TextFlow();

                    if (parent.node().getStyleClass().contains("ol") && parent.node() instanceof GridPane grid) {
                        isGrid = true;
                        // use Label instead of Text because of better vertical alignment
                        grid.addRow(grid.getRowCount(), new Label(getListItemNumber(grid)), text);
                        grid.getRowConstraints().add(
                            new RowConstraints(
                                -1, -1, -1, Priority.NEVER, VPos.TOP, false
                            )
                        );
                        yield new Block(text, text);
                    } else if (parent.node().getStyleClass().contains("ul")) {
                        var bullet = String.valueOf(parent.node().getUserData());
                        HBox pane = new HBox(hgap, new Text(bullet), text);
                        pane.setAlignment(Pos.BASELINE_LEFT);
                        pane.getStyleClass().add("li");
                        yield new Block(pane, text);
                    } else {
                        throw new UnsupportedOperationException("Invalid parent tag: 'ul' or 'ol' required.");
                    }
                }
                case "align" -> {
                    var text = new TextFlow();
                    var pane = new HBox(text);
                    pane.setAlignment(getEnumValue(Pos.class, tag.getParam("align"), Pos.TOP_LEFT));
                    yield new Block(pane, text);
                }
                case "left" -> {
                    var text = new TextFlow();
                    text.setTextAlignment(TextAlignment.LEFT);

                    var pane = new HBox(text);
                    pane.setAlignment(Pos.TOP_LEFT);

                    yield new Block(pane, text);
                }
                case "center" -> {
                    var text = new TextFlow();
                    text.setTextAlignment(TextAlignment.CENTER);

                    var pane = new HBox(text);
                    pane.setAlignment(Pos.TOP_CENTER);

                    yield new Block(pane, text);
                }
                case "right" -> {
                    var text = new TextFlow();
                    text.setTextAlignment(TextAlignment.RIGHT);

                    var pane = new HBox(text);
                    pane.setAlignment(Pos.TOP_RIGHT);

                    yield new Block(pane, text);
                }
                case "indent" -> {
                    var text = new TextFlow();
                    var pane = new HBox(text);
                    var indent = parseIntOrDefault(tag.getParam("indent"), 1);
                    pane.setPadding(new Insets(0, 0, 0, indent * 10));
                    pane.getStyleClass().add("indent");
                    yield new Block(pane, text);
                }

                default -> new Block(new VBox(), null);
            };

            // inherit all styles from stack
            current.node().getStyleClass().addAll(getStyleClass());

            var currentStyle = current.node().getStyle();
            var newStyle = getStyle();
            current.node().setStyle(
                currentStyle != null && !currentStyle.isEmpty() ? currentStyle + ";" + newStyle : getStyle()
            );

            if (!isGrid) { // grid requires col and row number
                parent.children().add(current.node());
            }

            openBlocks.push(current);
        }

        protected void appendSelfCloseTag(Tag tag) {
            final Block parent = !openBlocks.isEmpty() ? openBlocks.getFirst() : root;
            Node node = null;

            if ("hr".equals(tag.name())) {
                node = new HBox();
                node.getStyleClass().add("hr");
                var thickness = parseIntOrDefault(tag.getParam("hr"), 1);
                node.setStyle(String.format("-fx-border-width:0 0 %d 0", thickness));
            }

            if (node != null) {
                parent.children().add(node);
            }
        }

        protected void addStyleIfPresent(@Nullable Map<String,String> params,
                                         String name, String key, Collection<String> c) {
            if (params != null && params.containsKey(key)) {
                c.add(name + ":" + params.get(key));
            }
        }

        protected void addStyleIfPresent(@Nullable Map<String, String> params,
                                         String key, Collection<String> c, String sep) {
            if (params != null && params.containsKey(key)) {
                Collections.addAll(c, params.get(key).split(sep));
            }
        }

        protected <E extends Enum<E>> E getEnumValue(Class<E> c, @Nullable String value, E defaultValue) {
            if (value == null) {
                return defaultValue;
            }

            try {
                return Enum.valueOf(c, value.toUpperCase());
            } catch (Exception ignore) {
                return defaultValue;
            }
        }

        protected int getListStartNumber(@Nullable String param) {
            int start = 1;

            if (param == null) {
                return start;
            }

            try {
                start = Integer.parseInt(param);
            } catch (Exception ignore) {
                if (param.length() == 1 && Character.isLetter(param.charAt(0))) {
                    start = OL_LETTER_OFFSET + param.charAt(0);
                }
            }

            return start;
        }

        protected int parseIntOrDefault(@Nullable String s, int defaultValue) {
            if (s == null) {
                return defaultValue;
            }

            try {
                return Integer.parseInt(s);
            } catch (Exception ignore) {
                return defaultValue;
            }
        }

        protected String getParamOrDefault(@Nullable Map<String, String> params, String key, String defaultValue) {
            if (params != null) {
                return params.getOrDefault(key, defaultValue);
            } else {
                return defaultValue;
            }
        }

        protected String getListItemNumber(GridPane grid) {
            int start = (int) Objects.requireNonNullElse(grid.getUserData(), 1);
            return start < OL_LETTER_OFFSET
                ? (start + grid.getRowCount()) + "." // number
                : (char) (start + grid.getRowCount() - OL_LETTER_OFFSET) + "."; // letter
        }

        protected Set<String> getStyleClass() {
            // set keys cannot be overwritten,
            // if parent contains the same style class, child wins
            Iterator<Tag> it = openTags.descendingIterator();
            Set<String> styleClass = new LinkedHashSet<>();

            while (it.hasNext()) {
                var tag = it.next();
                if (tag.styleClasses() != null) {
                    styleClass.addAll(tag.styleClasses());
                }
            }

            return styleClass;
        }

        protected String getStyle() {
            // set keys cannot be overwritten,
            // if parent contains the same style rule, child wins
            Iterator<Tag> it = openTags.descendingIterator();
            Set<String> style = new LinkedHashSet<>();

            while (it.hasNext()) {
                var tag = it.next();
                if (tag.styles() != null) {
                    style.addAll(tag.styles());
                }
            }

            return String.join(";", style);
        }
    }

    /**
     * A generic block record.
     *
     * @param node The node that represents the block.
     * @param text The text content.
     */
    record Block(Pane node, @Nullable TextFlow text) {

        public Block {
            Objects.requireNonNull(node);
        }

        public void addText(Node child) {
            // Considering that markup can be generated at runtime the policy
            // is to silently ignore errors where possible. This can lead to
            // misaligned blocks or missed text at worst.
            if (canContainText()) {
                text.getChildren().add(child);
            }
        }

        public List<Node> children() {
            return node.getChildren();
        }

        public int size() {
            return node.getChildren().size();
        }

        public boolean isEmpty() {
            return node.getChildren().isEmpty();
        }

        public boolean canContainText() {
            return text != null;
        }
    }

    /**
     * Generic tag record.
     *
     * @param name         The tag name.
     * @param params       The tag params.
     * @param styleClasses The CSS classes.
     *                     Each element is either a single style or space delimited string.
     * @param styles       The CSS styles.
     *                     Each element is either a single style or semicolon delimited string.
     */
    record Tag(String name,
               Type type,
               @Nullable Map<String, String> params,
               @Nullable Set<String> styleClasses,
               @Nullable Set<String> styles) {

        public enum Type {
            BLOCK, TEXT, SELF_CLOSE
        }

        public Tag {
            Objects.requireNonNull(name);
            Objects.requireNonNull(type);
        }

        public boolean hasParam(String name) {
            return params != null && params.containsKey(name);
        }

        public @Nullable String getParam(String name) {
            return params != null ? params.get(name) : null;
        }

        public String getParam(String name, String defaultValue) {
            return params != null ? params.getOrDefault(name, defaultValue) : defaultValue;
        }

        public boolean isBlock() {
            return type == Type.BLOCK;
        }

        public boolean isSelfClose() {
            return type == Type.SELF_CLOSE;
        }
    }
}
