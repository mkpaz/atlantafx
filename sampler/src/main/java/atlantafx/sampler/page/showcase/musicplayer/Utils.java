/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.musicplayer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

final class Utils {

    public static WritableImage copyImage(Image source) {
        int height = (int) source.getHeight();
        int width = (int) source.getWidth();
        var reader = source.getPixelReader();
        var target = new WritableImage(width, height);
        var writer = target.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, y, color);
            }
        }
        return target;
    }

    public static Color getDominantColor(Image image, double opacity) {
        int[] dominant = ColorThief.getColor(SwingFXUtils.fromFXImage(image, null));
        if (dominant == null || dominant.length != 3) {
            return Color.TRANSPARENT;
        }
        return Color.rgb(dominant[0], dominant[1], dominant[2], opacity);
    }

    public static String formatDuration(Duration duration) {
        long seconds = (long) duration.toSeconds();
        return seconds < 3600
            ? String.format("%02d:%02d", (seconds % 3600) / 60, seconds % 60)
            : String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }
}
