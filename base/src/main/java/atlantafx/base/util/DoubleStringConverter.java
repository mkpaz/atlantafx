/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;

import static javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;

/**
 * Converts text edited by users into {@link Double} values and vice versa.
 *
 * <p>Formats numbers with up to two decimal places, omitting the decimal point
 * and fractional zero if the value is a whole number (for example, {@code 5}
 * or {@code 3.14}).
 *
 * <p>You can configure this converter to monitor text input fields in real time.
 * It can reject invalid characters during typing, restrict committed values
 * to a specific numeric range, or reset the field when parsing fails.
 *
 * <p><b>Restricting input in a TextField with a range</b>
 * <pre>{@code
 * // rejects invalid letters during typing and
 * // clamps the entered value to [0.0, 1.0] when the user presses ENTER
 * var opacityField = new TextField("1.0");
 * var converter = new DoubleStringConverter(opacityField, 0.0, 1.0);
 *
 * // bind text field changes directly to a model property
 * var formatter = new TextFormatter<Double>(converter, 1.0);
 * opacityField.setTextFormatter(formatter);
 * }</pre>
 *
 * <p><b>Configuring a Spinner component</b>
 * <pre>{@code
 * var factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100.0, 50.0, 0.5);
 *
 * var percentSpinner = new Spinner<Double>(factory);
 * percentSpinner.setEditable(true);
 *
 * // automatically wires converter to spinner editor
 * DoubleStringConverter.createFor(percentSpinner);
 * }</pre>
 */
public class DoubleStringConverter extends StringConverter<Double> {

    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    private @Nullable Runnable resetHandler;

    /**
     * Creates a {@code DoubleStringConverter} without a reset action.
     *
     * <p>Ignores {@link NumberFormatException} until you set a reset callback
     * using {@link #onReset(Runnable)}.
     */
    public DoubleStringConverter() {
        // default constructor
    }

    /**
     * Creates a {@code DoubleStringConverter} with an editor reset callback.
     *
     * @param resetHandler the action to run when input parsing fails;
     *                     passing {@code null} clears any existing action
     */
    public DoubleStringConverter(Runnable resetHandler) {
        this.resetHandler = resetHandler;
    }

    /**
     * Creates a {@code DoubleStringConverter} that monitors a text field.
     *
     * <p>Filters out invalid characters while the user types. When the user commits
     * the text, this converter restricts the value to the range between {@code min}
     * and {@code max} (inclusive). If the committed text is invalid, it resets
     * the text field to the value closest to zero within the valid range.
     *
     * @param input the text field providing user input
     * @param min   the lowest allowed value
     * @param max   the highest allowed value (inclusive)
     */
    public DoubleStringConverter(TextField input, double min, double max) {
        double resetValue = Math.min(Math.max(0, min), max);
        resetHandler = () -> input.setText(decimalFormat.format(resetValue));

        input.textProperty().addListener((_, old, val) -> {
            if (val == null || val.isEmpty()) {
                return;
            }

            // minus allowed for negative values
            if (min < 0 && val.endsWith("-")) {
                if (val.length() > 1) {
                    Platform.runLater(() -> input.setText("-"));
                }
                return;
            }

            // revert to the old value if new value cannot be parsed
            try {
                Double.parseDouble(val);
            } catch (NumberFormatException e) {
                Platform.runLater(() -> input.setText(old));
            }
        });

        // validate committed input and restrict to legal range
        EventHandler<ActionEvent> oldHandler = input.getOnAction();
        input.setOnAction(t -> {
            double value = fromString(input.getText());

            double restricted = Math.min(Math.max(value, min), max);
            if (value != restricted) {
                input.setText(decimalFormat.format(restricted));
            }

            if (oldHandler != null) {
                oldHandler.handle(t);
            }
        });
    }

    /**
     * Creates a {@code DoubleStringConverter} for the specified spinner.
     *
     * @param spinner the target spinner component
     * @return a new converter configured for the spinner
     */
    public static DoubleStringConverter createFor(Spinner<Double> spinner) {
        DoubleSpinnerValueFactory factory = (DoubleSpinnerValueFactory) spinner.getValueFactory();
        DoubleStringConverter converter = new DoubleStringConverter(
            spinner.getEditor(), factory.getMin(), factory.getMax()
        );
        factory.setConverter(converter);

        return converter;
    }

    /**
     * Sets the action to run when input parsing fails.
     *
     * <p>Setting a custom handler replaces any automatic reset behavior
     * configured for a text field or spinner.
     *
     * @param handler the action to execute on {@link NumberFormatException}
     */
    public void onReset(Runnable handler) {
        this.resetHandler = handler;
    }

    /**
     * Converts a text string into a double value.
     *
     * <p>Returns {@code 0.0} and triggers the reset action if the input string is
     * {@code null}, empty, or contains an invalid number format.
     *
     * @param s the string to convert
     * @return the parsed double value, or {@code 0.0} if conversion fails
     */
    @Override
    public Double fromString(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            if (resetHandler != null) {
                resetHandler.run();
            }
            return 0.0;
        }

        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            if (resetHandler != null) {
                resetHandler.run();
            }
            return 0.0;
        }
    }

    /**
     * Converts a double value into its text representation.
     *
     * <p>Formats numbers up to two decimal places, omitting trailing zeros.
     * Returns {@code "0"} if the value is {@code null}.
     *
     * @param value the double value to convert
     * @return the formatted string
     */
    @Override
    public String toString(@Nullable Double value) {
        if (value == null) {
            return "0";
        }
        return decimalFormat.format(value);
    }
}
