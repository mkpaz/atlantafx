/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

/**
 * Represents the cascading behavior of validation rules.
 *
 * <p>This enum determines whether to continue or stop the validation
 * process based on the outcome of the validation rule.
 */
public enum Cascade {

    /**
     * Indicates that the validation process should continue
     * to the next rule, even if the current rule fails.
     */
    CONTINUE,

    /**
     * Indicates that the validation process should stop
     * immediately if the current rule fails.
     */
    STOP
}
