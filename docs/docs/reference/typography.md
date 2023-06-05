# Typography

AtlantaFX doesn't include any fonts, it uses operating system font family.

Default font size is `14px` (`~= 11pt`). You're free to change it, but please note that all stylesheets are tested with default font size only, so some controls may break with this change.

## Font size

* `title-1`
* `title-2`
* `title-3`
* `title-4`
* `text-caption`
* `text-small`

## Font color

*Note that accent color support requires two CSS styles in order to reuse existing class names.*

* `text`, `accent`
* `text`, `success`
* `text`, `warning`
* `text`, `danger`
* `text-muted`
* `text-subtle`

## Font weight

!!! warning
    JavaFX only supports bold or regular font weight. Other values are recognized by CSS parser, but reduced to one them.

* `text-bold`
* `text-bolder`
* `text-normal`
* `text-lighter`

## Font style

* `text-italic`
* `text-oblique`
* `text-underlined`
* `text-strikethrough`
