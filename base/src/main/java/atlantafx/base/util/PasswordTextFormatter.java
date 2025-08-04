/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;

/**
 * An alternative to the {@link PasswordField} class. This formatter masks
 * or unmasks text field content based on a boolean property.
 */
public class PasswordTextFormatter extends TextFormatter<String> {

    public static final char BULLET = '✱'; // U+2731, heavy asterisk

    protected PasswordTextFormatter(StringConverter<String> valueConverter,
                                    UnaryOperator<Change> filter,
                                    TextField field,
                                    char bullet) {
        //noinspection DataFlowIssue (TextFormatter accepts null as default value)
        super(valueConverter, null, filter);

        NullSafetyHelper.assertNonNull(valueConverter, "StringConverter");
        NullSafetyHelper.assertNonNull(filter, "UnaryOperator");
        NullSafetyHelper.assertNonNull(field, "TextField");

        PasswordFilter passwordFilter = (PasswordFilter) getFilter();
        passwordFilter.setBullet(bullet);
        passwordFilter.setInitialText(field.getText());

        revealPasswordProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }

            // Force text field update, because converter is only called on focus events by default.
            // Also, reset caret first, because otherwise its position won't be correct due to
            // #javafx-bug (https://bugs.openjdk.org/browse/JDK-8248914).
            field.positionCaret(0);
            field.commitValue();
        });

        // force text field update on scene show
        Platform.runLater(field::commitValue);
    }

    /**
     * Always returns the unmasked password text regardless of
     * the {@link #revealPasswordProperty} state.
     */
    public ReadOnlyStringProperty passwordProperty() {
        return ((PasswordFilter) getFilter()).password.getReadOnlyProperty();
    }

    /**
     * See {@link #passwordProperty()}.
     */
    public String getPassword() {
        return passwordProperty().get();
    }

    /**
     * Specifies whether the unmasked password text is revealed or not.
     */
    public BooleanProperty revealPasswordProperty() {
        return ((PasswordFilter) getFilter()).revealPassword;
    }

    /**
     * See {@link #revealPasswordProperty}.
     */
    public boolean getRevealPassword() {
        return revealPasswordProperty().get();
    }

    /**
     * See {@link #revealPasswordProperty}.
     */
    public void setRevealPassword(boolean reveal) {
        revealPasswordProperty().set(reveal);
    }

    /**
     * Creates a new password text formatter with the provided mask character and
     * applies itself to the specified text field.
     */
    public static PasswordTextFormatter create(TextField field, char bullet) {
        var filter = new PasswordFilter();
        var converter = new PasswordStringConverter(filter);

        var formatter = new PasswordTextFormatter(converter, filter, field, bullet);
        field.setTextFormatter(formatter);

        return formatter;
    }

    /**
     * Creates a new password text formatter with the default mask character and
     * applies itself to the specified text field.
     */
    public static PasswordTextFormatter create(TextField textField) {
        return create(textField, BULLET);
    }

    ///////////////////////////////////////////////////////////////////////////

    protected static class PasswordStringConverter extends StringConverter<String> {

        protected final PasswordFilter filter;

        public PasswordStringConverter(PasswordFilter filter) {
            this.filter = filter;
        }

        @Override
        public String toString(String s) {
            return getPassword();
        }

        @Override
        public String fromString(String s) {
            return getPassword();
        }

        protected String getPassword() {
            var password = filter.password.get();
            return filter.revealPassword.get() ? password : filter.maskText(password.length());
        }
    }

    protected static class PasswordFilter implements UnaryOperator<Change> {

        protected final ReadOnlyStringWrapper password = new ReadOnlyStringWrapper("");
        protected final BooleanProperty revealPassword = new SimpleBooleanProperty(false);
        protected final StringBuilder sb = new StringBuilder();
        protected char bullet = PasswordTextFormatter.BULLET;

        @Override
        public TextFormatter.Change apply(TextFormatter.Change change) {
            if (change.isReplaced()) {
                sb.replace(change.getRangeStart(), change.getRangeEnd(), change.getText());
            } else if (change.isDeleted()) {
                sb.delete(change.getRangeStart(), change.getRangeEnd());
            } else if (change.isAdded()) {
                if (change.getRangeStart() == sb.length()) {
                    sb.append(change.getText());
                } else {
                    sb.insert(change.getRangeStart(), change.getText());
                }
            }

            // mask new text, so it won't appear on user input
            if (change.getText() != null && !change.getText().isEmpty() && !revealPassword.get()) {
                change.setText(maskText(change.getText().length()));
            }

            password.set(sb.toString());

            return change;
        }

        protected void setBullet(char bullet) {
            this.bullet = bullet;
        }

        protected String maskText(int length) {
            return String.valueOf(bullet).repeat(length);
        }

        protected void setInitialText(@Nullable String text) {
            if (text != null && !text.isEmpty()) {
                sb.append(text);
                password.set(sb.toString());
            }
        }
    }
}
