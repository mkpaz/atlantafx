/* SPDX-License-Identifier: MIT */

package atlantafx.decorations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.HeaderButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

/**
 * The HeaderButtonGroup represents a group consisting of one or more buttons
 * to control the window minimize, maximize, and close operations. The button
 * group uses the JavaFX stage {@link StageStyle#EXTENDED} and replaces the default
 * buttons.
 *
 * <p>The group is styleable, and you can find a variety of predefined themes in the
 * {@link Decoration} enum.
 *
 * <p>Usage example:
 * <pre>{@code
 * var headerBar = new HeaderBar();
 *
 * var windowsButtons = HeaderButtonGroup.standardGroup();
 * windowsButtons.install(headerBar, stage);
 *
 * var root = new BorderPane();
 * root.setTop(headerBar);
 *
 * var scene = new Scene(root, 800, 600);
 * scene.getStylesheets().add(Decoration.WIN10_LIGHT.getStylesheet());
 *
 * stage.initStyle(StageStyle.EXTENDED);
 * stage.setScene(scene);
 * stage.setOnCloseRequest(e -> windowsButtons.uninstall(headerBar, stage));
 * stage.show();
 * }</pre>
 */
@SuppressWarnings("deprecation")
public class HeaderButtonGroup extends Control {

    protected static final PseudoClass PSEUDO_CLASS_MAXIMIZED = PseudoClass.getPseudoClass("maximized");
    protected static final PseudoClass PSEUDO_CLASS_INACTIVE = PseudoClass.getPseudoClass("inactive");
    protected static final String OS = System.getProperty("os.name", "generic").toLowerCase();
    protected static final boolean MAC = OS.contains("mac") || OS.contains("darwin");

    protected final List<HeaderButton> buttons;
    protected @Nullable Subscription stageMaximizedSubscription;
    protected @Nullable Subscription stageFocusedSubscription;

    /**
     * Creates a HeaderButtonGroup using the specified buttons.
     *
     * @param buttons the buttons to include in the group
     */
    public HeaderButtonGroup(HeaderButton... buttons) {
        this(Arrays.asList(buttons));
    }

    /**
     * Creates a HeaderButtonGroup using the specified list of buttons.
     *
     * @param buttons the list of buttons to include in the group
     */
    public HeaderButtonGroup(List<HeaderButton> buttons) {
        super();

        getStyleClass().add("header-button-group");
        this.buttons = Collections.unmodifiableList(buttons);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Skin<?> createDefaultSkin() {
        return new HeaderButtonGroupSkin(this);
    }

    /**
     * Returns the unmodifiable list of buttons that this group consists of.
     */
    public List<HeaderButton> getButtons() {
        return buttons;
    }

    /**
     * Returns the button of the specified type from the group.
     *
     * @param type the type of the header button to retrieve
     */
    public Optional<HeaderButton> getButton(HeaderButtonType type) {
        return getButtons().stream()
                   .filter(button -> Objects.equals(type, button.getType()))
                   .findFirst();
    }

    /**
     * See {@link #install(HeaderBar, Stage, Alignment)}.
     */
    public void install(HeaderBar headerBar, Stage stage) {
        install(headerBar, stage, Alignment.AUTO);
    }

    /**
     * Installs this HeaderButtonGroup to the specified HeaderBar and Stage.
     *
     * <p>This includes disabling the JavaFX defaults, positioning the group
     * depending on the given alignment, and setting up the listeners required for
     * the decorations theme to work properly.
     *
     * @param headerBar the HeaderBar to which this group will be installed
     * @param stage     the Stage in which the HeaderBar is displayed
     * @param alignment the alignment of the button group
     */
    public void install(HeaderBar headerBar, Stage stage, @Nullable Alignment alignment) {
        removeButtons(headerBar);

        switch (alignment) {
            case AUTO -> setAutoAlignment(headerBar);
            case LEADING -> headerBar.setLeading(this);
            case TRAILING -> headerBar.setTrailing(this);
            case null -> setAutoAlignment(headerBar);
        }

        if (stageMaximizedSubscription == null) {
            stageMaximizedSubscription = stage.maximizedProperty().subscribe(() -> {
                var scene = stage.getScene();
                if (scene != null && scene.getRoot() != null) {
                    scene.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_MAXIMIZED, stage.isMaximized());
                }
            });
        }

        if (stageFocusedSubscription == null) {
            stageFocusedSubscription = stage.focusedProperty().subscribe(() -> {
                var scene = stage.getScene();
                if (scene != null && scene.getRoot() != null) {
                    scene.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_INACTIVE, !stage.isFocused());
                }
            });
        }

        HeaderBar.setPrefButtonHeight(stage, 0);
    }

    /**
     * Uninstalls this HeaderButtonGroup from the specified HeaderBar and Stage.
     *
     * <p>This method is the opposite of {@link #install(HeaderBar, Stage, Alignment)}.
     *
     * @param headerBar the HeaderBar from which this group will be uninstalled
     * @param stage     the Stage in which the HeaderBar is displayed
     */
    public void uninstall(HeaderBar headerBar, Stage stage) {
        if (stageMaximizedSubscription != null) {
            stageMaximizedSubscription.unsubscribe();
            stageMaximizedSubscription = null;
        }

        if (stageFocusedSubscription != null) {
            stageFocusedSubscription.unsubscribe();
            stageFocusedSubscription = null;
        }

        removeButtons(headerBar);

        HeaderBar.setPrefButtonHeight(stage, HeaderBar.USE_DEFAULT_SIZE);
    }

    protected void setAutoAlignment(HeaderBar headerBar) {
        if (MAC) {
            headerBar.setLeading(this);
        } else {
            headerBar.setTrailing(this);
        }
    }

    protected void removeButtons(HeaderBar headerBar) {
        if (headerBar.getTrailing() == this) {
            headerBar.setTrailing(null);
        } else if (headerBar.getLeading() == this) {
            headerBar.setLeading(null);
        }
    }

    //=========================================================================

    /**
     * Creates a standard HeaderButtonGroup that includes minimize, maximize,
     * and close buttons.
     */
    public static HeaderButtonGroup standardGroup() {
        return new HeaderButtonGroup(
            new HeaderButton(HeaderButtonType.ICONIFY),
            new HeaderButton(HeaderButtonType.MAXIMIZE),
            new HeaderButton(HeaderButtonType.CLOSE)
        );
    }

    /**
     * Creates a utility HeaderButtonGroup that includes minimize and close buttons.
     */
    public static HeaderButtonGroup utilityGroup() {
        return new HeaderButtonGroup(
            new HeaderButton(HeaderButtonType.ICONIFY),
            new HeaderButton(HeaderButtonType.CLOSE)
        );
    }

    /**
     * Creates a HeaderButtonGroup consisting of a single close button.
     */
    public static HeaderButtonGroup closeOnlyGroup() {
        return new HeaderButtonGroup(
            new HeaderButton(HeaderButtonType.CLOSE)
        );
    }
}
