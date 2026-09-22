package atlantafx.sampler;

import atlantafx.sampler.fake.domain.Metric;
import atlantafx.sampler.fake.domain.Product;
import devtoolsfx.gui.GUI;
import net.datafaker.Code;
import net.datafaker.Faker;
import net.datafaker.Job;
import net.datafaker.Name;
import us.hebi.graalvm.reachability.annotations.MemberAccess;
import us.hebi.graalvm.reachability.annotations.Reachable;

// Faker data generation
@Reachable(condition = Faker.class,
        classes = {Faker.class, Name.class, Code.class, Metric.class, Product.class, Job.class},
        resources = {"/*.yml", "/en/**"}
)

// Lib: Ikonli icon fonts
@Reachable(condition = org.kordamp.ikonli.javafx.IkonResolver.class,
        classes = org.kordamp.ikonli.javafx.IkonResolver.class,
        resources = {
                "/META-INF/services/org.kordamp.ikonli.IkonHandler",
                "/META-INF/resources/*/*/fonts/*.ttf",
        }
)

// Lib: devtoolsfx (that needs access to pretty much everything though, so it's not really working)
@Reachable(condition = GUI.class, classNames = "devtoolsfx.event.ConnectorEvent", memberAccess = {})

// AWT: musicplayer/Utils, layout/MainLayer
@Reachable(condition = javafx.embed.swing.SwingFXUtils.class, jniAccessible = true,
        classes = {
                java.awt.GraphicsEnvironment.class,
                java.awt.image.BufferedImage.class,
                java.awt.image.ColorModel.class,
                java.awt.image.Raster.class,
                java.awt.image.SampleModel.class,
                java.awt.image.SinglePixelPackedSampleModel.class,
                java.lang.System.class
        },
        classNames = "sun.awt.image.IntegerComponentRaster",
        memberAccess = {MemberAccess.ALL_DECLARED_FIELDS, MemberAccess.ALL_DECLARED_METHODS}
)

// AWT: layout/MainLayer
@Reachable(condition = javax.imageio.ImageIO.class, jniAccessible = true,
        classes = {java.awt.Insets.class, java.awt.Toolkit.class},
        classNames = "sun.java2d.Disposer"
)
@Reachable(condition = javax.imageio.ImageIO.class,
        classes = {javax.imageio.spi.ImageReaderSpi.class, javax.imageio.spi.ImageWriterSpi.class},
        memberAccess = {},
        resources = "/META-INF/services/javax.imageio.spi.*",
        bundles = "/sun.awt.resources.awt"
)

// AWT: filemanager/Utils
@Reachable(condition = java.awt.Desktop.class, jniAccessible = true,
        classes = {
                java.awt.AWTEvent.class,
                java.awt.AlphaComposite.class,
                java.awt.Color.class,
                java.awt.Component.class,
                java.awt.Desktop.Action.class,
                java.awt.Font.class,
                java.awt.GraphicsEnvironment.class,
                java.awt.Insets.class,
                java.awt.Rectangle.class,
                java.awt.Toolkit.class,
                java.awt.desktop.UserSessionEvent.Reason.class,
                java.awt.event.InputEvent.class,
                java.awt.geom.AffineTransform.class,
                java.awt.geom.Path2D.class,
                java.awt.geom.Path2D.Float.class,
                java.awt.image.ColorModel.class,
                java.awt.image.IndexColorModel.class,
                java.lang.System.class
        },
        classNames = {
                "sun.awt.AWTAutoShutdown",
                "sun.awt.SunHints",
                "sun.awt.SunToolkit",
                "sun.awt.X11.XDesktopPeer",
                "sun.awt.X11.XErrorHandlerUtil",
                "sun.awt.X11.XToolkit",
                "sun.awt.image.SunVolatileImage",
                "sun.awt.image.VolatileSurfaceManager",
                "sun.awt.shell.Win32ShellFolderManager2",
                "sun.awt.windows.WComponentPeer",
                "sun.awt.windows.WDesktopPeer",
                "sun.awt.windows.WObjectPeer",
                "sun.awt.windows.WToolkit",
                "sun.java2d.Disposer",
                "sun.java2d.InvalidPipeException",
                "sun.java2d.NullSurfaceData",
                "sun.java2d.SunGraphics2D",
                "sun.java2d.SurfaceData",
                "sun.java2d.loops.Blit",
                "sun.java2d.loops.BlitBg",
                "sun.java2d.loops.CompositeType",
                "sun.java2d.loops.DrawGlyphList",
                "sun.java2d.loops.DrawGlyphListAA",
                "sun.java2d.loops.DrawGlyphListLCD",
                "sun.java2d.loops.DrawLine",
                "sun.java2d.loops.DrawParallelogram",
                "sun.java2d.loops.DrawPath",
                "sun.java2d.loops.DrawPolygons",
                "sun.java2d.loops.DrawRect",
                "sun.java2d.loops.FillParallelogram",
                "sun.java2d.loops.FillPath",
                "sun.java2d.loops.FillRect",
                "sun.java2d.loops.FillSpans",
                "sun.java2d.loops.GraphicsPrimitive",
                "sun.java2d.loops.GraphicsPrimitive[]",
                "sun.java2d.loops.GraphicsPrimitiveMgr",
                "sun.java2d.loops.MaskBlit",
                "sun.java2d.loops.MaskFill",
                "sun.java2d.loops.ScaledBlit",
                "sun.java2d.loops.SurfaceType",
                "sun.java2d.loops.TransformHelper",
                "sun.java2d.loops.XORComposite",
                "sun.java2d.marlin.DMarlinRenderingEngine",
                "sun.java2d.pipe.Region",
                "sun.java2d.pipe.RegionIterator",
                "sun.java2d.xr.XRSurfaceData"
        },
        bundles = {"/sun.awt.resources.awt", "/sun.awt.resources.awtosx"}
)
public class LibMetadata {
}
