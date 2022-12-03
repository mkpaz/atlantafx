![logo](./.screenshots/logo.png)

![](https://img.shields.io/github/license/mkpaz/atlantafx)
![](https://img.shields.io/github/v/release/mkpaz/atlantafx)
![](https://img.shields.io/github/last-commit/mkpaz/atlantafx/master)

**[Docs](https://mkpaz.github.io/atlantafx/) | [Screenshots](https://github.com/mkpaz/atlantafx/tree/master/.screenshots)**

JavaFX CSS theme collection with additional controls.

* Modern flat interface inspired by the variety of Web component frameworks.
* CSS first. It works with existing JavaFX controls.
* Two themes in both light and dark variants. 
* Simple and intuitive color system based on the [GitHub Primer guidelines](https://primer.style/design/foundations/color).
* Fully customizable. Easily change global accent (brand) color or individual control via looked-up color variables.
* Written in modular [SASS](https://sass-lang.com/). No more digging in 3,500 lines of CSS code.
* [Custom themes support](https://github.com/mkpaz/atlantafx-sample-theme). Compile your own theme from existing SASS sources.
* Additional controls that essential for modern GUI development.
* Sampler app:
  * Play with themes and fonts.
  * Test every feature of each existing control and check source code directly in the app to learn how to implement it.
  * Check color palette and modify theme color contrast.
  * Hot reload. Play with control styles without restarting the whole app.
  * Showcases to demonstrate real-world project usage.

![alt](./.screenshots/demo.gif)

## Try it out

Grab a **[self-updating download of the Sampler app](https://downloads.hydraulic.dev/atlantafx/sampler/download.html)** for Windows, macOS and Linux, packaged with [Conveyor](https://www.hydraulic.software).

Or download the latest build on the [releases page](https://github.com/mkpaz/atlantafx/releases).

## Getting started

**Requirements:** JavaFX 17+ (because of `data-url` support).

```xml
<dependency>
    <groupId>io.github.mkpaz</groupId>
    <artifactId>atlantafx-base</artifactId>
    <version>1.1.0</version>
</dependency>
```

Set CSS theme:

```java
Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
// ... find more themes in 'atlantafx.base.theme' package
```

Check the [docs](https://mkpaz.github.io/atlantafx/) for more information.

## SceneBuilder Integration

While SceneBuilder does not support adding custom themes, it is possible to overwrite looked up css paths to make the existing buttons load custom css files. In order to use AtlantaFX in SceneBuilder you need to

1. run `mvn package -pl styles` to generate theme css files with the correct path names
2. copy `styles/target/AtlantaFX-${version}-scenebuilder.zip` to the SceneBuilder `$APPDIR` (e.g. `%HOMEPATH%/Local/SceneBuilder/app/` on Windows) or another directory of your choice
3. open `SceneBuilder.cfg` in the SceneBuilder app directory and add the zip file to the beginning of the `app.classpath` variable (e.g.  `app.classpath=$APPDIR\AtlantaFX-${version}-scenebuilder.zip;$APPDIR\scenebuilder-18.0.0-all.jar` on Windows)

After restarting SceneBuilder you can now select AtlantaFX themes in the menu `Preview -> Themes -> Caspian Embedded (FX2)`. The themes are mapped as follows

| SceneBuilder                | Modifier                    | AtlantaFX Theme |
|-----------------------------|-----------------------------|-----------------|
| Caspian Embedded (FX2)      | None                        | Primer Light    |
| Caspian Embedded (FX2)      | Caspian High Contrast (FX2) | Primer Dark     |
| Caspian Embedded QVGA (FX2) | None                        | Nord Light      |
| Caspian Embedded QVGA (FX2) | Caspian High Contrast (FX2) | Nord Dark       |

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

Also, if you created a custom AtlantaFX theme or using AtlantaFX in your app feel free to open an issue, and I will add the link to the project page.

## Motivation

**Goals**:

* SASS

  JavaFX standard themes, namely Modena and Caspian, maintained as a huge single CSS file, which is an overwhelmingly hard task. This alone makes creating a new JavaFX theme from scratch hardly possible. Also, JavaFX styling is based on CSS v2.1 specification which does not provide, nor variables, nor mixins, nor modules nor any other goodies that are default for modern front-end development. AtlantaFX themes are written in SASS with each component in a separate module and use recent [Dart SASS](https://sass-lang.com/dart-sass) implementation for CSS compilation. It also follows [GitHub Primer](https://primer.style/design/foundations/color) color system to make creating new themes more simple.

* Additional controls

  JavaFX 2.0 was started in 2011, and it introduced no additional controls since then. Some JavaFX controls are obsolete, some can be found in popular third-party libraries like [ControlsFX](https://github.com/controlsfx/controlsfx). The problem with the latter is that it provides much more than some missing controls. It provides many things that can be called a widget. That's why AtlantaFX borrows some existing controls from ControlsFX instead of supporting it directly. The rule of the thumb is to not re-invent any existing control from `javafx-controls` and to avoid widgets as well as everything that requires i18n support.

* Sampler application

  Theme development is not possible without some kind of demo application where you can test each control under every angle. That's what the Sampler application is. It supports hot reload, thanks to [cssfx](https://github.com/McFoggy/cssfx), and you can observe the scene graph via [Scenic View](https://github.com/JonathanGiles/scenic-view).

* Distribution and flexibility

  AtlantaFX is also distributed as a collection of CSS files. So, if you don't need additional controls, you can just download only CSS and use it via `Application.setUserAgentStylesheet()` method. If your application is only needs a subset of controls, you can compile your own theme by just removing unnecessary components from SASS.

**Non-goals**:

* Replacing `javafx-controls` or standard JavaFX themes

  It's not a goal to re-invent any existing `javafx-controls` component or replace standard JavaFX themes. Libraries come and gone, but committing to the core project benefits all the community.

* Mobile support

  This is a tremendous amount of work. Just use [Gluon Mobile](https://gluonhq.com/products/mobile/).

* Providing theme API

  AtlantaFX provides the Theme interface, which is nothing but a simple wrapper around the stylesheet path. [PR](https://github.com/openjdk/jfx/pull/511) is on its way, let's hope it will ever be merged.
