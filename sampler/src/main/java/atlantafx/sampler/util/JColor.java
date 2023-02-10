/**
 * MIT License
 *
 * <p>Copyright (c) 2022 National Geospatial-Intelligence Agency
 * Source: https://github.com/ngageoint/color-java
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package atlantafx.sampler.util;

/**
 * Color representation with support for hex, RGB, arithmetic RGB, HSL, and
 * integer colors.
 *
 * @author osbornb
 */
public class JColor {

    /**
     * Red arithmetic color value.
     */
    private float red = 0.0f;

    /**
     * Green arithmetic color value.
     */
    private float green = 0.0f;

    /**
     * Blue arithmetic color value.
     */
    private float blue = 0.0f;

    /**
     * Opacity arithmetic value.
     */
    private float opacity = 1.0f;

    /**
     * Create the color in hex.
     *
     * @param color hex color in format #RRGGBB, RRGGBB, #RGB, RGB, #AARRGGBB,
     *              AARRGGBB, #ARGB, or ARGB
     * @return color
     */
    public static JColor color(String color) {
        return new JColor(color);
    }

    /**
     * Create the color in hex with an opacity.
     *
     * @param color   hex color in format #RRGGBB, RRGGBB, #RGB, RGB, #AARRGGBB,
     *                AARRGGBB, #ARGB, or ARGB
     * @param opacity opacity float inclusively between 0.0 and 1.0
     * @return color
     */
    public static JColor color(String color, float opacity) {
        return new JColor(color, opacity);
    }

    /**
     * Create the color in hex with an alpha.
     *
     * @param color hex color in format #RRGGBB, RRGGBB, #RGB, RGB, #AARRGGBB,
     *              AARRGGBB, #ARGB, or ARGB
     * @param alpha alpha integer color inclusively between 0 and 255
     * @return color
     */
    public static JColor color(String color, int alpha) {
        return new JColor(color, alpha);
    }

    /**
     * Create the color with individual hex colors.
     *
     * @param red   red hex color in format RR
     * @param green green hex color in format GG
     * @param blue  blue hex color in format BB
     * @return color
     */
    public static JColor color(String red, String green, String blue) {
        return new JColor(red, green, blue);
    }

    /**
     * Create the color with individual hex colors and alpha.
     *
     * @param red   red hex color in format RR
     * @param green green hex color in format GG
     * @param blue  blue hex color in format BB
     * @param alpha alpha hex color in format AA
     * @return color
     */
    public static JColor color(String red, String green, String blue,
                               String alpha) {
        return new JColor(red, green, blue, alpha);
    }

    /**
     * Create the color with individual hex colors and opacity.
     *
     * @param red     red hex color in format RR
     * @param green   green hex color in format GG
     * @param blue    blue hex color in format BB
     * @param opacity opacity float inclusively between 0.0 and 1.0
     * @return color
     */
    public static JColor color(String red, String green, String blue,
                               float opacity) {
        return new JColor(red, green, blue, opacity);
    }

    /**
     * Create the color with RGB values.
     *
     * @param red   red integer color inclusively between 0 and 255
     * @param green green integer color inclusively between 0 and 255
     * @param blue  blue integer color inclusively between 0 and 255
     * @return color
     */
    public static JColor color(int red, int green, int blue) {
        return new JColor(red, green, blue);
    }

    /**
     * Create the color with RGBA values.
     *
     * @param red   red integer color inclusively between 0 and 255
     * @param green green integer color inclusively between 0 and 255
     * @param blue  blue integer color inclusively between 0 and 255
     * @param alpha alpha integer color inclusively between 0 and 255
     * @return color
     */
    public static JColor color(int red, int green, int blue, int alpha) {
        return new JColor(red, green, blue, alpha);
    }

    /**
     * Create the color with RGBA values.
     *
     * @param red     red integer color inclusively between 0 and 255
     * @param green   green integer color inclusively between 0 and 255
     * @param blue    blue integer color inclusively between 0 and 255
     * @param opacity opacity float inclusively between 0.0 and 1.0
     * @return color
     */
    public static JColor color(int red, int green, int blue, float opacity) {
        return new JColor(red, green, blue, opacity);
    }

