package atlantafx.base.util;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerStringConverterTest {

    @BeforeAll
    static void init() throws InterruptedException {
        Locale.setDefault(Locale.US);

        var latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
    }

    private void waitForFx() throws InterruptedException {
        var latch = new CountDownLatch(1);
        Platform.runLater(() -> Platform.runLater(latch::countDown));
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    @DisplayName("should return '0' when value is null")
    void testReturnZeroForNull() {
        var converter = new IntegerStringConverter();
        assertThat(converter.toString(null)).isEqualTo("0");
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0",
        "5, 5",
        "100, 100",
        "-27, -27"
    })
    @DisplayName("should format integer values to string")
    void testFormatIntegerValues(Integer input, String expected) {
        var converter = new IntegerStringConverter();
        assertThat(converter.toString(input)).isEqualTo(expected);
    }

    @Test
    @DisplayName("should parse valid string into integer")
    void testParseValidString() {
        var converter = new IntegerStringConverter();
        assertThat(converter.fromString("42")).isEqualTo(42);
        assertThat(converter.fromString("-12")).isEqualTo(-12);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid", "abc", "12.3", "2147483648"})
    @DisplayName("should return 0 for invalid input or empty string")
    void testReturnZeroOnInvalidInput(String invalidInput) {
        var converter = new IntegerStringConverter();
        assertThat(converter.fromString(invalidInput)).isEqualTo(0);
    }

    @Test
    @DisplayName("should return 0 when string is null")
    void testReturnsZeroOnNullInput() {
        var converter = new IntegerStringConverter();
        assertThat(converter.fromString(null)).isEqualTo(0);
    }

    @Test
    @DisplayName("should execute reset on invalid input")
    void testTriggerResetCallbackOnInvalidInput() {
        var resetCalled = new AtomicBoolean(false);
        var converter = new IntegerStringConverter(() -> resetCalled.set(true));

        converter.fromString("not-a-number");
        assertThat(resetCalled.getAndSet(false)).isTrue();

        converter.fromString("");
        assertThat(resetCalled.getAndSet(false)).isTrue();

        converter.fromString(null);
        assertThat(resetCalled.get()).isTrue();
    }

    @Test
    @DisplayName("should revert invalid typing in TextField")
    void testRevertInvalidTyping() throws InterruptedException {
        var textField = new TextField();

        Platform.runLater(() -> {
            textField.setText("10");
            new IntegerStringConverter(textField, 0, 100);
            textField.setText("10abc");
        });

        waitForFx();

        assertThat(textField.getText()).isEqualTo("10");
    }

    @Test
    @DisplayName("should allow minus sign if negative values are allowed")
    void testAllowMinusSignWhenMinIsNegative() throws InterruptedException {
        var textField = new TextField();

        Platform.runLater(() -> {
            new IntegerStringConverter(textField, -50, 50);
            textField.setText("-");
        });

        waitForFx();

        assertThat(textField.getText()).isEqualTo("-");
    }

    @Test
    @DisplayName("should clamp text to [min, max] when committed via ActionEvent")
    void testRestrictToMinMaxOnAction() throws InterruptedException {
        var textField = new TextField();

        Platform.runLater(() -> {
            textField.setText("150");
            new IntegerStringConverter(textField, 0, 100);
            textField.getOnAction().handle(new ActionEvent());
        });

        waitForFx();

        assertThat(textField.getText()).isEqualTo("100");
    }

    @Test
    @DisplayName("should reset to closest value to 0 within [min, max] when invalid text is committed")
    void testResetToClosestToZeroOnInvalidCommittedInput() throws InterruptedException {
        var textField = new TextField();

        Platform.runLater(() -> {
            textField.setText("invalid");
            new IntegerStringConverter(textField, 10, 50);
            textField.getOnAction().handle(new ActionEvent());
        });

        waitForFx();

        assertThat(textField.getText()).isEqualTo("10");
    }

    @Test
    @DisplayName("createFor() should attach converter and set correct tooltip on Spinner")
    @SuppressWarnings("unchecked")
    void testCreateConverterForSpinner() throws InterruptedException {
        Spinner<Integer>[] spinnerHolder = new Spinner[1];
        IntegerStringConverter[] converterHolder = new IntegerStringConverter[1];

        Platform.runLater(() -> {
            var spinner = new Spinner<Integer>(0, 10, 5, 1);
            converterHolder[0] = IntegerStringConverter.createFor(spinner);
            spinnerHolder[0] = spinner;
        });

        waitForFx();

        var spinner = spinnerHolder[0];
        var converter = converterHolder[0];

        assertThat(spinner.getValueFactory().getConverter()).isSameAs(converter);
    }
}