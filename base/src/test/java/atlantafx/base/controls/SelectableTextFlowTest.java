/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.scene.text.Text;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@NullMarked
public class SelectableTextFlowTest {

    private SelectableTextFlow textFlow;

    @BeforeEach
    public void setUp() {
        textFlow = new SelectableTextFlow();
    }

    @Test
    public void testSelectFirstChar() {
        textFlow.setText(new Text("Left"), new Text("Middle"), new Text("Right"));

        textFlow.getSelection().select(0, 1);
        assertTextSelection(0, 0, 1);
        assertTextSelection(1, -1, -1);
        assertTextSelection(2, -1, -1);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("L");
    }

    @Test
    public void testSelectMiddleChar() {
        textFlow.setText(new Text("Left"), new Text("Middle"), new Text("Right"));

        textFlow.getSelection().select(2, 3);
        assertTextSelection(0, 2, 3);
        assertTextSelection(1, -1, -1);
        assertTextSelection(2, -1, -1);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("f");
        textFlow.getSelection().clear();

        textFlow.getSelection().select(5, 6);
        assertTextSelection(0, -1, -1);
        assertTextSelection(1, 1, 2);
        assertTextSelection(2, -1, -1);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("i");
        textFlow.getSelection().clear();

        textFlow.getSelection().select(12, 13);
        assertTextSelection(0, -1, -1);
        assertTextSelection(1, -1, -1);
        assertTextSelection(2, 2, 3);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("g");
        textFlow.getSelection().clear();
    }

    @Test
    public void testSelectLastChar() {
        textFlow.setText(new Text("Left"), new Text("Middle"), new Text("Right"));

        textFlow.getSelection().select(14, 15);
        assertTextSelection(0, -1, -1);
        assertTextSelection(1, -1, -1);
        assertTextSelection(2, 4, 5);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("t");
    }

    @Test
    public void testSelectLeftText() {
        textFlow.setText(new Text("Left"), new Text("Middle"), new Text("Right"));

        textFlow.getSelection().select(0, 4);
        assertTextSelection(0, 0, 4);
        assertTextSelection(1, -1, -1);
        assertTextSelection(2, -1, -1);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("Left");
    }

    @Test
    public void testSelectMiddleText() {
        textFlow.setText(new Text("Left"), new Text("Middle"), new Text("Right"));

        textFlow.getSelection().select(4, 10);
        assertTextSelection(0, -1, -1);
        assertTextSelection(1, 0, 6);
        assertTextSelection(2, -1, -1);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("Middle");
    }

    @Test
    public void testSelectRightText() {
        textFlow.setText(new Text("Left"), new Text("Middle"), new Text("Right"));

        textFlow.getSelection().select(10, 15);
        assertTextSelection(0, -1, -1);
        assertTextSelection(1, -1, -1);
        assertTextSelection(2, 0, 5);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("Right");
    }

    @Test
    public void testSelectRange() {
        textFlow.setText(new Text("Left"), new Text("Middle"), new Text("Right"));

        textFlow.getSelection().select(1, 3);
        assertTextSelection(0, 1, 3);
        assertTextSelection(1, -1, -1);
        assertTextSelection(2, -1, -1);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("ef");
        textFlow.getSelection().clear();

        textFlow.getSelection().select(5, 7);
        assertTextSelection(0, -1, -1);
        assertTextSelection(1, 1, 3);
        assertTextSelection(2, -1, -1);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("id");
        textFlow.getSelection().clear();

        textFlow.getSelection().select(11, 13);
        assertTextSelection(0, -1, -1);
        assertTextSelection(1, -1, -1);
        assertTextSelection(2, 1, 3);
        assertThat(textFlow.getSelection().getSelectedRangeAsString()).isEqualTo("ig");
        textFlow.getSelection().clear();
    }

    //=========================================================================

    @Test
    public void testFindWordAtStart() {
        textFlow.setText(new Text("foo"), new Text(" bar"), new Text(","), new Text("baz"));

        var range = textFlow.findWord(2);
        assertThat(range).isNotNull();
        assertThat(range.start()).isEqualTo(0);
        assertThat(range.end()).isEqualTo(3);
    }

    @Test
    public void testFindWordAtMiddle() {
        textFlow.setText(new Text("foo"), new Text(" bar"), new Text(","), new Text("baz"));

        var range = textFlow.findWord(5);
        assertThat(range).isNotNull();
        assertThat(range.start()).isEqualTo(4);
        assertThat(range.end()).isEqualTo(7);
    }

    @Test
    public void testFindWordAtEnd() {
        textFlow.setText(new Text("foo"), new Text(" bar"), new Text(","), new Text("baz"));

        var range = textFlow.findWord(9);
        assertThat(range).isNotNull();
        assertThat(range.start()).isEqualTo(8);
        assertThat(range.end()).isEqualTo(11);
    }

    @Test
    public void testFindWordInEmptyText() {
        var range = textFlow.findWord(0);
        assertThat(range).isNull();
    }

    @Test
    public void testFindWordOutOfBounds() {
        textFlow.setText(new Text("foo"), new Text(" bar"), new Text(","), new Text("baz"));

        var range = textFlow.findWord(-1);
        assertThat(range).isNull();

        range = textFlow.findWord(15);
        assertThat(range).isNull();
    }

    @Test
    public void testFindWordAtBoundaries() {
        textFlow.setText(new Text("foo"), new Text(" bar"), new Text(","), new Text("baz"));

        var range = textFlow.findWord(0);
        assertThat(range).isNotNull();
        assertThat(range.start()).isEqualTo(0);
        assertThat(range.end()).isEqualTo(3);

        range = textFlow.findWord(10);
        assertThat(range).isNotNull();
        assertThat(range.start()).isEqualTo(8);
        assertThat(range.end()).isEqualTo(11);
    }

    //=========================================================================

    private void assertTextSelection(int index, int selectionStart, int selectionEnd) {
        assertThat(textFlow.getChildren().size()).isGreaterThan(index);

        var text = textFlow.getChildren().get(index);
        assertInstanceOf(Text.class, text);

        assertThat(((Text) text).getSelectionStart()).isEqualTo(selectionStart);
        assertThat(((Text) text).getSelectionEnd()).isEqualTo(selectionEnd);
    }
}
