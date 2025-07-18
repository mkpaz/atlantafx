#!/usr/bin/env python3

import argparse
import base64
import glob
import os
import re
import shutil
import subprocess

OUTPUT_DIR = "../resources/atlantafx/decorations/theme"


def parse_args() -> tuple[str, int, int]:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "-i", "--input-dir", type=str, help="input directory path", required=True
    )
    parser.add_argument(
        "-sw",
        "--scale-width",
        type=int,
        help="width for PNG scaling",
        required=False,
        default=0,
    )
    parser.add_argument(
        "-sh",
        "--scale-height",
        type=int,
        help="Height for PNG scaling",
        required=False,
        default=0,
    )
    args = parser.parse_args()

    if not os.path.isdir(args.input_dir):
        raise ValueError(f"Input directory '{args.input_dir}' doesn't exist")

    return args.input_dir, args.scale_width, args.scale_height


def read_metadata(input_dir: str) -> dict[str, str]:
    metadata_path = os.path.join(input_dir, "metadata.cfg")
    metadata = {}

    if not os.path.isfile(metadata_path):
        raise FileNotFoundError(f"Metadata file '{metadata_path}' doesn't exist")

    with open(metadata_path, "r") as file:
        for line in file:
            if "=" in line:
                key, value = line.strip().split("=", 1)
                metadata[key.strip()] = value.strip()

    return metadata


def create_tmp_dir(input_dir: str) -> str:
    tmp_dir = os.path.join(input_dir, "tmp")
    os.makedirs(tmp_dir, exist_ok=True)
    return tmp_dir


def convert_svg_to_png(
    svg_files: list[str],
    metadata: dict[str, str],
    tmp_dir: str,
    scale_width: int = 0,
    scale_height: int = 0,
) -> None:
    if shutil.which("svgexport") is None:
        raise EnvironmentError("svgexport command is not found in the system PATH")

    width = scale_width if scale_width else int(metadata.get("ButtonWidth", "0"))
    height = scale_height if scale_height else int(metadata.get("ButtonHeight", "0"))

    for svg in svg_files:
        png = os.path.join(tmp_dir, os.path.basename(svg).replace(".svg", ".png"))

        result = subprocess.run(
            ["svgexport", svg, png, f"{width}:{height}"], capture_output=True
        )
        if result.returncode != 0:
            raise RuntimeError(
                f"Error converting {svg} to PNG: {result.stderr.decode().strip()}"
            )


def generate_theme(metadata: dict[str, str], tmp_dir: str) -> None:
    theme_path = os.path.join(OUTPUT_DIR, f"{metadata.get('Name', 'default')}.css")

    with open(theme_path, "w") as css:
        width = metadata.get("ButtonWidth", "0")
        height = metadata.get("ButtonHeight", "0")
        css.write(
            f".header-button-group .header-button {{\n"
            f"\t-fx-background-repeat: no-repeat;\n"
            f"\t-fx-pref-width: {width};\n"
            f"\t-fx-pref-height: {height};\n"
            f"}}\n"
        )

        spacing = metadata.get("GroupSpacing", "0")
        pt = metadata.get("GroupPaddingTop", "0")
        pr = metadata.get("GroupPaddingRight", "0")
        pb = metadata.get("GroupPaddingBottom", "0")
        pl = metadata.get("GroupPaddingLeft", "0")
        css.write(
            f".header-button-group>.container {{\n"
            f"\t-fx-spacing: {spacing};\n"
            f"\t-fx-padding: {pt}px {pr}px {pb}px {pl}px;\n"
            f"}}\n"
        )

        for png in sorted(glob.glob(os.path.join(tmp_dir, "*.png"))):
            basename = os.path.basename(png).replace(".png", "")

            tokens = [token for token in re.split(r"[-_]", basename) if token]
            button_name = tokens[0]
            button_states = tokens[1:]

            # do we need pressed state? it's not really visible effect
            if "pressed" in button_states:
                continue

            if button_name == "minimize":
                selector = ".header-button.minimize"
            elif button_name == "maximize":
                selector = ".header-button.maximize"
            elif button_name == "close":
                selector = ".header-button.close"
            elif button_name == "restore":
                selector = ".root:maximized .header-button.maximize"
            else:
                raise ValueError(f"Unknown button name '{button_name}'")

            if "hover" in button_states:
                selector += ":hover"
            if "pressed" in button_states:
                selector += ":armed"
            if "deactivated" in button_states:
                selector += ":disabled"
            if "inactive" in button_states:
                if selector.startswith(".header-button"):
                    selector = f".root:inactive {selector}"
                elif ":maximized" in selector:
                    selector = selector.replace(":maximized", ":maximized:inactive")

            base64 = read_as_base64(png)
            css.write(
                f"{selector} {{\n"
                f'\t-fx-background-image: url("data:image/png;base64,{base64}");\n'
                "}\n"
            )


def read_as_base64(path: str) -> str:
    with open(path, "rb") as image_file:
        return base64.b64encode(image_file.read()).decode("utf-8")


def clean_up(tmp_dir: str) -> None:
    shutil.rmtree(tmp_dir)


def main() -> None:
    input_dir, scale_width, scale_height = parse_args()
    metadata = read_metadata(input_dir)
    tmp_dir = create_tmp_dir(input_dir)

    try:
        svg_files = glob.glob(os.path.join(input_dir, "*.svg"))
        convert_svg_to_png(svg_files, metadata, tmp_dir, scale_width, scale_height)
        generate_theme(metadata, tmp_dir)
    finally:
        clean_up(tmp_dir)


if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(f"An error occurred: {e}")
