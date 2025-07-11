/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import static atlantafx.base.util.MaskTextFormatter.createPlaceholderMask;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TextFormatter.Change;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
public class MaskTextFormatterTest {

    @Test
    public void testDefaultTextIsPlaceholderMask() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        assertThat(field.getText()).isEqualTo(createPlaceholderMask("+DD-DD"));
    }

    @Test
    public void testValidInitialTextSet() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("+11-22");
        assertThat(field.getText()).isEqualTo("+11-22");
    }

    @Test
    public void testInvalidInitialTextRejected() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("123");
        assertThat(field.getText()).isEqualTo(createPlaceholderMask("+DD-DD"));
    }

    @Test
    public void testPromptTextResetsPlaceholderMask() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("+11-22");
        field.setPromptText("whatever");
        assertThat(field.getText()).isEmpty();
    }

    /**
     * This one is a #javafx-bug. Calling the {@link Change#getControlNewText()}
     * in text formatter when text is set to null produces IndexOutOfBoundsException
     * or IllegalArgumentException (start must <= end). Ironically, there won't be
     * exception if you made some text manipulations first, like:
     * <pre>
     * field.setText("foo");
     * field.deleteText(0, 1);
     * field.setText(null); // no exception here
     * </pre>
     * It's worth to mention that the default text field value is empty string precisely,
     * not null.
     */
    @Test
    public void testInitialTextCanNotBeNull() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        assertThatThrownBy(() -> field.setText(null)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void testAppendText() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText(createPlaceholderMask("+DD-DD"));

        field.appendText("12");
        assertThat(field.getText()).isEqualTo("+__-__");
    }

    @Test
    public void testInsertText() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText(createPlaceholderMask("+DD-DD"));

        field.insertText(1, "12");
        assertThat(field.getText()).isEqualTo("+12-__");

        field.insertText(4, "34");
        assertThat(field.getText()).isEqualTo("+12-34");
    }

    @Test
    public void testInsertInvalidText() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText(createPlaceholderMask("+DD-DD"));

        field.insertText(1, "12");
        assertThat(field.getText()).isEqualTo("+12-__");

        field.insertText(4, "A2");
        assertThat(field.getText()).isEqualTo("+12-__");
    }

    @Test
    public void testDeleteSomeText() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("+12-34");

        field.deleteText(2, 3);
        assertThat(field.getText()).isEqualTo("+1_-34");

        field.deleteText(4, 6);
        assertThat(field.getText()).isEqualTo("+1_-__");
    }

    @Test
    public void testDeleteAllText() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("+12-34");

        field.deleteText(0, field.getText().length());
        assertThat(field.getText()).isEqualTo(createPlaceholderMask("+DD-DD"));
    }

    @Test
    public void testSetTextToNull() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("+12-34");

        field.deleteText(2, 3);
        assertThat(field.getText()).isEqualTo("+1_-34");

        field.setText(null);
        assertThat(field.getText()).isEqualTo(createPlaceholderMask("+DD-DD"));
    }

    @Test
    public void testReplaceSelection() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("+12-34");

        field.selectRange(1, 3);
        field.replaceSelection("56");
        assertThat(field.getText()).isEqualTo("+56-34");
    }

    @Test
    public void testReplaceSelectionWithInvalidText() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("+12-34");

        field.selectRange(1, 3);
        field.replaceSelection("A2");
        assertThat(field.getText()).isEqualTo("+12-34");
    }

    @Test
    public void testReplaceAll() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("+12-34");

        field.selectRange(0, field.getText().length());
        field.replaceSelection("+43-21");
        assertThat(field.getText()).isEqualTo("+43-21");
    }

    @Test
    public void testReplaceAllWithInvalidText() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        field.setText("+12-34");

        field.selectRange(0, field.getText().length());
        field.replaceSelection("+A2-21");
        assertThat(field.getText()).isEqualTo("+12-34");
    }

    @Test
    public void testBindingValidText() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        var prop = new SimpleStringProperty();
        prop.bindBidirectional(field.textProperty());

        assertThat(prop.get()).isEqualTo(createPlaceholderMask("+DD-DD"));
        assertThat(field.getText()).isEqualTo(createPlaceholderMask("+DD-DD"));

        prop.set("+12-34");
        assertThat(field.getText()).isEqualTo("+12-34");
    }

    @Test
    public void testBindingInvalidText() {
        var field = MaskTextFormatter.createTextField("+DD-DD");
        var prop = new SimpleStringProperty();
        prop.bindBidirectional(field.textProperty());

        assertThat(prop.get()).isEqualTo(createPlaceholderMask("+DD-DD"));
        assertThat(field.getText()).isEqualTo(createPlaceholderMask("+DD-DD"));

        prop.set("+12-34");
        assertThat(field.getText()).isEqualTo("+12-34");

        prop.set("+A2-34");
        assertThat(field.getText()).isEqualTo("+12-34");
    }
}