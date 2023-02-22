/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.TabPane;

/**
 * A set of constants and utility methods that simplifies adding
 * CSS classes programmatically.
 */
@SuppressWarnings("unused")
public final class Styles {

    // Colors

    public static final String ACCENT = "accent";
    public static final String SUCCESS = "success";
    public static final String WARNING = "warning";
    public static final String DANGER = "danger";

    public static final PseudoClass STATE_ACCENT = PseudoClass.getPseudoClass(ACCENT);
    public static final PseudoClass STATE_SUCCESS = PseudoClass.getPseudoClass(SUCCESS);
    public static final PseudoClass STATE_WARNING = PseudoClass.getPseudoClass(WARNING);
    public static final PseudoClass STATE_DANGER = PseudoClass.getPseudoClass(DANGER);

    // Controls

    public static final String TEXT = "text";
    public static final String FONT_ICON = "font-icon";

    public static final String BUTTON_CIRCLE = "button-circle";
    public static final String BUTTON_ICON = "button-icon";
    public static final String BUTTON_OUTLINED = "button-outlined";

    public static final String LEFT_PILL = "left-pill";
    public static final String CENTER_PILL = "center-pill";
    public static final String RIGHT_PILL = "right-pill";

    public static final String SMALL = "small";
    public static final String MEDIUM = "medium";
    public static final String LARGE = "large";

    public static final String TOP = "top";
    public static final String RIGHT = "right";
    public static final String BOTTOM = "bottom";
    public static final String LEFT = "left";
    public static final String CENTER = "center";

    public static final String FLAT = "flat";
    public static final String BORDERED = "bordered";
    public static final String DENSE = "dense";
    public static final String ELEVATED_1 = "elevated-1";
    public static final String ELEVATED_2 = "elevated-2";
    public static final String ELEVATED_3 = "elevated-3";
    public static final String ELEVATED_4 = "elevated-4";
    public static final String INTERACTIVE = "interactive";
    public static final String ROUNDED = "rounded";
    public static final String STRIPED = "striped";

    public static final String TABS_CLASSIC = "classic";
    public static final String TABS_FLOATING = TabPane.STYLE_CLASS_FLOATING;

    // Text

    public static final String TITLE_1 = "title-1";
    public static final String TITLE_2 = "title-2";
    public static final String TITLE_3 = "title-3";
    public static final String TITLE_4 = "title-4";
    public static final String TEXT_CAPTION = "text-caption";
    public static final String TEXT_SMALL = "text-small";

    public static final String TEXT_BOLD = "text-bold";
    public static final String TEXT_BOLDER = "text-bolder";
    public static final String TEXT_NORMAL = "text-normal";
    public static final String TEXT_LIGHTER = "text-lighter";

    public static final String TEXT_ITALIC = "text-italic";
    public static final String TEXT_OBLIQUE = "text-oblique";
    public static final String TEXT_STRIKETHROUGH = "text-strikethrough";
    public static final String TEXT_UNDERLINED = "text-underlined";

    public static final String TEXT_MUTED = "text-muted";
    public static final String TEXT_SUBTLE = "text-subtle";

    private Styles() {
        // Default constructor
    }

    /**
     * Adds given style class to the node if it's not present, otherwise
     * removes it.
     */
    public static void toggleStyleClass(Node node, String styleClass) {
        if (node == null) {
            throw new NullPointerException("Node cannot be null!");
        }
        if (styleClass == null) {
            throw new NullPointerException("Style class cannot be null!");
        }

        int idx = node.getStyleClass().indexOf(styleClass);
        if (idx > 0) {
            node.getStyleClass().remove(idx);
        } else {
            node.getStyleClass().add(styleClass);
        }
    }

    /**
     * Adds given style class to the node and removes the excluded classes.
     * This method is supposed to be used when only one from a set of classes
     * have to be present at once.
     */
    public static void addStyleClass(Node node, String styleClass, String... excludes) {
        if (node == null) {
            throw new NullPointerException("Node cannot be null!");
        }
        if (styleClass == null) {
            throw new NullPointerException("Style class cannot be null!");
        }

        if (excludes != null && excludes.length > 0) {
            node.getStyleClass().removeAll(excludes);
        }
        node.getStyleClass().add(styleClass);
    }

    /**
     * Activates given pseudo-class to the node and deactivates the excluded pseudo-classes.
     * This method is supposed to be used when only one from a set of pseudo-classes
     * have to be present at once.
     */
    public static void activatePseudoClass(Node node, PseudoClass pseudoClass, PseudoClass... excludes) {
        if (node == null) {
            throw new NullPointerException("Node cannot be null!");
        }
        if (pseudoClass == null) {
            throw new NullPointerException("PseudoClass cannot be null!");
        }

        if (excludes != null) {
            for (PseudoClass exclude : excludes) {
                node.pseudoClassStateChanged(exclude, false);
            }
        }
        node.pseudoClassStateChanged(pseudoClass, true);
    }
}
