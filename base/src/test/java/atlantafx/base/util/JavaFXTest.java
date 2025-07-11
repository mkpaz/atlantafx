/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import javafx.application.Platform;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JavaFXTest implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        try {
            Platform.startup(() -> {
            });
        } catch (Throwable t) {
            // https://bugs.openjdk.org/browse/JDK-8090933 (2013!)
        }
    }
}
