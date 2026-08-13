/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Implements the standard directories defined by the XDG Base Directory Specification,
 * with cross-platform resolutions for macOS and Windows.
 *
 * <p>The XDG Base Directory Specification defines standard paths for storing user-specific files,
 * configurations, cache, and other data in Unix-like systems. This enumeration contains constants
 * for all standard and some additional (non-standard) directories defined in the XDG specification.
 * On non-Linux platforms (macOS, Windows), these locations are automatically mapped to native OS
 * directories if explicit {@code $XDG_*} environment variables are not set.
 *
 * <p>For user directories such as {@code Desktop} or {@code Documents}, which may have localized names,
 * parsing of the {@code user-dirs.dirs} file is implemented.
 *
 * <pre>{@code
 * | Environment Variable | OS      | Path                                   |
 * |----------------------|---------|----------------------------------------|
 * | $XDG_DATA_HOME       | Linux   | $HOME/.local/share                     |
 * |                      | Windows | %APPDATA% → $HOME/AppData/Roaming      |
 * |                      | macOS   | $HOME/Library/Application Support      |
 * | $XDG_CONFIG_HOME     | Linux   | $HOME/.config                          |
 * |                      | Windows | %APPDATA% → $HOME/AppData/Roaming      |
 * |                      | macOS   | $HOME/Library/Application Support      |
 * | $XDG_CACHE_HOME      | Linux   | $HOME/.cache                           |
 * |                      | Windows | %LOCALAPPDATA% → $HOME/AppData/Local   |
 * |                      | macOS   | $HOME/Library/Caches                   |
 * | $XDG_STATE_HOME      | Linux   | $HOME/.local/state                     |
 * |                      | Windows | %LOCALAPPDATA% → $HOME/AppData/Local   |
 * |                      | macOS   | $HOME/Library/Application Support      |
 * | $XDG_RUNTIME_DIR     | Linux   | $HOME/.local/run                       |
 * |                      | Windows | %LOCALAPPDATA% → $HOME/AppData/Local   |
 * |                      | macOS   | $HOME/Library/Application Support/run  |
 * | $XDG_DATA_DIRS       | Linux   | /usr/local/share : /usr/share          |
 * |                      | Windows | %PROGRAMDATA% → C:\ProgramData         |
 * |                      | macOS   | /Library/Application Support           |
 * | $XDG_CONFIG_DIRS     | Linux   | /etc/xdg                               |
 * |                      | Windows | %PROGRAMDATA% → C:\ProgramData         |
 * |                      | macOS   | /Library/Application Support           |
 * | $XDG_DESKTOP_DIR     | Any     | $HOME/Desktop                          |
 * | $XDG_DOCUMENTS_DIR   | Any     | $HOME/Documents                        |
 * | $XDG_DOWNLOAD_DIR    | Any     | $HOME/Downloads                        |
 * | $XDG_MUSIC_DIR       | Any     | $HOME/Music                            |
 * | $XDG_PICTURES_DIR    | Any     | $HOME/Pictures                         |
 * | $XDG_PUBLICSHARE_DIR | Any     | $HOME/Public                           |
 * | $XDG_TEMPLATES_DIR   | Any     | $HOME/Templates                        |
 * | $XDG_VIDEOS_DIR      | Linux   | $HOME/Videos                           |
 * |                      | Windows | $HOME/Videos                           |
 * |                      | macOS   | $HOME/Movies                           |
 * }</pre>
 *
 * <p>This enum is inherently thread-safe and acts as a singleton for accessing application directories.
 *
 * <p><b>Usage Examples:</b>
 *
 * <pre>{@code
 * // Obtain path to the data home directory.
 * Path dataDir = XDG.DATA_HOME.path();
 *
 * // Resolve path to the app config directory.
 * Path configDir = XDG.CONFIG_HOME.resolve("myapp", "templates");
 * }</pre>
 *
 * <p>In addition, you can implement your own {@link Resolver} to customize how directories
 * are resolved for a specific use case.
 *
 * @see <a href="https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html">
 * XDG Base Directory Specification</a>
 *
 * @see <a href="https://www.freedesktop.org/wiki/Software/xdg-user-dirs/">xdg-user-dirs</a>
 *
 */
public enum XDG {

    /**
     * The path of the user's home directory.
     * Typically corresponds to the {@code $HOME} environment variable (or {@code %USERPROFILE%} on Windows).
     */
    HOME,

