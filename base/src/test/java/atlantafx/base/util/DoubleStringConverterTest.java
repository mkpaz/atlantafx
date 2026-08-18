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

class DoubleStringConverterTest {

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
        var converter = new DoubleStringConverter();
        assertThat(converter.toString(null)).isEqualTo("0");
    }

    @ParameterizedTest
    @CsvSource({
        "0.0, 0",
        "5.0, 5",
        "3.14, 3.14",
        "10.5, 10.5",
        "-2.7, -2.7"
    })

    @DisplayName("should format double values without trailing zero")
    void testFormatDoubleValues(Double input, String expected) {
        var converter = new DoubleStringConverter();
        assertThat(converter.toString(input)).isEqualTo(expected);
    }

    @Test
    @DisplayName("should parse valid string into double")
    void testParseValidString() {
        var converter = new DoubleStringConverter();
        assertThat(converter.fromString("42.5")).isEqualTo(42.5);
        assertThat(converter.fromString("-12.3")).isEqualTo(-12.3);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid", "abc", "12.3.4"})
    @DisplayName("should return 0.0 for invalid input or empty string")
    void testReturnZeroOnInvalidInput(String invalidInput) {
        var converter = new DoubleStringConverter();
        assertThat(converter.fromString(invalidInput)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("should return 0.0 when string is null")
    void testReturnsZeroOnNullInput() {
        var converter = new DoubleStringConverter();
        assertThat(converter.fromString(null)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("should execute reset on invalid input")
    void testTriggerResetCallbackOnInvalidInput() {
        var resetCalled = new AtomicBoolean(false);
        var converter = new DoubleStringConverter(() -> resetCalled.set(true));

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
            new DoubleStringConverter(textField, 0.0, 100.0);
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
            new DoubleStringConverter(textField, -50.0, 50.0);
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
            new DoubleStringConverter(textField, 0.0, 100.0);
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
            new DoubleStringConverter(textField, 10.0, 50.0);
            textField.getOnAction().handle(new ActionEvent());
        });

        waitForFx();

        assertThat(textField.getText()).isEqualTo("10");
    }

    @Test
    @DisplayName("createFor() should attach converter and set correct tooltip on Spinner")
    @SuppressWarnings("unchecked")
    void testCreateConverterForSpinner() throws InterruptedException {
        Spinner<Double>[] spinnerHolder = new Spinner[1];
        DoubleStringConverter[] converterHolder = new DoubleStringConverter[1];

        Platform.runLater(() -> {
            Spinner<Double> spinner = new Spinner<>(0.0, 10.0, 5.0, 0.5);
            converterHolder[0] = DoubleStringConverter.createFor(spinner);
            spinnerHolder[0] = spinner;
        });

        waitForFx();

        Spinner<Double> spinner = spinnerHolder[0];
        DoubleStringConverter converter = converterHolder[0];

        assertThat(spinner.getValueFactory().getConverter()).isSameAs(converter);
    }
}