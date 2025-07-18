/**
 * Provides window custom control buttons and themes for
 * JavaFX custom controls in title bar feature.
 */

module atlantafx.decorations {
    requires transitive javafx.controls;
    requires static org.jetbrains.annotations;

    exports atlantafx.decorations;
    opens atlantafx.decorations.theme;
}