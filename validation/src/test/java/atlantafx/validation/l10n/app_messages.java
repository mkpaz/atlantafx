/* SPDX-License-Identifier: MIT */

package atlantafx.validation.l10n;

import java.util.ListResourceBundle;

public class app_messages extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {"err.invalid", "Invalid value"}
        };
    }
}