    /**
     * Create the color with arithmetic RGB values.
     *
     * @param red   red float color inclusively between 0.0 and 1.0
     * @param green green float color inclusively between 0.0 and 1.0
     * @param blue  blue float color inclusively between 0.0 and 1.0
     * @return color
     */
    public static JColor color(float red, float green, float blue) {
        return new JColor(red, green, blue);
    }

    /**
     * Create the color with arithmetic RGB values.
     *
     * @param red     red float color inclusively between 0.0 and 1.0
     * @param green   green float color inclusively between 0.0 and 1.0
     * @param blue    blue float color inclusively between 0.0 and 1.0
     * @param opacity opacity float inclusively between 0.0 and 1.0
     * @return color
     */
    public static JColor color(float red, float green, float blue,
                               float opacity) {
        return new JColor(red, green, blue, opacity);
    }

    /**
     * Create the color with HSL (hue, saturation, lightness) or HSL (alpha)
     * values.
     *
     * @param hsl HSL array where: 0 = hue, 1 = saturation, 2 = lightness,
     *            optional 3 = alpha
     * @return color
     */
    public static JColor color(float[] hsl) {
        return new JColor(hsl);
    }

    /**
     * Create the color with HSLA (hue, saturation, lightness, alpha) values.
     *
     * @param hsl   HSL array where: 0 = hue, 1 = saturation, 2 = lightness
     * @param alpha alpha inclusively between 0.0 and 1.0
     * @return color
     */
    public static JColor color(float[] hsl, float alpha) {
        return new JColor(hsl, alpha);
    }

    /**
     * Create the color as a single integer.
     *
     * @param color color integer
     * @return color
     */
    public static JColor color(int color) {
        return new JColor(color);
    }

    /**
     * Default color constructor, opaque black.
     */
    public JColor() {

    }

    /**
     * Create the color in hex.
     *
     * @param color hex color in format #RRGGBB, RRGGBB, #RGB, RGB, #AARRGGBB,
     *              AARRGGBB, #ARGB, or ARGB
     */
    public JColor(String color) {
        setColor(color);
    }

    /**
     * Create the color in hex with an opacity.
     *
     * @param color   hex color in format #RRGGBB, RRGGBB, #RGB, RGB, #AARRGGBB,
     *                AARRGGBB, #ARGB, or ARGB
     * @param opacity opacity float inclusively between 0.0 and 1.0
     */
    public JColor(String color, float opacity) {
        setColor(color, opacity);
    }

    /**
     * Create the color in hex with an alpha.
     *
     * @param color hex color in format #RRGGBB, RRGGBB, #RGB, RGB, #AARRGGBB,
     *              AARRGGBB, #ARGB, or ARGB
     * @param alpha alpha integer color inclusively between 0 and 255
     */
    public JColor(String color, int alpha) {
        setColor(color, alpha);
    }

    /**
     * Create the color with individual hex colors.
     *
     * @param red   red hex color in format RR
     * @param green green hex color in format GG
     * @param blue  blue hex color in format BB
     */
    public JColor(String red, String green, String blue) {
        setColor(red, green, blue);
    }

    /**
     * Create the color with individual hex colors and alpha.
     *
     * @param red   red hex color in format RR
     * @param green green hex color in format GG
     * @param blue  blue hex color in format BB
     * @param alpha alpha hex color in format AA
     */
    public JColor(String red, String green, String blue, String alpha) {
        setColor(red, green, blue, alpha);
    }

    /**
     * Create the color with individual hex colors and opacity.
     *
     * @param red     red hex color in format RR
     * @param green   green hex color in format GG
     * @param blue    blue hex color in format BB
     * @param opacity opacity float inclusively between 0.0 and 1.0
     */
    public JColor(String red, String green, String blue, float opacity) {
        setColor(red, green, blue, opacity);
    }

    /**
     * Create the color with RGB values.
     *
     * @param red   red integer color inclusively between 0 and 255
     * @param green green integer color inclusively between 0 and 255
     * @param blue  blue integer color inclusively between 0 and 255
     */
    public JColor(int red, int green, int blue) {
        setColor(red, green, blue);
    }

    /**
     * Create the color with RGBA values.
     *
     * @param red   red integer color inclusively between 0 and 255
     * @param green green integer color inclusively between 0 and 255
     * @param blue  blue integer color inclusively between 0 and 255
     * @param alpha alpha integer color inclusively between 0 and 255
     */
    public JColor(int red, int green, int blue, int alpha) {
        setColor(red, green, blue, alpha);
    }

