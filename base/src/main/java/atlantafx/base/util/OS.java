/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import org.jspecify.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Provides utility methods to interact with the operating system.
 */
public final class OS {

    private OS() {
        // utility
    }

    private static final String NAME = System.getProperty("os.name", "generic").toLowerCase();
    private static final boolean WINDOWS = NAME.startsWith("windows");
    private static final boolean MAC = NAME.contains("mac") || NAME.contains("darwin");
    private static final boolean LINUX = NAME.startsWith("linux");
    private static final boolean FREE_BSD = NAME.startsWith("freebsd");
    private static final boolean OPEN_BSD = NAME.startsWith("openbsd");
    private static final boolean NET_BSD = NAME.startsWith("netbsd");
    private static final boolean SOLARIS = NAME.startsWith("sunos") || NAME.startsWith("solaris");
    private static final boolean AIX = NAME.startsWith("aix");

    /**
     * Returns the raw, lower-cased value of the {@code os.name} system property.
     *
     * @return the operating system name in lower case, or {@code "generic"} if the
     *         {@code os.name} property is unavailable
     */
    public static String getName() {
        return NAME;
    }

    /**
     * Checks whether the application is running on Windows.
     *
     * @return {@code true} if the current OS is Windows
     */
    public static boolean isWindows() {
        return WINDOWS;
    }

    /**
     * Checks whether the application is running on macOS.
     *
     * @return {@code true} if the current OS is macOS
     */
    public static boolean isMac() {
        return MAC;
    }

    /**
     * Checks whether the application is running on Linux.
     *
     * <p>Note: Chrome OS also reports {@code os.name = "Linux"} when running a JVM
     * inside its Linux container (Crostini), so this method returns {@code true} there
     * as well. The same applies to WSL (Windows Subsystem for Linux).
     *
     * @return {@code true} if the current OS is Linux
     */
    public static boolean isLinux() {
        return LINUX;
    }

    /**
     * Checks whether the current OS belongs to the BSD family.
     *
     * @return {@code true} if the current OS is FreeBSD, OpenBSD, or NetBSD
     */
    public static boolean isBSD() {
        return FREE_BSD || OPEN_BSD || NET_BSD;
    }

    /**
     * Checks whether the current OS belongs to the UNIX-like family.
     *
     * <p>Note: macOS is intentionally excluded from this check, even though its kernel
     * (Darwin) is BSD-based. Use {@link #isMac()} separately if macOS should also be
     * treated as UNIX-like.
     *
     * @return {@code true} if the current OS is Linux, a BSD variant, Solaris, or AIX
     */
    public static boolean isUnix() {
        return LINUX || FREE_BSD || OPEN_BSD || NET_BSD || SOLARIS || AIX;
    }

    //region RESOURCE HANDLER

    private static volatile ResourceHandler RESOURCE_HANDLER = createDefaultResourceHandler();

    /**
     * Checks if the system can open the specified file path.
     *
     * @param path the file path to check
     * @return {@code true} if the path can be opened, {@code false} otherwise
     */
    public static boolean canBrowse(Path path) {
        return canBrowse(path.toUri());
    }

    /**
     * Checks if the system can open the specified URI using default allowed schemes.
     *
     * @param uri the URI to check
     * @return {@code true} if the URI can be opened, {@code false} otherwise
     */
    public static boolean canBrowse(URI uri) {
        return canBrowse(uri, ResourceHandler.DEFAULT_ALLOWED_SCHEMES);
    }

    /**
     * Checks if the system can open the specified URI using a custom set of allowed schemes.
     *
     * @param uri the URI to check
     * @param allowedSchemes the set of permitted URI schemes
     * @return {@code true} if the URI is supported and can be opened, {@code false} otherwise
     */
    public static boolean canBrowse(URI uri, Set<String> allowedSchemes) {
        return RESOURCE_HANDLER.supports(uri, allowedSchemes);
    }

    /**
     * Opens the specified path in the default application for the current platform.
     *
     * @param path the file path to open
     * @throws IOException if an I/O error occurs or if the path cannot be opened
     */
    public static void browse(Path path) throws IOException {
        RESOURCE_HANDLER.browse(path.toUri());
    }

    /**
     * Opens the specified URI in the default application for the current platform.
     *
     * @param uri the URI to open
     * @throws IOException if an I/O error occurs or if the URI cannot be opened
     */
    public static void browse(URI uri) throws IOException {
        RESOURCE_HANDLER.browse(uri);
    }

    /**
     * Returns the active resource handler chosen automatically for the operating system.
     *
     * @return the current {@link ResourceHandler}
     */
    public static ResourceHandler getResourceHandler() {
        return RESOURCE_HANDLER;
    }

