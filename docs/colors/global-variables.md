---
title: Global variables
parent: Colors
nav_order: 2
---

Global variables are defined at the Scene root level. You can preview all of the them in the Sampler app.

AtlantaFX is based on GitHub Primer color system. You can check [GitHub Primer interface guidelines](https://primer.style/design/foundations/color) for more detailed instructions. There'are functional color variables and color scale variables.

## Functional colors

Foreground colors.

| Color                | Usage                                                                                                                            |
|----------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `-color-fg-default`  | Primary color for text and icons. It should be used for body content, titles and labels.                                         |
| `-color-fg-muted`    | For content that is secondary or that provides additional context but is not critical to understanding the flow of an interface. |
| `-color-fg-subtle`   | For placeholders or decorative foregrounds.                                                                                      |
| `-color-fg-emphasis` | The text color designed to combine with `*-emphasis` backgrounds for optimal contrast.                                           |

Background colors.

| Color               | Usage                                                              |
|---------------------|--------------------------------------------------------------------|
| `-color-bg-default` | Primary background color.                                          |
| `-color-bg-overlay` | Background color for popup controls such as popovers and tooltips. |
| `-color-bg-subtle`  | Provides visual rest and contrast against the default background.  |
| `-color-bg-inset`   | For a focal point, such as in conversations or activity feeds.     |

Border colors.

| Color                   | Usage                                                                        |
|-------------------------|------------------------------------------------------------------------------|
| `-color-border-default` | Default color to create bounds around content.                               |
| `-color-border-muted`   | For dividers to emphasize the separation between items, columns or sections. |
| `-color-border-subtle`  | Faint border color.                                                          |
| `-color-shadow-default` | Color for creating shadow effects around controls.                           |

The below colors are all accent colors. Use them according to their role. The variable names are self-explaining.

Neutral colors. Use to highlight content without any added meaning.

* `-color-neutral-emphasis-plus`
* `-color-neutral-emphasis`
* `-color-neutral-muted`
* `-color-neutral-subtle`

Accent (or primary) color. Use to draw attention to the particular area or component.

* `-color-accent-fg`
* `-color-accent-emphasis`
* `-color-accent-muted`
* `-color-accent-subtle`

Success colors. Use to express the completion or positive outcome of a task.

* `-color-success-fg`
* `-color-success-emphasis`
* `-color-success-muted`
* `-color-success-subtle`

Attention colors. Use to warn of pending tasks or highlight active content.

* `-color-warning-fg`
* `-color-warning-emphasis`
* `-color-warning-muted`
* `-color-warning-subtle`

Danger colors. Use to inform of error or another negative message.

* `-color-danger-fg`
* `-color-danger-emphasis`
* `-color-danger-muted`
* `-color-danger-subtle`

*Note that a functional color values is not always picked from the corresponding color palette. It can have its own unique value, e.g. to add opacity.*

## Scale variables

Generally, scale variables only supposed to be used by theme devs as replacement of dynamic brightness calculation functions. Avoid referencing them directly when building UI that needs to adapt to different color themes. Instead, use the functional variables listed above. All legitimate functional color combinations are guaranteed to look good in all color themes, because they need to maintain a certain amount of contrast. If you're using scale variable as a part of that combination it may break in another theme. In rare cases, you may need to use scale variables to define custom functional variables in your application.

Each color scale consists of 10 shades from 0 to 9.

* `-color-dark`
* `-color-light`
* `-color-base-[0-9]`
* `-color-accent-[0-9]`
* `-color-success-[0-9]`
* `-color-warning-[0-9]`
* `-color-danger-[0-9]`