    /**
     * The base directory relative to which user-specific data files should be stored.
     *
     * <p>This directory is defined by the {@code $XDG_DATA_HOME} environment variable.
     * If the variable is not set, a default equal to {@code $HOME/.local/share} should be used.
     *
     * <p><b>Platform fallbacks (if env variable is missing):</b>
     * <ul>
     *   <li>macOS: {@code $HOME/Library/Application Support}</li>
     *   <li>Windows: {@code %APPDATA%} or {@code $HOME/AppData/Roaming}</li>
     * </ul>
     */
    DATA_HOME,

    /**
     * The base directory relative to which user-specific configuration files should be written.
     *
     * <p>This directory is defined by the {@code $XDG_CONFIG_HOME} environment variable.
     * If the variable is not set, a default equal to {@code $HOME/.config} should be used.
     *
     * <p><b>Platform fallbacks (if env variable is missing):</b>
     * <ul>
     *   <li>macOS: {@code $HOME/Library/Application Support}</li>
     *   <li>Windows: {@code %APPDATA%} or {@code $HOME/AppData/Roaming}</li>
     * </ul>
     */
    CONFIG_HOME,

    /**
     * The base directory relative to which user-specific non-essential (cached) data should be written.
     *
     * <p>This directory is defined by the {@code $XDG_CACHE_HOME} environment variable.
     * If the variable is not set, a default equal to {@code $HOME/.cache} should be used.
     *
     * <p><b>Platform fallbacks (if env variable is missing):</b>
     * <ul>
     *   <li>macOS: {@code $HOME/Library/Caches}</li>
     *   <li>Windows: {@code %LOCALAPPDATA%} or {@code $HOME/AppData/Local}</li>
     * </ul>
     */
    CACHE_HOME,

    /**
     * The base directory relative to which user-specific state files should be stored.
     *
     * <p>This directory is defined by the {@code $XDG_STATE_HOME} environment variable.
     * If the variable is not set, a default equal to {@code ~/.local/state} should be used.
     *
     * <p><b>Platform fallbacks (if env variable is missing):</b>
     * <ul>
     *   <li>macOS: {@code $HOME/Library/Application Support}</li>
     *   <li>Windows: {@code %LOCALAPPDATA%} or {@code $HOME/AppData/Local}</li>
     * </ul>
     */
    STATE_HOME,

    /**
     * The base directory relative to which user-specific non-essential runtime files
     * and other file objects (such as sockets, named pipes, etc.) should be stored.
     *
     * <p>This directory is defined by the {@code $XDG_RUNTIME_DIR} environment variable.
     * If the variable is not set, applications should fall back to a replacement directory
     * with similar capabilities.
     *
     * <p>Applications should use this directory for communication and synchronization
     * purposes and should not place larger files in it, since it might reside in
     * runtime memory and cannot necessarily be swapped out to disk.
     *
     * <p><b>Platform fallbacks (if env variable is missing):</b>
     * <ul>
     *   <li>Linux default: {@code $HOME/.local/run}</li>
     *   <li>macOS: {@code $HOME/Library/Application Support/run}</li>
     *   <li>Windows: {@code %LOCALAPPDATA%} or {@code $HOME/AppData/Local}</li>
     * </ul>
     */
    RUNTIME_DIR,

    /**
     * The preference-ordered set of base directories to search for data files
     * in addition to the {@link #DATA_HOME} base directory.
     *
     * <p>This set of directories is defined by the {@code $XDG_DATA_DIRS} environment
     * variable. If the variable is not set, the default directories to be used are
     * {@code /usr/local/share} and {@code /usr/share}, in that order.
     *
     * <p>The {@link #DATA_HOME} directory is considered more important than any of the
     * directories defined by DATA_DIRS. Therefore, user data files should be
     * written relative to the {@link #DATA_HOME} directory, if possible.
     *
     * <p><b>Platform fallbacks (if env variable is missing):</b>
     * <ul>
     *   <li>macOS: {@code /Library/Application Support}</li>
     *   <li>Windows: {@code %PROGRAMDATA%} or {@code C:\ProgramData}</li>
     * </ul>
     */
    DATA_DIRS,

