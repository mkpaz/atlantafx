# Getting Started

AtlantaFX has *no dependencies*. If you want to use additional controls it provides, continue with Maven installation. If you just need to style standard JavaFX controls, you can also use the local installation, but in that case you'll have to download and update CSS files manually.

!!! info
    Any AtlantaFX theme is compiled to a user-agent stylesheet. It's a <ins>single CSS file</ins> that is used to provide default styling for all UI controls. It completely **replaces** default JavaFX stylesheet, namely [Modena](https://github.com/openjdk/jfx/blob/master/modules/javafx.controls/src/main/resources/com/sun/javafx/scene/control/skin/modena/modena.css) theme.

## Installing

Add project to the dependencies.

Maven:

```xml
<dependency>
    <groupId>io.github.mkpaz</groupId>
    <artifactId>atlantafx-base</artifactId>
    <version>2.0.0</version>
</dependency>
```

Gradle:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.mkpaz:atlantafx-base:2.0.0'
}
```

Set a theme:

```java
public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // find more themes in 'atlantafx.base.theme' package
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // the rest of the code ...
    }
}
```

### Local Installation

If you don't want to use additional dependencies, you can download compiled CSS themes from the [GitHub Releases](https://github.com/mkpaz/atlantafx/releases). Unpack `AtlantaFX-*-themes.zip` and place it to your project's classpath.

Set CSS theme:

```java
Application.setUserAgentStylesheet(/* path to the CSS file */);
```

Or use Java property:

```text
-Djavafx.userAgentStylesheetUrl=[URL]
```
