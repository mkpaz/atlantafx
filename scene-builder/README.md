# Scene Builder plugin

This plugin integrates AtlantaFX themes and controls
with [Gluon Scene Builder](https://github.com/gluonhq/scenebuilder).

**Required Scene Builder version**: 25+ (tested with 26).

## Installation

1. Build the plugin from sources or download the latest JAR version from the Releases page (**TBD**).

    ```sh
    mvn install
    ```

2. Find the Scene Builder installation directory and look for the app directory inside it. Depending on the
   operating system:

   ```text
   <scene-builder>/app             # Windows
   <scene-builder>/lib/app         # Linux 
   <scene-builder>/Resources/app   # MacOS 
   ```

3. Copy the plugin JAR into the `app` directory and edit `SceneBuilder.cfg`. You need to add the plugin JAR to the
   application's classpath. Be careful with the classpath separator - it's a colon on POSIX systems and semicolon on
   Windows:

   ```text
   [Application]
   app.classpath=$APPDIR/scenebuilder-26.0.0-all.jar:$APPDIR/atlantafx-scene-builder-2.1.0.jar
   ```

## Features

1. Switch between AtlantaFX themes via either the Preferences or the Preview. Note that they are not synchronized, as
   the latter is a hard-coded FXML menu.
2. All AtlantaFX controls are already included in the library.

<img src="https://raw.githubusercontent.com/mkpaz/atlantafx/master/.screenshots/scene-builder/main.png" alt="SceneBuilder"/><br/>
