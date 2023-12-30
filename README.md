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
    <version>2.0.1</version>
</dependency>
```

Gradle:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.mkpaz:atlantafx-base:2.0.1'
}
```

Set a theme:

```java
// Essential imports for theme functionality:
import atlantafx.base.theme.PrimerDark;  // Enables the Primer Dark theme
//import atlantafx.base.theme.PrimerLight; // Enables the Primer Light theme

// ... the rest of the imports 

public class Launcher extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    // Theme configuration:
    // - Choose either PrimerLight or PrimerDark by uncommenting the respective line below.
    // - To explore more themes, locate them in the 'atlantafx.base.theme' package and import them accordingly.

    Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());  // Currently set to Primer Dark theme
    // Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());  // Uncomment for Primer Light theme

    // ... the rest of the code
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

## Making a Contribution, Participate and improve AtlantaFX.

We always appreciate contributions! Making modifications or just taking part in a discussion are two examples of contributing. If you wish to discuss a potential feature, report a bug, or ask a question, please start an issue.

Please remember that the main focus of AtlantaFX is a CSS theme library. Support for controls and skins will likely increase over time, although adding more controls to the library is not the primary objective.

Here are a few ways you may support the project:
1. Resolving or reporting bugs. If the bug you're experiencing has nothing to do with CSS or a specific AtlantaFX control, please check the [OpenJFX bug tracker](https://bugs.openjdk.org/browse/JDK-8294722?jql=project%20%3D%20JDK%20AND%20resolution%20%3D%20Unresolved%20AND%20component%20%3D%20javafx%20%20ORDER%20BY%20priority%20DESC%2C%20updated%20DESC) site first.
2. Increasing or enhancing control samples, which facilitates understanding of current controls and allows us to experiment with how controls appear and function with other themes.
3. Increasing or enhancing widget samples, which offer simple illustrations of how to use a few common UI elements.
4. Increasing or enhancing app showcases, which show off AtlantaFX's appearance in the real world and aid in identifying more areas in need of development.
5. Improving the documentation is crucial since it acts as the face of the project and represents it to developers and users.
6. Promoting the endeavor.

