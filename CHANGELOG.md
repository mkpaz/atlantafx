# Changelog

## [Unreleased]

## [3.0.0] - TBD

### Breaking changes

- (Base) `PlatformUtils` renamed to `OS`.
- (Sampler) SceneBuilder integration removed in favor of the SceneBuilder plugin.

### Added

- 🚀 CSD decoration styles (see JDK-8356115) for all major OSs in `atlantafx-decorations` module.
- 🚀 SceneBuilder plugin for SceneBuilder 24+ in `atlantafx-scene-builder` module.
- 🚀 Fluent validation API in `atlantafx-validation` module.
- 🚀 Spin control (base) with 30 different skins in `atlantafx-spins` module.
- 🚀 (Base) New API for `Theme` and related features:
  - `ThemeManager` for centralized control over application themes and dynamic style options.
  - `ThemeManifest` to expose theme variables (colors, paddings, etc.) for programmatic access.
  - `ThemeProperties` for associating user-specific data with a theme.
  - Modular (partial) theme loading support.
- 🚀 (Base) Sidebar control.
- (CSS) Utility classes for `border-radius` (thanks to **ennerf**).
- (CSS) Size support for `ToggleButton` (thanks to **tanhuang2016**).
- (Sampler) Auto-downloading extra themes provided by `dlsc-software-consulting-gmbh/atlantafx-themes` (thanks to **dlemmermann**).

### Improved

- (Build) Java version bump to 25. All artifacts are compiled with the same version.
- (Build) JavaFX version bump to 27.
- (Base) Added GraalVM reachability metadata (thanks to **ennerf**).
- (Base) All modules switched to JSpecify annotations (and lots of internal refactoring caused by this change).
- (Base) Themes can now be discovered via `ServiceLoader`.
- (Base) `atlantafx.base.util` package promoted from internal utils to provide general utility classes for app development:
  - `StyleMap` helper for managing Node's inline CSS styles.
  - Toolkit-agnostic actions to open resources in the default OS app.
  - Cross-platform `XDG` specification implementation.
  - Observable `Colour` utility to simplify working with colors.
  - Auxiliary classes and contract interfaces: `Lazy`, `Focusable`, `Disposable`.
  - Various documentation and API improvements.
- (Base) `Animations` reset logic refactored to avoid registering helper listeners and store initial values as Node properties instead.
- (Base) `Styles.toDataUri()` deprecated in favor of new `Styles.encode()` and `Styles.decode()` methods that are aware of MIME types.
- (Sampler) Prepared Sampler to be released as a native image (thanks to **ennerf**).
- (Sampler) Used GemsFX `StageManager` to restore stage size (thanks to **dlemmermann**).

### Fixed

- (CSS) `PieChart` line color.
- (CSS) Removed vertical menu extra item padding.
- (CSS) JavaFX regression where scrollbar thumb height became invisible in large textareas and tables.
- (CSS) Removed accent hover border from toolbar buttons.
- (CSS) `MenuButton` icon color.

## [2.1.0] - 2025-07-12

### Added

- (Base) 🚀 `TabLine` component is an alternative `TabPane` control with custom slots, resize policies, tabs pinning and more.
- (Base) 🚀 `SegmentedControl` is another tabs component flavor for a limited number of fixed tabs.
- (Base) 🚀 `SelectableTextFlow` is a `TextFlow` that supports text selection.
- (Base) Label graphic support in the `ToggleSwitch` (thanks to **crschnick**).
- (CSS) Size modifiers support for `ComboBox` and `ChoiceBox`.
- (CSS) Flat style support for `ToggleButton`.
- (Sampler) `DevToolsFX` support for inspecting the scene-graph.

### Improved

- (Build) Java version bump to 21.
- (Build) JavaFX version bump to 22.
- (Base) `PasswordTextField` bullet constructor made public.
- (CSS) Embedded PNG files compression (thanks to **jandk**).

### Fixed

