/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

/**
 * Contains extra style class names introduced to tweak some controls view
 * if and where it makes sense. The reason of supporting tweaks is to allow
 * users to write less CSS code.
 */
public final class Tweaks {

    private Tweaks() {
        // Default constructor
    }

    /**
     * Removes or hides dropdown arrow button.
     */
    public static final String NO_ARROW = "no-arrow";

    /**
     * Removes external control borders.
     */
    public static final String EDGE_TO_EDGE = "edge-to-edge";

    /**
     * Removes control header.
     */
    public static final String NO_HEADER = "no-header";

    /**
     * Alignment.
     */
    public static final String ALIGN_LEFT = "align-left";
    public static final String ALIGN_CENTER = "align-center";
    public static final String ALIGN_RIGHT = "align-right";

    /**
     * Forces a control to use alternative icon, if available.
     */
    public static final String ALT_ICON = "alt-icon";
}
