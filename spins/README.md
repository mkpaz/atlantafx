# SPINS

This module contains a collection of skins for the `atlantafx.base.controls.Spin` control.

Maven:

```xml
<dependency>
    <groupId>io.github.mkpaz</groupId>
    <artifactId>atlantafx-spins</artifactId>
    <version>3.0.0</version>
</dependency>
```

Gradle:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.mkpaz:atlantafx-spins:3.0.0'
}
```

Each skin is designed to be self-contained and does not require the entire module to function.
If you only need a single skin for your project, feel free to copy and paste it directly.

The `atlantafx-styles` module already contains the required styling for all skins.
The styling is as simple as this:

```css
.spin-class-name {
  -spin-color-primary: -color-accent-emphasis;
  -spin-color-secondary: -color-accent-emphasis;
}
```

... and can be easily overridden.
