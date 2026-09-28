# DECORATIONS

This module contains an API and a collection of stylesheets to support the "JavaFX controls in the
title bar" feature ([JDK-8313424](https://bugs.openjdk.org/browse/JDK-8313424)), which is also
known as Client-Side Decorations (CSD), hence the module name.

Maven:

```xml

<dependency>
    <groupId>io.github.mkpaz</groupId>
    <artifactId>atlantafx-decorations</artifactId>
    <version>3.0.0</version>
</dependency>
```

Gradle:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.mkpaz:atlantafx-decorations:3.0.0'
}
```

## Usage

```java
var headerBar = new HeaderBar();
var windowsButtons = HeaderButtonGroup.standardGroup();
windowsButtons.install(headerBar, stage);

var root = new BorderPane();
root.setTop(headerBar);

var scene = new Scene(root, 800, 600);
// you can find a variety of predefined themes in the Decoration enum
scene.getStylesheets().add(Decoration.WIN10_LIGHT.getStylesheet());

stage.initStyle(StageStyle.EXTENDED);
stage.setScene(scene);
stage.setOnCloseRequest(e -> windowsButtons.uninstall(headerBar, stage));
stage.show();
```
