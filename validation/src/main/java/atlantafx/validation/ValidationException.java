/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

/**
 * Indicates an exception that occurs during validation.
 */
public class ValidationException extends RuntimeException {

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}
