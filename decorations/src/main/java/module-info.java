import org.jspecify.annotations.NullMarked;

/**
 * Provides window custom control buttons and themes for
 * JavaFX custom controls in title bar feature.
 */

@NullMarked
module atlantafx.decorations {
    requires static org.jspecify;

    requires transitive javafx.controls;

    exports atlantafx.decorations;
    opens atlantafx.decorations.theme;
}
