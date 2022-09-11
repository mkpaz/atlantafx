---
title: Themization
nav_order: 4
---

AtlantaFX stylesheet is written in SASS. You don't have to learn it, though it's a very simple language, if you're already familiar with CSS. You can find a very good doc at [sass-lang.com](https://sass-lang.com/documentation/).

To compile SASS to CSS, AtlantaFX uses the Dart SASS library and very handy [`sass-cli-maven-plugin`](https://github.com/HebiRobotics/sass-cli-maven-plugin) by **@ennerf**.

Note, that theme is not limited by colors. If you only want to change colors, all you need is to override default looked-up [color variables]({{ site.baseurl }}{% link colors/global-variables.md %}). The easiest way is to utilize pseudo-class, so you can always return to the default color scheme.

```css
.root:custom-theme {
    -color-bg-default: #123456;
    /* ... and so on */
}
```

```java
static PseudoClass CUSTOM_THEME = PseudoClass.getPseudoClass("custom-theme");
getScene().getRoot().pseudoClassStateChanged(CUSTOM_THEME, true);
```

## Custom theme compilation

You can find ready to use custom theme template in the [`atlantafx-sample-theme`](https://github.com/mkpaz/atlantafx-sample-theme).

Compile it and grab resulting CSS from `dist/` directory:

```sh
# you can also (optionally) watch for changes
mvn compile [-Pwatch]
```

Every SCSS file in the source directory is nothing but separate SASS module. You can find a bunch of SASS variables at its top. If a variable is marked as `default` it can be changed during theme compilation. In fact, any AtlantaFX theme can be used as an example. They're all share common sources and use SASS variable modification to compile different stylesheets.

**If you want to customize a style property that is not exposed as SASS variable don't hesitate to open an [issue](https://github.com/mkpaz/atlantafx/issues) or send a [PR](https://github.com/mkpaz/atlantafx/pulls).**

Note that SASS is only loading any module just once, so customization order does matter. E.g. if A module uses B and B use C then we should override C variables first, then B, then A. Otherwise, there will be an exception that we are attempting to change a variable in a module that has been already loaded.

```sass
// Color customization.
@forward "relative/path/to/settings/color-vars" with (
    //   ...
);

// Shared property customization.
@forward "relative/path/to/settings/config" with (
    //   ...
);

// This should precede controls customization, as it guarantees
// that .root styles will precede components styles.
@use "general";

// Individual component property customization.
// Use "as name-*" to avoid conflicts if two or more SASS modules
// contain variables with the same name.
@forward "relative/path/to/components/split-pane" as split-pane-*  with (
    //   ...
);
```
