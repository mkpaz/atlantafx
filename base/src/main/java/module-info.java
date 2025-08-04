import org.jspecify.annotations.NullMarked;

/**
 * Provides additional controls, layout and Java API for
 * custom themes support.
 */

@NullMarked
module atlantafx.base {

    requires transitive javafx.controls;
    requires static org.jspecify;

    exports atlantafx.base.controls;
    exports atlantafx.base.layout;
    exports atlantafx.base.shim.event;
    exports atlantafx.base.shim.collections;
    exports atlantafx.base.theme;
    exports atlantafx.base.util;

    opens atlantafx.base.theme;
}
