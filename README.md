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
<p align="center"><b>
See the <a href="https://mkpaz.github.io/atlantafx/">docs</a> for more info.
</b></p>

<p align="center">
<img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/.screenshots/titlepage/blueprints_primer-light.png" alt="blueprints"/><br/>
<img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/.screenshots/titlepage/overview_primer-dark.png" alt="overview"/><br/>
<img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/.screenshots/titlepage/toolbar_dracula.png" alt="page"/><br/>
<img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/.screenshots/titlepage/notifications_cupertino-dark.png" alt="page"/><br/>
</p>

* Flat interface inspired by the variety of Web component frameworks.
* CSS first! It works with existing JavaFX controls.
* Two themes in both light and dark variants.
* Simple and intuitive color system based on the [GitHub Primer guidelines](https://primer.style/design/foundations/color).
* Fully customizable. Easily change global accent (brand) color or individual control via looked-up color variables.
* Written in modular [SASS](https://sass-lang.com/). No more digging in 3,500 lines of CSS code.
* [Custom themes support](https://github.com/mkpaz/atlantafx-sample-theme). Compile your own theme from existing SASS sources.
* Additional controls that essential for modern GUI development.
* Beautiful demo app:
  * Preview all supported themes.
  * Test every feature of each existing control and check source code directly in the app to learn how to implement it.
  * Check color palette and modify theme color contrast.
  * Hot reload. Play with control styles without restarting the whole app.
  * Showcases to demonstrate real-world project usage.

## Try it out

Grab a **[self-updating download of the Sampler app](https://downloads.hydraulic.dev/atlantafx/sampler/download.html)** for Windows, macOS and Linux, packaged with [Conveyor](https://www.hydraulic.software).

Or download the latest build on the [releases page](https://github.com/mkpaz/atlantafx/releases).

## Getting started

**Requirements:** JavaFX 17+ (because of `data-url` support).

Maven:

```xml
<dependency>
    <groupId>io.github.mkpaz</groupId>
    <artifactId>atlantafx-base</artifactId>
    <version>2.1.0</version>
</dependency>
```

Gradle:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.mkpaz:atlantafx-base:2.1.0'
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

### Starter Project

If you use Maven you can quickly create a new project with AtlantaFX using the [starter](https://github.com/mkpaz/atlantafx-maven-starter):

```sh
git clone https://github.com/mkpaz/atlantafx-maven-starter
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

## Custom Themes

AtlantaFX is written in modular SASS, so you can create your own CSS themes using AtlantaFX. 
Here is the sample [repository](https://github.com/mkpaz/atlantafx-sample-theme):

```sh
git clone https://github.com/mkpaz/atlantafx-sample-theme
```

## Contributing

Contributions are always welcome! Contributing can mean many things such as participating in discussion or proposing changes. Feel free to open an issue if you've found a bug or want to raise a question, or discuss a possible feature.

Please, note that AtlantaFX is primarily CSS theme library. Controls and skins support will probably grow over time, but creating yet another control's library is not a main goal.

Here are some areas, where you can help the project:

1. Fixing or reporting bugs. Please, check [OpenJFX bug tracker](https://bugs.openjdk.org/browse/JDK-8294722?jql=project%20%3D%20JDK%20AND%20resolution%20%3D%20Unresolved%20AND%20component%20%3D%20javafx%20%20ORDER%20BY%20priority%20DESC%2C%20updated%20DESC) first if the bug you're experiencing isn't related to CSS or custom AtlantaFX control.
2. Adding or improving control samples, which helps people to learn more about existing controls and we can also test how controls look and work with different themes.
3. Adding or improving widget samples, which provides basic examples of how to implement some conventional UI components.
4. Adding or improving app showcases, which demonstrates how AtlantaFX looks in real-world that helps to find more areas for improvement.
5. Improving docs, because good docs is the face of the project.
6. Advertising the project.
