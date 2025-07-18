package atlantafx.sampler.theme;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Themes {

    PRIMER_LIGHT(new PrimerLight(), "Primer Light"),
    PRIMER_DARK(new PrimerDark(), "Primer Dark"),
    NORD_LIGHT(new NordLight(), "Nord Light"),
    NORD_DARK(new NordDark(), "Nord Dark"),
    CUPERTINO_LIGHT(new CupertinoLight(), "Cupertino Light"),
    CUPERTINO_DARK(new CupertinoDark(), "Cupertino Dark"),
    DRACULA(new Dracula(), "Dracula");

    private final Theme theme;
    private final String name;

    Themes(Theme theme, String name) {
        this.theme = theme;
        this.name = name;
    }

    public Theme getInstance() {
        return theme;
    }

    public String getName() {
        return name;
    }

    public static List<Theme> getAll() {
        return Stream.of(values())
                   .map(Themes::getInstance)
                   .collect(Collectors.toList());
    }

    public static Optional<Theme> getByName(String name) {
        return Stream.of(values())
                   .filter(theme -> theme.getName().equalsIgnoreCase(name))
                   .map(Themes::getInstance)
                   .findFirst();
    }
}