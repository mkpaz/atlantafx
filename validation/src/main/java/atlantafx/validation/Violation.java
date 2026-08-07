/* SPDX-License-Identifier: MIT */

package atlantafx.validation;

import org.jspecify.annotations.Nullable;

/**
 * Represents a single validation constraint violation, identified by a code
 * and an optional human-readable message.
 *
 * @param code    the violation code
 * @param message the violation message, or {@code null} if none was provided
 */
public record Violation(int code, @Nullable String message) { }
