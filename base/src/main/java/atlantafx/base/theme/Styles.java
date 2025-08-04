/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import java.util.Objects;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import org.jspecify.annotations.Nullable;

/**
 * A set of constants and utility methods that simplifies adding CSS
 * classes programmatically.
 */
@SuppressWarnings("unused")
public final class Styles {

    public static final String DATA_URI_PREFIX = "data:base64,";

    // Colors

    public static final String ACCENT = "accent";
    public static final String SUCCESS = "success";
    public static final String WARNING = "warning";
    public static final String DANGER = "danger";

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

    public static final String TABS_BORDER_TOP = "border-top";
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
    public static final String TEXT_ON_EMPHASIS = "text-on-emphasis";

    // Pseudo-classes

    public static final PseudoClass STATE_ACCENT = PseudoClass.getPseudoClass(ACCENT);
    public static final PseudoClass STATE_SUCCESS = PseudoClass.getPseudoClass(SUCCESS);
    public static final PseudoClass STATE_WARNING = PseudoClass.getPseudoClass(WARNING);
    public static final PseudoClass STATE_DANGER = PseudoClass.getPseudoClass(DANGER);
    public static final PseudoClass STATE_INTERACTIVE = PseudoClass.getPseudoClass(INTERACTIVE);

    // Backgrounds

    public static final String BG_DEFAULT = "bg-default";
    public static final String BG_INSET = "bg-inset";
    public static final String BG_SUBTLE = "bg-subtle";

    public static final String BG_NEUTRAL_EMPHASIS_PLUS = "bg-neutral-emphasis-plus";
    public static final String BG_NEUTRAL_EMPHASIS = "bg-neutral-emphasis";
    public static final String BG_NEUTRAL_MUTED = "bg-neutral-muted";
    public static final String BG_NEUTRAL_SUBTLE = "bg-neutral-subtle";

    public static final String BG_ACCENT_EMPHASIS = "bg-accent-emphasis";
    public static final String BG_ACCENT_MUTED = "bg-accent-muted";
    public static final String BG_ACCENT_SUBTLE = "bg-accent-subtle";

    public static final String BG_WARNING_EMPHASIS = "bg-warning-emphasis";
    public static final String BG_WARNING_MUTED = "bg-warning-muted";
    public static final String BG_WARNING_SUBTLE = "bg-warning-subtle";

    public static final String BG_SUCCESS_EMPHASIS = "bg-success-emphasis";
    public static final String BG_SUCCESS_MUTED = "bg-success-muted";
    public static final String BG_SUCCESS_SUBTLE = "bg-success-subtle";

    public static final String BG_DANGER_EMPHASIS = "bg-danger-emphasis";
    public static final String BG_DANGER_MUTED = "bg-danger-muted";
    public static final String BG_DANGER_SUBTLE = "bg-danger-subtle";

    // Borders

    public static final String BORDER_DEFAULT = "border-default";
    public static final String BORDER_MUTED = "border-muted";
    public static final String BORDER_SUBTLE = "border-subtle";

    private Styles() {
        // Default constructor
    }

    /**
     * Adds the given style class to the node if it's not present,
     * otherwise removes it.
     *
     * @param node       The target node.
     * @param styleClass The style class to be toggled.
     * @throws NullPointerException if node or style class is null
     */
    public static void toggleStyleClass(Node node, String styleClass) {
        int idx = node.getStyleClass().indexOf(styleClass);
        if (idx >= 0) {
            node.getStyleClass().remove(idx);
        } else {
            node.getStyleClass().add(styleClass);
        }
    }

    /**
     * Adds the given style class to the node and removes the excluded classes.
     * This method is supposed to be used when only one from a set of classes
     * have to be present at once.
     *
     * @param node       The target node.
     * @param styleClass The style class to be toggled.
     * @param excludes   The style classes to be excluded.
     * @throws NullPointerException if node or styleClass is null
     */
    public static void addStyleClass(Node node,
                                     String styleClass,
                                     String @Nullable... excludes) {
        if (excludes != null && excludes.length > 0) {
            node.getStyleClass().removeAll(excludes);
        }

        if (!node.getStyleClass().contains(styleClass)) {
            node.getStyleClass().add(styleClass);
        }
    }

    /**
     * Activates given pseudo-class to the node and deactivates the excluded pseudo-classes.
     * This method is supposed to be used when only one from a set of pseudo-classes
     * have to be present at once.
     *
     * @param node        The node to activate the pseudo-class on.
     * @param pseudoClass The pseudo-class to be activated.
     * @param excludes    The pseudo-classes to be deactivated.
     * @throws NullPointerException if node or pseudo-class is null
     */
    public static void activatePseudoClass(Node node,
                                           PseudoClass pseudoClass,
                                           PseudoClass @Nullable... excludes) {
        if (excludes != null) {
            for (PseudoClass exclude : excludes) {
                node.pseudoClassStateChanged(exclude, false);
            }
        }
        node.pseudoClassStateChanged(pseudoClass, true);
    }

    /**
     * Appends CSS style declaration to the specified node.
     * There's no check for duplicates, so the CSS declarations with the same property
     * name can be appended multiple times.
     *
     * @param node  The node to append the new style declaration.
     * @param prop  The CSS property name.
     * @param value The CSS property value.
     * @throws NullPointerException if node is null
     */
    public static void appendStyle(Node node, String prop, String value) {
        if (prop.isBlank() || value.isBlank()) {
            System.err.printf("Ignoring invalid style: property = '%s', value = '%s'%n", prop, value);
            return;
        }

        var style = Objects.requireNonNullElse(node.getStyle(), "");
        if (!style.isEmpty() && !style.endsWith(";")) {
            style += ";";
        }
        style = style + prop.trim() + ":" + value.trim() + ";";
        node.setStyle(style);
    }

    /**
     * Removes the specified CSS style declaration from the specified node.
     *
     * @param node The node to remove the style from.
     * @param prop The name of the style property to remove.
     * @throws NullPointerException if node is null
     */
    @SuppressWarnings("StringSplitter")
    public static void removeStyle(Node node, String prop) {
        var currentStyle = node.getStyle();
        if (currentStyle == null || currentStyle.isBlank()) {
            return;
        }

        if (prop.isBlank()) {
            System.err.printf("Ignoring invalid property = '%s'%n", prop);
            return;
        }

        String[] stylePairs = currentStyle.split(";");
        var newStyle = new StringBuilder();

        for (var s : stylePairs) {
            String[] styleParts = s.split(":");
            if (!styleParts[0].trim().equals(prop)) {
                newStyle.append(s);
                newStyle.append(";");
            }
        }

        node.setStyle(newStyle.toString());
    }

    /**
     * Converts a CSS string to the Base64-encoded data URI. The resulting string is
     * an inline data URI that can be applied to any node in the following manner:
     *
     * <pre>{@code}
     * var dataUri = Styles.toDataURI();
     * node.getStylesheets().add(dataUri);
     * node.getStylesheets().contains(dataUri);
     * node.getStylesheets().remove(dataUri);
     * </pre>
     *
     * @param css The CSS string to encode.
     * @return The resulting data URI string.
     */
    public static String toDataURI(String css) {
        return DATA_URI_PREFIX + new String(Base64.getEncoder().encode(css.getBytes(UTF_8)), UTF_8);
    }
}
