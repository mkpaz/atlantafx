/* SPDX-License-Identifier: MIT */

module atlantafx.base {

    requires transitive javafx.controls;

    exports atlantafx.base.controls;
    exports atlantafx.base.theme;
    exports atlantafx.base.util;

    opens atlantafx.base.theme;
}