    /**
     * The preference-ordered set of base directories to search for configuration files
     * in addition to the {@link #CONFIG_HOME} base directory.
     *
     * <p>This set of directories is defined by the {@code $XDG_CONFIG_DIRS} environment
     * variable. If the variable is not set, a default equal to {@code /etc/xdg} should be used.
     *
     * <p>The {@link #CONFIG_HOME} directory is considered more important than any of the
     * directories defined by CONFIG_DIRS. Therefore, user config files should be
     * written relative to the {@link #CONFIG_HOME} directory, if possible.
     *
     * <p><b>Platform fallbacks (if env variable is missing):</b>
     * <ul>
     *   <li>macOS: {@code /Library/Application Support}</li>
     *   <li>Windows: {@code %PROGRAMDATA%} or {@code C:\ProgramData}</li>
     * </ul>
     */
    CONFIG_DIRS,

    //*************************************************************************
    // User directory constants (order matters, see isUserDirectory())
    //*************************************************************************

    /**
     * The user's desktop directory.
     *
     * <p>This directory is typically defined by the {@code $XDG_DESKTOP_DIR} variable
     * in the {@code user-dirs.dirs} configuration file.
     *
     * <p>Fallback: {@code $HOME/Desktop} (on macOS and Windows maps directly to this path).
     */
    DESKTOP_DIR,

    /**
     * The user's documents directory.
     *
     * <p>This directory is typically defined by the {@code $XDG_DOCUMENTS_DIR} variable
     * in the {@code user-dirs.dirs} configuration file.
     *
     * <p>Fallback: {@code $HOME/Documents} (on macOS and Windows maps directly to this path).
     */
    DOCUMENTS_DIR,

    /**
     * The user's download directory.
     *
     * <p>This directory is typically defined by the {@code $XDG_DOWNLOAD_DIR} variable
     * in the {@code user-dirs.dirs} configuration file.
     *
     * <p>Fallback: {@code $HOME/Downloads} (on macOS and Windows maps directly to this path).
     */
    DOWNLOAD_DIR,

    /**
     * The user's music directory.
     *
     * <p>This directory is typically defined by the {@code $XDG_MUSIC_DIR} variable
     * in the {@code user-dirs.dirs} configuration file.
     *
     * <p>Fallback: {@code $HOME/Music} (on macOS and Windows maps directly to this path).
     */
    MUSIC_DIR,

    /**
     * The user's pictures directory.
     *
     * <p>This directory is typically defined by the {@code $XDG_PICTURES_DIR} variable
     * in the {@code user-dirs.dirs} configuration file.
     *
     * <p>Fallback: {@code $HOME/Pictures} (on macOS and Windows maps directly to this path).
     */
    PICTURES_DIR,

    /**
     * The user's public share directory.
     *
     * <p>This directory is typically defined by the {@code $XDG_PUBLICSHARE_DIR} variable
     * in the {@code user-dirs.dirs} configuration file.
     *
     * <p>Fallback: {@code $HOME/Public} (on macOS and Windows maps directly to this path).
     */
    PUBLICSHARE_DIR,

    /**
     * The user's templates directory.
     *
     * <p>This directory is typically defined by the {@code $XDG_TEMPLATES_DIR} variable
     * in the {@code user-dirs.dirs} configuration file.
     *
     * <p>Fallback: {@code $HOME/Templates} (on macOS and Windows maps directly to this path).
     */
    TEMPLATES_DIR,

    /**
     * The user's videos directory.
     *
     * <p>This directory is typically defined by the {@code $XDG_VIDEOS_DIR} variable
     * in the {@code user-dirs.dirs} configuration file.
     *
     * <p>Fallback: {@code $HOME/Videos} (on macOS maps to {@code $HOME/Movies}).
     */
    VIDEOS_DIR;

    private static final System.Logger LOGGER = System.getLogger(XDG.class.getName());
    private static volatile Resolver RESOLVER = createDefaultResolver();

    /**
     * Returns the environment variable name associated with this directory.
     *
     * <p>For {@link #HOME}, this returns {@code "HOME"}. For all other directory constants,
     * it prepends {@code "XDG_"} to the enum constant name (e.g., {@code "XDG_DATA_HOME"}).
     *
     * @return the name of the corresponding environment variable
     */
    public String env() {
        return this != HOME ? "XDG_" + name() : name();
    }