    /**
     * Sets a custom resource handler for opening URIs and files.
     *
     * @param handler the new handler to set
     */
    public static void setResourceHandler(ResourceHandler handler) {
        RESOURCE_HANDLER = handler;
    }

    private static ResourceHandler createDefaultResourceHandler() {
        if (OS.isWindows()) {
            return WindowsShell32Handler.create();
        }

        if (OS.isMac()) {
            return new MacOpenHandler();
        }

        if (OS.isUnix()) {
            LinuxGIOHandler gioHandler = LinuxGIOHandler.create();
            // try GIO (FFM), fallback to xdg-open if libraries are missing
            if (gioHandler.available()) {
                return gioHandler;
            }
            return new LinuxXDGHandler();
        }

        return new UnsupportedHandler();
    }

    /**
     * Starts an external process with a single argument without waiting for it to finish.
     *
     * @param command the executable command to run
     * @param arg the argument to pass to the command
     * @throws IOException if the process fails to start
     */
    public static void spawn(String command, String arg) throws IOException {
        try {
            new ProcessBuilder(command, arg)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                // intentionally not calling waitFor() as xdg-open/open often spawn
                // long-lived processes (browser) and are not required to exit immediately.
                .start();
        } catch (IOException e) {
            throw new IOException(
                String.format("Failed to start ProcessBuilder for '%s' with arg '%s'", command, arg), e
            );
        }
    }

    //*************************************************************************

    /**
     * Defines an interface for platform-specific implementations to open resources.
     */
    public interface ResourceHandler {

        /** The default set of allowed URI schemes. */
        Set<String> DEFAULT_ALLOWED_SCHEMES = Set.of("http", "https", "file", "mailto");

        /**
         * Checks if this handler can run on the current system.
         *
         * @return {@code true} if the handler is supported, {@code false} otherwise
         */
        boolean available();

        /**
         * Opens the specified URI if it passes security checks and is supported.
         *
         * @param uri the URI to open
         * @param allowedSchemes the set of allowed schemes
         * @throws IOException if the URI cannot be opened or does not pass security rules
         */
        void browse(URI uri, Set<String> allowedSchemes) throws IOException;

        //*************************************************************************

        /**
         * Opens the specified URI using default allowed schemes.
         *
         * @param uri the URI to open
         * @throws IOException if the URI cannot be opened
         */
        default void browse(URI uri) throws IOException {
            browse(uri, DEFAULT_ALLOWED_SCHEMES);
        }

        /**
         * Checks if the handler supports opening the given URI.
         *
         * @param uri the URI to evaluate
         * @param allowedSchemes the allowed URI schemes
         * @return {@code true} if the URI is supported, {@code false} otherwise
         */
        default boolean supports(URI uri, Set<String> allowedSchemes) {
            if (!available()) {
                return false;
            }

            String scheme = uri.getScheme();
            if (scheme == null) {
                return false;
            }

            scheme = scheme.toLowerCase(Locale.ROOT);
            if (!allowedSchemes.contains(scheme)) {
                return false;
            }

            if (scheme.equals("file")) {
                var fileUri = toFileURI(uri);
                return fileUri != null && Files.exists(Path.of(fileUri));
            }

            return true;
        }

        /**
         * Validates that a URI is supported before opening it.
         *
         * @param uri the URI to validate
         * @param allowedSchemes the set of allowed schemes
         * @throws IOException if the handler is unavailable or if the file does not exist
         * @throws IllegalArgumentException if the URI format is invalid
         * @throws SecurityException if the scheme is not allowed
         */
        default void requireSupport(URI uri, Set<String> allowedSchemes) throws IOException {
            Objects.requireNonNull(uri, "URI must not be null");

            if (!available()) {
                throw new IOException(
                    "System handler is unavailable for this platform (" + getClass().getSimpleName() + ")"
                );
            }

            String scheme = uri.getScheme();
            if (scheme == null) {
                throw new IllegalArgumentException("URI scheme is missing: " + uri);
            }

            scheme = scheme.toLowerCase(Locale.ROOT);
            if (!allowedSchemes.contains(scheme)) {
                throw new SecurityException(String.format(
                    "URI scheme '%s' is rejected by security policy. Allowed schemes: %s", scheme, allowedSchemes
                ));
            }

            if (scheme.equals("file")) {
                var fileUri = toFileURI(uri);
                if (fileUri == null) {
                    throw new IllegalArgumentException("Malformed or unsupported file URI format: " + uri);
                }
                if (!Files.exists(Path.of(fileUri))) {
                    throw new FileNotFoundException("File does not exist: " + fileUri);
                }
            }
        }