- (Base) Rare drag NPE in `Popover` (#66).
- (Base) `TileSkinBase` change listener update logic (#74).
- (Base) `Timer` in `Popover` that prevented shutting down JVM after primary stage closed (#109).
- (Base) `ModalPane` transitions on content change (#140) (thanks to **crschnick**).
- (CSS) Overlapping `ModalPane` scrollbar (#88).
- (CSS) `TextField` color in editable `ComboBox` (#98).
- (CSS) Font icon colors for `MenuButton`'s context menu (#100).
- (CSS) Individual cell selection color (#108).
- (CSS) Cell height artifact with fractional scaling (#111).
- (CSS) Sharp corners due to missing `background-radius` (#112).
- (CSS) `Checkbox` and `Radio` labels alignment (#115).

## [2.0.1] - 2023-06-18

### Fixed

- (Base) Incorrect Notification text width.
- (CSS) Increased overlay contrast of ModalPane.
- (Sampler) SceneBuilder installer classpath #42 (thanks to **ennerf**).
- (Sampler) Music player can't load demo files from jrt.
- (Sampler) Contrast checker bg color.

## [2.0.0] - 2023-06-02

### Breaking changes

- The `InlineDatePicker` control was renamed to `Calendar`.

### Added

- (Base) 🚀 [BBCode](https://ru.wikipedia.org/wiki/BBCode) markup support.
- (Base) 🚀 `DeckPane` layout with support for swipe and slide transitions.
- (Base) 🚀 `MaskTextField` (and `MaskTextFormatter`) control to support masked text input.
- (Base) 🚀 `Message` control for displaying banners or alerts.
- (Base) 🚀 `ModalPane` and `ModalBox` controls to display modal dialogs on the top of the current scene.
- (Base) 🚀 `Notification` control for displaying notifications.
- (Base) 🚀 The `Card` and `Tile` controls, which are both versatile containers that can be used in various contexts.
- (Base) All themes are now additionally available in the BSS format.
- (Base) Animations library.
- (Base) `InputGroup` layout to simplify creating, well, input groups.
- (Base) `PasswordTextField` control to simplify `PasswordTextFormatter` usage.
- (Base) `ToggleGroup` support for the `ToggleSwitch`
- (Base) `ToggleSwitch` property to control the label position (left or right).
- (Base) New utility methods in `Styles` class.
- (CSS) 🚀 MacOS-like Cupertino theme in light and dark variants.
- (CSS) 🚀 [Dracula](https://ui.draculatheme.com/) theme.
- (CSS) Classic `TabPane` style. There are three styles supported: default, floating and classic.
- (CSS) Regular outlined buttons. There was only colored option before.
- (CSS) `.no-header` tweak support for the `TableView` and `TreeTableView`.
- (CSS) `.edge-to-edge` tweak support for the `TextInput` and `Calendar`.
- (CSS) Intent pseudo-classes (`success`, `danger`) support for the `ToggleSwitch`.
- (CSS) An utility CSS classes for setting background colors.
- (CSS) Distinctive background color for the readonly text input state.
- (CSS) Breadcrumbs support for the `Toolbar`.
- (CSS) `Button` shadow effect support (`-color-button-shadow`). Only for themes compiled with the `button.$use-shadow` flag enabled.
- (Sampler) 🚀 The Sampler app is completely rewritten to give it a more modern look and feel.
- (Sampler) 🚀 SceneBuilder integration. AtlantaFX themes can be installed (or updated, or uninstalled) directly from the Sampler app.

### Improved

- (Build) JavaFX version bump to 20 (March 2023).
- (Base) A proper [Javadoc](https://mkpaz.github.io/atlantafx/apidocs/atlantafx.base/module-summary.html) for all controls.
- (Base) All controls are now more FXML-friendly.
- (CSS) Looked-up color variables for `Separator` and the selected `TabPane` tab.
- (CSS) Border radius and shadow effect to popup menu for `ComboBox` and all `ComboBox`-based controls.
- (CSS) `TextFieldTableCell` is highlighted when in the editable state thanks to the new `:focus-within` state support.
- (CSS) Icon buttons are now use `-fx-content-display: graphic-only` as the default.
- (CSS) Better `TreeView` alt icon. It's chevron character instead of `+/-`.
- (CSS) Better toolbar buttons styling.
- (CSS) Baseline-left is the default alignment for virtualized controls, because center-left sometimes lags on scrolling in large tables.

### Fixed

- (Base) Incorrect `Slider` progress track length calculation.
- (Base) NPE when the Popover owner is not added to the scene.
- (CSS) `Popover` arrow background color.
- (CSS) `ListView` with `.bordered` class displays borders on empty cells.
- (CSS) Baseline-left is now the default alignment for virtualized controls. This change was made because center-left alignment can lead to scrolling lags in large tables.
- (CSS) Tooltip inherits font properties from parent node.
- (CSS) Double-opacity in disabled `ChoiceBox`.

## [1.2.0] - 2023-02-11

This is a bugfix/maintenance release that also contains a few style improvements.

### Added

- (Build) ErrorProne plugin.
- (Build) Checkstyle plugin.
- (Build) SceneBuilder theme pack generation (#28) (thanks to **ennerf**).
- (CSS) Pseudo-classes to set the `Label` color.
- (CSS) Intent classes to set `FontIcon` color.

### Improved

- (Build) JavaFX version bump to 19 (September 2022).
- (CSS) Inner border radius to input controls (#24) (thanks to **mimoguz**).
- (CSS) Hover effect for `CheckBox` and `RadioButton`.
- (CSS) Hover effect for `TabPane` close button.
- (CSS) Increased `Menu`/`Menubar` paddings.

### Fixed

- (Base) Remove `ToggleSwitch` left padding when text is empty.
- (Base) `PasswordTextFormatter` garbled input.
- (CSS) `Tooltip` text not showing for circular buttons.
- (CSS) Prevent context menu from inheriting text input font properties.
- (CSS) Invalid text inputs borders color (#21).
- (CSS) Invalid `DatePicker` cell size.

## [1.1.0] - 2022-10-10

### Added

- (Sampler) 🚀 External themes support. Sampler can now be used to develop custom themes.
- (Sampler) 🚀 Brand new improved user interface.
- (Sampler) Widget page that aims to provide examples of some well-known components.
- (Base) `RingProgressIndicator` control. Like `ProgressIndicator`, but fully customizable and uses arc instead of fill to indicate the progress value.
- (Base) `ProgressSliderSkin` skin. A slider with color track.
- (Base) `Breadcrumbs` API to provide more control customization:
  - Anything that extends `ButtonBase` can be used as `Breadcrumbs` item.
  - Divider is now customizable via corresponding factory.
- (Base) `PasswordTextFormatter` utility. An alternative to the `PasswordField`, the formatter that masks or unmasks `TextField` content based on boolean property.
- (Base) Properties for setting the top and bottom node for `DatePicker`. E.g. those can be a clock widget or event list.
- (CSS) Size style support for the `TextField`, `Button`, `Slider`.
- (CSS) Rounded style support for the`TextField`, `Button`.
- (CSS) Dense style support for the `TabPane`, `TitledPane`, `Accordion`.
- (CSS) `.alt-icon` tweak support for the `TreeView`, `TitledPane`, `Accordion`.
- (CSS) Input group support for the `Label`. `Label` graphic property can be used to add arbitrary node to the input group.
- (CSS) Utility classes for muted and subtle text style.
- (CSS) Utility classes for box elevation effect: `.elevated-[1-4]`.
- (CSS) New global looked-up color variable `-color-shadow-default` for creating shadow effects.
-
### Improved

- (CSS) 🚀 Nord light and dark themes rewamp with better color contrast and improved design.
- (CSS) Refactoring and improved control design for the `Button`, `DatePicker`, `Slider`.
- (CSS) Looked-up color variables support for the `Hyperlink`, `TextField`, `TextArea`, `ProgressBar`.
- (CSS) Shadow effect for popup controls.

## [1.0.0] - 2022-09-06

Initial release.
