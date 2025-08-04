/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.util.Range;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ListChangeListener;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.PaintConverter;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * The {@code SelectableTextFlow} class extends the functionality of the regular {@link TextFlow}
 * by providing the ability to select a range of text. The selected range is visually highlighted
 * and can be retrieved as a string using {@link #getSelectedRangeAsString()}.
 *
 * <p>Similar to the regular {@link TextFlow}, the {@code SelectableTextFlow} can contain any type of
 * nodes; however, for the purpose of text selection, it is recommended to limit the children to
 * {@link Text} nodes exclusively. It is important to note that each child {@link Text}
 * <b>must</b> accurately report its length, which may not be the case for emojis and font icons.
 */
public class SelectableTextFlow extends TextFlow {

    protected static final Predicate<Character> WORD_BOUNDARY_PREDICATE =
        c -> Character.isWhitespace(c) || !(Character.isAlphabetic(c) || Character.isDigit(c));

    protected final Selection selection = new Selection(this);
    protected int mouseDragStartPos = -1;

    /**
     * See {@link #SelectableTextFlow(Text...)}.
     */
    public SelectableTextFlow() {
        this((Text[]) null);
    }

    /**
     * Creates a new instance of {@code SelectableTextFlow} with the specified child {@code Text} elements.
     *
     * @param children the child {@code Text} elements to be added to the flow, or null for no children
     */
    public SelectableTextFlow(Text @Nullable... children) {
        super();

        setCursor(Cursor.TEXT);
        setPrefWidth(Region.USE_PREF_SIZE);

        getStyleClass().add("selectable-text");
        initListeners();

        if (children != null) {
            setText(children);
        }
    }

    //=========================================================================
    // Properties
    //=========================================================================

    /**
     * Specifies the {@link ContextMenu} associated with this component.
     *
     * <p>The context menu provides a set of options that can be displayed
     * when the user right-clicks on the component.
     */
    public final SimpleObjectProperty<@Nullable ContextMenu> contextMenuProperty() {
        return contextMenuProperty;
    }

    protected final SimpleObjectProperty<@Nullable ContextMenu> contextMenuProperty = new SimpleObjectProperty<>();

    public @Nullable ContextMenu getContextMenu() {
        return contextMenuProperty.get();
    }

    public void setContextMenu(@Nullable ContextMenu contextMenu) {
        this.contextMenuProperty.set(contextMenu);
    }

    /**
     * Specifies the color used for the font (foreground) of selected text.
     *
     * <p>Only applies to the first node in a TextFlow.
     * For more details, see <a href="https://bugs.openjdk.org/browse/JDK-8149134">JDK-8149134</a>.
     */
    public final ObjectProperty<Paint> highlightTextFillProperty() {
        return highlightTextFill;
    }

    protected final ObjectProperty<Paint> highlightTextFill = new StyleableObjectProperty<>(Color.BLUE) {

        @Override
        public Object getBean() {
            return SelectableTextFlow.this;
        }

        @Override
        public String getName() {
            return "highlightTextFill";
        }

        @Override
        public CssMetaData<SelectableTextFlow, Paint> getCssMetaData() {
            return StyleableProperties.HIGHLIGHT_TEXT_FILL;
        }
    };

    public Paint getHighlightTextFill() {
        return highlightTextFill.get();
    }

    public void setHighlightTextFill(Paint value) {
        highlightTextFill.set(value);
    }

    /**
     * Defines the word boundaries for text selection when double-clicking.
     * See also {@link #textSelectionOnMouseClickProperty}.
     */
    public final SimpleObjectProperty<Predicate<Character>> wordBoundaryPredicateProperty() {
        return wordBoundaryPredicate;
    }

    protected final SimpleObjectProperty<Predicate<Character>> wordBoundaryPredicate = new SimpleObjectProperty<>(
        this, "wordBoundaryPredicate", WORD_BOUNDARY_PREDICATE
    );

    public Predicate<Character> getWordBoundaryPredicate() {
        return wordBoundaryPredicateProperty().get();
    }

    public void setWordBoundaryPredicate(@Nullable Predicate<Character> wordBoundaryPredicate) {
        wordBoundaryPredicateProperty().set(Objects.requireNonNullElse(wordBoundaryPredicate, WORD_BOUNDARY_PREDICATE));
    }

    /**
     * Allows text selection by double-clicking on any word.
     *
     * <p>Default is {@code false}.
     */
    public final BooleanProperty textSelectionOnMouseClickProperty() {
        return textSelectionOnMouseClick;
    }

    private final BooleanProperty textSelectionOnMouseClick = new SimpleBooleanProperty(
        this, "textSelectionOnMouseClick", false
    );

    public boolean getTextSelectionOnMouseClick() {
        return textSelectionOnMouseClickProperty().get();
    }

    public void setTextSelectionOnMouseClick(boolean textSelectionOnMouseClick) {
        textSelectionOnMouseClickProperty().set(textSelectionOnMouseClick);
    }

    //=========================================================================
    // API
    //=========================================================================

    /**
     * Sets the text content of the TextFlow by replacing its children with the specified Text nodes.
     */
    public void setText(Text... children) {
        getChildren().setAll(children);
    }

    /**
     * Clears all the text content from the TextFlow by removing all its children.
     */
    public void clear() {
        getChildren().clear();
    }

    /**
     * Returns the content of the TextFlow as a string representation.
     */
    public @Nullable String getContentAsString() {
        return getContent().toString();
    }

    /**
     * Retrieves the currently selected text range as a string.
     */
    public String getSelectedRangeAsString() {
        return selection.getSelectedRangeAsString();
    }

    /**
     * Selects all text content.
     */
    public void selectAll() {
        clearSelection();

        var totalLength = getContentLength();
        if (totalLength > 0) {
            setSelectionPath();
            selection.select(0, totalLength + 1);
        }
    }

    /**
     * Selects a range of text content from the specified indices.
     *
     * @param fromInclusive the starting index, inclusive
     * @param toExclusive   the ending index, exclusive
     */
    public void selectRange(int fromInclusive, int toExclusive) {
        clearSelection();

        var totalLength = getContentLength();
        if (fromInclusive >= 0 && toExclusive <= totalLength + 1 && toExclusive > fromInclusive) {
            setSelectionPath();
            selection.select(fromInclusive, toExclusive);
        }
    }

    /**
     * Removes the current text selection.
     */
    public void clearSelection() {
        selection.clear();
    }

    /**
     * Copies the currently selected text range to the clipboard.
     */
    public void copySelectedRangeToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(getSelectedRangeAsString());
        clipboard.setContent(content);
    }

    //=========================================================================

    protected void initListeners() {
        // bind fill property to any newly added text node
        getChildren().addListener((ListChangeListener<Node>) change -> {
            selection.clear();

            while (change.next()) {
                for (var node : change.getAddedSubList()) {
                    if (node instanceof Text text) {
                        text.selectionFillProperty().bind(highlightTextFillProperty());
                    }
                }
                for (var node : change.getRemoved()) {
                    if (node instanceof Text text) {
                        text.selectionFillProperty().unbind();
                    }
                }
            }
        });

        setOnMousePressed(e -> {
            if (e.isPopupTrigger()) {
                showContextMenu(e);
                return;
            }

            selection.clear();
            setSelectionPath();

            // to be able to clear selection on focus lost we should request it first
            requestFocus();

            var hit = getHitInfo(e);
            var charIndex = hit.getCharIndex();

            if (e.isPrimaryButtonDown() && e.getClickCount() == 1) {
                mouseDragStartPos = charIndex;
                return;
            }

            if (e.isPrimaryButtonDown() && e.getClickCount() == 2 && getTextSelectionOnMouseClick()) {
                var wordRange = findWord(charIndex);
                if (wordRange != null) {
                    selection.select(wordRange);
                }
            }
        });

        setOnMouseDragged(e -> {
            var hit = getHitInfo(e);
            var charIndex = hit.getCharIndex();

            requestFocus();

            // getCharIndex() return inclusive index value,
            // for selection purposes end must exclusive, therefore +1
            selection.select(Math.min(mouseDragStartPos, charIndex), Math.max(mouseDragStartPos, charIndex) + 1);
        });

        setOnMouseReleased(e -> {
            mouseDragStartPos = -1;
            if (e.isPopupTrigger()) {
                showContextMenu(e);
            }
        });

        // size can be changed by changing the stage size
        widthProperty().addListener((obs, old, val) -> selection.clear());
        heightProperty().addListener((obs, old, val) -> selection.clear());
    }

    protected HitInfo getHitInfo(MouseEvent e) {
        var pos = new Point2D(e.getX(), e.getY());
        return hitTest(pos);
    }

    protected void setSelectionPath() {
        if (!getChildren().contains(selection)) {
            getChildren().add(selection);
        }
    }

    protected StringBuilder getContent() {
        var sb = new StringBuilder();

        for (var node : getChildren()) {
            if (node instanceof Text text) {
                sb.append(text.getText());
            }
        }

        return sb;
    }

    protected int getContentLength() {
        var totalLength = 0;

        for (var node : getChildren()) {
            if (node instanceof Text text) {
                totalLength += text.getText().length();
            }
        }

        return totalLength;
    }

    protected void showContextMenu(MouseEvent e) {
        var contextMenu = contextMenuProperty.get();
        if (contextMenu != null) {
            contextMenu.show(this, e.getScreenX(), e.getScreenY());
        }
    }

    protected @Nullable Range findWord(int charIndex) {
        var sb = getContentAsString();
        var isBoundary = Objects.requireNonNull(getWordBoundaryPredicate());

        if (sb == null || sb.isEmpty()
            || charIndex < 0
            || charIndex >= sb.length()
            || isBoundary.test(sb.charAt(charIndex))) {

            return null;
        }

        int start = -1;
        int end = -1;

        for (int i = charIndex; i >= 0; i--) {
            start = i;
            if (isBoundary.test(sb.charAt(i))) {
                start++;
                break;
            }
        }

        for (int i = charIndex; i < sb.length(); i++) {
            if (isBoundary.test(sb.charAt(i))) {
                break;
            }
            end = i;
        }

        // end holds inclusive index value,
        // for selection purposes end must exclusive, therefore +1
        return start >= 0 && end > start ? new Range(start, end + 1) : null;
    }

    // for unit-tests
    Selection getSelection() {
        return selection;
    }

    //=========================================================================

    protected static class Selection extends Path {

        protected final SelectableTextFlow textFlow;
        protected int startPos = -1;
        protected int endPos = -1;

        public Selection(SelectableTextFlow textFlow) {
            this.textFlow = textFlow;

            setManaged(false);
            setViewOrder(100);
            getStyleClass().setAll("selection");
        }

        public int getStartPos() {
            return startPos;
        }

        public int getEndPos() {
            return endPos;
        }

        public void select(Range range) {
            select(range.start(), range.end());
        }

        public void select(int fromInclusive, int toExclusive) {
            startPos = fromInclusive;
            endPos = toExclusive;

            // rangeShape() is incorrect if padding is applied to the TextFlow
            // https://bugs.openjdk.org/browse/JDK-8341438
            PathElement[] selectionRange = textFlow.rangeShape(startPos, endPos);
            getElements().setAll(selectionRange);

            int totalLength = 0;
            for (var node : textFlow.getChildren()) {
                if (node instanceof Text text) {
                    int textLength = text.getText().length();
                    int textStartPos = totalLength;
                    totalLength += textLength;

                    // if the selection starts after the current text element, skip it
                    if (startPos >= totalLength) {
                        continue;
                    }

                    // set the selection range for the current text element
                    int textSelectionStart = Math.max(startPos - textStartPos, 0);
                    int textSelectionEnd = Math.min(endPos - textStartPos, totalLength - textStartPos);

                    if (textSelectionEnd > textSelectionStart) {
                        text.setSelectionStart(textSelectionStart);
                        text.setSelectionEnd(textSelectionEnd);
                    }

                    if (totalLength >= endPos) {
                        break;
                    }
                }
            }
        }

        public String getSelectedRangeAsString() {
            var sb = new StringBuilder();

            for (var node : textFlow.getChildren()) {
                if (node instanceof Text text
                    && text.getSelectionStart() >= 0
                    && text.getSelectionEnd() > text.getSelectionStart()) {

                    sb.append(
                        text.getText(),
                        Math.max(text.getSelectionStart(), 0),
                        Math.min(text.getSelectionEnd(), text.getText().length())
                    );
                }
            }

            return sb.toString();
        }

        public void clear() {
            startPos = -1;
            endPos = -1;

            for (var node : textFlow.getChildren()) {
                if (node instanceof Text text) {
                    text.setSelectionStart(-1);
                    text.setSelectionEnd(-1);
                }
            }

            getElements().clear();
        }
    }

    //=========================================================================
    // Styleable Properties
    //=========================================================================

    private static class StyleableProperties {

        private static final CssMetaData<SelectableTextFlow, Paint> HIGHLIGHT_FILL = new CssMetaData<>(
            "-fx-highlight-fill", PaintConverter.getInstance(), Color.TRANSPARENT
        ) {

            @Override
            public boolean isSettable(SelectableTextFlow styleable) {
                return styleable.selection.fillProperty() == null || !styleable.selection.fillProperty().isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(SelectableTextFlow styleable) {
                var val = (WritableValue<Paint>) styleable.selection.fillProperty();
                return (StyleableProperty<Paint>) val;
            }
        };

        private static final CssMetaData<SelectableTextFlow, Paint> HIGHLIGHT_TEXT_FILL = new CssMetaData<>(
            "-fx-highlight-text-fill", PaintConverter.getInstance(), Color.TRANSPARENT
        ) {

            @Override
            @SuppressWarnings("ConstantValue")
            public boolean isSettable(SelectableTextFlow styleable) {
                return styleable.highlightTextFillProperty() == null
                    || !styleable.highlightTextFillProperty().isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(SelectableTextFlow styleable) {
                var val = (WritableValue<Paint>) styleable.highlightTextFillProperty();
                return (StyleableProperty<Paint>) val;
            }
        };

        private static final CssMetaData<SelectableTextFlow, Paint> HIGHLIGHT_STROKE = new CssMetaData<>(
            "-fx-highlight-stroke", PaintConverter.getInstance(), Color.TRANSPARENT
        ) {

            @Override
            public boolean isSettable(SelectableTextFlow styleable) {
                return styleable.selection.strokeProperty() == null
                    || !styleable.selection.strokeProperty().isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(SelectableTextFlow styleable) {
                var val = (WritableValue<Paint>) styleable.selection.strokeProperty();
                return (StyleableProperty<Paint>) val;
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
                TextFlow.getClassCssMetaData()
            );

            styleables.add(HIGHLIGHT_FILL);
            styleables.add(HIGHLIGHT_STROKE);
            styleables.add(HIGHLIGHT_TEXT_FILL);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}
