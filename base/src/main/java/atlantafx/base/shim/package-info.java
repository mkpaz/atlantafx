/**
 * Unfortunately, unlike Swing, JavaFX project has chosen to make all of its internals
 * private and final. This effectively prevents users from fixing bugs and implementing
 * missing functionality in their applications.
 *
 * <p>This package contains copy-pasted classes that are not exported by OpenJFX but are
 * mandatory for some control development.
 *
 * <p>Relevant issues:
 * <ul>
 * <li><a href="https://bugs.openjdk.org/browse/JDK-8350921">JDK-8350921</a></li>
 * </ul>
 */

@NullUnmarked
package atlantafx.base.shim;

import org.jspecify.annotations.NullUnmarked;
