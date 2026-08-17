package atlantafx.base.theme;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ServiceLoader;

import static org.assertj.core.api.Assertions.assertThat;

class ThemeTest {

    @Test
    void testServiceLoader() {
        ServiceLoader<Theme> loader = ServiceLoader.load(Theme.class);

        List<Theme> themes = loader.stream()
            .map(ServiceLoader.Provider::get)
            .toList();

        assertThat(themes).hasSize(7);
        assertThat(themes).hasExactlyElementsOfTypes(
            PrimerLight.class, PrimerDark.class,
            NordLight.class, NordDark.class,
            CupertinoLight.class, CupertinoDark.class,
            Dracula.class
        );
    }
}