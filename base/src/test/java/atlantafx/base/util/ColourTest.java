package atlantafx.base.util;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static atlantafx.base.util.Colour.*;
import static org.junit.jupiter.api.Assertions.*;

class ColourTest {

    @Nested
    class ObservabilityTest {

        private Colour colour;

        @BeforeEach
        void setUp() {
            colour = new Colour(0.5, 0.5, 0.5, 1.0);
        }

        @Test
        @DisplayName("should not allocate listener lists on instantiation")
        void testLazyInitialization() {
            assertFalse(colour.isInvalidationListAllocated(), "Invalidation list should remain unallocated initially");
            assertFalse(colour.isChangeListAllocated(), "Change list should remain unallocated initially");
        }

        @Test
        @DisplayName("should allocate list on first addListener call")
        void testAddListenerAllocatesMemory() {
            colour.addListener(_ -> { });
            assertTrue(colour.isInvalidationListAllocated(), "Invalidation list should be allocated");
            assertFalse(colour.isChangeListAllocated(), "Change list should remain unallocated");

            colour.addListener((_, _, _) -> { });
            assertTrue(colour.isChangeListAllocated(), "Change list should be allocated");

            assertTrue(colour.hasInvalidationListeners());
            assertTrue(colour.hasChangeListeners());
        }

        @Test
        @DisplayName("should notify listeners with correct old and new state")
        void testNotificationFiredOnChange() {
            var invalidationCalls = new AtomicInteger(0);
            var changeCalls = new AtomicInteger(0);

            colour.addListener(_ -> invalidationCalls.incrementAndGet());
            colour.addListener((obs, oldVal, newVal) -> {
                changeCalls.incrementAndGet();
                assertSame(colour, obs, "Observable should be the colour instance");
                assertSame(colour, newVal, "New value should be the current colour instance");
                assertNotSame(colour, oldVal, "Old value should be a separate instance");

                assertEquals(0.5, oldVal.getRed(), 0.0001);
                assertEquals(0.8, newVal.getRed(), 0.0001);
            });

            colour.setRed(0.8);

            assertEquals(1, invalidationCalls.get(), "InvalidationListener should be triggered once");
            assertEquals(1, changeCalls.get(), "ChangeListener should be triggered once");
            assertEquals(0.8, colour.getRed(), 0.0001);
        }

        @Test
        @DisplayName("should reuse cached old state instance across mutations")
        void testOldStateInstanceReuse() {
            var values = new ArrayList<Colour>();
            colour.addListener((_, oldVal, _) -> values.add(oldVal));

            colour.setRed(0.8);
            colour.setGreen(0.2);

            assertEquals(2, values.size());
            assertSame(values.get(0), values.get(1), "Old value reference must be reused");
            assertEquals(0.8, values.get(1).getRed(), 0.0001, "Old value in 2nd event must hold red value before 2nd mutation");
            assertEquals(0.5, values.get(1).getGreen(), 0.0001, "Old value in 2nd event must hold green value before 2nd mutation");
        }

        @Test
        @DisplayName("should stop notifying after listener removal")
        void testRemoveListener() {
            var calls = new AtomicInteger(0);
            InvalidationListener listener = _ -> calls.incrementAndGet();

            colour.addListener(listener);
            colour.setRed(0.9);
            assertEquals(1, calls.get());

            colour.removeListener(listener);
            colour.setRed(0.2);

            assertEquals(1, calls.get(), "Removed listener should not receive notifications");
            assertFalse(colour.hasInvalidationListeners(), "Invalidation list should be empty");
        }

        @Test
        @DisplayName("should not notify when value remains unchanged")
        void testNoNotificationWhenValueIsSame() {
            var calls = new AtomicInteger(0);
            colour.addListener(_ -> calls.incrementAndGet());

            colour.setRed(colour.getRed());
            assertEquals(0, calls.get(), "Listener should not be called if state did not change");
        }

        @Test
        @DisplayName("should handle null listeners")
        void testNullListenersHandling() {
            assertDoesNotThrow(() -> {
                colour.addListener((InvalidationListener) null);
                colour.addListener((ChangeListener<Colour>) null);
                colour.removeListener((InvalidationListener) null);
                colour.removeListener((ChangeListener<Colour>) null);
            });

            assertFalse(colour.isInvalidationListAllocated());
            assertFalse(colour.isChangeListAllocated());
        }

        @MethodSource("provideSetters")
        @ParameterizedTest(name = "setter #{index} should trigger notification")
        @DisplayName("should notify listener")
        void testSettersTriggerNotification(Consumer<Colour> setter) {
            var calls = new AtomicInteger(0);
            colour.addListener(_ -> calls.incrementAndGet());

            setter.accept(colour);

            assertEquals(1, calls.get(), "Listener should be notified when setter modifies state");
        }

        static Stream<Consumer<Colour>> provideSetters() {
            return Stream.of(
                c -> c.setRed(0.8),
                c -> c.setRedInt(200),
                c -> c.setGreen(0.8),
                c -> c.setGreenInt(200),
                c -> c.setBlue(0.8),
                c -> c.setBlueInt(200),
                c -> c.setAlpha(128),
                c -> c.setOpacity(0.5),
                c -> c.setRGB(0.1, 0.2, 0.3),
                c -> c.setRGB(10, 20, 30)
            );
        }
    }

    //region PARAMETERIZED

    record ColorTestCase(
        Colour colour,
        int colorInt,
        int colorIntAlpha,
        String hex,
        String compactHex,
        String hexAlpha,
        String compactHexAlpha,
        int red,
        int green,
        int blue,
        int alpha,
        double opacity,
        float hue,
        float saturation,
        float lightness,
        Boolean expectedOpaque
    ) {
        public ColorTestCase(Colour colour, int colorInt, String hex, String compactHex,
                             int red, int green, int blue, float hue, float saturation, float lightness) {
            this(colour, colorInt, colorInt, hex, compactHex, hex + "FF",
                compactHex.length() <= 4 ? compactHex + "F" : compactHex + "FF",
                red, green, blue, 255, 1.0, hue, saturation, lightness, null);
        }

        public ColorTestCase(Colour colour, int colorInt, int colorIntAlpha, String hex, String compactHex,
                             String hexAlpha, String compactHexAlpha, int red, int green, int blue,
                             int alpha, float hue, float saturation, float lightness) {
            this(colour, colorInt, colorIntAlpha, hex, compactHex, hexAlpha, compactHexAlpha,
                red, green, blue, alpha, alpha / 255.0, hue, saturation, lightness, null);
        }

        public ColorTestCase(Colour colour, int colorInt, String hex, String compactHex,
                             int red, int green, int blue, float hue, float saturation, float lightness,
                             boolean expectedOpaque) {
            this(colour, colorInt, colorInt, hex, compactHex, hex + "FF",
                compactHex.length() <= 4 ? compactHex + "F" : compactHex + "FF",
                red, green, blue, 255, 1.0, hue, saturation, lightness, expectedOpaque);
        }
    }

    @ParameterizedTest
    @MethodSource("provideColorTestCases")
    void testColorValidation(ColorTestCase testCase) {
        Colour color = testCase.colour();
        Colour opaqueColor = color.copy();
        opaqueColor.setOpacity(1.0);

        assertEquals(testCase.hex(), opaqueColor.toHex());
        assertEquals(testCase.compactHex(), opaqueColor.toCompactHex());
        assertEquals(testCase.hexAlpha(), color.toHex(true));
        assertEquals(testCase.compactHexAlpha(), color.toCompactHex(true));

        assertEquals(testCase.colorInt(), color.toRGB().toInt(false));
        assertEquals(testCase.colorIntAlpha(), color.toRGB().toInt(true));

        assertEquals(testCase.red(), color.getRedInt());
        assertEquals(testCase.green(), color.getGreenInt());
        assertEquals(testCase.blue(), color.getBlueInt());

        assertEquals(testCase.opacity(), color.getOpacity(), 0.02);
        assertEquals(testCase.alpha(), color.getAlpha());

        HSL hsl = color.toHSL();
        assertEquals(testCase.hue(), hsl.hue(), 0.5);
        assertEquals(testCase.saturation(), hsl.saturation(), 0.01);
        assertEquals(testCase.lightness(), hsl.lightness(), 0.01);

        if (testCase.expectedOpaque() != null) {
            assertEquals(testCase.expectedOpaque(), color.isOpaque());
        }
    }

