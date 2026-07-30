import org.jspecify.annotations.NullMarked;

/**
 * Provides a set of loading indicators (aka spins).
 */
@NullMarked
module atlantafx.spins {
    requires static org.jspecify;

    requires transitive javafx.controls;
    requires transitive atlantafx.base;
    requires javafx.graphics;

    exports atlantafx.spins;
    opens atlantafx.spins;
}