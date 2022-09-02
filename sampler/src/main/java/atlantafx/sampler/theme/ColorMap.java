package atlantafx.sampler.theme;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static atlantafx.sampler.util.JColorUtils.opaqueColor;

// Theoretically, one should use different accent shades for each color theme
// and for both light and dark mode. But since creating color palettes is
// pretty time-consuming, dynamic color calculation based on opacity should
// suit demo purposes.
public class ColorMap {

    private static final String MUTED_COLOR_NAME = "-color-accent-muted";
    private static final String SUBTLE_COLOR_NAME = "-color-accent-subtle";

    private final Map<String, Color> allColors = new HashMap<>();
    private final Map<String, Color> dynamicColors = new HashMap<>();

    private final Color primaryColor;

    private ColorMap(Color primaryColor) {
        this.primaryColor = primaryColor;
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public Map<String, Color> getAll() {
        return new HashMap<>(allColors);
    }

    private void setColor(String colorName, Color colorValue) {
        allColors.put(Objects.requireNonNull(colorName), colorValue);
    }

    private void setDynamicColor(String colorName, Color colorValue) {
        dynamicColors.put(Objects.requireNonNull(colorName), colorValue);
    }

    void update(Color background) {
        allColors.put(MUTED_COLOR_NAME, opaqueColor(background, dynamicColors.get(MUTED_COLOR_NAME), 0.5));
        allColors.put(SUBTLE_COLOR_NAME, opaqueColor(background, dynamicColors.get(SUBTLE_COLOR_NAME), 0.4));
    }

    ///////////////////////////////////////////////////////////////////////////

    public static ColorMap primerPurple() {
        var map = new ColorMap(Color.web("#8250df"));

        map.setColor("-color-blue-0", Color.web("#fbefff"));
        map.setColor("-color-blue-1", Color.web("#ecd8ff"));
        map.setColor("-color-blue-2", Color.web("#d8b9ff"));
        map.setColor("-color-blue-3", Color.web("#c297ff"));
        map.setColor("-color-blue-4", Color.web("#a475f9"));
        map.setColor("-color-blue-5", Color.web("#8250df"));
        map.setColor("-color-blue-6", Color.web("#6639ba"));
        map.setColor("-color-blue-7", Color.web("#512a97"));
        map.setColor("-color-blue-8", Color.web("#3e1f79"));
        map.setColor("-color-blue-9", Color.web("#2e1461"));
        map.setColor("-color-accent-fg", Color.web("#8250df"));
        map.setColor("-color-accent-emphasis", Color.web("#8250df"));
        map.setColor(MUTED_COLOR_NAME, Color.web("#d8b9ff"));
        map.setColor(SUBTLE_COLOR_NAME, Color.web("#fbefff"));

        map.setDynamicColor(MUTED_COLOR_NAME, Color.web("#d8b9ff"));
        map.setDynamicColor(SUBTLE_COLOR_NAME, Color.web("#fbefff"));

        return map;
    }

    public static ColorMap primerPink() {
        var map = new ColorMap(Color.web("#bf3989"));

        map.setColor("-color-blue-0", Color.web("#ffeff7"));
        map.setColor("-color-blue-1", Color.web("#ffd3eb"));
        map.setColor("-color-blue-2", Color.web("#ffadda"));
        map.setColor("-color-blue-3", Color.web("#ff80c8"));
        map.setColor("-color-blue-4", Color.web("#e85aad"));
        map.setColor("-color-blue-5", Color.web("#bf3989"));
        map.setColor("-color-blue-6", Color.web("#99286e"));
        map.setColor("-color-blue-7", Color.web("#772057"));
        map.setColor("-color-blue-8", Color.web("#611347"));
        map.setColor("-color-blue-9", Color.web("#4d0336"));
        map.setColor("-color-accent-fg", Color.web("#bf3989"));
        map.setColor("-color-accent-emphasis", Color.web("#bf3989"));
        map.setColor(MUTED_COLOR_NAME, Color.web("#ffadda"));
        map.setColor(SUBTLE_COLOR_NAME, Color.web("#ffeff7"));

        map.setDynamicColor(MUTED_COLOR_NAME, Color.web("#ffadda"));
        map.setDynamicColor(SUBTLE_COLOR_NAME, Color.web("#ffeff7"));

        return map;
    }

    public static ColorMap primerCoral() {
        var map = new ColorMap(Color.web("#c4432b"));

        map.setColor("-color-blue-0", Color.web("#fff0eb"));
        map.setColor("-color-blue-1", Color.web("#ffd6cc"));
        map.setColor("-color-blue-2", Color.web("#ffb4a1"));
        map.setColor("-color-blue-3", Color.web("#fd8c73"));
        map.setColor("-color-blue-4", Color.web("#ec6547"));
        map.setColor("-color-blue-5", Color.web("#c4432b"));
        map.setColor("-color-blue-6", Color.web("#9e2f1c"));
        map.setColor("-color-blue-7", Color.web("#801f0f"));
        map.setColor("-color-blue-8", Color.web("#691105"));
        map.setColor("-color-blue-9", Color.web("#510901"));
        map.setColor("-color-accent-fg", Color.web("#c4432b"));
        map.setColor("-color-accent-emphasis", Color.web("#c4432b"));
        map.setColor(MUTED_COLOR_NAME, Color.web("#ffb4a1"));
        map.setColor(SUBTLE_COLOR_NAME, Color.web("#fff0eb"));

        map.setDynamicColor(MUTED_COLOR_NAME, Color.web("#ffb4a1"));
        map.setDynamicColor(SUBTLE_COLOR_NAME, Color.web("#fff0eb"));

        return map;
    }

    // empty map contains only color names and used to reset
    // accent color scale to its initial state
    static ColorMap empty() {
        var map = new ColorMap(Color.web("#bf3989"));

        map.setColor("-color-blue-0", null);
        map.setColor("-color-blue-1", null);
        map.setColor("-color-blue-2", null);
        map.setColor("-color-blue-3", null);
        map.setColor("-color-blue-4", null);
        map.setColor("-color-blue-5", null);
        map.setColor("-color-blue-6", null);
        map.setColor("-color-blue-7", null);
        map.setColor("-color-blue-8", null);
        map.setColor("-color-blue-9", null);
        map.setColor("-color-accent-fg", null);
        map.setColor("-color-accent-emphasis", null);
        map.setColor(MUTED_COLOR_NAME, null);
        map.setColor(SUBTLE_COLOR_NAME, null);

        return map;
    }
}
