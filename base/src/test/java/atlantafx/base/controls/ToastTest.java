/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.util.Duration;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class ToastTest {

    @Test
    public void testDefaultStyleClass() {
        var toast = new Toast();
        assertThat(toast.getStyleClass()).contains("toast");
    }

    @Test
    public void testDefaultPropertyValues() {
        var toast = new Toast();
        assertThat(toast.getMessage()).isNull();
        assertThat(toast.getDuration()).isEqualTo(Toast.DEFAULT_DURATION);
        assertThat(toast.getOnClose()).isNull();
    }

    @Test
    public void testMessageProperty() {
        var toast = new Toast("Hello");
        assertThat(toast.getMessage()).isEqualTo("Hello");

        toast.setMessage("World");
        assertThat(toast.getMessage()).isEqualTo("World");

        toast.setMessage(null);
        assertThat(toast.getMessage()).isNull();
    }

    @Test
    public void testDurationProperty() {
        var toast = new Toast();
        assertThat(toast.getDuration()).isEqualTo(Duration.seconds(5));

        toast.setDuration(Duration.seconds(3));
        assertThat(toast.getDuration()).isEqualTo(Duration.seconds(3));

        toast.setDuration(Duration.INDEFINITE);
        assertThat(toast.getDuration()).isEqualTo(Duration.INDEFINITE);

        toast.setDuration(null);
        assertThat(toast.getDuration()).isNull();
    }

    @Test
    public void testOnCloseProperty() {
        var toast = new Toast();
        assertThat(toast.getOnClose()).isNull();

        toast.setOnClose(e -> {});
        assertThat(toast.getOnClose()).isNotNull();

        toast.setOnClose(null);
        assertThat(toast.getOnClose()).isNull();
    }

    @Test
    public void testCreateDefaultSkin() {
        var toast = new Toast("Test");
        var skin = toast.createDefaultSkin();
        assertThat(skin).isInstanceOf(ToastSkin.class);
    }

    @Test
    public void testConstructorWithMessage() {
        var toast = new Toast("Hello World");
        assertThat(toast.getMessage()).isEqualTo("Hello World");
        assertThat(toast.getStyleClass()).contains("toast");
    }
}