    /**
     * Gets the current platform {@link Resolver} instance used to resolve paths.
     *
     * @return the active resolver
     */
    public static Resolver getResolver() {
        return RESOLVER;
    }

    /**
     * Sets a custom platform {@link Resolver}.
     *
     * <p>This method allows overriding the default OS resolution logic.
     *
     * @param resolver the resolver implementation to use
     */
    public static void setResolver(Resolver resolver) {
        RESOLVER = resolver;
    }

    /**
     * Resolves and returns the absolute base path for this XDG directory
     * using the active {@link Resolver}.
     *
     * @return the resolved path for this directory constant
     * @throws IllegalArgumentException if called on multi-directory constants such as {@link #DATA_DIRS}
     *                                  or {@link #CONFIG_DIRS} (use {@link #query(Path)} instead)
     */
    public Path path() {
        return RESOLVER.get(this);
    }

    /**
     * Resolves the given relative subpath against the base path of this XDG directory.
     *
     * @param subpath the relative path to append
     * @return the base path if {@code subpath} is {@code null}, or the resolved path combining the base and subpath
     */
    public Path resolve(@Nullable Path subpath) {
        Path base = RESOLVER.get(this);
        if (subpath == null) {
            return base;
        }

        return base.resolve(subpath);
    }

    /**
     * Resolves the sequence of path segments against the base path of this XDG directory.
     *
     * <p>Empty or whitespace-only elements in {@code subpath} are ignored during resolution.
     *
     * @param subpath the array of path segments to combine with the base path
     * @return the combined resolved path
     */
    public Path resolve(String... subpath) {
        Path base = RESOLVER.get(this);
        return combine(base, subpath);
    }

    /**
     * Queries for existing files or directories by appending the specified subpath
     * to all candidate base paths defined for this XDG directory, filtering out non-existent paths.
     *
     * <p>This method is intended for multi-directory search paths like {@link #DATA_DIRS}
     * and {@link #CONFIG_DIRS}, but works for single-directory bases as well.
     *
     * @param subpath the relative path to look for within the base directories, or {@code null}
     *                to query base directories directly
     * @return a stream of absolute {@link Path} instances that currently exist on the file system
     */
    public Stream<Path> query(@Nullable Path subpath) {
        Stream<Path> baseStream = RESOLVER.stream(this);
        if (subpath == null) {
            return baseStream;
        }

        return baseStream.map(base -> base.resolve(subpath)).filter(Files::exists);
    }

    /**
     * Queries for existing files or directories by appending the given sequence of path segments
     * to all candidate base paths defined for this XDG directory, filtering out non-existent paths.
     *
     * <p>Empty or whitespace-only elements in {@code subpath} are ignored during segment combination.
     *
     * @param subpath the path segments to look for within the base directories, or {@code null}/empty
     *                to query base directories directly
     * @return a stream of absolute {@link Path} instances that currently exist on the file system
     */
    public Stream<Path> query(String @Nullable ... subpath) {
        if (subpath == null || subpath.length == 0) {
            return query((Path) null);
        }

        return query(combine(Path.of(""), subpath));
    }

    /**
     * Checks whether this directory constant represents a user directory (such as {@link #DESKTOP_DIR},
     * {@link #DOCUMENTS_DIR}, etc.) rather than a base specification directory (like {@link #DATA_HOME}).
     *
     * @return {@code true} if this constant is a user directory; {@code false} otherwise
     */
    public boolean isUserDirectory() {
        return this.ordinal() >= XDG.DESKTOP_DIR.ordinal();
    }

    //*************************************************************************

    private static Resolver createDefaultResolver() {
        if (PlatformUtils.isWindows()) {
            return new WindowsResolver();
        }
        if (PlatformUtils.isMac()) {
            return new MacResolver();
        }
        if (PlatformUtils.isUnix()) {
            return new LinuxResolver();
        }

        String name = System.getProperty("os.name", "unknown");
        String arch = System.getProperty("os.arch", "unknown");
        String version = System.getProperty("os.version", "unknown");
        String msg = String.format(
            "Failed to initialize XDG resolver: Operating system '%s' (arch: %s, version: %s) is not supported. "
                + "Custom platform resolvers must be registered manually via XDG.setResolver(...).",
            name, arch, version
        );
        LOGGER.log(System.Logger.Level.ERROR, msg);

        return new LinuxResolver();
    }

