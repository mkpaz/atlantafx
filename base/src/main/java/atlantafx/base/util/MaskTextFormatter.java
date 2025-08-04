/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.jspecify.annotations.Nullable;

/**
 * A {@code TextFormatter} that can restrict the user input by applying a position-based mask.
 * It works for the editing cases when the input string has a fixed length and each character
 * can be restricted based on its position.<br/><br/>
 *
 * <p><h3>Input Mask</h3>
 * You can specify an input mask either as a string of the predefined characters or as a list of
 * {@link MaskChar}, including your own implementation if you're not satisfied with the default
 * {@link SimpleMaskChar}, e.g. if you want to override the placeholder character.
 * The pre-defined mask characters are:
 * <ul>
 * <li><b>A</b> - ASCII alphabetic character: <code>[a-zA-Z]</code>.</li>
 * <li><b>N</b> - ASCII alphanumeric character: <code>[a-zA-Z0-9]</code>.</li>
 * <li><b>X</b> - any character except spaces.</li>
 * <li><b>H</b> - hexadecimal character: <code>[a-fA-F0-9]</code>.</li>
 * <li><b>D</b> - any digit except zero: <code>[1-9]</code>.</li>
 * <li><b>9</b> - any digit required: <code>[0-9]</code>.</li>
 * <li><b>8..1</b> - any digit from 0 to that number, respectively.</li>
 * <li><b>0</b> - zero only.</li>
 * </ul>
 *
 * <p><h3>Behavior</h3>
 * Any {@code TextField} with {@code MaskTextFormatter} applied shows a placeholder
 * mask by default. This is basically the input mask with all mask characters replaced
 * with the {@link MaskChar#getPlaceholder()} character.
 *
 * <p>The behavior changes if you set the {@link TextField#promptTextProperty()}.
 * In that case placeholder mask is only displayed when {@code TextField} gets focus and
 * will be hidden after focus lost. So, the placeholder mask is always displayed when focus
 * is set to the {@code TextField}.
 *
 * <p>You can replace the placeholder mask with any sensible default simply by changing initial
 * {@code TextField} text to any string that is valid against the input mask.<br/><br/>
 *
 * <p>The caret will be positioned before the first not fixed character (see {@link MaskChar#isFixed()})
 * starting from the beginning of the input mask.<br/><br/>
 *
 * <p><h3>Validation</h3>
 * Validation is out of the {@code MaskTextFormatter} scope. E.g. if one can use {@code "29:59"}
 * to restrict time picker input then {@code "27:30"} would be a valid input, but obviously an
 * invalid time. Moreover, remember that partial input like this {@code "22:_9"} is also possible.
 * Input mask is supposed to assist and guide user input, but can barely cancel the validation
 * completely.
 */
public class MaskTextFormatter extends TextFormatter<String> {

    protected final MaskTextFilter filter;