        /**
         * Converts a general URI to a valid file URI if possible.
         *
         * @param uri the URI to convert
         * @return the valid file URI, or {@code null} if conversion fails
         */
        default @Nullable URI toFileURI(URI uri) {
            if (!"file".equalsIgnoreCase(uri.getScheme())) {
                return null;
            }

            try {
                uri = OS.isWindows()
                    ? new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null)
                    : new URI(uri.getScheme(), null, uri.getPath(), null, null);
                try {
                    var noop = Path.of(uri);
                    return uri;
                } catch (IllegalArgumentException e) {
                    return OS.isWindows()
                        ? new URI(uri.getScheme(), null, uri.getPath(), null, null)
                        : null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Converts a URI into a normalized string suitable for passing to an external API or command.
         *
         * @param uri the URI to normalize
         * @return the normalized string representation of the URI
         */
        default String normalize(URI uri) {
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                // file URI (open in web browser)
                if (uri.getFragment() != null || uri.getQuery() != null) {
                    return uri.toString();
                }

                var fileUri = toFileURI(uri);
                if (fileUri != null) { // file path (open in associated app)
                    return Path.of(fileUri).toString();
                }
            }
            return uri.toString();
        }
    }

    /**
     * Windows-specific resource handler that opens URIs via native ShellExecuteW API.
     */
    private record WindowsShell32Handler(@Nullable MethodHandle shellExecuteHandle) implements ResourceHandler {

        static OS.WindowsShell32Handler create() {
            try {
                Linker linker = Linker.nativeLinker();
                SymbolLookup shell32 = SymbolLookup.libraryLookup("shell32.dll", Arena.global());
                MethodHandle handle = linker.downcallHandle(
                    shell32.find("ShellExecuteW").orElseThrow(),
                    FunctionDescriptor.of(
                        ValueLayout.ADDRESS,  // HINSTANCE (return value)
                        ValueLayout.ADDRESS,  // hwnd
                        ValueLayout.ADDRESS,  // lpOperation
                        ValueLayout.ADDRESS,  // lpFile
                        ValueLayout.ADDRESS,  // lpParameters
                        ValueLayout.ADDRESS,  // lpDirectory
                        ValueLayout.JAVA_INT  // nShowCmd
                    )
                );
                return new OS.WindowsShell32Handler(handle);
            } catch (Throwable _) {
                return new OS.WindowsShell32Handler(null);
            }
        }

        @Override
        public boolean available() {
            return shellExecuteHandle != null;
        }

        @Override
        public void browse(URI uri, Set<String> allowedSchemes) throws IOException {
            requireSupport(uri, allowedSchemes);
            Objects.requireNonNull(shellExecuteHandle, "ShellExecuteW handle is not initialized");
            invokeNative(normalize(uri), shellExecuteHandle);
        }

        private static void invokeNative(String uri, MethodHandle handle) throws IOException {
            // Windows API requires UTF-16LE strings with null terminator
            byte[] operationBytes = ("open\0").getBytes(StandardCharsets.UTF_16LE);
            byte[] targetBytes = (uri + "\0").getBytes(StandardCharsets.UTF_16LE);

            try (Arena arena = Arena.ofConfined()) {
                MemorySegment operationCString = arena.allocateFrom(ValueLayout.JAVA_BYTE, operationBytes);
                MemorySegment targetCString = arena.allocateFrom(ValueLayout.JAVA_BYTE, targetBytes);

                MemorySegment hInstApp = (MemorySegment) handle.invokeExact(
                    MemorySegment.NULL, operationCString, targetCString,
                    MemorySegment.NULL, MemorySegment.NULL, 1
                );

                if (hInstApp.address() <= 32) { // return codes <= 32 indicate system errors
                    throw new IOException(String.format(
                        "ShellExecuteW failed. Error code: %d. Target: %s", hInstApp.address(), uri
                    ));
                }
            } catch (IOException e) {
                throw e;
            } catch (Throwable t) {
                throw new IOException("Native call to ShellExecuteW encountered a fatal error", t);
            }
        }
    }

    /**
     * macOS-specific resource handler that opens URIs via the native {@code /usr/bin/open} command.
     */
    private record MacOpenHandler() implements ResourceHandler {

        private static final Path OPEN_BIN = Path.of("/usr/bin/open");

        @Override
        public boolean available() {
            return Files.isExecutable(OPEN_BIN);
        }

        @Override
        public void browse(URI uri, Set<String> allowedSchemes) throws IOException {
            requireSupport(uri, allowedSchemes);
            spawn(OPEN_BIN.toString(), normalize(uri));
        }
    }

    /**
     * Linux resource handler that opens URIs via the {@code xdg-open} command.
     */
    private record LinuxXDGHandler(boolean available) implements ResourceHandler {

