This is the source of the decoration theme icons. Each icon name follows the KDE
Aurorae [naming convention](https://develop.kde.org/docs/plasma/aurorae/).

- `active:` normal button for active windows
- `hover`: hover state for active windows
- `pressed`: button is pressed (`armed`)
- `deactivated`: button cannot be clicked, e.g. window cannot be closed (`disabled`)

- `inactive`: normal button for inactive window
- `hover-inactive`: hover state for inactive windows
- `pressed-inactive`: button is pressed for inactive windows
- `deactivated-inactive`: button cannot be clicked for inactive windows

The `generate-theme.py` script is used to generate or update the theme. You must
have Python 3 and [svgexport](https://github.com/piqnt/svgexport) installed.

```sh
chmod +x generate-theme.py
./generate-theme.py -i path/to/theme

# generate all themes
find . -maxdepth 1 -type d ! -name . -exec ./generate-theme.py -i {} \;
```

You can also scale the images to your desired size in pixels:

```sh
./generate-theme.py -i path/to/theme -sw 32 -sh 32
```

Check the metadata.cfg file in theme directory for the theme settings.
