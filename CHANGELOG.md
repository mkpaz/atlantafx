# Changelog

## [Unreleased]

## [1.1.0] - 2022-10-10

### Features

- (Sampler) 🚀 External themes support. Sampler can now be used to develop custom themes.
- (Sampler) 🚀 Brand new improved user interface.
- (Sampler) Widget page that aims to provide examples of some well-known components that won't be added to the project, but can be easily created using existing controls and a bit of CSS. First examples: `Card`, `Message`, `Stepper`, `Tag`.
- (Base) New control: `RingProgressIndicator`. Like `ProgressIndicator`, but fully customizable and uses arc instead of fill to indicate progress value.
- (Base) New skin: `ProgressSliderSkin`. A slider with color track.
- (Base) New `Breadcrumbs` API to provide more control customization.
  - Anything that extends `ButtonBase` can be used as `Breadcrumbs` item.
  - Divider is now customizable via corresponding factory.
- (Base) New utility: `PasswordTextFormatter`. An alternative to the `PasswordField`, the formatter that (un)masks `TextField` content based on boolean property.
- (CSS) Size style support: `TextField`, `Button`, `Slider`.
- (CSS) Rounded style support: `TextField`, `Button`.
- (CSS) Dense style support: `TabPane`, `TitledPane` / `Accordion`.
- (CSS) Alt icon tweak support: `TreeView`, `TitledPane` / `Accordion`.
- (CSS) Input group support: `Label`. `Label` graphic property can be used to add arbitrary node to the input group.
- (CSS) Utility classes for muted and subtle text style.
- (CSS) Utility classes for box elevation effect: `.elevated-[1-4]`.

### Improvements

- (Base) Setting top and bottom node for `DatePicker`. E.g. those can be a clock widget or event list.
- (CSS) 🚀 Nord light and dark themes rewamp with better color contrast and improved design.
- (CSS) Refactoring and improved control design: `Button`, `DatePicker`, `Slider`.
- (CSS) Looked-up color variables support: `Hyperlink`, `TextField`, `TextArea`, `ProgressBar`.
- (CSS) New global looked-up color variable `-color-shadow-default` for creating shadow effects.
- (CSS) Shadow effect for popup controls.

## [1.0.0] - 2022-09-06

Initial release.
