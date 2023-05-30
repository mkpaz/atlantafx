/**
 * Provides additional controls, layout and Java API for
 * custom themes support.
 */

module atlantafx.base {

    requires transitive javafx.controls;
    requires static org.jetbrains.annotations;

    exports atlantafx.base.controls;
    exports atlantafx.base.layout;
    exports atlantafx.base.theme;
    exports atlantafx.base.util;

    opens atlantafx.base.theme;
}
