/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
public class PasswordTextFormatterTest {

    @Test
    public void testTextIsMaskedByDefault() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        assertEquals("+".repeat(3), field.getText());
        assertEquals("123", fmt.getPassword());
    }

    @Test
    public void testTextCanBeRevealed() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        fmt.setRevealPassword(true);
        assertEquals("123", field.getText());
        assertEquals("123", fmt.getPassword());
    }

    @Test
    public void testPrependText() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        field.insertText(0, "456");
        assertEquals("+".repeat(6), field.getText());
        assertEquals("456123", fmt.getPassword());
    }

    @Test
    public void testAppendText() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        field.appendText("456");
        assertEquals("+".repeat(6), field.getText());
        assertEquals("123456", fmt.getPassword());
    }

    @Test
    public void testInsertText() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        field.insertText(2, "456");
        assertEquals("+".repeat(6), field.getText());
        assertEquals("124563", fmt.getPassword());
    }

    @Test
    public void testNoInitialText() {
        var field = new TextField(null);
        var fmt = PasswordTextFormatter.create(field, '+');

        field.appendText("456");
        assertEquals("+".repeat(3), field.getText());
        assertEquals("456", fmt.getPassword());
    }

    @Test
    public void testDeleteSomeText() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        field.deleteText(0, 2);
        assertEquals("+", field.getText());
        assertEquals("3", fmt.getPassword());
    }

    @Test
    public void testDeleteAllText() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        field.deleteText(0, field.getText().length());
        assertEquals("", field.getText());
        assertEquals("", fmt.getPassword());
    }

    @Test
    public void testSetTextToNull() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        field.setText(null);
        assertNull(null, field.getText());
        assertEquals("", fmt.getPassword());
    }

    @Test
    public void testReplaceSelection() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        field.selectRange(1, field.getText().length());
        field.replaceSelection("456");
        assertEquals("+".repeat(4), field.getText());
        assertEquals("1456", fmt.getPassword());
    }

    @Test
    public void testReplaceAll() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123");

        field.selectRange(0, field.getText().length());
        field.replaceSelection("456");
        assertEquals("+".repeat(3), field.getText());
        assertEquals("456", fmt.getPassword());
    }

    @Test
    public void testCanContainBullets() {
        var field = new TextField();
        var fmt = PasswordTextFormatter.create(field, '+');
        field.setText("123++");

        assertEquals("+".repeat(5), field.getText());
        assertEquals("123++", fmt.getPassword());
    }
}
