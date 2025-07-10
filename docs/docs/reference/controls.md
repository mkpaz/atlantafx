# Controls

This reference lists all supported custom CSS classes and color variables.

Standard options are described in the [JavaFX CSS Reference](https://openjfx.io/javadoc/19/javafx.graphics/javafx/scene/doc-files/cssref.html).

## Button

CSS classes:

* `accent`, `success`, `danger` (accent color support)
* `button-circle`
* `button-icon`
* `button-outlined`
* `flat`
* `left-pill`, `center-pill`, `right-pill` (input group support)
* `rounded`
* `small`, `large` (size support)

Color variables:

* `-color-button-bg`
* `-color-button-fg`
* `-color-button-border`
* `-color-button-bg-hover`
* `-color-button-fg-hover`
* `-color-button-border-hover`
* `-color-button-bg-focused`
* `-color-button-fg-focused`
* `-color-button-border-focused`
* `-color-button-bg-pressed`
* `-color-button-fg-pressed`
* `-color-button-border-pressed`

## ChoiceBox

CSS classes:

* `.alt-icon` (tweak)
* `left-pill`, `center-pill`, `right-pill` (input group support)

Pseudo-classes:

* `:success`, `:danger`

## ComboBox

The same as [`ChoiceBox`](#choicebox).

## Data Iterators

This includes all virtualized controls such as ListView, TreeView, TableView and TreeTableView.

**NOTE:**

The default cell height is fixed. Set `-fx-cell-size: -1` CSS property to  use cell height based on content.

### Common

CSS classes:

* `.dense`
* `.edge-to-edge` (tweak)

Color variables:

* `-color-cell-bg`
* `-color-cell-fg`
* `-color-cell-bg-selected`
* `-color-cell-fg-selected`
* `-color-cell-bg-selected-focused`
* `-color-cell-fg-selected-focused`
* `-color-cell-bg-odd`
* `-color-cell-border`

### TableView

CSS classes:

* `align-left`, `align-center`, `align-right` (column alignment must be applied to the TableColumn)
* `.bordered`
* `.striped`

Color variables:

* `-color-header-bg`
* `-color-header-fg`

### TreeView

CSS classes:

* `.alt-icon` (tweak)

Color variables:

* `-color-disclosure`

### TreeTableView

Inherits all [TableView](#tableview) and [TreeView](#treeview) CSS classes and variables

## DatePicker

Color variables:

* `-color-date-bg`
* `-color-date-border`
* `-color-date-month-year-bg`
* `-color-date-month-year-fg`
* `-color-date-day-bg`
* `-color-date-day-bg-hover`
* `-color-date-day-bg-selected`
* `-color-date-day-fg`
* `-color-date-day-fg-hover`
* `-color-date-day-fg-selected`
* `-color-date-week-bg`
* `-color-date-week-fg`
* `-color-date-today-bg`
* `-color-date-today-fg`
* `-color-date-other-month-fg`
* `-color-date-chrono-fg`

## Hyperlink

Color variables:

* `-color-link-fg`
* `-color-link-fg-visited`
* `-color-link-fg-armed`

## Label

CSS classes:

* `accent`, `success`, `warning`, `danger`, `text-muted`, `text-subtle` (text color support)
* `left-pill`, `center-pill`, `right-pill` (input group support)
* `small`, `large` (size support)

Pseudo-classes:

* `:accent`, `:success`, `:warning`, `:danger` (text color support)

## MenuButton

CSS classes:

* `accent`, `success`, `danger` (accent color support)
* `button-icon`
* `button-outlined`
* `flat`
* `left-pill`, `center-pill`, `right-pill` (input group support)
* `no-arrow` (tweak)

Color variables:

* `-color-button-bg`
* `-color-button-fg`
* `-color-button-border`
* `-color-button-bg-hover`
* `-color-button-fg-hover`
* `-color-button-border-hover`
* `-color-button-bg-focused`
* `-color-button-fg-focused`
* `-color-button-border-focused`
* `-color-button-bg-pressed`
* `-color-button-fg-pressed`
* `-color-button-border-pressed`

## Pagination

CSS classes:

* `.bullet` (`Pagination.STYLE_CLASS_BULLET`)

## ProgressBar

Color variables:

* `-color-progress-bar-track`
* `-color-progress-bar-fill`

## RingProgressIndicator

Color variables:

* `-color-progress-indicator-track`
* `-color-progress-indicator-fill`

## Separator

Color variables:

* `-color-separator`

## Slider

CSS classes

* `small`, `large` (size support)

Color variables:

* `-color-slider-thumb`
* `-color-slider-thumb-border`
* `-color-slider-track`
* `-color-slider-track-progress`
* `-color-slider-tick`

## SplitMenuButton

The same as [`MenuButton`](#menubutton).

## SplitPane

Color variables:

* `-color-split-divider`
* `-color-split-divider-pressed`
* `-color-split-grabber`
* `-color-split-grabber-pressed`

## TextInput

This includes all text controls such as TextField, TextArea, PasswordField and CustomTextField.

CSS classes:

* `left-pill`, `center-pill`, `right-pill` (input group support)
* `rounded`
* `small`, `large` (size support)

Pseudo-classes:

* `:success`, `:danger`

Color variables:

* `-color-input-bg`
* `-color-input-fg`
* `-color-input-border`
* `-color-input-bg-focused`
* `-color-input-border-focused`
* `-color-input-bg-highlight`
* `-color-input-fg-highlight`

## TabPane

CSS classes:

* `.dense`
* `.floating` (`TabPane.STYLE_CLASS_FLOATING` or `Styles.TABS_FLOATING`)
* `.classic` (`Styles.TABS_CLASSIC`)

Floating and classic styles are mutually exclusive. 

Color variables:

* `-color-tab-bg-selected`
* `-color-tab-fg-selected`
* `-color-tab-border-selected`

To apply these variables ensure that your custom CSS rule has more specificity, e.g.:

```css
.my-tab-pane.floating {
    -color-tab-fg-selected: red;
}
```

## Titled Pane

Also applied to the Accordion.

CSS classes:

* `.alt-icon` (tweak)
* `.dense`
* `.elevated-1`, `.elevated-2`, `.elevated-3`, `.elevated-4`, `.interactive` (elevation support)

## Toggle Button

Color variables:

* `-color-button-bg-selected`
* `-color-button-fg-selected`

## Toggle Switch

Pseudo-classes:

* `:success`, `:danger`