        public LinuxXDGHandler() {
            this(which("xdg-open") != null);
        }

        @Override
        public boolean available() {
            return available;
        }

        @Override
        public void browse(URI uri, Set<String> allowedSchemes) throws IOException {
            requireSupport(uri, allowedSchemes);
            spawn("xdg-open", normalize(uri));
        }

        private static @Nullable Path which(String executable) {
            String env = System.getenv("PATH");
            if (env == null) {
                return null;
            }

            for (String dir : env.split(java.io.File.pathSeparator)) {
                Path candidate = Path.of(dir, executable);
                if (Files.isRegularFile(candidate) && Files.isExecutable(candidate)) {
                    return candidate;
                }
            }
            return null;
        }
    }

    /**
     * Linux resource handler that opens URIs via the GIO native library.
     */
    private record LinuxGIOHandler(@Nullable MethodHandle launchHandle,
                                   @Nullable MethodHandle errorFreeHandle) implements ResourceHandler {

        // struct GError { guint32 domain; gint32 code; gchar *message; }
        private static final int MESSAGE_OFFSET = 8;

        static OS.LinuxGIOHandler create() {
            try {
                Linker linker = Linker.nativeLinker();
                SymbolLookup gio = SymbolLookup.libraryLookup("libgio-2.0.so.0", Arena.global());

                MethodHandle launch = linker.downcallHandle(
                    gio.find("g_app_info_launch_default_for_uri").orElseThrow(),
                    FunctionDescriptor.of(
                        ValueLayout.JAVA_INT,  // gboolean
                        ValueLayout.ADDRESS,   // const gchar *uri
                        ValueLayout.ADDRESS,   // GAppLaunchContext *context (NULL ok)
                        ValueLayout.ADDRESS    // GError **error
                    )
                );
                MethodHandle free = linker.downcallHandle(
                    gio.find("g_error_free").orElseThrow(),
                    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
                );
                return new OS.LinuxGIOHandler(launch, free);
            } catch (Throwable t) {
                // headless environment without graphical subsystem or missing libgio
                return new OS.LinuxGIOHandler(null, null);
            }
        }

        @Override
        public boolean available() {
            return launchHandle != null && errorFreeHandle != null;
        }

        @Override
        public void browse(URI uri, Set<String> allowedSchemes) throws IOException {
            requireSupport(uri, allowedSchemes);
            invokeNative(uri.toString());
        }

        private void invokeNative(String uri) throws IOException {
            Objects.requireNonNull(launchHandle, "GIO launch_default_for_uri handle is null");
            Objects.requireNonNull(errorFreeHandle, "GIO error_free handle is null");

            try (Arena arena = Arena.ofConfined()) {
                MemorySegment uriCString = arena.allocateFrom(uri);
                // allocate memory for a pointer to GError structure (GError**)
                MemorySegment gErrorPtrHolder = arena.allocate(ValueLayout.ADDRESS);

                int isSuccess = (int) launchHandle.invokeExact(uriCString, MemorySegment.NULL, gErrorPtrHolder);

                if (isSuccess == 0) { // 0 in glib means FALSE
                    MemorySegment gErrorStructAddress = gErrorPtrHolder.get(ValueLayout.ADDRESS, 0);
                    String errorMessage = "Unknown GError";

                    if (!gErrorStructAddress.equals(MemorySegment.NULL)) {
                        try {
                            MemorySegment gErrorStruct = gErrorStructAddress.reinterpret(MESSAGE_OFFSET
                                + ValueLayout.ADDRESS.byteSize());
                            MemorySegment errorMessagePtr = gErrorStruct.get(ValueLayout.ADDRESS, MESSAGE_OFFSET);

                            if (!errorMessagePtr.equals(MemorySegment.NULL)) {
                                errorMessage = errorMessagePtr.reinterpret(Long.MAX_VALUE).getString(0);
                            }
                        } finally {
                            errorFreeHandle.invokeExact(gErrorStructAddress);
                        }
                    }
                    throw new IOException(
                        String.format("g_app_info_launch_default_for_uri failed to open '%s': %s", uri, errorMessage)
                    );
                }
            } catch (IOException e) {
                throw e;
            } catch (Throwable t) {
                throw new IOException("Native call to GLib/GIO encountered a fatal error", t);
            }
        }
    }

    /**
     * Represents a handler for platforms that do not support opening resources.
     */
    public record UnsupportedHandler() implements ResourceHandler {

        @Override
        public boolean available() {
            return false;
        }

        @Override
        public void browse(URI uri, Set<String> allowedSchemes) throws IOException {
            throw new IOException("Resource opening is not supported on this platform: " + NAME);
        }
    }
    //endregion
}
