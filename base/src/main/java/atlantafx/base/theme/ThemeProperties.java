package atlantafx.base.theme;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry for managing properties associated with {@link Theme} classes.
 *
 * <p>This class associates a property map with each theme class. Properties are tied to
 * the theme's {@link Class} rather than individual instances, allowing shared configuration
 * across all instances of a specific theme implementation.
 *
 * <p>While properties can be accessed and modified directly using {@link #getPropertiesFor(Class)},
 * it is often more convenient to manage theme properties through the default methods provided by the
 * {@link Theme} interface.
 *
 * <h3>Example</h3>
 *
 * <p>Accessing properties via the {@link Theme} interface:
 *
 * <pre>{@code
 * var theme = new PrimerLight();
 * theme.setProperty("accentColor", "#0066CC");
 *
 * // any other instance of the same theme class shares this property
 * var anotherTheme = new PrimerLight();
 * var color = anotherTheme.getProperty("accentColor"); // returns "#0066CC"
 * }</pre>
 *
 * <p>Direct registry management via {@code ThemeProperties}:
 *
 * <pre>{@code
 * // configure theme properties directly by class before instantiating objects
 * Map<String, Object> properties = ThemeProperties.getPropertiesFor(PrimerLight.class);
 * properties.put("fontSize", 14);
 * properties.put("darkMode", false);
 * }</pre>
 *
 * @see Theme
 * @see Theme#getProperties()
 */
public final class ThemeProperties {

    private ThemeProperties() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Lazily computes and holds a property map for each registered theme class.
     */
    private static final ClassValue<Map<String, Object>> PROPERTIES = new ClassValue<>() {

        @Override
        protected Map<String, Object> computeValue(Class<?> type) {
            return new HashMap<>();
        }
    };

    /**
     * Retrieves the mutable property map for the specified theme class.
     *
     * <p>If no map currently exists for the given class, a new empty map is automatically
     * instantiated and associated with the class.
     *
     * @param themeClass the theme class whose property map is to be retrieved
     * @return the mutable map containing the properties associated with the specified theme class
     */
    public static Map<String, Object> getPropertiesFor(Class<? extends Theme> themeClass) {
        return PROPERTIES.get(themeClass);
    }
}