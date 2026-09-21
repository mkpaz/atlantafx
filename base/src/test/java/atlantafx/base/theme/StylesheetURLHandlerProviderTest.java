package atlantafx.base.theme;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.net.URLStreamHandler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

 @NullMarked
class StylesheetURLHandlerProviderTest {

    @Test
    void testCreateURLStreamHandler() {
        var provider = new StylesheetURLHandlerProvider();

        URLStreamHandler handler = provider.createURLStreamHandler(StylesheetURLHandler.SCHEME);
        assertNotNull(handler, "Handler should not be null for supported scheme");

        URLStreamHandler httpHandler = provider.createURLStreamHandler("http");
        assertNull(httpHandler, "Handler should be null for unsupported scheme");
    }
}