/* SPDX-License-Identifier: MIT */

package atlantafx.decorations;

import javafx.scene.Node;
import javafx.scene.layout.HeaderBar;

/**
 * Represents the alignment of the {@link HeaderButtonGroup} in the {@link HeaderBar}.
 */
@SuppressWarnings("deprecation")
public enum Alignment {

    /**
     * The alignment is based on the operating system.
     * Left (leading) for macOS and right (trailing) otherwise.
     */
    AUTO,

    /**
     * Aligns the button group on the left using the {@link HeaderBar#setLeading(Node)} method.
     */
    LEADING,

    /**
     * Aligns the button group on the right using the {@link HeaderBar#setTrailing(Node)} method.
     */
    TRAILING
}