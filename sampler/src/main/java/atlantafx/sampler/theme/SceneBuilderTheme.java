package atlantafx.sampler.theme;

import java.util.List;
import java.util.Objects;

public record SceneBuilderTheme(String name, String url) {

    public static final List<SceneBuilderTheme> CASPIAN_THEMES = List.of(
        new SceneBuilderTheme(
            "Caspian Embedded (FX2)",
            "com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded.css"
        ),
        new SceneBuilderTheme(
            "Caspian Embedded QVGA (FX2)",
            "com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded-qvga.css"
        ),
        new SceneBuilderTheme(
            "Caspian High Contrast Embedded (FX2)",
            "com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded-highContrast.css"
        ),
        new SceneBuilderTheme(
            "Caspian High Contrast Embedded QVGA (FX2)",
            "com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded-qvga-highContrast.css"
        )
    );

    public SceneBuilderTheme {
        Objects.requireNonNull(name);
        Objects.requireNonNull(url);
    }
}