    /**
     * Create the color with RGBA values.
     *
     * @param red     red integer color inclusively between 0 and 255
     * @param green   green integer color inclusively between 0 and 255
     * @param blue    blue integer color inclusively between 0 and 255
     * @param opacity opacity float inclusively between 0.0 and 1.0
     */
    public JColor(int red, int green, int blue, float opacity) {
        setColor(red, green, blue, opacity);
    }

    /**
     * Create the color with arithmetic RGB values.
     *
     * @param red   red float color inclusively between 0.0 and 1.0
     * @param green green float color inclusively between 0.0 and 1.0
     * @param blue  blue float color inclusively between 0.0 and 1.0
     */
    public JColor(float red, float green, float blue) {
        setColor(red, green, blue);
    }

    /**
     * Create the color with arithmetic RGB values.
     *
     * @param red     red float color inclusively between 0.0 and 1.0
     * @param green   green float color inclusively between 0.0 and 1.0
     * @param blue    blue float color inclusively between 0.0 and 1.0
     * @param opacity opacity float inclusively between 0.0 and 1.0
     */
    public JColor(float red, float green, float blue, float opacity) {
        setColor(red, green, blue, opacity);
    }

    /**
     * Create the color with HSL (hue, saturation, lightness) or HSL (alpha).
     * values
     *
     * @param hsl HSL array where: 0 = hue, 1 = saturation, 2 = lightness,
     *            optional 3 = alpha
     */
    public JColor(float[] hsl) {
        if (hsl.length > 3) {
            setColorByHSL(hsl[0], hsl[1], hsl[2], hsl[3]);
        } else {
            setColorByHSL(hsl[0], hsl[1], hsl[2]);
        }
    }

    /**
     * Create the color with HSLA (hue, saturation, lightness, alpha) values.
     *
     * @param hsl   HSL array where: 0 = hue, 1 = saturation, 2 = lightness
     * @param alpha alpha inclusively between 0.0 and 1.0
     */
    public JColor(float[] hsl, float alpha) {
        setColorByHSL(hsl[0], hsl[1], hsl[2], alpha);
    }

    /**
     * Create the color as a single integer.
     *
     * @param color color integer
     */
    public JColor(int color) {
        setColor(color);
    }

    /**
     * Copy constructor.
     *
     * @param color color to copy
     */
    public JColor(JColor color) {
        this.red = color.red;
        this.green = color.green;
        this.blue = color.blue;
        this.opacity = color.opacity;
    }

    /**
     * Set the color in hex.
     *
     * @param color hex color in format #RRGGBB, RRGGBB, #RGB, RGB, #AARRGGBB,
     *              AARRGGBB, #ARGB, or ARGB
     */
    public void setColor(String color) {
        setRed(JColorUtils.getRed(color));
        setGreen(JColorUtils.getGreen(color));
        setBlue(JColorUtils.getBlue(color));
        String alpha = JColorUtils.getAlpha(color);
        if (alpha != null) {
            setAlpha(alpha);
        }
    }

    /**
     * Set the color in hex with an opacity.
     *
     * @param color   hex color in format #RRGGBB, RRGGBB, #RGB, RGB, #AARRGGBB,
     *                AARRGGBB, #ARGB, or ARGB
     * @param opacity opacity float inclusively between 0.0 and 1.0
     */
    public void setColor(String color, float opacity) {
        setColor(color);
        setOpacity(opacity);
    }

    /**
     * Set the color in hex with an alpha.
     *
     * @param color hex color in format #RRGGBB, RRGGBB, #RGB, RGB, #AARRGGBB,
     *              AARRGGBB, #ARGB, or ARGB
     * @param alpha alpha integer color inclusively between 0 and 255
     */
    public void setColor(String color, int alpha) {
        setColor(color);
        setAlpha(alpha);
    }

