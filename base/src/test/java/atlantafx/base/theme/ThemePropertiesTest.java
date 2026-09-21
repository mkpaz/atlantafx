package atlantafx.base.theme;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class ThemePropertiesTest {

    private PrimerLight primerLight;
    private PrimerDark primerDark;
    private NordLight nordLight;

    @BeforeEach
    void setup() {
        primerLight = new PrimerLight();
        primerLight.getProperties().clear();

        primerDark = new PrimerDark();
        primerDark.getProperties().clear();

        nordLight = new NordLight();
        nordLight.getProperties().clear();

        ThemeProperties.getPropertiesFor(CupertinoDark.class).clear();
    }

    @Test
    @DisplayName("should set, get, and remove properties using Theme default methods")
    void testProperties() {
        primerLight.setProperty("accentColor", "#0022CC");
        primerLight.setProperty("fontSize", 14);

        String accentColor = primerLight.getProperty("accentColor");
        Integer fontSize = primerLight.getProperty("fontSize");

        assertThat(accentColor).isEqualTo("#0022CC");
        assertThat(fontSize).isEqualTo(14);

        primerLight.removeProperty("accentColor");

        assertThat(primerLight.<String>getProperty("accentColor")).isNull();
        assertThat(primerLight.<Integer>getProperty("fontSize")).isEqualTo(14);
    }

    @Test
    @DisplayName("should share properties across instances of the same theme class")
    void testPropertySharingAcrossInstances() {
        var primerLight2 = new PrimerLight();

        primerLight.setProperty("darkMode", false);

        Boolean isDarkMode = primerLight2.getProperty("darkMode");
        assertThat(isDarkMode).isNotNull().isFalse();

        // modify property via second instance and verify change in first instance
        primerLight2.setProperty("darkMode", true);
        assertThat(primerLight.<Boolean>getProperty("darkMode")).isTrue();
    }

    @Test
    @DisplayName("should allow direct property manipulation via ThemeProperties")
    void testDirectRegistryAccess() {
        Class<CupertinoDark> themeClass = CupertinoDark.class;

        // set properties directly on the class before any instance is created
        Map<String, Object> cupertinoProperties = ThemeProperties.getPropertiesFor(themeClass);
        cupertinoProperties.put("borderRadius", 8);
        cupertinoProperties.put("translucent", true);

        // verify instance reads registry properties
        var cupertinoDark = new CupertinoDark();
        assertThat(cupertinoDark.<Integer>getProperty("borderRadius")).isEqualTo(8);
        assertThat(cupertinoDark.<Boolean>getProperty("translucent")).isTrue();

        // modify via instance and verify directly in registry map
        cupertinoDark.setProperty("borderRadius", 12);
        assertThat(ThemeProperties.getPropertiesFor(themeClass)).containsEntry("borderRadius", 12);
    }

    @Test
    @DisplayName("should isolate properties between different theme classes")
    void testThemeIsolation() {
        // set distinct properties for each theme class
        primerLight.setProperty("accentColor", "#0022CC");
        primerDark.setProperty("accentColor", "#1122CC");
        nordLight.setProperty("accentColor", "#2222CC");

        // set unique property for one theme
        primerLight.setProperty("translucent", "enabled");

        // assert properties remain isolated
        assertThat(primerLight.<String>getProperty("accentColor")).isEqualTo("#0022CC");
        assertThat(primerDark.<String>getProperty("accentColor")).isEqualTo("#1122CC");
        assertThat(nordLight.<String>getProperty("accentColor")).isEqualTo("#2222CC");

        // verify key is absent in other themes
        assertThat(primerDark.<String>getProperty("translucent")).isNull();
        assertThat(nordLight.<String>getProperty("translucent")).isNull();
    }

    @Test
    @DisplayName("should return the same map instance from getProperties()")
    void testGetPropertiesReturnsSameMapInstance() {
        var mapFromInstance = primerLight.getProperties();
        var mapFromRegistry = ThemeProperties.getPropertiesFor(PrimerLight.class);

        // assert exact object reference equality
        assertThat(mapFromInstance).isSameAs(mapFromRegistry);
    }
}