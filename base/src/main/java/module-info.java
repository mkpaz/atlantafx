import atlantafx.base.theme.*;
import org.jspecify.annotations.NullMarked;

/**
 * Provides additional controls, layout and Java API for custom themes support.
 */
@NullMarked
module atlantafx.base {
    requires static org.jspecify;

    requires transitive javafx.controls;

    exports atlantafx.base.controls;
    exports atlantafx.base.layout;
    exports atlantafx.base.shim.event;
    exports atlantafx.base.shim.collections;
    exports atlantafx.base.theme;
    exports atlantafx.base.util;

    opens atlantafx.base.theme;

    uses Theme;

    provides Theme with
        PrimerLight, PrimerDark,
        NordLight, NordDark,
        CupertinoLight, CupertinoDark,
        Dracula;
}