    private static Path combine(Path base, String @Nullable [] subpath) {
        if (subpath == null || subpath.length == 0) {
            return base;
        }

        Path result = base;
        for (var sub : subpath) {
            if (!sub.trim().isEmpty()) {
                result = result.resolve(sub);
            }
        }

        return result;
    }

    /**
     * Defines the resolution strategy for XDG directory paths across different operating systems.
     *
     * <p>Implementations are responsible for mapping {@link XDG} directory constants to platform-specific
     * absolute paths, respecting environment variables, system properties, and platform-specific
     * fallback strategies.
     */
    public interface Resolver {

        /**
         * Resolves the single primary absolute path for the given XDG directory constant.
         *
         * @param xdg the XDG directory constant to resolve
         * @return the resolved absolute {@link Path}
         * @throws IllegalArgumentException if called on multi-directory constants such as
         *                                  {@link XDG#DATA_DIRS} or {@link XDG#CONFIG_DIRS}
         */
        Path get(XDG xdg);

        /**
         * Resolves all candidate directory paths for the given XDG directory constant as a stream.
         *
         * <p>For multi-directory constants like {@link XDG#DATA_DIRS} and {@link XDG#CONFIG_DIRS}, this returns
         * all configured candidate locations in order of preference. For single-directory constants, this returns
         * a stream containing only the primary directory.
         *
         * @param xdg the XDG directory constant to resolve
         * @return a stream of candidate paths for the given directory constant
         */
        Stream<Path> stream(XDG xdg);

        /**
         * Retrieves the value of the specified environment variable.
         *
         * <p>This method provides an abstraction over {@link System#getenv(String)} to facilitate
         * mocking and testing under simulated environment configurations.
         *
         * @param name the name of the environment variable
         * @return the string value of the variable, or {@code null} if not defined
         */
        default @Nullable String getEnv(String name) {
            return System.getenv(name);
        }

        /**
         * Retrieves the value of the specified system property.
         *
         * <p>This method provides an abstraction over {@link System#getProperty(String)} to facilitate
         * mocking and testing under simulated system property configurations.
         *
         * @param name the name of the system property
         * @return the string value of the property, or {@code null} if not defined
         */
        default @Nullable String getProperty(String name) {
            return System.getProperty(name);
        }

        /**
         * Resolves the user's home directory.
         *
         * <p>The home directory is retrieved from the {@code user.home} system property first,
         * falling back to the {@code HOME} environment variable if unconfigured or empty.
         * If neither yields a valid value, it falls back to the current directory ({@code "."}).
         * The resulting path is guaranteed to be absolute.
         *
         * @return the absolute {@link Path} representing the user's home directory
         */
        default Path getHome() {
            String home = getProperty("user.home");
            if (home == null || home.trim().isEmpty()) {
                home = getEnv("HOME");
            }
            if (home == null || home.trim().isEmpty()) {
                home = ".";
            }

            Path path = Paths.get(home);
            return path.isAbsolute() ? path : path.toAbsolutePath();
        }

        /**
         * Resolves a single path from the corresponding environment variable for an XDG directory.
         *
         * <p>According to the XDG Base Directory Specification, if the environment variable contains a
         * relative path or is unset/empty, it must be ignored and this method returns {@code null}.
         *
         * @param xdg the XDG directory constant whose environment variable should be read
         * @return an absolute {@link Path} if the environment variable is set to a valid absolute path,
         *         or {@code null} otherwise.
         */
        default @Nullable Path resolve(XDG xdg) {
            if (xdg == HOME) { // special case for $HOME
                return getHome();
            }

            String env = getEnv(xdg.env());
            if (env == null || env.trim().isEmpty()) {
                return null;
            }

            Path path = Paths.get(env); // according to the XDG, relative paths are ignored
            return path.isAbsolute() ? path : null;
        }

        /**
         * Resolves a list of paths from a search-path environment variable (delimited by {@link File#pathSeparator}).
         *
         * <p>According to the XDG specification, empty path entries and relative paths within the variable are ignored.
         *
         * @param xdg the XDG directory constant whose environment variable should be parsed as a list
         * @return a list of absolute {@link Path} objects defined in the environment variable,
         *         or {@code null} if no valid absolute paths were found
         */
        default @Nullable List<Path> resolveList(XDG xdg) {
            if (xdg == HOME) { // special case for $HOME
                return List.of(getHome());
            }

            String env = getEnv(xdg.env());
            if (env == null || env.trim().isEmpty()) {
                return null;
            }

            String[] split = env.split(File.pathSeparator);
            var result = new ArrayList<Path>(split.length);
            for (var p : split) {
                if (!p.trim().isEmpty()) {
                    Path path = Paths.get(p);
                    if (path.isAbsolute()) {
                        result.add(path); // according to the XDG, relative paths are ignored
                    }
                }
            }

            return result.isEmpty() ? null : result;
        }
    }

