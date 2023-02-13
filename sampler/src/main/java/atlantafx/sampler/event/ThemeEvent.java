package atlantafx.sampler.event;

public class ThemeEvent extends Event {

    private final EventType eventType;

    public ThemeEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public enum EventType {
        // theme can change both, base font size and colors
        THEME_CHANGE,
        // font size or family only change
        FONT_CHANGE,
        // colors only change
        COLOR_CHANGE,
        // new theme added or removed
        THEME_ADD,
        THEME_REMOVE
    }

    @Override
    public String toString() {
        return "ThemeEvent{"
            + "eventType=" + eventType
            + "} " + super.toString();
    }
}
