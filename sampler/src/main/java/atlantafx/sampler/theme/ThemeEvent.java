package atlantafx.sampler.theme;

public record ThemeEvent(EventType eventType) {

    public enum EventType {
        THEME_CHANGE,
        FONT_FAMILY_CHANGE,
        FONT_SIZE_CHANGE,
        CUSTOM_CSS_CHANGE
    }
}