    //region IMPLEMENTATIONS
    //*************************************************************************
    record WindowsResolver() implements Resolver {

        @Override
        public Path get(XDG xdg) {
            Path path = resolve(xdg);
            if (path != null) {
                return path;
            }

            return switch (xdg) {
                case HOME -> getHome();
                case DATA_HOME, CONFIG_HOME -> {
                    String appData = getEnv("APPDATA");
                    yield (appData != null && !appData.isEmpty())
                        ? Paths.get(appData)
                        : getHome().resolve("AppData/Roaming");
                }
                case CACHE_HOME, STATE_HOME, RUNTIME_DIR -> {
                    String localAppData = getEnv("LOCALAPPDATA");
                    yield (localAppData != null && !localAppData.isEmpty())
                        ? Paths.get(localAppData)
                        : getHome().resolve("AppData/Local");
                }
                case DESKTOP_DIR -> getHome().resolve("Desktop");
                case DOCUMENTS_DIR -> getHome().resolve("Documents");
                case DOWNLOAD_DIR -> getHome().resolve("Downloads");
                case MUSIC_DIR -> getHome().resolve("Music");
                case PICTURES_DIR -> getHome().resolve("Pictures");
                case PUBLICSHARE_DIR -> getHome().resolve("Public");
                case TEMPLATES_DIR -> getHome().resolve("Templates");
                case VIDEOS_DIR -> getHome().resolve("Videos");
                case DATA_DIRS, CONFIG_DIRS -> throw new IllegalArgumentException(String.format(
                    "%s contains multiple directories separated by '%s'. Use XDG.%s.query(...) to search within them.",
                    xdg, File.pathSeparator, xdg
                ));
            };
        }

        @Override
        public Stream<Path> stream(XDG xdg) {
            List<Path> paths = resolveList(xdg);
            if (paths == null) {
                String progData = getEnv("PROGRAMDATA");
                paths = switch (xdg) {
                    case DATA_DIRS, CONFIG_DIRS -> List.of(
                        progData != null && !progData.isEmpty()
                            ? Paths.get(progData)
                            : Paths.get("C:\\ProgramData")
                    );
                    default -> List.of(get(xdg));
                };
            }
            return paths.stream().filter(Files::isDirectory);
        }
    }

    record MacResolver() implements Resolver {

        @Override
        public Path get(XDG xdg) {
            Path path = resolve(xdg);
            if (path != null) {
                return path;
            }

            return switch (xdg) {
                case HOME -> getHome();
                case DATA_HOME, CONFIG_HOME, STATE_HOME -> getHome().resolve("Library/Application Support");
                case CACHE_HOME -> getHome().resolve("Library/Caches");
                case RUNTIME_DIR -> getHome().resolve("Library/Application Support/run");
                case DESKTOP_DIR -> getHome().resolve("Desktop");
                case DOCUMENTS_DIR -> getHome().resolve("Documents");
                case DOWNLOAD_DIR -> getHome().resolve("Downloads");
                case MUSIC_DIR -> getHome().resolve("Music");
                case PICTURES_DIR -> getHome().resolve("Pictures");
                case PUBLICSHARE_DIR -> getHome().resolve("Public");
                case TEMPLATES_DIR -> getHome().resolve("Templates");
                case VIDEOS_DIR -> getHome().resolve("Movies");
                case DATA_DIRS, CONFIG_DIRS -> throw new IllegalArgumentException(String.format(
                    "%s contains multiple directories separated by '%s'. Use XDG.%s.query(...) to search within them.",
                    xdg, File.pathSeparator, xdg
                ));
            };
        }

        @Override
        public Stream<Path> stream(XDG xdg) {
            List<Path> paths = resolveList(xdg);
            if (paths == null) {
                paths = switch (xdg) {
                    case DATA_DIRS, CONFIG_DIRS -> List.of(Paths.get("/Library/Application Support"));
                    default -> List.of(get(xdg));
                };
            }
            return paths.stream().filter(Files::isDirectory);
        }
    }

