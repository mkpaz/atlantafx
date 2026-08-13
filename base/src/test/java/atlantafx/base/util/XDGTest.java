/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("DuplicateExpressions")
@NullMarked
class XDGTest {

    // Creates a spy over a resolver instance to intercept system env variables and Java system properties.
    private <T extends XDG.Resolver> T createSpy(T instance, Map<String, String> env, Map<String, String> props) {
        T spy = spy(instance);

        doAnswer(inv -> env.get(inv.getArgument(0, String.class)))
            .when(spy).getEnv(anyString());

        doAnswer(inv -> props.get(inv.getArgument(0, String.class)))
            .when(spy).getProperty(anyString());

        return spy;
    }

    @Nested
    class LinuxResolverTest {

        private Map<String, String> env;

        @BeforeEach
        void setup() {
            env = new HashMap<>();
            Map<String, String> props = Map.of("user.home", "/home/user");

            XDG.Resolver resolver = createSpy(new XDG.LinuxResolver(), env, props);
            XDG.setResolver(resolver);
        }

        @Test
        @DisplayName("should return default base directories")
        void testBaseDefaults() {
            assertEquals(Path.of("/home/user", ".local", "share"), XDG.DATA_HOME.resolve());
            assertEquals(Path.of("/home/user", ".config"), XDG.CONFIG_HOME.resolve());
            assertEquals(Path.of("/home/user", ".cache"), XDG.CACHE_HOME.resolve());
            assertEquals(Path.of("/home/user", ".local", "state"), XDG.STATE_HOME.resolve());
            assertEquals(Path.of("/home/user", ".local", "run"), XDG.RUNTIME_DIR.resolve());

            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.isDirectory(any(Path.class))).thenReturn(true);

                List<Path> expectedDataDirs = List.of(Path.of("/usr/local/share"), Path.of("/usr/share"));
                assertEquals(expectedDataDirs, XDG.getResolver().stream(XDG.DATA_DIRS).toList());

