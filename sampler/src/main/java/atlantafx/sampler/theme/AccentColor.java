package atlantafx.sampler.theme;

public enum AccentColor {

    PURPLE(ColorMap.primerPurple()),
    PINK(ColorMap.primerPink()),
    CORAL(ColorMap.primerCoral());

    private final ColorMap colorMap;

    AccentColor(ColorMap colorMap) {
        this.colorMap = colorMap;
    }

    public ColorMap getColorMap() {
        return colorMap;
    }
}
