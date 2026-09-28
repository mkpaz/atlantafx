<h3 align="center">
  <img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/sampler/icons/icon-rounded-64.png" alt="Logo"/><br/>
  AtlantaFX
</h3>

<p align="center">
    <a href="https://github.com/mkpaz/atlantafx/stargazers"><img src="https://img.shields.io/github/license/mkpaz/atlantafx?style=for-the-badge" alt="License"></a>
    <a href="https://github.com/mkpaz/atlantafx/releases"><img src="https://img.shields.io/github/v/release/mkpaz/atlantafx?5&style=for-the-badge" alt="Latest Version"></a>
    <a href="https://github.com/mkpaz/atlantafx/issues"><img src="https://img.shields.io/github/issues/mkpaz/atlantafx?style=for-the-badge" alt="Open Issues"></a>
    <a href="https://github.com/mkpaz/atlantafx/contributors"><img src="https://img.shields.io/github/contributors/mkpaz/atlantafx?5&style=for-the-badge" alt="Contributors"></a>
</p>
<p align="center">
  <a href="https://www.jfx-central.com/libraries/atlantafx"><img src="https://img.shields.io/badge/Find_me_on-JFXCentral-blue?logo=googlechrome&logoColor=white&style=for-the-badge" alt="JFXCentral"></a>
</p>

<p align="center">
Modern JavaFX CSS theme collection with additional controls.
</p>

<p align="center">
<img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/.screenshots/titlepage/blueprints_primer-light.png" alt="blueprints"/><br/>
<img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/.screenshots/titlepage/overview_primer-dark.png" alt="overview"/><br/>
<img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/.screenshots/titlepage/toolbar_dracula.png" alt="page"/><br/>
<img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/.screenshots/titlepage/notifications_cupertino-dark.png" alt="page"/><br/>
</p>

* Flat interface inspired by the variety of Web component frameworks.
* CSS first! It works with existing JavaFX controls.
* Multiple themes in both light and dark variants.
* Simple and intuitive color system.
* Fully customizable. Easily change global accent (brand) color or individual control via looked-up
  color variables.
* Written in modular [SASS](https://sass-lang.com/). No more digging in 3,500 lines of CSS code.
* [Custom themes support](https://github.com/mkpaz/atlantafx-sample-theme). Compile your own theme
  from existing SASS sources.
* Additional controls for modern GUI development.
* Beautiful demo app:
  * Preview all supported themes.
  * Test every feature of each existing control and check source code directly in the app to learn
    how to implement it.
  * Check color palette and modify theme color contrast.
  * Hot reload. Play with control styles without restarting the whole app.
  * Showcases to demonstrate real-world project usage.
* Custom [window decorations](https://github.com/mkpaz/atlantafx/tree/master/decorations) support
  (aka JavaFX controls in the title bar).
* Fluent [validation API](https://github.com/mkpaz/atlantafx/tree/master/validation) designed for
  JavaFX applications.
* Scene Builder [integration](https://github.com/mkpaz/atlantafx/tree/master/scene-builder).

## Getting started

**Requirements:** Java 25+ and JavaFX 27+.

Maven:

```xml
<dependency>
    <groupId>io.github.mkpaz</groupId>
    <artifactId>atlantafx-base</artifactId>
    <version>3.0.0</version>
</dependency>
```

Gradle:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.mkpaz:atlantafx-base:3.0.0'
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

        // the rest of the code ...
    }
}
```

Or use the `ThemeManager`:

```java
// set theme based on platform color scheme preference
// or pick theme from Java properties if run with -Datlantafx.theme=name
ThemeManager.useDefault()

// ... or set theme manually
ThemeManager.instance().setTheme(new PrimerLight())
```

### Starter Project

If you use Maven you can quickly create a new project with AtlantaFX using the
[starter](https://github.com/mkpaz/atlantafx-maven-starter):

```sh
git clone https://github.com/mkpaz/atlantafx-maven-starter
```

### Local Installation

If you don't want to use additional dependencies, you can download compiled CSS themes from the
[GitHub Releases](https://github.com/mkpaz/atlantafx/releases). Unpack `AtlantaFX-*-themes.zip`
and place it to your project's classpath.

Set CSS theme:

```text
// specify the theme stylesheet URI directly:
Application.setUserAgentStylesheet(URI);

// ... or use a Java property:
-Djavafx.userAgentStylesheetUrl=[URI]
```

## Documentation

Refer to the relevant module documentation for more information about its usage.

* [Base Controls and Themes](base/README.md)
* [Theming and Styles](styles/README.md)
* [Decorations](decorations/README.md)
* [Validation Support](validation/README.md)
* [Sampler Demo Application](sampler/README.md)
* [Scene Builder Plugin](scene-builder/README.md)
* [Custom Spins aka Loading Indicators](spins/README.md)

## Contributing

> [!NOTE]
> Check [CONTRIBUTING.md](CONTRIBUTING.md) if you need more information about the project structure
> and build instructions.

Contributions are always welcome! Feel free to open an issue if you've found a bug or want to raise
a question, or discuss a possible feature.

Please note that AtlantaFX is primarily a CSS theme library. Controls and skins support will probably
grow over time, but creating another controls library is not the main goal.

Here are some areas where you can help the project:

1. Fixing or reporting bugs. Please check the [OpenJFX bug tracker](https://bugs.openjdk.org/browse/JDK-8378253?jql=project%20%3D%20%2210100%22%20AND%20component%20%3D%20javafx)
first if the bug you're experiencing is not related to CSS or a custom AtlantaFX control.
2. Adding or improving control samples, which help people learn more about existing controls.
   They also let us test how controls look and work with different themes.
3. Adding or improving widget samples, which provide basic examples of how to implement some
   conventional UI components.
4. Adding or improving app showcases, which demonstrate how AtlantaFX looks in the real world and
   help to find more areas for improvement.
5. Improving docs, because good docs are the face of the project.
6. Advertising the project.
