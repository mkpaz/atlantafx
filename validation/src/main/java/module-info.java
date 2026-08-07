import org.jspecify.annotations.NullMarked;

/**
 * A lightweight, fluent validation API designed for JavaFX applications.
 */
@NullMarked
module atlantafx.validation {
    requires static org.jspecify;

    requires javafx.controls;

    exports atlantafx.validation;
    exports atlantafx.validation.actions;
}