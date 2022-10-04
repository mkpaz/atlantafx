/* SPDX-License-Identifier: MIT */
package atlantafx.base.util;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * An alternative for the {@link javafx.scene.control.PasswordField}.
 * The formatter (un)masks text field content based on boolean property.
 */
public class PasswordTextFormatter extends TextFormatter<String> {

    public static final char BULLET = '\u2731'; // heavy asterisk

    protected PasswordTextFormatter(StringConverter<String> valueConverter,
                                    UnaryOperator<Change> filter,
                                    TextField textField,
                                    char bullet) {
        super(valueConverter, null, filter);

        Objects.requireNonNull(valueConverter);
        Objects.requireNonNull(filter);
        Objects.requireNonNull(textField);

        PasswordFilter passwordFilter = (PasswordFilter) getFilter();
        passwordFilter.setBullet(bullet);
        passwordFilter.setInitialText(textField.getText());

        revealPasswordProperty().addListener((obs, old, val) -> {
            // Force text field update, because converter is only called on focus
            // events by default. Don't use commitValue() here because caret position
            // won't be correct due to #javafx-bug (https://bugs.openjdk.org/browse/JDK-8248914).
            if (val == null) { return; }
            textField.setText(passwordProperty().get());
        });

        // force text field update on scene show
        Platform.runLater(textField::commitValue);
    }

    public ReadOnlyStringProperty passwordProperty() {
        return ((PasswordFilter) getFilter()).password.getReadOnlyProperty();
    }

    public String getPassword() {
        return passwordProperty().get();
    }

    public BooleanProperty revealPasswordProperty() {
        return ((PasswordFilter) getFilter()).revealPassword;
    }

    public boolean isRevealPassword() {
        return revealPasswordProperty().get();
    }

    public void setRevealPassword(boolean reveal) {
        revealPasswordProperty().set(reveal);
    }

    // Life would be easier if TextFormatter had the default constructor.
    public static PasswordTextFormatter create(TextField textField, char bullet) {
        var filter = new PasswordFilter();
        var converter = new PasswordStringConverter(filter);
        return new PasswordTextFormatter(converter, filter, textField, bullet);
    }

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
            if (s == null) { return ""; }
            return filter.revealPassword.get() ? filter.password.get() : filter.maskText(s.length());
        }

        @Override
        public String fromString(String string) {
            return filter.password.get();
        }
    }

    protected static class PasswordFilter implements UnaryOperator<Change> {

        protected final ReadOnlyStringWrapper password = new ReadOnlyStringWrapper("");
        protected final BooleanProperty revealPassword = new SimpleBooleanProperty(false);
        protected final StringBuilder sb = new StringBuilder();
        protected char bullet = PasswordTextFormatter.BULLET;

        @Override
        public TextFormatter.Change apply(TextFormatter.Change change) {
            // Since we are using setText() to force text field to update (see above),
            // we should protect internal password value from changing when `revealPassword`is toggled.
            if (Objects.equals(change.getText(), sb.toString())) {
                if (!revealPassword.get()) {
                    change.setText(maskText(change.getText().length()));
                }
                return change;
            }

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

        protected void setInitialText(String text) {
            if (text != null && !text.isEmpty()) {
                sb.append(text);
                password.set(sb.toString());
            }
        }
    }
}