    private static Stream<ColorTestCase> provideColorTestCases() {
        return Stream.of(
            // --- Base tests with isOpaque checks ---
            new ColorTestCase(rgb(0, 0, 0), 0x000000FF, "#000000", "#000", 0, 0, 0, 0.0f, 0.0f, 0.0f),
            new ColorTestCase(rgb(64, 0, 0), 0x400000FF, "#400000", "#400000", 64, 0, 0, 0.0f, 1.0f, 0.13f),
            new ColorTestCase(rgb(128, 0, 0), 0x800000FF, "#800000", "#800000", 128, 0, 0, 0.0f, 1.0f, 0.25f),
            new ColorTestCase(rgb(255, 0, 0), 0xFF0000FF, "#FF0000", "#F00", 255, 0, 0, 0.0f, 1.0f, 0.5f, true),

            new ColorTestCase(rgb(255, 64, 0), 0xFF4000FF, "#FF4000", "#FF4000", 255, 64, 0, 15.1f, 1.0f, 0.5f),
            new ColorTestCase(rgb(255, 128, 0), 0xFF8000FF, "#FF8000", "#FF8000", 255, 128, 0, 30.1f, 1.0f, 0.5f),
            new ColorTestCase(rgb(255, 255, 0), 0xFFFF00FF, "#FFFF00", "#FF0", 255, 255, 0, 60.0f, 1.0f, 0.5f, true),

            new ColorTestCase(rgb(255, 255, 64), 0xFFFF40FF, "#FFFF40", "#FFFF40", 255, 255, 64, 60.0f, 1.0f, 0.63f),
            new ColorTestCase(rgb(1.0, 1.0, 0.5), 0xFFFF80FF, "#FFFF80", "#FFFF80", 255, 255, 128, 60.0f, 1.0f, 0.75f),
            new ColorTestCase(rgb(255, 255, 255), 0xFFFFFFFF, "#FFFFFF", "#FFF", 255, 255, 255, 0.0f, 0.0f, 1.0f, true),

            new ColorTestCase(rgb(255, 255, 255, 64.0 / 255), 0xFFFFFFFF, 0xFFFFFF40, "#FFFFFF", "#FFF", "#FFFFFF40", "#FFFFFF40", 255, 255, 255, 64, 64 / 255.0, 0.0f, 0.0f, 1.0f, false),
            new ColorTestCase(rgb(1.0, 1.0, 1.0, 0.5), 0xFFFFFFFF, 0xFFFFFF80, "#FFFFFF", "#FFF", "#FFFFFF80", "#FFFFFF80", 255, 255, 255, 128, 0.5, 0.0f, 0.0f, 1.0f, false),
            new ColorTestCase(rgb(255, 255, 255), 0xFFFFFFFF, 0xFFFFFFFF, "#FFFFFF", "#FFF", "#FFFFFFFF", "#FFFF", 255, 255, 255, 255, 1.0, 0.0f, 0.0f, 1.0f, true),

            // --- HEX Parsing ---
            new ColorTestCase(of("#000000"), 0x000000FF, "#000000", "#000", 0, 0, 0, 0.0f, 0.0f, 0.0f),
            new ColorTestCase(of("#0000FF"), 0x0000FFFF, "#0000FF", "#00F", 0, 0, 255, 240.0f, 1.0f, 0.5f),
            new ColorTestCase(of("#A52A2A"), 0xA52A2AFF, "#A52A2A", "#A52A2A", 165, 42, 42, 0.0f, 0.59f, 0.41f),
            new ColorTestCase(of("#00FFFF"), 0x00FFFFFF, "#00FFFF", "#0FF", 0, 255, 255, 180.0f, 1.0f, 0.5f),
            new ColorTestCase(of("#444444"), 0x444444FF, "#444444", "#444", 68, 68, 68, 0.0f, 0.0f, 0.27f),
            new ColorTestCase(of("#888888"), 0x888888FF, "#888888", "#888", 136, 136, 136, 0.0f, 0.0f, 0.53f),
            new ColorTestCase(of("#00FF00"), 0x00FF00FF, "#00FF00", "#0F0", 0, 255, 0, 120.0f, 1.0f, 0.5f),
            new ColorTestCase(of("#CCCCCC"), 0xCCCCCCFF, "#CCCCCC", "#CCC", 204, 204, 204, 0.0f, 0.0f, 0.8f),
            new ColorTestCase(of("#FF00FF"), 0xFF00FFFF, "#FF00FF", "#F0F", 255, 0, 255, 300.0f, 1.0f, 0.5f),
            new ColorTestCase(of("#FFA500"), 0xFFA500FF, "#FFA500", "#FFA500", 255, 165, 0, 39.0f, 1.0f, 0.5f),
            new ColorTestCase(of("#FFC0CB"), 0xFFC0CBFF, "#FFC0CB", "#FFC0CB", 255, 192, 203, 350.0f, 1.0f, 0.88f),
            new ColorTestCase(of("#800080"), 0x800080FF, "#800080", "#800080", 128, 0, 128, 300.0f, 1.0f, 0.25f),
            new ColorTestCase(of("#FF0000"), 0xFF0000FF, "#FF0000", "#F00", 255, 0, 0, 0.0f, 1.0f, 0.5f),
            new ColorTestCase(of("#EE82EE"), 0xEE82EEFF, "#EE82EE", "#EE82EE", 238, 130, 238, 300.0f, 0.76f, 0.72f),
            new ColorTestCase(of("#FFFFFF"), 0xFFFFFFFF, "#FFFFFF", "#FFF", 255, 255, 255, 0.0f, 0.0f, 1.0f),
            new ColorTestCase(of("#FFFF00"), 0xFFFF00FF, "#FFFF00", "#FF0", 255, 255, 0, 60.0f, 1.0f, 0.5f),

            new ColorTestCase(of("#000000", 0.5f), 0x000000FF, 0x00000080, "#000000", "#000", "#00000080", "#00000080", 0, 0, 0, 128, 0.5, 0.0f, 0.0f, 0.0f, null),
            new ColorTestCase(of("#FFA500", 0.25f), 0xFFA500FF, 0xFFA50040, "#FFA500", "#FFA500", "#FFA50040", "#FFA50040", 255, 165, 0, 64, 0.25, 39.0f, 1.0f, 0.5f, null),
            new ColorTestCase(of("#FFFF00", 0.85f), 0xFFFF00FF, 0xFFFF00D9, "#FFFF00", "#FF0", "#FFFF00D9", "#FFFF00D9", 255, 255, 0, 217, 0.85, 60.0f, 1.0f, 0.5f, null),

            new ColorTestCase(of("#00000080"), 0x000000FF, 0x00000080, "#000000", "#000", "#00000080", "#00000080", 0, 0, 0, 128, 0.0f, 0.0f, 0.0f),
            new ColorTestCase(of("#FFA50040"), 0xFFA500FF, 0xFFA50040, "#FFA500", "#FFA500", "#FFA50040", "#FFA50040", 255, 165, 0, 64, 39.0f, 1.0f, 0.5f),
            new ColorTestCase(of("#FFFF00D9"), 0xFFFF00FF, 0xFFFF00D9, "#FFFF00", "#FF0", "#FFFF00D9", "#FFFF00D9", 255, 255, 0, 217, 60.0f, 1.0f, 0.5f),

            // --- Int RGB / Alpha Int ---
            new ColorTestCase(rgb(0, 0, 0, 255), 0x000000FF, "#000000", "#000", 0, 0, 0, 0.0f, 0.0f, 0.0f),
            new ColorTestCase(rgb(0, 0, 255, 255), 0x0000FFFF, 0x0000FFFF, "#0000FF", "#00F", "#0000FFFF", "#00FF", 0, 0, 255, 255, 240.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(165, 42, 42, 255), 0xA52A2AFF, 0xA52A2AFF, "#A52A2A", "#A52A2A", "#A52A2AFF", "#A52A2AFF", 165, 42, 42, 255, 0.0f, 0.59f, 0.41f),
            new ColorTestCase(rgb(0, 255, 255, 255), 0x00FFFFFF, 0x00FFFFFF, "#00FFFF", "#0FF", "#00FFFFFF", "#0FFF", 0, 255, 255, 255, 180.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(68, 68, 68, 255), 0x444444FF, 0x444444FF, "#444444", "#444", "#444444FF", "#444F", 68, 68, 68, 255, 0.0f, 0.0f, 0.27f),
            new ColorTestCase(rgb(136, 136, 136, 255), 0x888888FF, 0x888888FF, "#888888", "#888", "#888888FF", "#888F", 136, 136, 136, 255, 0.0f, 0.0f, 0.53f),
            new ColorTestCase(rgb(0, 255, 0, 255), 0x00FF00FF, 0x00FF00FF, "#00FF00", "#0F0", "#00FF00FF", "#0F0F", 0, 255, 0, 255, 120.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(204, 204, 204, 255), 0xCCCCCCFF, 0xCCCCCCFF, "#CCCCCC", "#CCC", "#CCCCCCFF", "#CCCF", 204, 204, 204, 255, 0.0f, 0.0f, 0.8f),
            new ColorTestCase(rgb(255, 0, 255, 255), 0xFF00FFFF, 0xFF00FFFF, "#FF00FF", "#F0F", "#FF00FFFF", "#F0FF", 255, 0, 255, 255, 300.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(255, 165, 0, 255), 0xFFA500FF, 0xFFA500FF, "#FFA500", "#FFA500", "#FFA500FF", "#FFA500FF", 255, 165, 0, 255, 39.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(255, 192, 203, 255), 0xFFC0CBFF, 0xFFC0CBFF, "#FFC0CB", "#FFC0CB", "#FFC0CBFF", "#FFC0CBFF", 255, 192, 203, 255, 350.0f, 1.0f, 0.88f),
            new ColorTestCase(rgb(128, 0, 128, 255), 0x800080FF, 0x800080FF, "#800080", "#800080", "#800080FF", "#800080FF", 128, 0, 128, 255, 300.0f, 1.0f, 0.25f),
            new ColorTestCase(rgb(255, 0, 0, 255), 0xFF0000FF, 0xFF0000FF, "#FF0000", "#F00", "#FF0000FF", "#F00F", 255, 0, 0, 255, 0.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(238, 130, 238, 255), 0xEE82EEFF, 0xEE82EEFF, "#EE82EE", "#EE82EE", "#EE82EEFF", "#EE82EEFF", 238, 130, 238, 255, 300.0f, 0.76f, 0.72f),
            new ColorTestCase(rgb(255, 255, 255, 255), 0xFFFFFFFF, 0xFFFFFFFF, "#FFFFFF", "#FFF", "#FFFFFFFF", "#FFFF", 255, 255, 255, 255, 0.0f, 0.0f, 1.0f),
            new ColorTestCase(rgb(255, 255, 0, 255), 0xFFFF00FF, 0xFFFF00FF, "#FFFF00", "#FF0", "#FFFF00FF", "#FF0F", 255, 255, 0, 255, 60.0f, 1.0f, 0.5f),

            new ColorTestCase(rgb(0, 0, 0, 1), 0x000000FF, 0x00000001, "#000000", "#000", "#00000001", "#00000001", 0, 0, 0, 1, 0.00392156862, 0.0f, 0.0f, 0.0f, null), new ColorTestCase(rgb(255, 255, 255, 127), 0xFFFFFFFF, 0xFFFFFF7F, "#FFFFFF", "#FFF", "#FFFFFF7F", "#FFFFFF7F", 255, 255, 255, 127, 0.0f, 0.0f, 1.0f),
            new ColorTestCase(rgb(0, 0, 0, 128), 0x000000FF, 0x00000080, "#000000", "#000", "#00000080", "#00000080", 0, 0, 0, 128, 0.0f, 0.0f, 0.0f),

            // --- RGB Division/Double opacity ---
            new ColorTestCase(rgb(0, 0, 0, 128.0 / 255), 0x000000FF, 0x00000080, "#000000", "#000", "#00000080", "#00000080", 0, 0, 0, 128, 0.0f, 0.0f, 0.0f),
            new ColorTestCase(rgb(255, 165, 0, 64.0 / 255), 0xFFA500FF, 0xFFA50040, "#FFA500", "#FFA500", "#FFA50040", "#FFA50040", 255, 165, 0, 64, 39.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(255, 255, 0, 217.0 / 255), 0xFFFF00FF, 0xFFFF00D9, "#FFFF00", "#FF0", "#FFFF00D9", "#FFFF00D9", 255, 255, 0, 217, 60.0f, 1.0f, 0.5f),

            new ColorTestCase(rgb(0, 0, 0, 0.5), 0x000000FF, 0x00000080, "#000000", "#000", "#00000080", "#00000080", 0, 0, 0, 128, 0.5, 0.0f, 0.0f, 0.0f, null),
            new ColorTestCase(rgb(255, 165, 0, 0.25), 0xFFA500FF, 0xFFA50040, "#FFA500", "#FFA500", "#FFA50040", "#FFA50040", 255, 165, 0, 64, 0.25, 39.0f, 1.0f, 0.5f, null),
            new ColorTestCase(rgb(255, 255, 0, 0.85), 0xFFFF00FF, 0xFFFF00D9, "#FFFF00", "#FF0", "#FFFF00D9", "#FFFF00D9", 255, 255, 0, 217, 0.85, 60.0f, 1.0f, 0.5f, null),

            // --- Arithmetic / Normalized RGB ---
            new ColorTestCase(rgb(0.0, 0.0, 0.0), 0x000000FF, "#000000", "#000", 0, 0, 0, 0.0f, 0.0f, 0.0f),
            new ColorTestCase(rgb(0.0, 0.0, 1.0), 0x0000FFFF, "#0000FF", "#00F", 0, 0, 255, 240.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(0.64705882352, 0.16470588235, 0.16470588235), 0xA52A2AFF, "#A52A2A", "#A52A2A", 165, 42, 42, 0.0f, 0.59f, 0.41f),
            new ColorTestCase(rgb(0.0, 1.0, 1.0), 0x00FFFFFF, "#00FFFF", "#0FF", 0, 255, 255, 180.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(0.26666666666, 0.26666666666, 0.26666666666), 0x444444FF, "#444444", "#444", 68, 68, 68, 0.0f, 0.0f, 0.27f),
            new ColorTestCase(rgb(0.53333333333, 0.53333333333, 0.53333333333), 0x888888FF, "#888888", "#888", 136, 136, 136, 0.0f, 0.0f, 0.53f),
            new ColorTestCase(rgb(0.0, 1.0, 0.0), 0x00FF00FF, "#00FF00", "#0F0", 0, 255, 0, 120.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(0.8, 0.8, 0.8), 0xCCCCCCFF, "#CCCCCC", "#CCC", 204, 204, 204, 0.0f, 0.0f, 0.8f),
            new ColorTestCase(rgb(1.0, 0.0, 1.0), 0xFF00FFFF, "#FF00FF", "#F0F", 255, 0, 255, 300.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(1.0, 0.64705882352, 0.0), 0xFFA500FF, "#FFA500", "#FFA500", 255, 165, 0, 39.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(1.0, 0.75294117647, 0.79607843137), 0xFFC0CBFF, "#FFC0CB", "#FFC0CB", 255, 192, 203, 350.0f, 1.0f, 0.88f),
            new ColorTestCase(rgb(0.50196078431, 0.0, 0.50196078431), 0x800080FF, "#800080", "#800080", 128, 0, 128, 300.0f, 1.0f, 0.25f),
            new ColorTestCase(rgb(1.0, 0.0, 0.0), 0xFF0000FF, "#FF0000", "#F00", 255, 0, 0, 0.0f, 1.0f, 0.5f),
            new ColorTestCase(rgb(0.93333333333, 0.50980392156, 0.93333333333), 0xEE82EEFF, "#EE82EE", "#EE82EE", 238, 130, 238, 300.0f, 0.76f, 0.72f),
            new ColorTestCase(rgb(1.0, 1.0, 1.0), 0xFFFFFFFF, "#FFFFFF", "#FFF", 255, 255, 255, 0.0f, 0.0f, 1.0f),
            new ColorTestCase(rgb(1.0, 1.0, 0.0), 0xFFFF00FF, "#FFFF00", "#FF0", 255, 255, 0, 60.0f, 1.0f, 0.5f),

            new ColorTestCase(rgb(0.0, 0.0, 0.0, 0.50196078431), 0x000000FF, 0x00000080, "#000000", "#000", "#00000080", "#00000080", 0, 0, 0, 128, 0.5, 0.0f, 0.0f, 0.0f, null),
            new ColorTestCase(rgb(1.0, 0.64705882352, 0.0, 0.25098039215), 0xFFA500FF, 0xFFA50040, "#FFA500", "#FFA500", "#FFA50040", "#FFA50040", 255, 165, 0, 64, 0.25, 39.0f, 1.0f, 0.5f, null),
            new ColorTestCase(rgb(1.0, 1.0, 0.0, 0.85098039215), 0xFFFF00FF, 0xFFFF00D9, "#FFFF00", "#FF0", "#FFFF00D9", "#FFFF00D9", 255, 255, 0, 217, 0.85, 60.0f, 1.0f, 0.5f, null),

            // --- HSL Constructors ---
            new ColorTestCase(hsl(0, 0, 0), 0x000000FF, "#000000", "#000", 0, 0, 0, 0.0f, 0.0f, 0.0f),
            new ColorTestCase(hsl(240, 1, 0.5), 0x0000FFFF, "#0000FF", "#00F", 0, 0, 255, 240.0f, 1.0f, 0.5f),
            new ColorTestCase(hsl(0, 0.59420294f, 0.40588236f), 0xA52A2AFF, "#A52A2A", "#A52A2A", 165, 42, 42, 0.0f, 0.59f, 0.41f),
            new ColorTestCase(hsl(180, 1, 0.5), 0x00FFFFFF, "#00FFFF", "#0FF", 0, 255, 255, 180.0f, 1.0f, 0.5f),
            new ColorTestCase(hsl(0, 0, 0.26666668f), 0x444444FF, "#444444", "#444", 68, 68, 68, 0.0f, 0.0f, 0.27f),
            new ColorTestCase(hsl(0, 0, 0.53333336f), 0x888888FF, "#888888", "#888", 136, 136, 136, 0.0f, 0.0f, 0.53f),
            new ColorTestCase(hsl(120, 1, 0.5), 0x00FF00FF, "#00FF00", "#0F0", 0, 255, 0, 120.0f, 1.0f, 0.5f),
            new ColorTestCase(hsl(0, 0, 0.8), 0xCCCCCCFF, "#CCCCCC", "#CCC", 204, 204, 204, 0.0f, 0.0f, 0.8f),
            new ColorTestCase(hsl(300, 1, 0.5), 0xFF00FFFF, "#FF00FF", "#F0F", 255, 0, 255, 300.0f, 1.0f, 0.5f),
            new ColorTestCase(hsl(38.823532f, 1, 0.5), 0xFFA500FF, "#FFA500", "#FFA500", 255, 165, 0, 39.0f, 1.0f, 0.5f),
            new ColorTestCase(hsl(349.5238f, 1, 0.87647057f), 0xFFC0CBFF, "#FFC0CB", "#FFC0CB", 255, 192, 203, 350.0f, 1.0f, 0.88f),
            new ColorTestCase(hsl(300, 1, 0.2509804f), 0x800080FF, "#800080", "#800080", 128, 0, 128, 300.0f, 1.0f, 0.25f),
            new ColorTestCase(hsl(0, 1, 0.5), 0xFF0000FF, "#FF0000", "#F00", 255, 0, 0, 0.0f, 1.0f, 0.5f),
            new ColorTestCase(hsl(300, 0.76056343f, 0.72156864f), 0xEE82EEFF, "#EE82EE", "#EE82EE", 238, 130, 238, 300.0f, 0.76f, 0.72f),
            new ColorTestCase(hsl(0, 0, 1), 0xFFFFFFFF, "#FFFFFF", "#FFF", 255, 255, 255, 0.0f, 0.0f, 1.0f),
            new ColorTestCase(hsl(60, 1, 0.5), 0xFFFF00FF, "#FFFF00", "#FF0", 255, 255, 0, 60.0f, 1.0f, 0.5f),

            new ColorTestCase(hsl(0, 0, 0, 0.50196078431f), 0x000000FF, 0x00000080, "#000000", "#000", "#00000080", "#00000080", 0, 0, 0, 128, 0.5, 0.0f, 0.0f, 0.0f, null),
            new ColorTestCase(hsl(38.823532f, 1, 0.5, 0.25098039215f), 0xFFA500FF, 0xFFA50040, "#FFA500", "#FFA500", "#FFA50040", "#FFA50040", 255, 165, 0, 64, 0.25, 39.0f, 1.0f, 0.5f, null),
            new ColorTestCase(hsl(60, 1, 0.5, 0.85098039215f), 0xFFFF00FF, 0xFFFF00D9, "#FFFF00", "#FF0", "#FFFF00D9", "#FFFF00D9", 255, 255, 0, 217, 0.85, 60.0f, 1.0f, 0.5f, null)
        );
    }

