package atlantafx.base.util;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * Represents an (optionally) observable color.
 *
 * <p>The base color implementation is both mutable and observable and is used to reduce
 * the number of allocations. It is stored internally as an 64-bit packed integer providing
 * 16 bits for each RGBA color channel, which is more than enough for the majority of operations.
 * Observability is an option; not using it costs nothing.
 *
 * <p>For the purpose of an immutable color data holder, any of the following models can be chosen:
 * {@link RGB}, {@link HSB}, {@link HSL}. Converting between color models costs some precision due to
 * floating-point operations, but unless it's done a hundred times in a row, it is negligible.
 *
 * <p>This class (and concrete models) also provides a variety of color formatting and
 * conversion options.
 */
public class Colour implements ObservableValue<Colour> {

    protected static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    protected static final int PARSE_COMPONENT = 0;
    protected static final int PARSE_PERCENT = 1;
    protected static final int PARSE_ANGLE = 2;
    protected static final int PARSE_ALPHA = 3;

    // maximum value for a 16-bit color channel.
    protected static final long MAX_16BIT_L = 65535L;
    protected static final double MAX_16BIT_D = 65535.0;

    // internal color representation (0xRRRRGGGGBBBBAAAA format, 16 bits per channel)
    protected long rgba;

    // observable
    private @Nullable Colour oldState = null;
    private @Nullable List<InvalidationListener> invalidationListeners = null;
    private @Nullable List<ChangeListener<? super Colour>> changeListeners = null;

    /**
     * Creates a color using a packed raw value.
     *
     * @param rgba 64-bit integer containing RGBA channels
     */
    public Colour(long rgba) {
        this.rgba = rgba;
    }

    /**
     * Creates a color using floating-point components.
     *
     * @param red     red channel value from 0.0 to 1.0
     * @param green   green channel value from 0.0 to 1.0
     * @param blue    blue channel value from 0.0 to 1.0
     * @param opacity opacity value from 0.0 to 1.0
     */
    public Colour(double red, double green, double blue, double opacity) {
        this.rgba = pack16(red, green, blue, opacity);
    }