    protected MaskTextFormatter(MaskTextFilter filter) {
        super(filter);
        this.filter = filter;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Factory Methods                                                       //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new text field with the provided string input mask.
     */
    public static TextField createTextField(String mask) {
        return createTextField(fromString(mask));
    }

    /**
     * Creates a new text field with the provided input mask.
     */
    public static TextField createTextField(List<MaskChar> mask) {
        final var field = new TextField();
        create(field, mask);
        return field;
    }

    /**
     * Creates a new mask text formatter with the provided string input mask and
     * applies itself to the specified text field.
     */
    public static MaskTextFormatter create(TextField field, String mask) {
        return create(field, fromString(mask));
    }

    /**
     * Creates a new mask text formatter with the provided input mask and
     * applies itself to the specified text field.
     */
    public static MaskTextFormatter create(TextField field, List<MaskChar> mask) {
        Objects.requireNonNull(field, "Text field can't be null");

        if (mask.isEmpty()) {
            throw new IllegalArgumentException("Input mask can't be null or empty.");
        }

        final var placeholder = createPlaceholderMask(mask);
        final var filter = new MaskTextFilter(mask);

        field.focusedProperty().addListener((obs, old, val) -> {
            var text = field.getText();
            var prompt = field.getPromptText();

            if (val) {
                // always show input mask to the user when control is focused and has no text
                if (text == null || text.isBlank()) {
                    filter.doInternalChange(() -> field.setText(placeholder));

                    final int caretPos = IntStream.range(0, mask.size())
                        .filter(i -> !mask.get(i).isFixed())
                        .findFirst()
                        .orElse(0);

                    Platform.runLater(() -> {
                        field.deselect();
                        field.positionCaret(caretPos);
                    });
                }
            } else {
                // remove the input mask, but only if control prompt text is set
                if (prompt != null && !prompt.isBlank() && Objects.equals(placeholder, text)) {
                    filter.doInternalChange(() -> field.setText(""));
                }
            }
        });

        field.promptTextProperty().addListener((obs, old, val) -> {
            if (val == null || val.isBlank()) {
                filter.doInternalChange(() -> field.setText(placeholder));
            } else {
                filter.doInternalChange(() -> field.setText(""));
            }
        });

        var formatter = new MaskTextFormatter(filter);
        field.setTextFormatter(formatter);

        // default text, will be changed after/if prompt text is set
        filter.doInternalChange(() -> field.setText(placeholder));

        return formatter;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers                                                               //
    ///////////////////////////////////////////////////////////////////////////

    protected static String createPlaceholderMask(String inputMask) {
        return createPlaceholderMask(fromString(inputMask));
    }

    protected static String createPlaceholderMask(List<MaskChar> mask) {
        return mask.stream()
            .map(mc -> Character.toString(mc.getPlaceholder()))
            .collect(Collectors.joining());
    }

    protected static List<MaskChar> fromString(String inputMask) {
        if (inputMask.trim().isEmpty()) {
            throw new IllegalArgumentException("Input mask can't be null or empty.");
        }

        var mask = new ArrayList<MaskChar>(inputMask.trim().length());

        for (int i = 0; i < inputMask.length(); i++) {
            char curChar = inputMask.charAt(i);
            SimpleMaskChar maskChar = switch (curChar) {
                case MaskChar.INPUT_MASK_LETTER -> new SimpleMaskChar(Character::isLetter);
                case MaskChar.INPUT_MASK_DIGIT_OR_LETTER -> new SimpleMaskChar(Character::isLetterOrDigit);
                case MaskChar.INPUT_MASK_ANY_NON_SPACE -> new SimpleMaskChar(ch -> !Character.isSpaceChar(ch));
                case MaskChar.INPUT_MASK_HEX -> new SimpleMaskChar(ch ->
                    (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f') || Character.isDigit(ch)
                );
                case MaskChar.INPUT_MASK_DIGIT_NON_ZERO -> new SimpleMaskChar(ch -> Character.isDigit(ch) && ch != '0');
                case MaskChar.INPUT_MASK_DIGIT -> new SimpleMaskChar(Character::isDigit);
                case MaskChar.INPUT_MASK_DIGIT_0_TO_8 -> new SimpleMaskChar(ch -> ch >= '0' && ch <= '8');
                case MaskChar.INPUT_MASK_DIGIT_0_TO_7 -> new SimpleMaskChar(ch -> ch >= '0' && ch <= '7');
                case MaskChar.INPUT_MASK_DIGIT_0_TO_6 -> new SimpleMaskChar(ch -> ch >= '0' && ch <= '6');
                case MaskChar.INPUT_MASK_DIGIT_0_TO_5 -> new SimpleMaskChar(ch -> ch >= '0' && ch <= '5');
                case MaskChar.INPUT_MASK_DIGIT_0_TO_4 -> new SimpleMaskChar(ch -> ch >= '0' && ch <= '4');
                case MaskChar.INPUT_MASK_DIGIT_0_TO_3 -> new SimpleMaskChar(ch -> ch >= '0' && ch <= '3');
                case MaskChar.INPUT_MASK_DIGIT_0_TO_2 -> new SimpleMaskChar(ch -> ch >= '0' && ch <= '2');
                case MaskChar.INPUT_MASK_DIGIT_0_TO_1 -> new SimpleMaskChar(ch -> ch >= '0' && ch <= '1');
                case MaskChar.INPUT_MASK_DIGIT_ZERO -> new SimpleMaskChar(ch -> ch == '0');
                default -> SimpleMaskChar.fixed(curChar);

            };

            mask.add(maskChar);
        }

        return Collections.unmodifiableList(mask);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Filter Implementation                                                 //
    ///////////////////////////////////////////////////////////////////////////

    protected static class MaskTextFilter implements UnaryOperator<Change> {

        protected final List<MaskChar> mask;
        protected boolean ignoreFilter;

        public MaskTextFilter(List<MaskChar> mask) {
            this.mask = Objects.requireNonNull(mask);
        }

        public boolean isInternalChange() {
            return ignoreFilter;
        }

        public void doInternalChange(Runnable r) {
            try {
                this.ignoreFilter = true;
                r.run();
            } finally {
                this.ignoreFilter = false;
            }
        }

        @Override
        public @Nullable Change apply(Change change) {
            if (!change.isContentChange() || isInternalChange()) {
                return change;
            }

            return correctContentChange(change);
        }

        /**
         * Corrects and returns the content change if it matches the mask.
         * Otherwise, returns null to drop the change.
         */
        protected @Nullable Change correctContentChange(Change change) {
            String newText = null;

            if (change.isReplaced()) {
                newText = correctReplacedText(change);
            } else if (change.isAdded()) {
                newText = correctAddedText(change);
            } else if (change.isDeleted()) {
                newText = correctDeletedText(change);
            }

            if (newText != null) {
                int start = change.getRangeStart();
                change.setRange(start, Math.min(start + newText.length(), change.getControlText().length()));
                change.setText(newText);
            }

            // only adjust caret pos for content changes, as it allows
            // to select and copy arbitrary part of the text including fixed chars
            adjustCaretPosition(change);

            return newText != null ? change : null;
        }

        /**
         * Corrects the replaced text. For any replaced character it checks whether it matches
         * the mask character at the same position and applies {@link MaskChar#transform(char)}.
         * if true. If not, returns null, thus signifying that added text is not valid.
         */
        protected @Nullable String correctReplacedText(Change change) {
            final int start = change.getRangeStart();
            final int end = change.getRangeEnd();
            final var changedText = change.getText();
            final var newText = new StringBuilder(end - start);

            // replaces new text with transformed according to mask
            // or exits if the new text doesn't match the mask
            for (int i = start; i - start < changedText.length() && i < end && i < mask.size(); i++) {
                final char ch = changedText.charAt(i - start);
                if (mask.get(i).isAllowed(ch)) {
                    newText.append(mask.get(i).transform(ch));
                } else {
                    return null; // mark all replaced text as invalid
                }
            }

            // replace is basically 'remove + add' and this handles the situation when
            // removed text length is greater than added text length, e.g. select 'abc' and type 'd',
            // in that case the rest of the text (bc) should be replaced with placeholders
            for (int i = start + changedText.length(); i < end && i < mask.size(); i++) {
                newText.append(mask.get(i).getPlaceholder());
            }

            return newText.toString();
        }

        /**
         * Corrects added text. For any input character it checks whether it matches
         * the mask character at the same position and applies {@link MaskChar#transform(char)}.
         * if true. If not, returns null, thus signifying that added text is not valid.
         */
        protected @Nullable String correctAddedText(Change change) {
            final int start = change.getRangeStart();
            final var changedText = change.getText();
            final var newText = new StringBuilder(changedText.length());

            for (int i = start; i - start < changedText.length() && i < mask.size(); i++) {
                final char ch = changedText.charAt(i - start);

                if (mask.get(i).isAllowed(ch)) {
                    newText.append(mask.get(i).transform(ch));
                } else {
                    return null; // mark all added text as invalid
                }
            }

            return newText.toString();
        }

        /**
         * Corrects the deleted text. Basically, replaces all deleted characters with
         * placeholders and returns the resulting text which is always not null.
         */
        protected String correctDeletedText(Change change) {
            int start = change.getRangeStart();
            final int end = change.getRangeEnd();
            final var newText = new StringBuilder(end - start);

            // replaces deleted text with placeholders
            for (int i = start; i < end; i++) {
                newText.append(mask.get(i).getPlaceholder());
            }

            // handles the situation when backspace is pressed to delete a fixes char (separator),
            // in that case the character before the separator, if any, should be removed
            for (int i = start; i > 0 && mask.get(i).isFixed(); i--, start--) {
                newText.insert(0, mask.get(i - 1).getPlaceholder());
            }

            change.setRange(start, end);

            return newText.toString();
        }

        protected void adjustCaretPosition(Change change) {
            final int oldPos = change.getControlCaretPosition();
            int newPos = Math.min(change.getCaretPosition(), mask.size());

            if (oldPos != newPos) {
                // caret can't be placed before a fixed character,
                // it jumps over it to the previous or the next character
                final int sign = newPos > oldPos ? 1 : -1;
                while (newPos > 0 && newPos < mask.size() && mask.get(newPos).isFixed()) {
                    newPos += sign;
                }

                // prevents caret from moving before a fixed prefix,
                while (newPos < mask.size() && mask.get(newPos).isFixed()) {
                    newPos++;
                }
            }

            // make sure caret position won't exceed control text length
            newPos = Math.min(newPos, change.getControlNewText().length());

            if (change.getAnchor() == change.getCaretPosition()) {
                change.setAnchor(newPos);
            }

            change.setCaretPosition(newPos);
        }
    }
}
