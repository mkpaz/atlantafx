/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import org.jspecify.annotations.Nullable;

import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;

/**
 * A {@link URLStreamHandlerProvider} service implementation that registers
 * {@link StylesheetURLHandler} for the custom {@value StylesheetURLHandler#SCHEME} protocol.
 */
public final class StylesheetURLHandlerProvider extends URLStreamHandlerProvider {

    @Override
    public @Nullable URLStreamHandler createURLStreamHandler(String protocol) {
        if (StylesheetURLHandler.SCHEME.equalsIgnoreCase(protocol)) {
            return new StylesheetURLHandler();
        }
        return null;
    }
}