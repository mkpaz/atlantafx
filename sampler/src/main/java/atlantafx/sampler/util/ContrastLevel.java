/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.util;

import static atlantafx.sampler.util.JColorUtils.flattenColor;

import java.util.Arrays;
import javafx.scene.paint.Color;

/**
 * The WCAG contrast measures the difference in brightness (luminance) between two colours.
 * It ranges from 1:1 (white on white) to 21:1 (black on white). WCAG requirements are:
 * <ul>
 * <li>0.14285 (7.0:1) for small text in AAA-level</li>
 * <li>0.22222 (4.5:1) for small text in AA-level, or large text in AAA-level</li>
 * <li>0.33333 (3.0:1) for large text in AA-level</li>
 * </ul>
 * WCAG defines large text as text that is 18pt and larger, or 14pt and larger if it is bold.
 * <br/>
 * <a href="https://www.w3.org/TR/WCAG20-TECHS/G18.html">More info</a>.
 */
public enum ContrastLevel {

    AA_NORMAL,
    AA_LARGE,
    AAA_NORMAL,
    AAA_LARGE;

    public boolean satisfies(double ratio) {
        switch (this) {
            case AA_NORMAL, AAA_LARGE -> {
                return ratio >= 4.5;
            }
            case AA_LARGE -> {
                return ratio >= 3;
            }
            case AAA_NORMAL -> {
                return ratio >= 7;
            }
            default -> {
                return false;
            }
        }
    }

    public static double getContrastRatio(Color color1, Color color2) {
        return getContrastRatio(getColorLuminance(color1), getColorLuminance(color2));
    }

    public static double getContrastRatio(double luminance1, double luminance2) {
        var x = luminance1 > luminance2
            ? (luminance2 + 0.05) / (luminance1 + 0.05)
            : (luminance1 + 0.05) / (luminance2 + 0.05);
        return 1 / x;
    }

    public static double getContrastRatioOpacityAware(Color bgColor, Color fgColor, Color bgBaseColor) {
        double luminance1 = getColorLuminance(flattenColor(bgBaseColor, bgColor));
        double luminance2 = getColorLuminance(flattenColor(bgBaseColor, fgColor));
        return getContrastRatio(luminance1, luminance2);
    }

    /**
     * Measures relative color luminance according to the
     * <a href="https://www.w3.org/TR/WCAG20-TECHS/G18.html">W3C</a>.
     * <br/>
     * Note that JavaFX provides {@link Color#getBrightness()} which
     * IS NOT the same thing as luminance.
     */
    public static double getColorLuminance(double[] rgb) {
        double[] tmp = Arrays.stream(rgb)
            .map(v -> v <= 0.03928 ? (v / 12.92) : Math.pow((v + 0.055) / 1.055, 2.4))
            .toArray();
        return (tmp[0] * 0.2126) + (tmp[1] * 0.7152) + (tmp[2] * 0.0722);
    }

    /**
     * See {@link ContrastLevel#getColorLuminance}.
     */
    public static double getColorLuminance(Color color) {
        return getColorLuminance(new double[] {color.getRed(), color.getGreen(), color.getBlue()});
    }
}
