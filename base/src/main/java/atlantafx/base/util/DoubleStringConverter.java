/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import java.text.DecimalFormat;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;

/**
 * Converts between user-edited strings and Double values.
 *
 * <p>Accepts an optional Runnable that resets the editor on {@code NumberFormatException},
 * or a TextField or Spinner that is preemptively monitored for invalid input
 * during typing, and restricts valid input to a specified range when committed.
 *
 * <p>This implementation shows up to two decimal digits, but only if a fractional part
 * exists. The default implementation always shows one decimal digit which hinders typing.
 *
 * @author Christoph Nahr
 */
public class DoubleStringConverter extends StringConverter<Double> {

    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    private @Nullable Runnable reset;

    /**
     * Creates a DoubleStringConverter.
     *
     * <p>Swallows {@code NumberFormatException} but does nothing
     * in response until {@link #setReset} is defined.
     */
    public DoubleStringConverter() {
        // Default constructor
    }

    /**
     * Creates a DoubleStringConverter with an editor reset callback.
     * Specifying null has the same effect as the default constructor.
     *
     * @param reset the Runnable to call upon {@code NumberFormatException}
     */
    public DoubleStringConverter(Runnable reset) {
        this.reset = reset;
    }

    /**
     * Creates a DoubleStringConverter with the specified input range.
     *
     * <p>Preemptively monitors input to reject any invalid characters during
     * typing. Restricts input to [{@code min}, {@code max}] (inclusive) when
     * valid text is committed, and resets input to the closest value to zero
     * within [{@code min}, {@code max}] when invalid text is committed.
     *
     * @param input The TextField providing user-edited strings.
     * @param min   The smallest valid value.
     * @param max   The greatest valid value.
     * @throws NullPointerException if  input is {@code null}.
     */
    public DoubleStringConverter(TextField input, double min, double max) {
        final double resetValue = Math.min(Math.max(0, min), max);
        reset = () -> input.setText(decimalFormat.format(resetValue));

        // restrict direct input to valid numerical characters
        input.textProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            // special case: minus sign if negative values allowed
            if (min < 0 && newValue.endsWith("-")) {
                if (newValue.length() > 1) {
                    Platform.runLater(() -> input.setText("-"));
                }
                return;
            }

            // revert to oldValue if newValue cannot be parsed
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                Platform.runLater(() -> input.setText(oldValue));
            }
        });

        // validate committed input and restrict to legal range
        final EventHandler<ActionEvent> oldHandler = input.getOnAction();
        input.setOnAction(t -> {
            // fromString performs input validation
            final double value = fromString(input.getText());

            // redundant for Spinner but not harmful
            final double restricted = Math.min(Math.max(value, min), max);
            if (value != restricted) {
                input.setText(decimalFormat.format(restricted));
            }

            // required for Spinner which handles onAction
            if (oldHandler != null) {
                oldHandler.handle(t);
            }
        });
    }

    /**
     * Creates a DoubleStringConverter for the specified Spinner.
     *
     * <p>Uses the TextField and minimum and maximum values of the specified
     * Spinner for construction, and also sets the new DoubleStringConverter
     * on its {@link SpinnerValueFactory.DoubleSpinnerValueFactory}.
     *
     * @param spinner The Spinner to create a DoubleStringConverter for.
     * @return the new DoubleStringConverter
     * @throws NullPointerException if the Spinner is {@code null}
     */
    public static DoubleStringConverter createFor(Spinner<Double> spinner) {
        final SpinnerValueFactory.DoubleSpinnerValueFactory factory =
            (SpinnerValueFactory.DoubleSpinnerValueFactory) spinner.getValueFactory();

        final DoubleStringConverter converter = new DoubleStringConverter(
            spinner.getEditor(), factory.getMin(), factory.getMax());

        factory.setConverter(converter);
        spinner.setTooltip(new Tooltip(String.format(
            "Enter a value between %.2f and %.2f",
            factory.getMin(), factory.getMax())));

        return converter;
    }

    /**
     * Sets the editor reset callback.
     *
     * <p>Specify {@code null} to clear a previously set Runnable. When creating
     * a DoubleStringConverter for a TextField or Spinner, this callback is
     * automatically defined to reset committed invalid input to the closest value
     * to zero within the legal range. Setting a different callback will overwrite this
     * functionality.
     *
     * @param reset The Runnable to call upon NumberFormatException.
     * @see #fromString
     */
    public void setReset(Runnable reset) {
        this.reset = reset;
    }

    /**
     * Converts the specified string into its double value.
     * A {@code null}, empty, or otherwise invalid argument returns zero
     * and also executes the editor reset callback, if any.
     *
     * @param s The string to convert.
     * @return the double value of {@code s}
     * @see #setReset
     */
    @Override
    public Double fromString(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            if (reset != null) {
                reset.run();
            }
            return 0.0;
        }

        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            if (reset != null) {
                reset.run();
            }
            return 0.0;
        }
    }

    /**
     * Converts the specified double into its string form.
     * A {@code null} argument is converted into the literal string "0".
     *
     * @param value The Double to convert.
     * @return the string form of {@code value}
     */
    @Override
    public String toString(@Nullable Double value) {
        if (value == null) {
            return "0";
        }
        return decimalFormat.format(value);
    }
}