    @Nested
    class HexValidationTest {

        @ParameterizedTest
        @ValueSource(strings = {
            "000000", "#000000", "00000000", "#00000000", "000", "#000", "0000", "#0000",
            "FFFFFF", "#FFFFFF", "FFFFFFFF", "#ffffffff", "FfF", "#fFf", "ffff", "#fFfF"
        })
        void testValidHex(String hex) {
            assertTrue(RGB.isValidHex(hex));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "", "00000", "0000000", "#00000", "#0000000", "000000000", "#000000000",
            "00", "#00", "FFFFF", "FFFFFFF", "#FFFFF", "#FFFFFFF", "FFFFFFFFF", "#FFFFFFFFF",
            "FF", "#FF", "G00000", "#00000H", "000i0000", "#0000J000", "00K", "#0l0",
            "0M00", "#n000", "FFGFFF", "#FFFHFF", "iFFFFFFF", "#FFFFFFFj", "FFK", "#LFF", "FFmF", "#FnFF"
        })
        void testInvalidHex(String hex) {
            assertFalse(RGB.isValidHex(hex));
        }
    }

    @Nested
    class HexUtilityTest {

        @ParameterizedTest
        @CsvSource({
            "10a0d1, 10a0d1",
            "#10a0d1, #10a0d1",
            "0D0A0B0C, 0D0A0B0C",
            "#0D0a0B0c, #0D0a0B0c",
            "11aadd, 1ad",
            "#11aADd, #1aD",
            "DDAABBCC, DABC",
            "#dDAabBCc, #dAbC"
        })
        void testCompressHex(String input, String expected) {
            assertEquals(expected, RGB.compressHex(input));
        }

        @ParameterizedTest
        @CsvSource({
            "10a0d1, 10a0d1",
            "#10a0d1, #10a0d1",
            "0D0A0B0C, 0D0A0B0C",
            "#0D0a0B0c, #0D0a0B0c",
            "1ad, 11aadd",
            "#1aD, #11aaDD",
            "DABC, DDAABBCC",
            "#dAbC, #ddAAbbCC"
        })
        void testExpandHex(String input, String expected) {
            assertEquals(expected, RGB.expandHex(input));
        }
    }

    @Test
    void testHexLowerCaseFormatting() {
        assertEquals("#a0b0c0", rgb(0xA0, 0xB0, 0xC0).toHex().toLowerCase());
        assertEquals("#a0b0c0ff", rgb(0xA0, 0xB0, 0xC0, 0xFF).toHex(true).toLowerCase());
        assertEquals("#a0b0c0", rgb(0xA0, 0xB0, 0xC0).toCompactHex().toLowerCase());
        assertEquals("#abc", rgb(0xAA, 0xBB, 0xCC).toCompactHex().toLowerCase());
        assertEquals("#a0b0c0ff", rgb(0xA0, 0xB0, 0xC0, 0xFF).toCompactHex(true).toLowerCase());
        assertEquals("#abcf", rgb(0xAA, 0xBB, 0xCC, 0xFF).toCompactHex(true).toLowerCase());
        assertEquals("#a0b0c0d0", rgb(0xA0, 0xB0, 0xC0, 0xD0).toHex(true).toLowerCase());
        assertEquals("#a0b0c0d0", rgb(0xA0, 0xB0, 0xC0, 0xD0).toCompactHex(true).toLowerCase());
        assertEquals("#aabbccd0", rgb(0xAA, 0xBB, 0xCC, 0xD0).toCompactHex(true).toLowerCase());
        assertEquals("#abcd", rgb(0xAA, 0xBB, 0xCC, 0xDD).toCompactHex(true).toLowerCase());
    }

    @Nested
    class HslTest {

        record HslConversionCase(int r, int g, int b, int a, double expectedHue, double expectedSat,
                                 double expectedLight) { }

        @ParameterizedTest
        @MethodSource("provideHslFromRgbCases")
        void testHslFromRgb(HslConversionCase c) {
            HSL hsl = HSL.fromRGB(c.r(), c.g(), c.b(), c.a());
            assertEquals(c.expectedHue(), hsl.hue(), 1e-5);
            assertEquals(c.expectedSat(), hsl.saturation(), 1e-7);
            assertEquals(c.expectedLight(), hsl.lightness(), 1e-7);
        }

        private static Stream<HslConversionCase> provideHslFromRgbCases() {
            return Stream.of(
                new HslConversionCase(0, 0, 0, 0, 0.0, 0.0, 0.0),
                new HslConversionCase(255, 0, 0, 0, 0.0, 1.0, 0.5),
                new HslConversionCase(0, 255, 0, 0, 120.0, 1.0, 0.5),
                new HslConversionCase(0, 0, 255, 0, 240.0, 1.0, 0.5),
                new HslConversionCase(255, 255, 255, 0, 0.0, 0.0, 1.0),
                new HslConversionCase(200, 165, 10, 0, 48.94737, 0.9047619, 0.4117647),
                new HslConversionCase(52, 113, 82, 0, 149.50819, 0.36969696, 0.32352942)
            );
        }

        @Test
        void testHslToRgb() {
            RGB rgb1 = new HSL(48.94737f, 0.9047619f, 0.4117647f, 0).toRGB();
            assertEquals(200, rgb1.redInt());
            assertEquals(165, rgb1.greenInt());
            assertEquals(10, rgb1.blueInt());

            RGB rgb2 = new HSL(149.50821f, 0.36969694f, 0.32352942f, 0).toRGB();
            assertEquals(52, rgb2.redInt());
            assertEquals(113, rgb2.greenInt());
            assertEquals(82, rgb2.blueInt());
        }
    }
    //endregion

    //region FX_TEST_SUITE

    /*
     * Copyright (c) 2010, 2025, Oracle and/or its affiliates. All rights reserved.
     * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
     *
     * This code is free software; you can redistribute it and/or modify it
     * under the terms of the GNU General Public License version 2 only, as
     * published by the Free Software Foundation.  Oracle designates this
     * particular file as subject to the "Classpath" exception as provided
     * by Oracle in the LICENSE file that accompanied this code.
     *
     * This code is distributed in the hope that it will be useful, but WITHOUT
     * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
     * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
     * version 2 for more details (a copy is included in the LICENSE file that
     * accompanied this code).
     *
     * You should have received a copy of the GNU General Public License version
     * 2 along with this work; if not, write to the Free Software Foundation,
     * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
     *
     * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
     * or visit www.oracle.com if you need additional information or have any
     * questions.
     */
    @Nested
    class FXTestSuite {

        @Test
        public void testRedIsBoundedBy0And1() {
            try {
                new Colour(-1.0, 0.0, 0.0, 0.0);
                fail();
            } catch (Exception _) {
            }

            try {
                new Colour(100.0, 0.0, 0.0, 0.0);
                fail();
            } catch (Exception _) {
            }
        }

        @Test
        public void testGreenIsBoundedBy0And1() {
            try {
                new Colour(0.0, -1.0, 0.0, 0.0);
                fail();
            } catch (Exception _) {
            }

            try {
                new Colour(0.0, 100.0, 0.0, 0.0);
                fail();
            } catch (Exception _) {
            }
        }

        @Test
        public void testBlueIsBoundedBy0And1() {
            try {
                new Colour(0.0, 0.0, -1, 0.0);
                fail();
            } catch (Exception _) {
            }

            try {
                new Colour(0.0, 0.0, 100.0, 0.0);
                fail();
            } catch (Exception _) {
            }
        }

        @Test
        public void testOpacityIsBoundedBy0And1() {
            try {
                new Colour(0.0, 0.0, 0.0, -1.0);
                fail();
            } catch (Exception _) {
            }

            try {
                new Colour(0.0, 0.0, 0.0, 100.0);
                fail();
            } catch (Exception _) {
            }
        }

        @Test
        public void testOfTheWay() {
            Colour start = new Colour(0.0, 0.0, 0.0, 0.0);
            Colour end = new Colour(1.0, 1.0, 1.0, 1.0);
            Colour mid = start.interpolate(end, .5);
            assertEquals(.5, mid.getRed(), 0.001);
            assertEquals(.5, mid.getGreen(), 0.001);
            assertEquals(.5, mid.getBlue(), 0.001);
            assertEquals(.5, mid.getOpacity(), 0.001);
        }

        @Test
        public void testOfTheWayAll() throws IllegalArgumentException, IllegalAccessException {
            var colors = new ArrayList<Colour>();
            for (Field f : Colour.class.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers()) &&
                    f.getType() == Colour.class) {
                    Colour c = (Colour) f.get(null);
                    colors.add(c);
                }
            }
            for (Colour c1 : colors) {
                for (Colour c2 : colors) {
                    c1.interpolate(c2, 0.0);
                    c1.interpolate(c2, Double.MIN_VALUE);
                    c1.interpolate(c2, 0.5);
                    c1.interpolate(c2, 1.0 - Double.MIN_VALUE);
                    c1.interpolate(c2, 1.0);
                }
            }
        }

        @Test
        public void testOfTheWayIndirect() {
            Colour start = new Colour(0.0, 0.0, 0.0, 0.0);
            Colour end = new Colour(1.0, 1.0, 1.0, 1.0);
            Colour mid = start.interpolate(end, .5);
            assertEquals(0.5, mid.getRed(), 0.001);
            assertEquals(0.5, mid.getGreen(), 0.001);
            assertEquals(0.5, mid.getBlue(), 0.001);
            assertEquals(0.5, mid.getOpacity(), 0.001);
        }

        @Test
        public void testColorIsBoundedBy0And1() {
            try {
                rgb(-1, 0, 0, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(0, -1, 0, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(0, 0, -1, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(0, 0, 0, -1);
                fail();
            } catch (Exception _) {
            }
        }

        @Test
        public void testColor() {
            Colour color = rgb(.1, .2, .3);
            assertEquals(.1, color.getRed(), 0.001);
            assertEquals(.2, color.getGreen(), 0.001);
            assertEquals(.3, color.getBlue(), 0.001);
            assertEquals(1, color.getOpacity(), 0.001);
        }

        @Test
        public void testColorWithOpacity() {
            Colour color = rgb(.1, .2, .3, .4);
            assertEquals(.1, color.getRed(), 0.001);
            assertEquals(.2, color.getGreen(), 0.001);
            assertEquals(.3, color.getBlue(), 0.001);
            assertEquals(.4, color.getOpacity(), 0.001);
        }

        @Test
        public void testRgbIsBoundedBy0And255() {
            try {
                rgb(-1, 0, 0, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(0, -1, 0, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(0, 0, -1, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(0, 0, 0, -1);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(300, 0, 0, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(0, 300, 0, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(0, 0, 300, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                rgb(0, 0, 0, 300);
                fail();
            } catch (Exception _) {
            }
        }

        @Test
        public void testRgb() {
            Colour color = rgb(255, 0, 255, 0);
            assertEquals(1, color.getRed());
            assertEquals(0, color.getGreen());
            assertEquals(1, color.getBlue());
            assertEquals(0, color.getOpacity());

            HSB hsb = HSB.fromRGB(1.0, 0.0, 1.0, 0.0);
            assertEquals(hsb.hue(), color.toHSB().hue(), 0.001);
            assertEquals(hsb.saturation(), color.toHSB().saturation(), 0.001);
            assertEquals(hsb.brightness(), color.toHSB().brightness(), 0.001);
        }

        @Test
        public void testHsbIsBounded() {
            try {
                hsb(100, -1, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                hsb(100, 0, -1);
                fail();
            } catch (Exception _) {
            }

            try {
                hsb(100, 2, 0);
                fail();
            } catch (Exception _) {
            }

            try {
                hsb(100, 0, 2);
                fail();
            } catch (Exception _) {
            }
        }

        @Test
        public void testHsb() {
            Colour color = hsb(210, 1, .5);
            assertEquals(0.0, color.getRed(), .001);
            assertEquals(0.25, color.getGreen(), .001);
            assertEquals(0.5, color.getBlue(), .001);
            assertEquals(1.0, color.getOpacity(), .001);
            HSB hsb = color.toHSB();
            assertEquals(210, hsb.hue(), 0.001);
            assertEquals(1.0, hsb.saturation(), 0.001);
            assertEquals(0.5, hsb.brightness(), 0.001);
        }

        @Test
        public void testHsbWithOpacity() {
            Colour color = hsb(210, 1, .5, .4);
            assertEquals(0.0, color.getRed(), .001);
            assertEquals(0.25, color.getGreen(), .001);
            assertEquals(0.5, color.getBlue(), .001);
            assertEquals(0.4, color.getOpacity(), .001);
            HSB hsb = color.toHSB();
            assertEquals(210, hsb.hue(), 0.001);
            assertEquals(1.0, hsb.saturation(), 0.001);
            assertEquals(0.5, hsb.brightness(), 0.001);
        }

        @Test
        public void testWebPoundNotation() {
            Colour color = of("#aabbcc");
            assertEquals(170.0 / 255.0, color.getRed(), 0.001);
            assertEquals(187.0 / 255.0, color.getGreen(), 0.001);
            assertEquals(204.0 / 255.0, color.getBlue(), 0.001);
        }

        @Test
        public void testWebPoundNotationShort() {
            Colour color = of("#abc");
            assertEquals(10.0 / 15.0, color.getRed(), 0.001);
            assertEquals(11.0 / 15.0, color.getGreen(), 0.001);
            assertEquals(12.0 / 15.0, color.getBlue(), 0.001);
        }

        @Test
        public void testWebPoundNotationWithAlphaAndOpacity() {
            Colour color = of("#aabbcc80", 0.5);
            assertEquals(170.0 / 255.0, color.getRed(), 0.001);
            assertEquals(187.0 / 255.0, color.getGreen(), 0.001);
            assertEquals(204.0 / 255.0, color.getBlue(), 0.001);
            assertEquals((128.0 / 255.0) / 2.0, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebPoundNotationIllegalValue() {
            assertThrows(IllegalArgumentException.class, () -> of("#aabbccddee"));
        }

        @Test
        public void testWebEmptyColor() {
            assertThrows(IllegalArgumentException.class, () -> of("", 0.5));
        }

        @Test
        public void testWebHexNotation() {
            Colour color = of("0xaabbcc");
            assertEquals(170.0 / 255.0, color.getRed(), 0.001);
            assertEquals(187.0 / 255.0, color.getGreen(), 0.001);
            assertEquals(204.0 / 255.0, color.getBlue(), 0.001);
        }

        @Test
        public void testWebHexNotationWithAlpha() {
            Colour color = of("0xaabbcc80");
            assertEquals(170.0 / 255.0, color.getRed(), 0.001);
            assertEquals(187.0 / 255.0, color.getGreen(), 0.001);
            assertEquals(204.0 / 255.0, color.getBlue(), 0.001);
            assertEquals(128.0 / 255.0, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebHexNotationIllegalValue() {
            assertThrows(IllegalArgumentException.class, () -> of("0xaabbccddee"));
        }

        @Test
        public void testWebNamedWrongName() {
            assertThrows(IllegalArgumentException.class, () -> of("foobar"));
        }

        @Test
        public void testWebHex0xNotation() {
            Colour color = of("0xaabbcc");
            assertEquals(170.0 / 255.0, color.getRed(), 0.001);
            assertEquals(187.0 / 255.0, color.getGreen(), 0.001);
            assertEquals(204.0 / 255.0, color.getBlue(), 0.001);
        }

        @Test
        public void testWebHex0xNotationShort() {
            Colour color = of("0xabc");
            assertEquals(10.0 / 15.0, color.getRed(), 0.001);
            assertEquals(11.0 / 15.0, color.getGreen(), 0.001);
            assertEquals(12.0 / 15.0, color.getBlue(), 0.001);
        }

        @Test
        public void testWebHexNoLeadingSymbol() {
            Colour color = of("aAbBcC");
            assertEquals(170.0 / 255.0, color.getRed(), 0.001);
            assertEquals(187.0 / 255.0, color.getGreen(), 0.001);
            assertEquals(204.0 / 255.0, color.getBlue(), 0.001);
        }

        @Test
        public void testWebHexNoLeadingSymbolShort() {
            Colour color = of("aBc");
            assertEquals(10.0 / 15.0, color.getRed(), 0.001);
            assertEquals(11.0 / 15.0, color.getGreen(), 0.001);
            assertEquals(12.0 / 15.0, color.getBlue(), 0.001);
        }

        @Test
        public void testWebHexNoLeadingSymbolShortWithAlpha() {
            Colour color = of("aBc9");
            assertEquals(10.0 / 15.0, color.getRed(), 0.001);
            assertEquals(11.0 / 15.0, color.getGreen(), 0.001);
            assertEquals(12.0 / 15.0, color.getBlue(), 0.001);
            assertEquals(0.6, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebRgb() {
            Colour color = of("rgb(128, 64, 192)");
            assertEquals(128.0 / 255.0, color.getRed(), 0.001);
            assertEquals(64.0 / 255.0, color.getGreen(), 0.001);
            assertEquals(192.0 / 255.0, color.getBlue(), 0.001);
            assertEquals(1.0, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebRgbRange() {
            Colour c1 = of("rgb(255, 0, 255)");
            Colour c2 = of("rgb(256, -1, 256)");
            assertEquals(c1, c2);
            Colour c3 = of("rgb(0, 255, 0)");
            Colour c4 = of("rgb(-1, 256, -1)");
            assertEquals(c3, c4);
        }

        @Test
        public void testWebRgba() {
            Colour color = of("rgba(128, 64, 192, 0.6)");
            assertEquals(128.0 / 255.0, color.getRed(), 0.001);
            assertEquals(64.0 / 255.0, color.getGreen(), 0.001);
            assertEquals(192.0 / 255.0, color.getBlue(), 0.001);
            assertEquals(0.6, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebRgbaRange() {
            Colour c1 = of("rgba(255, 0, 255, 1.0)");
            Colour c2 = of("rgba(256, -1, 256, 1.1)");
            assertEquals(c1, c2);
            Colour c3 = of("rgba(0, 255, 0, 1.0)");
            Colour c4 = of("rgba(-1, 256, -1, 1.1)");
            assertEquals(c3, c4);
            Colour c5 = of("rgba(0, 0, 0, 0.0)");
            Colour c6 = of("rgba(0, 0, 0, -1)");
            assertEquals(c5, c6);
        }

        @Test
        public void testWebRgbPercent() {
            Colour color = of("rgb(60%, 40%, 100%)");
            assertEquals(0.6, color.getRed(), 0.001);
            assertEquals(0.4, color.getGreen(), 0.001);
            assertEquals(1.0, color.getBlue(), 0.001);
            assertEquals(1.0, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebRgbPercentFloat() {
            Colour color = of("rgb(60.0%, 40.0%, 100.0%)");
            assertEquals(0.6, color.getRed(), 0.001);
            assertEquals(0.4, color.getGreen(), 0.001);
            assertEquals(1.0, color.getBlue(), 0.001);
            assertEquals(1.0, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebRgbPercentRange() {
            Colour c1 = of("rgb(100%,  0%, 100%)");
            Colour c2 = of("rgb(101%, -1%, 101%)");
            assertEquals(c1, c2);
            Colour c3 = of("rgb( 0%, 100%,  0%)");
            Colour c4 = of("rgb(-1%, 101%, -1%)");
            assertEquals(c3, c4);
        }

        @Test
        public void testWebRgbaPercent() {
            Colour color = of("rgba(60%, 40%, 100%, 0.6)");
            assertEquals(0.6, color.getRed(), 0.001);
            assertEquals(0.4, color.getGreen(), 0.001);
            assertEquals(1.0, color.getBlue(), 0.001);
            assertEquals(0.6, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebRgbaPercentFloat() {
            Colour color = of("rgba(60.0%, 40.0%, 100.0%, 0.6)");
            assertEquals(0.6, color.getRed(), 0.001);
            assertEquals(0.4, color.getGreen(), 0.001);
            assertEquals(1.0, color.getBlue(), 0.001);
            assertEquals(0.6, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebRgbaPercentRange() {
            Colour c1 = of("rgba(100%,  0%, 100%, 1.0)");
            Colour c2 = of("rgba(101%, -1%, 101%, 1.1)");
            assertEquals(c1, c2);
            Colour c3 = of("rgba( 0%, 100%,  0%, 1.0)");
            Colour c4 = of("rgba(-1%, 101%, -1%, 1.1)");
            assertEquals(c3, c4);
            Colour c5 = of("rgba(0%, 0%, 0%, 0.0)");
            Colour c6 = of("rgba(0%, 0%, 0%, -1)");
            assertEquals(c5, c6);
        }

        @Test
        public void testWebRgbPercentMix() {
            Colour color = of("rgb(60%, 40.0%, 192)");
            assertEquals(0.6, color.getRed(), 0.001);
            assertEquals(0.4, color.getGreen(), 0.001);
            assertEquals(192.0 / 255.0, color.getBlue(), 0.001);
            assertEquals(1.0, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebRgbaPercentMix() {
            Colour color = of("rgba(60%, 40.0%, 192, 0.6)");
            assertEquals(0.6, color.getRed(), 0.001);
            assertEquals(0.4, color.getGreen(), 0.001);
            assertEquals(192.0 / 255.0, color.getBlue(), 0.001);
            assertEquals(0.6, color.getOpacity(), 0.001);
        }

        @Test
        public void testWebHsl() {
            Colour color = of("hsl(180, 50%, 100%)");
            Colour ref = hsb(180, 0.5, 1.0);
            assertEquals(ref.getRed(), color.getRed(), 0.001);
            assertEquals(ref.getGreen(), color.getGreen(), 0.001);
            assertEquals(ref.getBlue(), color.getBlue(), 0.001);
            assertEquals(ref.getOpacity(), color.getOpacity(), 0.001);
        }

        @Test
        public void testWebHslFloat() {
            Colour color = of("hsl(180.0, 50.0%, 100.0%)");
            Colour ref = hsb(180, 0.5, 1.0);
            assertEquals(ref.getRed(), color.getRed(), 0.001);
            assertEquals(ref.getGreen(), color.getGreen(), 0.001);
            assertEquals(ref.getBlue(), color.getBlue(), 0.001);
            assertEquals(ref.getOpacity(), color.getOpacity(), 0.001);
        }

        @Test
        public void testWebHslRange() {
            Colour c1 = of("hsl( 240,  0%, 100%)");
            Colour c2 = of("hsl(-120, -1%, 101%)");
            assertEquals(c1, c2);
            Colour c3 = of("hsl(240, 100%,  0%)");
            Colour c4 = of("hsl(600, 101%, -1%)");
            assertEquals(c3, c4);
        }

        @Test
        public void testWebHsla() {
            Colour color = of("hsla(180, 50%, 100%, 0.6)");
            Colour ref = hsb(180, 0.5, 1.0, 0.6);
            assertEquals(ref.getRed(), color.getRed(), 0.001);
            assertEquals(ref.getGreen(), color.getGreen(), 0.001);
            assertEquals(ref.getBlue(), color.getBlue(), 0.001);
            assertEquals(ref.getOpacity(), color.getOpacity(), 0.001);
        }

        @Test
        public void testWebHslaFloat() {
            Colour color = of("hsla(180.0, 50.0%, 100.0%, 0.6)");
            Colour ref = hsb(180, 0.5, 1.0, 0.6);
            assertEquals(ref.getRed(), color.getRed(), 0.001);
            assertEquals(ref.getGreen(), color.getGreen(), 0.001);
            assertEquals(ref.getBlue(), color.getBlue(), 0.001);
            assertEquals(ref.getOpacity(), color.getOpacity(), 0.001);
        }

        @Test
        public void testWebHslaRange() {
            Colour c1 = of("hsla( 240,  0%, 100%, 1.0)");
            Colour c2 = of("hsla(-120, -1%, 101%, 1.1)");
            assertEquals(c1, c2);
            Colour c3 = of("hsla(240, 100%,  0%, 1.0)");
            Colour c4 = of("hsla(600, 101%, -1%, 1.1)");
            assertEquals(c3, c4);
            Colour c5 = of("hsla(240, 0%, 0%, 0.0)");
            Colour c6 = of("hsla(240, 0%, 0%, -1)");
            assertEquals(c5, c6);
        }

        @Test
        public void testWebRGBParam() {
            assertThrows(IllegalArgumentException.class, () -> of("rgb(100, 100)"));
        }

        @Test
        public void testWebRgb1Param() {
            assertThrows(IllegalArgumentException.class, () -> of("rgb(100)"));
        }

        @Test
        public void testWebRgb0Param() {
            assertThrows(IllegalArgumentException.class, () -> of("rgb()"));
        }

        @Test
        public void testWebRgbNoParen() {
            assertThrows(IllegalArgumentException.class, () -> of("rgb 100, 100, 100"));
        }

        @Test
        public void testWebRgbNoCloseParen() {
            assertThrows(IllegalArgumentException.class, () -> of("rgb(100, 100, 100"));
        }

        @Test
        public void testWebRgba3Param() {
            assertThrows(IllegalArgumentException.class, () -> of("rgba(100, 100, 100)"));
        }

        @Test
        public void testWebRgba2Param() {
            assertThrows(IllegalArgumentException.class, () -> of("rgba(100, 100)"));
        }

        @Test
        public void testWebRgba1Param() {
            assertThrows(IllegalArgumentException.class, () -> of("rgba(100)"));
        }

        @Test
        public void testWebRgba0Param() {
            assertThrows(IllegalArgumentException.class, () -> of("rgba()"));
        }

        @Test
        public void testWebRgbaNoParen() {
            assertThrows(IllegalArgumentException.class, () -> of("rgba 100, 100, 100"));
        }

        @Test
        public void testWebRgbaNoCloseParen() {
            assertThrows(IllegalArgumentException.class, () -> of("rgba(100, 100, 100, 0.5"));
        }

        @Test
        public void testWebHsl2Param() {
            assertThrows(IllegalArgumentException.class, () -> of("hsl(240, 50%)"));
        }

        @Test
        public void testWebHsl1Param() {
            assertThrows(IllegalArgumentException.class, () -> of("hsl(240)"));
        }

        @Test
        public void testWebHsl0Param() {
            assertThrows(IllegalArgumentException.class, () -> of("hsl()"));
        }

        @Test
        public void testWebHslNoParen() {
            assertThrows(IllegalArgumentException.class, () -> of("hsl 240, 50%, 50%"));
        }

        @Test
        public void testWebHslNoCloseParen() {
            assertThrows(IllegalArgumentException.class, () -> of("hsl(240, 50%, 50%"));
        }

        @Test
        public void testWebHsla3Param() {
            assertThrows(IllegalArgumentException.class, () -> of("hsla(240, 50%, 50%)"));
        }

        @Test
        public void testWebHsla2Param() {
            assertThrows(IllegalArgumentException.class, () -> of("hsla(240, 50%)"));
        }

        @Test
        public void testWebHsla1Param() {
            assertThrows(IllegalArgumentException.class, () -> of("hsla(240)"));
        }

        @Test
        public void testWebHsla0Param() {
            assertThrows(IllegalArgumentException.class, () -> of("hsla()"));
        }

        @Test
        public void testWebHslaNoParen() {
            assertThrows(IllegalArgumentException.class, () -> of("hsla 240, 50%, 50%, 0.5"));
        }

        @Test
        public void testWebHslaNoCloseParen() {
            assertThrows(IllegalArgumentException.class, () -> of("hsla(240, 50%, 50%, 0.5"));
        }

        @Test
        public void testDerive() {
            Colour original = hsb(180, 0.4, 0.8, 0.5);
            Colour color = original.derive(-90, 2, 0.5, 2);
            assertEquals(90, color.toHSB().hue(), 0.002);
            assertEquals(0.8, color.toHSB().saturation(), 0.001);
            assertEquals(0.4, color.toHSB().brightness(), 0.001);
            assertEquals(1.0, color.getOpacity(), 0.001);
        }

        @Test
        public void testDeriveFromRgb() {
            Colour original = rgb(128, 0, 255);

            double origHue = original.toHSB().hue();
            double origSat = original.toHSB().saturation();
            double origBri = original.toHSB().brightness();

            Colour color = original.derive(-30, 0.5, 0.5, 0.5);

            assertEquals(origHue - 30, color.toHSB().hue(), 0.001);
            assertEquals(origSat / 2, color.toHSB().saturation(), 0.001);
            assertEquals(origBri / 2, color.toHSB().brightness(), 0.001);
            assertEquals(0.5, color.getOpacity(), 0.001);
        }

        @Test
        public void testDeriveClipS() {
            Colour original = hsb(180, 0.4, 0.8, 0.5);
            Colour color = original.derive(-1170, -5, 20, -5);
            assertEquals(0, color.toHSB().hue(), 0.001);
            assertEquals(0.0, color.toHSB().saturation(), 0.001);
            assertEquals(1.0, color.toHSB().brightness(), 0.001);
            assertEquals(0.0, color.getOpacity(), 0.001);
        }

        @Test
        public void testDeriveClipHB() {
            Colour original = hsb(180, 0.4, 0.8, 0.5);
            Colour color = original.derive(-1170, 1.0, 20, -5);
            assertEquals(90, color.toHSB().hue(), 0.001);
            assertEquals(0.4, color.toHSB().saturation(), 0.001);
            assertEquals(1.0, color.toHSB().brightness(), 0.001);
            assertEquals(0.0, color.getOpacity(), 0.001);
        }

        @Test
        public void testDarker() {
            Colour original = hsb(180, 0.4, 0.8, 0.5);
            Colour color = original.darker(0.7);
            assertEquals(180, color.toHSB().hue(), 0.001);
            assertEquals(0.4, color.toHSB().saturation(), 0.001);
            assertEquals(0.56, color.toHSB().brightness(), 0.001);
            assertEquals(0.5, color.getOpacity(), 0.001);
        }

        @Test
        public void testBrighter() {
            Colour original = hsb(180, 0.4, 0.4, 0.5);
            Colour color = original.brighter(0.7);
            assertEquals(180, color.toHSB().hue(), 0.001);
            assertEquals(0.4, color.toHSB().saturation(), 0.001);
            assertEquals(0.5714, color.toHSB().brightness(), 0.001);
            assertEquals(0.5, color.getOpacity(), 0.001);
        }

        @Test
        public void testBlackBrighter() {
            Colour color = rgb(0.0f, 0.0f, 0.0f).brighter(0.7);
            assertTrue(color.toHSB().brightness() > 0.0);
            assertEquals(color.getRed(), color.getGreen(), 0.001);
            assertEquals(color.getRed(), color.getBlue(), 0.001);
        }

        @Test
        public void testSaturate() {
            Colour original = hsb(180, 0.4, 0.4, 0.5);
            Colour color = original.saturate(0.7);
            assertEquals(180, color.toHSB().hue(), 0.001);
            assertEquals(0.5714, color.toHSB().saturation(), 0.001);
            assertEquals(0.4, color.toHSB().brightness(), 0.001);
            assertEquals(0.5, color.getOpacity(), 0.001);
        }

        @Test
        public void testDesaturate() {
            Colour original = hsb(180, 0.8, 0.4, 0.5);
            Colour color = original.desaturate(0.7);
            assertEquals(180, color.toHSB().hue(), 0.001);
            assertEquals(0.56, color.toHSB().saturation(), 0.001);
            assertEquals(0.4, color.toHSB().brightness(), 0.001);
            assertEquals(0.5, color.getOpacity(), 0.001);
        }

        @Test
        public void testInvert() {
            Colour original = rgb(0.2, 0.3, 0.4, 0.6);
            Colour color = original.invert();
            assertEquals(0.8, color.getRed(), 0.001);
            assertEquals(0.7, color.getGreen(), 0.001);
            assertEquals(0.6, color.getBlue(), 0.001);
            assertEquals(0.6, color.getOpacity(), 0.001);
        }

        @Test
        public void testGreyscale() {
            Colour original = rgb(0.2, 0.3, 0.4, 0.6);
            Colour color = original.grayscale();
            assertEquals(0.283, color.getRed(), 0.001);
            assertEquals(0.283, color.getGreen(), 0.001);
            assertEquals(0.283, color.getBlue(), 0.001);
            assertEquals(0.6, color.getOpacity(), 0.001);
        }

        @Test
        public void testEquals() {
            Colour basic = rgb(0, 0, 0, 0.5);
            Colour equal = rgb(0, 0, 0, 0.5);
            Colour color1 = rgb(0xAA, 0, 0, 0.5);
            Colour color2 = rgb(0, 0xAA, 0, 0.5);
            Colour color3 = rgb(0, 0, 0xAA, 0.5);
            Colour color4 = rgb(0, 0, 0, 0.6);

            assertNotEquals(null, basic);
            assertNotEquals(new Object(), basic);
            //noinspection EqualsWithItself
            assertEquals(basic, basic);
            assertEquals(basic, equal);
            assertNotEquals(basic, color1);
            assertNotEquals(basic, color2);
            assertNotEquals(basic, color3);
            assertNotEquals(basic, color4);
        }

        @Test
        public void testHashCode() {
            Colour basic = rgb(0, 0, 0, 0.5);
            Colour equal = rgb(0, 0, 0, 0.5);
            Colour diffColor = rgb(0, 0xAA, 0, 0.5);
            Colour diffOpacity = rgb(0, 0, 0, 0.7);
            Colour transparent = rgb(0, 0, 0, 0.0);

            int code = basic.hashCode();
            int second = basic.hashCode();
            assertEquals(code, second);
            assertEquals(code, equal.hashCode());
            assertNotEquals(code, diffColor.hashCode());
            assertNotEquals(code, diffOpacity.hashCode());
            assertEquals(0, transparent.hashCode());
        }

        @Test
        public void testToString() {
            Colour color = rgb(0, 0, 0, 0.0);

            String s = color.toString();
            assertNotNull(s);
            assertFalse(s.isEmpty());
        }

        @Nested
        class InterpolationTest {
            @Test
            public void interpolateBetweenTwoDifferentValuesReturnsNewInstance() {
                var startValue = new Colour(0.2, 0.4, 0.6, 0.8);
                var endValue = new Colour(0.3, 0.5, 0.7, 0.9);
                assertEquals(new Colour(0.25, 0.45, 0.65, 0.85), startValue.interpolate(endValue, 0.5));
            }

            @Test
            public void interpolateBetweenTwoEqualValuesReturnsSameInstance() {
                var startValue = new Colour(0.2, 0.4, 0.6, 0.8);
                var endValue = new Colour(0.2, 0.4, 0.6, 0.8);
                assertSame(startValue, startValue.interpolate(endValue, 0.5));
            }

            @Test
            public void interpolationFactorZeroReturnsStartInstance() {
                var startValue = new Colour(0.2, 0.4, 0.6, 0.8);
                var endValue = new Colour(0.3, 0.5, 0.7, 0.9);
                assertSame(startValue, startValue.interpolate(endValue, 0));
            }

            @Test
            public void interpolationFactorOneReturnsEndInstance() {
                var startValue = new Colour(0.2, 0.4, 0.6, 0.8);
                var endValue = new Colour(0.3, 0.5, 0.7, 0.9);
                assertSame(endValue, startValue.interpolate(endValue, 1));
            }

            @Test
            public void interpolationFactorLessThanZero() {
                var startValue = new Colour(0.2, 0.4, 0.6, 0.8);
                var endValue = new Colour(0.3, 0.5, 0.7, 0.9);
                assertSimilar(new Colour(0.1, 0.3, 0.5, 0.7), startValue.interpolate(endValue, -1));
                assertSimilar(new Colour(0, 0.2, 0.4, 0.6), startValue.interpolate(endValue, -2));
                assertSimilar(new Colour(0, 0.1, 0.3, 0.5), startValue.interpolate(endValue, -3));
                assertSimilar(new Colour(0, 0, 0.2, 0.4), startValue.interpolate(endValue, -4));
            }

            @Test
            public void interpolationFactorGreaterThanOne() {
                var startValue = new Colour(0.2, 0.4, 0.6, 0.8);
                var endValue = new Colour(0.3, 0.5, 0.7, 0.9);
                assertSimilar(new Colour(0.4, 0.6, 0.8, 1), startValue.interpolate(endValue, 2));
                assertSimilar(new Colour(0.5, 0.7, 0.9, 1), startValue.interpolate(endValue, 3));
                assertSimilar(new Colour(0.6, 0.8, 1, 1), startValue.interpolate(endValue, 4));
                assertSimilar(new Colour(0.7, 0.9, 1, 1), startValue.interpolate(endValue, 5));
            }

            private static void assertSimilar(Colour expected, Colour actual) {
                assertEquals(expected.getRed(), actual.getRed(), 0.001);
                assertEquals(expected.getGreen(), actual.getGreen(), 0.001);
                assertEquals(expected.getBlue(), actual.getBlue(), 0.001);
                assertEquals(expected.getOpacity(), actual.getOpacity(), 0.001);
            }
        }
    }
    //endregion
}
