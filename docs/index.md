---
# To modify the layout, see https://jekyllrb.com/docs/themes/#overriding-theme-defaults
layout: home
title: Home
nav_order: 1
---

## Getting started

Add project to the dependencies:

```xml
<dependency>
    <groupId>io.github.mkpaz</groupId>
    <artifactId>atlantafx-base</artifactId>
    <version>1.0.0</version>
</dependency>
```

If you don't want to include additional dependencies into your project classpath, you can download compiled CSS themes from the [GitHub Releases](https://github.com/mkpaz/atlantafx/releases).

Set CSS theme:

```java
// find more themes in 'atlantafx.base.theme' package
Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
// or
Application.setUserAgentStylesheet(/* path to the CSS file in your project classpath */);
```
