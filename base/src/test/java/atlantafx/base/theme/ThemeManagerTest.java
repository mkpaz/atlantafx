/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import atlantafx.base.util.NullSafety;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static atlantafx.base.theme.ThemeManager.Change;
import static atlantafx.base.theme.ThemeManager.instance;
import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class ThemeManagerTest {

    private ThemeManager themeManager = NullSafety.lateNonNull();
    private Theme testTheme1 = NullSafety.lateNonNull();
    private Theme testTheme2 = NullSafety.lateNonNull();

    @BeforeAll
    static void init() throws InterruptedException {
        var latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX startup timed out");
    }

    @BeforeEach
    void setUp() {
        runFx(() -> {
            themeManager = instance();
            testTheme1 = new TestTheme("Theme1");
            testTheme2 = new TestTheme("Theme2");
        });
    }

    @AfterEach
    void tearDown() {
        runFx(() -> themeManager.resetOptions());
    }

    record TestTheme(String stylesheet) implements Theme {

        @Override
        public String getName() {
            return stylesheet;
        }

        @Override
        public String getUserAgentStylesheet() {
            return stylesheet + ".css";
        }

        @Override
        public String getUserAgentStylesheetBSS() {
            return stylesheet + ".bss";
        }

        @Override
        public boolean isDarkMode() {
            return false;
        }
    }

    @SuppressWarnings("ConstantValue")
    static void runFx(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        var latch = new CountDownLatch(1);
        Throwable[] error = new Throwable[1];

        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });

        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                fail("JavaFX thread execution timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail(e);
        }

        if (error[0] != null) {
            if (error[0] instanceof RuntimeException re) throw re;
            if (error[0] instanceof AssertionError ae) throw ae;
            throw new RuntimeException(error[0]);
        }
    }

    //*************************************************************************

    @Nested
    class BaseTests {

        @Test
        @DisplayName("should update theme property")
        void testSetTheme() {
            runFx(() -> {
                themeManager.setTheme(testTheme1);

                assertEquals(testTheme1, themeManager.getTheme());
                assertEquals(testTheme1, themeManager.themeProperty().get());

                themeManager.setTheme(testTheme2);
                assertEquals(testTheme2, themeManager.getTheme());
            });
        }

        @Test
        @DisplayName("should satisfy record contract")
        void testKeyContract() {
            var key1 = new ThemeOption.Key<>("id.a", String.class);
            var key1Duplicate = new ThemeOption.Key<>("id.a", String.class);
            var key2DifferentType = new ThemeOption.Key<>("id.a", Integer.class);
            var key3DifferentId = new ThemeOption.Key<>("id.b", String.class);

            assertEquals(key1, key1Duplicate);
            assertEquals(key1.hashCode(), key1Duplicate.hashCode());

            assertNotEquals(key1, key2DifferentType);
            assertNotEquals(key1.hashCode(), key2DifferentType.hashCode());

            assertNotEquals(key1, key3DifferentId);

            assertNotNull(key1.toString());
            assertEquals("Key[id.a: String]", key1.toString());
        }

        @Test
        @DisplayName("key with same id but different type should not resolve to a registered option")
        void testKeyMismatchNotFound() {
            runFx(() -> {
                var stringKey = new ThemeOption.Key<>("test.mismatch", String.class);
                var option = ThemeOption.of(stringKey, "default", _ -> { });
                var integerKey = new ThemeOption.Key<>("test.mismatch", Integer.class);

                try {
                    themeManager.register(option);

                    assertTrue(themeManager.supports(stringKey));
                    assertFalse(themeManager.supports(integerKey));

                    assertThrows(IllegalArgumentException.class,
                        () -> themeManager.getOption(integerKey));
                    assertThrows(IllegalArgumentException.class,
                        () -> themeManager.setOption(integerKey, 42));
                } finally {
                    themeManager.unregister(option);
                }
            });
        }

        @Test
        @DisplayName("setTheme applies the theme's user agent stylesheet globally")
        void testSetThemeAppliesUserAgentStylesheet() {
            runFx(() -> {
                themeManager.setTheme(testTheme1);
                assertEquals(testTheme1.getUserAgentStylesheet(), Application.getUserAgentStylesheet());

                themeManager.setTheme(testTheme2);
                assertEquals(testTheme2.getUserAgentStylesheet(), Application.getUserAgentStylesheet());
            });
        }
    }

    @Nested
    class OptionTests {

        @Test
        @DisplayName("should register and return default value")
        void testRegisterAndGetDefault() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.register.str", String.class);
                var option = ThemeOption.of(key, "defaultValue", _ -> { });

                try {
                    themeManager.register(option);
                    assertTrue(themeManager.supports(key));
                    assertEquals("defaultValue", themeManager.getOption(key));
                } finally {
                    themeManager.unregister(option);
                }
                assertFalse(themeManager.supports(key));
            });
        }

        @Test
        @DisplayName("should throw on duplicate registration")
        void testRegisterDuplicate() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.dup.str", String.class);
                var option1 = ThemeOption.of(key, "val1", _ -> { });
                var option2 = ThemeOption.of(key, "val2", _ -> { });

                try {
                    themeManager.register(option1);

                    IllegalStateException ex = assertThrows(
                        IllegalStateException.class,
                        () -> themeManager.register(option2)
                    );
                    assertTrue(ex.getMessage().contains("already registered"));
                } finally {
                    themeManager.unregister(option1);
                }
            });
        }

        @Test
        @DisplayName("should update option value")
        void testSetOptionValue() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.set.str", String.class);
                var option = ThemeOption.of(key, "default", _ -> { });

                try {
                    themeManager.register(option);

                    themeManager.setOption(key, "newValue");
                    assertEquals("newValue", themeManager.getOption(key));

                    themeManager.setOption(key, null);
                    assertNull(themeManager.getOption(key));
                } finally {
                    themeManager.unregister(option);
                }
            });
        }

        @Test
        @DisplayName("should throw on invalid type cast")
        @SuppressWarnings("all")
        void testSetOptionInvalidType() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.cast.str", String.class);

                ThemeOption.Key rawKey = key;
                var option = ThemeOption.of(key, "default", _ -> { });

                try {
                    themeManager.register(option);
                    assertThrows(ClassCastException.class, () -> themeManager.setOption(rawKey, 12345));
                } finally {
                    themeManager.unregister(option);
                }
            });
        }

        @Test
        @DisplayName("should throw for unregistered option")
        void testUnregisteredOption() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.unregistered", Integer.class);
                assertThrows(IllegalArgumentException.class, () -> themeManager.getOption(key));
                assertThrows(IllegalArgumentException.class, () -> themeManager.setOption(key, 10));
                assertThrows(IllegalArgumentException.class, () -> themeManager.resetOption(key));
            });
        }

        @Test
        @DisplayName("should reset single option")
        void testResetSingleOption() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.reset.single", String.class);
                var option = ThemeOption.of(key, "initial", _ -> { });

                try {
                    themeManager.register(option);

                    themeManager.setOption(key, "modified");
                    assertEquals("modified", themeManager.getOption(key));

                    themeManager.resetOption(key);
                    assertEquals("initial", themeManager.getOption(key));
                } finally {
                    themeManager.unregister(option);
                }
            });
        }

        @Test
        @DisplayName("should reset all options")
        void testResetAllOptions() {
            runFx(() -> {
                var strKey = new ThemeOption.Key<>("test.reset.all.str", String.class);
                var intKey = new ThemeOption.Key<>("test.reset.all.int", Integer.class);

                var opt1 = ThemeOption.of(strKey, "def1", _ -> { });
                var opt2 = ThemeOption.of(intKey, 100, _ -> { });

                try {
                    themeManager.register(opt1).register(opt2);

                    themeManager.setOption(strKey, "changed");
                    themeManager.setOption(intKey, 999);

                    themeManager.resetOptions();

                    assertEquals("def1", themeManager.getOption(strKey));
                    assertEquals(100, themeManager.getOption(intKey));
                } finally {
                    themeManager.unregister(opt1).unregister(opt2);
                }
            });
        }

        @Test
        @DisplayName("should trigger option apply")
        void testDirectOptionApply() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.direct.apply", String.class);
                var ref = new AtomicReference<ThemeManager.@Nullable Change<String>>();

                var option = ThemeOption.of(key, "default", ref::set);
                var mockScene = new Scene(new Pane());

                var change = new ThemeManager.Change<>(testTheme1, "newValue", mockScene);
                option.apply(change);

                var captured = ref.get();
                assertNotNull(captured);
                assertEquals("newValue", captured.value());
                assertEquals(testTheme1, captured.theme());
                assertEquals(mockScene, captured.scene());
            });
        }

        @Test
        @DisplayName("should throw on duplicate id even with different Key type")
        void testRegisterDuplicateIdDifferentType() {
            runFx(() -> {
                var stringKey = new ThemeOption.Key<>("test.dup.mixed", String.class);
                var intKey = new ThemeOption.Key<>("test.dup.mixed", Integer.class);

                var option1 = ThemeOption.of(stringKey, "val1", _ -> { });
                var option2 = ThemeOption.of(intKey, 1, _ -> { });

                try {
                    themeManager.register(option1);

                    IllegalStateException ex = assertThrows(
                        IllegalStateException.class,
                        () -> themeManager.register(option2)
                    );
                    assertTrue(ex.getMessage().contains("already registered"));

                    assertFalse(themeManager.supports(intKey));
                } finally {
                    themeManager.unregister(option1);
                }
            });
        }

        @Test
        @DisplayName("unregister on a key that was never registered should be no-op")
        void testUnregisterNonExistent() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.never.registered", String.class);
                var option = ThemeOption.of(key, "default", _ -> { });

                assertFalse(themeManager.supports(key));
                assertDoesNotThrow(() -> themeManager.unregister(option));
                assertFalse(themeManager.supports(key));
            });
        }

        @Test
        @DisplayName("supports should return false before any registration")
        void testSupportsInitiallyFalse() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.supports.initial", String.class);
                assertFalse(themeManager.supports(key));
            });
        }

        @Test
        @DisplayName("should allow null default value")
        void testNullDefaultValue() {
            runFx(() -> {
                var key = new ThemeOption.Key<>("test.null.default", String.class);
                var option = ThemeOption.of(key, null, _ -> { });

                try {
                    themeManager.register(option);
                    assertNull(themeManager.getOption(key));

                    themeManager.setOption(key, "value");
                    assertEquals("value", themeManager.getOption(key));

                    themeManager.resetOption(key);
                    assertNull(themeManager.getOption(key));
                } finally {
                    themeManager.unregister(option);
                }
            });
        }

        @Test
        @DisplayName("resetOptions on empty registry should not throw")
        void testResetOptionsWhenEmpty() {
            runFx(() -> assertDoesNotThrow(() -> themeManager.resetOptions()));
        }
    }

    @Nested
    class ChangeTests {

        @Test
        @DisplayName("should add stylesheet when value is present")
        void testAddStylesheet() {
            runFx(() -> {
                Pane root = new Pane();
                Scene scene = new Scene(root);
                Change<String> change = new Change<>(testTheme1, "16px", scene);

                change.applyStylesheet("font.size", root, val -> ".root { -fx-font-size: " + val + "; }");

                assertEquals(1, root.getStylesheets().size());
                assertTrue(root.getStylesheets().getFirst().endsWith("/*option:font.size*/"));
            });
        }

        @Test
        @DisplayName("should update existing stylesheet when value changes")
        void testUpdateStylesheet() {
            runFx(() -> {
                Pane root = new Pane();
                Scene scene = new Scene(root);

                Change<String> initialChange = new Change<>(testTheme1, "16px", scene);
                initialChange.applyStylesheet("font.size", root, val -> ".root { -fx-font-size: " + val + "; }");

                Change<String> updatedChange = new Change<>(testTheme1, "18px", scene);
                updatedChange.applyStylesheet("font.size", root, val -> ".root { -fx-font-size: " + val + "; }");

                assertEquals(1, root.getStylesheets().size());
                assertTrue(root.getStylesheets().getFirst().endsWith("/*option:font.size*/"));
            });
        }

        @Test
        @DisplayName("should remove stylesheet when value is null")
        void testRemovesStylesheetOnNullValue() {
            runFx(() -> {
                Pane root = new Pane();
                Scene scene = new Scene(root);

                Change<String> initialChange = new Change<>(testTheme1, "16px", scene);
                initialChange.applyStylesheet("font.size", root, val -> ".root { -fx-font-size: " + val + "; }");
                assertEquals(1, root.getStylesheets().size());

                Change<String> nullChange = new Change<>(testTheme1, null, scene);
                nullChange.applyStylesheet("font.size", root, val -> ".root { -fx-font-size: " + val + "; }");

                assertTrue(root.getStylesheets().isEmpty());
            });
        }
    }
}