    record LinuxResolver() implements Resolver {

        private static volatile @Nullable Map<String, Path> USER_DIRS;

        @Override
        public Path get(XDG xdg) {
            if (xdg.isUserDirectory()) {
                Path userDir = getUserDirs().get(xdg.env());
                if (userDir != null) {
                    return userDir;
                }
            }

            Path path = resolve(xdg);
            if (path != null) {
                return path;
            }

            return switch (xdg) {
                case HOME -> getHome();
                case DATA_HOME -> getHome().resolve(".local/share");
                case CONFIG_HOME -> getHome().resolve(".config");
                case CACHE_HOME -> getHome().resolve(".cache");
                case STATE_HOME -> getHome().resolve(".local/state");
                case RUNTIME_DIR -> getHome().resolve(".local/run");
                case DESKTOP_DIR -> getHome().resolve("Desktop");
                case DOCUMENTS_DIR -> getHome().resolve("Documents");
                case DOWNLOAD_DIR -> getHome().resolve("Downloads");
                case MUSIC_DIR -> getHome().resolve("Music");
                case PICTURES_DIR -> getHome().resolve("Pictures");
                case PUBLICSHARE_DIR -> getHome().resolve("Public");
                case TEMPLATES_DIR -> getHome().resolve("Templates");
                case VIDEOS_DIR -> getHome().resolve("Videos");
                case DATA_DIRS, CONFIG_DIRS -> throw new IllegalArgumentException(String.format(
                    "%s contains multiple directories separated by '%s'. Use XDG.%s.query(...) to search within them.",
                    xdg, File.pathSeparator, xdg
                ));
            };
        }

        @Override
        public Stream<Path> stream(XDG xdg) {
            List<Path> paths = resolveList(xdg);
            if (paths == null) {
                paths = switch (xdg) {
                    case DATA_DIRS -> List.of(
                        Paths.get("/usr/local/share/"),
                        Paths.get("/usr/share/")
                    );
                    case CONFIG_DIRS -> List.of(Paths.get("/etc/xdg"));
                    default -> List.of(get(xdg));
                };
            }
            return paths.stream().filter(Files::isDirectory);
        }

        private Map<String, Path> getUserDirs() {
            Map<String, Path> userDirs = USER_DIRS;
            if (userDirs == null) {
                synchronized (LinuxResolver.class) {
                    userDirs = USER_DIRS;
                    if (userDirs == null) {
                        USER_DIRS = userDirs = parseUserDirs();
                    }
                }
            }
            return userDirs;
        }

        private Map<String, Path> parseUserDirs() {
            Path configHome = resolve(CONFIG_HOME);
            if (configHome == null) {
                configHome = getHome().resolve(".config");
            }

            Path userDirsFile = configHome.resolve("user-dirs.dirs");
            if (!Files.exists(userDirsFile)) {
                return Map.of();
            }

            var map = new HashMap<String, Path>();
            try (var reader = Files.newBufferedReader(userDirsFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    int separatorPos = line.indexOf('=');
                    if (separatorPos <= 0) {
                        continue;
                    }

                    String key = line.substring(0, separatorPos).trim();
                    String val = line.substring(separatorPos + 1).trim();

                    if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                        val = val.substring(1, val.length() - 1);
                    }

                    String home = getHome().toString();
                    if (val.startsWith("$HOME")) {
                        val = home + val.substring(5);
                    } else if (val.startsWith("${HOME}")) {
                        val = home + val.substring(7);
                    }

                    Path path = Paths.get(val);

                    if (path.isAbsolute()) { // according to the XDG, relative paths are ignored
                        for (XDG xdg : values()) {
                            if (xdg.isUserDirectory() && xdg.env().equals(key)) {
                                map.put(xdg.env(), path);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                var msg = String.format(
                    "Failed to parse user-dirs.dirs at %s. User directory shortcuts will not be available.",
                    userDirsFile
                );
                LOGGER.log(System.Logger.Level.WARNING, msg, e);
                return Map.of();
            }

            return map;
        }

        static void resetUserDirs() {
            synchronized (LinuxResolver.class) {
                USER_DIRS = null;
            }
        }
    }
    //endregion
}