    /**
     * Creates a color using integer components.
     *
     * @param red   red channel value from 0 to 255
     * @param green green channel value from 0 to 255
     * @param blue  blue channel value from 0 to 255
     * @param alpha alpha value from 0 to 255
     */
    public Colour(int red, int green, int blue, int alpha) {
        this.rgba = pack16(red, green, blue, alpha);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Colour c)) {
            return false;
        }
        return this.rgba == c.rgba;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(rgba);
    }

    @Override
    public String toString() {
        return toHex(true);
    }

    /**
     * Creates a copy of this color.
     */
    public Colour copy() {
        return new Colour(rgba);
    }

    //region ACCESSORS
    //*************************************************************************

    /**
     * Returns the red channel value.
     *
     * @return red component from 0.0 to 1.0
     */
    public double getRed() {
        return ((rgba >>> 48) & 0xFFFFL) / MAX_16BIT_D;
    }

    /**
     * Returns the red channel value as an integer.
     *
     * @return red component from 0 to 255
     */
    public int getRedInt() {
        return (int) Math.round(((rgba >>> 48) & 0xFFFFL) * 255.0 / MAX_16BIT_D);
    }

    /**
     * Sets the red channel value.
     *
     * @param red red component from 0.0 to 1.0
     */
    public void setRed(double red) {
        long next = (rgba & 0x0000FFFFFFFFFFFFL) | (packDouble("Red", red) << 48);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Sets the red channel value using an integer.
     *
     * @param red red component from 0 to 255
     */
    public void setRedInt(int red) {
        long next = (rgba & 0x0000FFFFFFFFFFFFL) | (packInt("Red", red) << 48);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Returns the green channel value.
     *
     * @return green component from 0.0 to 1.0
     */
    public double getGreen() {
        return ((rgba >>> 32) & 0xFFFFL) / MAX_16BIT_D;
    }

    /**
     * Returns the green channel value as an integer.
     *
     * @return green component from 0 to 255
     */
    public int getGreenInt() {
        return (int) Math.round(((rgba >>> 32) & 0xFFFFL) * 255.0 / MAX_16BIT_D);
    }

    /**
     * Sets the green channel value.
     *
     * @param green green component from 0.0 to 1.0
     */
    public void setGreen(double green) {
        long next = (rgba & 0xFFFF0000FFFFFFFFL) | (packDouble("Green", green) << 32);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Sets the green channel value using an integer.
     *
     * @param green green component from 0 to 255
     */
    public void setGreenInt(int green) {
        long next = (rgba & 0xFFFF0000FFFFFFFFL) | (packInt("Green", green) << 32);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Returns the blue channel value.
     *
     * @return blue component from 0.0 to 1.0
     */
    public double getBlue() {
        return ((rgba >>> 16) & 0xFFFFL) / MAX_16BIT_D;
    }

    /**
     * Returns the blue channel value as an integer.
     *
     * @return blue component from 0 to 255
     */
    public int getBlueInt() {
        return (int) Math.round(((rgba >>> 16) & 0xFFFFL) * 255.0 / MAX_16BIT_D);
    }

    /**
     * Sets the blue channel value.
     *
     * @param blue blue component from 0.0 to 1.0
     */
    public void setBlue(double blue) {
        long next = (rgba & 0xFFFFFFFF0000FFFFL) | (packDouble("Blue", blue) << 16);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Sets the blue channel value using an integer.
     *
     * @param blue blue component from 0 to 255
     */
    public void setBlueInt(int blue) {
        long next = (rgba & 0xFFFFFFFF0000FFFFL) | (packInt("Blue", blue) << 16);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Returns the alpha value as an integer.
     *
     * @return alpha component from 0 to 255
     */
    public int getAlpha() {
        return (int) Math.round((rgba & 0xFFFFL) * 255.0 / MAX_16BIT_D);
    }

    /**
     * Sets the alpha value using an integer.
     *
     * @param alpha alpha component from 0 to 255
     */
    public void setAlpha(int alpha) {
        long next = (rgba & 0xFFFFFFFFFFFF0000L) | packInt("Alpha", alpha);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Returns the opacity value.
     *
     * @return opacity component from 0.0 to 1.0
     */
    public double getOpacity() {
        return (rgba & 0xFFFFL) / MAX_16BIT_D;
    }

    /**
     * Sets the opacity value.
     *
     * @param opacity opacity component from 0.0 to 1.0
     */
    public void setOpacity(double opacity) {
        long next = (rgba & 0xFFFFFFFFFFFF0000L) | packDouble("Opacity", opacity);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Sets color components using floating-point values.
     * Negative values preserve current channel state.
     *
     * @param red   red component from 0.0 to 1.0, or negative to ignore
     * @param green green component from 0.0 to 1.0, or negative to ignore
     * @param blue  blue component from 0.0 to 1.0, or negative to ignore
     */
    public void setRGB(double red, double green, double blue) {
        long r = (red < 0.0) ? ((rgba >>> 48) & 0xFFFFL) : packDouble("Red", red);
        long g = (green < 0.0) ? ((rgba >>> 32) & 0xFFFFL) : packDouble("Green", green);
        long b = (blue < 0.0) ? ((rgba >>> 16) & 0xFFFFL) : packDouble("Blue", blue);
        long a = rgba & 0xFFFFL;

        long next = pack16(r, g, b, a);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Sets color components using integer values.
     * Negative values preserve current channel state.
     *
     * @param red   red component from 0 to 255, or negative to ignore
     * @param green green component from 0 to 255, or negative to ignore
     * @param blue  blue component from 0 to 255, or negative to ignore
     */
    public void setRGB(int red, int green, int blue) {
        long r = (red < 0) ? ((rgba >>> 48) & 0xFFFFL) : packInt("Red", red);
        long g = (green < 0) ? ((rgba >>> 32) & 0xFFFFL) : packInt("Green", green);
        long b = (blue < 0) ? ((rgba >>> 16) & 0xFFFFL) : packInt("Blue", blue);
        long a = rgba & 0xFFFFL;

        long next = pack16(r, g, b, a);
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }

    /**
     * Sets color components using floating-point values.
     */
    public void setRGB(RGB rgb) {
        long next = pack16(rgb.red(), rgb.green(), rgb.blue(), rgb.opacity());
        if (rgba != next) {
            long prev = rgba;
            rgba = next;
            fireValueChanged(prev);
        }
    }
    //endregion

    //region UTILS

    /**
     * Checks if this color is completely opaque.
     *
     * @return {@code true} if opacity is fully set, otherwise {@code false}
     */
    public boolean isOpaque() {
        return (rgba & 0xFFFFL) >= MAX_16BIT_L;
    }

    /**
     * Checks if this color has any degree of transparency.
     *
     * @return {@code true} if opacity is less than fully opaque, otherwise {@code false}
     */
    public boolean isTranslucent() {
        return (rgba & 0xFFFFL) < MAX_16BIT_L;
    }

    /**
     * Checks if this color is completely transparent.
     *
     * @return {@code true} if opacity is zero, otherwise {@code false}
     */
    public boolean isTransparent() {
        return (rgba & 0xFFFFL) == 0L;
    }

    /**
     * Measures relative color luminance according to the
     * <a href="https://www.w3.org/TR/WCAG20-TECHS/G18.html">W3C</a>.
     *
     * <p>Note that {@link HSB#brightness()} is not the same thing as luminance.
     *
     * @return relative luminance value from 0.0 (darkest black) to 1.0 (lightest white)
     */
    public double getLuminance() {
        double r = convertChannelToLinear(getRed());
        double g = convertChannelToLinear(getGreen());
        double b = convertChannelToLinear(getBlue());

        return (0.2126 * r) + (0.7152 * g) + (0.0722 * b);
    }

    /**
     * Calculates the WCAG contrast ratio between this color and another color.
     *
     * @param other the other color to compare against
     * @return contrast ratio in range [1.0, 21.0]
     */
    public double getContrastRatioTo(Colour other) {
        return ContrastLevel.getContrastRatio(this, other);
    }
    //endregion

    //region FACTORIES
    //*************************************************************************

    /** See {@link #of(String, double)}. */
    public static Colour of(String color) {
        return of(color, 1.0);
    }

    /**
     * Creates a color by parsing a string specification.
     * Supported formats include hex, RGB, and HSL strings.
     *
     * @param color   text representation of a color
     * @param opacity opacity scaling factor from 0.0 to 1.0
     * @throws IllegalArgumentException if the text format is invalid
     */
    public static Colour of(String color, double opacity) {
        if (color.isEmpty()) {
            throw new IllegalArgumentException("Color string cannot be empty.");
        }

        color = color.toLowerCase(Locale.ROOT);

        if (color.startsWith("#")) {
            color = color.substring(1);
        } else if (color.startsWith("0x")) {
            color = color.substring(2);
        } else if (color.startsWith("rgb")) {
            if (color.startsWith("(", 3)) {
                return parseRGB(color, 4, false, opacity);
            } else if (color.startsWith("a(", 3)) {
                return parseRGB(color, 5, true, opacity);
            }
        } else if (color.startsWith("hsl")) {
            if (color.startsWith("(", 3)) {
                return parseHSB(color, 4, false, opacity);
            } else if (color.startsWith("a(", 3)) {
                return parseHSB(color, 5, true, opacity);
            }
        }

        int len = color.length();
        try {
            int r, g, b, a;
            if (len == 3) {
                r = Integer.parseInt(color.substring(0, 1), 16);
                g = Integer.parseInt(color.substring(1, 2), 16);
                b = Integer.parseInt(color.substring(2, 3), 16);
                return Colour.rgb(r / 15.0, g / 15.0, b / 15.0, opacity);
            } else if (len == 4) {
                r = Integer.parseInt(color.substring(0, 1), 16);
                g = Integer.parseInt(color.substring(1, 2), 16);
                b = Integer.parseInt(color.substring(2, 3), 16);
                a = Integer.parseInt(color.substring(3, 4), 16);
                return Colour.rgb(r / 15.0, g / 15.0, b / 15.0, opacity * a / 15.0);
            } else if (len == 6) {
                r = Integer.parseInt(color.substring(0, 2), 16);
                g = Integer.parseInt(color.substring(2, 4), 16);
                b = Integer.parseInt(color.substring(4, 6), 16);
                return Colour.rgb(r, g, b, opacity);
            } else if (len == 8) {
                r = Integer.parseInt(color.substring(0, 2), 16);
                g = Integer.parseInt(color.substring(2, 4), 16);
                b = Integer.parseInt(color.substring(4, 6), 16);
                a = Integer.parseInt(color.substring(6, 8), 16);
                return Colour.rgb(r, g, b, opacity * a / 255.0);
            }
        } catch (NumberFormatException _) {
            // ignored
        }

        throw new IllegalArgumentException("Invalid color specification");
    }

    /** See {@link #of(String, double)}. */
    public static Colour of(String color, int alpha) {
        return of(color, checkInt("Alpha", alpha) / 255.0);
    }

    /** See {@link #rgb(double, double, double, double)}. */
    public static Colour rgb(double red, double green, double blue) {
        return rgb(red, green, blue, 1.0);
    }

    /**
     * Creates a color using floating-point RGB components.
     *
     * @param red     red channel value from 0.0 to 1.0
     * @param green   green channel value from 0.0 to 1.0
     * @param blue    blue channel value from 0.0 to 1.0
     * @param opacity opacity value from 0.0 to 1.0
     */
    public static Colour rgb(double red, double green, double blue, double opacity) {
        return new Colour(red, green, blue, opacity);
    }

    /** See {@link #rgb(int, int, int, int)}. */
    public static Colour rgb(int red, int green, int blue) {
        return rgb(red, green, blue, 1.0);
    }

    /**
     * Creates a color using integer RGB components.
     *
     * @param red   red channel value from 0 to 255
     * @param green green channel value from 0 to 255
     * @param blue  blue channel value from 0 to 255
     * @param alpha alpha value from 0 to 255
     */
    public static Colour rgb(int red, int green, int blue, int alpha) {
        return new Colour(red, green, blue, alpha);
    }

    /** See {@link #rgb(double, double, double, double)}. */
    public static Colour rgb(int red, int green, int blue, double opacity) {
        return new Colour(
            checkInt("Red", red) / 255.0,
            checkInt("Green", green) / 255.0,
            checkInt("Blue", blue) / 255.0,
            opacity
        );
    }

    /** See {@link #rgb(double, double, double, double)}. */
    public static Colour rgb(RGB rgb) {
        return new Colour(rgb.red(), rgb.green(), rgb.blue(), rgb.opacity());
    }

    /** See {@link #hsb(double, double, double, double)}. */
    public static Colour hsb(double hue, double saturation, double brightness) {
        return hsb(hue, saturation, brightness, 1.0);
    }

    /**
     * Creates a color using HSB values.
     *
     * @param hue        angle in degrees from 0.0 to 360.0
     * @param saturation saturation value from 0.0 to 1.0
     * @param brightness brightness value from 0.0 to 1.0
     * @param opacity    opacity value from 0.0 to 1.0
     */
    public static Colour hsb(double hue, double saturation, double brightness, double opacity) {
        return new HSB(hue, saturation, brightness, opacity).toColour();
    }

    /** See {@link #hsb(double, double, double, double)}. */
    public static Colour hsb(HSB hsb) {
        return hsb(hsb.hue(), hsb.saturation(), hsb.brightness(), hsb.opacity());
    }

    /** See {@link #hsl(double, double, double, double)}. */
    public static Colour hsl(double hue, double saturation, double lightness) {
        return hsl(hue, saturation, lightness, 1.0);
    }

    /**
     * Creates a color using HSL values.
     *
     * @param hue        angle in degrees from 0.0 to 360.0
     * @param saturation saturation value from 0.0 to 1.0
     * @param lightness  lightness value from 0.0 to 1.0
     * @param opacity    opacity value from 0.0 to 1.0
     */
    public static Colour hsl(double hue, double saturation, double lightness, double opacity) {
        return new HSL(hue, saturation, lightness, opacity).toColour();
    }

    /** See {@link #hsl(double, double, double, double)}. */
    public static Colour hsl(HSL hsl) {
        return hsl(hsl.hue(), hsl.saturation(), hsl.lightness(), hsl.opacity());
    }

    /**
     * Creates a color instance from a JavaFX {@link Color} object.
     *
     * @param color original JavaFX color object
     */
    public static Colour color(Color color) {
        return new Colour(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
    }
    //endregion

    //region MANIPULATIONS
    //*************************************************************************

    /**
     * Creates a modified copy of this color by applying scaling factors.
     *
     * @param hueShift         degrees to add to current hue
     * @param saturationFactor multiplier for current saturation
     * @param brightnessFactor multiplier for current brightness
     * @param opacityFactor    multiplier for current opacity
     */
    public Colour derive(double hueShift, double saturationFactor,
                         double brightnessFactor, double opacityFactor) {
        HSB hsb = HSB.fromRGB(getRed(), getGreen(), getBlue());

        double b = hsb.brightness();
        if (b == 0 && brightnessFactor > 1.0) {
            b = 0.05;
        }

        double h = (((hsb.hue() + hueShift) % 360.0) + 360.0) % 360.0;
        double s = Math.clamp(hsb.saturation() * saturationFactor, 0.0, 1.0);
        b = Math.clamp(b * brightnessFactor, 0.0, 1.0);
        double a = Math.clamp(getOpacity() * opacityFactor, 0.0, 1.0);

        h = Math.round(h * 1e4) / 1e4;
        s = Math.round(s * 1e4) / 1e4;
        b = Math.round(b * 1e4) / 1e4;

        return hsb(h, s, b, a);
    }

    /**
     * Creates a brighter version of this color.
     *
     * @param factor brightness scaling value
     */
    public Colour brighter(double factor) {
        checkDouble("Brighter factor", factor);
        return derive(0, 1.0, 1.0 / factor, 1.0);
    }

    /**
     * Creates a darker version of this color.
     *
     * @param factor darkness scaling value
     */
    public Colour darker(double factor) {
        checkDouble("Darker factor", factor);
        return derive(0, 1.0, factor, 1.0);
    }

    /**
     * Creates a more saturated version of this color.
     *
     * @param factor saturation scaling value
     */
    public Colour saturate(double factor) {
        checkDouble("Saturate factor", factor);
        return derive(0, 1.0 / factor, 1.0, 1.0);
    }

    /**
     * Creates a less saturated version of this color.
     *
     * @param factor desaturation scaling value
     */
    public Colour desaturate(double factor) {
        checkDouble("Desaturate factor", factor);
        return derive(0, factor, 1.0, 1.0);
    }

    /**
     * Converts this color to grayscale.
     *
     * @return a gray color instance preserving current opacity
     */
    public Colour grayscale() {
        double gray = 0.21 * getRed() + 0.71 * getGreen() + 0.07 * getBlue();
        return Colour.rgb(gray, gray, gray, getOpacity());
    }

    /**
     * Inverts red, green, and blue components.
     *
     * @return an inverted color instance preserving current opacity
     */
    public Colour invert() {
        return Colour.rgb(1.0 - getRed(), 1.0 - getGreen(), 1.0 - getBlue(), getOpacity());
    }

    /**
     * Performs linear interpolation between this color and a target color.
     *
     * @param endValue target color value
     * @param t        progress ratio from 0.0 to 1.0
     */
    public Colour interpolate(Colour endValue, double t) {
        if (t == 0.0 || equals(endValue)) {
            return this;
        }
        if (t == 1.0) {
            return endValue;
        }

        double ir = getRed();
        double ig = getGreen();
        double ib = getBlue();
        double io = getOpacity();

        double r = Math.round((ir + (endValue.getRed() - ir) * t) * 1e10) / 1e10;
        double g = Math.round((ig + (endValue.getGreen() - ig) * t) * 1e10) / 1e10;
        double b = Math.round((ib + (endValue.getBlue() - ib) * t) * 1e10) / 1e10;
        double a = Math.round((io + (endValue.getOpacity() - io) * t) * 1e10) / 1e10;

        return new Colour(Math.clamp(r, 0, 1), Math.clamp(g, 0, 1), Math.clamp(b, 0, 1), Math.clamp(a, 0, 1));
    }

    /**
     * Blends this translucent color over a solid background color.
     *
     * <p>When implementing designs, you'll sometimes want to use a lighter shade of a color for
     * a background. A simple way to achieve lightness is by increasing the transparency or reducing
     * the opacity of the color (changing what is known as the alpha channel). Against a white
     * background, the color will look lighter.
     *
     * <p>There are however several issues. Adding an alpha channel means that the rendered color
     * depends on what color lies underneath. Your elements may look fine when drawn over a default
     * white background, but if they end up over another color, the foreground will be affected. Even
     * if a white background is enforced, if your elements ever overlap, you'll also run into a problem
     * when using transparency: the overlapping regions will get darker than the individual elements.
     *
     * <p>To remove the transparency we need to blend the foreground color with the background color,
     * using the transparency value to determine how much to weight the foreground layer.
     *
     * <p><a href="https://filosophy.org/code/online-tool-to-lighten-color-without-alpha-channel/">Source</a>.
     *
     * @param background opaque color underneath
     */
    public Colour flatten(Colour background) {
        double opacity = getOpacity();
        if (opacity >= 1.0) {
            return this;
        }

        double r = opacity * getRed() + (1.0 - opacity) * background.getRed();
        double g = opacity * getGreen() + (1.0 - opacity) * background.getGreen();
        double b = opacity * getBlue() + (1.0 - opacity) * background.getBlue();

        return new Colour(Math.clamp(r, 0.0, 1.0), Math.clamp(g, 0.0, 1.0), Math.clamp(b, 0.0, 1.0), 1.0);
    }

    /**
     * Checks if flattening this color over the specified background will produce a distinct
     * visual result.
     *
     * <p>Returns {@code false} if this color is already fully opaque, or if its RGB components
     * match the background (meaning alpha blending would yield the exact same color).
     *
     * @param background the opaque color underneath
     */
    public boolean hasFlattenEffect(Colour background) {
        return !isSameAs(background);
    }

    /**
     * Calculates the transparent color needed to produce this color over a background.
     *
     * <p>The opposite to the {@link #flatten(Colour)}. This method converts target opaque
     * color to its equivalent with the desired opacity level.
     *
     * @param background background color reference
     * @param opacity    desired opacity target
     * @return equivalent translucent color instance
     */
    public Colour translucent(Colour background, double opacity) {
        checkDouble("Opacity", opacity);

        double safeOpacity = Math.max(opacity, 0.00001);

        double r = (getRed() - background.getRed() * (1.0 - safeOpacity)) / safeOpacity;
        double g = (getGreen() - background.getGreen() * (1.0 - safeOpacity)) / safeOpacity;
        double b = (getBlue() - background.getBlue() * (1.0 - safeOpacity)) / safeOpacity;

        return new Colour(Math.clamp(r, 0.0, 1.0), Math.clamp(g, 0.0, 1.0), Math.clamp(b, 0.0, 1.0), opacity);
    }
    //endregion

    //region CONVERTERS
    //*************************************************************************

    /** See {@link #toHex(boolean)}. */
    public String toHex() {
        return toHex(false);
    }

    /**
     * Formats this color as a hexadecimal (web) string.
     *
     * @param forceAlpha {@code true} to append always alpha channel
     * @return hex representation of this color
     */
    public String toHex(boolean forceAlpha) {
        return forceAlpha || getOpacity() < 1.0
            ? RGB.toHex(getRed(), getGreen(), getBlue(), getOpacity())
            : RGB.toHex(getRed(), getGreen(), getBlue());
    }

    /** See {@link #toCompactHex(boolean)}. */
    public String toCompactHex() {
        return toCompactHex(false);
    }

    /**
     * Formats this color as a compact hexadecimal (web) string when possible.
     *
     * @param forceAlpha {@code true} to always append alpha channel
     * @return short hex representation of this color
     */
    public String toCompactHex(boolean forceAlpha) {
        return forceAlpha || getOpacity() < 1.0
            ? RGB.toCompactHex(getRed(), getGreen(), getBlue(), getOpacity())
            : RGB.toCompactHex(getRed(), getGreen(), getBlue());
    }

    /**
     * Converts this color to an RGB model representation.
     */
    public RGB toRGB() {
        return new RGB(getRed(), getGreen(), getBlue(), getOpacity());
    }

    /**
     * Converts this color to HSB model representation.
     */
    public HSB toHSB() {
        return HSB.fromRGB(getRed(), getGreen(), getBlue(), getOpacity());
    }

    /**
     * Converts this color to HSL model representation.
     */
    public HSL toHSL() {
        return HSL.fromRGB(getRed(), getGreen(), getBlue(), getOpacity());
    }

    /**
     * Converts this color to a JavaFX color object.
     */
    public Color toColor() {
        return Color.color(getRed(), getGreen(), getBlue(), getOpacity());
    }
    //endregion

    //region HELPERS
    //*************************************************************************

    protected static long pack16(double component) {
        return Math.round(component * MAX_16BIT_D);
    }

    protected static long pack16(int component) {
        return Math.round(component * MAX_16BIT_D / 255.0);
    }

    protected static long packDouble(String param, double value) {
        return pack16(checkDouble(param, value));
    }

    protected static long packInt(String param, int value) {
        return pack16(checkInt(param, value));
    }

    protected static long pack16(long r, long g, long b, long a) {
        return ((r & 0xFFFFL) << 48) | ((g & 0xFFFFL) << 32) | ((b & 0xFFFFL) << 16) | (a & 0xFFFFL);
    }

    protected static long pack16(double red, double green, double blue, double opacity) {
        return pack16(
            packDouble("Red", red),
            packDouble("Green", green),
            packDouble("Blue", blue),
            packDouble("Opacity", opacity)
        );
    }

    protected static long pack16(int red, int green, int blue, int alpha) {
        return pack16(
            packInt("Red", red),
            packInt("Green", green),
            packInt("Blue", blue),
            packInt("Alpha", alpha)
        );
    }

    protected static double checkDouble(String param, double value) {
        Colour.checkRange(param, value, 0.0, 1.0);
        return value;
    }

    protected static int checkInt(String param, int value) {
        Colour.checkRange(param, value, 0, 255);
        return value;
    }

    protected static double checkRange(String param, double val, double min, double max) {
        if (val < min || val > max) {
            throw new IllegalArgumentException(
                "%s value (%.1f) must be inclusively in the range %.1f-%.1f".formatted(param, val, min, max)
            );
        }
        return val;
    }

    protected static int checkRange(String param, int val, int min, int max) {
        if (val < min || val > max) {
            throw new IllegalArgumentException(
                "%s value (%d) must be inclusively in the range %d-%d".formatted(param, val, min, max)
            );
        }
        return val;
    }

    protected static Colour parseRGB(String color, int roff, boolean hasAlpha, double a) {
        int rend = color.indexOf(',', roff);
        int gend = rend < 0 ? -1 : color.indexOf(',', rend + 1);
        int bend = gend < 0 ? -1 : color.indexOf(hasAlpha ? ',' : ')', gend + 1);
        int aend = hasAlpha ? (bend < 0 ? -1 : color.indexOf(')', bend + 1)) : bend;

        if (aend < 0) {
            String expectedFormat = hasAlpha ? "rgb(r, g, b, a)" : "rgb(r, g, b)";
            throw new IllegalArgumentException(String.format(
                "Invalid RGB color structure in '%s' starting at offset %d. Expected format: '%s'",
                color, roff, expectedFormat
            ));
        }

        double r = parseComponent(color, roff, rend, PARSE_COMPONENT);
        double g = parseComponent(color, rend + 1, gend, PARSE_COMPONENT);
        double b = parseComponent(color, gend + 1, bend, PARSE_COMPONENT);
        if (hasAlpha) {
            a *= parseComponent(color, bend + 1, aend, PARSE_ALPHA);
        }

        return new Colour(r, g, b, a);
    }

    protected static Colour parseHSB(String color, int hoff, boolean hasAlpha, double a) {
        int hend = color.indexOf(',', hoff);
        int send = hend < 0 ? -1 : color.indexOf(',', hend + 1);
        int bend = send < 0 ? -1 : color.indexOf(hasAlpha ? ',' : ')', send + 1);
        int aend = hasAlpha ? (bend < 0 ? -1 : color.indexOf(')', bend + 1)) : bend;

        if (aend < 0) {
            String expectedFormat = hasAlpha ? "hsb(h, s%%, b%%, a)" : "hsb(h, s%%, b%%)";
            throw new IllegalArgumentException(String.format(
                "Invalid HSB color structure in '%s' starting at offset %d. Expected format: '%s'",
                color, hoff, expectedFormat
            ));
        }

        double h = parseComponent(color, hoff, hend, PARSE_ANGLE);
        double s = parseComponent(color, hend + 1, send, PARSE_PERCENT);
        double b = parseComponent(color, send + 1, bend, PARSE_PERCENT);
        if (hasAlpha) {
            a *= parseComponent(color, bend + 1, aend, PARSE_ALPHA);
        }

        return Colour.hsb(h, s, b, a);
    }

    protected static double parseComponent(String color, int off, int end, int type) {
        String rawComponent = color.substring(off, end).trim();
        String value = rawComponent;

        if (value.endsWith("%")) {
            if (type > PARSE_PERCENT) {
                throw new IllegalArgumentException(String.format(
                    "Unexpected percentage sign '%%' in component '%s'", rawComponent
                ));
            }
            type = PARSE_PERCENT;
            value = value.substring(0, value.length() - 1).trim();
        } else if (type == PARSE_PERCENT) {
            throw new IllegalArgumentException(String.format(
                "Expected percentage value (ending with '%%'), but got '%s'", rawComponent
            ));
        }

        try {
            double c = (type == PARSE_COMPONENT) ? Integer.parseInt(value) : Double.parseDouble(value);
            return switch (type) {
                case PARSE_ALPHA -> Math.clamp(c, 0.0, 1.0);
                case PARSE_PERCENT -> Math.clamp(c / 100.0, 0.0, 1.0);
                case PARSE_COMPONENT -> Math.clamp(c / 255.0, 0.0, 1.0);
                case PARSE_ANGLE -> ((c % 360.0) + 360.0) % 360.0;
                default -> throw new IllegalArgumentException("Unsupported component parse type: " + type);
            };
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(
                "Failed to parse numeric value from component '%s'", rawComponent
            ), e);
        }
    }

    /**
     * Converts an sRGB color channel value to linear RGB space by removing gamma correction.
     */
    protected static double convertChannelToLinear(double val) {
        return val <= 0.03928 ? (val / 12.92) : Math.pow((val + 0.055) / 1.055, 2.4);
    }

    protected boolean isSameAs(Colour other) {
        return Math.abs(this.getRed() - other.getRed()) < 0.001
            && Math.abs(this.getGreen() - other.getGreen()) < 0.001
            && Math.abs(this.getBlue() - other.getBlue()) < 0.001;
    }
    //endregion

    //region OBSERVABLE
    @Override
    public Colour getValue() {
        return this;
    }

    @Override
    public void addListener(@Nullable InvalidationListener listener) {
        if (listener == null) {
            return;
        }
        if (invalidationListeners == null) {
            invalidationListeners = new CopyOnWriteArrayList<>();
        }
        invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(@Nullable InvalidationListener listener) {
        if (listener == null || invalidationListeners == null) {
            return;
        }
        invalidationListeners.remove(listener);
    }

    @Override
    public void addListener(@Nullable ChangeListener<? super Colour> listener) {
        if (listener == null) {
            return;
        }
        if (changeListeners == null) {
            changeListeners = new CopyOnWriteArrayList<>();
        }
        changeListeners.add(listener);
    }

    @Override
    public void removeListener(@Nullable ChangeListener<? super Colour> listener) {
        if (listener == null || changeListeners == null) {
            return;
        }
        changeListeners.remove(listener);
    }

    protected void fireValueChanged(long oldValue) {
        if (invalidationListeners != null && !invalidationListeners.isEmpty()) {
            for (InvalidationListener listener : invalidationListeners) {
                listener.invalidated(this);
            }
        }
        if (changeListeners != null && !changeListeners.isEmpty()) {
            if (oldState == null) {
                oldState = new Colour(oldValue);
            } else {
                oldState.rgba = oldValue;
            }
            for (ChangeListener<? super Colour> listener : changeListeners) {
                listener.changed(this, oldState, this);
            }
        }
    }

    // for unit tests
    boolean isInvalidationListAllocated() {
        return invalidationListeners != null;
    }

    boolean isChangeListAllocated() {
        return changeListeners != null;
    }

    boolean hasInvalidationListeners() {
        return invalidationListeners != null && !invalidationListeners.isEmpty();
    }

    boolean hasChangeListeners() {
        return changeListeners != null && !changeListeners.isEmpty();
    }
    //endregion

    //region DATA CLASSES

    /**
     * Represents a color in the RGB color space.
     *
     * @param red     red channel value from 0.0 to 1.0
     * @param green   green channel value from 0.0 to 1.0
     * @param blue    blue channel value from 0.0 to 1.0
     * @param opacity opacity value from 0.0 to 1.0
     */
    public record RGB(double red, double green, double blue, double opacity) {

        private static final Pattern HEX_PATTERN = Pattern.compile("^#?((\\p{XDigit}{3}){1,2}|(\\p{XDigit}{4}){1,2})$");

        public RGB {
            checkDouble("Red", red);
            checkDouble("Green", green);
            checkDouble("Blue", blue);
            checkDouble("Opacity", opacity);
        }

        /**
         * Creates an opaque RGB color.
         *
         * @param red   red channel value from 0.0 to 1.0
         * @param green green channel value from 0.0 to 1.0
         * @param blue  blue channel value from 0.0 to 1.0
         */
        public RGB(double red, double green, double blue) {
            this(red, green, blue, 1.0);
        }

        /**
         * Returns the red channel value as an integer.
         *
         * @return red component from 0 to 255
         */
        public int redInt() {
            return toByte(red);
        }

        /**
         * Returns the green channel value as an integer.
         *
         * @return green component from 0 to 255
         */
        public int greenInt() {
            return toByte(green);
        }

        /**
         * Returns the blue channel value as an integer.
         *
         * @return blue component from 0 to 255
         */
        public int blueInt() {
            return toByte(blue);
        }

        /**
         * Returns the opacity value as an integer.
         *
         * @return alpha component from 0 to 255
         */
        public int alpha() {
            return toByte(opacity);
        }

        /** See {@link #toInt(boolean)}. */
        public int toInt() {
            return toInt(true);
        }

        /**
         * Packs color channels into a 32-bit integer (0xRRGGBBAA or 0xRRGGBBFF).
         *
         * @param includeAlpha {@code true} to preserve opacity, {@code false} to set alpha to 0xFF
         */
        public int toInt(boolean includeAlpha) {
            if (includeAlpha) {
                return toInt(redInt(), greenInt(), blueInt(), alpha());
            }
            return toInt(redInt(), greenInt(), blueInt());
        }

        /**
         * Converts this instance to a {@link Colour} object.
         */
        public Colour toColour() {
            return rgb(this);
        }

        @Override
        public String toString() {
            int r = redInt();
            int g = greenInt();
            int b = blueInt();

            if (opacity >= 1.0) {
                return "rgb(" + r + ", " + g + ", " + b + ")";
            }

            double alphaVal = Math.round(opacity * 100.0) / 100.0;
            return "rgba(" + r + ", " + g + ", " + b + ", " + alphaVal + ")";
        }

        /**
         * Validates whether a text string matches HEX color format.
         *
         * @param color text representation of HEX color
         * @return {@code true} if format matches #RGB, #RGBA, #RRGGBB, #RRGGBBAA (with optional leading #)
         */
        public static boolean isValidHex(@Nullable String color) {
            return color != null && HEX_PATTERN.matcher(color).matches();
        }

        /**
         * Formats floating-point RGB components as a hexadecimal string (#RRGGBB).
         *
         * @param red   red channel value from 0.0 to 1.0
         * @param green green channel value from 0.0 to 1.0
         * @param blue  blue channel value from 0.0 to 1.0
         */
        public static String toHex(double red, double green, double blue) {
            return formatHex(toByte(red), toByte(green), toByte(blue), -1, false);
        }

        /**
         * Formats floating-point RGBA components as a hexadecimal string (#RRGGBBAA).
         *
         * @param red     red channel value from 0.0 to 1.0
         * @param green   green channel value from 0.0 to 1.0
         * @param blue    blue channel value from 0.0 to 1.0
         * @param opacity opacity value from 0.0 to 1.0
         */
        public static String toHex(double red, double green, double blue, double opacity) {
            return formatHex(toByte(red), toByte(green), toByte(blue), toByte(opacity), false);
        }

        /**
         * Formats floating-point RGB components as a compact hexadecimal string (#RGB) when possible.
         *
         * @param red   red channel value from 0.0 to 1.0
         * @param green green channel value from 0.0 to 1.0
         * @param blue  blue channel value from 0.0 to 1.0
         */
        public static String toCompactHex(double red, double green, double blue) {
            return formatHex(toByte(red), toByte(green), toByte(blue), -1, true);
        }

        /**
         * Formats floating-point RGBA components as a compact hexadecimal string (#RGBA) when possible.
         *
         * @param red     red channel value from 0.0 to 1.0
         * @param green   green channel value from 0.0 to 1.0
         * @param blue    blue channel value from 0.0 to 1.0
         * @param opacity opacity value from 0.0 to 1.0
         */
        public static String toCompactHex(double red, double green, double blue, double opacity) {
            return formatHex(toByte(red), toByte(green), toByte(blue), toByte(opacity), true);
        }

        private static String formatHex(int r, int g, int b, int a, boolean compact) {
            boolean hasAlpha = (a >= 0);
            boolean canCompress = compact && isCompressible(r) && isCompressible(g) && isCompressible(b)
                && (!hasAlpha || isCompressible(a));

            if (canCompress) {
                char[] chars = new char[hasAlpha ? 5 : 4];
                chars[0] = '#';
                chars[1] = HEX_DIGITS[r & 0xF];
                chars[2] = HEX_DIGITS[g & 0xF];
                chars[3] = HEX_DIGITS[b & 0xF];
                if (hasAlpha) {
                    chars[4] = HEX_DIGITS[a & 0xF];
                }
                return new String(chars);
            } else {
                char[] chars = new char[hasAlpha ? 9 : 7];
                chars[0] = '#';
                chars[1] = HEX_DIGITS[(r >>> 4) & 0xF];
                chars[2] = HEX_DIGITS[r & 0xF];
                chars[3] = HEX_DIGITS[(g >>> 4) & 0xF];
                chars[4] = HEX_DIGITS[g & 0xF];
                chars[5] = HEX_DIGITS[(b >>> 4) & 0xF];
                chars[6] = HEX_DIGITS[b & 0xF];
                if (hasAlpha) {
                    chars[7] = HEX_DIGITS[(a >>> 4) & 0xF];
                    chars[8] = HEX_DIGITS[a & 0xF];
                }
                return new String(chars);
            }
        }

        /**
         * Compresses a hexadecimal color string to short notation if possible (#RRGGBB -> #RGB).
         *
         * @param color text representation of HEX color
         * @throws IllegalArgumentException if the text format is invalid
         */
        public static String compressHex(String color) {
            checkHex(color);

            boolean hasHash = color.startsWith("#");
            int start = hasHash ? 1 : 0;
            int hexLength = color.length() - start;

            if (hexLength <= 4) {
                return color;
            }

            // check if hex color can be compressed
            for (int i = start; i < color.length(); i += 2) {
                if (Character.toUpperCase(color.charAt(i)) != Character.toUpperCase(color.charAt(i + 1))) {
                    return color;
                }
            }

            char[] chars = new char[hasHash ? 1 + hexLength / 2 : hexLength / 2];
            int idx = 0;
            if (hasHash) {
                chars[idx++] = '#';
            }

            for (int i = start; i < color.length(); i += 2) {
                chars[idx++] = color.charAt(i);
            }

            return new String(chars);
        }

        /**
         * Expands a compact hexadecimal color string to full notation (#RGB -> #RRGGBB).
         *
         * @param color text representation of HEX color
         * @throws IllegalArgumentException if the text format is invalid
         */
        public static String expandHex(String color) {
            checkHex(color);

            boolean hasHash = color.startsWith("#");
            int start = hasHash ? 1 : 0;
            int hexLength = color.length() - start;

            if (hexLength > 4) {
                return color;
            }

            char[] chars = new char[hasHash ? 1 + hexLength * 2 : hexLength * 2];
            int idx = 0;
            if (hasHash) {
                chars[idx++] = '#';
            }
            for (int i = start; i < color.length(); i++) {
                char c = color.charAt(i);
                chars[idx++] = c;
                chars[idx++] = c;
            }
            return new String(chars);
        }

        private static void checkHex(String color) {
            if (!isValidHex(color)) {
                throw new IllegalArgumentException(
                    "HEX color must be specified as #RRGGBB, #RGB, #RRGGBBAA, #RGBA, RRGGBB, RGB, RRGGBBAA, or RGBA."
                );
            }
        }

        private static boolean isCompressible(int b) {
            return (b >>> 4) == (b & 0xF);
        }

        /** See {@link #toInt(int, int, int, int)}. */
        public static int toInt(int red, int green, int blue) {
            checkInt("Red", red);
            checkInt("Green", green);
            checkInt("Blue", blue);
            return ((red & 0xFF) << 24) | ((green & 0xFF) << 16) | ((blue & 0xFF) << 8) | 0xFF;
        }

        /**
         * Packs integer RGBA components (0–255) into a 32-bit integer (0xRRGGBBAA).
         *
         * @param red   red channel value from 0 to 255
         * @param green green channel value from 0 to 255
         * @param blue  blue channel value from 0 to 255
         * @param alpha alpha value from 0 to 255
         */
        public static int toInt(int red, int green, int blue, int alpha) {
            checkInt("Red", red);
            checkInt("Green", green);
            checkInt("Blue", blue);
            checkInt("Alpha", alpha);
            return ((red & 0xFF) << 24) | ((green & 0xFF) << 16) | ((blue & 0xFF) << 8) | (alpha & 0xFF);
        }

        private static int toByte(double d) {
            int i = (int) (d * 255.0 + 0.5);
            return i < 0 ? 0 : Math.min(i, 255);
        }
    }

    /**
     * Represents a color in the HSB (Hue, Saturation, Brightness) color space.
     *
     * @param hue        hue angle in degrees, normalized to range [0.0, 360.0)
     * @param saturation saturation value from 0.0 to 1.0
     * @param brightness brightness value from 0.0 to 1.0
     * @param opacity    opacity value from 0.0 to 1.0
     */
    public record HSB(double hue, double saturation, double brightness, double opacity) {

        public HSB {
            hue = ((hue % 360.0) + 360.0) % 360.0;
            checkRange("Saturation", saturation, 0.0, 1.0);
            checkRange("Brightness", brightness, 0.0, 1.0);
            checkRange("Opacity", opacity, 0.0, 1.0);
        }

        /**
         * Creates an opaque HSB color instance.
         *
         * @param hue        hue angle in degrees
         * @param saturation saturation value from 0.0 to 1.0
         * @param brightness brightness value from 0.0 to 1.0
         */
        public HSB(double hue, double saturation, double brightness) {
            this(hue, saturation, brightness, 1.0);
        }

        /**
         * Converts this HSB representation to a {@link Colour} object.
         */
        public Colour toColour() {
            return rgb(toRGB());
        }

        @Override
        public String toString() {
            int h = (int) Math.round(hue);
            int s = (int) Math.round(saturation * 100.0);
            int b = (int) Math.round(brightness * 100.0);

            if (opacity >= 1.0) {
                return String.format("hsb(%d, %d%%, %d%%)", h, s, b);
            }

            double alpha = Math.round(opacity * 100.0) / 100.0;
            return String.format("hsba(%d, %d%%, %d%%, %.2f)", h, s, b, alpha);
        }

        /** See {@link #fromRGB(double, double, double, double)}. */
        public static HSB fromRGB(double red, double green, double blue) {
            return fromRGB(red, green, blue, 1.0);
        }

        /**
         * Converts floating-point RGB components to an HSB color representation.
         *
         * @param red     red channel value from 0.0 to 1.0
         * @param green   green channel value from 0.0 to 1.0
         * @param blue    blue channel value from 0.0 to 1.0
         * @param opacity opacity value from 0.0 to 1.0
         */
        public static HSB fromRGB(double red, double green, double blue, double opacity) {
            checkRange("Red", red, 0.0, 1.0);
            checkRange("Green", green, 0.0, 1.0);
            checkRange("Blue", blue, 0.0, 1.0);
            checkRange("Opacity", opacity, 0.0, 1.0);

            double cmax = Math.max(red, Math.max(green, blue));
            double cmin = Math.min(red, Math.min(green, blue));
            double delta = cmax - cmin;

            @SuppressWarnings("UnnecessaryLocalVariable")
            double brightness = cmax;
            double saturation = (cmax != 0) ? delta / cmax : 0;
            double hue = 0;

            if (saturation != 0) {
                if (red == cmax) {
                    hue = (green - blue) / delta;
                } else if (green == cmax) {
                    hue = 2.0 + (blue - red) / delta;
                } else {
                    hue = 4.0 + (red - green) / delta;
                }

                hue *= 60.0;
                if (hue < 0) {
                    hue += 360.0;
                }
            }

            return new HSB(hue, saturation, brightness, opacity);
        }

        /**
         * Converts this HSB representation to an {@link RGB} color representation.
         *
         * @return equivalent {@link RGB} record
         */
        public RGB toRGB() {
            return toRGB(hue, saturation, brightness, opacity);
        }

        /**
         * Converts HSB components to an {@link RGB} color representation.
         *
         * @param hue        hue angle in degrees
         * @param saturation saturation value from 0.0 to 1.0
         * @param brightness brightness value from 0.0 to 1.0
         * @param opacity    opacity value from 0.0 to 1.0
         */
        public static RGB toRGB(double hue, double saturation, double brightness, double opacity) {
            checkRange("Saturation", saturation, 0.0, 1.0);
            checkRange("Brightness", brightness, 0.0, 1.0);
            checkRange("Opacity", opacity, 0.0, 1.0);

            double normalizedHue = ((hue % 360.0) + 360.0) % 360.0;

            if (saturation == 0) {
                return new RGB(brightness, brightness, brightness, opacity);
            }

            double h = normalizedHue / 60.0;
            int i = (int) Math.floor(h);
            double f = h - i;

            double p = brightness * (1.0 - saturation);
            double q = brightness * (1.0 - saturation * f);
            double t = brightness * (1.0 - saturation * (1.0 - f));

            return switch (i) {
                case 0 -> new RGB(brightness, t, p, opacity);
                case 1 -> new RGB(q, brightness, p, opacity);
                case 2 -> new RGB(p, brightness, t, opacity);
                case 3 -> new RGB(p, q, brightness, opacity);
                case 4 -> new RGB(t, p, brightness, opacity);
                default -> new RGB(brightness, p, q, opacity);
            };
        }
    }

    /**
     * Represents a color in the HSL (Hue, Saturation, Lightness) color space.
     *
     * @param hue        hue angle in degrees, normalized to range [0.0, 360.0)
     * @param saturation saturation value from 0.0 to 1.0
     * @param lightness  lightness value from 0.0 to 1.0
     * @param opacity    opacity value from 0.0 to 1.0
     */
    public record HSL(double hue, double saturation, double lightness, double opacity) {

        public HSL {
            hue = ((hue % 360.0) + 360.0) % 360.0;
            checkRange("Saturation", saturation, 0.0, 1.0);
            checkRange("Lightness", lightness, 0.0, 1.0);
            checkRange("Opacity", opacity, 0.0, 1.0);
        }

        /**
         * Creates an opaque HSL color instance.
         *
         * @param hue        hue angle in degrees
         * @param saturation saturation value from 0.0 to 1.0
         * @param lightness  lightness value from 0.0 to 1.0
         */
        public HSL(double hue, double saturation, double lightness) {
            this(hue, saturation, lightness, 1.0);
        }

        /**
         * Converts this HSL representation to a {@link Colour} object.
         *
         * @return new {@link Colour} instance
         */
        public Colour toColour() {
            return rgb(toRGB());
        }

        @Override
        public String toString() {
            int h = (int) Math.round(hue);
            int s = (int) Math.round(saturation * 100.0);
            int l = (int) Math.round(lightness * 100.0);

            if (opacity >= 1.0) {
                return String.format("hsl(%d, %d%%, %d%%)", h, s, l);
            }

            double alpha = Math.round(opacity * 100.0) / 100.0;
            return String.format("hsla(%d, %d%%, %d%%, %.2f)", h, s, l, alpha);
        }

        /** See {@link #fromRGB(int, int, int, double)}. */
        public static HSL fromRGB(int red, int green, int blue) {
            return fromRGB(red, green, blue, 1.0);
        }

        /**
         * Converts integer RGB components in the range [0, 255] to an HSL color representation.
         *
         * @param red     red channel value from 0 to 255
         * @param green   green channel value from 0 to 255
         * @param blue    blue channel value from 0 to 255
         * @param opacity opacity value from 0.0 to 1.0
         */
        public static HSL fromRGB(int red, int green, int blue, double opacity) {
            return fromRGB(
                checkRange("Red", red, 0, 255) / 255.0,
                checkRange("Green", green, 0, 255) / 255.0,
                checkRange("Blue", blue, 0, 255) / 255.0,
                opacity
            );
        }

        /** See {@link #fromRGB(double, double, double, double)}. */
        public static HSL fromRGB(double red, double green, double blue) {
            return fromRGB(red, green, blue, 1.0);
        }

        /**
         * Converts floating-point RGB components to an HSL color representation.
         *
         * @param red     red channel value from 0.0 to 1.0
         * @param green   green channel value from 0.0 to 1.0
         * @param blue    blue channel value from 0.0 to 1.0
         * @param opacity opacity value from 0.0 to 1.0
         */
        public static HSL fromRGB(double red, double green, double blue, double opacity) {
            checkRange("Red", red, 0.0, 1.0);
            checkRange("Green", green, 0.0, 1.0);
            checkRange("Blue", blue, 0.0, 1.0);
            checkRange("Opacity", opacity, 0.0, 1.0);

            double min = Math.min(Math.min(red, green), blue);
            double max = Math.max(Math.max(red, green), blue);
            double range = max - min;

            double lightness = (max + min) / 2.0;
            double saturation = 0.0;
            double hue = 0.0;

            if (range > 0.0) {
                saturation = lightness > 0.5
                    ? range / (2.0 - max - min)
                    : range / (max + min);

                if (max == red) {
                    hue = (green - blue) / range + (green < blue ? 6.0 : 0.0);
                } else if (max == green) {
                    hue = (blue - red) / range + 2.0;
                } else {
                    hue = (red - green) / range + 4.0;
                }
                hue *= 60.0;
            }

            return new HSL(hue, saturation, lightness, opacity);
        }

        /**
         * Converts this HSL representation to an {@link RGB} color representation.
         */
        public RGB toRGB() {
            return toRGB(hue, saturation, lightness, opacity);
        }

        /**
         * Converts HSL components to an {@link RGB} color representation.
         *
         * @param hue        hue angle in degrees
         * @param saturation saturation value from 0.0 to 1.0
         * @param lightness  lightness value from 0.0 to 1.0
         * @param opacity    opacity value from 0.0 to 1.0
         */
        public static RGB toRGB(double hue, double saturation, double lightness, double opacity) {
            checkRange("Saturation", saturation, 0.0, 1.0);
            checkRange("Lightness", lightness, 0.0, 1.0);
            checkRange("Opacity", opacity, 0.0, 1.0);

            double normalizedHue = ((hue % 360.0) + 360.0) % 360.0;
            double c = (1.0 - Math.abs(2.0 * lightness - 1.0)) * saturation;
            double hPrime = normalizedHue / 60.0;
            double x = c * (1.0 - Math.abs((hPrime % 2.0) - 1.0));
            double m = lightness - c / 2.0;

            int segment = (int) Math.floor(hPrime);

            double r, g, b;
            switch (segment) {
                case 0 -> {
                    r = c;
                    g = x;
                    b = 0;
                }
                case 1 -> {
                    r = x;
                    g = c;
                    b = 0;
                }
                case 2 -> {
                    r = 0;
                    g = c;
                    b = x;
                }
                case 3 -> {
                    r = 0;
                    g = x;
                    b = c;
                }
                case 4 -> {
                    r = x;
                    g = 0;
                    b = c;
                }
                default -> {
                    r = c;
                    g = 0;
                    b = x;
                }
            }

            return new RGB(
                Math.clamp(r + m, 0.0, 1.0),
                Math.clamp(g + m, 0.0, 1.0),
                Math.clamp(b + m, 0.0, 1.0),
                opacity
            );
        }
    }

    /**
     * WCAG 2.0 accessibility contrast levels and ratio criteria.
     *
     * <p>Contrast ratio ranges from 1:1 (monochrome overlap, zero contrast)
     * to 21:1 (pure black on pure white).
     *
     * @see <a href="https://www.w3.org/TR/WCAG20-TECHS/G18.html">WCAG 2.0 G18 Specification</a>
     */
    public enum ContrastLevel {

        /**
         * Level AA for normal text (smaller than 18pt, or smaller than 14pt bold).
         * Requires a minimum contrast ratio of 4.5:1.
         */
        AA_NORMAL(4.5),

        /**
         * Level AA for large text (at least 18pt, or at least 14pt bold).
         * Requires a minimum contrast ratio of 3.0:1.
         */
        AA_LARGE(3.0),

        /**
         * Level AAA for normal text (smaller than 18pt, or smaller than 14pt bold).
         * Requires a minimum contrast ratio of 7.0:1.
         */
        AAA_NORMAL(7.0),

        /**
         * Level AAA for large text (at least 18pt, or at least 14pt bold).
         * Requires a minimum contrast ratio of 4.5:1.
         */
        AAA_LARGE(4.5);

        private final double ratio;

        ContrastLevel(double ratio) {
            this.ratio = ratio;
        }

        /**
         * Checks if the given contrast ratio meets or exceeds this level's requirement.
         *
         * @param ratio contrast ratio value (from 1.0 to 21.0)
         * @return {@code true} if the ratio satisfies this level, {@code false} otherwise
         */
        public boolean satisfies(double ratio) {
            return this.ratio <= ratio;
        }

        /**
         * Calculates the WCAG contrast ratio between two colors.
         *
         * @param color1 first color
         * @param color2 second color
         * @return contrast ratio in range [1.0, 21.0]
         */
        public static double getContrastRatio(Colour color1, Colour color2) {
            return getContrastRatio(color1.getLuminance(), color2.getLuminance());
        }

        /**
         * Calculates the WCAG contrast ratio between two relative luminance values.
         *
         * @param luminance1 relative luminance of the first color (0.0 to 1.0)
         * @param luminance2 relative luminance of the second color (0.0 to 1.0)
         * @return contrast ratio in range [1.0, 21.0]
         */
        public static double getContrastRatio(double luminance1, double luminance2) {
            double l1 = Math.max(luminance1, luminance2);
            double l2 = Math.min(luminance1, luminance2);
            return (l1 + 0.05) / (l2 + 0.05);
        }

        /**
         * Calculates the WCAG contrast ratio between a foreground color and a background color,
         * taking into account semi-transparent alpha channels by flattening them against a solid base.
         *
         * @param bgColor   background color (can be semi-transparent)
         * @param fgColor   foreground color (can be semi-transparent)
         * @param baseColor solid background underneath both layers
         * @return contrast ratio in range [1.0, 21.0]
         */
        public static double getContrastRatioOpacityAware(Colour bgColor, Colour fgColor, Colour baseColor) {
            Colour solidBg = bgColor.flatten(baseColor);
            Colour solidFg = fgColor.flatten(baseColor);
            return getContrastRatio(solidBg, solidFg);
        }
    }
    //endregion
}