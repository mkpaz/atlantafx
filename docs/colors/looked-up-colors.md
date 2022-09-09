---
title: Looked-up colors
parent: Colors
nav_order: 1
---

JavaFX has limited CSS 2.1 support, but the good news is that it supports color variables that are called as looked-up colors in the [JavaFX CSS reference](https://openjfx.io/javadoc/17). The second important thing is that looked-up colors as any other CSS property are resolved according to CSS specificity rules.

If you imagine the following hierarchy:

```text
Scene [class = root]
    Region [class = r1]
        Region [class = r2]
            Region [class = r3]
```

You can manipulate the background color of each descending node with the following CSS rules (most specific wins):

```css
.root {
    -color-background: transparent;
}

.r1, .r2, .r3 {
    -fx-background-color: -color-background;
}

.r2 {
    -color-background: red; /* applied to the r2 and below */
}

.r2 > .r3  {
    -color-background: green;
}
```

JavaFX will try to resolve color variable value starting from the most to the least specific rule, which is always the root of the Scene hierarchy.

Result:

```text
r1 - transparent
r2 - red
r3 - green
```
