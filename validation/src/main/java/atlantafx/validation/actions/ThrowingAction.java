/* SPDX-License-Identifier: MIT */

package atlantafx.validation.actions;

import atlantafx.validation.Descriptor;
import atlantafx.validation.Failure;
import atlantafx.validation.ValidationException;

import java.util.function.Function;

/**
 * An action that throws a {@link ValidationException} when a validation {@link Failure} occurs.
 *
 * @param extractor a function that extracts an exception message from a {@link Failure}
 */
public record ThrowingAction(Function<Failure, String> extractor) implements Action {

    /**
     * Constructs a {@code TooltipAction} with a default message extractor ({@link Failure#ALL_VIOLATIONS}).
     */
    public ThrowingAction() {
        this(Failure.ALL_VIOLATIONS);
    }

    @Override
    public void apply(Failure failure) {
        throw new ValidationException(extractor.apply(failure));
    }

    @Override
    public void clear(Descriptor descriptor) { }
}