                List<Path> expectedConfigDirs = List.of(Path.of("/etc/xdg"));
                assertEquals(expectedConfigDirs, XDG.getResolver().stream(XDG.CONFIG_DIRS).toList());
            }
        }

        @Test
        @DisplayName("should return default user directories")
        void testUserDefaults() {
            assertEquals(Path.of("/home/user", "Desktop"), XDG.DESKTOP_DIR.resolve());
            assertEquals(Path.of("/home/user", "Documents"), XDG.DOCUMENTS_DIR.resolve());
            assertEquals(Path.of("/home/user", "Downloads"), XDG.DOWNLOAD_DIR.resolve());
            assertEquals(Path.of("/home/user", "Music"), XDG.MUSIC_DIR.resolve());
            assertEquals(Path.of("/home/user", "Pictures"), XDG.PICTURES_DIR.resolve());
            assertEquals(Path.of("/home/user", "Public"), XDG.PUBLICSHARE_DIR.resolve());
            assertEquals(Path.of("/home/user", "Templates"), XDG.TEMPLATES_DIR.resolve());
            assertEquals(Path.of("/home/user", "Videos"), XDG.VIDEOS_DIR.resolve());
        }

        @Test
        @DisplayName("should override paths when env variables are set")
        void testEnvOverrides() {
            env.put("XDG_DATA_HOME", "/custom/data");
            env.put("XDG_CONFIG_HOME", "/custom/config");
            env.put("XDG_CACHE_HOME", "/custom/cache");
            env.put("XDG_STATE_HOME", "/custom/state");
            env.put("XDG_RUNTIME_DIR", "/custom/run");

            assertEquals(Path.of("/custom/data"), XDG.DATA_HOME.resolve());
            assertEquals(Path.of("/custom/config"), XDG.CONFIG_HOME.resolve());
            assertEquals(Path.of("/custom/cache"), XDG.CACHE_HOME.resolve());
            assertEquals(Path.of("/custom/state"), XDG.STATE_HOME.resolve());
            assertEquals(Path.of("/custom/run"), XDG.RUNTIME_DIR.resolve());
        }

        @Test
        @DisplayName("should split multi path env variables")
        void testMultiPathOverrides() {
            env.put("XDG_DATA_DIRS", "/opt/share:/usr/share");
            env.put("XDG_CONFIG_DIRS", "/etc/custom/xdg:/etc/xdg");

            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.isDirectory(any(Path.class))).thenReturn(true);

                List<Path> expectedDataDirs = List.of(Path.of("/opt/share"), Path.of("/usr/share"));
                List<Path> expectedConfigDirs = List.of(Path.of("/etc/custom/xdg"), Path.of("/etc/xdg"));

                assertEquals(expectedDataDirs, XDG.getResolver().stream(XDG.DATA_DIRS).toList());
                assertEquals(expectedConfigDirs, XDG.getResolver().stream(XDG.CONFIG_DIRS).toList());
            }
        }

        @Test
        @DisplayName("should filter non existent files when querying")
        void testQueryFiltersFiles(@TempDir Path tempDir) throws IOException {
            Path dir1 = tempDir.resolve("dir1");
            Path dir2 = tempDir.resolve("dir2");
            Files.createDirectories(dir1);
            Files.createDirectories(dir2);

            Path targetFile = Files.createFile(dir2.resolve("app.conf"));

            env.put("XDG_CONFIG_DIRS", dir1 + ":" + dir2);

            List<Path> found = XDG.CONFIG_DIRS.query("app.conf").toList();

            assertEquals(List.of(targetFile), found);
        }

        @Test
        @DisplayName("should ignore empty and blank path segments")
        void testIgnoreEmptySegments() {
            assertEquals(
                Path.of("/home/user", ".config", "myOrg", "myApp"),
                XDG.CONFIG_HOME.resolve("myOrg", "  ", "", "myApp")
            );
        }

        @Test
        @DisplayName("should throw exception when single path is requested for multi path entries")
        void testGetDirFailsForMultiPaths() {
            assertThrows(IllegalArgumentException.class, XDG.DATA_DIRS::resolve);
            assertThrows(IllegalArgumentException.class, XDG.CONFIG_DIRS::resolve);
        }
    }

    @Nested
    class MacResolverTest {

        private Map<String, String> envVars;

        @BeforeEach
        void setup() {
            envVars = new HashMap<>();
            Map<String, String> sysProps = Map.of("user.home", "/Users/user");

            XDG.Resolver macSpy = createSpy(new XDG.MacResolver(), envVars, sysProps);
            XDG.setResolver(macSpy);
        }

        @Test
        @DisplayName("should return macOS default base directories when no env variables exist")
        void testBaseDefaults() {
            assertEquals(Path.of("/Users/user/Library/Application Support"), XDG.DATA_HOME.resolve());
            assertEquals(Path.of("/Users/user/Library/Application Support"), XDG.CONFIG_HOME.resolve());
            assertEquals(Path.of("/Users/user/Library/Caches"), XDG.CACHE_HOME.resolve());
            assertEquals(Path.of("/Users/user/Library/Application Support"), XDG.STATE_HOME.resolve());
            assertEquals(Path.of("/Users/user/Library/Application Support/run"), XDG.RUNTIME_DIR.resolve());

            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.isDirectory(any(Path.class))).thenReturn(true);

                List<Path> expectedDirs = List.of(Path.of("/Library/Application Support"));
                assertEquals(expectedDirs, XDG.getResolver().stream(XDG.DATA_DIRS).toList());
                assertEquals(expectedDirs, XDG.getResolver().stream(XDG.CONFIG_DIRS).toList());
            }
        }

        @Test
        @DisplayName("should return macOS default user directories")
        void testUserDefaults() {
            assertEquals(Path.of("/Users/user/Desktop"), XDG.DESKTOP_DIR.resolve());
            assertEquals(Path.of("/Users/user/Documents"), XDG.DOCUMENTS_DIR.resolve());
            assertEquals(Path.of("/Users/user/Downloads"), XDG.DOWNLOAD_DIR.resolve());
            assertEquals(Path.of("/Users/user/Music"), XDG.MUSIC_DIR.resolve());
            assertEquals(Path.of("/Users/user/Pictures"), XDG.PICTURES_DIR.resolve());
            assertEquals(Path.of("/Users/user/Public"), XDG.PUBLICSHARE_DIR.resolve());
            assertEquals(Path.of("/Users/user/Templates"), XDG.TEMPLATES_DIR.resolve());
            assertEquals(Path.of("/Users/user/Movies"), XDG.VIDEOS_DIR.resolve());
        }

        @Test
        @DisplayName("should honor explicit XDG env variables on macOS")
        void testEnvOverrides() {
            envVars.put("XDG_DATA_HOME", "/custom/mac/data");
            envVars.put("XDG_CONFIG_HOME", "/custom/mac/config");

            assertEquals(Path.of("/custom/mac/data"), XDG.DATA_HOME.resolve());
            assertEquals(Path.of("/custom/mac/config"), XDG.CONFIG_HOME.resolve());
        }
    }

    @Nested
    class WindowsResolverTest {

        private Map<String, String> envVars;

        @BeforeEach
        void setup() {
            envVars = new HashMap<>();
            Map<String, String> sysProps = Map.of("user.home", "C:\\Users\\User");

            XDG.Resolver winSpy = createSpy(new XDG.WindowsResolver(), envVars, sysProps);
            XDG.setResolver(winSpy);
        }

        @Test
        @DisplayName("should map Windows env variables to XDG directories")
        void testWindowsEnvMapping() {
            envVars.put("APPDATA", "C:\\Custom\\AppData");
            envVars.put("LOCALAPPDATA", "C:\\Custom\\LocalAppData");
            envVars.put("PROGRAMDATA", "C:\\Custom\\ProgramData");

            assertEquals(Path.of("C:\\Custom\\AppData"), XDG.DATA_HOME.resolve());
            assertEquals(Path.of("C:\\Custom\\AppData"), XDG.CONFIG_HOME.resolve());
            assertEquals(Path.of("C:\\Custom\\LocalAppData"), XDG.CACHE_HOME.resolve());
            assertEquals(Path.of("C:\\Custom\\LocalAppData"), XDG.STATE_HOME.resolve());
            assertEquals(Path.of("C:\\Custom\\LocalAppData"), XDG.RUNTIME_DIR.resolve());

            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.isDirectory(any(Path.class))).thenReturn(true);

                assertEquals(List.of(Path.of("C:\\Custom\\ProgramData")), XDG.getResolver().stream(XDG.DATA_DIRS).toList());
                assertEquals(List.of(Path.of("C:\\Custom\\ProgramData")), XDG.getResolver().stream(XDG.CONFIG_DIRS).toList());
            }
        }

        @Test
        @DisplayName("should fallback to default paths when Windows env variables are missing")
        void testWindowsFallbackWhenEnvMissing() {
            XDG.WindowsResolver winResolver = spy(new XDG.WindowsResolver());
            doReturn(Path.of("C:\\Users\\User")).when(winResolver).getHome();
            doReturn(null).when(winResolver).getEnv(anyString());
            XDG.setResolver(winResolver);

            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.isDirectory(any(Path.class))).thenReturn(true);

                assertEquals(Path.of("C:\\Users\\User", "AppData", "Roaming"), XDG.DATA_HOME.resolve());
                assertEquals(Path.of("C:\\Users\\User", "AppData", "Roaming"), XDG.CONFIG_HOME.resolve());
                assertEquals(Path.of("C:\\Users\\User", "AppData", "Local"), XDG.CACHE_HOME.resolve());
                assertEquals(Path.of("C:\\Users\\User", "AppData", "Local"), XDG.STATE_HOME.resolve());
                assertEquals(Path.of("C:\\Users\\User", "AppData", "Local"), XDG.RUNTIME_DIR.resolve());

                assertEquals(List.of(Path.of("C:\\ProgramData")), XDG.getResolver().stream(XDG.DATA_DIRS).toList());
                assertEquals(List.of(Path.of("C:\\ProgramData")), XDG.getResolver().stream(XDG.CONFIG_DIRS).toList());
            }
        }

        @Test
        @DisplayName("should fallback to default user directories on Windows")
        void testWindowsUserDefaults() {
            XDG.WindowsResolver winResolver = spy(new XDG.WindowsResolver());
            doReturn(Path.of("C:\\Users\\User")).when(winResolver).getHome();
            XDG.setResolver(winResolver);

            assertEquals(Path.of("C:\\Users\\User", "Desktop"), XDG.DESKTOP_DIR.resolve());
            assertEquals(Path.of("C:\\Users\\User", "Documents"), XDG.DOCUMENTS_DIR.resolve());
            assertEquals(Path.of("C:\\Users\\User", "Downloads"), XDG.DOWNLOAD_DIR.resolve());
            assertEquals(Path.of("C:\\Users\\User", "Music"), XDG.MUSIC_DIR.resolve());
            assertEquals(Path.of("C:\\Users\\User", "Pictures"), XDG.PICTURES_DIR.resolve());
            assertEquals(Path.of("C:\\Users\\User", "Public"), XDG.PUBLICSHARE_DIR.resolve());
            assertEquals(Path.of("C:\\Users\\User", "Templates"), XDG.TEMPLATES_DIR.resolve());
            assertEquals(Path.of("C:\\Users\\User", "Videos"), XDG.VIDEOS_DIR.resolve());
        }

        @Test
        @DisplayName("should honor explicit XDG env variables on Windows over system defaults")
        void testExplicitXdgOverridesOnWindows() {
            Path targetPath = Path.of("/custom/xdg/data");

            XDG.Resolver winResolver = spy(new XDG.WindowsResolver());
            doReturn(targetPath).when(winResolver).resolve(XDG.DATA_HOME);
            XDG.setResolver(winResolver);

            assertEquals(targetPath, XDG.DATA_HOME.resolve());
        }
    }

    @Nested
    class UserDirsTest {

        private Map<String, String> envVars;
        private Map<String, String> sysProps;

        @BeforeEach
        void setup() {
            envVars = new HashMap<>();
            sysProps = new HashMap<>();
            XDG.LinuxResolver.resetUserDirs();
        }

        @AfterEach
        void tearDown() {
            XDG.LinuxResolver.resetUserDirs();
        }

        @Test
        @DisplayName("should expand home variable in user-dirs file")
        void testHomeExpansion(@TempDir Path tempDir) throws IOException {
            Path homeDir = tempDir.resolve("home");
            Path configDir = homeDir.resolve(".config");
            Files.createDirectories(configDir);

            Path userDirsFile = configDir.resolve("user-dirs.dirs");
            String content = """
                # Comment line
                XDG_DESKTOP_DIR="$HOME/MyDesktop"
                XDG_DOCUMENTS_DIR="${HOME}/MyDocuments"
                XDG_DOWNLOAD_DIR="/absolute/downloads/path"
                XDG_MUSIC_DIR="$HOME/Music"
                """;
            Files.writeString(userDirsFile, content);

            sysProps.put("user.home", homeDir.toString());

            XDG.Resolver linuxSpy = createSpy(new XDG.LinuxResolver(), envVars, sysProps);
            XDG.setResolver(linuxSpy);

            assertEquals(homeDir.resolve("MyDesktop"), XDG.DESKTOP_DIR.resolve());
            assertEquals(homeDir.resolve("MyDocuments"), XDG.DOCUMENTS_DIR.resolve());
            assertEquals(Path.of("/absolute/downloads/path"), XDG.DOWNLOAD_DIR.resolve());
            assertEquals(homeDir.resolve("Music"), XDG.MUSIC_DIR.resolve());
        }

        @Test
        @DisplayName("should prioritize user-dirs file over env variables")
        void testUserDirsPriority(@TempDir Path tempDir) throws IOException {
            Path homeDir = tempDir.resolve("home");
            Path configDir = homeDir.resolve(".config");
            Files.createDirectories(configDir);

            Path userDirsFile = configDir.resolve("user-dirs.dirs");
            Files.writeString(userDirsFile, "XDG_DOCUMENTS_DIR=\"$HOME/FromUserDirs\"\n");

            sysProps.put("user.home", homeDir.toString());
            envVars.put("XDG_DOCUMENTS_DIR", "/from/env/var");

            XDG.Resolver linuxSpy = createSpy(new XDG.LinuxResolver(), envVars, sysProps);
            XDG.setResolver(linuxSpy);

            assertEquals(homeDir.resolve("FromUserDirs"), XDG.DOCUMENTS_DIR.resolve());
        }

        @Test
        @DisplayName("should handle invalid user-dirs file gracefully")
        void testInvalidFileHandling(@TempDir Path tempDir) throws IOException {
            Path homeDir = tempDir.resolve("home");
            Path configDir = homeDir.resolve(".config");
            Files.createDirectories(configDir);

            Path userDirsFile = configDir.resolve("user-dirs.dirs");
            Files.createDirectories(userDirsFile);

            sysProps.put("user.home", homeDir.toString());

            XDG.Resolver linuxSpy = createSpy(new XDG.LinuxResolver(), envVars, sysProps);
            XDG.setResolver(linuxSpy);

            assertEquals(homeDir.resolve("Documents"), XDG.DOCUMENTS_DIR.resolve());
        }

        @Test
        @DisplayName("should ignore relative paths in user-dirs file and fallback to default")
        void testIgnoreRelativePaths(@TempDir Path tempDir) throws IOException {
            Path homeDir = tempDir.resolve("home");
            Path configDir = homeDir.resolve(".config");
            Files.createDirectories(configDir);

            Path userDirsFile = configDir.resolve("user-dirs.dirs");
            String content = """
                XDG_DESKTOP_DIR="RelativeDesktop"
                XDG_DOCUMENTS_DIR="relative/path/to/docs"
                XDG_DOWNLOAD_DIR="Downloads"
                """;
            Files.writeString(userDirsFile, content);

            sysProps.put("user.home", homeDir.toString());

            XDG.Resolver linuxSpy = createSpy(new XDG.LinuxResolver(), envVars, sysProps);
            XDG.setResolver(linuxSpy);

            assertEquals(homeDir.resolve("Desktop"), XDG.DESKTOP_DIR.resolve());
            assertEquals(homeDir.resolve("Documents"), XDG.DOCUMENTS_DIR.resolve());
            assertEquals(homeDir.resolve("Downloads"), XDG.DOWNLOAD_DIR.resolve());
        }

        @Test
        @DisplayName("should fallback to env variable if user dirs path is relative")
        void testFallbackToEnvOnRelativePath(@TempDir Path tempDir) throws IOException {
            Path homeDir = tempDir.resolve("home");
            Path configDir = homeDir.resolve(".config");
            Files.createDirectories(configDir);

            Path userDirsFile = configDir.resolve("user-dirs.dirs");
            Files.writeString(userDirsFile, "XDG_DOCUMENTS_DIR=\"relative/docs\"\n");

            sysProps.put("user.home", homeDir.toString());
            envVars.put("XDG_DOCUMENTS_DIR", "/absolute/env/docs");

            XDG.Resolver linuxSpy = createSpy(new XDG.LinuxResolver(), envVars, sysProps);
            XDG.setResolver(linuxSpy);

            assertEquals(Path.of("/absolute/env/docs"), XDG.DOCUMENTS_DIR.resolve());
        }
    }

    @Nested
    class ValidationAndFallbackTest {

        @Test
        @DisplayName("should fallback to default when env variable is blank")
        void testEmptyEnvFallback() {
            XDG.LinuxResolver linuxResolver = spy(new XDG.LinuxResolver());

            doReturn("   ").when(linuxResolver).getEnv("XDG_DATA_HOME");
            doReturn("/home/user").when(linuxResolver).getProperty("user.home");

            Path result = linuxResolver.get(XDG.DATA_HOME);
            assertEquals(Paths.get("/home/user/.local/share"), result);
        }

        @Test
        @DisplayName("should ignore relative paths in env variables")
        void testIgnoreRelativeEnvPath() {
            XDG.LinuxResolver linuxResolver = spy(new XDG.LinuxResolver());

            doReturn("relative/path/to/config").when(linuxResolver).getEnv("XDG_CONFIG_HOME");
            doReturn("/home/user").when(linuxResolver).getProperty("user.home");

            Path result = linuxResolver.get(XDG.CONFIG_HOME);
            assertEquals(Paths.get("/home/user/.config"), result);
        }

        @Test
        @DisplayName("should filter out relative paths from list")
        void testFilterRelativePathsFromList() {
            XDG.LinuxResolver linuxResolver = spy(new XDG.LinuxResolver());

            String mockPathList = "/valid/abs/path:relative/path:/another/valid/path";
            doReturn(mockPathList).when(linuxResolver).getEnv("XDG_DATA_DIRS");

            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.isDirectory(any(Path.class))).thenReturn(true);

                List<Path> result = linuxResolver.stream(XDG.DATA_DIRS).toList();

                assertEquals(2, result.size());
                assertEquals(Paths.get("/valid/abs/path"), result.get(0));
                assertEquals(Paths.get("/another/valid/path"), result.get(1));
            }
        }

        @Test
        @DisplayName("should resolve home path from $HOME env when user.home property is missing")
        void testResolveHomeFromEnv() {
            XDG.Resolver resolver = new XDG.Resolver() {
                @Override
                public Path get(XDG xdg) {
                    return getHome();
                }

                @Override
                public Stream<Path> stream(XDG xdg) {
                    return Stream.of(getHome()).filter(Files::isDirectory);
                }

                @Override
                public @Nullable String getProperty(String name) {
                    if ("user.home".equals(name)) {
                        return null;
                    }
                    return XDG.Resolver.super.getProperty(name);
                }

                @Override
                public @Nullable String getEnv(String name) {
                    if ("HOME".equals(name)) {
                        return "/custom/home/dir";
                    }
                    return XDG.Resolver.super.getEnv(name);
                }
            };

            XDG.Resolver original = XDG.getResolver();
            XDG.setResolver(resolver);

            try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
                filesMock.when(() -> Files.isDirectory(Path.of("/custom/home/dir"))).thenReturn(true);

                List<Path> paths = XDG.HOME.query().toList();

                assertEquals(1, paths.size());
                assertEquals(Path.of("/custom/home/dir"), paths.getFirst());
            } finally {
                XDG.setResolver(original);
            }
        }
    }

    @Nested
    class EnumStructureTest {

        @Test
        @DisplayName("should format env variable names correctly")
        void testEnvNameFormatting() {
            assertEquals("XDG_DATA_HOME", XDG.DATA_HOME.env());
            assertEquals("XDG_CONFIG_DIRS", XDG.CONFIG_DIRS.env());
            assertEquals("XDG_RUNTIME_DIR", XDG.RUNTIME_DIR.env());
        }

        @Test
        @DisplayName("should set and return custom resolver")
        void testSetAndGetResolver() {
            XDG.Resolver customResolver = new XDG.LinuxResolver();
            XDG.setResolver(customResolver);

            assertSame(customResolver, XDG.getResolver());
        }
    }
}