    /**
     * Set the color with individual hex colors.
     *
     * @param red   red hex color in format RR
     * @param green green hex color in format GG
     * @param blue  blue hex color in format BB
     */
    public void setColor(String red, String green, String blue) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }

    /**
     * Set the color with individual hex colors and alpha.
     *
     * @param red   red hex color in format RR
     * @param green green hex color in format GG
     * @param blue  blue hex color in format BB
     * @param alpha alpha hex color in format AA
     */
    public void setColor(String red, String green, String blue, String alpha) {
        setColor(red, green, blue);
        setAlpha(alpha);
    }

    /**
     * Set the color with individual hex colors and opacity.
     *
     * @param red     red hex color in format RR
     * @param green   green hex color in format GG
     * @param blue    blue hex color in format BB
     * @param opacity opacity float inclusively between 0.0 and 1.0
     */
    public void setColor(String red, String green, String blue, float opacity) {
        setColor(red, green, blue);
        setOpacity(opacity);
    }

    /**
     * Set the color with RGB values.
     *
     * @param red   red integer color inclusively between 0 and 255
     * @param green green integer color inclusively between 0 and 255
     * @param blue  blue integer color inclusively between 0 and 255
     */
    public void setColor(int red, int green, int blue) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }

    /**
     * Set the color with RGBA values.
     *
     * @param red   red integer color inclusively between 0 and 255
     * @param green green integer color inclusively between 0 and 255
     * @param blue  blue integer color inclusively between 0 and 255
     * @param alpha alpha integer color inclusively between 0 and 255
     */
    public void setColor(int red, int green, int blue, int alpha) {
        setColor(red, green, blue);
        setAlpha(alpha);
    }

    /**
     * Set the color with RGBA values.
     *
     * @param red     red integer color inclusively between 0 and 255
     * @param green   green integer color inclusively between 0 and 255
     * @param blue    blue integer color inclusively between 0 and 255
     * @param opacity opacity float inclusively between 0.0 and 1.0
     */
    public void setColor(int red, int green, int blue, float opacity) {
        setColor(red, green, blue);
        setOpacity(opacity);
    }

    /**
     * Set the color with arithmetic RGB values.
     *
     * @param red   red float color inclusively between 0.0 and 1.0
     * @param green green float color inclusively between 0.0 and 1.0
     * @param blue  blue float color inclusively between 0.0 and 1.0
     */
    public void setColor(float red, float green, float blue) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }

    /**
     * Set the color with arithmetic RGB values.
     *
     * @param red     red float color inclusively between 0.0 and 1.0
     * @param green   green float color inclusively between 0.0 and 1.0
     * @param blue    blue float color inclusively between 0.0 and 1.0
     * @param opacity opacity float inclusively between 0.0 and 1.0
     */
    public void setColor(float red, float green, float blue, float opacity) {
        setColor(red, green, blue);
        setOpacity(opacity);
    }

    /**
     * Set the color as a single integer.
     *
     * @param color color integer
     */
    public void setColor(int color) {
        setRed(JColorUtils.getRed(color));
        setGreen(JColorUtils.getGreen(color));
        setBlue(JColorUtils.getBlue(color));
        if (color > 16777215 || color < 0) {
            setAlpha(JColorUtils.getAlpha(color));
        }
    }

    /**
     * Set the color with HSL (hue, saturation, lightness) values.
     *
     * @param hue        hue value inclusively between 0.0 and 360.0
     * @param saturation saturation inclusively between 0.0 and 1.0
     * @param lightness  lightness inclusively between 0.0 and 1.0
     */
    public void setColorByHSL(float hue, float saturation, float lightness) {
        float[] arithmeticRGB = JColorUtils.toArithmeticRGB(hue, saturation,
            lightness);
        setRed(arithmeticRGB[0]);
        setGreen(arithmeticRGB[1]);
        setBlue(arithmeticRGB[2]);
    }

    /**
     * Set the color with HSLA (hue, saturation, lightness, alpha) values.
     *
     * @param hue        hue value inclusively between 0.0 and 360.0
     * @param saturation saturation inclusively between 0.0 and 1.0
     * @param lightness  lightness inclusively between 0.0 and 1.0
     * @param alpha      alpha inclusively between 0.0 and 1.0
     */
    public void setColorByHSL(float hue, float saturation, float lightness,
                              float alpha) {
        setColorByHSL(hue, saturation, lightness);
        setAlpha(alpha);
    }

    /**
     * Set the red color in hex.
     *
     * @param red red hex color in format RR or R
     */
    public void setRed(String red) {
        setRed(JColorUtils.toArithmeticRGB(red));
    }

    /**
     * Set the red color as an integer.
     *
     * @param red red integer color inclusively between 0 and 255
     */
    public void setRed(int red) {
        setRed(JColorUtils.toHex(red));
    }

    /**
     * Set the red color as an arithmetic float.
     *
     * @param red red float color inclusively between 0.0 and 1.0
     */
    public void setRed(float red) {
        JColorUtils.validateArithmeticRGB(red);
        this.red = red;
    }

    /**
     * Set the green color in hex.
     *
     * @param green green hex color in format GG or G
     */
    public void setGreen(String green) {
        setGreen(JColorUtils.toArithmeticRGB(green));
    }

    /**
     * Set the green color as an integer.
     *
     * @param green green integer color inclusively between 0 and 255
     */
    public void setGreen(int green) {
        setGreen(JColorUtils.toHex(green));
    }

    /**
     * Set the green color as an arithmetic float.
     *
     * @param green green float color inclusively between 0.0 and 1.0
     */
    public void setGreen(float green) {
        JColorUtils.validateArithmeticRGB(green);
        this.green = green;
    }

    /**
     * Set the blue color in hex.
     *
     * @param blue blue hex color in format BB or B
     */
    public void setBlue(String blue) {
        setBlue(JColorUtils.toArithmeticRGB(blue));
    }

    /**
     * Set the blue color as an integer.
     *
     * @param blue blue integer color inclusively between 0 and 255
     */
    public void setBlue(int blue) {
        setBlue(JColorUtils.toHex(blue));
    }

    /**
     * Set the blue color as an arithmetic float.
     *
     * @param blue blue float color inclusively between 0.0 and 1.0
     */
    public void setBlue(float blue) {
        JColorUtils.validateArithmeticRGB(blue);
        this.blue = blue;
    }

    /**
     * Set the alpha color in hex.
     *
     * @param alpha alpha hex color in format AA or A
     */
    public void setAlpha(String alpha) {
        setOpacity(JColorUtils.toArithmeticRGB(alpha));
    }

    /**
     * Set the alpha color as an integer.
     *
     * @param alpha alpha integer color inclusively between 0 and 255
     */
    public void setAlpha(int alpha) {
        setOpacity(JColorUtils.toArithmeticRGB(alpha));
    }

    /**
     * Set the alpha color as an arithmetic float.
     *
     * @param alpha alpha float color inclusively between 0.0 and 1.0
     */
    public void setAlpha(float alpha) {
        setOpacity(alpha);
    }

    /**
     * Set the opacity as an arithmetic float.
     *
     * @param opacity opacity float color inclusively between 0.0 and 1.0
     */
    public void setOpacity(float opacity) {
        JColorUtils.validateArithmeticRGB(opacity);
        this.opacity = opacity;
    }

    /**
     * Check if the color is opaque (opacity or alpha of 1.0, 255, or x00)
     *
     * @return true if opaque
     */
    public boolean isOpaque() {
        return opacity == 1.0f;
    }

    /**
     * Get the color as a hex string.
     *
     * @return hex color in the format #RRGGBB
     */
    public String getColorHex() {
        return JColorUtils.toColor(getRedHex(), getGreenHex(), getBlueHex());
    }

    /**
     * Get the color as a hex string with alpha.
     *
     * @return hex color in the format #AARRGGBB
     */
    public String getColorHexWithAlpha() {
        return JColorUtils.toColorWithAlpha(getRedHex(), getGreenHex(),
            getBlueHex(), getAlphaHex());
    }

    /**
     * Get the color as a hex string, shorthanded when possible.
     *
     * @return hex color in the format #RGB or #RRGGBB
     */
    public String getColorHexShorthand() {
        return JColorUtils.toColorShorthand(getRedHex(), getGreenHex(),
            getBlueHex());
    }

    /**
     * Get the color as a hex string with alpha, shorthanded when possible.
     *
     * @return hex color in the format #ARGB or #AARRGGBB
     */
    public String getColorHexShorthandWithAlpha() {
        return JColorUtils.toColorShorthandWithAlpha(getRedHex(), getGreenHex(),
            getBlueHex(), getAlphaHex());
    }

    /**
     * Get the color as an integer.
     *
     * @return integer color
     */
    public int getColor() {
        return JColorUtils.toColor(getRed(), getGreen(), getBlue());
    }

    /**
     * Get the color as an integer including the alpha.
     *
     * @return integer color
     */
    public int getColorWithAlpha() {
        return JColorUtils.toColorWithAlpha(getRed(), getGreen(), getBlue(),
            getAlpha());
    }

    /**
     * Get the red color in hex.
     *
     * @return red hex color in format RR
     */
    public String getRedHex() {
        return JColorUtils.toHex(red);
    }

    /**
     * Get the green color in hex.
     *
     * @return green hex color in format GG
     */
    public String getGreenHex() {
        return JColorUtils.toHex(green);
    }

    /**
     * Get the blue color in hex.
     *
     * @return blue hex color in format BB
     */
    public String getBlueHex() {
        return JColorUtils.toHex(blue);
    }

    /**
     * Get the alpha color in hex.
     *
     * @return alpha hex color in format AA
     */
    public String getAlphaHex() {
        return JColorUtils.toHex(opacity);
    }

    /**
     * Get the red color in hex, shorthand when possible.
     *
     * @return red hex color in format R or RR
     */
    public String getRedHexShorthand() {
        return JColorUtils.shorthandHexSingle(getRedHex());
    }

    /**
     * Get the green color in hex, shorthand when possible.
     *
     * @return green hex color in format G or GG
     */
    public String getGreenHexShorthand() {
        return JColorUtils.shorthandHexSingle(getGreenHex());
    }

    /**
     * Get the blue color in hex, shorthand when possible.
     *
     * @return blue hex color in format B or BB
     */
    public String getBlueHexShorthand() {
        return JColorUtils.shorthandHexSingle(getBlueHex());
    }

    /**
     * Get the alpha color in hex, shorthand when possible.
     *
     * @return alpha hex color in format A or AA
     */
    public String getAlphaHexShorthand() {
        return JColorUtils.shorthandHexSingle(getAlphaHex());
    }

    /**
     * Get the red color as an integer.
     *
     * @return red integer color inclusively between 0 and 255
     */
    public int getRed() {
        return JColorUtils.toRGB(red);
    }

    /**
     * Get the green color as an integer.
     *
     * @return green integer color inclusively between 0 and 255
     */
    public int getGreen() {
        return JColorUtils.toRGB(green);
    }

    /**
     * Get the blue color as an integer.
     *
     * @return blue integer color inclusively between 0 and 255
     */
    public int getBlue() {
        return JColorUtils.toRGB(blue);
    }

    /**
     * Get the alpha color as an integer.
     *
     * @return alpha integer color inclusively between 0 and 255
     */
    public int getAlpha() {
        return JColorUtils.toRGB(opacity);
    }

    /**
     * Get the red color as an arithmetic float.
     *
     * @return red float color inclusively between 0.0 and 1.0
     */
    public float getRedArithmetic() {
        return red;
    }

    /**
     * Get the green color as an arithmetic float.
     *
     * @return green float color inclusively between 0.0 and 1.0
     */
    public float getGreenArithmetic() {
        return green;
    }

    /**
     * Get the blue color as an arithmetic float.
     *
     * @return blue float color inclusively between 0.0 and 1.0
     */
    public float getBlueArithmetic() {
        return blue;
    }

    /**
     * Get the opacity as an arithmetic float.
     *
     * @return opacity float inclusively between 0.0 and 1.0
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * Get the alpha color as an arithmetic float.
     *
     * @return alpha float color inclusively between 0.0 and 1.0
     */
    public float getAlphaArithmetic() {
        return getOpacity();
    }

    /**
     * Get the HSL (hue, saturation, lightness) values.
     *
     * @return HSL array where: 0 = hue, 1 = saturation, 2 = lightness
     */
    public float[] getHSL() {
        return JColorUtils.toHSL(red, green, blue);
    }

    /**
     * Get the HSL hue value.
     *
     * @return hue value
     */
    public float getHue() {
        return getHSL()[0];
    }

    /**
     * Get the HSL saturation value.
     *
     * @return saturation value
     */
    public float getSaturation() {
        return getHSL()[1];
    }

    /**
     * Get the HSL lightness value.
     *
     * @return lightness value
     */
    public float getLightness() {
        return getHSL()[2];
    }

    /**
     * Copy the color.
     *
     * @return color copy
     */
    public JColor copy() {
        return new JColor(this);
    }

}