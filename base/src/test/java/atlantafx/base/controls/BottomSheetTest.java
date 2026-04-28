/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import atlantafx.base.util.JavaFXTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class BottomSheetTest {

    @Test
    public void testDefaultStyleClass() {
        var sheet = new BottomSheet();
        assertThat(sheet.getStyleClass()).contains("bottom-sheet");
    }

    @Test
    public void testDefaultPropertyValues() {
        var sheet = new BottomSheet();
        assertThat(sheet.isDisplay()).isFalse();
        assertThat(sheet.getContent()).isNull();
        assertThat(sheet.getHeader()).isNull();
        assertThat(sheet.getPersistent()).isFalse();
        assertThat(sheet.getDismissThreshold()).isEqualTo(BottomSheet.DEFAULT_DISMISS_THRESHOLD);
    }

    @Test
    public void testContentProperty() {
        var sheet = new BottomSheet();
        var content = new VBox(new Label("Test"));

        sheet.setContent(content);
        assertThat(sheet.getContent()).isSameAs(content);

        sheet.setContent(null);
        assertThat(sheet.getContent()).isNull();
    }

    @Test
    public void testHeaderProperty() {
        var sheet = new BottomSheet();
        var header = new Label("Header");

        sheet.setHeader(header);
        assertThat(sheet.getHeader()).isSameAs(header);

        sheet.setHeader(null);
        assertThat(sheet.getHeader()).isNull();
    }

    @Test
    public void testDisplayProperty() {
        var sheet = new BottomSheet();
        assertThat(sheet.isDisplay()).isFalse();

        sheet.setDisplay(true);
        assertThat(sheet.isDisplay()).isTrue();

        sheet.setDisplay(false);
        assertThat(sheet.isDisplay()).isFalse();
    }

    @Test
    public void testPersistentProperty() {
        var sheet = new BottomSheet();
        assertThat(sheet.getPersistent()).isFalse();

        sheet.setPersistent(true);
        assertThat(sheet.getPersistent()).isTrue();
    }

    @Test
    public void testDismissThresholdProperty() {
        var sheet = new BottomSheet();
        assertThat(sheet.getDismissThreshold()).isEqualTo(100);

        sheet.setDismissThreshold(200);
        assertThat(sheet.getDismissThreshold()).isEqualTo(200);
    }

    @Test
    public void testShowSetsContentAndDisplay() {
        var sheet = new BottomSheet();
        var content = new VBox(new Label("Sheet content"));

        sheet.show(content);
        assertThat(sheet.getContent()).isSameAs(content);
        assertThat(sheet.isDisplay()).isTrue();
    }

    @Test
    public void testHideSetsDisplayFalse() {
        var sheet = new BottomSheet();
        var content = new VBox(new Label("Sheet content"));
        sheet.show(content);

        sheet.hide();
        assertThat(sheet.isDisplay()).isFalse();
    }

    @Test
    public void testCreateDefaultSkin() {
        var sheet = new BottomSheet();
        var skin = sheet.createDefaultSkin();
        assertThat(skin).isInstanceOf(BottomSheetSkin.class);
    }
}
