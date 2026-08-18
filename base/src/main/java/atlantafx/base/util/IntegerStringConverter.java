/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;

import static javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

/**
 * Converts text edited by users into {@link Integer} values and vice versa.
 *
 * <p>Formats integer values as plain numeric strings without decimal places
 * (for example, {@code 5} or {@code -42}).
 *
 * <p>You can configure this converter to monitor text input fields in real time.
 * It can reject non-digit characters during typing, restrict committed values
 * to a specific integer range, or reset the field when parsing fails.
 *
 * <p><b>Restricting input in a TextField with a range</b>
 * <pre>{@code
 * // rejects invalid non-digit characters during typing and
 * // clamps the entered value to [1, 100] when the user presses ENTER
 * var countField = new TextField("10");
 * var converter = new IntegerStringConverter(countField, 1, 100);
 *
 * // bind text field changes directly to a model property
 * var formatter = new TextFormatter<Integer>(converter, 10);
 * countField.setTextFormatter(formatter);
 * }</pre>
 *
 * <p><b>Configuring a Spinner component</b>
 * <pre>{@code
 * var factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50, 1);
 *
 * Spinner<Integer> quantitySpinner = new Spinner<>(factory);
 * quantitySpinner.setEditable(true);
 *
 * // automatically wires converter to spinner editor
 * IntegerStringConverter.createFor(quantitySpinner);
 * }</pre>
 */
public class IntegerStringConverter extends StringConverter<Integer> {

    private @Nullable Runnable resetHandler;

    /**
     * Creates an {@code IntegerStringConverter} without a reset action.
     *
     * <p>Ignores {@link NumberFormatException} until you set a reset callback
     * using {@link #onReset(Runnable)}.
     */
    public IntegerStringConverter() {
        // default constructor
    }

    /**
     * Creates an {@code IntegerStringConverter} with an editor reset callback.
     *
     * @param resetHandler the action to run when input parsing fails;
     *                     passing {@code null} clears any existing action
     */
    public IntegerStringConverter(Runnable resetHandler) {
        this.resetHandler = resetHandler;
    }

    /**
     * Creates an {@code IntegerStringConverter} that monitors a text field.
     *
     * <p>Filters out non-integer characters while the user types. When the user commits
     * the text, this converter restricts the value to the range between {@code min}
     * and {@code max} (inclusive). If the committed text is invalid, it resets
     * the text field to the value closest to zero within the valid range.
     *
     * @param input the text field providing user input
     * @param min   the lowest allowed value
     * @param max   the highest allowed value (inclusive)
     */
    public IntegerStringConverter(TextField input, int min, int max) {
        int resetValue = Math.min(Math.max(0, min), max);
        resetHandler = () -> input.setText(Integer.toString(resetValue));

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
                Integer.parseInt(val);
            } catch (NumberFormatException e) {
                Platform.runLater(() -> input.setText(old));
            }
        });

        // validate committed input and restrict to legal range
        EventHandler<ActionEvent> oldHandler = input.getOnAction();
        input.setOnAction(t -> {
            int value = fromString(input.getText());

            int restricted = Math.min(Math.max(value, min), max);
            if (value != restricted) {
                input.setText(Integer.toString(restricted));
            }

            if (oldHandler != null) {
                oldHandler.handle(t);
            }
        });
    }

    /**
     * Creates an {@code IntegerStringConverter} for the specified spinner.
     *
     * @param spinner the target spinner component
     * @return a new converter configured for the spinner
     */
    public static IntegerStringConverter createFor(Spinner<Integer> spinner) {
        IntegerSpinnerValueFactory factory = (IntegerSpinnerValueFactory) spinner.getValueFactory();
        IntegerStringConverter converter = new IntegerStringConverter(
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
     * Converts a text string into an integer value.
     *
     * <p>Returns {@code 0} and triggers the reset action if the input string is
     * {@code null}, empty, or contains an invalid number format.
     *
     * @param s the string to convert
     * @return the parsed integer value, or {@code 0} if conversion fails
     */
    @Override
    public Integer fromString(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            if (resetHandler != null) {
                resetHandler.run();
            }
            return 0;
        }

        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            if (resetHandler != null) {
                resetHandler.run();
            }
            return 0;
        }
    }

    /**
     * Converts an integer value into its text representation.
     *
     * <p>Returns {@code "0"} if the value is {@code null}.
     *
     * @param value the integer value to convert
     * @return the formatted string
     */
    @Override
    public String toString(@Nullable Integer value) {
        if (value == null) {
            return "0";
        }
        return Integer.toString(value);
    }
}